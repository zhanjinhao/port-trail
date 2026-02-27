package cn.addenda.porttrail.server.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 数据库配置(EstDbConfig)实体类
 *
 * @author addenda
 * @since 2026-02-16 19:07:07
 */
@Setter
@Getter
@ToString
public class EstDbConfig extends SimpleBaseModel implements Serializable {
  private static final long serialVersionUID = 478010776641425227L;
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
   * jdbcUrl
   */
  private String jdbcUrl;
  /**
   * user
   */
  private String user;
  /**
   * password
   */
  private String password;
  /**
   * driverName
   */
  private String driverName;
  /**
   * t_est_entry_point_snapshot的ID
   */
  private Long entryPointSnapshotId;

  public static EstDbConfig ofParam() {
    return new EstDbConfig();
  }

}

