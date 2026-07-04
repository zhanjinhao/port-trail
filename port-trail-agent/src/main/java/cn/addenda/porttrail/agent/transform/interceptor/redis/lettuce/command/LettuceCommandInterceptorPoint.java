package cn.addenda.porttrail.agent.transform.interceptor.redis.lettuce.command;

import cn.addenda.porttrail.agent.transform.interceptor.Interceptor;
import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPoint;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

/**
 * 定义 RedisCommand 子类的三个生命周期方法的拦截点。
 * 包含三个内部 InterceptorPoint 实现类，分别对应 complete()、completeExceptionally()、cancel()。
 */
public class LettuceCommandInterceptorPoint {

  /**
   * 匹配 RedisCommand.complete(T) 方法
   */
  public static class CompleteInterceptorPoint implements InterceptorPoint {

    @Override
    public ElementMatcher<MethodDescription> getMethodsMatcher() {
      return ElementMatchers.named("complete")
              .and(ElementMatchers.takesArguments(1));
    }

    @Override
    public Interceptor getInterceptor() {
      return new LettuceCommandCompleteInterceptor();
    }

  }

  /**
   * 匹配 RedisCommand.completeExceptionally(Throwable) 方法
   */
  public static class CompleteExceptionallyInterceptorPoint implements InterceptorPoint {

    @Override
    public ElementMatcher<MethodDescription> getMethodsMatcher() {
      return ElementMatchers.named("completeExceptionally")
              .and(ElementMatchers.takesArguments(1));
    }

    @Override
    public Interceptor getInterceptor() {
      return new LettuceCommandCompleteExceptionallyInterceptor();
    }

  }

  /**
   * 匹配 RedisCommand.cancel() 方法
   */
  public static class CancelInterceptorPoint implements InterceptorPoint {

    @Override
    public ElementMatcher<MethodDescription> getMethodsMatcher() {
      return ElementMatchers.named("cancel")
              .and(ElementMatchers.takesArguments(0));
    }

    @Override
    public Interceptor getInterceptor() {
      return new LettuceCommandCancelInterceptor();
    }

  }

}
