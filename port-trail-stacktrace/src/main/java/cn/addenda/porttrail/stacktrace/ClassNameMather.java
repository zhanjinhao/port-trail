package cn.addenda.porttrail.stacktrace;

import java.util.Objects;

public class ClassNameMather implements IdentifierMather {

  private final String className;

  public ClassNameMather(String className) {
    this.className = className;
    if (className == null || className.isEmpty()) {
      throw new StackTraceException("className can not be null or empty.");
    }
  }

  @Override
  public boolean match(StackTraceElement stackTraceElement) {
    return className.equals(stackTraceElement.getClassName());
  }

  @Override
  public String toString() {
    return "ClassNameMather{" +
            "className='" + className + '\'' +
            '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ClassNameMather that = (ClassNameMather) o;
    return Objects.equals(className, that.className);
  }

  @Override
  public int hashCode() {
    return Objects.hash(className);
  }
}
