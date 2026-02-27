package cn.addenda.porttrail.agent.transform.interceptor.jdbc;

import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPoint;
import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPointDefine;
import cn.addenda.porttrail.agent.transform.match.IndirectMatch;
import cn.addenda.porttrail.agent.transform.match.MultiClassNameMatch;
import cn.addenda.porttrail.common.util.ArrayUtils;

import java.util.List;

public class PortTrailStatementInterceptorPointDefine implements InterceptorPointDefine {

  @Override
  public IndirectMatch getEnhancedClass() {
    return MultiClassNameMatch.of(
            "cn.addenda.porttrail.jdbc.core.PortTrailStatement",
            "cn.addenda.porttrail.jdbc.core.PortTrailPreparedStatement");
  }

  @Override
  public List<InterceptorPoint> getInterceptorPointList() {
    return ArrayUtils.asArrayList(new PortTrailStatementInterceptorPoint());
  }

}
