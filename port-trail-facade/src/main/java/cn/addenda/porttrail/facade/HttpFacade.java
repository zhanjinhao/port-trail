package cn.addenda.porttrail.facade;

public interface HttpFacade {

  void sendRequest(String uri, String jsonParam);

  void sendRequest(String uri, byte[] bytesParam);

}
