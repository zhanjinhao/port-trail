package cn.addenda.porttrail.link.http;

import cn.addenda.porttrail.common.exception.PortTrailException;

public class PortTrailLinkHttpException extends PortTrailException {

  public PortTrailLinkHttpException() {
  }

  public PortTrailLinkHttpException(String message) {
    super(message);
  }

  public PortTrailLinkHttpException(String message, Throwable cause) {
    super(message, cause);
  }

  public PortTrailLinkHttpException(Throwable cause) {
    super(cause);
  }

  public PortTrailLinkHttpException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public static void runWithPortTrailLinkHttpException(TRunnable tRunnable) {
    try {
      tRunnable.run();
    } catch (Throwable t) {
      throw new PortTrailLinkHttpException(t);
    }
  }

  public static <T> T getWithPortTrailLinkHttpException(TSupplier<T> tSupplier) {
    try {
      return tSupplier.get();
    } catch (Throwable t) {
      throw new PortTrailLinkHttpException(t);
    }
  }

  @FunctionalInterface
  public interface TRunnable {

    void run() throws Throwable;
  }

  @FunctionalInterface
  public interface TSupplier<T> {

    T get() throws Throwable;
  }

}
