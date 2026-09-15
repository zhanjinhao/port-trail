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
import io.lettuce.core.protocol.RedisCommand;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
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
    // write(List) 时 assembleWriteCommandString 会遍历整批命令，成本随规模增长，故先判断级别
    if (log.isDebugEnabled()) {
      log.debug("Intercepted [{}], command [{}].",
              Interceptor.assembleDetail(targetObj, targetMethod),
              assembleWriteCommandString(targetMethodArgs[0]));
    }
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
    context.setCommandName(LettuceRedisCommandUtils.extractCommandName(command));
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

  /**
   * write(RedisCommand) 返回命令名；write(List) 返回逗号分隔的命令名列表。
   * <p>
   * 本方法必须保证不抛异常：日志语句不得影响业务代码。
   */
  private static String assembleWriteCommandString(Object writeArg) {
    try {
      if (writeArg instanceof List) {
        StringBuilder sb = new StringBuilder();
        for (Object command : (List<?>) writeArg) {
          if (sb.length() > 0) {
            sb.append(',');
          }
          sb.append(resolveCommandName(command));
        }
        return sb.toString();
      }
      return resolveCommandName(writeArg);
    } catch (Exception e) {
      return "UNKNOWN";
    }
  }

  private static String resolveCommandName(Object command) {
    return LettuceRedisCommandUtils.extractCommandName(
            LettuceRedisCommandUtils.resolveCommand((RedisCommand<?, ?, ?>) command));
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
        appendSingularArgument(sb, singularArgument);
      }
      return sb.toString();
    } catch (Exception ignored) {
      return safeToCommandString(args);
    }
  }

  /**
   * 将单个 SingularArgument 追加到 StringBuilder 中。
   * 按类型优先级依次尝试提取可读的字节值：
   * 1. BytesArgument（排除 ProtocolKeywordArgument）
   * 2. ValueArgument（val 为 byte[] 直接取；否则通过 codec 编码）
   * 3. KeyArgument（通过 codec.encodeKey 编码为字节）
   * 4. 其他类型（StringArgument、IntegerArgument、DoubleArgument、CharArrayArgument 等）使用 toString()
   */
  private static void appendSingularArgument(StringBuilder sb, Object singularArgument) {
    // 1. BytesArgument（排除 ProtocolKeywordArgument）
    if (SingularArgumentsHolder.appendBytesArgument(singularArgument, sb)) {
      return;
    }

    // 2. ValueArgument
    if (SingularArgumentsHolder.appendValueArgument(singularArgument, sb)) {
      return;
    }

    // 3. KeyArgument
    if (SingularArgumentsHolder.appendKeyArgument(singularArgument, sb)) {
      return;
    }

    // 4. 其他类型：StringArgument、IntegerArgument、DoubleArgument、CharArrayArgument 等
    //    这些类型的 toString() 都是可读的
    try {
      sb.append(singularArgument);
    } catch (Exception e) {
      sb.append(singularArgument.getClass().getSimpleName());
    }
  }

  /**
   * 安全的 toCommandString 兜底，避免因 SingularArgument.toString() 异常导致整体失败。
   */
  private static String safeToCommandString(CommandArgs<?, ?> args) {
    try {
      return args.toCommandString();
    } catch (Exception e) {
      return "";
    }
  }

  /**
   * 通过反射访问 CommandArgs 的包私有内部类和私有字段。
   * SingularArgument 是包私有的，BytesArgument、ValueArgument、KeyArgument也是包私有的，无法直接从外部访问。
   * <p>
   * Lettuce 6.4.2 中 SingularArgument 的子类：
   * - BytesArgument (byte[] val)
   * - ProtocolKeywordArgument (extends BytesArgument)
   * - KeyArgument (K key, RedisCodec codec)
   * - ValueArgument (V val, RedisCodec codec)
   * - StringArgument (String val)
   * - CharArrayArgument (char[] val)
   * - IntegerArgument (long val)
   * - DoubleArgument (double val)
   */
  private static class SingularArgumentsHolder {
    public static final String SIMPLE_NAME_BytesArgument = "BytesArgument";
    public static final String SIMPLE_NAME_ValueArgument = "ValueArgument";
    public static final String SIMPLE_NAME_KeyArgument = "KeyArgument";

    private static volatile Field singularArgumentsField;

    // BytesArgument
    private static volatile Class<?> bytesArgumentClass;
    private static volatile Field bytesArgumentValField;

    // ValueArgument
    private static volatile Class<?> valueArgumentClass;
    private static volatile Field valueArgumentValField;
    private static volatile Field valueArgumentCodecField;

    // KeyArgument
    private static volatile Class<?> keyArgumentClass;
    private static volatile Field keyArgumentKeyField;
    private static volatile Field keyArgumentCodecField;

    // RedisCodec.encodeKey / encodeValue 方法
    private static volatile Method encodeKeyMethod;
    private static volatile Method encodeValueMethod;

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
            String simpleName = inner.getSimpleName();
            if (SIMPLE_NAME_BytesArgument.equals(simpleName)) {
              bytesArgumentClass = inner;
              bytesArgumentValField = inner.getDeclaredField("val");
              bytesArgumentValField.setAccessible(true);
            } else if (SIMPLE_NAME_ValueArgument.equals(simpleName)) {
              valueArgumentClass = inner;
              valueArgumentValField = inner.getDeclaredField("val");
              valueArgumentValField.setAccessible(true);
              valueArgumentCodecField = inner.getDeclaredField("codec");
              valueArgumentCodecField.setAccessible(true);
            } else if (SIMPLE_NAME_KeyArgument.equals(simpleName)) {
              keyArgumentClass = inner;
              keyArgumentKeyField = inner.getDeclaredField("key");
              keyArgumentKeyField.setAccessible(true);
              keyArgumentCodecField = inner.getDeclaredField("codec");
              keyArgumentCodecField.setAccessible(true);
            }
          }

          // 加载 RedisCodec 的 encodeKey/encodeValue 方法
          try {
            Class<?> codecClass = Class.forName("io.lettuce.core.codec.RedisCodec");
            encodeKeyMethod = codecClass.getMethod("encodeKey", Object.class);
            encodeValueMethod = codecClass.getMethod("encodeValue", Object.class);
          } catch (Exception ignored) {
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
    static boolean appendBytesArgument(Object singularArgument, StringBuilder sb) {
      ensureInitialized();
      String simpleName = singularArgument.getClass().getSimpleName();
      // BytesArgument 及其子类（排除 ProtocolKeywordArgument）
      if (bytesArgumentClass != null && bytesArgumentClass.isInstance(singularArgument)
              && !"ProtocolKeywordArgument".equals(simpleName)) {
        try {
          byte[] bytes = (byte[]) bytesArgumentValField.get(singularArgument);
          tryAppendBytes(sb, singularArgument, bytes);
          return true;
        } catch (IllegalAccessException e) {
          return false;
        }
      }
      return false;
    }

    private static final String VALUE_FORMAT = "value<%s>";

    /**
     * 提取 ValueArgument 中的字节值。
     * 1. val 为 byte[] 时直接返回
     * 2. val 为非 byte[] 时，通过 codec.encodeValue(val) 编码为 ByteBuffer 再转为 byte[]
     * 非 ValueArgument 类型返回 null。
     */
    static boolean appendValueArgument(Object singularArgument, StringBuilder sb) {
      ensureInitialized();
      if (valueArgumentClass != null && valueArgumentClass.isInstance(singularArgument)) {
        try {
          Object val = valueArgumentValField.get(singularArgument);
          if (val == null || val instanceof CharSequence) {
            sb.append(String.format(VALUE_FORMAT, val));
            return true;
          }
          if (val instanceof byte[]) {
            byte[] bytes = (byte[]) val;
            tryAppendBytes(sb, singularArgument, bytes, VALUE_FORMAT);
            return true;
          }
          // val 为非 byte[]，通过 codec 编码
          Object codec = valueArgumentCodecField.get(singularArgument);
          if (codec != null) {
            byte[] bytes = encodeViaCodec(codec, val, true);
            if (bytes != null) {
              tryAppendBytes(sb, singularArgument, bytes, VALUE_FORMAT);
              return true;
            }
          }
        } catch (Exception e) {
          return false;
        }
      }
      return false;
    }

    private static final String KEY_FORMAT = "key<%s>";

    /**
     * 提取 KeyArgument 中的字节值。
     * 通过 codec.encodeKey(key) 编码为 ByteBuffer，再转为 byte[]。
     * 非 KeyArgument 类型返回 null。
     */
    static boolean appendKeyArgument(Object singularArgument, StringBuilder sb) {
      ensureInitialized();
      if (keyArgumentClass != null && keyArgumentClass.isInstance(singularArgument)) {
        try {
          Object key = keyArgumentKeyField.get(singularArgument);
          if (key == null || key instanceof CharSequence) {
            sb.append(String.format(KEY_FORMAT, key));
            return true;
          }
          if (key instanceof byte[]) {
            byte[] bytes = (byte[]) key;
            tryAppendBytes(sb, singularArgument, bytes, KEY_FORMAT);
            return true;
          }
          // key 为非 byte[]，通过 codec 编码
          Object codec = keyArgumentCodecField.get(singularArgument);
          if (codec != null) {
            byte[] bytes = encodeViaCodec(codec, key, false);
            if (bytes != null) {
              tryAppendBytes(sb, singularArgument, bytes, KEY_FORMAT);
              return true;
            }
          }
        } catch (Exception e) {
          return false;
        }
      }
      return false;
    }

    /**
     * 通过 RedisCodec 将对象编码为字节数组。
     *
     * @param codec   RedisCodec 实例
     * @param value   待编码的对象
     * @param isValue true 调用 encodeValue，false 调用 encodeKey
     */
    private static byte[] encodeViaCodec(Object codec, Object value, boolean isValue) {
      if (codec == null || value == null) {
        return null;
      }
      try {
        Method method = isValue ? encodeValueMethod : encodeKeyMethod;
        if (method == null) {
          return null;
        }
        ByteBuffer buffer = (ByteBuffer) method.invoke(codec, value);
        if (buffer == null) {
          return null;
        }
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        return bytes;
      } catch (Exception e) {
        return null;
      }
    }

    private static void tryAppendBytes(StringBuilder sb, Object singularArgument, byte[] bytes) {
      try {
        sb.append(LettuceRedisCommandUtils.bytesToString(bytes));
      } catch (Exception e1) {
        try {
          sb.append(new String(bytes, java.nio.charset.StandardCharsets.UTF_8));
        } catch (Exception e2) {
          sb.append(singularArgument);
        }
      }
    }

    private static void tryAppendBytes(StringBuilder sb, Object singularArgument, byte[] bytes, String format) {
      try {
        sb.append(String.format(format, LettuceRedisCommandUtils.bytesToString(bytes)));
      } catch (Exception e1) {
        try {
          sb.append(String.format(format, new String(bytes, java.nio.charset.StandardCharsets.UTF_8)));
        } catch (Exception e2) {
          sb.append(singularArgument);
        }
      }
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
