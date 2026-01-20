package cn.addenda.porttrail.client.task;

import java.util.concurrent.Callable;

public final class PortTrailCallable<R> implements Callable<R>, PortTrailTask<Callable<? extends R>> {

  private final Callable<R> callable;

  private PortTrailCallable(Callable<R> callable) {
    this.callable = callable;
  }

  public static <R> PortTrailCallable<R> of(Callable<R> callable) {
    return new PortTrailCallable<>(callable);
  }

  @Override
  public R call() throws Exception {
    return callable.call();
  }

  @Override
  public Callable<? extends R> getDelegate() {
    return callable;
  }

}
