package cn.addenda.porttrail.stacktrace;

import java.util.Objects;

public class ClassNameAndMethodNameMather implements IdentifierMather {

  private final String className;

  private final String methodName;

  public ClassNameAndMethodNameMather(String className, String methodName) {
    this.className = className;
    this.methodName = methodName;
    if (methodName == null || methodName.isEmpty()) {
      throw new StackTraceException("methodName can not be null or empty.");
    }
    if (className == null || className.isEmpty()) {
      throw new StackTraceException("className can not be null or empty.");
    }
  }

  @Override
  public boolean match(StackTraceElement stackTraceElement) {
    return className.equals(stackTraceElement.getClassName()) && methodName.equals(stackTraceElement.getMethodName());
  }

  @Override
  public String toString() {
    return "ClassNameAndMethodNameMather{" +
            "className='" + className + '\'' +
            ", methodName='" + methodName + '\'' +
            '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ClassNameAndMethodNameMather that = (ClassNameAndMethodNameMather) o;
    return Objects.equals(className, that.className) && Objects.equals(methodName, that.methodName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(className, methodName);
  }
}
