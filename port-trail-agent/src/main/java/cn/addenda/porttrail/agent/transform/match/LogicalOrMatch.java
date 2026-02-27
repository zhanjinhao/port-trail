package cn.addenda.porttrail.agent.transform.match;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

/**
 * Match classes by multiple criteria with OR conjunction
 */
public class LogicalOrMatch implements IndirectMatch {

  private final IndirectMatch[] indirectMatches;

  public LogicalOrMatch(final IndirectMatch... indirectMatches) {
    this.indirectMatches = indirectMatches;
  }

  @Override
  public ElementMatcher.Junction<? super TypeDescription> buildJunction() {
    ElementMatcher.Junction<? super TypeDescription> junction = null;

    for (final IndirectMatch indirectMatch : indirectMatches) {
      if (junction == null) {
        junction = indirectMatch.buildJunction();
      } else {
        junction = junction.or(indirectMatch.buildJunction());
      }
    }

    return junction;
  }

  @Override
  public boolean isMatch(final TypeDescription typeDescription) {
    for (final IndirectMatch indirectMatch : indirectMatches) {
      if (indirectMatch.isMatch(typeDescription)) {
        return true;
      }
    }

    return false;
  }

}
