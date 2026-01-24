package cn.addenda.porttrail.stacktrace;

public interface IdentifierMather {
  String HASH = "#";

  boolean match(StackTraceElement stackTraceElement);

}
