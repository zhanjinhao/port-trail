package cn.addenda.porttrail.agent.log;


import cn.addenda.porttrail.agent.link.LinkFacade;
import cn.addenda.porttrail.facade.LogFacade;
import cn.addenda.porttrail.infrastructure.log.PortTrailLogger;

public class AgentPortTrailLogger implements PortTrailLogger {

  private final String name;

  private final LogFacade logFacade;

  public AgentPortTrailLogger(String name) {
    this.name = name;
    this.logFacade = LinkFacade.createLogFacade(name, AgentPortTrailLogger.class.getName());
  }

  @Override
  public boolean isTraceEnabled() {
    return logFacade.isTraceEnabled();
  }

  @Override
  public void trace(String msg) {
    logFacade.trace(msg);
  }

  @Override
  public void trace(String format, Object arg) {
    logFacade.trace(format, arg);
  }

  @Override
  public void trace(String format, Object arg1, Object arg2) {
    logFacade.trace(format, arg1, arg2);
  }

  @Override
  public void trace(String format, Object... arguments) {
    logFacade.trace(format, arguments);
  }

  @Override
  public boolean isDebugEnabled() {
    return logFacade.isDebugEnabled();
  }

  @Override
  public void debug(String msg) {
    logFacade.debug(msg);
  }

  @Override
  public void debug(String format, Object arg) {
    logFacade.debug(format, arg);
  }

  @Override
  public void debug(String format, Object arg1, Object arg2) {
    logFacade.debug(format, arg1, arg2);
  }

  @Override
  public void debug(String format, Object... arguments) {
    logFacade.debug(format, arguments);
  }

  @Override
  public boolean isInfoEnabled() {
    return logFacade.isInfoEnabled();
  }

  @Override
  public void info(String msg) {
    logFacade.info(msg);
  }

  @Override
  public void info(String format, Object arg) {
    logFacade.info(format, arg);
  }

  @Override
  public void info(String format, Object arg1, Object arg2) {
    logFacade.info(format, arg1, arg2);
  }

  @Override
  public void info(String format, Object... arguments) {
    logFacade.info(format, arguments);
  }

  @Override
  public boolean isWarnEnabled() {
    return logFacade.isWarnEnabled();
  }

  @Override
  public void warn(String msg) {
    logFacade.warn(msg);
  }

  @Override
  public void warn(String format, Object arg) {
    logFacade.warn(format, arg);
  }

  @Override
  public void warn(String format, Object arg1, Object arg2) {
    logFacade.warn(format, arg1, arg2);
  }

  @Override
  public void warn(String format, Object... arguments) {
    logFacade.warn(format, arguments);
  }

  @Override
  public boolean isErrorEnabled() {
    return logFacade.isErrorEnabled();
  }

  @Override
  public void error(String msg) {
    logFacade.error(msg);
  }

  @Override
  public void error(String format, Object arg) {
    logFacade.error(format, arg);
  }

  @Override
  public void error(String format, Object arg1, Object arg2) {
    logFacade.error(format, arg1, arg2);
  }

  @Override
  public void error(String format, Object... arguments) {
    logFacade.error(format, arguments);
  }

}
