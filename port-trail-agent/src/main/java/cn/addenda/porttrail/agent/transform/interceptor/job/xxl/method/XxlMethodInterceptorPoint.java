package cn.addenda.porttrail.agent.transform.interceptor.job.xxl.method;

import cn.addenda.porttrail.agent.transform.interceptor.Interceptor;
import cn.addenda.porttrail.agent.transform.interceptor.job.xxl.AbstractJobHandlerExecuteInterceptorPoint;

public class XxlMethodInterceptorPoint extends AbstractJobHandlerExecuteInterceptorPoint {

  @Override
  public Interceptor getInterceptor() {
    return new XxlMethodInterceptor();
  }

}
