package cn.addenda.porttrail.agent.transform.interceptor.tx.transactiontemplate;

import cn.addenda.porttrail.agent.transform.interceptor.Interceptor;
import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPoint;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

public class SpringTransactionTemplateExecuteInterceptorPoint implements InterceptorPoint {

  private static final String EXECUTE = "execute";

  @Override
  public ElementMatcher<MethodDescription> getMethodsMatcher() {
    return ElementMatchers.nameMatches(EXECUTE);
  }

  @Override
  public Interceptor getInterceptor() {
    return new SpringTransactionTemplateInterceptor();
  }

}
