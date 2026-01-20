package cn.addenda.porttrail.facade;

public interface JsonFacade {

  String toStr(Object input);

  <T> T toObj(String inputJson, Class<T> clazz);

}
