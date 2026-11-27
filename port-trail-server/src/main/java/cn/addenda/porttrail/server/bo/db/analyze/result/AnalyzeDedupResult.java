package cn.addenda.porttrail.server.bo.db.analyze.result;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 去重记录的分析结果。一条 execution 最多产出一条去重结果。
 *
 * <p>{@code ifExists} 表示 Redis 里已经有这个去重键的记录：为 true 时 consume 走合并 UPDATE，
 * 否则走 insert（插入冲突时再回退到合并 UPDATE）。
 *
 * @author addenda
 */
public class AnalyzeDedupResult implements AnalyzeResult {

  @Setter
  private String source;

  /**
   * 一条 execution 最多产出一条去重记录；{@code handle} 返回非 null 时该字段必然存在
   */
  @Getter
  private final AnalyzeDedupSingleResult analyzeDedupSingleResult;

  public AnalyzeDedupResult(AnalyzeDedupSingleResult analyzeDedupSingleResult) {
    this.analyzeDedupSingleResult = analyzeDedupSingleResult;
  }

  @Override
  public String getSource() {
    return source;
  }

  @Getter
  @Setter
  @ToString
  public static class AnalyzeDedupSingleResult {

    /**
     * 是否已有记录（来自 Redis 去重标记）
     */
    private boolean ifExists;

    /**
     * 本次执行是否写入了 explain-none 负缓存；为 true 时事务回滚需要删除它
     */
    private boolean explainNoneCacheWritten;

    private String dedupKey;

    private String statPeriod;

    private String systemCode;

    private String serviceName;

    private String env;

    private String imageName;

    private String instanceId;

    private String entryPointDetails;

    private String sqlType;

    private String parameterizedSql;

    private Integer ifSelectAll;

    private Long selectAllOuterId;

    private Integer ifFullScan;

    private Long fullScanOuterId;

    private String fullScanExplainJson;

    private Integer ifInTooMany;

    private Long inTooManyOuterId;

    private Integer inTooManyCount;

    private Integer ifCostTooHigh;

    private Long costTooHighOuterId;

    private Integer costTooHighCost;

    /**
     * 合并后的命中规则汇总，同时作为 Redis 去重标记的值
     */
    private String hitRules;

  }

}
