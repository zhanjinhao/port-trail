package cn.addenda.porttrail.agent;

import cn.addenda.porttrail.common.exception.PortTrailException;

/**
 * 这个异常是在处理自定义ClassLoader后，已经进行Class改写，或者加载自定义link包的时候使用。
 */
public class PortTrailAgentStartException extends PortTrailException {

  public PortTrailAgentStartException() {
  }

  public PortTrailAgentStartException(String message) {
    super(message);
  }

  public PortTrailAgentStartException(String message, Throwable cause) {
    super(message, cause);
  }

  public PortTrailAgentStartException(Throwable cause) {
    super(cause);
  }

  public PortTrailAgentStartException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
