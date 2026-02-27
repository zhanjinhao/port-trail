package cn.addenda.porttrail.agent.transform.match;

import cn.addenda.porttrail.agent.log.AgentPortTrailLoggerFactory;
import cn.addenda.porttrail.infrastructure.log.PortTrailLogger;
import net.bytebuddy.matcher.ElementMatcher;

/**
 * copy from skywalking.
 * <p>
 * In some cases, some frameworks and libraries use some binary codes tech too. From the community feedback, some of
 * them have compatible issues with byte-buddy core, which trigger "Can't resolve type description" exception.
 * <p>
 * So I build this protective shield by a nested matcher. When the origin matcher(s) can't resolve the type, the
 * SkyWalking agent ignores this types.
 * <p>
 * Notice: this ignore mechanism may miss some instrumentations, but at most cases, it's same. If missing happens,
 * please pay attention to the WARNING logs.
 */
public class ProtectiveShieldMatcher<T> extends ElementMatcher.Junction.AbstractBase<T> {
  private static final PortTrailLogger LOGGER = AgentPortTrailLoggerFactory.getInstance().getPortTrailLogger(ProtectiveShieldMatcher.class);

  private final ElementMatcher<? super T> matcher;

  public ProtectiveShieldMatcher(ElementMatcher<? super T> matcher) {
    this.matcher = matcher;
  }

  @Override
  public boolean matches(T target) {
    try {
      return this.matcher.matches(target);
    } catch (Throwable t) {
      LOGGER.warn("Byte-buddy occurs exception when match type [{}].", target, t);
      return false;
    }
  }

}
