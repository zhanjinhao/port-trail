package cn.addenda.porttrail.agent.transform.interceptor.driver.mysql;

import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPoint;
import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPointDefine;
import cn.addenda.porttrail.agent.transform.match.ClassMatch;
import cn.addenda.porttrail.agent.transform.match.NameMatch;
import cn.addenda.porttrail.common.util.ArrayUtils;

import java.util.List;

public class MySQLDriverInterceptorPointDefine implements InterceptorPointDefine {

  public static final String DRIVER_NAME = "com.mysql.cj.jdbc.NonRegisteringDriver";

  @Override
  public ClassMatch getEnhancedClass() {
    return NameMatch.of(DRIVER_NAME);
  }

  @Override
  public List<InterceptorPoint> getInterceptorPointList() {
    return ArrayUtils.asArrayList(new MySQLDriverInterceptorPoint());
  }

}
