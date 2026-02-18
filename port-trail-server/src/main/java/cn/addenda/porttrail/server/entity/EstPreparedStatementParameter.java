package cn.addenda.porttrail.server.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * PreparedStatementParameter(EstPreparedStatementParameter)实体类
 *
 * @author addenda
 * @since 2026-02-16 19:08:23
 */
@Setter
@Getter
@ToString
public class EstPreparedStatementParameter extends SimpleBaseModel implements Serializable {
  private static final long serialVersionUID = 601509619918514788L;
  /**
   * 主键
   */
  private Long id;
  /**
   * t_est_prepared_statement_execution的ID
   */
  private Long preparedStatementExecutionId;
  /**
   * 参数序列化为JSON
   */
  private String parameterJson;
  /**
   * 参数序列化为数组
   */
  private byte[] parameterBytes;
  /**
   * 参数个数
   */
  private Integer capacity;
  /**
   * 当前SQL再statement里的顺序
   */
  private Integer orderInStatement;
  /**
   * 当前SQL再connection里的顺序
   */
  private Integer orderInConnection;

  public static EstPreparedStatementParameter ofParam() {
    return new EstPreparedStatementParameter();
  }

}

