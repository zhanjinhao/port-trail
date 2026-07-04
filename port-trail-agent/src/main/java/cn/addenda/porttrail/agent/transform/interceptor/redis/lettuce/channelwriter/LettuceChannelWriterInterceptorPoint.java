package cn.addenda.porttrail.agent.transform.interceptor.redis.lettuce.channelwriter;

import cn.addenda.porttrail.agent.transform.interceptor.Interceptor;
import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPoint;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

/**
 * 匹配 RedisChannelWriter 实现类的 write(RedisCommand) 和 write(List) 方法。
 */
public class LettuceChannelWriterInterceptorPoint implements InterceptorPoint {

  @Override
  public ElementMatcher<MethodDescription> getMethodsMatcher() {
    return ElementMatchers.named("write")
            .and(
                    ElementMatchers.takesArgument(0, ElementMatchers.named("io.lettuce.core.protocol.RedisCommand"))
                            .or(ElementMatchers.takesArgument(0, java.util.List.class))
            );
  }

  @Override
  public Interceptor getInterceptor() {
    return new LettuceChannelWriterInterceptor();
  }

}
