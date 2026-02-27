package cn.addenda.porttrail.infrastructure.context;

import cn.addenda.porttrail.common.pojo.ServiceRuntimeInfo;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AgentContext {

  /**
   * 系统编码
   */
  @Getter
  private static String systemCode;

  /**
   * 服务名
   */
  @Getter
  private static String serviceName;

  /**
   * 镜像名
   */
  @Getter
  private static String imageName;

  /**
   * 环境
   */
  @Getter
  private static String env;

  /**
   * 实例ID
   */
  @Getter
  private static String instanceId;

  public static void setSystemCode(String systemCode) {
    AgentContext.systemCode = systemCode;
  }

  public static void setServiceName(String serviceName) {
    AgentContext.serviceName = serviceName;
  }

  public static void setImageName(String imageName) {
    AgentContext.imageName = imageName;
  }

  public static void setEnv(String env) {
    AgentContext.env = env;
  }

  public static void setInstanceId(String instanceId) {
    AgentContext.instanceId = instanceId;
  }


  private static ServiceRuntimeInfo serviceRuntimeInfoInstance;

  public static void postInit() {
    serviceRuntimeInfoInstance = new ServiceRuntimeInfo();
    serviceRuntimeInfoInstance.setSystemCode(systemCode);
    serviceRuntimeInfoInstance.setServiceName(serviceName);
    serviceRuntimeInfoInstance.setImageName(imageName);
    serviceRuntimeInfoInstance.setEnv(env);
    serviceRuntimeInfoInstance.setInstanceId(instanceId);
  }

  public static ServiceRuntimeInfo getServiceRuntimeInfo() {
    return serviceRuntimeInfoInstance;
  }

}
