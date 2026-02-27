package cn.addenda.porttrail.infrastructure.log;

public interface PortTrailLoggerFactory {

  PortTrailLogger getPortTrailLogger(String name);

  PortTrailLogger getPortTrailLogger(Class<?> clazz);

}
