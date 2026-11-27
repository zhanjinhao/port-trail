package cn.addenda.porttrail.server.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 分析-问题SQL去重记录(AnalyzeDedupResultEntity)实体类
 *
 * <p>同一天同一去重键只保留一条，由 {@code uk_dedup_key} 唯一索引兜底幂等；
 * 后续执行命中新规则时对已有行做幂等合并（flag 只增不减，证据列保留第一次的值）。
 *
 * <p><b>本表不允许人工逻辑删除</b>：{@code deleteFlag} / {@code deleteDt} 只为
 * {@link SimpleBaseModel} 的字段约定保留，值恒为 {@code 'N'}；Mapper 里的删除语句与查询条件
 * 仅作防御性写法，不承载「删除后恢复」的语义。
 *
 * @author addenda
 */
@Setter
@Getter
@ToString
public class AnalyzeDedupResultEntity extends SimpleBaseModel implements Serializable {
  private static final long serialVersionUID = 1L;
  /**
   * 主键
   */
  private Long id;
  /**
   * 去重键：SHA-256(日期+systemCode+serviceName+env+detail集合+parameterizedSql)
   */
  private String dedupKey;
  /**
   * 统计周期。格式化后的字符串，当前为 {@code yyyy-MM-dd}，后续可配置为按小时等粒度
   */
  private String statPeriod;
  /**
   * 来源，当前恒为 PREPARED_STATEMENT_EXECUTION
   */
  private String source;
  /**
   * 系统编码
   */
  private String systemCode;
  /**
   * 服务名
   */
  private String serviceName;
  /**
   * 环境
   */
  private String env;
  /**
   * 镜像名
   */
  private String imageName;
  /**
   * 实例ID
   */
  private String instanceId;
  /**
   * 执行线程名，只记第一次记录时执行的线程（取自 entry_point snapshot 的 threadName）。
   * <p>去重键里的 detail 已经标识了入口/调用链，所以不需要按规则或按次数记录多个线程；合并时不更新。
   */
  private String threadName;
  /**
   * entry_point 的 detail 集合，按调用链顺序逗号拼接（不排序、不去重）
   */
  private String entryPointDetails;
  /**
   * SQL类型
   */
  private String sqlType;
  /**
   * 参数化SQL
   */
  private String parameterizedSql;
  /**
   * 命中：select *
   */
  private Integer ifSelectAll;
  /**
   * 命中 select * 的首次 execution ID
   */
  private Long selectAllOuterId;
  /**
   * 命中：全表扫描(explain type=ALL)
   */
  private Integer ifFullScan;
  /**
   * 命中 full scan 的首次 execution ID
   */
  private Long fullScanOuterId;
  /**
   * 首次命中 full scan 时，explain 全部行的 JSON
   */
  private String fullScanExplainJson;
  /**
   * 命中：IN元素数超阈值
   */
  private Integer ifInTooMany;
  /**
   * 命中 IN 过多的首次 execution ID
   */
  private Long inTooManyOuterId;
  /**
   * 首次命中 IN 过多时的 IN 元素个数
   */
  private Integer inTooManyCount;
  /**
   * 命中：cost超阈值
   */
  private Integer ifCostTooHigh;
  /**
   * 命中 cost 过高的首次 execution ID
   */
  private Long costTooHighOuterId;
  /**
   * 首次命中 cost 过高时的 cost(ms)
   */
  private Integer costTooHighCost;
  /**
   * 命中规则汇总，如 SELECT_ALL,FULL_SCAN
   */
  private String hitRules;

  public static AnalyzeDedupResultEntity ofParam() {
    return new AnalyzeDedupResultEntity();
  }

}
