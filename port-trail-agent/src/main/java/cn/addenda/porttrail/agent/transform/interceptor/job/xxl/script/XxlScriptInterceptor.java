package cn.addenda.porttrail.agent.transform.interceptor.job.xxl.script;

import cn.addenda.porttrail.agent.log.AgentPortTrailLoggerFactory;
import cn.addenda.porttrail.agent.transform.interceptor.AbstractDeduplicationEntryPointInterceptor;
import cn.addenda.porttrail.agent.transform.interceptor.Interceptor;
import cn.addenda.porttrail.agent.util.CachedField;
import cn.addenda.porttrail.common.entrypoint.EntryPoint;
import cn.addenda.porttrail.common.entrypoint.EntryPointType;
import cn.addenda.porttrail.infrastructure.log.PortTrailLogger;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Deque;
import java.util.concurrent.Callable;

public class XxlScriptInterceptor extends AbstractDeduplicationEntryPointInterceptor implements Interceptor {

  private static final CachedField JOB_ID_FIELD = new CachedField("jobId");

  // XXL 不同版本该字段名不同，两个候选都试
  private static final CachedField GLUE_SOURCE_FIELD = new CachedField("gluesource", "glueSource");

  private static final CachedField GLUE_TYPE_FIELD = new CachedField("glueType");

  private static final PortTrailLogger log =
          AgentPortTrailLoggerFactory.getInstance().getPortTrailLogger(XxlScriptInterceptor.class);

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
    log.debug("Intercepted [{}].", Interceptor.assembleDetail(targetObj, targetMethod));

    Field jobIdField = JOB_ID_FIELD.get(targetObj);
    Field glueSourceField = GLUE_SOURCE_FIELD.get(targetObj);
    Field glueTypeField = GLUE_TYPE_FIELD.get(targetObj);
    return callWithEntryPoint(
            getFieldValueFromObject(targetObj, jobIdField, "UNKNOWN_JOB_ID")
                    + ":" + getFieldValueFromObject(targetObj, glueSourceField, "UNKNOWN_GLUE_SOURCE")
                    + ":" + getFieldValueFromObject(targetObj, glueTypeField, "UNKNOWN_GLUE_TYPE"), zuper);
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
