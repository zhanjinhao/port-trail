package cn.addenda.porttrail.server.biz.analyze;

import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 去重记录的四条规则判定结果。
 *
 * <p>规则之间是「或」：任一命中即记录。四条规则互相独立，命中任意一条都不构成跳过 explain
 * 的理由，否则 {@link #hitRules()} 不完整。
 *
 * @author addenda
 */
@Getter
@ToString
public class AnalyzeDedupRule {

  public static final String RULE_SELECT_ALL = "SELECT_ALL";

  public static final String RULE_FULL_SCAN = "FULL_SCAN";

  public static final String RULE_IN_TOO_MANY = "IN_TOO_MANY";

  public static final String RULE_COST_TOO_HIGH = "COST_TOO_HIGH";

  private static final String HIT_RULES_DELIMITER = ",";

  /**
   * {@code hit_rules} 的固定输出顺序，与 SQL 里 {@code CONCAT_WS} 的拼接顺序保持一致。
   */
  private static final List<String> RULE_ORDER = Collections.unmodifiableList(Arrays.asList(
          RULE_SELECT_ALL, RULE_FULL_SCAN, RULE_IN_TOO_MANY, RULE_COST_TOO_HIGH));

  private final int ifSelectAll;

  private final int ifFullScan;

  private final int ifInTooMany;

  private final int ifCostTooHigh;

  /**
   * 规则判定结果载体。各参数取 0 / 1，判定逻辑由调用方负责。
   */
  public AnalyzeDedupRule(int ifSelectAll, int ifFullScan, int ifInTooMany, int ifCostTooHigh) {
    this.ifSelectAll = ifSelectAll;
    this.ifFullScan = ifFullScan;
    this.ifInTooMany = ifInTooMany;
    this.ifCostTooHigh = ifCostTooHigh;
  }

  public boolean anyHit() {
    return ifSelectAll == 1 || ifFullScan == 1 || ifInTooMany == 1 || ifCostTooHigh == 1;
  }

  /**
   * 命中规则汇总，如 {@code SELECT_ALL,FULL_SCAN}；全未命中时返回空串。
   */
  public String hitRules() {
    List<String> hitList = new ArrayList<>(4);
    if (ifSelectAll == 1) {
      hitList.add(RULE_SELECT_ALL);
    }
    if (ifFullScan == 1) {
      hitList.add(RULE_FULL_SCAN);
    }
    if (ifInTooMany == 1) {
      hitList.add(RULE_IN_TOO_MANY);
    }
    if (ifCostTooHigh == 1) {
      hitList.add(RULE_COST_TOO_HIGH);
    }
    return String.join(HIT_RULES_DELIMITER, hitList);
  }

  /**
   * 四条规则的固定顺序，标记读写和 {@code hit_rules} 拼装都按这个顺序。
   */
  public static List<String> ruleOrder() {
    return RULE_ORDER;
  }

  /**
   * 解析 {@code hit_rules}，如 {@code SELECT_ALL,FULL_SCAN}；null / 空串返回空集合。
   */
  public static Set<String> parseHitRules(String hitRules) {
    Set<String> ruleSet = new LinkedHashSet<>();
    if (hitRules == null || hitRules.isEmpty()) {
      return ruleSet;
    }
    for (String rule : hitRules.split(HIT_RULES_DELIMITER)) {
      String trimmed = rule.trim();
      if (!trimmed.isEmpty()) {
        ruleSet.add(trimmed);
      }
    }
    return ruleSet;
  }

  /**
   * 合并两组 {@code hit_rules}，按 {@link #RULE_ORDER} 的固定顺序输出。
   */
  public static String mergeHitRules(String existingHitRules, String currentHitRules) {
    Set<String> merged = parseHitRules(existingHitRules);
    merged.addAll(parseHitRules(currentHitRules));

    List<String> hitList = new ArrayList<>(merged.size());
    for (String rule : RULE_ORDER) {
      if (merged.contains(rule)) {
        hitList.add(rule);
      }
    }
    return String.join(HIT_RULES_DELIMITER, hitList);
  }

  /**
   * 本次命中集合里是否有已记录集合之外的规则。
   */
  public static boolean hasNewHitRule(String existingHitRules, String currentHitRules) {
    Set<String> existing = parseHitRules(existingHitRules);
    for (String rule : parseHitRules(currentHitRules)) {
      if (!existing.contains(rule)) {
        return true;
      }
    }
    return false;
  }

}
