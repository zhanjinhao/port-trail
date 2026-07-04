package cn.addenda.porttrail.agent.transform.interceptor.redis.lettuce.channelwriter;

import cn.addenda.porttrail.agent.log.AgentPortTrailLoggerFactory;
import cn.addenda.porttrail.agent.transform.interceptor.AbstractDeduplicationEntryPointInterceptor;
import cn.addenda.porttrail.agent.transform.interceptor.Interceptor;
import cn.addenda.porttrail.agent.transform.interceptor.redis.lettuce.DefaultEndpointPeerHolder;
import cn.addenda.porttrail.agent.transform.interceptor.redis.lettuce.LettuceRedisCommandContext;
import cn.addenda.porttrail.agent.transform.interceptor.redis.lettuce.LettuceRedisCommandContextHolder;
import cn.addenda.porttrail.agent.transform.interceptor.redis.lettuce.RedisCommandUtils;
import cn.addenda.porttrail.common.entrypoint.EntryPoint;
import cn.addenda.porttrail.common.entrypoint.EntryPointType;
import cn.addenda.porttrail.infrastructure.entrypoint.EntryPointStackContext;
import cn.addenda.porttrail.infrastructure.log.PortTrailLogger;
import io.lettuce.core.protocol.CommandArgs;
import io.lettuce.core.protocol.ProtocolKeyword;
import io.lettuce.core.protocol.RedisCommand;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * 拦截 RedisChannelWriter 实现类的 write(RedisCommand) / write(List) 方法。
 * 在业务线程记录开始时间、入口调用栈快照，push EntryPoint。
 */
public class LettuceChannelWriterInterceptor
        extends AbstractDeduplicationEntryPointInterceptor
        implements Interceptor {

  private static final PortTrailLogger log =
          AgentPortTrailLoggerFactory.getInstance().getPortTrailLogger(LettuceChannelWriterInterceptor.class);

  @RuntimeType
  public Object intercept(
          @This Object targetObj,
          @Origin Method targetMethod,
          @AllArguments Object[] targetMethodArgs,
          @Super Object originalObj,
          @SuperCall Callable<?> zuper
  ) throws Exception {
    log.info("TargetObj is [{}] and it's classloader is [{}].", targetObj, targetObj.getClass().getClassLoader());
    String peer = DefaultEndpointPeerHolder.get(targetObj);

    // 从ClusterWriter或SentinelWriter中调用write()，是没有peer的。
    if (peer == null) {
      return callWithEntryPoint(
              assembleDetail(targetObj, targetMethod),
              () -> {
                Object arg0 = targetMethodArgs[0];
                if (arg0 instanceof List) {
                  for (Object cmdObj : (List<?>) arg0) {
                    createAndPutContext((RedisCommand<?, ?, ?>) cmdObj);
                  }
                } else {
                  createAndPutContext((RedisCommand<?, ?, ?>) arg0);
                }
                return zuper.call();
              });
    }
    // 从DefaultEndpoint中调用write()，是有peer的。
    else {
      Object arg0 = targetMethodArgs[0];
      if (arg0 instanceof List) {
        for (Object cmdObj : (List<?>) arg0) {
          setPeerAndPutContextIfAbsent((RedisCommand<?, ?, ?>) cmdObj, peer);
        }
      } else {
        setPeerAndPutContextIfAbsent((RedisCommand<?, ?, ?>) arg0, peer);
      }
      return zuper.call();
    }
  }

  private LettuceRedisCommandContext createContext(RedisCommand<?, ?, ?> command) {
    long startTime = System.currentTimeMillis();

    LettuceRedisCommandContext context = new LettuceRedisCommandContext();
    context.setPeer(null);
    context.setCommandName(extractCommandName(command));
    context.setCommandArgString(extractCommandArgString(command));
    context.setStartTime(startTime);
    context.setEntryPointSnapshot(EntryPointStackContext.snapshot());

    return context;
  }

  private void createAndPutContext(RedisCommand<?, ?, ?> command) {
    command = RedisCommandUtils.resolveCommand(command);
    LettuceRedisCommandContext context = createContext(command);
    LettuceRedisCommandContextHolder.put(command, context);
  }

  private void setPeerAndPutContextIfAbsent(RedisCommand<?, ?, ?> command, String peer) {
    command = RedisCommandUtils.resolveCommand(command);
    LettuceRedisCommandContext context = LettuceRedisCommandContextHolder.get(command);
    if (context == null) {
      context = createContext(command);
      LettuceRedisCommandContextHolder.put(command, context);
    }
    context.setPeer(peer);
  }

  static String extractCommandName(RedisCommand<?, ?, ?> command) {
    ProtocolKeyword type = command.getType();
    try {
      return type.name();
    } catch (Exception ignored) {
      return type.toString();
    }
  }

  static String extractCommandArgString(RedisCommand<?, ?, ?> command) {
    try {
      CommandArgs<?, ?> args = command.getArgs();
      if (args != null) {
        return args.toCommandString();
      }
    } catch (Exception ignored) {
    }
    return null;
  }

  @Override
  public boolean ifOverride() {
    return false;
  }

  @Override
  protected EntryPoint entryPoint(String detail) {
    return EntryPoint.of(EntryPointType.REMOTE_REDIS, detail);
  }

  private static final ThreadLocal<Deque<String>> deduplicationStack = new ThreadLocal<Deque<String>>() {
    @Override
    public String toString() {
      return getClass().getName() + "@" + LettuceChannelWriterInterceptor.class.getName() + "@" + Integer.toHexString(hashCode());
    }
  };

  @Override
  protected ThreadLocal<Deque<String>> getDeduplicationStack() {
    return deduplicationStack;
  }

}
