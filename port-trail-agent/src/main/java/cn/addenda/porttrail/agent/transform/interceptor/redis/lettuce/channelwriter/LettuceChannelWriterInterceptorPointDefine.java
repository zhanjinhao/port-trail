package cn.addenda.porttrail.agent.transform.interceptor.redis.lettuce.channelwriter;

import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPoint;
import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPointDefine;
import cn.addenda.porttrail.agent.transform.match.HierarchyMatch;
import cn.addenda.porttrail.common.util.ArrayUtils;

import java.util.List;

/**
 * 增强类: io.lettuce.core.RedisChannelWriter 的所有实现类。
 * <p>
 * 同时拦截 ClusterDistributionChannelWriter 和 DefaultEndpoint：
 * <ul>
 *   <li>ClusterDistributionChannelWriter — 在业务线程捕获入口栈快照，存入 LettuceSnapshotHolder</li>
 *   <li>DefaultEndpoint — 创建 RedisCommandContext，使用存储的快照（cluster）或自行捕获（standalone）</li>
 * </ul>
 */
public class LettuceChannelWriterInterceptorPointDefine implements InterceptorPointDefine {

  @Override
  public HierarchyMatch getEnhancedClass() {
    return HierarchyMatch.of("io.lettuce.core.RedisChannelWriter");
  }

  @Override
  public List<InterceptorPoint> getInterceptorPointList() {
    return ArrayUtils.asArrayList(new LettuceChannelWriterInterceptorPoint());
  }

}
