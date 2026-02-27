package cn.addenda.porttrail.agent.transform.interceptor.job.xxl;

import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPoint;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

public abstract class AbstractJobHandlerExecuteInterceptorPoint implements InterceptorPoint {

  @Override
  public ElementMatcher<MethodDescription> getMethodsMatcher() {
    return ElementMatchers.named("execute")
            .and(ElementMatchers.isPublic())
            .and(
                    ElementMatchers.takesArguments(1)
                            .and(ElementMatchers.takesArgument(0, String.class))
                            .or(ElementMatchers.takesArguments(0))
            );
  }

}
