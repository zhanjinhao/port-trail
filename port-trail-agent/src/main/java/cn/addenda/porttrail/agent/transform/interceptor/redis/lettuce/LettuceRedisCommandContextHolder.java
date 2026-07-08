package cn.addenda.porttrail.agent.transform.interceptor.redis.lettuce;

import cn.addenda.porttrail.agent.log.AgentPortTrailLoggerFactory;
import cn.addenda.porttrail.common.util.DateUtils;
import cn.addenda.porttrail.infrastructure.log.PortTrailLogger;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
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

  private static final PortTrailLogger log =
          AgentPortTrailLoggerFactory.getInstance().getPortTrailLogger(LettuceRedisCommandContextHolder.class);

  private static final Map<Integer, Map<Long, CommandContextEntry>> OUTER_MAP = new HashMap<>();

  private static final AtomicLong SEQ = new AtomicLong(0);

  private static final long LEAK_THRESHOLD_MS = TimeUnit.MINUTES.toMillis(10);
  private static final long LEAK_DETECTION_INITIAL_DELAY = 5 * 60;
  private static final long LEAK_DETECTION_PERIOD = 5 * 60;

  static {
    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
      Thread t = new Thread(r, "LettuceRedisCommandContextHolder-LeakDetector");
      t.setDaemon(true);
      return t;
    });
    scheduler.scheduleAtFixedRate(LettuceRedisCommandContextHolder::detectLeaks,
            LEAK_DETECTION_INITIAL_DELAY, LEAK_DETECTION_PERIOD, TimeUnit.SECONDS);
  }

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

  private static void detectLeaks() {
    List<String> staleList = new ArrayList<>();
    long now = System.currentTimeMillis();
    int total = 0;
    synchronized (LettuceRedisCommandContextHolder.class) {
      for (Map<Long, CommandContextEntry> innerMap : OUTER_MAP.values()) {
        for (CommandContextEntry entry : innerMap.values()) {
          total++;
          if (now - entry.birthtime > LEAK_THRESHOLD_MS) {
            staleList.add(String.format("command=%s,peer=%s,birthtime=%s",
                    entry.context.getCommandName(),
                    entry.context.getPeer(),
                    DateUtils.format(DateUtils.timestampToLocalDateTime(entry.birthtime), DateUtils.yMdHmsS_FORMATTER)));
          }
        }
      }
    }
    if (!staleList.isEmpty()) {
      log.error("RedisCommandContextHolder 泄露检测发现[{}]条存活超过10分钟的记录, 总数[{}]。详情: {}",
              staleList.size(), total, staleList);
    }
  }

}
