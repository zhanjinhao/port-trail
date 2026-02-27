package cn.addenda.porttrail.agent.transform.interceptor.task;

import cn.addenda.porttrail.agent.transform.interceptor.Interceptor;
import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPoint;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

public class TaskExecuteInterceptorPoint implements InterceptorPoint {

  @Override
  public ElementMatcher<MethodDescription> getMethodsMatcher() {
    return ElementMatchers.named("call")
            .and(ElementMatchers.isPublic())
            .and(ElementMatchers.takesArguments(0))
            .and(ElementMatchers.isDeclaredBy(java.util.concurrent.Callable.class))
            .or(ElementMatchers.named("accept")
                    .and(ElementMatchers.isPublic())
                    .and(ElementMatchers.takesArguments(1)))
            .and(ElementMatchers.isDeclaredBy(java.util.function.Consumer.class))
            .or(ElementMatchers.named("apply")
                    .and(ElementMatchers.isPublic())
                    .and(ElementMatchers.takesArguments(1)))
            .and(ElementMatchers.isDeclaredBy(java.util.function.Function.class))
            .or(ElementMatchers.named("run")
                    .and(ElementMatchers.isPublic())
                    .and(ElementMatchers.takesArguments(0)))
            .and(ElementMatchers.isDeclaredBy(java.lang.Runnable.class))
            .or(ElementMatchers.named("get")
                    .and(ElementMatchers.isPublic())
                    .and(ElementMatchers.takesArguments(0))
                    .and(ElementMatchers.isDeclaredBy(java.util.function.Supplier.class))
            );
  }

  @Override
  public Interceptor getInterceptor() {
    return new TaskInterceptor();
  }

}
