package cn.addenda.porttrail.agent.transform.interceptor.server.tomcat;

import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPoint;
import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPointDefine;
import cn.addenda.porttrail.agent.transform.match.NameMatch;
import cn.addenda.porttrail.common.util.ArrayUtils;

import java.util.List;

public class TomcatAbstractProtocolInterceptorPointDefine implements InterceptorPointDefine {

  @Override
  public NameMatch getEnhancedClass() {
    return NameMatch.of("org.apache.coyote.AbstractProtocol$ConnectionHandler");
  }

  @Override
  public List<InterceptorPoint> getInterceptorPointList() {
    return ArrayUtils.asArrayList(new TomcatAbstractProtocolProcessInterceptorPoint());
  }

}
