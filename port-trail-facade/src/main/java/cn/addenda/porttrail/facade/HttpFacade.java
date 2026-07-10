package cn.addenda.porttrail.facade;

public interface HttpFacade {

  HttpRequestResult sendRequest(String uri, String jsonParam);

  HttpRequestResult sendRequest(String uri, byte[] bytesParam);

}
