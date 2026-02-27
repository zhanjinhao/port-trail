package cn.addenda.porttrail.link.json;

import cn.addenda.porttrail.facade.AgentPropertiesAware;
import cn.addenda.porttrail.facade.LinkPropertiesAware;
import cn.addenda.porttrail.facade.FacadeType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Properties;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonConfigAware {

  private static Properties agentProperties;
  private static Properties jsonProperties;

  public static Properties getAgentProperties() {
    return agentProperties;
  }

  @AgentPropertiesAware
  public static void setAgentProperties(Properties agentProperties) {
    JsonConfigAware.agentProperties = agentProperties;
  }

  public static Properties getJsonProperties() {
    return jsonProperties;
  }

  @LinkPropertiesAware(value = FacadeType.JSON)
  public static void setJsonProperties(Properties jsonProperties) {
    JsonConfigAware.jsonProperties = jsonProperties;
  }

}
