package cn.addenda.porttrail.agent.transform.interceptor.driver.oracle;

import cn.addenda.porttrail.agent.log.AgentPortTrailLoggerFactory;
import cn.addenda.porttrail.agent.transform.interceptor.Interceptor;
import cn.addenda.porttrail.agent.transform.interceptor.datasource.hikari.HikariConcurrentBagBorrowInterceptor;
import cn.addenda.porttrail.agent.writer.db.AgentDbWriter;
import cn.addenda.porttrail.common.pojo.db.bo.DbConfigBo;
import cn.addenda.porttrail.infrastructure.entrypoint.EntryPointStackContext;
import cn.addenda.porttrail.infrastructure.log.PortTrailLogger;
import cn.addenda.porttrail.infrastructure.writer.DbWriter;
import cn.addenda.porttrail.jdbc.core.PortTrailConnection;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Properties;
import java.util.concurrent.Callable;

public class OracleDriverConnectInterceptor implements Interceptor {

  private final DbWriter dbWriter;

  private final PortTrailLogger portTrailConnectionPortTrailLogger;

  private static final PortTrailLogger log =
          AgentPortTrailLoggerFactory.getInstance().getPortTrailLogger(HikariConcurrentBagBorrowInterceptor.class);

  public OracleDriverConnectInterceptor() {
    this.dbWriter = AgentDbWriter.getInstance();
    this.portTrailConnectionPortTrailLogger = AgentPortTrailLoggerFactory.getInstance().getPortTrailLogger(PortTrailConnection.class);
  }

  /**
   * 被@RuntimeType标注的方法就是被委托的方法
   */
  @RuntimeType
  public Connection intercept(
          // byteBuddy会在运行期间给被注定注解修饰的方法参数进行赋值:

          // 当前被拦截的、动态生成的那个对象
          @This Object targetObj,
          // 被调用的原始方法
          @Origin Method targetMethod,
          // 被拦截的方法参数
          @AllArguments Object[] targetMethodArgs,
          // 当前被拦截的、动态生成的那个对象的父类对象
          @Super Object originalObj,
          // 用于调用父类的方法。
          @SuperCall Callable<?> zuper
  ) throws Exception {

    log.info("TargetObj is [{}] and it's classloader is [{}].", targetObj, targetObj.getClass().getClassLoader());

    Object call = zuper.call();

    if (call instanceof PortTrailConnection) {
      return (Connection) call;
    }

    PortTrailConnection portTrailConnection = new PortTrailConnection(
            (Connection) call, portTrailConnectionPortTrailLogger, dbWriter);

    DbConfigBo dbConfigBo = new DbConfigBo();
    dbConfigBo.setJdbcUrl((String) targetMethodArgs[0]);
    dbConfigBo.setUser(((Properties) targetMethodArgs[1]).getProperty("user"));
    dbConfigBo.setPassword(((Properties) targetMethodArgs[1]).getProperty("password"));
    dbConfigBo.setDataSourcePortTrailId(null);
    dbConfigBo.setConnectionPortTrailId(portTrailConnection.getPortTrailId());
    dbConfigBo.setDriverName(targetObj.getClass().getName());
    dbConfigBo.setStatementPortTrailId(null);
    dbConfigBo.setEntryPointSnapshot(EntryPointStackContext.snapshot());

    dbWriter.writeDbConfig(dbConfigBo);

    return portTrailConnection;
  }

  @Override
  public boolean ifOverride() {
    return false;
  }

}
