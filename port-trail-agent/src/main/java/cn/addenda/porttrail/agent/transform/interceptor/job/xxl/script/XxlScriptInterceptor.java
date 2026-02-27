package cn.addenda.porttrail.agent.transform.interceptor.job.xxl.script;

import cn.addenda.porttrail.agent.transform.interceptor.AbstractDeduplicationEntryPointInterceptor;
import cn.addenda.porttrail.agent.transform.interceptor.Interceptor;
import cn.addenda.porttrail.agent.util.ReflectionUtils;
import cn.addenda.porttrail.common.entrypoint.EntryPoint;
import cn.addenda.porttrail.common.entrypoint.EntryPointType;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Deque;
import java.util.concurrent.Callable;

public class XxlScriptInterceptor extends AbstractDeduplicationEntryPointInterceptor implements Interceptor {

  private static Field _jobIdField;
  private static Field _glueSourceField;
  private static Field _glueTypeField;

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
    Field jobIdField = getJobIdField(targetObj);
    Field glueSourceField = getGlueSourceField(targetObj);
    Field glueTypeField = getGlueTypeField(targetObj);
    return callWithEntryPoint(
            getFieldValueFromObject(targetObj, jobIdField, "UNKNOWN_JOB_ID")
                    + ":" + getFieldValueFromObject(targetObj, glueSourceField, "UNKNOWN_GLUE_SOURCE")
                    + ":" + getFieldValueFromObject(targetObj, glueTypeField, "UNKNOWN_GLUE_TYPE"), zuper);
  }

  private static synchronized Field getJobIdField(Object o) {
    if (_jobIdField == null) {
      _jobIdField = ReflectionUtils.getField(o, "jobId");
    }
    return _jobIdField;
  }

  private static synchronized Field getGlueSourceField(Object o) {
    if (_glueSourceField == null) {
      _glueSourceField = ReflectionUtils.getField(o, "gluesource");
    }
    if (_glueSourceField == null) {
      _glueSourceField = ReflectionUtils.getField(o, "glueSource");
    }
    return _glueSourceField;
  }

  private static synchronized Field getGlueTypeField(Object o) {
    if (_glueTypeField == null) {
      _glueTypeField = ReflectionUtils.getField(o, "glueType");
    }
    return _glueTypeField;
  }

  @Override
  public boolean ifOverride() {
    return false;
  }

  @Override
  protected EntryPoint entryPoint(String detail) {
    return EntryPoint.of(EntryPointType.JOB_XXL_SCRIPT_JOB, detail);
  }

  private static final ThreadLocal<Deque<String>> deduplicationStack = new ThreadLocal<Deque<String>>() {
    @Override
    public String toString() {
      return getClass().getName() + "@" + XxlScriptInterceptor.class.getName() + "@" + Integer.toHexString(hashCode());
    }
  };

  @Override
  protected ThreadLocal<Deque<String>> getDeduplicationStack() {
    return deduplicationStack;
  }

}
