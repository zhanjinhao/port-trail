package cn.addenda.porttrail.client.task;

import java.util.function.Supplier;

public final class PortTrailSupplier<R> implements Supplier<R>, PortTrailTask<Supplier<? extends R>> {

  private final Supplier<R> supplier;

  private PortTrailSupplier(Supplier<R> supplier) {
    this.supplier = supplier;
  }

  public static <R> PortTrailSupplier<R> of(Supplier<R> supplier) {
    return new PortTrailSupplier<>(supplier);
  }

  @Override
  public R get() {
    return supplier.get();
  }

  @Override
  public Supplier<? extends R> getDelegate() {
    return supplier;
  }

}
