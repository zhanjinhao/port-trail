package cn.addenda.porttrail.server.manager;

public interface PortTrailDeserializeThrowableLogManager {

  void insert(byte[] bytes, String deserializeType, Throwable throwable);

}
