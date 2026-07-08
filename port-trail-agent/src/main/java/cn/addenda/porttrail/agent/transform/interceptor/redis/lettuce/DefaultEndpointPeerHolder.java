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
 * 持有 DefaultEndpoint 实例与 peer 地址的映射关系。
 * <p>
 * 采用二级 Map 结构避免 identityHashCode 冲突：
 * 外层 key = identityHashCode（分桶），内层 key = 原子序列号（唯一标识）。
 * get/remove 时通过 endpoint 引用在内层 Map 中线性匹配。
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DefaultEndpointPeerHolder {

  private static final PortTrailLogger log =
          AgentPortTrailLoggerFactory.getInstance().getPortTrailLogger(DefaultEndpointPeerHolder.class);

  private static final Map<Integer, HashMap<Long, EndpointPeerEntry>> OUTER_MAP = new HashMap<>();

  private static final AtomicLong SEQ = new AtomicLong(0);

  private static final long LEAK_THRESHOLD_MS = TimeUnit.MINUTES.toMillis(10);
  private static final long LEAK_DETECTION_INITIAL_DELAY = 5 * 60;
  private static final long LEAK_DETECTION_PERIOD = 5 * 60;

  static {
    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
      Thread t = new Thread(r, "DefaultEndpointPeerHolder-LeakDetector");
      t.setDaemon(true);
      return t;
    });
    scheduler.scheduleAtFixedRate(DefaultEndpointPeerHolder::detectLeaks,
            LEAK_DETECTION_INITIAL_DELAY, LEAK_DETECTION_PERIOD, TimeUnit.SECONDS);
  }

  public static synchronized void put(Object defaultEndpoint, String peer) {
    int idHash = System.identityHashCode(defaultEndpoint);
    long seq = SEQ.incrementAndGet();
    OUTER_MAP.computeIfAbsent(idHash, k -> new HashMap<>())
            .put(seq, new EndpointPeerEntry(defaultEndpoint, peer));
  }

  public static synchronized String get(Object defaultEndpoint) {
    int idHash = System.identityHashCode(defaultEndpoint);
    HashMap<Long, EndpointPeerEntry> innerMap = OUTER_MAP.get(idHash);
    if (innerMap == null) {
      return null;
    }
    for (EndpointPeerEntry entry : innerMap.values()) {
      if (defaultEndpoint == entry.endpoint) {
        return entry.peer;
      }
    }
    return null;
  }

  public static synchronized String remove(Object defaultEndpoint) {
    int idHash = System.identityHashCode(defaultEndpoint);
    Map<Long, EndpointPeerEntry> innerMap = OUTER_MAP.get(idHash);
    if (innerMap == null) {
      return null;
    }
    for (Iterator<Map.Entry<Long, EndpointPeerEntry>> it = innerMap.entrySet().iterator(); it.hasNext(); ) {
      Map.Entry<Long, EndpointPeerEntry> mapEntry = it.next();
      if (defaultEndpoint == mapEntry.getValue().endpoint) {
        it.remove();
        if (innerMap.isEmpty()) {
          OUTER_MAP.remove(idHash);
        }
        return mapEntry.getValue().peer;
      }
    }
    return null;
  }

  static class EndpointPeerEntry {

    final Object endpoint;
    final String peer;
    final Long birthtime;

    EndpointPeerEntry(Object endpoint, String peer) {
      this.endpoint = endpoint;
      this.peer = peer;
      this.birthtime = System.currentTimeMillis();
    }

  }

  private static void detectLeaks() {
    List<String> staleList = new ArrayList<>();
    long now = System.currentTimeMillis();
    int total = 0;
    synchronized (DefaultEndpointPeerHolder.class) {
      for (HashMap<Long, EndpointPeerEntry> innerMap : OUTER_MAP.values()) {
        for (EndpointPeerEntry entry : innerMap.values()) {
          total++;
          if (now - entry.birthtime > LEAK_THRESHOLD_MS) {
            staleList.add(String.format("peer=%s, birthtime=%s",
                    entry.peer,
                    DateUtils.format(DateUtils.timestampToLocalDateTime(entry.birthtime), DateUtils.yMdHmsS_FORMATTER)));
          }
        }
      }
    }
    if (!staleList.isEmpty()) {
      log.error("DefaultEndpointPeerHolder 泄露检测发现[{}]条存活超过10分钟的记录, 总数[{}]。详情: {}",
              staleList.size(), total, staleList);
    }
  }

}
