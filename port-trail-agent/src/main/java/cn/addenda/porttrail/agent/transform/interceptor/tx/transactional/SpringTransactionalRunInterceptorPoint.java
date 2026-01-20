package cn.addenda.porttrail.agent.transform.interceptor.tx.transactional;

import cn.addenda.porttrail.agent.transform.interceptor.Interceptor;
import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPoint;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

public class SpringTransactionalRunInterceptorPoint implements InterceptorPoint {

  private static final String TRANSACTIONAL_NAME = "org.springframework.transaction.annotation.Transactional";

  @Override
  public ElementMatcher<MethodDescription> getMethodsMatcher() {
    return ElementMatchers.not(ElementMatchers.isStatic())
            .and(ElementMatchers.isAnnotatedWith(
                            ElementMatchers.named(TRANSACTIONAL_NAME)
                    )
            );
  }

  @Override
  public Interceptor getInterceptor() {
    return new SpringTransactionalInterceptor();
  }

}
