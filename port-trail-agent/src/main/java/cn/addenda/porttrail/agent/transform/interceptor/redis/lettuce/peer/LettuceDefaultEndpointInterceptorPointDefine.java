package cn.addenda.porttrail.agent.transform.interceptor.redis.lettuce.peer;

import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPoint;
import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPointDefine;
import cn.addenda.porttrail.agent.transform.match.NameMatch;
import cn.addenda.porttrail.common.util.ArrayUtils;

import java.util.List;

/**
 * 增强类: io.lettuce.core.protocol.DefaultEndpoint
 */
public class LettuceDefaultEndpointInterceptorPointDefine implements InterceptorPointDefine {

  @Override
  public NameMatch getEnhancedClass() {
    return NameMatch.of("io.lettuce.core.protocol.DefaultEndpoint");
  }

  @Override
  public List<InterceptorPoint> getInterceptorPointList() {
    return ArrayUtils.asArrayList(
            new LettuceDefaultEndpointInterceptorPoint(),
            new LettuceDefaultEndpointInactiveInterceptorPoint()
    );
  }

}
