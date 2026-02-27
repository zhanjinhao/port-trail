package cn.addenda.porttrail.agent.transform.interceptor.job.xxl.script;

import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPoint;
import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPointDefine;
import cn.addenda.porttrail.agent.transform.match.NameMatch;
import cn.addenda.porttrail.common.util.ArrayUtils;

import java.util.List;

public class XxlScriptInterceptorPointDefine implements InterceptorPointDefine {

  public static final String SCRIPT_JOB_HANDLER_NAME = "com.xxl.job.core.handler.impl.ScriptJobHandler";

  @Override
  public NameMatch getEnhancedClass() {
    return NameMatch.of(SCRIPT_JOB_HANDLER_NAME);
  }

  @Override
  public List<InterceptorPoint> getInterceptorPointList() {
    return ArrayUtils.asArrayList(new XxlScriptExecuteInterceptorPoint());
  }

}
