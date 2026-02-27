package cn.addenda.porttrail.client.task;

public final class PortTrailRunnable implements Runnable, PortTrailTask<Runnable> {

  private final Runnable runnable;

  private PortTrailRunnable(Runnable runnable) {
    this.runnable = runnable;
  }

  public static PortTrailRunnable of(Runnable runnable) {
    return new PortTrailRunnable(runnable);
  }

  @Override
  public void run() {
    runnable.run();
  }

  @Override
  public Runnable getDelegate() {
    return runnable;
  }

}
