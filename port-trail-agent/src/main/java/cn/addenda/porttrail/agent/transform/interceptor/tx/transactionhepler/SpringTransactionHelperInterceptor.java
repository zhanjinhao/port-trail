package cn.addenda.porttrail.agent.transform.interceptor.tx.transactionhepler;

import cn.addenda.porttrail.agent.log.AgentPortTrailLoggerFactory;
import cn.addenda.porttrail.agent.transform.interceptor.Interceptor;
import cn.addenda.porttrail.agent.transform.interceptor.tx.AbstractTxEntryPointInterceptor;
import cn.addenda.porttrail.common.entrypoint.EntryPoint;
import cn.addenda.porttrail.common.entrypoint.EntryPointType;
import cn.addenda.porttrail.infrastructure.log.PortTrailLogger;
import cn.addenda.porttrail.stacktrace.StackTraceUtils;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import static cn.addenda.porttrail.agent.transform.interceptor.tx.transactionhepler.SpringTransactionHelperInterceptorPointDefine.TRANSACTION_HELPER_NAME;

public class SpringTransactionHelperInterceptor extends AbstractTxEntryPointInterceptor implements Interceptor {

  private static final PortTrailLogger log =
          AgentPortTrailLoggerFactory.getInstance().getPortTrailLogger(SpringTransactionHelperInterceptor.class);

  private static final String TRANSACTION_ASPECT_SUPPORT_NAME = "#org.springframework.transaction.interceptor.TransactionAspectSupport";

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
    log.info("TargetObj is [{}] and it's classloader is [{}].", targetObj, targetObj.getClass().getClassLoader());

    String callerInfo = StackTraceUtils.getCallerInfo(false, false, false, TRANSACTION_HELPER_NAME, TRANSACTION_ASPECT_SUPPORT_NAME);

    return callWithEntryPoint(callerInfo, zuper);
  }

  @Override
  public boolean ifOverride() {
    return false;
  }

  @Override
  protected EntryPoint entryPoint(String detail) {
    return EntryPoint.of(EntryPointType.TX_TRANSACTION_HELPER, detail);
  }

}
