package cn.addenda.porttrail.jdbc.core;

import java.sql.SQLException;
import java.sql.Wrapper;

/**
 * copy from sharding-jdbc project
 *
 * @author addenda
 * @since 2024/4/16 11:34
 */
public abstract class WrapperAdapter implements Wrapper {

  // 新增：子类实现此方法返回被包装的 delegate 对象
  protected abstract Object getDelegate();

  /**
   * 当前对象强转为 iface。
   */
  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    // 1. 先检查自身
    if (iface.isInstance(this)) {
      return (T) this;
    }
    // 2. 检查 delegate 是否匹配
    Object delegate = getDelegate();
    if (delegate != null && iface.isInstance(delegate)) {
      return (T) delegate;
    }
    // 3. 如果 delegate 本身也是 Wrapper，递归委托
    if (delegate instanceof Wrapper) {
      try {
        return ((Wrapper) delegate).unwrap(iface);
      } catch (SQLException e) {
        // delegate 也无法 unwrap，继续抛统一错误
      }
    }
    throw new SQLException(String.format("[%s] cannot be unwrapped as [%s]", getClass().getName(), iface.getName()));
  }

  /**
   * 当前对象是否是 iface 的实例。<br/>
   * 是： true <br/>
   * 否： false
   */
  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    // 1. 先检查自身
    if (iface.isInstance(this)) {
      return true;
    }
    // 2. 检查 delegate 是否匹配
    Object delegate = getDelegate();
    if (delegate != null && iface.isInstance(delegate)) {
      return true;
    }
    // 3. 如果 delegate 本身也是 Wrapper，递归委托
    if (delegate instanceof Wrapper) {
      return ((Wrapper) delegate).isWrapperFor(iface);
    }
    return false;
  }

}
