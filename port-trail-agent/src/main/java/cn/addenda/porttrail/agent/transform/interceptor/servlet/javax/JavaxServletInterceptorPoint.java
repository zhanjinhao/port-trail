package cn.addenda.porttrail.agent.transform.interceptor.servlet.javax;

import cn.addenda.porttrail.agent.transform.interceptor.Interceptor;
import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPoint;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

public class JavaxServletInterceptorPoint implements InterceptorPoint {

  @Override
  public ElementMatcher<MethodDescription> getMethodsMatcher() {
    return ElementMatchers.named("service").and(ElementMatchers.isPublic());
  }

  @Override
  public Interceptor getInterceptor() {
    return new JavaxServletServiceInterceptor();
  }

}
