package cn.addenda.porttrail.link.json;

import cn.addenda.porttrail.facade.JsonFacade;

public class PortTrailLinkJsonFacadeImpl implements JsonFacade {

  public PortTrailLinkJsonFacadeImpl() {
  }

  @Override
  public String toStr(Object input) {
    return PortTrailLinkJsonException.getWithPortTrailLinkJsonException(
            () -> PortTrailJsonUtils.toStr(input)
    );
  }

  @Override
  public <T> T toObj(String inputJson, Class<T> clazz) {
    return PortTrailLinkJsonException.getWithPortTrailLinkJsonException(
            () -> PortTrailJsonUtils.toObj(inputJson, clazz)
    );
  }

}
