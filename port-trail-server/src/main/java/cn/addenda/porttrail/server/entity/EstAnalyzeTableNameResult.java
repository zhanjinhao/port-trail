package cn.addenda.porttrail.server.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 分析-表名(EstAnalyzeTableNameResult)实体类
 *
 * @author addenda
 * @since 2026-02-24 16:44:43
 */
@Setter
@Getter
@ToString
public class EstAnalyzeTableNameResult extends SimpleBaseModel implements Serializable {
  private static final long serialVersionUID = -87642986436329269L;
  /**
   * 主键
   */
  private Long id;
  /**
   * PREPARED_STATEMENT_EXECUTION或STATEMENT_SQL
   */
  private String source;
  /**
   * SQL类型
   */
  private String sqlType;
  /**
   * EST_PREPARED_STATEMENT_EXECUTION的ID或EST_STATEMENT_SQL的ID
   */
  private Long outerId;
  /**
   * 表名
   */
  private String tableNames;
  /**
   * 表个数
   */
  private Integer tableCount;

  public static EstAnalyzeTableNameResult ofParam() {
    return new EstAnalyzeTableNameResult();
  }

}

