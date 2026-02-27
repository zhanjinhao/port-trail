package cn.addenda.porttrail.agent.transform.interceptor.driver.oracle;

import cn.addenda.porttrail.agent.transform.interceptor.Interceptor;
import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPoint;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import java.sql.Driver;

public class OracleDriverInterceptorPoint implements InterceptorPoint {

  @Override
  public ElementMatcher<MethodDescription> getMethodsMatcher() {
    return ElementMatchers.named("connect").and(ElementMatchers.isOverriddenFrom(Driver.class));
  }

  @Override
  public Interceptor getInterceptor() {
    return new OracleDriverConnectInterceptor();
  }

}
