package cn.addenda.porttrail.agent.transform.interceptor.datasource.hikari;

import cn.addenda.porttrail.agent.transform.interceptor.Interceptor;
import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPoint;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

public class HikariConcurrentBagInterceptorPoint implements InterceptorPoint {

  @Override
  public ElementMatcher<MethodDescription> getMethodsMatcher() {
    return ElementMatchers.named("borrow");
  }

  @Override
  public Interceptor getInterceptor() {
    return new HikariConcurrentBagBorrowInterceptor();
  }

}
