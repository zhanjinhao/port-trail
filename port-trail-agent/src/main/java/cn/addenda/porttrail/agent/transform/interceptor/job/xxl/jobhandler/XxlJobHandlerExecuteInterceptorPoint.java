package cn.addenda.porttrail.agent.transform.interceptor.job.xxl.jobhandler;

import cn.addenda.porttrail.agent.transform.interceptor.Interceptor;
import cn.addenda.porttrail.agent.transform.interceptor.job.xxl.AbstractJobHandlerExecuteInterceptorPoint;

public class XxlJobHandlerExecuteInterceptorPoint extends AbstractJobHandlerExecuteInterceptorPoint {

  @Override
  public Interceptor getInterceptor() {
    return new XxlJobHandlerInterceptor();
  }

}
