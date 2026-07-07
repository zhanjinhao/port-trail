package cn.addenda.porttrail.server.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * StatementExecution(StatementExecutionEntity)实体类
 *
 * @author addenda
 * @since 2026-02-16 19:09:36
 */
@Setter
@Getter
@ToString
public class StatementExecutionEntity extends SimpleBaseModel implements Serializable {
  private static final long serialVersionUID = 339631787150169125L;
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
   * t_entry_point_snapshot_entity的ID
   */
  private Long entryPointSnapshotId;
  /**
   * 是否分析过
   */
  private Integer ifAnalyzed;

  public static StatementExecutionEntity ofParam() {
    return new StatementExecutionEntity();
  }

}
