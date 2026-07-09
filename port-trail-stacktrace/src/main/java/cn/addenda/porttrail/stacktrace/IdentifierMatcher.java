package cn.addenda.porttrail.stacktrace;

public interface IdentifierMatcher {
  String HASH = "#";

  boolean match(StackTraceElement stackTraceElement);

}
