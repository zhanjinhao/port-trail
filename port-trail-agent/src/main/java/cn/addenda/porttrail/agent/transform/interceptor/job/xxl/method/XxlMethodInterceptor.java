package cn.addenda.porttrail.agent.transform.interceptor.job.xxl.method;

import cn.addenda.porttrail.agent.transform.interceptor.AbstractDeduplicationEntryPointInterceptor;
import cn.addenda.porttrail.agent.transform.interceptor.Interceptor;
import cn.addenda.porttrail.agent.util.AnnotationUtils;
import cn.addenda.porttrail.agent.util.ReflectionUtils;
import cn.addenda.porttrail.common.entrypoint.EntryPoint;
import cn.addenda.porttrail.common.entrypoint.EntryPointType;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Deque;
import java.util.Optional;
import java.util.concurrent.Callable;

public class XxlMethodInterceptor extends AbstractDeduplicationEntryPointInterceptor implements Interceptor {

  private static final String XXL_JOB_NAME = "com.xxl.job.core.handler.annotation.XxlJob";

  private static Field _methodField;

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

    Field methodField = getMethodField(targetObj);

    Annotation xxlJobAnnotation = null;
    Method method = (Method) getFieldValueFromObject(targetObj, methodField);
    if (method != null) {
      xxlJobAnnotation = AnnotationUtils.getAnnotation(method, XXL_JOB_NAME);
    }

    return callWithEntryPoint(getAttributeValue(xxlJobAnnotation, "value", "UNKNOWN_METHOD_JOB")
                    + ":" + Optional.ofNullable(method).map(a -> a.getDeclaringClass().getName()).orElse("UNKNOWN_CLASS")
                    + "#" + Optional.ofNullable(method).map(Method::getName).orElse("UNKNOWN_METHOD")
            , zuper);
  }

  private static synchronized Field getMethodField(Object o) {
    if (_methodField == null) {
      _methodField = ReflectionUtils.getField(o, "method");
    }
    return _methodField;
  }

  @Override
  public boolean ifOverride() {
    return false;
  }

  @Override
  protected EntryPoint entryPoint(String detail) {
    return EntryPoint.of(EntryPointType.JOB_XXL_METHOD_JOB, detail);
  }

  private static final ThreadLocal<Deque<String>> deduplicationStack = new ThreadLocal<Deque<String>>() {
    @Override
    public String toString() {
      return getClass().getName() + "@" + XxlMethodInterceptor.class.getName() + "@" + Integer.toHexString(hashCode());
    }
  };

  @Override
  protected ThreadLocal<Deque<String>> getDeduplicationStack() {
    return deduplicationStack;
  }

}
