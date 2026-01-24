package cn.addenda.porttrail.stacktrace;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author addenda
 * @since 2023/2/17 9:51
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StackTraceUtils {

  @Getter
  private static final Set<IdentifierMather> defaultExcludeSet;

  private static final String EXCLUDED_PATH = "META-INF/port-trail-stacktrace.excluded";

  static {
    Set<IdentifierMather> lines = new HashSet<>();
    try {
      ClassLoader classLoader = StackTraceUtils.class.getClassLoader();
      Enumeration<URL> urls = classLoader.getResources(EXCLUDED_PATH);
      while (urls.hasMoreElements()) {
        URL url = urls.nextElement();
        URLConnection urlConnection = url.openConnection();
        urlConnection.setUseCaches(false);
        InputStream inputStream = urlConnection.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
          if (line.isEmpty()) {
            continue;
          }
          lines.add(IdentifierMatherFactory.getIdentifierMatcher(line));
        }
      }
    } catch (Exception e) {
      throw new StackTraceException(String.format("Can not open file: [%s].", EXCLUDED_PATH), e);
    }
    defaultExcludeSet = Collections.unmodifiableSet(lines);
  }

  /**
   * @param useSimpleClassName 是否按简写的类名输出
   * @param excludes           全类名
   */
  public static String getCallerInfo(
          boolean useSimpleClassName, boolean ifExcludeLambda, boolean ifExcludeAnonymousInnerClass, String... excludes) {
    StackTraceElement stackTraceElement = determineStackTraceElement(ifExcludeLambda, ifExcludeAnonymousInnerClass, excludes);
    String className = stackTraceElement.getClassName();
    String methodName = stackTraceElement.getMethodName();
    if (useSimpleClassName) {
      className = extractSimpleClassName(className);
    }
    return className + "#" + methodName;
  }

  public static String getCallerInfo(boolean useSimpleClassName, boolean ifExcludeLambda, boolean ifExcludeAnonymousInnerClass) {
    return getCallerInfo(useSimpleClassName, ifExcludeLambda, ifExcludeAnonymousInnerClass, (String[]) null);
  }

  public static String getCallerInfo(boolean ifExcludeLambda, boolean ifExcludeAnonymousInnerClass) {
    return getCallerInfo(true, ifExcludeLambda, ifExcludeAnonymousInnerClass);
  }

  public static String getCallerInfo() {
    return getCallerInfo(true, false, false);
  }

  public static String getCallerInfo(String... excludes) {
    return getCallerInfo(true, false, false, excludes);
  }

  /**
   * @param useSimpleClassName 是否按简写的类名输出
   * @param excludes           全类名
   */
  public static String getDetailedCallerInfo(
          boolean useSimpleClassName, boolean ifExcludeLambda, boolean ifExcludeAnonymousInnerClass, String... excludes) {
    StackTraceElement stackTraceElement = determineStackTraceElement(ifExcludeLambda, ifExcludeAnonymousInnerClass, excludes);
    String className = stackTraceElement.getClassName();
    String fileName = stackTraceElement.getFileName();
    int lineNumber = stackTraceElement.getLineNumber();
    String methodName = stackTraceElement.getMethodName();
    if (useSimpleClassName) {
      className = extractSimpleClassName(className);
    }
    return String.format("%s of %s:%s", className + "#" + methodName, fileName, lineNumber);
  }

  public static String getDetailedCallerInfo(
          boolean useSimpleClassName, boolean ifExcludeLambda, boolean ifExcludeAnonymousInnerClass) {
    return getDetailedCallerInfo(useSimpleClassName, ifExcludeLambda, ifExcludeAnonymousInnerClass, (String) null);
  }

  public static String getDetailedCallerInfo(boolean ifExcludeLambda, boolean ifExcludeAnonymousInnerClass) {
    return getDetailedCallerInfo(true, ifExcludeLambda, ifExcludeAnonymousInnerClass, (String[]) null);
  }

  public static String getDetailedCallerInfo() {
    return getDetailedCallerInfo(true, false, false);
  }

  public static String getDetailedCallerInfo(String... excludes) {
    return getDetailedCallerInfo(true, false, false, excludes);
  }

  private static StackTraceElement determineStackTraceElement(
          boolean ifExcludeLambda, boolean ifExcludeAnonymousInnerClass, String... excludes) {
    Set<IdentifierMather> excludeSet = defaultExcludeSet;
    if (excludes != null) {
      excludeSet = new HashSet<>(defaultExcludeSet);
      excludeSet.addAll(Arrays.stream(excludes).filter(Objects::nonNull)
              .map(IdentifierMatherFactory::getIdentifierMatcher).collect(Collectors.toList()));
    }
    return determineStackTraceElement(Thread.currentThread().getStackTrace(), excludeSet, ifExcludeLambda, ifExcludeAnonymousInnerClass);
  }

  private static StackTraceElement determineStackTraceElement(
          StackTraceElement[] stackTraceElements, Set<IdentifierMather> excludeSet,
          boolean ifExcludeLambda, boolean ifExcludeAnonymousInnerClass) {
    if (stackTraceElements == null || stackTraceElements.length == 0) {
      throw new IllegalArgumentException("The arg `stackTraceElements` can not null and can not be empty.");
    }
    for (StackTraceElement stackTraceElement : stackTraceElements) {
      String methodName = stackTraceElement.getMethodName();
      if (ifExcludeLambda && methodName.matches(".*lambda\\$.*")) {
        continue;
      }
      String className = stackTraceElement.getClassName();
      if (ifExcludeAnonymousInnerClass && className.matches(".*\\$\\d+.*")) {
        continue;
      }
      boolean flag = IdentifierMatherFactory.match(excludeSet, stackTraceElement);
      if (!flag) {
        return stackTraceElement;
      }
    }
    String a = "All elements of `stackTraceElements` are excluded. The `stackTraceElements` are [%s]. The `excludeSet` are [%s].";
    throw new StackTraceException(String.format(a, toString(stackTraceElements), toString(excludeSet)));
  }

  private static String toString(StackTraceElement[] stackTraceElements) {
    return Arrays.stream(stackTraceElements).map(StackTraceElement::toString).collect(Collectors.joining(",", "[", "]"));
  }

  private static String toString(Set<IdentifierMather> excludeSet) {
    return excludeSet.stream().map(IdentifierMather::toString).collect(Collectors.joining(",", "[", "]"));
  }

  private static String extractSimpleClassName(String className) {
    StringBuilder simpleClassName = new StringBuilder();
    for (int i = className.length() - 1; i > -1; i--) {
      char c = className.charAt(i);
      if (c == '.') {
        break;
      }
      simpleClassName.append(c);
    }
    return simpleClassName.reverse().toString();
  }

}
