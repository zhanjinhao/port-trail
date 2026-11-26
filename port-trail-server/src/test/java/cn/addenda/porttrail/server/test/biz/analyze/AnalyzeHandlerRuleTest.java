package cn.addenda.porttrail.server.test.biz.analyze;

import cn.addenda.porttrail.server.biz.analyze.AnalyzeHandlerRule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

class AnalyzeHandlerRuleTest {

  private static AnalyzeHandlerRule parseOne(String ruleText) {
    List<AnalyzeHandlerRule> rules = AnalyzeHandlerRule.parse(ruleText);
    Assertions.assertEquals(1, rules.size());
    return rules.get(0);
  }

  // ---------------- parse ----------------

  @Test
  void testParseNullOrBlankReturnsEmpty() {
    Assertions.assertTrue(AnalyzeHandlerRule.parse(null).isEmpty());
    Assertions.assertTrue(AnalyzeHandlerRule.parse("").isEmpty());
    Assertions.assertTrue(AnalyzeHandlerRule.parse("   ").isEmpty());
    Assertions.assertTrue(AnalyzeHandlerRule.parse(";;;").isEmpty());
  }

  @Test
  void testParseSingleRule() {
    AnalyzeHandlerRule rule = parseOne("SYS|svc|prod|AnalyzeExplainHandler,AnalyzeDedupHandler");

    Assertions.assertEquals("SYS", rule.getSystemCodePattern());
    Assertions.assertEquals("svc", rule.getServiceNamePattern());
    Assertions.assertEquals("prod", rule.getEnvPattern());
    Assertions.assertEquals(Arrays.asList("AnalyzeExplainHandler", "AnalyzeDedupHandler"),
            rule.getHandlerNames());
  }

  @Test
  void testParseTrimsWhitespace() {
    AnalyzeHandlerRule rule = parseOne(" SYS | svc | prod | AnalyzeExplainHandler , AnalyzeDedupHandler ");

    Assertions.assertEquals("SYS", rule.getSystemCodePattern());
    Assertions.assertEquals("svc", rule.getServiceNamePattern());
    Assertions.assertEquals("prod", rule.getEnvPattern());
    Assertions.assertEquals(Arrays.asList("AnalyzeExplainHandler", "AnalyzeDedupHandler"),
            rule.getHandlerNames());
  }

  @Test
  void testParseIgnoresBlankSegments() {
    List<AnalyzeHandlerRule> rules = AnalyzeHandlerRule.parse(
            ";SYS|svc|prod|A;;OTHER|svc|prod|B;");
    Assertions.assertEquals(2, rules.size());
    Assertions.assertEquals("SYS", rules.get(0).getSystemCodePattern());
    Assertions.assertEquals("OTHER", rules.get(1).getSystemCodePattern());
  }

  @Test
  void testParsePreservesRuleOrder() {
    List<AnalyzeHandlerRule> rules = AnalyzeHandlerRule.parse(
            "A|s1|e1|H1;B|s2|e2|H2;C|s3|e3|H3");
    Assertions.assertEquals(Arrays.asList("A", "B", "C"),
            Arrays.asList(rules.get(0).getSystemCodePattern(),
                    rules.get(1).getSystemCodePattern(),
                    rules.get(2).getSystemCodePattern()));
  }

  @Test
  void testParseIgnoresBlankHandlerNames() {
    AnalyzeHandlerRule rule = parseOne("SYS|svc|prod|A, ,B,");
    Assertions.assertEquals(Arrays.asList("A", "B"), rule.getHandlerNames());
  }

  @Test
  void testParseInvalidFieldCountFailsFast() {
    Assertions.assertThrows(IllegalArgumentException.class,
            () -> AnalyzeHandlerRule.parse("SYS|svc|prod"));
    Assertions.assertThrows(IllegalArgumentException.class,
            () -> AnalyzeHandlerRule.parse("SYS|svc|prod|A|extra"));
  }

  @Test
  void testParseEmptyHandlerListFailsFast() {
    Assertions.assertThrows(IllegalArgumentException.class,
            () -> AnalyzeHandlerRule.parse("SYS|svc|prod|"));
    Assertions.assertThrows(IllegalArgumentException.class,
            () -> AnalyzeHandlerRule.parse("SYS|svc|prod| , "));
  }

  @Test
  void testToStringUsesConfigFormat() {
    // 输出格式与配置一致：systemCode|serviceName|env|handler1,handler2（解析后已 trim）
    AnalyzeHandlerRule rule = parseOne(
            " SYS | svc | prod | AnalyzeExplainHandler , AnalyzeDedupHandler ");
    Assertions.assertEquals("SYS|svc|prod|AnalyzeExplainHandler,AnalyzeDedupHandler",
            rule.toString());
  }

  // ---------------- matches ----------------

  @Test
  void testMatchesExact() {
    AnalyzeHandlerRule rule = parseOne("SYS|svc|prod|A");
    Assertions.assertTrue(rule.matches("SYS", "svc", "prod"));
  }

  @Test
  void testMatchesEachDimensionMismatch() {
    AnalyzeHandlerRule rule = parseOne("SYS|svc|prod|A");
    Assertions.assertFalse(rule.matches("OTHER", "svc", "prod"));
    Assertions.assertFalse(rule.matches("SYS", "other", "prod"));
    Assertions.assertFalse(rule.matches("SYS", "svc", "sit"));
  }

  @Test
  void testMatchingIsCaseSensitive() {
    AnalyzeHandlerRule rule = parseOne("SYS|svc|prod|A");
    Assertions.assertFalse(rule.matches("sys", "svc", "prod"));
  }

  @Test
  void testFullWildcardInEachDimension() {
    Assertions.assertTrue(parseOne("*|*|*|A").matches("ANY", "ANY", "ANY"));
    Assertions.assertTrue(parseOne("*|svc|*|A").matches("ANY", "svc", "ANY"));
    Assertions.assertFalse(parseOne("*|svc|*|A").matches("ANY", "other", "ANY"));
    Assertions.assertTrue(parseOne("SYS|*|prod|A").matches("SYS", "ANY", "prod"));
    Assertions.assertTrue(parseOne("SYS|svc|*|A").matches("SYS", "svc", "ANY"));
  }

  @Test
  void testPartialWildcard() {
    Assertions.assertTrue(parseOne("SYS_*|*|*|A").matches("SYS_A", "svc", "prod"));
    Assertions.assertFalse(parseOne("SYS_*|*|*|A").matches("OTHER_A", "svc", "prod"));
    Assertions.assertTrue(parseOne("*|svc-*|*|A").matches("SYS", "svc-order", "prod"));
    Assertions.assertTrue(parseOne("*|*|*-prod|A").matches("SYS", "svc", "any-prod"));
    Assertions.assertTrue(parseOne("S*S|*|*|A").matches("SYS", "svc", "prod"));
    Assertions.assertFalse(parseOne("S*S|*|*|A").matches("SAX", "svc", "prod"));
  }

  @Test
  void testFullWildcardMatchesNull() {
    // * / ** / *** 都是全匹配，null 也命中
    Assertions.assertTrue(parseOne("*|*|*|A").matches(null, null, null));
    Assertions.assertTrue(parseOne("*|*|prod|A").matches(null, null, "prod"));
    Assertions.assertFalse(parseOne("*|*|prod|A").matches(null, null, "sit"));
    // 空串是「有值」，* 也可以匹配
    Assertions.assertTrue(parseOne("SYS|*|*|A").matches("SYS", "", ""));
  }

  @Test
  void testPatternIsMatchedLiterally() {
    // '.'、'+' 等正则元字符必须按字面量处理
    Assertions.assertTrue(parseOne("SYS.A|*|*|A").matches("SYS.A", "svc", "prod"));
    Assertions.assertFalse(parseOne("SYS.A|*|*|A").matches("SYSXA", "svc", "prod"));
    Assertions.assertTrue(parseOne("a+b|*|*|A").matches("a+b", "svc", "prod"));
  }

  // ---------------- containsHandler ----------------

  @Test
  void testContainsHandlerIsCaseSensitive() {
    AnalyzeHandlerRule rule = parseOne("SYS|svc|prod|AnalyzeExplainHandler,AnalyzeDedupHandler");

    Assertions.assertTrue(rule.containsHandler("AnalyzeExplainHandler"));
    Assertions.assertTrue(rule.containsHandler("AnalyzeDedupHandler"));
    Assertions.assertFalse(rule.containsHandler("analyzeexplainhandler"));
    Assertions.assertFalse(rule.containsHandler("ANALYZEDEDUPHANDLER"));
    Assertions.assertFalse(rule.containsHandler("AnalyzeTableNameHandler"));
  }

  // ---------------- wildcardMatch ----------------

  @Test
  void testNullPatternRejected() {
    Assertions.assertThrows(NullPointerException.class,
            () -> AnalyzeHandlerRule.wildcardMatch(null, "SYS"));
    Assertions.assertThrows(NullPointerException.class,
            () -> AnalyzeHandlerRule.wildcardMatch(null, null));
  }

  @Test
  void testSingleStarMatchesAnything() {
    Assertions.assertTrue(AnalyzeHandlerRule.wildcardMatch("*", "SYS"));
    Assertions.assertTrue(AnalyzeHandlerRule.wildcardMatch("*", ""));
    Assertions.assertTrue(AnalyzeHandlerRule.wildcardMatch("*", "任意 值"));
    Assertions.assertTrue(AnalyzeHandlerRule.wildcardMatch("*", null));
  }

  @Test
  void testExactMatch() {
    Assertions.assertTrue(AnalyzeHandlerRule.wildcardMatch("SYS", "SYS"));
    Assertions.assertFalse(AnalyzeHandlerRule.wildcardMatch("SYS", "sys"));
    Assertions.assertFalse(AnalyzeHandlerRule.wildcardMatch("SYS", "SYSX"));
    Assertions.assertFalse(AnalyzeHandlerRule.wildcardMatch("SYS", "XSYS"));
  }

  @Test
  void testNullValueMatchesOnlyFullWildcard() {
    Assertions.assertFalse(AnalyzeHandlerRule.wildcardMatch("SYS", null));
    Assertions.assertTrue(AnalyzeHandlerRule.wildcardMatch("*", null));
    Assertions.assertFalse(AnalyzeHandlerRule.wildcardMatch("", null));
    Assertions.assertTrue(AnalyzeHandlerRule.wildcardMatch("**", null));
    Assertions.assertTrue(AnalyzeHandlerRule.wildcardMatch("***", null));
  }

  @Test
  void testEmptyPattern() {
    Assertions.assertTrue(AnalyzeHandlerRule.wildcardMatch("", ""));
    Assertions.assertFalse(AnalyzeHandlerRule.wildcardMatch("", "A"));
  }

  @Test
  void testTrailingStar() {
    Assertions.assertTrue(AnalyzeHandlerRule.wildcardMatch("SYS*", "SYS"));
    Assertions.assertTrue(AnalyzeHandlerRule.wildcardMatch("SYS*", "SYS_A"));
    Assertions.assertTrue(AnalyzeHandlerRule.wildcardMatch("SYS*", "SYS-A"));
    Assertions.assertFalse(AnalyzeHandlerRule.wildcardMatch("SYS*", "X_SYS"));
  }

  @Test
  void testLeadingStar() {
    Assertions.assertTrue(AnalyzeHandlerRule.wildcardMatch("*SYS", "SYS"));
    Assertions.assertTrue(AnalyzeHandlerRule.wildcardMatch("*SYS", "A_SYS"));
    Assertions.assertFalse(AnalyzeHandlerRule.wildcardMatch("*SYS", "SYS_A"));
  }

  @Test
  void testMiddleStar() {
    Assertions.assertTrue(AnalyzeHandlerRule.wildcardMatch("S*S", "SYS"));
    Assertions.assertTrue(AnalyzeHandlerRule.wildcardMatch("S*S", "SS"));
    Assertions.assertTrue(AnalyzeHandlerRule.wildcardMatch("S*S", "SxxS"));
    Assertions.assertFalse(AnalyzeHandlerRule.wildcardMatch("S*S", "SAX"));
    Assertions.assertFalse(AnalyzeHandlerRule.wildcardMatch("S*S", "S"));
  }

  @Test
  void testConsecutiveStars() {
    // 连续 * 等价于一个 *
    Assertions.assertTrue(AnalyzeHandlerRule.wildcardMatch("A**B", "AB"));
    Assertions.assertTrue(AnalyzeHandlerRule.wildcardMatch("A**B", "AxxB"));
    // 纯星号之间语义一致，且都能匹配 null
    Assertions.assertTrue(AnalyzeHandlerRule.wildcardMatch("**", null));
    Assertions.assertTrue(AnalyzeHandlerRule.wildcardMatch("***", null));
    Assertions.assertTrue(AnalyzeHandlerRule.wildcardMatch("***", ""));
    Assertions.assertTrue(AnalyzeHandlerRule.wildcardMatch("***", "anything"));
  }

  @Test
  void testMultipleWildcards() {
    Assertions.assertTrue(AnalyzeHandlerRule.wildcardMatch("a*b*c", "abc"));
    Assertions.assertTrue(AnalyzeHandlerRule.wildcardMatch("a*b*c", "aXbYc"));
    Assertions.assertTrue(AnalyzeHandlerRule.wildcardMatch("a*b*c", "aXXbYYc"));
    Assertions.assertFalse(AnalyzeHandlerRule.wildcardMatch("a*b*c", "aXbY"));
    Assertions.assertFalse(AnalyzeHandlerRule.wildcardMatch("a*b*c", "ab"));
  }

  @Test
  void testWholeStringAnchored() {
    Assertions.assertFalse(AnalyzeHandlerRule.wildcardMatch("ab", "xab"));
    Assertions.assertFalse(AnalyzeHandlerRule.wildcardMatch("ab", "abx"));
    Assertions.assertFalse(AnalyzeHandlerRule.wildcardMatch("ab", "xabx"));
  }

  @Test
  void testRegexMetacharactersAreLiteral() {
    Assertions.assertTrue(AnalyzeHandlerRule.wildcardMatch("SYS.A", "SYS.A"));
    Assertions.assertFalse(AnalyzeHandlerRule.wildcardMatch("SYS.A", "SYSXA"));

    Assertions.assertTrue(AnalyzeHandlerRule.wildcardMatch("a+b", "a+b"));
    Assertions.assertFalse(AnalyzeHandlerRule.wildcardMatch("a+b", "aab"));

    Assertions.assertTrue(AnalyzeHandlerRule.wildcardMatch("a|b", "a|b"));
    Assertions.assertFalse(AnalyzeHandlerRule.wildcardMatch("a|b", "a"));

    Assertions.assertTrue(AnalyzeHandlerRule.wildcardMatch("x(y)", "x(y)"));
    Assertions.assertFalse(AnalyzeHandlerRule.wildcardMatch("x(y)", "xy"));

    Assertions.assertTrue(AnalyzeHandlerRule.wildcardMatch("a$b", "a$b"));
    Assertions.assertTrue(AnalyzeHandlerRule.wildcardMatch("a^b", "a^b"));
  }

  @Test
  void testBackslashIsLiteral() {
    Assertions.assertTrue(AnalyzeHandlerRule.wildcardMatch("a\\b", "a\\b"));
    Assertions.assertFalse(AnalyzeHandlerRule.wildcardMatch("a\\b", "ab"));
  }

  @Test
  void testCaseSensitive() {
    Assertions.assertFalse(AnalyzeHandlerRule.wildcardMatch("ABC", "abc"));
    Assertions.assertFalse(AnalyzeHandlerRule.wildcardMatch("abc", "ABC"));
    Assertions.assertTrue(AnalyzeHandlerRule.wildcardMatch("A*C", "ABC"));
    Assertions.assertFalse(AnalyzeHandlerRule.wildcardMatch("A*C", "abc"));
  }

  @Test
  void testUnicodeValue() {
    Assertions.assertTrue(AnalyzeHandlerRule.wildcardMatch("订单*", "订单详情"));
    Assertions.assertFalse(AnalyzeHandlerRule.wildcardMatch("订单*", "x订单详情"));
    Assertions.assertTrue(AnalyzeHandlerRule.wildcardMatch("*详情", "订单详情"));
  }

  @Test
  void testWhitespaceIsNotTrimmed() {
    // trim 是 parse 的职责，wildcardMatch 本身按字面量比较
    Assertions.assertFalse(AnalyzeHandlerRule.wildcardMatch(" SYS ", "SYS"));
    Assertions.assertTrue(AnalyzeHandlerRule.wildcardMatch(" SYS ", " SYS "));
  }

  @Test
  void testStarInValueIsJustACharacter() {
    Assertions.assertTrue(AnalyzeHandlerRule.wildcardMatch("a*b", "a*b"));
    Assertions.assertTrue(AnalyzeHandlerRule.wildcardMatch("a*b", "axxb"));
  }

}
