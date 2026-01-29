package cn.addenda.porttrail.common.exception;

public class PortTrailException extends RuntimeException {

  public PortTrailException() {
  }

  public PortTrailException(String message) {
    super(message);
  }

  public PortTrailException(String message, Throwable cause) {
    super(message, cause);
  }

  public PortTrailException(Throwable cause) {
    super(cause);
  }

  public PortTrailException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
