package cn.addenda.porttrail.jdbc.log;

import cn.addenda.porttrail.infrastructure.log.PortTrailLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcPortTrailLogger implements PortTrailLogger {

  private final Logger log;

  public JdbcPortTrailLogger(String name) {
    this.log = LoggerFactory.getLogger(name);
  }

  @Override
  public boolean isTraceEnabled() {
    return log.isTraceEnabled();
  }

  @Override
  public void trace(String msg) {
    log.trace(msg);
  }

  @Override
  public void trace(String format, Object arg) {
    log.trace(format, arg);
  }

  @Override
  public void trace(String format, Object arg1, Object arg2) {
    log.trace(format, arg1, arg2);
  }

  @Override
  public void trace(String format, Object... arguments) {
    log.trace(format, arguments);
  }

  @Override
  public boolean isDebugEnabled() {
    return log.isDebugEnabled();
  }

  @Override
  public void debug(String msg) {
    log.debug(msg);
  }

  @Override
  public void debug(String format, Object arg) {
    log.debug(format, arg);
  }

  @Override
  public void debug(String format, Object arg1, Object arg2) {
    log.debug(format, arg1, arg2);
  }

  @Override
  public void debug(String format, Object... arguments) {
    log.debug(format, arguments);
  }

  @Override
  public boolean isInfoEnabled() {
    return log.isInfoEnabled();
  }

  @Override
  public void info(String msg) {
    log.info(msg);
  }

  @Override
  public void info(String format, Object arg) {
    log.info(format, arg);
  }

  @Override
  public void info(String format, Object arg1, Object arg2) {
    log.info(format, arg1, arg2);
  }

  @Override
  public void info(String format, Object... arguments) {
    log.info(format, arguments);
  }

  @Override
  public boolean isWarnEnabled() {
    return log.isWarnEnabled();
  }

  @Override
  public void warn(String msg) {
    log.warn(msg);
  }

  @Override
  public void warn(String format, Object arg) {
    log.warn(format, arg);
  }

  @Override
  public void warn(String format, Object arg1, Object arg2) {
    log.warn(format, arg1, arg2);
  }

  @Override
  public void warn(String format, Object... arguments) {
    log.warn(format, arguments);
  }

  @Override
  public boolean isErrorEnabled() {
    return log.isErrorEnabled();
  }

  @Override
  public void error(String msg) {
    log.error(msg);
  }

  @Override
  public void error(String format, Object arg) {
    log.error(format, arg);
  }

  @Override
  public void error(String format, Object arg1, Object arg2) {
    log.error(format, arg1, arg2);
  }

  @Override
  public void error(String format, Object... arguments) {
    log.error(format, arguments);
  }

}
