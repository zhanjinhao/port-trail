package cn.addenda.porttrail.agent.transform.interceptor;

import cn.addenda.porttrail.common.entrypoint.EntryPoint;
import cn.addenda.porttrail.infrastructure.entrypoint.EntryPointStackContext;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public abstract class AbstractEntryPointInterceptor {

  protected Object callWithEntryPoint(String detail, Callable<?> zuper)
          throws Exception {
    boolean ifPushTxEntryPoint = ifPushEntryPoint(detail);
    if (ifPushTxEntryPoint) {
      beforePush(detail);
      EntryPointStackContext.pushEntryPoint(entryPoint(detail));
    }
    try {
      return zuper.call();
    } finally {
      if (ifPushTxEntryPoint) {
        beforePop(detail);
        EntryPointStackContext.popEntryPoint();
      }
    }
  }

  protected abstract EntryPoint entryPoint(String detail);

  protected String assembleDetail(Object targetObj, Method targetMethod) {
    return targetObj.getClass().getName() + "#" + targetMethod.getName();
  }

  /**
   * 在pushEntryPoint之前回调
   */
  protected void beforePush(String detail) {
  }

  /**
   * 在popEntryPoint之前回调
   */
  protected void beforePop(String detail) {
  }

  /**
   * 是否能pushEntryPoint
   */
  protected boolean ifPushEntryPoint(String detail) {
    return true;
  }

}
