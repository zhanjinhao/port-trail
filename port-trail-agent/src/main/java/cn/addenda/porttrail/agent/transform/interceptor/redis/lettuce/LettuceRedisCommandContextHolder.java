package cn.addenda.porttrail.agent.transform.interceptor.redis.lettuce;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 持有 RedisCommand 实例与 RedisCommandContext 的映射关系。
 *
 * <p>
 * 采用二级 Map 结构避免 identityHashCode 冲突：
 * 外层 key = identityHashCode（分桶），内层 key = 原子序列号（唯一标识）。
 * get/remove 时通过 command 引用在内层 Map 中线性匹配。
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LettuceRedisCommandContextHolder {

  // todo 增加泄露检测
  private static final Map<Integer, Map<Long, CommandContextEntry>> OUTER_MAP = new HashMap<>();

  private static final AtomicLong SEQ = new AtomicLong(0);

  public static synchronized void put(Object command, LettuceRedisCommandContext context) {
    int idHash = System.identityHashCode(command);
    long seq = SEQ.incrementAndGet();
    CommandContextEntry commandContextEntry = new CommandContextEntry(command, context);
    OUTER_MAP.computeIfAbsent(idHash, k -> new HashMap<>()).put(seq, commandContextEntry);
  }

  public static synchronized LettuceRedisCommandContext get(Object command) {
    int idHash = System.identityHashCode(command);
    Map<Long, CommandContextEntry> innerMap = OUTER_MAP.get(idHash);
    if (innerMap == null) {
      return null;
    }
    for (CommandContextEntry ctx : innerMap.values()) {
      if (command == ctx.command) {
        return ctx.context;
      }
    }
    return null;
  }

  public static synchronized LettuceRedisCommandContext remove(Object command) {
    int idHash = System.identityHashCode(command);
    Map<Long, CommandContextEntry> innerMap = OUTER_MAP.get(idHash);
    if (innerMap == null) {
      return null;
    }
    for (Iterator<Map.Entry<Long, CommandContextEntry>> it = innerMap.entrySet().iterator();
         it.hasNext(); ) {
      Map.Entry<Long, CommandContextEntry> entry = it.next();
      if (command == entry.getValue().command) {
        it.remove();
        if (innerMap.isEmpty()) {
          OUTER_MAP.remove(idHash);
        }
        return entry.getValue().context;
      }
    }
    return null;
  }

  static class CommandContextEntry {
    final Object command;
    final LettuceRedisCommandContext context;
    final Long birthtime;

    CommandContextEntry(Object command, LettuceRedisCommandContext context) {
      this.command = command;
      this.context = context;
      this.birthtime = System.currentTimeMillis();
    }

  }

}
