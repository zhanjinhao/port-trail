package cn.addenda.porttrail.agent.transform.interceptor.redis.lettuce.command;

import cn.addenda.porttrail.agent.link.LinkFacade;
import cn.addenda.porttrail.agent.log.AgentPortTrailLoggerFactory;
import cn.addenda.porttrail.agent.transform.interceptor.Interceptor;
import cn.addenda.porttrail.agent.transform.interceptor.redis.lettuce.LettuceRedisCommandContext;
import cn.addenda.porttrail.agent.transform.interceptor.redis.lettuce.LettuceRedisCommandContextHolder;
import cn.addenda.porttrail.agent.transform.interceptor.redis.lettuce.LettuceRedisCommandUtils;
import cn.addenda.porttrail.agent.writer.redis.AgentRedisWriter;
import cn.addenda.porttrail.common.pojo.redis.bo.RedisBo;
import cn.addenda.porttrail.common.pojo.redis.bo.RedisExecution;
import cn.addenda.porttrail.infrastructure.log.PortTrailLogger;
import io.lettuce.core.StreamMessage;
import io.lettuce.core.ValueScanCursor;
import io.lettuce.core.models.stream.ClaimedMessages;
import io.lettuce.core.output.CommandOutput;
import io.lettuce.core.protocol.RedisCommand;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * 拦截 RedisCommand 子类的 complete() 方法。
 * 在 Netty IO 线程记录结束时间、打印日志。
 */
public class LettuceCommandCompleteInterceptor implements Interceptor {

  private static final AgentRedisWriter agentRedisWriter = AgentRedisWriter.getInstance();

  private static final PortTrailLogger log =
          AgentPortTrailLoggerFactory.getInstance().getPortTrailLogger(LettuceCommandCompleteInterceptor.class);

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
        String commandResult = extractResult(targetObj);

        long startTime = context.getStartTime();
        long endTime = System.currentTimeMillis();
        int cost = (int) (endTime - startTime);

        RedisBo redisBo = new RedisBo(RedisExecution.RESULT_TYPE_SUCCESS, context.getCommandName());
        redisBo.setCommandArgString(context.getCommandArgString());
        redisBo.setPeer(context.getPeer());
        redisBo.setResult(commandResult);
        redisBo.setError(null);
        redisBo.setStartTime(startTime);
        redisBo.setEndTime(endTime);
        redisBo.setCost(cost);
        redisBo.setEntryPointSnapshot(context.getEntryPointSnapshot());
        agentRedisWriter.writeRedisExecution(redisBo);
      }
    } catch (Exception e) {
      log.error("Failed to process Redis command complete callback", e);
    }

    return result;
  }

  static String extractResult(Object targetObj) {
    try {
      RedisCommand<?, ?, ?> command = (RedisCommand<?, ?, ?>) targetObj;
      CommandOutput<?, ?, ?> output = command.getOutput();
      Object get = output != null ? output.get() : null;
      if (get == null) {
        return null;
      }
      if (get instanceof ValueScanCursor) {
        ValueScanCursor<?> valueScanCursor = (ValueScanCursor<?>) get;
        ValueScanCursorResult valueScanCursorResult = ValueScanCursorResult.of(valueScanCursor.getCursor(), valueScanCursor.getValues(), valueScanCursor.isFinished());
        return LinkFacade.toStr(valueScanCursorResult);
      }
      if (get instanceof ClaimedMessages) {
        ClaimedMessages<?, ?> claimedMessages = (ClaimedMessages<?, ?>) get;
        return Optional.ofNullable(claimedMessages.getMessages())
                .map(a -> a.stream().map(StreamMessage::toString)
                        .collect(Collectors.joining(",", "[", "]")))
                .orElse(null);
      }
      return deepConvertToString(get);
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * 递归将对象转换为可读字符串，正确处理 byte[]、Collection、Map 中的 byte[] 元素。
   */
  @SuppressWarnings("unchecked")
  private static String deepConvertToString(Object obj) {
    if (obj == null) {
      return "null";
    }
    if (obj instanceof byte[]) {
      byte[] bytes = (byte[]) obj;
      try {
        return LettuceRedisCommandUtils.bytesToString(bytes);
      } catch (Exception e) {
        return obj.toString();
      }
    }
    if (obj instanceof Collection) {
      return ((Collection<?>) obj).stream()
              .map(LettuceCommandCompleteInterceptor::deepConvertToString)
              .collect(Collectors.joining(",", "[", "]"));
    }
    if (obj instanceof Map) {
      return ((Map<?, ?>) obj).entrySet().stream()
              .map(entry -> deepConvertToString(entry.getKey()) + ":" + deepConvertToString(entry.getValue()))
              .collect(Collectors.joining(",", "{", "}"));
    }
    return obj.toString();
  }

  @Setter
  @Getter
  @ToString
  private static class ValueScanCursorResult {
    private String cursor;
    private List<Object> valueList = new ArrayList<>();
    private boolean finished;

    public static ValueScanCursorResult of(String cursor, List<?> valueList, boolean finished) {
      ValueScanCursorResult result = new ValueScanCursorResult();
      result.cursor = cursor;
      if (valueList != null) {
        result.valueList.addAll(valueList);
      }
      result.finished = finished;
      return result;
    }
  }

  @Override
  public boolean ifOverride() {
    return false;
  }

}
