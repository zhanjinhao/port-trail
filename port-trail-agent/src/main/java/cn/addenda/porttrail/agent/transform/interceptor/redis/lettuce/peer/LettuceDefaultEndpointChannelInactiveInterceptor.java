package cn.addenda.porttrail.agent.transform.interceptor.redis.lettuce.peer;

import cn.addenda.porttrail.agent.log.AgentPortTrailLoggerFactory;
import cn.addenda.porttrail.agent.transform.interceptor.Interceptor;
import cn.addenda.porttrail.agent.transform.interceptor.redis.lettuce.DefaultEndpointPeerHolder;
import cn.addenda.porttrail.infrastructure.log.PortTrailLogger;
import io.lettuce.core.protocol.DefaultEndpoint;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * 拦截 DefaultEndpoint.notifyChannelInactive(Channel)。
 * 仅在连接永久关闭（isClosed()==true）时清理 peer 映射，避免内存泄漏。
 * 临时断连（自动重连场景）不清理，保证 disconnectedBuffer 中的命令仍能关联到正确 peer。
 */
public class LettuceDefaultEndpointChannelInactiveInterceptor implements Interceptor {

  private static final PortTrailLogger log =
          AgentPortTrailLoggerFactory.getInstance().getPortTrailLogger(LettuceDefaultEndpointChannelInactiveInterceptor.class);

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
      if (targetObj instanceof DefaultEndpoint && ((DefaultEndpoint) targetObj).isClosed()) {
        String removed = DefaultEndpointPeerHolder.remove(targetObj);
        log.info("Lettuce DefaultEndpoint unregistered (closed), peer=[{}].", removed);
      }
    } catch (Exception e) {
      log.error("Failed to handle DefaultEndpoint channel inactive.", e);
    }

    return result;
  }

  @Override
  public boolean ifOverride() {
    return false;
  }

}