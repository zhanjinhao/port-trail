package cn.addenda.porttrail.agent.transform.interceptor.redis.lettuce.peer;

import cn.addenda.porttrail.agent.log.AgentPortTrailLoggerFactory;
import cn.addenda.porttrail.agent.transform.interceptor.Interceptor;
import cn.addenda.porttrail.agent.transform.interceptor.redis.lettuce.DefaultEndpointPeerHolder;
import cn.addenda.porttrail.infrastructure.log.PortTrailLogger;
import io.netty.channel.Channel;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.concurrent.Callable;

/**
 * 拦截 DefaultEndpoint.notifyChannelActive(Channel)，提取 Redis 连接地址。
 */
public class LettuceDefaultEndpointChannelActiveInterceptor implements Interceptor {

  private static final PortTrailLogger log =
          AgentPortTrailLoggerFactory.getInstance().getPortTrailLogger(LettuceDefaultEndpointChannelActiveInterceptor.class);

  @RuntimeType
  public Object intercept(
          @This Object targetObj,
          @Origin Method targetMethod,
          @AllArguments Object[] targetMethodArgs,
          @Super Object originalObj,
          @SuperCall Callable<?> zuper
  ) throws Exception {

    log.info("TargetObj is [{}] and it's classloader is [{}].", targetObj, targetObj.getClass().getClassLoader());

    Object result = zuper.call();

    try {
      if (targetMethodArgs.length > 0 && targetMethodArgs[0] instanceof Channel) {
        Channel channel = (Channel) targetMethodArgs[0];
        if (channel.remoteAddress() instanceof InetSocketAddress) {
          InetSocketAddress inetAddr = (InetSocketAddress) channel.remoteAddress();
          String peer = inetAddr.getHostString() + ":" + inetAddr.getPort();
          DefaultEndpointPeerHolder.put(targetObj, peer);
          log.info("Lettuce DefaultEndpoint registered, peer=[{}].", peer);
        }
      }
    } catch (Exception e) {
      log.error("Failed to extract peer from DefaultEndpoint.notifyChannelActive.", e);
    }

    return result;
  }

  @Override
  public boolean ifOverride() {
    return false;
  }

}