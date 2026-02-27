package cn.addenda.porttrail.agent.transform.interceptor.job.xxl.glue;

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

public class XxlGlueInterceptor extends AbstractDeduplicationEntryPointInterceptor implements Interceptor {

  private static Field _jobHandlerField;

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
    Field jobHandlerField = getJobHandlerField(targetObj);
    return callWithEntryPoint((String) getFieldValueFromObject(targetObj, jobHandlerField, "UNKNOWN_JOB_HANDLER"), zuper);
  }

  private static synchronized Field getJobHandlerField(Object o) {
    if (_jobHandlerField == null) {
      _jobHandlerField = ReflectionUtils.getField(o, "jobHandler");
    }
    return _jobHandlerField;
  }

  @Override
  public boolean ifOverride() {
    return false;
  }

  @Override
  protected EntryPoint entryPoint(String detail) {
    return EntryPoint.of(EntryPointType.JOB_XXL_GLUE_JOB, detail);
  }

  private static final ThreadLocal<Deque<String>> deduplicationStack = new ThreadLocal<Deque<String>>() {
    @Override
    public String toString() {
      return getClass().getName() + "@" + XxlGlueInterceptor.class.getName() + "@" + Integer.toHexString(hashCode());
    }
  };

  @Override
  protected ThreadLocal<Deque<String>> getDeduplicationStack() {
    return deduplicationStack;
  }

}
