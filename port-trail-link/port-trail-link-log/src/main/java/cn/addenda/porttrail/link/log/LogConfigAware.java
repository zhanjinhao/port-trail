package cn.addenda.porttrail.link.log;

import cn.addenda.porttrail.facade.AgentPropertiesAware;
import cn.addenda.porttrail.facade.LinkPropertiesAware;
import cn.addenda.porttrail.facade.FacadeType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Properties;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LogConfigAware {

  private static Properties agentProperties;
  private static Properties logProperties;

  public static Properties getAgentProperties() {
    return agentProperties;
  }

  @AgentPropertiesAware
  public static void setAgentProperties(Properties agentProperties) {
    LogConfigAware.agentProperties = agentProperties;
  }

  public static Properties getLogProperties() {
    return logProperties;
  }

  @LinkPropertiesAware(value = FacadeType.LOG)
  public static void setLogProperties(Properties logProperties) {
    LogConfigAware.logProperties = logProperties;
  }

}
