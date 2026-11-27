package cn.addenda.porttrail.server.config;

import cn.addenda.component.cache.codec.DefaultValueCodec;
import cn.addenda.component.cache.helper.CacheHelper;
import cn.addenda.component.cache.helper.JacksonStringCacheValueSerializer;
import io.lettuce.core.RedisURI;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import io.lettuce.core.cluster.RedisClusterClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * redis 相关配置。连接信息来自 redis_config.txt（见 application.yml 的 spring.config.import）。
 *
 * <p>这里连的是 redis cluster，所以用 {@link RedisClusterClient} 并通过
 * {@code CacheHelperBuilder#useLettuceCluster} 装配底层 KVCache；
 * CacheHelper 只用 get/set/delete 三类单 key 操作，由集群按 key 的 slot 路由，
 * 不存在跨 slot 问题。
 */
@Configuration
public class RedisConfig {

  @Value("${redis.cluster.nodes}")
  private List<String> clusterNodes;

  @Value("${redis.cluster.max-redirects:3}")
  private int maxRedirects;

  @Bean(destroyMethod = "shutdown")
  public RedisClusterClient redisClusterClient() {
    List<RedisURI> redisURIList = clusterNodes.stream()
            .map(node -> {
              String[] parts = node.split(":");
              return RedisURI.create(parts[0], Integer.parseInt(parts[1]));
            })
            .collect(Collectors.toList());

    RedisClusterClient clusterClient = RedisClusterClient.create(redisURIList);

    ClusterTopologyRefreshOptions topologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
            .enablePeriodicRefresh(Duration.ofMinutes(10))
            .enableAllAdaptiveRefreshTriggers()
            .build();

    clusterClient.setOptions(ClusterClientOptions.builder()
            .topologyRefreshOptions(topologyRefreshOptions)
            .maxRedirects(maxRedirects)
            .build());

    return clusterClient;
  }

  /**
   * 全局统一的 CacheHelper。
   * <p>
   * 底层集群连接由 builder 创建并通过 onDestroy 登记，destroy() 由 DisposableBean 触发：
   * 先补做完剩余的延迟删除任务，再关闭连接；RedisClusterClient 自身由上面那个 bean 的
   * destroyMethod 关闭，且在依赖顺序上晚于本 bean。
   * <p>
   * value 的 wire 格式用 {@code codecWithStringAsIdentity()}：String 原样存 UTF-8（CacheHelper
   * 的 NULL 哨兵也是裸串），业务对象仍由 {@link JacksonStringCacheValueSerializer} 转成 JSON。
   */
  @Bean
  public CacheHelper<String> cacheHelper(RedisClusterClient redisClusterClient) {
    return CacheHelper.<String>builder()
            .useLettuceCluster(redisClusterClient, DefaultValueCodec.codecWithStringAsIdentity())
            .valueSerializer(JacksonStringCacheValueSerializer.INSTANCE)
            .concurrencyGranularity(CacheHelper.CONCURRENCY_GRANULARITY_PREFIX)
            .build();
  }

}
