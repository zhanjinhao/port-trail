package cn.addenda.porttrail.agent;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * {@link PortTrailAgent#validateRouteValue} 的单元测试。
 *
 * <p>systemCode / serviceName / env 会作为 server 端 handler 路由规则的字段值，
 * 只允许 {@code [A-Za-z0-9_.@-]}。
 */
class PortTrailAgentRouteValueTest {

  @Test
  void testPlainValueAccepted() {
    PortTrailAgent.validateRouteValue("systemCode", "AUTH-CORE");
    PortTrailAgent.validateRouteValue("serviceName", "auth_core_v2");
    PortTrailAgent.validateRouteValue("serviceName", "auth.core.v2");
    PortTrailAgent.validateRouteValue("serviceName", "svc@v1");
    PortTrailAgent.validateRouteValue("env", "prod");
    PortTrailAgent.validateRouteValue("env", "SIT_1-TEST");
    PortTrailAgent.validateRouteValue("env", "0123456789");
  }

  @Test
  void testNullAndEmptyAccepted() {
    // 未配置：调用方随后会用 UNKNOWN_* 默认值补齐
    PortTrailAgent.validateRouteValue("env", null);
    PortTrailAgent.validateRouteValue("env", "");
  }

  @Test
  void testRouteReservedCharsRejected() {
    assertRejected("systemCode", "AUTH*CORE", '*');
    assertRejected("systemCode", "AUTH|CORE", '|');
    assertRejected("systemCode", "AUTH;CORE", ';');
  }

  @Test
  void testNonWhitelistCharsRejected() {
    assertRejected("serviceName", "with space", ' ');
    assertRejected("serviceName", "a/b", '/');
    assertRejected("serviceName", "a:b", ':');
    assertRejected("serviceName", "订单服务", '订');
    assertRejected("env", "prod=", '=');
    assertRejected("env", "prod,x", ',');
  }

  @Test
  void testIllegalCharPositionDoesNotMatter() {
    assertRejected("env", ":prod", ':');
    assertRejected("env", "pro:d", ':');
    assertRejected("env", "prod:", ':');
  }

  private static void assertRejected(String name, String value, char illegalChar) {
    PortTrailAgentBootstrapException exception = Assertions.assertThrows(
            PortTrailAgentBootstrapException.class,
            () -> PortTrailAgent.validateRouteValue(name, value));
    String message = exception.getMessage();
    Assertions.assertTrue(message.contains(name), "异常信息应包含字段名: " + message);
    Assertions.assertTrue(message.contains(value), "异常信息应包含字段值: " + message);
    Assertions.assertTrue(message.contains(String.valueOf(illegalChar)),
            "异常信息应包含非法字符[" + illegalChar + "]: " + message);
    Assertions.assertTrue(message.contains("[A-Za-z0-9_.@-]"),
            "异常信息应包含合法字符集: " + message);
  }

}
