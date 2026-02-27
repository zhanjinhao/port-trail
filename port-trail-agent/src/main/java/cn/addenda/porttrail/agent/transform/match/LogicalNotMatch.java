package cn.addenda.porttrail.agent.transform.match;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.NegatingMatcher;

public class LogicalNotMatch implements IndirectMatch {

  private final IndirectMatch indirectMatch;

  public LogicalNotMatch(final IndirectMatch indirectMatch) {
    this.indirectMatch = indirectMatch;
  }

  @Override
  public ElementMatcher.Junction<? super TypeDescription> buildJunction() {
    return new NegatingMatcher<>(indirectMatch.buildJunction());
  }

  @Override
  public boolean isMatch(final TypeDescription typeDescription) {
    return !indirectMatch.isMatch(typeDescription);
  }

}
