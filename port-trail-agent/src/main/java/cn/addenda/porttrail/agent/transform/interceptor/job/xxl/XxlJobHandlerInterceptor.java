package cn.addenda.porttrail.agent.transform.interceptor.job.xxl;

import cn.addenda.porttrail.agent.log.AgentPortTrailLoggerFactory;
import cn.addenda.porttrail.agent.transform.interceptor.AbstractDeduplicationEntryPointInterceptor;
import cn.addenda.porttrail.agent.transform.interceptor.Interceptor;
import cn.addenda.porttrail.agent.util.ReflectionUtils;
import cn.addenda.porttrail.common.entrypoint.EntryPoint;
import cn.addenda.porttrail.common.entrypoint.EntryPointType;
import cn.addenda.porttrail.infrastructure.log.PortTrailLogger;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Deque;
import java.util.concurrent.Callable;

import static cn.addenda.porttrail.agent.transform.interceptor.job.xxl.XxlJobHandlerInterceptorPointDefine.*;

public class XxlJobHandlerInterceptor extends AbstractDeduplicationEntryPointInterceptor implements Interceptor {

  private static final PortTrailLogger log =
          AgentPortTrailLoggerFactory.getInstance().getPortTrailLogger(XxlJobHandlerInterceptor.class);

  private static Field _executorHandler;
  private static Field _jobHandler;
  private static Field _jobId;
  private static Field _glueType;

  /**
   * 被@RuntimeType标注的方法就是被委托的方法
   */
  @RuntimeType
  public Object intercept(
          // byteBuddy会在运行期间给被注定注解修饰的方法参数进行赋值:

          // 当前被拦截的、动态生成的那个对象
          @This Object targetObj,
          // 被调用的原始方法
          @Origin Method targetMethod,
          // 被拦截的方法参数
          @AllArguments Object[] targetMethodArgs,
          // 当前被拦截的、动态生成的那个对象的父类对象
          @Super Object concurrentBag,
          // 用于调用父类的方法。
          @SuperCall Callable<?> zuper
  ) throws Exception {

    String clazzName = targetObj.getClass().getName();

    if (EXECUTOR_BIZ_IMPL_NAME.equals(clazzName)) {
      Object triggerParam = targetMethodArgs[0];
      Field executorHandlerField = getExecutorHandlerField(triggerParam);
      return callWithEntryPoint("BEAN:" + getFieldValueFromObject(triggerParam, executorHandlerField, "UNKNOWN_EXECUTOR_HANDLER"), zuper);
    } else if (GLUE_JOB_HANDLER_NAME.equals(clazzName)) {
      Field jobHandlerField = getJobHandlerField(targetObj);
      return callWithEntryPoint("GLUE:" + getFieldValueFromObject(targetObj, jobHandlerField, "UNKNOWN_JOB_HANDLER"), zuper);
    } else if (SCRIPT_JOB_HANDLER_NAME.equals(clazzName)) {
      Field jobIdField = getJobIdField(targetObj);
      Field glueTypeField = getGlueTypeField(targetObj);
      return callWithEntryPoint("SCRIPT:" + getFieldValueFromObject(targetObj, jobIdField, "UNKNOWN_JOB_ID")
              + ":" + getFieldValueFromObject(targetObj, glueTypeField, "UNKNOWN_GLUE_TYPE"), zuper);
    } else {
      log.error("Unknown point define of interceptor ：[{}].", targetObj.getClass().getName());
      return zuper.call();
    }
  }

  private static synchronized Field getExecutorHandlerField(Object o) {
    if (_executorHandler == null) {
      _executorHandler = ReflectionUtils.getField(o, "executorHandler");
    }
    return _executorHandler;
  }

  private static synchronized Field getJobHandlerField(Object o) {
    if (_jobHandler == null) {
      _jobHandler = ReflectionUtils.getField(o, "jobHandler");
    }
    return _jobHandler;
  }

  private static synchronized Field getJobIdField(Object o) {
    if (_jobId == null) {
      _jobId = ReflectionUtils.getField(o, "jobId");
    }
    return _jobId;
  }

  private static synchronized Field getGlueTypeField(Object o) {
    if (_glueType == null) {
      _glueType = ReflectionUtils.getField(o, "glueType");
    }
    return _glueType;
  }

  @Override
  public boolean ifOverride() {
    return false;
  }

  @Override
  protected EntryPoint entryPoint(String detail) {
    return EntryPoint.of(EntryPointType.JOB_XXL, detail);
  }

  private static final ThreadLocal<Deque<String>> deduplicationStack = new ThreadLocal<Deque<String>>() {
    @Override
    public String toString() {
      return getClass().getName() + "@" + XxlJobHandlerInterceptor.class.getName() + "@" + Integer.toHexString(hashCode());
    }
  };

  @Override
  protected ThreadLocal<Deque<String>> getDeduplicationStack() {
    return deduplicationStack;
  }

}
