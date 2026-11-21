package cn.addenda.porttrail.server.test.biz.analyze;

import cn.addenda.porttrail.server.biz.analyze.AnalyzeDedupRule;
import cn.addenda.porttrail.server.helper.SqlParseResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;

class AnalyzeDedupRuleTest {

  private static final int COST_THRESHOLD = 100;

  private static final int IN_THRESHOLD = 200;

  @Test
  void testNoRuleHit() {
    SqlParseResult parseResult = new SqlParseResult(false, 200, Collections.emptySet());
    AnalyzeDedupRule rule = AnalyzeDedupRule.evaluate(parseResult, false, 100, COST_THRESHOLD, IN_THRESHOLD);

    Assertions.assertFalse(rule.anyHit());
    Assertions.assertEquals("", rule.hitRules());
  }

  @Test
  void testThresholdIsExclusive() {
    SqlParseResult parseResult = new SqlParseResult(false, IN_THRESHOLD, Collections.emptySet());

    Assertions.assertFalse(AnalyzeDedupRule.evaluate(parseResult, false, COST_THRESHOLD,
            COST_THRESHOLD, IN_THRESHOLD).anyHit());
    Assertions.assertTrue(AnalyzeDedupRule.evaluate(parseResult, false, COST_THRESHOLD + 1,
            COST_THRESHOLD, IN_THRESHOLD).anyHit());
  }

  @Test
  void testEachRuleIndependently() {
    Assertions.assertEquals("SELECT_ALL", AnalyzeDedupRule.evaluate(
            new SqlParseResult(true, 0, Collections.emptySet()), false, null,
            COST_THRESHOLD, IN_THRESHOLD).hitRules());
    Assertions.assertEquals("FULL_SCAN", AnalyzeDedupRule.evaluate(
            new SqlParseResult(false, 0, Collections.emptySet()), true, null,
            COST_THRESHOLD, IN_THRESHOLD).hitRules());
    Assertions.assertEquals("IN_TOO_MANY", AnalyzeDedupRule.evaluate(
            new SqlParseResult(false, IN_THRESHOLD + 1, Collections.emptySet()), false, null,
            COST_THRESHOLD, IN_THRESHOLD).hitRules());
    Assertions.assertEquals("COST_TOO_HIGH", AnalyzeDedupRule.evaluate(
            new SqlParseResult(false, 0, Collections.emptySet()), false, COST_THRESHOLD + 1,
            COST_THRESHOLD, IN_THRESHOLD).hitRules());
  }

  @Test
  void testHitRulesAggregatedInFixedOrder() {
    AnalyzeDedupRule rule = AnalyzeDedupRule.evaluate(
            new SqlParseResult(true, IN_THRESHOLD + 1, Collections.emptySet()), true, COST_THRESHOLD + 1,
            COST_THRESHOLD, IN_THRESHOLD);

    Assertions.assertTrue(rule.anyHit());
    Assertions.assertEquals(1, rule.getIfSelectAll());
    Assertions.assertEquals(1, rule.getIfFullScan());
    Assertions.assertEquals(1, rule.getIfInTooMany());
    Assertions.assertEquals(1, rule.getIfCostTooHigh());
    Assertions.assertEquals("SELECT_ALL,FULL_SCAN,IN_TOO_MANY,COST_TOO_HIGH", rule.hitRules());
  }

  @Test
  void testParseFailedOnlyJudgesFullScanAndCost() {
    Assertions.assertFalse(AnalyzeDedupRule.evaluate(
            null, false, null, COST_THRESHOLD, IN_THRESHOLD).anyHit());
    Assertions.assertEquals("FULL_SCAN", AnalyzeDedupRule.evaluate(
            null, true, null, COST_THRESHOLD, IN_THRESHOLD).hitRules());
    Assertions.assertEquals("COST_TOO_HIGH", AnalyzeDedupRule.evaluate(
            null, false, COST_THRESHOLD + 1, COST_THRESHOLD, IN_THRESHOLD).hitRules());
  }

}
