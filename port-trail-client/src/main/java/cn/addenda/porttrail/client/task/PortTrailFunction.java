package cn.addenda.porttrail.client.task;

import java.util.function.Function;

public final class PortTrailFunction<T, R> implements Function<T, R>, PortTrailTask<Function<? extends T, ? extends R>> {

  private final Function<T, R> function;

  private PortTrailFunction(Function<T, R> function) {
    this.function = function;
  }

  public static <T, R> PortTrailFunction<T, R> of(Function<T, R> function) {
    return new PortTrailFunction<>(function);
  }

  @Override
  public R apply(T t) {
    return function.apply(t);
  }

  @Override
  public Function<? extends T, ? extends R> getDelegate() {
    return function;
  }

}
