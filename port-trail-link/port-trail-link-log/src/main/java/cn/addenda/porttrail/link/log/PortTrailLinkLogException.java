package cn.addenda.porttrail.link.log;

import cn.addenda.porttrail.common.exception.PortTrailException;

public class PortTrailLinkLogException extends PortTrailException {

  public PortTrailLinkLogException() {
  }

  public PortTrailLinkLogException(String message) {
    super(message);
  }

  public PortTrailLinkLogException(String message, Throwable cause) {
    super(message, cause);
  }

  public PortTrailLinkLogException(Throwable cause) {
    super(cause);
  }

  public PortTrailLinkLogException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public static void runWithPortTrailLinkLogException(TRunnable tRunnable) {
    try {
      tRunnable.run();
    } catch (Throwable t) {
      throw new PortTrailLinkLogException(t);
    }
  }

  public static <T> T getWithPortTrailLinkLogException(TSupplier<T> tSupplier) {
    try {
      return tSupplier.get();
    } catch (Throwable t) {
      throw new PortTrailLinkLogException(t);
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
