package cn.addenda.porttrail.agent.transform.interceptor.datasource.druid;


import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPoint;
import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPointDefine;
import cn.addenda.porttrail.agent.transform.match.NameMatch;
import cn.addenda.porttrail.common.util.ArrayUtils;

import java.util.List;

public class DruidDruidDataSourceInterceptorPointDefine implements InterceptorPointDefine {

  @Override
  public NameMatch getEnhancedClass() {
    return NameMatch.of("com.alibaba.druid.pool.DruidDataSource");
  }

  @Override
  public List<InterceptorPoint> getInterceptorPointList() {
    return ArrayUtils.asArrayList(new DruidDruidDataSourceInterceptorPoint());
  }

}
