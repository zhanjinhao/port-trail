package cn.addenda.porttrail.agent.transform.interceptor.tx.transactionhepler;

import cn.addenda.porttrail.agent.transform.interceptor.Interceptor;
import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPoint;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

public class SpringTransactionHelperDoTransactionInterceptorPoint implements InterceptorPoint {

  @Override
  public ElementMatcher<MethodDescription> getMethodsMatcher() {
    return ElementMatchers.named("get");
  }

  @Override
  public Interceptor getInterceptor() {
    return new SpringTransactionHelperInterceptor();
  }

}
