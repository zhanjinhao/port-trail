package cn.addenda.porttrail.agent.transform.match;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

public class LogicalAndMatch implements IndirectMatch {

  private final IndirectMatch[] indirectMatches;

  public LogicalAndMatch(final IndirectMatch... indirectMatches) {
    this.indirectMatches = indirectMatches;
  }

  @Override
  public ElementMatcher.Junction<? super TypeDescription> buildJunction() {
    ElementMatcher.Junction<? super TypeDescription> junction = null;

    for (final IndirectMatch indirectMatch : indirectMatches) {
      if (junction == null) {
        junction = indirectMatch.buildJunction();
      } else {
        junction = junction.and(indirectMatch.buildJunction());
      }
    }

    return junction;
  }

  @Override
  public boolean isMatch(final TypeDescription typeDescription) {
    for (final IndirectMatch indirectMatch : indirectMatches) {
      if (!indirectMatch.isMatch(typeDescription)) {
        return false;
      }
    }

    return true;
  }

}
