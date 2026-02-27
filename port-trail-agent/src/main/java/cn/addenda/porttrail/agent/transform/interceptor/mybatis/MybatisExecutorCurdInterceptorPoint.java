package cn.addenda.porttrail.agent.transform.interceptor.mybatis;

import cn.addenda.porttrail.agent.transform.interceptor.Interceptor;
import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPoint;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

public class MybatisExecutorCurdInterceptorPoint implements InterceptorPoint {

  @Override
  public ElementMatcher<MethodDescription> getMethodsMatcher() {
    return ElementMatchers.named("update")
            .or(ElementMatchers.named("query"))
            .or(ElementMatchers.named("queryCursor"));
  }

  @Override
  public Interceptor getInterceptor() {
    return new MybatisExecutorInterceptor();
  }

}
