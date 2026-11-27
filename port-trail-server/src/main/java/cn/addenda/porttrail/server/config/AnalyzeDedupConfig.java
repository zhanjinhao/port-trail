package cn.addenda.porttrail.server.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 去重记录分析器的阈值与缓存 TTL。
 *
 * @author addenda
 */
@Getter
@Component
public class AnalyzeDedupConfig {

  /**
   * cost 超过该值判定为慢 SQL，单位 ms。
   */
  @Value("${port-trail.analyze.dedup.cost-threshold-ms:1000}")
  private int costThresholdMs;

  /**
   * IN 子句元素个数超过该值判定为 IN 过多。
   */
  @Value("${port-trail.analyze.dedup.in-threshold:200}")
  private int inThreshold;

  /**
   * 去重标记（每规则一个）的缓存时长，单位 ms，默认 2 天。
   */
  @Value("${port-trail.analyze.dedup.cache-ttl-ms:172800000}")
  private long cacheTtlMs;

  /**
   * 解析结论的缓存时长，单位 ms，默认 7 天。
   */
  @Value("${port-trail.analyze.dedup.sql-parse-cache-ttl-ms:604800000}")
  private long sqlParseCacheTtlMs;

  /**
   * 「explain 过且没有全表扫描」的负缓存时长，单位 ms，默认 1 小时；0 表示不缓存。
   * <p>
   * 用来避免对未命中全表扫描的 SQL 在每次执行时都连业务库 explain。
   */
  @Value("${port-trail.analyze.dedup.explain-none-ttl-ms:3600000}")
  private long explainNoneTtlMs;

}
