package cn.addenda.porttrail.server.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 分析-SQL的explain(EstAnalyzeExplainResult)实体类
 *
 * @author addenda
 * @since 2026-02-24 21:12:52
 */
@Setter
@Getter
@ToString
public class EstAnalyzeExplainResult extends SimpleBaseModel implements Serializable {
  private static final long serialVersionUID = -22505042081129067L;
  /**
   * 主键
   */
  private Long id;
  /**
   * PREPARED_STATEMENT_PARAMETER或者STATEMENT_SQL
   */
  private String source;
  /**
   * EST_PREPARED_STATEMENT_PARAMETER的ID或EST_STATEMENT_SQL的ID
   */
  private Long outerId;
  /**
   * SQL类型
   */
  private String sqlType;
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
   * explain的extra字段
   */
  private String explainExtra;

  public static EstAnalyzeExplainResult ofParam() {
    return new EstAnalyzeExplainResult();
  }

}

