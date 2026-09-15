package cn.addenda.porttrail.agent.transform.interceptor.jdbc;

import cn.addenda.porttrail.agent.log.AgentPortTrailLoggerFactory;
import cn.addenda.porttrail.agent.transform.interceptor.AbstractDeduplicationEntryPointInterceptor;
import cn.addenda.porttrail.agent.transform.interceptor.Interceptor;
import cn.addenda.porttrail.agent.util.CachedField;
import cn.addenda.porttrail.agent.util.ReflectionUtils;
import cn.addenda.porttrail.common.entrypoint.EntryPoint;
import cn.addenda.porttrail.common.entrypoint.EntryPointType;
import cn.addenda.porttrail.common.pojo.db.bo.StatementExecutionBo;
import cn.addenda.porttrail.common.pojo.db.bo.StatementSql;
import cn.addenda.porttrail.infrastructure.log.PortTrailLogger;
import cn.addenda.porttrail.jdbc.bo.PortTrailPreparedStatementAttachment;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.Callable;

public class PortTrailStatementExecuteInterceptor extends AbstractDeduplicationEntryPointInterceptor implements Interceptor {

  private static final PortTrailLogger log =
          AgentPortTrailLoggerFactory.getInstance().getPortTrailLogger(PortTrailStatementExecuteInterceptor.class);

  private static final CachedField SQL_ATTACHMENT_FIELD =
          new CachedField("portTrailStatementSqlAttachment");

  private static final CachedField STASHED_EXECUTION_BO_FIELD =
          new CachedField("stashedStatementExecutionBo");

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
    String detail = Interceptor.assembleDetail(targetObj, targetMethod);

    if (log.isDebugEnabled()) {
      log.debug("Intercepted [{}], sql [{}].", detail, extractSql(targetObj, targetMethodArgs));
    }

    return callWithEntryPoint(detail, zuper);
  }

  @Override
  public boolean ifOverride() {
    return false;
  }

  @Override
  protected EntryPoint entryPoint(String detail) {
    return EntryPoint.of(EntryPointType.REMOTE_JDBC, detail);
  }

  /**
   * 取当前正在执行的 SQL，取不到返回 null。
   * <ol>
   *   <li>Statement 的 execute*(String sql) 系列：SQL 就是第一个参数</li>
   *   <li>addBatch(String) 之后的 executeBatch()：批次 SQL 累积在 stashedStatementExecutionBo 上</li>
   *   <li>PreparedStatement 的无参 execute*()：SQL 是构造时传入的 parameterizedSql</li>
   * </ol>
   * 返回值已归一为单行（见 {@link #toSingleLine(String)}）。
   * 本方法必须保证不抛异常：日志语句不得影响业务代码。
   */
  private static String extractSql(Object targetObj, Object[] args) {
    try {
      if (args != null && args.length > 0 && args[0] instanceof String) {
        return toSingleLine((String) args[0]);
      }

      Object attachment = getFieldValue(targetObj, SQL_ATTACHMENT_FIELD.get(targetObj));
      if (attachment == null) {
        return null;
      }

      Object stashed = getFieldValue(attachment, STASHED_EXECUTION_BO_FIELD.get(attachment));
      if (stashed instanceof StatementExecutionBo) {
        List<StatementSql> batchSqlList = ((StatementExecutionBo) stashed).getStatementSqlList();
        if (batchSqlList != null) {
          StringBuilder sb = new StringBuilder();
          for (StatementSql statementSql : batchSqlList) {
            if (statementSql == null || statementSql.getSql() == null) {
              continue;
            }
            if (sb.length() > 0) {
              sb.append("; ");
            }
            sb.append(statementSql.getSql());
          }
          if (sb.length() > 0) {
            return toSingleLine(sb.toString());
          }
        }
      }

      if (attachment instanceof PortTrailPreparedStatementAttachment) {
        return toSingleLine(((PortTrailPreparedStatementAttachment) attachment).getParameterizedSql());
      }
      return null;
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * 换行符替换为空格，保证 SQL 单行输出。
   * <p>
   * SQL 常是多行书写的（MyBatis XML 里的尤其如此），原样打进日志会把一条日志拆成多行，
   * 既破坏「一条事件一行」，也让 grep 难以按行匹配。
   * <p>
   * 不能替换成空串：那会把 {@code SELECT a\nFROM t} 粘成 {@code SELECT aFROM t}。
   */
  private static String toSingleLine(String sql) {
    if (sql == null) {
      return null;
    }
    return sql.replace("\r\n", " ").replace('\n', ' ').replace('\r', ' ');
  }

  private static Object getFieldValue(Object o, Field field) {
    return field == null ? null : ReflectionUtils.getFieldValueFromObject(o, field);
  }

  private static final ThreadLocal<Deque<String>> deduplicationStack = new ThreadLocal<Deque<String>>() {
    @Override
    public String toString() {
      return getClass().getName() + "@" + PortTrailStatementExecuteInterceptor.class.getName() + "@" + Integer.toHexString(hashCode());
    }
  };

  @Override
  protected ThreadLocal<Deque<String>> getDeduplicationStack() {
    return deduplicationStack;
  }

}
