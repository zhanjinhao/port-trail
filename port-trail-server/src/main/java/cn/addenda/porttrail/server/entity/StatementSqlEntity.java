package cn.addenda.porttrail.server.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * StatementSql(StatementSqlEntity)实体类
 *
 * @author addenda
 * @since 2026-02-16 19:10:07
 */
@Setter
@Getter
@ToString
public class StatementSqlEntity extends SimpleBaseModel implements Serializable {
  private static final long serialVersionUID = -61680516350769370L;
  /**
   * 主键
   */
  private Long id;
  /**
   * t_statement_execution_entity的ID
   */
  private Long statementExecutionId;
  /**
   * 执行的SQL
   */
  private String sql;
  /**
   * 当前SQL在statement里的顺序
   */
  private Integer orderInStatement;
  /**
   * 当前SQL在connection里的顺序
   */
  private Integer orderInConnection;

  public static StatementSqlEntity ofParam() {
    return new StatementSqlEntity();
  }

}
