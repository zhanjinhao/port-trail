package cn.addenda.porttrail.server.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * PreparedStatementExecution(EstPreparedStatementExecution)实体类
 *
 * @author addenda
 * @since 2026-02-16 19:07:53
 */
@Setter
@Getter
@ToString
public class EstPreparedStatementExecution extends SimpleBaseModel implements Serializable {
  private static final long serialVersionUID = -18644157344698148L;
  /**
   * 主键
   */
  private Long id;
  /**
   * 系统编码
   */
  private String systemCode;
  /**
   * 服务名
   */
  private String serviceName;
  /**
   * 镜像名
   */
  private String imageName;
  /**
   * 环境
   */
  private String env;
  /**
   * 实例ID
   */
  private String instanceId;
  /**
   * dataSourcePortTrailId
   */
  private String dataSourcePortTrailId;
  /**
   * connectionPortTrailId
   */
  private String connectionPortTrailId;
  /**
   * statementPortTrailId
   */
  private String statementPortTrailId;
  /**
   * 预编译的SQL
   */
  private String parameterizedSql;

  private String statementState;
  /**
   * 事务ID
   */
  private String txId;
  /**
   * 开始执行时间
   */
  private LocalDateTime start;
  /**
   * 结束执行时间
   */
  private LocalDateTime end;
  /**
   * 耗时
   */
  private Integer cost;
  /**
   * t_est_entry_point_snapshot的ID
   */
  private Long entryPointSnapshotId;
  /**
   * 是否分析过
   */
  private Integer ifAnalyzed;

  public static EstPreparedStatementExecution ofParam() {
    return new EstPreparedStatementExecution();
  }

}

