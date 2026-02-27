package cn.addenda.porttrail.stacktrace;

import java.util.Objects;

public class ClassNamePrefixMather implements IdentifierMather {

  private final String classNamePrefix;

  public ClassNamePrefixMather(String classNamePrefix) {
    this.classNamePrefix = classNamePrefix;
    if (classNamePrefix == null || classNamePrefix.isEmpty()) {
      throw new StackTraceException("classNamePrefix can not be null or empty.");
    }
  }

  @Override
  public boolean match(StackTraceElement stackTraceElement) {
    return stackTraceElement.getClassName().startsWith(classNamePrefix);
  }

  @Override
  public String toString() {
    return "ClassNamePrefixMather{" +
            "classNamePrefix='" + classNamePrefix + '\'' +
            '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ClassNamePrefixMather that = (ClassNamePrefixMather) o;
    return Objects.equals(classNamePrefix, that.classNamePrefix);
  }

  @Override
  public int hashCode() {
    return Objects.hash(classNamePrefix);
  }
}
