package cn.addenda.porttrail.server.biz.analyze;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 按 systemCode / serviceName / env 从全部 handler 中筛出本次要执行的 handler。
 *
 * <p>规则在启动时解析一次；匹配顺序与配置顺序一致，首条命中即生效。
 * <b>规则是唯一依据</b>：没有配置任何规则、或没有任何规则命中时返回空集合，
 * 本次 execution 不执行任何 handler（不会退化成「执行全部 handler」的兜底）。
 *
 * <p>规则和 handler 列表在运行期都不变，因此匹配结果按 {@code (systemCode, serviceName, env)}
 * 缓存：每个应用/环境组合只在首次出现时走一次通配匹配，之后直接命中缓存。
 *
 * @author addenda
 */
@Slf4j
@Component
public class AnalyzeHandlerRuleMatcher {

  /**
   * 缓存上限。(systemCode, serviceName, env) 的组合数等于接入的应用/环境数，正常只有几十个；
   * 设上限是为了防止异常值把缓存撑大。达到上限后新的组合不再写入缓存，退化为每次现算。
   */
  private static final int CACHE_CAPACITY = 256;

  private final List<AnalyzeHandler<?>> allHandlers;

  private final List<AnalyzeHandlerRule> rules;

  /**
   * 路由结果缓存。规则和 handler 列表在运行期不变，所以缓存只写一次、之后只读；
   * 读路径无锁（{@link ConcurrentHashMap}），同一 key 的并发首次计算由 putIfAbsent 收敛。
   */
  private final ConcurrentMap<RouteKey, List<AnalyzeHandler<?>>> effectiveHandlersCache =
          new ConcurrentHashMap<>();

  public AnalyzeHandlerRuleMatcher(@Value("${port-trail.analyze.handler-rules:}") String ruleText,
                                   List<AnalyzeHandler<?>> analyzeHandlerList) {
    this.rules = AnalyzeHandlerRule.parse(ruleText);
    this.allHandlers = Collections.unmodifiableList(new ArrayList<>(analyzeHandlerList));
    log.info("AnalyzeHandlerRuleMatcher initialized, rules: [{}], handlers: [{}].",
            rules, handlerNames());
  }

  /**
   * @return 本次生效的 handler 集合，保持注入顺序；结果是不可变列表，可直接缓存
   */
  public List<AnalyzeHandler<?>> effectiveHandlers(String systemCode, String serviceName, String env) {
    RouteKey key = new RouteKey(systemCode, serviceName, env);

    List<AnalyzeHandler<?>> cached = effectiveHandlersCache.get(key);
    if (cached != null) {
      return cached;
    }

    List<AnalyzeHandler<?>> resolved = resolve(key);
    // 达到上限后不再写入：size() 检查与 putIfAbsent 之间的并发最多让实际条数略微超出上限
    if (effectiveHandlersCache.size() < CACHE_CAPACITY) {
      List<AnalyzeHandler<?>> existing = effectiveHandlersCache.putIfAbsent(key, resolved);
      if (existing != null) {
        return existing;
      }
    }
    return resolved;
  }

  private List<AnalyzeHandler<?>> resolve(RouteKey key) {
    if (rules.isEmpty()) {
      log.warn("No handler rules configured, skip analysis. systemCode: [{}], serviceName: [{}], env: [{}].",
              key.systemCode, key.serviceName, key.env);
      return Collections.emptyList();
    }
    AnalyzeHandlerRule hitRule = null;
    for (AnalyzeHandlerRule rule : rules) {
      if (rule.matches(key.systemCode, key.serviceName, key.env)) {
        hitRule = rule;
        break;
      }
    }
    if (hitRule == null) {
      log.warn("No handler rule matched, skip analysis. systemCode: [{}], serviceName: [{}], env: [{}].",
              key.systemCode, key.serviceName, key.env);
      return Collections.emptyList();
    }

    List<AnalyzeHandler<?>> effectiveHandlers = new ArrayList<>();
    for (AnalyzeHandler<?> handler : allHandlers) {
      if (hitRule.containsHandler(handler.handlerName())) {
        effectiveHandlers.add(handler);
      }
    }
    if (effectiveHandlers.isEmpty()) {
      // 规则命中了但 handler 名字都写错时，本次 execution 不会被任何 handler 消费
      log.warn("Rule [{}] matched but no handler bean matched, systemCode: [{}], serviceName: [{}], env: [{}].",
              hitRule, key.systemCode, key.serviceName, key.env);
    }
    return Collections.unmodifiableList(effectiveHandlers);
  }

  private List<String> handlerNames() {
    List<String> handlerNames = new ArrayList<>(allHandlers.size());
    for (AnalyzeHandler<?> handler : allHandlers) {
      handlerNames.add(handler.handlerName());
    }
    return handlerNames;
  }

  /**
   * 缓存 key。systemCode / serviceName / env 都可能为 null，用 {@link Objects} 统一处理。
   */
  private static final class RouteKey {

    private final String systemCode;

    private final String serviceName;

    private final String env;

    private RouteKey(String systemCode, String serviceName, String env) {
      this.systemCode = systemCode;
      this.serviceName = serviceName;
      this.env = env;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof RouteKey)) {
        return false;
      }
      RouteKey that = (RouteKey) o;
      return Objects.equals(systemCode, that.systemCode)
              && Objects.equals(serviceName, that.serviceName)
              && Objects.equals(env, that.env);
    }

    @Override
    public int hashCode() {
      return Objects.hash(systemCode, serviceName, env);
    }

    @Override
    public String toString() {
      return systemCode + "|" + serviceName + "|" + env;
    }

  }

}
