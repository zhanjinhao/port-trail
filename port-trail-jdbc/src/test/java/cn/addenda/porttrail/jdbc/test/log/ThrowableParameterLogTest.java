package cn.addenda.porttrail.jdbc.test.log;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 Throwable 以「格式化参数」的形式传给 log4j2 时，异常栈会不会被输出。
 *
 * <p>背景：{@code PortTrailLogger} 只有 {@code (String msg)} 和 {@code (String format, Object... arguments)}
 * 两种签名，没有 {@code (String msg, Throwable t)}。所以
 * {@code log.error("Failed to process ... callback", e)} 里的 {@code e} 会走
 * {@code (String, Object)}，被当作格式化参数传下去，而那条消息里并没有 {@code {}} 占位符。
 *
 * <p>本测试复现 {@code PortTrailLinkLogFacadeImpl} 对 log4j2 的调用方式：它把收到的
 * format/arguments 原样透传给 {@code Logger#logIfEnabled}，所以这里直接按各调用形态
 * 调用同一个方法，即可判定异常栈是否落到最终输出。
 *
 * <p>覆盖的形态与生产调用点对应关系：
 * <ul>
 *   <li>{@code (String, Object)} —— 2 参数调用点，如
 *       {@code LettuceCommandCompleteInterceptor} 的
 *       {@code log.error("Failed to process Redis command complete callback", e)}</li>
 *   <li>{@code (String, Object, Object)} —— 3 参数调用点，如
 *       {@code PortTrailHttpRequestInterceptor} 的
 *       {@code log.error("unexpected error, requestLine: [{}].", requestLine, t)}</li>
 *   <li>{@code (String, Object...)} —— 重构前的 varargs 形态</li>
 *   <li>{@code (String, Throwable)} —— 给 PortTrailLogger 补重载后会走的形态</li>
 * </ul>
 */
class ThrowableParameterLogTest {

  /**
   * 与 dist/link/log/log4j2.xml 的 LOG_PATTERN 一致。
   * 注意 pattern 里没有 %throwable，是否输出异常栈完全依赖 PatternLayout 的 alwaysWriteExceptions 兜底。
   */
  private static final String LOG_PATTERN =
          "%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level [%class.%method.%L] - %msg%n";

  /**
   * PortTrailLinkLogFacadeImpl 传给 log4j2 的 fqcn。
   * 用字符串常量而不是 {@code Log4jLogger.FQCN}，避免为测试引入 log4j-slf4j-impl 依赖。
   */
  private static final String FQCN = "org.apache.logging.slf4j.Log4jLogger";

  private LoggerContext loggerContext;

  private CapturingAppender capturer;

  private Logger logger;

  @BeforeEach
  void setUp() {
    loggerContext = new LoggerContext("ThrowableParameterLogTest");
    // LoggerContext 默认配置（DefaultConfiguration）的 root 上挂了一个 ConsoleAppender，
    // 不摘掉的话每条日志都会打到控制台，一个通过的测试不该刷异常栈。
    // 先拷贝再删，避免边遍历边改同一张 map。
    List<Appender> defaultAppenders =
            new ArrayList<>(loggerContext.getConfiguration().getAppenders().values());
    defaultAppenders.forEach(loggerContext.getRootLogger()::removeAppender);
    PatternLayout layout = PatternLayout.newBuilder()
            .withConfiguration(loggerContext.getConfiguration())
            .withPattern(LOG_PATTERN)
            .build();
    capturer = new CapturingAppender(layout);
    capturer.start();
    loggerContext.getConfiguration().addAppender(capturer);
    loggerContext.getRootLogger().addAppender(capturer);
    loggerContext.getRootLogger().setLevel(Level.ALL);
    logger = loggerContext.getLogger("porttrail.test");
  }

  @AfterEach
  void tearDown() {
    loggerContext.stop();
  }

  /**
   * 对应 PortTrailLinkLogFacadeImpl#error(String, Object)。
   * 生产用法：log.error("Failed to process Redis command complete callback", e)
   */
  @Test
  @DisplayName("(String, Object) 且消息无占位符")
  void fixedArityOneArg() {
    Object arg = new IllegalStateException("boom-2arg");
    logger.logIfEnabled(FQCN, Level.ERROR, null,
            "Failed to process Redis command complete callback", arg);
    assertStackPrinted("(String, Object)，消息无 {}", "fixedArityOneArg");
  }

  /**
   * 对应 PortTrailLinkLogFacadeImpl#error(String, Object, Object)。
   * 生产用法：log.error("unexpected error, requestLine: [{}].", requestLine, t)
   */
  @Test
  @DisplayName("(String, Object, Object) 且消息有 1 个占位符，Throwable 在末位")
  void fixedArityTwoArgs() {
    Object arg1 = "POST /demo HTTP/1.1";
    Object arg2 = new IllegalStateException("boom-3arg");
    logger.logIfEnabled(FQCN, Level.ERROR, null,
            "unexpected error, requestLine: [{}].", arg1, arg2);
    assertStackPrinted("(String, Object, Object)，消息有 1 个 {}", "fixedArityTwoArgs");
  }

  /**
   * 对应 PortTrailLinkLogFacadeImpl#error(String, Object...)。
   * 重构前的调用形态：Throwable 作为唯一的 vararg 元素。
   */
  @Test
  @DisplayName("(String, Object...) varargs，Throwable 作为唯一可变参数")
  void varargsTrailingThrowable() {
    Object[] arguments = new Object[]{new IllegalStateException("boom-varargs")};
    logger.logIfEnabled(FQCN, Level.ERROR, null,
            "Failed to process Redis command complete callback", arguments);
    assertStackPrinted("(String, Object...)，Throwable 作为唯一 vararg", "varargsTrailingThrowable");
  }

  /**
   * 给 PortTrailLogger 补 (String, Throwable) 重载后，走的就是 log4j2 的这个重载。
   * 与上面几条对比即可看出差异；若上面已通过，则本重载并非必需。
   */
  @Test
  @DisplayName("(String, Throwable) 显式异常参数")
  void throwableOverload() {
    Throwable t = new IllegalStateException("boom-throwable");
    logger.logIfEnabled(FQCN, Level.ERROR, null,
            "Failed to process Redis command complete callback", t);
    assertStackPrinted("(String, Throwable)", "throwableOverload");
  }

  /**
   * 断言：渲染结果里出现了异常消息、至少一个栈帧，且栈帧确实来自本测试方法
   * （最后一条用于排除「断言到的其实是别处输出的栈」）。
   * 失败信息里带上实际渲染结果和 LogEvent 上是否挂了 Throwable，便于定位。
   */
  private void assertStackPrinted(String shape, String callingMethod) {
    List<String> lines = capturer.rendered;
    assertFalse(lines.isEmpty(), shape + "：没有产生任何日志");

    String output = String.join("", lines);
    String diagnostic = String.format(
            "%n调用形态：%s%nLogEvent.getThrown() = %s%n实际渲染结果：%n%s",
            shape, capturer.thrownFlags, output);

    assertTrue(output.contains("boom-"), shape + "：异常消息没出现在日志里。" + diagnostic);
    assertTrue(output.contains("\tat "), shape + "：异常栈没有被输出。" + diagnostic);
    assertTrue(output.contains("ThrowableParameterLogTest." + callingMethod),
            shape + "：栈帧不是来自本测试方法，断言的可能不是被测异常的栈。" + diagnostic);
  }

  /**
   * 把渲染结果和 LogEvent 上是否挂了 Throwable 都收集起来。
   */
  private static class CapturingAppender extends AbstractAppender {

    private final List<String> rendered = new CopyOnWriteArrayList<>();

    private final List<String> thrownFlags = new CopyOnWriteArrayList<>();

    private CapturingAppender(PatternLayout layout) {
      super("capture", null, layout, false, null);
    }

    @Override
    public void append(LogEvent event) {
      thrownFlags.add(String.valueOf(event.getThrown()));
      rendered.add(new String(getLayout().toByteArray(event), StandardCharsets.UTF_8));
    }
  }

}
