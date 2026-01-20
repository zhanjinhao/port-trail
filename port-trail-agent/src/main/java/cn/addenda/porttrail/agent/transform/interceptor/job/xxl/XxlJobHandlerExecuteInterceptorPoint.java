package cn.addenda.porttrail.agent.transform.interceptor.job.xxl;

import cn.addenda.porttrail.agent.transform.interceptor.Interceptor;
import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPoint;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import static cn.addenda.porttrail.agent.transform.interceptor.job.xxl.XxlJobHandlerInterceptorPointDefine.*;

public class XxlJobHandlerExecuteInterceptorPoint implements InterceptorPoint {

  private static final String TRIGGER_PARAM_NAME = "com.xxl.job.core.biz.model.TriggerParam";

  @Override
  public ElementMatcher<MethodDescription> getMethodsMatcher() {
    return ElementMatchers.named("run")
            .and(ElementMatchers.isPublic())
            .and(ElementMatchers.takesArguments(1))
            .and(ElementMatchers.takesArgument(1, ElementMatchers.named(TRIGGER_PARAM_NAME)))
            .and(ElementMatchers.isDeclaredBy(ElementMatchers.named(EXECUTOR_BIZ_IMPL_NAME)))
            .or(ElementMatchers.named("execute")
                    .and(ElementMatchers.isPublic())
                    .and(ElementMatchers.takesArguments(1))
                    .and(ElementMatchers.takesArgument(1, String.class))
                    .and(ElementMatchers.isDeclaredBy(ElementMatchers.named(GLUE_JOB_HANDLER_NAME))))
            .or(ElementMatchers.named("execute")
                    .and(ElementMatchers.isPublic())
                    .and(ElementMatchers.takesArguments(1))
                    .and(ElementMatchers.takesArgument(1, String.class))
                    .and(ElementMatchers.isDeclaredBy(ElementMatchers.named(SCRIPT_JOB_HANDLER_NAME))));
  }

  @Override
  public Interceptor getInterceptor() {
    return new XxlJobHandlerInterceptor();
  }

}
