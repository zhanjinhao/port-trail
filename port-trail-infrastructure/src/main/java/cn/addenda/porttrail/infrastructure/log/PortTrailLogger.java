package cn.addenda.porttrail.infrastructure.log;

public interface PortTrailLogger {

  boolean isTraceEnabled();

  void trace(String msg);

  void trace(String format, Object... arguments);

  boolean isDebugEnabled();

  void debug(String msg);

  void debug(String format, Object... arguments);

  boolean isInfoEnabled();

  void info(String msg);

  void info(String format, Object... arguments);

  boolean isWarnEnabled();

  void warn(String msg);

  void warn(String format, Object... arguments);

  boolean isErrorEnabled();

  void error(String msg);

  void error(String format, Object... arguments);

}
