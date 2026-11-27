package cn.addenda.porttrail.server.test.biz.analyze;

import cn.addenda.porttrail.server.biz.analyze.AnalyzeDedupRule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;

class AnalyzeDedupRuleTest {

  @Test
  void testNoRuleHit() {
    AnalyzeDedupRule rule = new AnalyzeDedupRule(0, 0, 0, 0);

    Assertions.assertFalse(rule.anyHit());
    Assertions.assertEquals("", rule.hitRules());
  }

  @Test
  void testEachRuleIndependently() {
    Assertions.assertEquals("SELECT_ALL", new AnalyzeDedupRule(1, 0, 0, 0).hitRules());
    Assertions.assertEquals("FULL_SCAN", new AnalyzeDedupRule(0, 1, 0, 0).hitRules());
    Assertions.assertEquals("IN_TOO_MANY", new AnalyzeDedupRule(0, 0, 1, 0).hitRules());
    Assertions.assertEquals("COST_TOO_HIGH", new AnalyzeDedupRule(0, 0, 0, 1).hitRules());
  }

  @Test
  void testHitRulesAggregatedInFixedOrder() {
    AnalyzeDedupRule rule = new AnalyzeDedupRule(1, 1, 1, 1);

    Assertions.assertTrue(rule.anyHit());
    Assertions.assertEquals(1, rule.getIfSelectAll());
    Assertions.assertEquals(1, rule.getIfFullScan());
    Assertions.assertEquals(1, rule.getIfInTooMany());
    Assertions.assertEquals(1, rule.getIfCostTooHigh());
    Assertions.assertEquals("SELECT_ALL,FULL_SCAN,IN_TOO_MANY,COST_TOO_HIGH", rule.hitRules());
  }

  // ---------------- hit_rules 解析 / 合并 ----------------

  @Test
  void testParseHitRules() {
    Assertions.assertTrue(AnalyzeDedupRule.parseHitRules(null).isEmpty());
    Assertions.assertTrue(AnalyzeDedupRule.parseHitRules("").isEmpty());
    Assertions.assertEquals(
            new HashSet<>(Arrays.asList("SELECT_ALL", "COST_TOO_HIGH")),
            AnalyzeDedupRule.parseHitRules(" SELECT_ALL , COST_TOO_HIGH "));
  }

  @Test
  void testMergeHitRulesUsesFixedOrder() {
    Assertions.assertEquals("SELECT_ALL,FULL_SCAN",
            AnalyzeDedupRule.mergeHitRules("SELECT_ALL", "FULL_SCAN,SELECT_ALL"));
    Assertions.assertEquals("FULL_SCAN",
            AnalyzeDedupRule.mergeHitRules(null, "FULL_SCAN"));
    Assertions.assertEquals("SELECT_ALL,IN_TOO_MANY,COST_TOO_HIGH",
            AnalyzeDedupRule.mergeHitRules("COST_TOO_HIGH", "SELECT_ALL,IN_TOO_MANY"));
  }

  @Test
  void testHasNewHitRule() {
    Assertions.assertFalse(AnalyzeDedupRule.hasNewHitRule("SELECT_ALL", "SELECT_ALL"));
    Assertions.assertFalse(AnalyzeDedupRule.hasNewHitRule("SELECT_ALL,COST_TOO_HIGH", "COST_TOO_HIGH"));
    Assertions.assertTrue(AnalyzeDedupRule.hasNewHitRule("SELECT_ALL", "SELECT_ALL,COST_TOO_HIGH"));
    Assertions.assertTrue(AnalyzeDedupRule.hasNewHitRule(null, "COST_TOO_HIGH"));
    Assertions.assertFalse(AnalyzeDedupRule.hasNewHitRule(null, null));
  }

}
