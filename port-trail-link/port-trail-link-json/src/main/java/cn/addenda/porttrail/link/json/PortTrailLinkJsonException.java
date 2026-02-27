package cn.addenda.porttrail.link.json;

import cn.addenda.porttrail.common.exception.PortTrailException;

public class PortTrailLinkJsonException extends PortTrailException {

  public PortTrailLinkJsonException() {
  }

  public PortTrailLinkJsonException(String message) {
    super(message);
  }

  public PortTrailLinkJsonException(String message, Throwable cause) {
    super(message, cause);
  }

  public PortTrailLinkJsonException(Throwable cause) {
    super(cause);
  }

  public PortTrailLinkJsonException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public static void runWithPortTrailLinkJsonException(TRunnable tRunnable) {
    try {
      tRunnable.run();
    } catch (Throwable t) {
      throw new PortTrailLinkJsonException(t);
    }
  }

  public static <T> T getWithPortTrailLinkJsonException(TSupplier<T> tSupplier) {
    try {
      return tSupplier.get();
    } catch (Throwable t) {
      throw new PortTrailLinkJsonException(t);
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
