package cn.addenda.porttrail.agent.transform.interceptor.job.xxl.jobhandler;

import cn.addenda.porttrail.agent.transform.interceptor.AbstractDeduplicationEntryPointInterceptor;
import cn.addenda.porttrail.agent.transform.interceptor.Interceptor;
import cn.addenda.porttrail.agent.util.AnnotationUtils;
import cn.addenda.porttrail.common.entrypoint.EntryPoint;
import cn.addenda.porttrail.common.entrypoint.EntryPointType;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Deque;
import java.util.concurrent.Callable;

import static cn.addenda.porttrail.agent.transform.interceptor.job.xxl.jobhandler.XxlJobHandlerInterceptorPointDefine.JOB_HANDLER_NAME;

public class XxlJobHandlerInterceptor extends AbstractDeduplicationEntryPointInterceptor implements Interceptor {

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
    Class<?> aClass = targetObj.getClass();
    Annotation jobHandler = AnnotationUtils.getAnnotation(aClass, JOB_HANDLER_NAME);

    return callWithEntryPoint(
            getAttributeValue(jobHandler, "value", "UNKNOWN_JOB_HANDLER") +
                    ":" + aClass.getName(), zuper);
  }

  @Override
  public boolean ifOverride() {
    return false;
  }

  @Override
  protected EntryPoint entryPoint(String detail) {
    return EntryPoint.of(EntryPointType.JOB_XXL_JOB_HANDLER, detail);
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
