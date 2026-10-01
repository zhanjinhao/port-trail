package cn.addenda.porttrail.server.config;

import cn.addenda.component.cache.helper.LettuceRedisClusterCacheHelper;
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
 * <p>这里连的是 redis cluster，所以用 {@link RedisClusterClient} 和
 * {@link LettuceRedisClusterCacheHelper}；CacheHelper 只用 get/set/delete 三类单 key 操作，
 * 由集群按 key 的 slot 路由，不存在跨 slot 问题。
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
   * 全局统一的 CacheHelper。destroy() 由 DisposableBean 触发，其中会关闭自己那条连接。
   */
  @Bean
  public LettuceRedisClusterCacheHelper lettuceRedisClusterCacheHelper(RedisClusterClient redisClusterClient) {
    return new LettuceRedisClusterCacheHelper(redisClusterClient);
  }

}
