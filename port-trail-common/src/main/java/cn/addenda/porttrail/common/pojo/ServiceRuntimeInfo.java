package cn.addenda.porttrail.common.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 服务运行时信息
 */
@Setter
@Getter
@ToString
public class ServiceRuntimeInfo implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * 服务名
   */
  private String serviceName;

  /**
   * 镜像名
   */
  private String imageName;

  /**
   * 实例ID
   */
  private String instanceId;

}
