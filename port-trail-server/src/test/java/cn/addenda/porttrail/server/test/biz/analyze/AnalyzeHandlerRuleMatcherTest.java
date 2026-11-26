package cn.addenda.porttrail.server.test.biz.analyze;

import cn.addenda.porttrail.server.biz.analyze.AnalyzeHandler;
import cn.addenda.porttrail.server.biz.analyze.AnalyzeHandlerRuleMatcher;
import cn.addenda.porttrail.server.bo.db.analyze.param.AnalyzePreparedStatementExecutionParam;
import cn.addenda.porttrail.server.bo.db.analyze.param.AnalyzeStatementExecutionParam;
import cn.addenda.porttrail.server.bo.db.analyze.result.AnalyzeResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class AnalyzeHandlerRuleMatcherTest {

  private static final String EXPLAIN = "AnalyzeExplainHandler";

  private static final String TABLE_NAME = "AnalyzeTableNameHandler";

  private static final String SELECT_ALL = "AnalyzeSelectAllHandler";

  private static final String DEDUP = "AnalyzeDedupHandler";

  private static List<AnalyzeHandler<?>> handlers() {
    return Arrays.asList(
            new StubAnalyzeHandler(EXPLAIN),
            new StubAnalyzeHandler(TABLE_NAME),
            new StubAnalyzeHandler(SELECT_ALL),
            new StubAnalyzeHandler(DEDUP));
  }

  private static AnalyzeHandlerRuleMatcher matcher(String ruleText) {
    return new AnalyzeHandlerRuleMatcher(ruleText, handlers());
  }

  private static List<String> names(List<AnalyzeHandler<?>> handlers) {
    return handlers.stream().map(AnalyzeHandler::handlerName).collect(Collectors.toList());
  }

  @Test
  void testNoRulesConfiguredSkipsAnalysis() {
    AnalyzeHandlerRuleMatcher matcher = matcher("");
    Assertions.assertTrue(matcher.effectiveHandlers("SYS", "svc", "prod").isEmpty());
  }

  @Test
  void testRuleFiltersHandlers() {
    AnalyzeHandlerRuleMatcher matcher = matcher(
            "*|*|prod|AnalyzeExplainHandler,AnalyzeDedupHandler");
    Assertions.assertEquals(Arrays.asList(EXPLAIN, DEDUP),
            names(matcher.effectiveHandlers("SYS", "svc", "prod")));
  }

  @Test
  void testHandlerNameIsCaseSensitive() {
    AnalyzeHandlerRuleMatcher lowercase = matcher("*|*|*|analyzeDedupHandler");
    Assertions.assertTrue(lowercase.effectiveHandlers("SYS", "svc", "prod").isEmpty());

    AnalyzeHandlerRuleMatcher exact = matcher("*|*|*|AnalyzeDedupHandler");
    Assertions.assertEquals(Arrays.asList(DEDUP),
            names(exact.effectiveHandlers("SYS", "svc", "prod")));
  }

  @Test
  void testFirstMatchingRuleWins() {
    AnalyzeHandlerRuleMatcher matcher = matcher(
            "SYS|svc|prod|AnalyzeDedupHandler;*|*|prod|AnalyzeExplainHandler");
    Assertions.assertEquals(Arrays.asList(DEDUP),
            names(matcher.effectiveHandlers("SYS", "svc", "prod")));
    Assertions.assertEquals(Arrays.asList(EXPLAIN),
            names(matcher.effectiveHandlers("OTHER", "svc", "prod")));
  }

  @Test
  void testNoRuleMatchesSkipsAnalysis() {
    AnalyzeHandlerRuleMatcher matcher = matcher(
            "SYS|svc|prod|AnalyzeDedupHandler");
    Assertions.assertTrue(matcher.effectiveHandlers("SYS", "svc", "sit").isEmpty());
  }

  @Test
  void testEachDimensionWildcard() {
    Assertions.assertEquals(Arrays.asList(DEDUP), names(matcher(
            "*|*|*|AnalyzeDedupHandler").effectiveHandlers("ANY", "ANY", "ANY")));
    Assertions.assertEquals(Arrays.asList(DEDUP), names(matcher(
            "SYS_*|*|*|AnalyzeDedupHandler").effectiveHandlers("SYS_A", "svc", "prod")));
    Assertions.assertTrue(matcher(
            "SYS_*|*|*|AnalyzeDedupHandler").effectiveHandlers("OTHER", "svc", "prod").isEmpty());
  }

  @Test
  void testEffectiveHandlersCachedPerRoute() {
    AnalyzeHandlerRuleMatcher matcher = matcher("*|*|prod|AnalyzeDedupHandler");

    List<AnalyzeHandler<?>> first = matcher.effectiveHandlers("SYS", "svc", "prod");
    List<AnalyzeHandler<?>> second = matcher.effectiveHandlers("SYS", "svc", "prod");
    // 同一 (systemCode, serviceName, env) 第二次直接命中缓存
    Assertions.assertSame(first, second);

    // 不同组合各自缓存
    List<AnalyzeHandler<?>> otherRoute = matcher.effectiveHandlers("SYS", "svc", "sit");
    Assertions.assertNotSame(first, otherRoute);
    Assertions.assertSame(otherRoute, matcher.effectiveHandlers("SYS", "svc", "sit"));
  }

  @Test
  void testEffectiveHandlersAreImmutable() {
    List<AnalyzeHandler<?>> effectiveHandlers = matcher("*|*|prod|AnalyzeDedupHandler")
            .effectiveHandlers("SYS", "svc", "prod");
    Assertions.assertThrows(UnsupportedOperationException.class,
            () -> effectiveHandlers.add(new StubAnalyzeHandler("Other")));
  }

  @Test
  void testConcreteRuleDoesNotMatchNullRouteValues() {
    // 规则不是纯星号：null 字段不参与匹配，规则不命中 → 不执行任何 handler
    AnalyzeHandlerRuleMatcher matcher = matcher("SYS|svc|prod|AnalyzeDedupHandler");

    Assertions.assertTrue(matcher.effectiveHandlers(null, null, null).isEmpty());
    Assertions.assertTrue(matcher.effectiveHandlers(null, "svc", "prod").isEmpty());
    Assertions.assertTrue(matcher.effectiveHandlers("SYS", null, "prod").isEmpty());
    Assertions.assertTrue(matcher.effectiveHandlers("SYS", "svc", null).isEmpty());

    // 三个字段都有值且完全匹配时才生效
    Assertions.assertEquals(Arrays.asList(DEDUP),
            names(matcher.effectiveHandlers("SYS", "svc", "prod")));
  }

  @Test
  void testNullRouteValuesMatchFullWildcardAndAreCached() {
    AnalyzeHandlerRuleMatcher matcher = matcher("*|*|*|AnalyzeDedupHandler");
    List<AnalyzeHandler<?>> first = matcher.effectiveHandlers(null, null, null);
    List<AnalyzeHandler<?>> second = matcher.effectiveHandlers(null, null, null);

    // 纯星号 pattern 是全匹配，null 也命中
    Assertions.assertEquals(Arrays.asList(DEDUP), names(first));
    Assertions.assertSame(first, second);
  }

  @Test
  void testInvalidRuleFailsFast() {
    Assertions.assertThrows(IllegalArgumentException.class,
            () -> new AnalyzeHandlerRuleMatcher("SYS|svc|prod", handlers()));
    Assertions.assertThrows(IllegalArgumentException.class,
            () -> new AnalyzeHandlerRuleMatcher("SYS|svc|prod|", handlers()));
  }

  private static class StubAnalyzeHandler implements AnalyzeHandler<AnalyzeResult> {

    private final String handlerName;

    private StubAnalyzeHandler(String handlerName) {
      this.handlerName = handlerName;
    }

    @Override
    public String handlerName() {
      return handlerName;
    }

    @Override
    public AnalyzeResult handle(AnalyzePreparedStatementExecutionParam analyzePreparedStatementExecutionParam) {
      return null;
    }

    @Override
    public AnalyzeResult handle(AnalyzeStatementExecutionParam analyzeStatementExecutionParam) {
      return null;
    }

    @Override
    public boolean canConsume(AnalyzeResult analyzeResult) {
      return false;
    }

    @Override
    public void consume(AnalyzeResult analyzeResult) {
    }
  }

}
