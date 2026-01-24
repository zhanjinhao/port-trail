package cn.addenda.porttrail.agent.transform.interceptor.job.xxl.jobhandler;

import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPoint;
import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPointDefine;
import cn.addenda.porttrail.agent.transform.match.IndirectMatch;
import cn.addenda.porttrail.agent.transform.match.MultiAnnotationMatch;
import cn.addenda.porttrail.common.util.ArrayUtils;

import java.util.List;

public class XxlJobHandlerInterceptorPointDefine implements InterceptorPointDefine {

  public static final String JOB_HANDLER_NAME = "com.xxl.job.core.handler.annotation.JobHandler";

  @Override
  public IndirectMatch getEnhancedClass() {
    return MultiAnnotationMatch.of(JOB_HANDLER_NAME);
  }

  @Override
  public List<InterceptorPoint> getInterceptorPointList() {
    return ArrayUtils.asArrayList(new XxlJobHandlerExecuteInterceptorPoint());
  }

}
