package cn.addenda.porttrail.agent.transform.interceptor.redis.lettuce;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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

  // todo 泄露检测
  private static final Map<Integer, HashMap<Long, EndpointPeerEntry>> OUTER_MAP = new HashMap<>();

  private static final AtomicLong SEQ = new AtomicLong(0);

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

}
