package cn.addenda.porttrail.server.test.biz.analyze;

import cn.addenda.component.cache.helper.CacheHelper;
import cn.addenda.component.transaction.PlatformTransactionHelper;
import cn.addenda.porttrail.common.pojo.db.dto.PreparedStatementParameterDto;
import cn.addenda.porttrail.common.util.CompressUtils;
import cn.addenda.porttrail.common.util.JdkSerializationUtils;
import cn.addenda.porttrail.server.PortTrailServerApplication;
import cn.addenda.porttrail.server.biz.analyze.AnalyzeDedupRule;
import cn.addenda.porttrail.server.biz.analyze.AnalyzeHandlerScheduler;
import cn.addenda.porttrail.server.bo.EntryPointEntityBo;
import cn.addenda.porttrail.server.bo.EntryPointSnapshotEntityBo;
import cn.addenda.porttrail.server.bo.db.PreparedStatementExecutionEntityBo;
import cn.addenda.porttrail.server.bo.db.PreparedStatementParameterEntityBo;
import cn.addenda.porttrail.server.bo.db.analyze.param.AnalyzePreparedStatementExecutionParam;
import cn.addenda.porttrail.server.curd.DbConfigEntityCurder;
import cn.addenda.porttrail.server.entity.AnalyzeDedupResultEntity;
import cn.addenda.porttrail.server.entity.DbConfigEntity;
import cn.addenda.porttrail.server.entity.EntryPointEntity;
import cn.addenda.porttrail.server.entity.EntryPointSnapshotEntity;
import cn.addenda.porttrail.server.entity.PreparedStatementExecutionEntity;
import cn.addenda.porttrail.server.entity.PreparedStatementParameterEntity;
import cn.addenda.porttrail.server.helper.AnalyzeDedupHandlerKeySupport;
import cn.addenda.porttrail.server.mapper.AnalyzeDedupResultEntityMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * 走真实 Redis + MySQL 的流程测试：从 {@link AnalyzeHandlerScheduler#handle} 一路到落库和标记写入，
 * 不在测试里 mock 任何组件。
 *
 * <p>用一个真实 {@code DbConfigEntity} 指向应用自身的数据库，explain 目标表也用应用自身的表，
 * 因此用例只需要本地 db_config.txt / redis_config.txt 可用。
 *
 * <p><b>数据清理</b>：MySQL 侧用 {@link Sql} 物理删除，原因是 Mapper 的 delete 是逻辑删除
 * （{@code update ... set delete_flag = 'Y'}），残留行仍占着 {@code uk_dedup_key}，
 * 且本表约定不允许人工逻辑删除。另外数据源配置了 {@code spring.datasource.hikari.auto-commit=false}，
 * 裸的 {@code JdbcTemplate.update} 不处于事务里不会提交（连接归还连接池时回滚），
 * 而 {@code @Sql} 内部走 {@code TransactionTemplate}，语句会真正提交。清理谓词用测试专属前缀，
 * 不依赖随机参数：
 * <ul>
 *   <li>{@code t_db_config_entity.connection_port_trail_id} 形如 {@code it-<uuid>}；</li>
 *   <li>{@code t_analyze_dedup_result_entity.entry_point_details} 形如 {@code /port-trail-it/<uuid>}。</li>
 * </ul>
 * Redis 侧按本次用例实际用到的 key 精确删除（标记、负缓存、解析缓存都有 TTL，删除只是让用例互不影响）。
 */
@SpringBootTest(classes = PortTrailServerApplication.class)
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, statements = {
        "delete from t_analyze_dedup_result_entity where entry_point_details like '/port-trail-it/%'",
        "delete from t_db_config_entity where connection_port_trail_id like 'it-%'"
})
class AnalyzeDedupHandlerFlowTest {

  private static final String DEDUP_PREFIX = "port-trail:analyze:dedup:";

  private static final String SQL_PARSE_PREFIX = "port-trail:analyze:sqlparse:";

  private static final String EXPLAIN_NONE_PREFIX = "port-trail:analyze:explain-none:";

  private static final String CONFIG_PPF_PREFIX = "AnalyzeHandler:pff:";

  private static final String SYSTEM_CODE = "SYS_IT";

  private static final String SERVICE_NAME = "svc_it";

  private static final String ENV = "sit";

  @Autowired
  private AnalyzeHandlerScheduler scheduler;

  @Autowired
  private CacheHelper<String> cacheHelper;

  @Autowired
  private DbConfigEntityCurder dbConfigEntityCurder;

  @Autowired
  private AnalyzeDedupResultEntityMapper analyzeDedupResultEntityMapper;

  @Autowired
  private Environment environment;

  @Autowired
  private PlatformTransactionHelper transactionHelper;

  private String connectionPortTrailId;

  /**
   * detail 是去重键前像的一部分，每条用例用唯一值，避免同一天重复跑用例时命中上一条记录
   */
  private String detail;

  private final List<String> usedDedupKeys = new ArrayList<>();

  private final List<String> usedSqlHashes = new ArrayList<>();

  @BeforeEach
  void setUp() {
    // 两个前缀让类级 @Sql 能无参、精确地删掉本用例的数据
    connectionPortTrailId = "it-" + UUID.randomUUID();
    detail = "/port-trail-it/" + UUID.randomUUID();

    DbConfigEntity dbConfig = DbConfigEntity.ofParam();
    dbConfig.setSystemCode(SYSTEM_CODE);
    dbConfig.setServiceName(SERVICE_NAME);
    dbConfig.setImageName("image_it");
    dbConfig.setEnv(ENV);
    dbConfig.setInstanceId("instance_it");
    dbConfig.setDataSourcePortTrailId("ds_it");
    dbConfig.setConnectionPortTrailId(connectionPortTrailId);
    dbConfig.setStatementPortTrailId("stmt_it");
    dbConfig.setJdbcUrl(environment.getRequiredProperty("spring.datasource.url"));
    dbConfig.setUser(environment.getRequiredProperty("spring.datasource.username"));
    dbConfig.setPassword(environment.getRequiredProperty("spring.datasource.password"));
    dbConfig.setDriverName(environment.getProperty(
            "spring.datasource.driver-class-name", "com.mysql.cj.jdbc.Driver"));
    dbConfig.setEntryPointSnapshotId(0L);
    transactionHelper.doTransaction(() -> dbConfigEntityCurder.insert(dbConfig));
  }

  @AfterEach
  void tearDown() {
    for (String dedupKey : usedDedupKeys) {
      for (String rule : AnalyzeDedupRule.ruleOrder()) {
        cacheHelper.delete(DEDUP_PREFIX, dedupKey + ":" + rule);
      }
      cacheHelper.delete(EXPLAIN_NONE_PREFIX, dedupKey);
    }
    for (String sqlHash : usedSqlHashes) {
      cacheHelper.delete(SQL_PARSE_PREFIX, sqlHash);
    }
    cacheHelper.delete(CONFIG_PPF_PREFIX, connectionPortTrailId);
  }

  // ================== 正常流程 ==================

  @Test
  void testSelectAllFlow() {
    String sql = "select * from t_db_config_entity where id = 0";

    scheduler.handle(newParam(sql, 10));

    AnalyzeDedupResultEntity row = queryDedup(sql);
    Assertions.assertNotNull(row);
    Assertions.assertEquals(1, row.getIfSelectAll());
    Assertions.assertEquals(0, row.getIfFullScan());
    Assertions.assertEquals("SELECT_ALL", row.getHitRules());
    Assertions.assertNull(row.getFullScanExplainJson());

    String dedupKey = dedupKeyFor(sql);
    Assertions.assertTrue(cacheHelper.containsKey(DEDUP_PREFIX,
            dedupKey + ":" + AnalyzeDedupRule.RULE_SELECT_ALL));
    // explain 成功且没有全表扫描 → 写入负缓存
    Assertions.assertTrue(cacheHelper.containsKey(EXPLAIN_NONE_PREFIX, dedupKey));
  }

  @Test
  void testFullScanFlow() {
    // 没有索引覆盖的列做全表查询，explain 应为 ALL
    String sql = "select parameterized_sql from t_analyze_dedup_result_entity";

    scheduler.handle(newParam(sql, 10));

    AnalyzeDedupResultEntity row = queryDedup(sql);
    Assertions.assertNotNull(row);
    Assertions.assertEquals(1, row.getIfFullScan());
    Assertions.assertEquals("FULL_SCAN", row.getHitRules());
    Assertions.assertNotNull(row.getFullScanExplainJson());
    Assertions.assertTrue(row.getFullScanExplainJson().contains("ALL"));

    String dedupKey = dedupKeyFor(sql);
    Assertions.assertTrue(cacheHelper.containsKey(DEDUP_PREFIX,
            dedupKey + ":" + AnalyzeDedupRule.RULE_FULL_SCAN));
    Assertions.assertFalse(cacheHelper.containsKey(EXPLAIN_NONE_PREFIX, dedupKey));
  }

  @Test
  void testNoRuleHitWritesNoRow() {
    String sql = "select id from t_db_config_entity where id = 0";

    scheduler.handle(newParam(sql, 10));

    Assertions.assertNull(queryDedup(sql));
    Assertions.assertTrue(cacheHelper.containsKey(EXPLAIN_NONE_PREFIX, dedupKeyFor(sql)));
  }

  @Test
  void testExplainFailureStillRecordsNonFullScanRules() {
    // JSqlParser 不校验列名，解析能通过；explain 在业务库执行时会失败
    String sql = "select * from t_db_config_entity where no_such_column = 0";

    scheduler.handle(newParam(sql, 10));

    AnalyzeDedupResultEntity row = queryDedup(sql);
    Assertions.assertNotNull(row);
    Assertions.assertEquals(1, row.getIfSelectAll());
    Assertions.assertEquals(0, row.getIfFullScan());
    // explain 失败不写负缓存，下次会重试
    Assertions.assertFalse(cacheHelper.containsKey(EXPLAIN_NONE_PREFIX, dedupKeyFor(sql)));
  }

  @Test
  void testTransactionRollbackClearsNegativeCacheAndSkipsMarkers() {
    String sql = "select * from t_db_config_entity where id = 0";
    // system_code 是 VARCHAR(64)：超长值让 dedup insert 在事务内失败，模拟落库异常触发回滚
    String tooLongSystemCode = longString(80);

    Exception exception = Assertions.assertThrows(Exception.class,
            () -> scheduler.handle(newParam(sql, 10, tooLongSystemCode)));
    // 确认异常发生在 consume 的 insert 上（而不是更早的阶段），否则本用例证明不了回滚清理
    Assertions.assertTrue(causeChainContains(exception, DataIntegrityViolationException.class),
            "expected dedup insert to fail, but got: " + exception);

    String dedupKey = dedupKeyFor(tooLongSystemCode, sql);
    Assertions.assertTrue(queryDedupList(tooLongSystemCode, sql).isEmpty());
    // 回滚：afterCommit 没执行，标记不会写
    Assertions.assertFalse(cacheHelper.containsKey(DEDUP_PREFIX,
            dedupKey + ":" + AnalyzeDedupRule.RULE_SELECT_ALL));
    // 回滚：本次写入的 explain-none 负缓存被清掉，下次执行会重新 explain
    Assertions.assertFalse(cacheHelper.containsKey(EXPLAIN_NONE_PREFIX, dedupKey));
  }

  // ================== 重复执行 / 更新 ==================

  @Test
  void testSecondExecutionDoesNotInsertAgain() {
    String sql = "select * from t_db_config_entity where id = 0";

    scheduler.handle(newParam(sql, 10));
    scheduler.handle(newParam(sql, 10));

    Assertions.assertEquals(1, queryDedupList(sql).size());
  }

  @Test
  void testCostUpdateMergesExistingRow() {
    String sql = "select * from t_db_config_entity where id = 0";

    AnalyzePreparedStatementExecutionParam firstParam = newParam(sql, 10);
    scheduler.handle(firstParam);

    // 第一次：只有 SELECT_ALL，cost 未超阈值
    AnalyzeDedupResultEntity first = queryDedup(sql);
    Assertions.assertNotNull(first);
    Assertions.assertEquals(1, first.getIfSelectAll());
    Assertions.assertEquals(firstParam.getPreparedStatementExecutionEntityBo().getId(),
            first.getSelectAllOuterId());
    Assertions.assertEquals(0, first.getIfCostTooHigh());
    Assertions.assertNull(first.getCostTooHighOuterId());
    Assertions.assertNull(first.getCostTooHighCost());
    Assertions.assertEquals("SELECT_ALL", first.getHitRules());

    // 第二次：cost 阈值 1000ms（见 application.yml），5000 一定判定为慢 SQL
    AnalyzePreparedStatementExecutionParam secondParam = newParam(sql, 5000);
    scheduler.handle(secondParam);

    // 合并到同一行，而不是新增
    AnalyzeDedupResultEntity merged = queryDedup(sql);
    Assertions.assertNotNull(merged);
    Assertions.assertEquals(1, queryDedupList(sql).size());
    Assertions.assertEquals(first.getId(), merged.getId());
    // SELECT_ALL 的首次证据保留
    Assertions.assertEquals(firstParam.getPreparedStatementExecutionEntityBo().getId(),
            merged.getSelectAllOuterId());
    Assertions.assertEquals(1, merged.getIfSelectAll());
    // COST_TOO_HIGH 用本次执行作为首次证据
    Assertions.assertEquals(1, merged.getIfCostTooHigh());
    Assertions.assertEquals(secondParam.getPreparedStatementExecutionEntityBo().getId(),
            merged.getCostTooHighOuterId());
    Assertions.assertEquals(5000, merged.getCostTooHighCost());
    Assertions.assertEquals("SELECT_ALL,COST_TOO_HIGH", merged.getHitRules());
  }

  @Test
  void testMarkersRestoredFromDb() {
    String sql = "select * from t_db_config_entity where id = 0";
    scheduler.handle(newParam(sql, 10));
    String dedupKey = dedupKeyFor(sql);

    // 模拟 Redis 标记丢失
    for (String rule : AnalyzeDedupRule.ruleOrder()) {
      cacheHelper.delete(DEDUP_PREFIX, dedupKey + ":" + rule);
    }
    Assertions.assertFalse(cacheHelper.containsKey(DEDUP_PREFIX,
            dedupKey + ":" + AnalyzeDedupRule.RULE_SELECT_ALL));

    scheduler.handle(newParam(sql, 10));

    // 回源 MySQL 后回填标记，且不产生第二条记录
    Assertions.assertTrue(cacheHelper.containsKey(DEDUP_PREFIX,
            dedupKey + ":" + AnalyzeDedupRule.RULE_SELECT_ALL));
    Assertions.assertEquals(1, queryDedupList(sql).size());
  }

  // ================== 辅助 ==================

  private AnalyzePreparedStatementExecutionParam newParam(String sql, int cost) {
    return newParam(sql, cost, SYSTEM_CODE);
  }

  private AnalyzePreparedStatementExecutionParam newParam(String sql, int cost, String systemCode) {
    PreparedStatementExecutionEntity execution = PreparedStatementExecutionEntity.ofParam();
    execution.setId(System.nanoTime());
    execution.setSystemCode(systemCode);
    execution.setServiceName(SERVICE_NAME);
    execution.setImageName("image_it");
    execution.setEnv(ENV);
    execution.setInstanceId("instance_it");
    execution.setConnectionPortTrailId(connectionPortTrailId);
    execution.setParameterizedSql(sql);
    execution.setCost(cost);
    PreparedStatementExecutionEntityBo executionBo =
            new PreparedStatementExecutionEntityBo(execution);

    EntryPointSnapshotEntity snapshot = EntryPointSnapshotEntity.ofParam();
    snapshot.setId(0L);
    EntryPointSnapshotEntityBo snapshotBo = new EntryPointSnapshotEntityBo(snapshot);
    EntryPointEntity entryPoint = EntryPointEntity.ofParam();
    entryPoint.setDetail(detail);
    snapshotBo.setEntryPointEntityBoList(
            Collections.singletonList(new EntryPointEntityBo(entryPoint)));
    executionBo.setEntryPointSnapshotEntityBo(snapshotBo);

    PreparedStatementParameterEntity parameter = PreparedStatementParameterEntity.ofParam();
    parameter.setId(0L);
    parameter.setParameterBytes(emptyParameterBytes());
    executionBo.setPreparedStatementParameterEntityBoList(
            Collections.singletonList(new PreparedStatementParameterEntityBo(parameter)));

    usedDedupKeys.add(dedupKeyFor(systemCode, sql));
    usedSqlHashes.add(AnalyzeDedupHandlerKeySupport.computeSqlHash(sql));

    AnalyzePreparedStatementExecutionParam param = new AnalyzePreparedStatementExecutionParam();
    param.setPreparedStatementExecutionEntityBo(executionBo);
    return param;
  }

  /**
   * capacity = 0 的参数记录：applyParameters 不会回填任何参数，适合不依赖参数值的 SQL。
   */
  private static byte[] emptyParameterBytes() {
    PreparedStatementParameterDto dto = new PreparedStatementParameterDto();
    dto.setSetMethodList(new ArrayList<>());
    dto.setParameterTypeList(new ArrayList<>());
    dto.setParameterValueList(new ArrayList<>());
    return CompressUtils.compress(JdkSerializationUtils.serialize(dto));
  }

  private static boolean causeChainContains(Throwable throwable, Class<? extends Throwable> type) {
    Throwable current = throwable;
    while (current != null) {
      if (type.isInstance(current)) {
        return true;
      }
      current = current.getCause();
    }
    return false;
  }

  private static String longString(int length) {
    StringBuilder builder = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      builder.append('x');
    }
    return builder.toString();
  }

  private String dedupKeyFor(String sql) {
    return dedupKeyFor(SYSTEM_CODE, sql);
  }

  private String dedupKeyFor(String systemCode, String sql) {
    return AnalyzeDedupHandlerKeySupport.computeDedupKey(
            LocalDate.now().toString(), systemCode, SERVICE_NAME, ENV,
            Collections.singletonList(detail), sql);
  }

  private AnalyzeDedupResultEntity queryDedup(String sql) {
    List<AnalyzeDedupResultEntity> list = queryDedupList(sql);
    return list.isEmpty() ? null : list.get(0);
  }

  private List<AnalyzeDedupResultEntity> queryDedupList(String sql) {
    return queryDedupList(SYSTEM_CODE, sql);
  }

  private List<AnalyzeDedupResultEntity> queryDedupList(String systemCode, String sql) {
    AnalyzeDedupResultEntity param = AnalyzeDedupResultEntity.ofParam();
    param.setDedupKey(dedupKeyFor(systemCode, sql));
    return analyzeDedupResultEntityMapper.queryByEntity(param);
  }

}
