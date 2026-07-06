package cn.addenda.porttrail.agent.transform.interceptor.redis.lettuce.channelwriter;

import cn.addenda.porttrail.agent.log.AgentPortTrailLoggerFactory;
import cn.addenda.porttrail.agent.transform.interceptor.AbstractDeduplicationEntryPointInterceptor;
import cn.addenda.porttrail.agent.transform.interceptor.Interceptor;
import cn.addenda.porttrail.agent.transform.interceptor.redis.lettuce.DefaultEndpointPeerHolder;
import cn.addenda.porttrail.agent.transform.interceptor.redis.lettuce.LettuceRedisCommandContext;
import cn.addenda.porttrail.agent.transform.interceptor.redis.lettuce.LettuceRedisCommandContextHolder;
import cn.addenda.porttrail.agent.transform.interceptor.redis.lettuce.LettuceRedisCommandUtils;
import cn.addenda.porttrail.common.entrypoint.EntryPoint;
import cn.addenda.porttrail.common.entrypoint.EntryPointType;
import cn.addenda.porttrail.infrastructure.entrypoint.EntryPointStackContext;
import cn.addenda.porttrail.infrastructure.log.PortTrailLogger;
import io.lettuce.core.protocol.CommandArgs;
import io.lettuce.core.protocol.ProtocolKeyword;
import io.lettuce.core.protocol.RedisCommand;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Field;
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
    command = LettuceRedisCommandUtils.resolveCommand(command);
    LettuceRedisCommandContext context = createContext(command);
    LettuceRedisCommandContextHolder.put(command, context);
  }

  private void setPeerAndPutContextIfAbsent(RedisCommand<?, ?, ?> command, String peer) {
    command = LettuceRedisCommandUtils.resolveCommand(command);
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
    CommandArgs<?, ?> args = command.getArgs();
    if (args == null) {
      return null;
    }
    try {
      List<?> singularArguments = SingularArgumentsHolder.getSingularArguments(args);
      if (singularArguments == null || singularArguments.isEmpty()) {
        return args.toCommandString();
      }
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < singularArguments.size(); i++) {
        if (i > 0) {
          sb.append(' ');
        }
        Object singularArgument = singularArguments.get(i);
        byte[] byteVal = SingularArgumentsHolder.getBytesArgumentByteValue(singularArgument);
        if (byteVal != null) {
          try {
            sb.append(LettuceRedisCommandUtils.bytesToString(byteVal));
          } catch (Exception e) {
            sb.append(singularArgument);
          }
        } else {
          byteVal = SingularArgumentsHolder.getValueArgumentByteValue(singularArgument);
          if (byteVal != null) {
            try {
              sb.append(String.format("value<%s>", LettuceRedisCommandUtils.bytesToString(byteVal)));
            } catch (Exception e) {
              sb.append(singularArgument);
            }
          } else {
            sb.append(singularArgument);
          }
        }
      }
      return sb.toString();
    } catch (Exception ignored) {
      return args.toCommandString();
    }
  }

  /**
   * 通过反射访问 CommandArgs 的包私有内部类和私有字段。
   * SingularArgument 是包私有的，BytesArgument 也是包私有的，无法直接从外部访问。
   */
  private static class SingularArgumentsHolder {
    public static final String SIMPLE_NAME_BytesArgument = "BytesArgument";
    public static final String SIMPLE_NAME_ValueArgument = "ValueArgument";
    private static volatile Field singularArgumentsField;
    private static volatile Class<?> bytesArgumentClass;
    private static volatile Field bytesArgumentValField;
    private static volatile Class<?> valueArgumentClass;
    private static volatile Field valueArgumentValField;
    private static volatile boolean initialized = false;

    private static void ensureInitialized() {
      if (initialized) {
        return;
      }
      synchronized (SingularArgumentsHolder.class) {
        if (initialized) {
          return;
        }
        try {
          singularArgumentsField = CommandArgs.class.getDeclaredField("singularArguments");
          singularArgumentsField.setAccessible(true);

          for (Class<?> inner : CommandArgs.class.getDeclaredClasses()) {
            if (SIMPLE_NAME_BytesArgument.equals(inner.getSimpleName())) {
              bytesArgumentClass = inner;
              bytesArgumentValField = inner.getDeclaredField("val");
              bytesArgumentValField.setAccessible(true);
            } else if (SIMPLE_NAME_ValueArgument.equals(inner.getSimpleName())) {
              valueArgumentClass = inner;
              valueArgumentValField = inner.getDeclaredField("val");
              valueArgumentValField.setAccessible(true);
            }
          }
        } catch (NoSuchFieldException e) {
          throw new RuntimeException("Failed to access CommandArgs internal fields", e);
        }
        initialized = true;
      }
    }

    @SuppressWarnings("unchecked")
    static List<?> getSingularArguments(CommandArgs<?, ?> args) {
      ensureInitialized();
      try {
        return (List<?>) singularArgumentsField.get(args);
      } catch (IllegalAccessException e) {
        return null;
      }
    }

    /**
     * 提取 BytesArgument 中的 byte[] 值。
     * 排除 ProtocolKeywordArgument（继承自 BytesArgument，但有更好的 toString()）。
     * 非 BytesArgument 类型返回 null。
     */
    static byte[] getBytesArgumentByteValue(Object arg) {
      ensureInitialized();
      String simpleName = arg.getClass().getSimpleName();
      // BytesArgument 及其子类（排除 ProtocolKeywordArgument）
      if (bytesArgumentClass != null && bytesArgumentClass.isInstance(arg)
              && !"ProtocolKeywordArgument".equals(simpleName)) {
        try {
          return (byte[]) bytesArgumentValField.get(arg);
        } catch (IllegalAccessException e) {
          return null;
        }
      }
      return null;
    }

    /**
     * 提取 ValueArgument 中的 byte[] 值（仅当 val 为 byte[] 类型时）。
     * val 为非 byte[] 类型或非 ValueArgument 时返回 null。
     */
    static byte[] getValueArgumentByteValue(Object arg) {
      ensureInitialized();
      // ValueArgument，仅当 val 为 byte[] 时
      if (valueArgumentClass != null && valueArgumentClass.isInstance(arg)) {
        try {
          Object val = valueArgumentValField.get(arg);
          if (val instanceof byte[]) {
            return (byte[]) val;
          }
        } catch (IllegalAccessException e) {
          return null;
        }
      }
      return null;
    }
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
