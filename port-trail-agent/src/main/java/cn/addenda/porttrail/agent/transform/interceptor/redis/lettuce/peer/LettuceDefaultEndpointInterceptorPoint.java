package cn.addenda.porttrail.agent.transform.interceptor.redis.lettuce.peer;

import cn.addenda.porttrail.agent.transform.interceptor.Interceptor;
import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPoint;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

/**
 * 匹配 DefaultEndpoint.notifyChannelActive(Channel) 方法。
 */
public class LettuceDefaultEndpointInterceptorPoint implements InterceptorPoint {

  @Override
  public ElementMatcher<MethodDescription> getMethodsMatcher() {
    return ElementMatchers.named("notifyChannelActive")
            .and(ElementMatchers.takesArgument(0, ElementMatchers.named("io.netty.channel.Channel")));
  }

  @Override
  public Interceptor getInterceptor() {
    return new LettuceDefaultEndpointChannelActiveInterceptor();
  }

}
