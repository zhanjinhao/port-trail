package cn.addenda.porttrail.agent.transform.interceptor.job.xxl.glue;

import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPoint;
import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPointDefine;
import cn.addenda.porttrail.agent.transform.match.NameMatch;
import cn.addenda.porttrail.common.util.ArrayUtils;

import java.util.List;

public class XxlGlueInterceptorPointDefine implements InterceptorPointDefine {

  public static final String GLUE_JOB_HANDLER_NAME = "com.xxl.job.core.handler.impl.GlueJobHandler";

  @Override
  public NameMatch getEnhancedClass() {
    return NameMatch.of(GLUE_JOB_HANDLER_NAME);
  }

  @Override
  public List<InterceptorPoint> getInterceptorPointList() {
    return ArrayUtils.asArrayList(new XxlGlueExecuteInterceptorPoint());
  }

}
