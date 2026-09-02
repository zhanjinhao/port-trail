package cn.addenda.porttrail.agent.transform.interceptor.redis.lettuce.command;

import cn.addenda.porttrail.agent.log.AgentPortTrailLoggerFactory;
import cn.addenda.porttrail.agent.transform.interceptor.Interceptor;
import cn.addenda.porttrail.agent.transform.interceptor.redis.lettuce.LettuceRedisCommandContext;
import cn.addenda.porttrail.agent.transform.interceptor.redis.lettuce.LettuceRedisCommandContextHolder;
import cn.addenda.porttrail.agent.transform.interceptor.redis.lettuce.LettuceRedisCommandUtils;
import cn.addenda.porttrail.agent.writer.redis.AgentRedisWriter;
import cn.addenda.porttrail.common.pojo.redis.bo.RedisBo;
import cn.addenda.porttrail.common.pojo.redis.bo.RedisExecution;
import cn.addenda.porttrail.infrastructure.log.PortTrailLogger;
import io.lettuce.core.*;
import io.lettuce.core.models.stream.ClaimedMessages;
import io.lettuce.core.output.CommandOutput;
import io.lettuce.core.protocol.RedisCommand;
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
    String tmp = safeToString(obj);
    if (tmp != null) {
      return tmp;
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
    if (obj instanceof KeyValue) {
      return extractKeyValueResult((KeyValue<?, ?>) obj);
    }
    if (obj instanceof ScoredValue) {
      return extractKeyValueResult((ScoredValue<?>) obj);
    }
    if (obj instanceof GeoValue) {
      return extractGeoValueResult((GeoValue<?>) obj);
    }
    if (obj instanceof Value) {
      return extractValueResult((Value<?>) obj);
    }
    if (obj instanceof GeoWithin) {
      return extractGeoWithinResult((GeoWithin<?>) obj);
    }
    if (obj instanceof TransactionResult) {
      return extractTransactionResult((TransactionResult) obj);
    }
    if (obj instanceof StreamMessage) {
      return extractStreamMessageResult((StreamMessage<?, ?>) obj);
    }
    // ClaudeCode+DSV4Flash说明：这里不（也无法）单独拦截 ClusterKeyScanCursor / ClusterStreamScanCursor。
    // 集群 SCAN 返回的 Cluster*ScanCursor 是 Lettuce 集群层在节点级 RedisCommand 完成之后，
    // 由 resultMapper（RedisAdvancedClusterAsyncCommandsImpl#clusterScan）用
    // new ClusterKeyScanCursor(nodeIds, currentNodeId, 节点结果) 包装出来的"调用侧"对象，
    // 它并不存在于任何 RedisCommand 的 output 中，因此挂在 RedisCommand.complete() 上的本拦截器永远看不到它。
    // 本拦截器实际拿到的是逐节点的普通 KeyScanCursor / StreamScanCursor，由下面两个 instanceof 分支覆盖。
    if (obj instanceof KeyScanCursor) {
      return extractKeyScanCursorResult((KeyScanCursor<?>) obj);
    }
    if (obj instanceof ScoredValueScanCursor) {
      return extractScoredValueScanCursorResult((ScoredValueScanCursor<?>) obj);
    }
    if (obj instanceof MapScanCursor) {
      return extractMapScanCursorResult((MapScanCursor<?, ?>) obj);
    }
    if (obj instanceof ValueScanCursor) {
      return extractValueScanCursorResult((ValueScanCursor<?>) obj);
    }
    if (obj instanceof StreamScanCursor) {
      return extractStreamScanCursorResult((StreamScanCursor) obj);
    }
    if (obj instanceof StringMatchResult) {
      return extractStringMatchResult((StringMatchResult) obj);
    }
    if (obj instanceof ClaimedMessages) {
      return extractClaimedMessagesResult((ClaimedMessages<?, ?>) obj);
    }
    // ClaudeCode+DSV4Flash说明：这里不（也无法）处理 ReplayOutput.Signal。
    // Lettuce 6.4.2 中 ReplayOutput 仅被 PubSubCommandHandler 用作 pub/sub 响应解码期的内部缓冲
    // （ResponseHeaderReplayOutput，信号经 replay(command.getOutput()) 回放到命令自己的 output），
    // 它永远不会成为任何 RedisCommand 的 output，因此本拦截器拿到的 output.get() 里不会出现 ReplayOutput.Signal。
    return obj.toString();
  }

  private static String extractValueResult(Value<?> get) {
    return get.hasValue() ?
            String.format("Value[%s]", safeToString(get.getValue()))
            : "Value.empty";
  }

  private static String extractGeoWithinResult(GeoWithin<?> get) {
    return String.format("GeoWithin[member=%s, distance=%s, geohash=%s, coordinates=%s]",
            safeToString(get.getMember()),
            get.getDistance(),
            get.getGeohash(),
            get.getCoordinates() != null ? extractGeoCoordinatesResult(get.getCoordinates()) : "null");
  }

  private static String extractTransactionResult(TransactionResult result) {
    StringBuilder sb = new StringBuilder("TransactionResult[wasDiscarded=")
            .append(result.wasDiscarded())
            .append(", responses=[");
    boolean first = true;
    for (Object response : result) {
      if (!first) {
        sb.append(",");
      }
      sb.append(deepConvertToString(response));
      first = false;
    }
    sb.append("]]");
    return sb.toString();
  }

  private static String extractStreamMessageResult(StreamMessage<?, ?> msg) {
    String bodyStr = msg.getBody() != null ?
            deepConvertToString(msg.getBody())
            : "null";
    return String.format("StreamMessage[%s:%s]%s",
            safeToString(msg.getStream()),
            msg.getId(),
            bodyStr);
  }

  private static String extractKeyScanCursorResult(KeyScanCursor<?> cursor) {
    String keysStr = cursor.getKeys().stream()
            .map(LettuceCommandCompleteInterceptor::deepConvertToString)
            .collect(Collectors.joining(",", "[", "]"));
    return String.format("KeyScanCursor[cursor=%s, finished=%b, keys=%s]",
            cursor.getCursor(), cursor.isFinished(), keysStr);
  }

  private static String extractScoredValueScanCursorResult(ScoredValueScanCursor<?> cursor) {
    String valuesStr = cursor.getValues().stream()
            .map(LettuceCommandCompleteInterceptor::deepConvertToString)
            .collect(Collectors.joining(",", "[", "]"));
    return String.format("ScoredValueScanCursor[cursor=%s, finished=%b, values=%s]",
            cursor.getCursor(), cursor.isFinished(), valuesStr);
  }

  private static String extractMapScanCursorResult(MapScanCursor<?, ?> cursor) {
    String mapStr = ((Map<?, ?>) cursor.getMap()).entrySet().stream()
            .map(entry -> deepConvertToString(entry.getKey()) + ":" + deepConvertToString(entry.getValue()))
            .collect(Collectors.joining(",", "{", "}"));
    return String.format("MapScanCursor[cursor=%s, finished=%b, map=%s]",
            cursor.getCursor(), cursor.isFinished(), mapStr);
  }

  private static String extractValueScanCursorResult(ValueScanCursor<?> cursor) {
    String valuesStr = cursor.getValues().stream()
            .map(LettuceCommandCompleteInterceptor::deepConvertToString)
            .collect(Collectors.joining(",", "[", "]"));
    return String.format("ValueScanCursor[cursor=%s, finished=%b, values=%s]",
            cursor.getCursor(), cursor.isFinished(), valuesStr);
  }

  private static String extractStreamScanCursorResult(StreamScanCursor cursor) {
    return String.format("StreamScanCursor[cursor=%s, finished=%b, count=%d]",
            cursor.getCursor(), cursor.isFinished(), cursor.getCount());
  }

  private static String extractStringMatchResult(StringMatchResult result) {
    String matchesStr = result.getMatches().stream()
            .map(mp -> String.format("MatchedPosition[a=(%d,%d), b=(%d,%d), matchLen=%d]",
                    mp.getA().getStart(), mp.getA().getEnd(),
                    mp.getB().getStart(), mp.getB().getEnd(),
                    mp.getMatchLen()))
            .collect(Collectors.joining(",", "[", "]"));
    return String.format("StringMatchResult[matchString=%s, len=%d, matches=%s]",
            result.getMatchString(), result.getLen(), matchesStr);
  }

  private static String extractClaimedMessagesResult(ClaimedMessages<?, ?> claimedMessages) {
    String messagesStr = Optional.ofNullable(claimedMessages.getMessages())
            .map(msgs -> msgs.stream()
                    .map(LettuceCommandCompleteInterceptor::extractStreamMessageResult)
                    .collect(Collectors.joining(",", "[", "]")))
            .orElse("null");
    return String.format("ClaimedMessages[id=%s, messages=%s]", claimedMessages.getId(), messagesStr);
  }

  private static String extractGeoCoordinatesResult(GeoCoordinates obj) {
    return String.format("(%s, %s)", obj.getX(), obj.getY());
  }

  private static String extractGeoValueResult(GeoValue<?> get) {
    return get.hasValue() ?
            String.format("GeoValue[%s, %s]", extractGeoCoordinatesResult(get.getCoordinates()), safeToString(get.getValue()))
            : String.format("GeoValue[%s].empty", extractGeoCoordinatesResult(get.getCoordinates()));
  }

  private static String extractKeyValueResult(ScoredValue<?> get) {
    return get.hasValue() ?
            String.format("ScoredValue[%f, %s]", get.getScore(), safeToString(get.getValue()))
            : String.format("ScoredValue[%f].empty", get.getScore());
  }

  private static String extractKeyValueResult(KeyValue<?, ?> get) {
    return get.hasValue() ?
            String.format("KeyValue[%s, %s]", safeToString(get.getKey()), safeToString(get.getValue()))
            : String.format("KeyValue[%s].empty", get.getKey());
  }

  private static String safeToString(Object obj) {
    if (obj == null) {
      return "null";
    }
    if (obj instanceof CharSequence) {
      return obj.toString();
    }
    if (obj instanceof byte[]) {
      byte[] bytes = (byte[]) obj;
      try {
        return LettuceRedisCommandUtils.bytesToString(bytes);
      } catch (Exception e) {
        return obj.toString();
      }
    }
    return null;
  }

  @Override
  public boolean ifOverride() {
    return false;
  }

}
