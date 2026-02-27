package cn.addenda.porttrail.agent.transform.interceptor.server.jetty;

import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPoint;
import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPointDefine;
import cn.addenda.porttrail.agent.transform.match.NameMatch;
import cn.addenda.porttrail.common.util.ArrayUtils;

import java.util.List;

public class JettyServerInterceptorPointDefine implements InterceptorPointDefine {

  @Override
  public NameMatch getEnhancedClass() {
    return NameMatch.of("org.eclipse.jetty.server.Server");
  }

  @Override
  public List<InterceptorPoint> getInterceptorPointList() {
    return ArrayUtils.asArrayList(new JettyServerHandleInterceptorPoint());
  }

}
