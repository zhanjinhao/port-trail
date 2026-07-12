package cn.addenda.porttrail.jdbc.test.core;

import cn.addenda.porttrail.jdbc.core.WrapperAdapter;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Wrapper;

import static org.junit.jupiter.api.Assertions.*;

class WrapperAdapterTest {

  // ============================================================
  //  测试辅助子类
  // ============================================================

  /**
   * 包装一个非 Wrapper 的普通对象（String），验证 delegate 类型直接匹配的场景。
   */
  static class StringDelegate extends WrapperAdapter {
    private final String delegate;

    StringDelegate(String delegate) {
      this.delegate = delegate;
    }

    @Override
    protected Object getDelegate() {
      return delegate;
    }
  }

  /**
   * 包装另一个 WrapperAdapter，验证递归穿透（chain）的场景。
   */
  static class WrapperChain extends WrapperAdapter {
    private final Wrapper delegate;

    WrapperChain(Wrapper delegate) {
      this.delegate = delegate;
    }

    @Override
    protected Object getDelegate() {
      return delegate;
    }
  }

  /**
   * delegate 为 null，验证边界情况。
   */
  static class NullDelegate extends WrapperAdapter {
    @Override
    protected Object getDelegate() {
      return null;
    }
  }

  // ============================================================
  //  isWrapperFor 测试
  // ============================================================

  @Test
  void test_isWrapperFor_selfType_true() throws SQLException {
    StringDelegate sd = new StringDelegate("hello");

    assertTrue(sd.isWrapperFor(StringDelegate.class));
  }

  @Test
  void test_isWrapperFor_delegateType_true() throws SQLException {
    StringDelegate sd = new StringDelegate("hello");

    assertTrue(sd.isWrapperFor(String.class));
  }

  @Test
  void test_isWrapperFor_interface_true() throws SQLException {
    StringDelegate sd = new StringDelegate("hello");

    // StringDelegate extends WrapperAdapter implements Wrapper
    assertTrue(sd.isWrapperFor(Wrapper.class));
    assertTrue(sd.isWrapperFor(WrapperAdapter.class));
  }

  @Test
  void test_isWrapperFor_unrelatedType_false() throws SQLException {
    StringDelegate sd = new StringDelegate("hello");

    assertFalse(sd.isWrapperFor(Integer.class));
  }

  @Test
  void test_isWrapperFor_chain_penetratesRecursive_true() throws SQLException {
    // 外层 WrapperChain -> 内层 StringDelegate -> "hello"
    StringDelegate inner = new StringDelegate("hello");
    WrapperChain outer = new WrapperChain(inner);

    // 外层不匹配 String，内层也不匹配 String（内层是 StringDelegate），
    // 但内层是 Wrapper，其 delegate 是 String → 递归穿透
    assertTrue(outer.isWrapperFor(String.class));
  }

  @Test
  void test_isWrapperFor_chain_selfType_true() throws SQLException {
    StringDelegate inner = new StringDelegate("hello");
    WrapperChain outer = new WrapperChain(inner);

    assertTrue(outer.isWrapperFor(WrapperChain.class));
  }

  @Test
  void test_isWrapperFor_chain_unrelatedType_false() throws SQLException {
    StringDelegate inner = new StringDelegate("hello");
    WrapperChain outer = new WrapperChain(inner);

    assertFalse(outer.isWrapperFor(Integer.class));
  }

  @Test
  void test_isWrapperFor_nullDelegate_false() throws SQLException {
    NullDelegate nd = new NullDelegate();

    assertFalse(nd.isWrapperFor(String.class));
    // null delegate 不影响 self check
    assertTrue(nd.isWrapperFor(NullDelegate.class));
  }

  // ============================================================
  //  unwrap 测试
  // ============================================================

  @Test
  void test_unwrap_selfType_returnsThis() throws SQLException {
    StringDelegate sd = new StringDelegate("hello");

    assertSame(sd, sd.unwrap(StringDelegate.class));
  }

  @Test
  void test_unwrap_parentType_returnsThis() throws SQLException {
    StringDelegate sd = new StringDelegate("hello");

    assertSame(sd, sd.unwrap(WrapperAdapter.class));
    assertSame(sd, sd.unwrap(Wrapper.class));
  }

  @Test
  void test_unwrap_delegateType_returnsDelegate() throws SQLException {
    StringDelegate sd = new StringDelegate("hello");

    assertEquals("hello", sd.unwrap(String.class));
  }

  @Test
  void test_unwrap_chain_penetratesRecursive() throws SQLException {
    // 外层 WrapperChain -> 内层 StringDelegate -> "hello"
    StringDelegate inner = new StringDelegate("hello");
    WrapperChain outer = new WrapperChain(inner);

    assertEquals("hello", outer.unwrap(String.class));
  }

  @Test
  void test_unwrap_chain_selfType_returnsThis() throws SQLException {
    StringDelegate inner = new StringDelegate("hello");
    WrapperChain outer = new WrapperChain(inner);

    assertSame(outer, outer.unwrap(WrapperChain.class));
  }

  @Test
  void test_unwrap_chain_middleType_returnsMiddle() throws SQLException {
    // 外层 WrapperChain -> 内层 StringDelegate -> "hello"
    StringDelegate inner = new StringDelegate("hello");
    WrapperChain outer = new WrapperChain(inner);

    // 外层不匹配 StringDelegate，但内层 (delegate) instanceof StringDelegate
    // 命中规则2：delegate != null && iface.isInstance(delegate)
    assertSame(inner, outer.unwrap(StringDelegate.class));
  }

  @Test
  void test_unwrap_chain_deeplyNested() throws SQLException {
    // 三层：最外层 -> WrapperChain -> StringDelegate -> "hello"
    StringDelegate s1 = new StringDelegate("deep");
    WrapperChain c1 = new WrapperChain(s1);
    WrapperChain c2 = new WrapperChain(c1);

    // c2 -> c1 -> s1 -> "deep"
    // unwrap(String)：c2 不匹配，c1 不匹配，c1.isWrapperFor(String) → c1 递归到 s1
    assertEquals("deep", c2.unwrap(String.class));
  }

  @Test
  void test_unwrap_unrelatedType_throwsSQLException() {
    StringDelegate sd = new StringDelegate("hello");

    assertThrows(SQLException.class, () -> sd.unwrap(Integer.class));
  }

  @Test
  void test_unwrap_nullDelegate_throwsSQLException() {
    NullDelegate nd = new NullDelegate();

    // self check 通过，返回 this
    assertSame(nd, assertDoesNotThrow(() -> nd.unwrap(NullDelegate.class)));

    // self check 不通过，delegate 为 null，且 delegate 不是 Wrapper → 抛异常
    assertThrows(SQLException.class, () -> nd.unwrap(String.class));
  }

}
