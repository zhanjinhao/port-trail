package cn.addenda.porttrail.agent.transform.interceptor.server.jetty;

import cn.addenda.porttrail.agent.transform.interceptor.Interceptor;
import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPoint;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

public class JettyServerHandleInterceptorPoint implements InterceptorPoint {

  @Override
  public ElementMatcher<MethodDescription> getMethodsMatcher() {
    return ElementMatchers.named("handle")
            .and(ElementMatchers.isPublic())
            .and(ElementMatchers.takesArguments(1))
            .and(ElementMatchers.takesArgument(0, ElementMatchers.named("org.eclipse.jetty.server.HttpChannel")));
  }

  @Override
  public Interceptor getInterceptor() {
    return new JettyServerInterceptor();
  }

}
