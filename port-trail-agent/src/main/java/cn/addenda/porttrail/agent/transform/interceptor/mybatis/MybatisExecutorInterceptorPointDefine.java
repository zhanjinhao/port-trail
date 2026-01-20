package cn.addenda.porttrail.agent.transform.interceptor.mybatis;


import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPoint;
import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPointDefine;
import cn.addenda.porttrail.agent.transform.match.IndirectMatch;
import cn.addenda.porttrail.agent.transform.match.MultiClassNameMatch;
import cn.addenda.porttrail.common.util.ArrayUtils;

import java.util.List;

public class MybatisExecutorInterceptorPointDefine implements InterceptorPointDefine {

  @Override
  public IndirectMatch getEnhancedClass() {
    return MultiClassNameMatch.of(
            "org.apache.ibatis.executor.BaseExecutor",
            "org.apache.ibatis.executor.CachingExecutor");
  }

  @Override
  public List<InterceptorPoint> getInterceptorPointList() {
    return ArrayUtils.asArrayList(new MybatisExecutorCurdInterceptorPoint());
  }

}
