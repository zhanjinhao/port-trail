package cn.addenda.porttrail.server.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 分析-select语句是否有*(EstAnalyzeSelectAllResult)实体类
 *
 * @author addenda
 * @since 2026-02-24 18:52:04
 */
@Setter
@Getter
@ToString
public class EstAnalyzeSelectAllResult extends SimpleBaseModel implements Serializable {
  private static final long serialVersionUID = -43378747428029987L;
  /**
   * 主键
   */
  private Long id;
  /**
   * PREPARED_STATEMENT_EXECUTION或STATEMENT_SQL
   */
  private String source;
  /**
   * EST_PREPARED_STATEMENT_EXECUTION的ID或EST_STATEMENT_SQL的ID
   */
  private Long outerId;
  /**
   * 是否查询了所有字段
   */
  private Integer ifSelectAll;

  public static EstAnalyzeSelectAllResult ofParam() {
    return new EstAnalyzeSelectAllResult();
  }

}

