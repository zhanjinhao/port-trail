package cn.addenda.porttrail.agent.transform.interceptor.datasource.hikari;

import cn.addenda.porttrail.agent.log.AgentPortTrailLoggerFactory;
import cn.addenda.porttrail.agent.transform.interceptor.Interceptor;
import cn.addenda.porttrail.agent.util.ReflectionUtils;
import cn.addenda.porttrail.infrastructure.log.PortTrailLogger;
import cn.addenda.porttrail.jdbc.core.PortTrailConnection;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.concurrent.Callable;

public class HikariConcurrentBagBorrowInterceptor implements Interceptor {

  private static Field _connectionField;

  private static final PortTrailLogger log =
          AgentPortTrailLoggerFactory.getInstance().getPortTrailLogger(HikariConcurrentBagBorrowInterceptor.class);

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

    Object call = zuper.call();
    if (call == null) {
      return call;
    }

    if (!"com.zaxxer.hikari.pool.PoolEntry".equals(call.getClass().getName())) {
      log.error("ConcurrentBag#borrow return [{}].", call.getClass().getName());
      return call;
    }

    Field connectionField = getConnectionField(call);
    Connection connection = (Connection) getFieldValueFromObject(call, connectionField);
    if (connection == null) {
      return call;
    }

    if (connection instanceof PortTrailConnection) {
      PortTrailConnection portTrailConnection = ((PortTrailConnection) connection);
      portTrailConnection.reset();
    }

    return call;
  }

  private static synchronized Field getConnectionField(Object o) {
    if (_connectionField == null) {
      _connectionField = ReflectionUtils.getField(o, "connection");
    }
    return _connectionField;
  }

  @Override
  public boolean ifOverride() {
    return false;
  }

}
