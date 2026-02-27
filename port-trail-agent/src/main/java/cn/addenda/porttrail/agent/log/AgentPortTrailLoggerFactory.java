package cn.addenda.porttrail.agent.log;

import cn.addenda.porttrail.infrastructure.log.PortTrailLogger;
import cn.addenda.porttrail.infrastructure.log.PortTrailLoggerFactory;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AgentPortTrailLoggerFactory implements PortTrailLoggerFactory {

  @Getter
  private static final AgentPortTrailLoggerFactory instance;

  static {
    instance = new AgentPortTrailLoggerFactory();
  }

  private final Map<String, PortTrailLogger> portTrailLoggerMap = new ConcurrentHashMap<>();

  private AgentPortTrailLoggerFactory() {
  }

  @Override
  public PortTrailLogger getPortTrailLogger(String name) {
    return portTrailLoggerMap.computeIfAbsent(name, AgentPortTrailLogger::new);
  }

  @Override
  public PortTrailLogger getPortTrailLogger(Class<?> clazz) {
    return getPortTrailLogger(clazz.getName());
  }

}
