package cn.addenda.porttrail.server.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * EntryPoint(EntryPointEntity)实体类
 *
 * @author addenda
 * @since 2026-02-16 18:40:58
 */
@Setter
@Getter
@ToString
public class EntryPointEntity extends SimpleBaseModel implements Serializable {
  private static final long serialVersionUID = 850274240866886640L;
  /**
   * 主键
   */
  private Long id;

  // ------------------------------------------------------ entryPointType & detail ----------------------------------------
  //     JOB_XXL_JOB_HANDLER,charterAgreementAutoGenerateOrderJob:com.ade.task.job.CharterAgreementAutoGenerateOrderJob
  //     ORM_MYBATIS,com.ade.monitor.mapper.MonitorAlarmMapper.selectAlarmByFlightId
  //     SERVER_JETTY,org.eclipse.jetty.server.Server#handle
  //     REMOTE_JDBC,cn.addenda.porttrail.jdbc.core.PortTrailPreparedStatement#execute
  // ------------------------------------------------------------ entryPointType & detail ----------------------------------

  /**
   * 进入点名称
   */
  private String entryPointType;
  /**
   * 进入点详情
   */
  private String detail;
  /**
   * 进入点ID
   */
  private Long entryId;
  /**
   * t_entry_point_entity_snapshot的ID
   */
  private Long entryPointSnapshotId;

  public static EntryPointEntity ofParam() {
    return new EntryPointEntity();
  }

}
