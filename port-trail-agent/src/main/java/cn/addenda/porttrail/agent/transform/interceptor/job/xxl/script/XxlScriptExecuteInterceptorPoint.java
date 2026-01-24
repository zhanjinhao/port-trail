package cn.addenda.porttrail.agent.transform.interceptor.job.xxl.script;

import cn.addenda.porttrail.agent.transform.interceptor.Interceptor;
import cn.addenda.porttrail.agent.transform.interceptor.job.xxl.AbstractJobHandlerExecuteInterceptorPoint;

public class XxlScriptExecuteInterceptorPoint extends AbstractJobHandlerExecuteInterceptorPoint {

  @Override
  public Interceptor getInterceptor() {
    return new XxlScriptInterceptor();
  }

}
