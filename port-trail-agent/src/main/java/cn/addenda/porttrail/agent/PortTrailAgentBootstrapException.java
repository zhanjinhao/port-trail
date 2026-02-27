package cn.addenda.porttrail.agent;

/**
 * 这个异常是在Agent启动后，还没有处理自定义ClassLoader的时候使用。如果启动有问题，会扔出此异常。
 */
public class PortTrailAgentBootstrapException extends RuntimeException {

  public PortTrailAgentBootstrapException() {
  }

  public PortTrailAgentBootstrapException(String message) {
    super(message);
  }

  public PortTrailAgentBootstrapException(String message, Throwable cause) {
    super(message, cause);
  }

  public PortTrailAgentBootstrapException(Throwable cause) {
    super(cause);
  }

  public PortTrailAgentBootstrapException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
