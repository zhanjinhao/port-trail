package cn.addenda.porttrail.server.biz.analyze;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * explain 单行结果的中间载体。
 *
 * <p>由 {@link com.sf.sfa.est.manager.biz.analyze.AbstractExplainAnalyzeHandler} 解析 ResultSet 产出，
 * 各个 AnalyzeHandler 再把它转换为自己需要的结果结构，避免抽象层耦合具体的 Result 类型。
 */
@Setter
@Getter
@ToString
public class ExplainRow {

  /**
   * explain的id字段
   */
  private Long explainId;
  /**
   * explain的select_type字段
   */
  private String explainSelectType;
  /**
   * explain的table字段
   */
  private String explainTable;
  /**
   * explain的partitions字段
   */
  private String explainPartitions;
  /**
   * explain的type字段
   */
  private String explainType;
  /**
   * explain的possible_keys字段
   */
  private String explainPossibleKeys;
  /**
   * explain的key字段
   */
  private String explainKey;
  /**
   * explain的key_len字段
   */
  private Integer explainKeyLen;
  /**
   * explain的ref字段
   */
  private String explainRef;
  /**
   * explain的rows字段
   */
  private Integer explainRows;
  /**
   * explain的filtered字段
   */
  private Integer explainFiltered;
  /**
   * explain的Extra字段
   */
  private String explainExtra;

}