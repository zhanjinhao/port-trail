package cn.addenda.porttrail.infrastructure.jvm;

public interface JVMShutdownCallback {

  void shutdown() throws Throwable;

  Integer getOrder();

}
