package cn.addenda.porttrail.server.biz.analyze;

import cn.addenda.component.base.jackson.util.JacksonUtils;
import cn.addenda.porttrail.common.util.SqlUtils;
import cn.addenda.porttrail.server.bo.EntryPointEntityBo;
import cn.addenda.porttrail.server.bo.EntryPointSnapshotEntityBo;
import cn.addenda.porttrail.server.bo.db.PreparedStatementExecutionEntityBo;
import cn.addenda.porttrail.server.bo.db.PreparedStatementParameterEntityBo;
import cn.addenda.porttrail.server.bo.db.analyze.param.AnalyzePreparedStatementExecutionParam;
import cn.addenda.porttrail.server.bo.db.analyze.param.AnalyzeStatementExecutionParam;
import cn.addenda.porttrail.server.bo.db.analyze.result.AnalyzeDedupResult;
import cn.addenda.porttrail.server.bo.db.analyze.result.AnalyzeResult;
import cn.addenda.porttrail.server.config.AnalyzeDedupConfig;
import cn.addenda.porttrail.server.curd.AnalyzeDedupResultEntityCurder;
import cn.addenda.porttrail.server.entity.AnalyzeDedupResultEntity;
import cn.addenda.porttrail.server.entity.DbConfigEntity;
import cn.addenda.porttrail.server.helper.AnalyzeDedupHandlerKeySupport;
import cn.addenda.porttrail.server.helper.SqlHelper;
import cn.addenda.porttrail.server.helper.SqlParseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.CollectionUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 去重记录分析器：只记录命中问题规则的 SQL，同一天同一去重键只记一条，并附带 explain 结论。
 *
 * <p>本次只覆盖 prepared 执行，statement 链路直接返回 null。
 *
 * <p>Redis 里维护三类 key（都走 {@code CacheHelper} 的直通 get/set/containsKey）：
 * <ul>
 *   <li>去重标记：每条规则一个幂等标记，已记录 FULL_SCAN 时跳过 explain；</li>
 *   <li>解析结论：按 SQL 文本 hash，跨实例跨天共享；</li>
 *   <li>explain 负缓存：explain 未发现全表扫描时按可配置 TTL 写入，命中则跳过 explain。</li>
 * </ul>
 * 正向 explain 结论不单独缓存，由 FULL_SCAN 标记表达；负缓存有 TTL，避免把某个参数下的
 * 执行计划当成永久结论。
 *
 * @author addenda
 */
@Slf4j
@Component
public class AnalyzeDedupHandler extends AbstractExplainAnalyzeHandler<AnalyzeDedupResult> {

  static final String DEDUP_PREFIX = "port-trail:analyze:dedup:";

  static final String SQL_PARSE_PREFIX = "port-trail:analyze:sqlparse:";

  static final String EXPLAIN_NONE_PREFIX = "port-trail:analyze:explain-none:";

  private static final String EXPLAIN_TYPE_ALL = "ALL";

  @Autowired
  private SqlHelper sqlHelper;

  @Autowired
  private AnalyzeDedupConfig analyzeDedupConfig;

  @Autowired
  private AnalyzeDedupResultEntityCurder analyzeDedupResultEntityCurder;

  @Override
  public String handlerName() {
    return "AnalyzeDedupHandler";
  }

  @Override
  public AnalyzeDedupResult handle(AnalyzePreparedStatementExecutionParam analyzeParam) {
    PreparedStatementExecutionEntityBo executionBo = analyzeParam.getPreparedStatementExecutionEntityBo();
    if (executionBo == null || executionBo.getId() == null) {
      return null;
    }
    String parameterizedSql = executionBo.getParameterizedSql();
    if (parameterizedSql == null || parameterizedSql.isEmpty()) {
      return null;
    }

    // 统计周期：当前固定为 yyyy-MM-dd，后续由配置决定格式
    String statPeriod = LocalDate.now().toString();
    List<String> detailList = extractDetailList(executionBo);
    String dedupKey = AnalyzeDedupHandlerKeySupport.computeDedupKey(statPeriod,
            executionBo.getSystemCode(), executionBo.getServiceName(), executionBo.getEnv(),
            detailList, parameterizedSql);

    // ① 每规则一个幂等标记；全部 miss 时回源 MySQL（cache-aside）并回填标记
    String recordedHitRules = readRecordedHitRules(dedupKey);
    Set<String> recordedRules = AnalyzeDedupRule.parseHitRules(recordedHitRules);
    boolean selectAllRecorded = recordedRules.contains(AnalyzeDedupRule.RULE_SELECT_ALL);
    boolean inTooManyRecorded = recordedRules.contains(AnalyzeDedupRule.RULE_IN_TOO_MANY);
    boolean costTooHighRecorded = recordedRules.contains(AnalyzeDedupRule.RULE_COST_TOO_HIGH);
    boolean ifFullScan = recordedRules.contains(AnalyzeDedupRule.RULE_FULL_SCAN);

    // ② 解析结论：SELECT_ALL / IN_TOO_MANY 只取决于 SQL 文本；两条都已记录时无需再解析，
    //    这两个规则不可能再产生「新规则」，只剩 explain / 未记录规则的判定
    SqlParseResult parseResult = null;
    if (!selectAllRecorded || !inTooManyRecorded) {
      String sqlHash = AnalyzeDedupHandlerKeySupport.computeSqlHash(parameterizedSql);
      parseResult = cacheHelper.get(SQL_PARSE_PREFIX, sqlHash, SqlParseResult.class);
      if (parseResult == null) {
        parseResult = sqlHelper.parseSql(parameterizedSql);
        if (parseResult != null) {
          cacheHelper.set(SQL_PARSE_PREFIX, sqlHash, parseResult,
                  analyzeDedupConfig.getSqlParseCacheTtlMs(), TimeUnit.MILLISECONDS);
        }
      }
    }
    boolean ifSelectAll = selectAllRecorded || (parseResult != null && parseResult.isHasSelectAll());
    boolean ifInTooMany = inTooManyRecorded
            || (parseResult != null && parseResult.getMaxInElementCount() > analyzeDedupConfig.getInThreshold());

    // ③ explain：
    //    - 已记录 FULL_SCAN → 不需要再 explain；
    //    - 命中「explain 过且没有全表扫描」的负缓存 → 也不需要；
    //    - 否则重新 explain，未发现全表扫描时按配置写负缓存（ttl = 0 表示不写）。
    //    同一条 SQL 在不同参数下执行计划可能不同，负缓存只保留一段有限时间，不是永久结论。
    ExplainConclusion conclusion = null;
    boolean explainNoneCacheWritten = false;
    if (!ifFullScan && !cacheHelper.containsKey(EXPLAIN_NONE_PREFIX, dedupKey)) {
      conclusion = doExplain(executionBo, parameterizedSql);
      if (conclusion != null) {
        ifFullScan = conclusion.isIfFullScan();
        if (ifFullScan) {
          // 已经有正向结论，负缓存作废
          cacheHelper.delete(EXPLAIN_NONE_PREFIX, dedupKey);
        } else if (analyzeDedupConfig.getExplainNoneTtlMs() > 0) {
          cacheHelper.set(EXPLAIN_NONE_PREFIX, dedupKey, "1",
                  analyzeDedupConfig.getExplainNoneTtlMs(), TimeUnit.MILLISECONDS);
          // 事务回滚时要删掉它，下次重新 explain
          explainNoneCacheWritten = true;
        }
      }
      // conclusion == null（explain 失败）不写负缓存，下次继续重试
    }

    // ④ cost：已记录的直接置 1；未记录的才用当次 execution 的耗时判定
    boolean ifCostTooHigh = costTooHighRecorded;
    if (!ifCostTooHigh) {
      Integer cost = executionBo.getCost();
      ifCostTooHigh = cost != null && cost > analyzeDedupConfig.getCostThresholdMs();
    }

    // ⑤ 合并四个判定结果：
    //    - 全未命中 → 不记录；
    //    - 已记录且没有新规则 → 本次不产生任何写入；
    //    - 否则合并 hit_rules，交给 consume 落库
    AnalyzeDedupRule rule = new AnalyzeDedupRule(
            ifSelectAll ? 1 : 0, ifFullScan ? 1 : 0, ifInTooMany ? 1 : 0, ifCostTooHigh ? 1 : 0);
    if (!rule.anyHit()) {
      return null;
    }
    String currentHitRules = rule.hitRules();
    if (recordedHitRules != null && !AnalyzeDedupRule.hasNewHitRule(recordedHitRules, currentHitRules)) {
      log.debug("dedup key [{}] already recorded with rules [{}], no new rule, skip.",
              dedupKey, recordedHitRules);
      return null;
    }
    String mergedHitRules = AnalyzeDedupRule.mergeHitRules(recordedHitRules, currentHitRules);

    // ⑥ 组装 AnalyzeDedupResult
    AnalyzeDedupResult analyzeDedupResult = new AnalyzeDedupResult(
            buildSingleResult(executionBo, parameterizedSql, detailList,
                    dedupKey, statPeriod, parseResult, conclusion, rule,
                    recordedHitRules != null, mergedHitRules, explainNoneCacheWritten));
    analyzeDedupResult.setSource(AnalyzeResult.SOURCE_PREPARED_STATEMENT_EXECUTION);
    return analyzeDedupResult;
  }

  @Override
  public AnalyzeDedupResult handle(AnalyzeStatementExecutionParam analyzeParam) {
    // 本次只覆盖 prepared（预编译）执行
    return null;
  }

  @Override
  public boolean canConsume(AnalyzeResult analyzeResult) {
    return Objects.equals(AnalyzeDedupResult.class, analyzeResult.getClass());
  }

  @Override
  public void consume(AnalyzeResult analyzeResult) {
    AnalyzeDedupResult analyzeDedupResult = (AnalyzeDedupResult) analyzeResult;
    AnalyzeDedupResult.AnalyzeDedupSingleResult singleResult =
            analyzeDedupResult.getAnalyzeDedupSingleResult();

    // 先注册：即使后续 insert/merge 抛异常导致回滚，也要把本次写入的负缓存清掉
    registerExplainNoneRollbackCleanup(singleResult);

    AnalyzeDedupResultEntity entity = toEntity(analyzeDedupResult.getSource(), singleResult);
    boolean inserted = insertIfAbsent(entity, singleResult);
    if (!inserted) {
      merge(entity, singleResult);
    }
    // 标记必须在事务提交后写：提交前写会让回滚后的记录当天永久丢失
    registerDedupMarkerAfterCommit(singleResult.getDedupKey(), singleResult.getHitRules());
  }

  /**
   * 没有记录时插入；已有记录（标记存在）或插入冲突时返回 false，由调用方走合并 UPDATE。
   */
  private boolean insertIfAbsent(AnalyzeDedupResultEntity entity,
                                 AnalyzeDedupResult.AnalyzeDedupSingleResult singleResult) {
    if (singleResult.isIfExists()) {
      return false;
    }
    try {
      analyzeDedupResultEntityCurder.insert(entity);
      return true;
    } catch (DuplicateKeyException e) {
      // Redis 标记丢失 / 多实例并发 / 消息重投递：唯一索引兜底，回退到合并
      log.info("dedup result already exists (redis marker missing?), dedupKey: [{}].",
              singleResult.getDedupKey());
      return false;
    }
  }

  /**
   * 幂等合并。本表不允许人工逻辑删除，所以 merge 影响 0 行只可能是行被物理删除或
   * Redis 标记残留（例如恢复备份），此时退化为插入做恢复。
   */
  private void merge(AnalyzeDedupResultEntity entity,
                     AnalyzeDedupResult.AnalyzeDedupSingleResult singleResult) {
    int updated = analyzeDedupResultEntityCurder.mergeByDedupKey(entity);
    if (updated == 0) {
      log.info("dedup result not found when merging, fallback to insert. dedupKey: [{}].",
              singleResult.getDedupKey());
      try {
        analyzeDedupResultEntityCurder.insert(entity);
      } catch (DuplicateKeyException e) {
        log.info("dedup result inserted concurrently, dedupKey: [{}].", singleResult.getDedupKey());
      }
    }
  }

  private AnalyzeDedupResult.AnalyzeDedupSingleResult buildSingleResult(
          PreparedStatementExecutionEntityBo executionBo,
          String parameterizedSql, List<String> detailList, String dedupKey, String statPeriod,
          SqlParseResult parseResult, ExplainConclusion conclusion, AnalyzeDedupRule rule,
          boolean ifExists, String mergedHitRules, boolean explainNoneCacheWritten) {

    AnalyzeDedupResult.AnalyzeDedupSingleResult singleResult =
            new AnalyzeDedupResult.AnalyzeDedupSingleResult();
    singleResult.setIfExists(ifExists);
    singleResult.setExplainNoneCacheWritten(explainNoneCacheWritten);
    singleResult.setDedupKey(dedupKey);
    singleResult.setStatPeriod(statPeriod);
    singleResult.setSystemCode(executionBo.getSystemCode());
    singleResult.setServiceName(executionBo.getServiceName());
    singleResult.setEnv(executionBo.getEnv());
    singleResult.setImageName(executionBo.getImageName());
    singleResult.setInstanceId(executionBo.getInstanceId());
    singleResult.setEntryPointDetails(AnalyzeDedupHandlerKeySupport.joinDetails(detailList));
    singleResult.setSqlType(SqlUtils.getSqlType(parameterizedSql));
    singleResult.setParameterizedSql(parameterizedSql);

    Long outerId = executionBo.getId();
    singleResult.setIfSelectAll(rule.getIfSelectAll());
    singleResult.setSelectAllOuterId(rule.getIfSelectAll() == 1 ? outerId : null);

    singleResult.setIfFullScan(rule.getIfFullScan());
    singleResult.setFullScanOuterId(rule.getIfFullScan() == 1 ? outerId : null);
    if (rule.getIfFullScan() == 1 && conclusion != null && conclusion.getExplainRowList() != null) {
      singleResult.setFullScanExplainJson(JacksonUtils.toStr(conclusion.getExplainRowList()));
    }

    singleResult.setIfInTooMany(rule.getIfInTooMany());
    singleResult.setInTooManyOuterId(rule.getIfInTooMany() == 1 ? outerId : null);
    singleResult.setInTooManyCount(rule.getIfInTooMany() == 1 && parseResult != null
            ? parseResult.getMaxInElementCount() : null);

    singleResult.setIfCostTooHigh(rule.getIfCostTooHigh());
    singleResult.setCostTooHighOuterId(rule.getIfCostTooHigh() == 1 ? outerId : null);
    singleResult.setCostTooHighCost(rule.getIfCostTooHigh() == 1 ? executionBo.getCost() : null);

    singleResult.setHitRules(mergedHitRules);
    return singleResult;
  }

  /**
   * 执行 explain。以下场景跳过：INSERT、没有参数记录、没有业务库配置、explain 失败。
   * 跳过时按 ifFullScan = 0 处理，其余规则照常判定。
   */
  private ExplainConclusion doExplain(PreparedStatementExecutionEntityBo executionBo, String parameterizedSql) {
    if (SqlUtils.SQL_TYPE_INSERT.equals(SqlUtils.getSqlType(parameterizedSql))) {
      return null;
    }
    List<PreparedStatementParameterEntityBo> parameterBoList =
            executionBo.getPreparedStatementParameterEntityBoList();
    if (CollectionUtils.isEmpty(parameterBoList)) {
      return null;
    }
    DbConfigEntity dbConfigEntity = queryByConnectionPortTrailId(executionBo.getConnectionPortTrailId());
    if (dbConfigEntity == null) {
      return null;
    }

    try (Connection connection = getConnection(dbConfigEntity)) {
      return buildConclusion(explainOne(connection, parameterizedSql, parameterBoList.get(0)));
    } catch (SQLException e) {
      log.error("explain failed, dedupKey will skip full scan rule. sql: [{}].", parameterizedSql, e);
      return null;
    }
  }

  /**
   * 判断 explain 结果中是否存在 type = ALL 的行，并保留全量行。
   */
  public static ExplainConclusion buildConclusion(List<ExplainRow> explainRowList) {
    ExplainConclusion conclusion = new ExplainConclusion();
    if (CollectionUtils.isEmpty(explainRowList)) {
      return conclusion;
    }

    boolean ifFullScan = false;
    for (ExplainRow explainRow : explainRowList) {
      if (EXPLAIN_TYPE_ALL.equalsIgnoreCase(explainRow.getExplainType())) {
        ifFullScan = true;
        break;
      }
    }

    conclusion.setIfFullScan(ifFullScan);
    conclusion.setExplainRowList(new ArrayList<>(explainRowList));
    return conclusion;
  }

  private List<String> extractDetailList(PreparedStatementExecutionEntityBo executionBo) {
    EntryPointSnapshotEntityBo entryPointSnapshotEntityBo = executionBo.getEntryPointSnapshotEntityBo();
    if (entryPointSnapshotEntityBo == null
            || entryPointSnapshotEntityBo.getEntryPointEntityBoList() == null) {
      return Collections.emptyList();
    }
    List<String> detailList = new ArrayList<>();
    for (EntryPointEntityBo entryPointEntityBo : entryPointSnapshotEntityBo.getEntryPointEntityBoList()) {
      detailList.add(entryPointEntityBo.getDetail());
    }
    return detailList;
  }

  private AnalyzeDedupResultEntity toEntity(String source,
                                            AnalyzeDedupResult.AnalyzeDedupSingleResult singleResult) {
    AnalyzeDedupResultEntity entity = AnalyzeDedupResultEntity.ofParam();
    entity.setDedupKey(singleResult.getDedupKey());
    entity.setStatPeriod(singleResult.getStatPeriod());
    entity.setSource(source);
    entity.setSystemCode(singleResult.getSystemCode());
    entity.setServiceName(singleResult.getServiceName());
    entity.setEnv(singleResult.getEnv());
    entity.setImageName(singleResult.getImageName());
    entity.setInstanceId(singleResult.getInstanceId());
    entity.setEntryPointDetails(singleResult.getEntryPointDetails());
    entity.setSqlType(singleResult.getSqlType());
    entity.setParameterizedSql(singleResult.getParameterizedSql());
    entity.setIfSelectAll(singleResult.getIfSelectAll());
    entity.setSelectAllOuterId(singleResult.getSelectAllOuterId());
    entity.setIfFullScan(singleResult.getIfFullScan());
    entity.setFullScanOuterId(singleResult.getFullScanOuterId());
    entity.setFullScanExplainJson(singleResult.getFullScanExplainJson());
    entity.setIfInTooMany(singleResult.getIfInTooMany());
    entity.setInTooManyOuterId(singleResult.getInTooManyOuterId());
    entity.setInTooManyCount(singleResult.getInTooManyCount());
    entity.setIfCostTooHigh(singleResult.getIfCostTooHigh());
    entity.setCostTooHighOuterId(singleResult.getCostTooHighOuterId());
    entity.setCostTooHighCost(singleResult.getCostTooHighCost());
    entity.setHitRules(singleResult.getHitRules());
    return entity;
  }

  /**
   * 读取已记录的规则集合：每条规则一个幂等标记，按固定顺序拼装。
   * <p>
   * 全部标记都 miss 时回源 MySQL（cache-aside）；部分 miss 说明规则还没记录过
   * （或标记被淘汰），按「新规则」处理即可，后续 merge 是幂等的。
   */
  private String readRecordedHitRules(String dedupKey) {
    List<String> recordedRules = new ArrayList<>(4);
    for (String rule : AnalyzeDedupRule.ruleOrder()) {
      if (cacheHelper.containsKey(DEDUP_PREFIX, markerId(dedupKey, rule))) {
        recordedRules.add(rule);
      }
    }
    if (!recordedRules.isEmpty()) {
      return String.join(",", recordedRules);
    }
    return restoreRecordedHitRulesFromDb(dedupKey);
  }

  /**
   * 标记全部 miss 时回源 MySQL（cache-aside）：行存在就把 {@code hit_rules} 当作已记录集合，
   * 并为每条规则回填幂等标记。
   * <p>
   * consume 里 {@code insert → DuplicateKeyException → merge} 的兜底仍然保留：
   * 回源和插入之间存在并发窗口，另一个实例可能刚好在同一时刻插入。
   */
  private String restoreRecordedHitRulesFromDb(String dedupKey) {
    String hitRules = analyzeDedupResultEntityCurder.queryHitRulesByDedupKey(dedupKey);
    if (hitRules == null || hitRules.isEmpty()) {
      return null;
    }
    writeDedupMarkers(dedupKey, hitRules);
    return hitRules;
  }

  /**
   * 本次执行写入过 explain-none 负缓存时，注册回滚清理：事务回滚则删除它，下次重新 explain。
   * 只在本次真正写入时注册，避免误删其它执行写入的有效负缓存。
   */
  private void registerExplainNoneRollbackCleanup(
          AnalyzeDedupResult.AnalyzeDedupSingleResult singleResult) {
    if (!singleResult.isExplainNoneCacheWritten()) {
      return;
    }
    if (!TransactionSynchronizationManager.isSynchronizationActive()) {
      return;
    }
    String dedupKey = singleResult.getDedupKey();
    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
      @Override
      public void afterCompletion(int status) {
        if (status != TransactionSynchronization.STATUS_ROLLED_BACK) {
          return;
        }
        try {
          cacheHelper.delete(EXPLAIN_NONE_PREFIX, dedupKey);
        } catch (Throwable t) {
          log.error("delete explain-none cache after rollback failed, dedupKey: [{}].", dedupKey, t);
        }
      }
    });
  }

  /**
   * 事务内注册 afterCommit 回调；无事务上下文时立即写。写失败只记日志：此时库表已提交，
   * 抛异常会让调用方误判；重复记录由唯一索引兜底。
   */
  private void registerDedupMarkerAfterCommit(String dedupKey, String hitRules) {
    if (dedupKey == null || hitRules == null || hitRules.isEmpty()) {
      return;
    }
    if (TransactionSynchronizationManager.isSynchronizationActive()) {
      TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
        @Override
        public void afterCommit() {
          writeDedupMarkers(dedupKey, hitRules);
        }
      });
    } else {
      writeDedupMarkers(dedupKey, hitRules);
    }
  }

  /**
   * 逐条规则写标记。每个标记是独立的「该规则已记录」布尔值，写入幂等且可交换，
   * 并发写不同规则不会互相覆盖，不需要 read-modify-write。
   */
  private void writeDedupMarkers(String dedupKey, String hitRules) {
    for (String rule : AnalyzeDedupRule.parseHitRules(hitRules)) {
      writeRuleMarker(dedupKey, rule);
    }
  }

  private void writeRuleMarker(String dedupKey, String rule) {
    try {
      cacheHelper.set(DEDUP_PREFIX, markerId(dedupKey, rule), "1",
              analyzeDedupConfig.getCacheTtlMs(), TimeUnit.MILLISECONDS);
    } catch (Throwable t) {
      log.error("write dedup marker failed, dedupKey: [{}], rule: [{}].", dedupKey, rule, t);
    }
  }

  private static String markerId(String dedupKey, String rule) {
    return dedupKey + ":" + rule;
  }

}
