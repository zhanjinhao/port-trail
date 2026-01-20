package cn.addenda.porttrail.agent.transform.interceptor.task;

import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPoint;
import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPointDefine;
import cn.addenda.porttrail.agent.transform.match.MultiClassNameMatch;
import cn.addenda.porttrail.common.util.ArrayUtils;

import java.util.List;

public class TaskInterceptorPointDefine implements InterceptorPointDefine {

  private static final String CALLABLE_TASK_NAME = "cn.addenda.porttrail.client.task.PortTrailCallable";
  private static final String CONSUMER_TASK_NAME = "cn.addenda.porttrail.client.task.PortTrailConsumer";
  private static final String FUNCTION_TASK_NAME = "cn.addenda.porttrail.client.task.PortTrailFunction";
  private static final String RUNNABLE_TASK_NAME = "cn.addenda.porttrail.client.task.PortTrailRunnable";
  private static final String SUPPLIER_TASK_NAME = "cn.addenda.porttrail.client.task.PortTrailSupplier";

  @Override
  public MultiClassNameMatch getEnhancedClass() {
    return MultiClassNameMatch.of(
            CALLABLE_TASK_NAME, CONSUMER_TASK_NAME, FUNCTION_TASK_NAME, RUNNABLE_TASK_NAME, SUPPLIER_TASK_NAME);
  }

  @Override
  public List<InterceptorPoint> getInterceptorPointList() {
    return ArrayUtils.asArrayList(new TaskExecuteInterceptorPoint());
  }

}
