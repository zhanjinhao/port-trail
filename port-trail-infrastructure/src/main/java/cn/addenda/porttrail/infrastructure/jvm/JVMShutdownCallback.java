package cn.addenda.porttrail.infrastructure.jvm;

public interface JVMShutdownCallback {

  void shutdown() throws Throwable;

  int getOrder();

}
