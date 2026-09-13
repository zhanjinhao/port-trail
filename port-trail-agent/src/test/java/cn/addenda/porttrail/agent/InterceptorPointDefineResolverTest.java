package cn.addenda.porttrail.agent;

import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPoint;
import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPointDefine;
import cn.addenda.porttrail.agent.transform.match.ClassMatch;
import cn.addenda.porttrail.agent.transform.match.NameMatch;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

class InterceptorPointDefineResolverTest {

  private static final String TOMCAT =
          "cn.addenda.porttrail.agent.transform.interceptor.server.tomcat.TomcatAbstractProtocolInterceptorPointDefine";
  private static final String JETTY =
          "cn.addenda.porttrail.agent.transform.interceptor.server.jetty.JettyServerInterceptorPointDefine";
  private static final String JAKARTA =
          "cn.addenda.porttrail.agent.transform.interceptor.servlet.jakarta.JakartaServletInterceptorPointDefine";

  // ==================== 1. 正常解析 ====================

  @Test
  void testResolve_normal() {
    List<InterceptorPointDefine> list =
            PortTrailAgent.resolveInterceptorPointDefineList(TOMCAT + "," + JETTY);

    Assertions.assertEquals(Arrays.asList(TOMCAT, JETTY), names(list));
  }

  @Test
  void testResolve_trimAndSkipBlankToken() {
    List<InterceptorPointDefine> list =
            PortTrailAgent.resolveInterceptorPointDefineList(" " + TOMCAT + " ,, " + JETTY + ", ");

    Assertions.assertEquals(Arrays.asList(TOMCAT, JETTY), names(list));
  }

  @Test
  void testResolve_orderPreserved() {
    List<InterceptorPointDefine> list =
            PortTrailAgent.resolveInterceptorPointDefineList(JETTY + "," + TOMCAT);

    Assertions.assertEquals(Arrays.asList(JETTY, TOMCAT), names(list));
  }

  // ==================== 2. 解析失败快速失败 ====================

  @Test
  void testResolve_classNotFound() {
    String typo = TOMCAT + "X";
    PortTrailAgentBootstrapException e = Assertions.assertThrows(PortTrailAgentBootstrapException.class,
            () -> PortTrailAgent.resolveInterceptorPointDefineList(typo));

    Assertions.assertTrue(e.getMessage().contains(typo), e.getMessage());
    Assertions.assertInstanceOf(ClassNotFoundException.class, e.getCause());
  }

  /**
   * JakartaServletInterceptorPointDefine整个类被注释掉，写进配置必须快速失败而不是静默跳过。
   */
  @Test
  void testResolve_jakartaIsCommentedOut() {
    PortTrailAgentBootstrapException e = Assertions.assertThrows(PortTrailAgentBootstrapException.class,
            () -> PortTrailAgent.resolveInterceptorPointDefineList(JAKARTA));

    Assertions.assertTrue(e.getMessage().contains(JAKARTA), e.getMessage());
  }

  @Test
  void testResolve_notAnInterceptorPointDefine() {
    PortTrailAgentBootstrapException e = Assertions.assertThrows(PortTrailAgentBootstrapException.class,
            () -> PortTrailAgent.resolveInterceptorPointDefineList("java.lang.String"));

    Assertions.assertTrue(e.getMessage().contains("未实现InterceptorPointDefine接口"), e.getMessage());
  }

  @Test
  void testResolve_noNoArgConstructor() {
    PortTrailAgentBootstrapException e = Assertions.assertThrows(PortTrailAgentBootstrapException.class,
            () -> PortTrailAgent.resolveInterceptorPointDefineList(NoNoArgCtorDefine.class.getName()));

    Assertions.assertTrue(e.getMessage().contains("无法通过public无参构造函数实例化"), e.getMessage());
  }

  @Test
  void testResolve_duplicateClassName() {
    PortTrailAgentBootstrapException e = Assertions.assertThrows(PortTrailAgentBootstrapException.class,
            () -> PortTrailAgent.resolveInterceptorPointDefineList(TOMCAT + "," + JETTY + "," + TOMCAT));

    Assertions.assertTrue(e.getMessage().contains("重复"), e.getMessage());
    Assertions.assertTrue(e.getMessage().contains(TOMCAT), e.getMessage());
  }

  @Test
  void testResolve_noValidClassName() {
    PortTrailAgentBootstrapException e = Assertions.assertThrows(PortTrailAgentBootstrapException.class,
            () -> PortTrailAgent.resolveInterceptorPointDefineList(",,  ,"));

    Assertions.assertTrue(e.getMessage().contains("没有有效的类名"), e.getMessage());
  }

  // ==================== 3. 内置默认清单 ====================

  /**
   * 兜底清单必须与历史行为一致，数量与顺序都不能变。
   */
  @Test
  void testDefaultList_matchesHistoricalBehaviour() {
    List<InterceptorPointDefine> list =
            PortTrailAgent.resolveInterceptorPointDefineList(PortTrailAgent.defaultInterceptorPointDefineImpl());

    Assertions.assertEquals(expectedDefaultClassNames(), names(list));
  }

  // ==================== 4. 随包发布的agent.properties ====================

  /**
   * 校验随包发布的agent.properties能被正确解析成完整的默认清单。
   * 续行末尾漏写反斜杠会导致配置值被静默截断，这个用例专门守住该风险。
   */
  @Test
  void testShippedAgentProperties_parsesToFullDefaultList() throws Exception {
    Properties properties = new Properties();
    try (InputStream in = InterceptorPointDefineResolverTest.class.getClassLoader()
            .getResourceAsStream("agent.properties")) {
      Assertions.assertNotNull(in, "agent.properties is not on the test classpath.");
      properties.load(in);
    }

    String property = properties.getProperty("interceptorPointDefine.impl");
    Assertions.assertNotNull(property, "interceptorPointDefine.impl is missing from agent.properties.");

    Assertions.assertEquals(expectedDefaultClassNames(),
            names(PortTrailAgent.resolveInterceptorPointDefineList(property)));
  }

  // ==================== helpers ====================

  private static List<String> names(List<InterceptorPointDefine> list) {
    List<String> names = new ArrayList<>();
    for (InterceptorPointDefine interceptorPointDefine : list) {
      names.add(interceptorPointDefine.getClass().getName());
    }
    return names;
  }

  private static List<String> expectedDefaultClassNames() {
    return Arrays.asList(
            "cn.addenda.porttrail.agent.transform.interceptor.server.tomcat.TomcatAbstractProtocolInterceptorPointDefine",
            "cn.addenda.porttrail.agent.transform.interceptor.server.jetty.JettyServerInterceptorPointDefine",
            "cn.addenda.porttrail.agent.transform.interceptor.servlet.javax.JavaxServletInterceptorPointDefine",
            "cn.addenda.porttrail.agent.transform.interceptor.task.TaskInterceptorPointDefine",
            "cn.addenda.porttrail.agent.transform.interceptor.job.xxl.jobhandler.XxlJobHandlerInterceptorPointDefine",
            "cn.addenda.porttrail.agent.transform.interceptor.job.xxl.method.XxlMethodInterceptorPointDefine",
            "cn.addenda.porttrail.agent.transform.interceptor.job.xxl.glue.XxlGlueInterceptorPointDefine",
            "cn.addenda.porttrail.agent.transform.interceptor.job.xxl.script.XxlScriptInterceptorPointDefine",
            "cn.addenda.porttrail.agent.transform.interceptor.tx.transactional.SpringTransactionalInterceptorPointDefine",
            "cn.addenda.porttrail.agent.transform.interceptor.tx.transactionhepler.SpringTransactionHelperInterceptorPointDefine",
            "cn.addenda.porttrail.agent.transform.interceptor.tx.transactiontemplate.SpringTransactionTemplateInterceptorPointDefine",
            "cn.addenda.porttrail.agent.transform.interceptor.mybatis.MybatisExecutorInterceptorPointDefine",
            "cn.addenda.porttrail.agent.transform.interceptor.datasource.hikari.HikariConcurrentBagInterceptorPointDefine",
            "cn.addenda.porttrail.agent.transform.interceptor.datasource.druid.DruidDruidDataSourceInterceptorPointDefine",
            "cn.addenda.porttrail.agent.transform.interceptor.jdbc.PortTrailStatementInterceptorPointDefine",
            "cn.addenda.porttrail.agent.transform.interceptor.driver.mysql.MySQLDriverInterceptorPointDefine",
            "cn.addenda.porttrail.agent.transform.interceptor.driver.oracle.OracleDriverInterceptorPointDefine",
            "cn.addenda.porttrail.agent.transform.interceptor.http.httpclient4.HttpClient4HttpClientBuilderInterceptorPointDefine",
            "cn.addenda.porttrail.agent.transform.interceptor.redis.lettuce.peer.LettuceDefaultEndpointInterceptorPointDefine",
            "cn.addenda.porttrail.agent.transform.interceptor.redis.lettuce.command.LettuceCommandInterceptorPointDefine",
            "cn.addenda.porttrail.agent.transform.interceptor.redis.lettuce.channelwriter.LettuceChannelWriterInterceptorPointDefine"
    );
  }

  // ==================== test fixtures ====================

  /**
   * 只有(String)构造函数，用于验证缺少public无参构造函数时的快速失败。
   */
  static class NoNoArgCtorDefine implements InterceptorPointDefine {

    NoNoArgCtorDefine(String ignored) {
    }

    @Override
    public ClassMatch getEnhancedClass() {
      return NameMatch.of("java.lang.Object");
    }

    @Override
    public List<InterceptorPoint> getInterceptorPointList() {
      return new ArrayList<>();
    }
  }

}
