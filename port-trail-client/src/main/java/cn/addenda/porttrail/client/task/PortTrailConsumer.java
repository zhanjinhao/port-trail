package cn.addenda.porttrail.client.task;

import java.util.function.Consumer;

public final class PortTrailConsumer<T> implements Consumer<T>, PortTrailTask<Consumer<? extends T>> {

  private final Consumer<T> consumer;

  private PortTrailConsumer(Consumer<T> consumer) {
    this.consumer = consumer;
  }

  public static <T> PortTrailConsumer<T> of(Consumer<T> consumer) {
    return new PortTrailConsumer<>(consumer);
  }

  @Override
  public void accept(T t) {
    consumer.accept(t);
  }

  @Override
  public Consumer<? extends T> getDelegate() {
    return consumer;
  }

}
