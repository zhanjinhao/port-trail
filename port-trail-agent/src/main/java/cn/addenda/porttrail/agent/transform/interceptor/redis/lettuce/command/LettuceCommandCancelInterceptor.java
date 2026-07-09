package cn.addenda.porttrail.agent.transform.interceptor.redis.lettuce.command;

import cn.addenda.porttrail.agent.transform.interceptor.Interceptor;
import cn.addenda.porttrail.agent.log.AgentPortTrailLoggerFactory;
import cn.addenda.porttrail.agent.transform.interceptor.redis.lettuce.LettuceRedisCommandContext;
import cn.addenda.porttrail.agent.transform.interceptor.redis.lettuce.LettuceRedisCommandContextHolder;
import cn.addenda.porttrail.agent.transform.interceptor.redis.lettuce.LettuceRedisCommandUtils;
import cn.addenda.porttrail.agent.writer.redis.AgentRedisWriter;
import cn.addenda.porttrail.common.pojo.redis.bo.RedisBo;
import cn.addenda.porttrail.common.pojo.redis.bo.RedisExecution;
import cn.addenda.porttrail.infrastructure.log.PortTrailLogger;
import io.lettuce.core.protocol.RedisCommand;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * 拦截 RedisCommand 子类的 cancel() 方法。
 * 在 Netty IO 线程记录取消信息。
 */
public class LettuceCommandCancelInterceptor implements Interceptor {

  private static final AgentRedisWriter agentRedisWriter = AgentRedisWriter.getInstance();

  private static final PortTrailLogger log =
          AgentPortTrailLoggerFactory.getInstance().getPortTrailLogger(LettuceCommandCancelInterceptor.class);

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
      LettuceRedisCommandContext context = LettuceRedisCommandContextHolder.remove(LettuceRedisCommandUtils.resolveCommand((RedisCommand<?, ?, ?>) targetObj));
      if (context != null) {
        long startTime = context.getStartTime();
        long endTime = System.currentTimeMillis();
        int cost = (int) (endTime - startTime);

        RedisBo redisBo = new RedisBo(RedisExecution.RESULT_TYPE_CANCEL, context.getCommandName());
        redisBo.setCommandArgString(context.getCommandArgString());
        redisBo.setPeer(context.getPeer());
        redisBo.setResult(null);
        redisBo.setError(null);
        redisBo.setStartTime(startTime);
        redisBo.setEndTime(endTime);
        redisBo.setCost(cost);
        redisBo.setEntryPointSnapshot(context.getEntryPointSnapshot());
        agentRedisWriter.writeRedisExecution(redisBo);
      }
    } catch (Exception e) {
      log.error("Failed to process Redis command cancel callback", e);
    }

    return result;
  }

  @Override
  public boolean ifOverride() {
    return false;
  }

}
