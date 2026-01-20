package cn.addenda.porttrail.jdbc.log;

import cn.addenda.porttrail.infrastructure.log.PortTrailLogger;
import cn.addenda.porttrail.infrastructure.log.PortTrailLoggerFactory;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JdbcPortTrailLoggerFactory implements PortTrailLoggerFactory {

  @Getter
  private static final JdbcPortTrailLoggerFactory instance;

  static {
    instance = new JdbcPortTrailLoggerFactory();
  }

  private final Map<String, PortTrailLogger> portTrailLoggerMap = new ConcurrentHashMap<>();

  private JdbcPortTrailLoggerFactory() {
  }

  @Override
  public PortTrailLogger getPortTrailLogger(String name) {
    return portTrailLoggerMap.computeIfAbsent(name, JdbcPortTrailLogger::new);
  }

  @Override
  public PortTrailLogger getPortTrailLogger(Class<?> clazz) {
    return getPortTrailLogger(clazz.getName());
  }

}
