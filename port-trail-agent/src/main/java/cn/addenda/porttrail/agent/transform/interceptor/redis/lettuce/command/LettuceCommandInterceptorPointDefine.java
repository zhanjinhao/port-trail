package cn.addenda.porttrail.agent.transform.interceptor.redis.lettuce.command;

import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPoint;
import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPointDefine;
import cn.addenda.porttrail.agent.transform.match.HierarchyMatch;
import cn.addenda.porttrail.common.util.ArrayUtils;

import java.util.List;

/**
 * 增强类: io.lettuce.core.protocol.RedisCommand 的所有子类
 */
public class LettuceCommandInterceptorPointDefine implements InterceptorPointDefine {

  @Override
  public HierarchyMatch getEnhancedClass() {
    return HierarchyMatch.of("io.lettuce.core.protocol.RedisCommand");
  }

  @Override
  public List<InterceptorPoint> getInterceptorPointList() {
    return ArrayUtils.asArrayList(
            new LettuceCommandInterceptorPoint.CompleteInterceptorPoint(),
            new LettuceCommandInterceptorPoint.CompleteExceptionallyInterceptorPoint(),
            new LettuceCommandInterceptorPoint.CancelInterceptorPoint()
    );
  }

}