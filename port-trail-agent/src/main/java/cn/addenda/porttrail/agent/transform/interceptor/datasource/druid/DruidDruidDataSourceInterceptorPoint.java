package cn.addenda.porttrail.agent.transform.interceptor.datasource.druid;

import cn.addenda.porttrail.agent.transform.interceptor.Interceptor;
import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPoint;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

public class DruidDruidDataSourceInterceptorPoint implements InterceptorPoint {

  @Override
  public ElementMatcher<MethodDescription> getMethodsMatcher() {
    return ElementMatchers.named("getConnectionDirect");
  }

  @Override
  public Interceptor getInterceptor() {
    return new DruidDruidDataSourceGetConnectionDirectInterceptor();
  }

}
