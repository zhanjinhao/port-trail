package cn.addenda.porttrail.agent.transform.interceptor.servlet.javax;

import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPoint;
import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPointDefine;
import cn.addenda.porttrail.agent.transform.match.ClassMatch;
import cn.addenda.porttrail.agent.transform.match.NameMatch;
import cn.addenda.porttrail.common.util.ArrayUtils;

import java.util.List;

public class JavaxServletInterceptorPointDefine implements InterceptorPointDefine {

  @Override
  public ClassMatch getEnhancedClass() {
    return NameMatch.of("javax.servlet.http.HttpServlet");
  }

  @Override
  public List<InterceptorPoint> getInterceptorPointList() {
    return ArrayUtils.asArrayList(new JavaxServletInterceptorPoint());
  }

}
