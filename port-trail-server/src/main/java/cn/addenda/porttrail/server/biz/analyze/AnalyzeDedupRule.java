package cn.addenda.porttrail.server.biz.analyze;

import cn.addenda.porttrail.server.helper.SqlParseResult;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

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

  private final int ifSelectAll;

  private final int ifFullScan;

  private final int ifInTooMany;

  private final int ifCostTooHigh;

  private AnalyzeDedupRule(int ifSelectAll, int ifFullScan, int ifInTooMany, int ifCostTooHigh) {
    this.ifSelectAll = ifSelectAll;
    this.ifFullScan = ifFullScan;
    this.ifInTooMany = ifInTooMany;
    this.ifCostTooHigh = ifCostTooHigh;
  }

  /**
   * @param parseResult    SQL 解析结论，解析失败（null）时按「无 select* / 无 IN」处理
   * @param ifFullScan     explain 是否出现 type = ALL；explain 未执行或失败时为 false
   * @param cost           当前这次 execution 的耗时，必须用当次值而不是缓存值
   * @param costThresholdMs cost 超过该值判定为慢 SQL
   * @param inThreshold    IN 元素个数超过该值判定为 IN 过多
   */
  public static AnalyzeDedupRule evaluate(SqlParseResult parseResult, boolean ifFullScan, Integer cost,
                                          int costThresholdMs, int inThreshold) {
    int selectAll = parseResult != null && parseResult.isHasSelectAll() ? 1 : 0;
    int fullScan = ifFullScan ? 1 : 0;
    int inTooMany = parseResult != null && parseResult.getMaxInElementCount() > inThreshold ? 1 : 0;
    int costTooHigh = cost != null && cost > costThresholdMs ? 1 : 0;
    return new AnalyzeDedupRule(selectAll, fullScan, inTooMany, costTooHigh);
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

}
