package cn.addenda.porttrail.link.http;

import cn.addenda.porttrail.facade.AgentPropertiesAware;
import cn.addenda.porttrail.facade.FacadeType;
import cn.addenda.porttrail.facade.LinkPropertiesAware;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Properties;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpConfigAware {

  private static Properties agentProperties;
  private static Properties httpProperties;

  public static Properties getAgentProperties() {
    return agentProperties;
  }

  @AgentPropertiesAware
  public static void setAgentProperties(Properties agentProperties) {
    HttpConfigAware.agentProperties = agentProperties;
  }

  public static Properties getHttpProperties() {
    return httpProperties;
  }

  @LinkPropertiesAware(value = FacadeType.HTTP)
  public static void setHttpProperties(Properties httpProperties) {
    HttpConfigAware.httpProperties = httpProperties;
  }

}
