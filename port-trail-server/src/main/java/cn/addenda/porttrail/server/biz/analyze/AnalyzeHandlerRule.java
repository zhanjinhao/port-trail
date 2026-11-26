package cn.addenda.porttrail.server.biz.analyze;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 一条 handler 路由规则：{@code systemCode|serviceName|env|handler1,handler2}。
 *
 * <p>规则之间用分号分隔，按顺序匹配，首条命中即生效；字段支持 {@code *} 通配。
 *
 * @author addenda
 */
@Getter
public class AnalyzeHandlerRule {

  // ----------------------------------------------------------------------
  //    REG_ 前缀表示该值会被当作正则使用（`String.split` 的参数），不是字面量分隔符
  // ----------------------------------------------------------------------

  private static final String REG_FIELD_DELIMITER = "\\|";

  private static final String REG_RULE_DELIMITER = ";";

  private static final String REG_HANDLER_DELIMITER = ",";

  private static final String REG_WILDCARD = "\\*";

  /**
   * 通配符。只由它组成的 pattern（{@code *}、{@code **}、{@code ***}）语义一致：匹配一切。
   */
  private static final char WILDCARD = '*';

  private static final int FIELD_COUNT = 4;

  private final String systemCodePattern;

  private final String serviceNamePattern;

  private final String envPattern;

  private final List<String> handlerNames;

  private AnalyzeHandlerRule(String systemCodePattern, String serviceNamePattern,
                             String envPattern, List<String> handlerNames) {
    this.systemCodePattern = systemCodePattern;
    this.serviceNamePattern = serviceNamePattern;
    this.envPattern = envPattern;
    this.handlerNames = handlerNames;
  }

  /**
   * 解析配置文本；空文本返回空列表，格式不合法时抛 {@link IllegalArgumentException}（启动即失败）。
   */
  public static List<AnalyzeHandlerRule> parse(String ruleText) {
    if (ruleText == null || ruleText.trim().isEmpty()) {
      return Collections.emptyList();
    }
    List<AnalyzeHandlerRule> rules = new ArrayList<>();
    for (String rule : ruleText.split(REG_RULE_DELIMITER)) {
      String trimmed = rule.trim();
      if (trimmed.isEmpty()) {
        continue;
      }
      // -1 表示保留末尾空段：handler 列表为空（如 "SYS|svc|prod|"）时仍能切出 4 段，
      // 从而命中下面「handler list must not be empty」的精确报错，而不是误报字段数不对
      String[] fields = trimmed.split(REG_FIELD_DELIMITER, -1);
      if (fields.length != FIELD_COUNT) {
        throw new IllegalArgumentException(String.format(
                "Invalid handler rule [%s]: expected [systemCode|serviceName|env|handler1,handler2].", trimmed));
      }
      List<String> handlerNames = new ArrayList<>();
      // 这里不传 limit：末尾空项（如 "A,,"）直接丢弃；中间空项仍会保留，由下面的 trim/isEmpty 过滤
      for (String handlerName : fields[3].split(REG_HANDLER_DELIMITER)) {
        String name = handlerName.trim();
        if (!name.isEmpty()) {
          handlerNames.add(name);
        }
      }
      if (handlerNames.isEmpty()) {
        throw new IllegalArgumentException(String.format(
                "Invalid handler rule [%s]: handler list must not be empty.", trimmed));
      }
      rules.add(new AnalyzeHandlerRule(fields[0].trim(), fields[1].trim(), fields[2].trim(), handlerNames));
    }
    return rules;
  }

  public boolean matches(String systemCode, String serviceName, String env) {
    return wildcardMatch(systemCodePattern, systemCode)
            && wildcardMatch(serviceNamePattern, serviceName)
            && wildcardMatch(envPattern, env);
  }

  /**
   * handler 名按大小写精确匹配，与 {@code handlerName()} 的返回值保持一致。
   */
  public boolean containsHandler(String handlerName) {
    for (String name : handlerNames) {
      if (name.equals(handlerName)) {
        return true;
      }
    }
    return false;
  }

  /**
   * 按配置格式输出：{@code systemCode|serviceName|env|handler1,handler2}，
   * 便于日志里直接和 {@code port-trail.analyze.handler-rules} 对照。
   */
  @Override
  public String toString() {
    return systemCodePattern + "|" + serviceNamePattern + "|" + envPattern + "|"
            + String.join(",", handlerNames);
  }

  /**
   * 通配匹配：整串匹配，其余字符按字面量比较。
   *
   * <p><b>纯星号 pattern 优先</b>：只由 {@code *} 组成的 pattern（{@code *}、{@code **}、{@code ***}）
   * 语义完全一致，表示「匹配一切」，对 null 也返回 true。
   *
   * <p><b>其余 pattern 对 null 一律不匹配</b>：null 表示该项未上报，不猜测它的值。
   *
   * <p>实现方式是把 glob 转成正则：按 {@code *} 切分 pattern，相邻字面量之间补 {@code .*}，
   * 每个字面量用 {@link Pattern#quote(String)} 转义，首尾加 {@code ^} / {@code $}。
   * 例如 {@code SYS_*} 转成 {@code ^\QSYS_\E.*\Q\E$}。
   *
   * <p>生成结果里的 {@code \Q}/{@code \E} 是正则的引用标记：两者之间的内容一律按普通字符匹配，
   * 不参与元字符解析（{@link Pattern#quote(String)} 对每个字面量段输出的就是这种包装）。
   *
   * @param pattern 通配串，不能为 null（由 {@link #parse(String)} 保证）
   * @param value   待匹配值；为 null 时只有纯星号 pattern 能匹配
   * @throws NullPointerException pattern 为 null
   */
  public static boolean wildcardMatch(String pattern, String value) {
    // pattern 由 parse 保证非 null；这里显式校验，避免把调用方传 null 变成晦涩的 NPE
    Objects.requireNonNull(pattern, "pattern must not be null!");
    // 纯星号 pattern 语义一致：匹配一切，优先级高于 null 判断
    if (isFullWildcard(pattern)) {
      return true;
    }
    // null 表示未上报，除纯星号外的 pattern 一律不匹配
    if (value == null) {
      return false;
    }

    // 把 glob 转成正则：^ <字面量> .* <字面量> ... $
    StringBuilder regex = new StringBuilder("^");
    // -1 保留首尾空段，这样 "*SYS" 切出 ["", "SYS"]、"SYS*" 切出 ["SYS", ""]
    for (String part : pattern.split(REG_WILDCARD, -1)) {
      // regex 初始只有 "^"（长度 1）：第一段前面不补 ".*"；
      // 第一段拼入后长度必然大于 1，后续每段前都补 ".*"
      if (regex.length() > 1) {
        regex.append(".*");
      }
      // 字面量整体转义，保证 "." "+" "|" "(" 等正则元字符按普通字符匹配。
      // Pattern.quote 输出的是 \Q...\E 引用段，例如 "SYS_" → \QSYS_\E、"" → \Q\E，
      // 段内所有字符都不参与正则解析，因此不用逐个元字符手写转义。
      regex.append(Pattern.quote(part));
    }
    // 首尾锚定。String.matches 本身已要求整串匹配，这里加上是为了让正则表达式自解释
    regex.append('$');

    return value.matches(regex.toString());
  }

  /**
   * 是否只由 {@code *} 组成（如 {@code *}、{@code **}、{@code ***}）。空串不算。
   */
  private static boolean isFullWildcard(String pattern) {
    if (pattern.isEmpty()) {
      return false;
    }
    for (int i = 0; i < pattern.length(); i++) {
      if (pattern.charAt(i) != WILDCARD) {
        return false;
      }
    }
    return true;
  }

}
