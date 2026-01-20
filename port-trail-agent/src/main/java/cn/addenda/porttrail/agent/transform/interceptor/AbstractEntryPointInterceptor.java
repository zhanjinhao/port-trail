package cn.addenda.porttrail.agent.transform.interceptor;

import cn.addenda.porttrail.common.entrypoint.EntryPoint;
import cn.addenda.porttrail.infrastructure.entrypoint.EntryPointStackContext;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public abstract class AbstractEntryPointInterceptor {

  protected Object callWithEntryPoint(String detail, Callable<?> zuper)
          throws Exception {

    try {
      EntryPointStackContext.pushEntryPoint(entryPoint(detail));
      return zuper.call();
    } finally {
      EntryPointStackContext.popEntryPoint();
    }

  }

  protected abstract EntryPoint entryPoint(String detail);

  protected String assembleDetail(Object targetObj, Method targetMethod) {
    return targetObj.getClass().getName() + "#" + targetMethod.getName();
  }

}
