package cn.addenda.porttrail.agent.transform.interceptor.job.xxl.glue;

import cn.addenda.porttrail.agent.transform.interceptor.Interceptor;
import cn.addenda.porttrail.agent.transform.interceptor.job.xxl.AbstractJobHandlerExecuteInterceptorPoint;

public class XxlGlueExecuteInterceptorPoint extends AbstractJobHandlerExecuteInterceptorPoint {

  @Override
  public Interceptor getInterceptor() {
    return new XxlGlueInterceptor();
  }

}
