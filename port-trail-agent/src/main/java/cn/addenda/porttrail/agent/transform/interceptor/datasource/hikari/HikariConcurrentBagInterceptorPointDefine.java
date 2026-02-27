package cn.addenda.porttrail.agent.transform.interceptor.datasource.hikari;


import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPoint;
import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPointDefine;
import cn.addenda.porttrail.agent.transform.match.ClassMatch;
import cn.addenda.porttrail.agent.transform.match.NameMatch;
import cn.addenda.porttrail.common.util.ArrayUtils;

import java.util.List;

public class HikariConcurrentBagInterceptorPointDefine implements InterceptorPointDefine {

  @Override
  public ClassMatch getEnhancedClass() {
    return NameMatch.of("com.zaxxer.hikari.util.ConcurrentBag");
  }

  @Override
  public List<InterceptorPoint> getInterceptorPointList() {
    return ArrayUtils.asArrayList(new HikariConcurrentBagInterceptorPoint());
  }

}
