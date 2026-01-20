package cn.addenda.porttrail.agent.transform.interceptor.job.xxl;

import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPoint;
import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPointDefine;
import cn.addenda.porttrail.agent.transform.match.IndirectMatch;
import cn.addenda.porttrail.agent.transform.match.MultiClassNameMatch;
import cn.addenda.porttrail.common.util.ArrayUtils;

import java.util.List;

public class XxlJobHandlerInterceptorPointDefine implements InterceptorPointDefine {

  public static final String EXECUTOR_BIZ_IMPL_NAME = "com.xxl.job.core.biz.impl.ExecutorBizImpl";
  public static final String GLUE_JOB_HANDLER_NAME = "com.xxl.job.core.handler.impl.GlueJobHandler";
  public static final String SCRIPT_JOB_HANDLER_NAME = "com.xxl.job.core.handler.impl.ScriptJobHandler";

  @Override
  public IndirectMatch getEnhancedClass() {
    return MultiClassNameMatch.of(EXECUTOR_BIZ_IMPL_NAME, GLUE_JOB_HANDLER_NAME, SCRIPT_JOB_HANDLER_NAME);
  }

  @Override
  public List<InterceptorPoint> getInterceptorPointList() {
    return ArrayUtils.asArrayList(new XxlJobHandlerExecuteInterceptorPoint());
  }

}
