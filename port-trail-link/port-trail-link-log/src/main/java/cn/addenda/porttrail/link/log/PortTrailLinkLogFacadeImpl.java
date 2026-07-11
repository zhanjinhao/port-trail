package cn.addenda.porttrail.link.log;

import cn.addenda.porttrail.facade.LogFacade;
import cn.addenda.porttrail.infrastructure.jvm.JVMShutdown;
import cn.addenda.porttrail.infrastructure.jvm.JVMShutdownCallback;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.impl.Log4jContextFactory;
import org.apache.logging.log4j.core.selector.ClassLoaderContextSelector;

import java.io.File;
import java.util.Properties;

import static org.apache.logging.slf4j.Log4jLogger.FQCN;

public class PortTrailLinkLogFacadeImpl implements LogFacade {

  private final String name;
  private final String fqcn;
  private final Logger logger;

  private static final Log4jContextFactory factory = new Log4jContextFactory(new ClassLoaderContextSelector());
  private static volatile LoggerContext loggerContext;

  private static LoggerContext getLoggerContext() {
    if (loggerContext == null) {
      synchronized (PortTrailLinkLogFacadeImpl.class) {
        if (loggerContext == null) {
          Properties logProperties = LogConfigAware.getLogProperties();
          String absolutePath = logProperties.getProperty("log4j2.conf.absolutePath");
          loggerContext = factory.getContext(FQCN, null, null, false, new File(absolutePath).toURI(), null);
          JVMShutdown.getInstance().addJvmShutdownCallback(new JVMShutdownCallback() {
            @Override
            public void shutdown() throws Throwable {
              try {
                loggerContext.stop();
              } catch (Throwable ignore) {
              }
            }

            @Override
            public Integer getOrder() {
              return 100;
            }
          });
        }
      }
    }
    return loggerContext;
  }

  public PortTrailLinkLogFacadeImpl(String name, String fqcn) {
    this.name = name;
    this.fqcn = fqcn;
    this.logger = getLoggerContext().getLogger(name);
  }

  public PortTrailLinkLogFacadeImpl(String name) {
    this(name, PortTrailLinkLogFacadeImpl.class.getName());
  }

  @Override
  public boolean isTraceEnabled() {
    return logger.isTraceEnabled();
  }

  @Override
  public void trace(String format) {
    logger.logIfEnabled(fqcn, Level.TRACE, null, format);
  }

  @Override
  public void trace(String format, Object arg) {
    logger.logIfEnabled(fqcn, Level.TRACE, null, format, arg);
  }

  @Override
  public void trace(String format, Object arg1, Object arg2) {
    logger.logIfEnabled(fqcn, Level.TRACE, null, format, arg1, arg2);
  }

  @Override
  public void trace(String format, Object... arguments) {
    logger.logIfEnabled(fqcn, Level.TRACE, null, format, arguments);
  }

  @Override
  public void trace(String msg, Throwable t) {
    logger.logIfEnabled(fqcn, Level.TRACE, null, msg, t);
  }

  @Override
  public boolean isDebugEnabled() {
    return logger.isDebugEnabled();
  }

  @Override
  public void debug(String format) {
    logger.logIfEnabled(fqcn, Level.DEBUG, null, format);
  }

  @Override
  public void debug(String format, Object arg) {
    logger.logIfEnabled(fqcn, Level.DEBUG, null, format, arg);
  }

  @Override
  public void debug(String format, Object arg1, Object arg2) {
    logger.logIfEnabled(fqcn, Level.DEBUG, null, format, arg1, arg2);

  }

  @Override
  public void debug(String format, Object... arguments) {
    logger.logIfEnabled(fqcn, Level.DEBUG, null, format, arguments);
  }

  @Override
  public void debug(String msg, Throwable t) {
    logger.logIfEnabled(fqcn, Level.DEBUG, null, msg, t);
  }

  @Override
  public boolean isInfoEnabled() {
    return logger.isInfoEnabled();
  }

  @Override
  public void info(String format) {
    logger.logIfEnabled(fqcn, Level.INFO, null, format);
  }

  @Override
  public void info(String format, Object arg) {
    logger.logIfEnabled(fqcn, Level.INFO, null, format, arg);
  }

  @Override
  public void info(String format, Object arg1, Object arg2) {
    logger.logIfEnabled(fqcn, Level.INFO, null, format, arg1, arg2);
  }

  @Override
  public void info(String format, Object... arguments) {
    logger.logIfEnabled(fqcn, Level.INFO, null, format, arguments);
  }

  @Override
  public void info(String msg, Throwable t) {
    logger.logIfEnabled(fqcn, Level.INFO, null, msg, t);
  }

  @Override
  public boolean isWarnEnabled() {
    return logger.isWarnEnabled();
  }

  @Override
  public void warn(String format) {
    logger.logIfEnabled(fqcn, Level.WARN, null, format);
  }

  @Override
  public void warn(String format, Object arg) {
    logger.logIfEnabled(fqcn, Level.WARN, null, format, arg);
  }

  @Override
  public void warn(String format, Object arg1, Object arg2) {
    logger.logIfEnabled(fqcn, Level.WARN, null, format, arg1, arg2);
  }

  @Override
  public void warn(String format, Object... arguments) {
    logger.logIfEnabled(fqcn, Level.WARN, null, format, arguments);
  }

  @Override
  public void warn(String msg, Throwable t) {
    logger.logIfEnabled(fqcn, Level.WARN, null, msg, t);
  }

  @Override
  public boolean isErrorEnabled() {
    return logger.isErrorEnabled();
  }

  @Override
  public void error(String format) {
    logger.logIfEnabled(fqcn, Level.ERROR, null, format);
  }

  @Override
  public void error(String format, Object arg) {
    logger.logIfEnabled(fqcn, Level.ERROR, null, format, arg);
  }

  @Override
  public void error(String format, Object arg1, Object arg2) {
    logger.logIfEnabled(fqcn, Level.ERROR, null, format, arg1, arg2);
  }

  @Override
  public void error(String format, Object... arguments) {
    logger.logIfEnabled(fqcn, Level.ERROR, null, format, arguments);
  }

  @Override
  public void error(String msg, Throwable t) {
    logger.logIfEnabled(fqcn, Level.ERROR, null, msg, t);
  }

}
