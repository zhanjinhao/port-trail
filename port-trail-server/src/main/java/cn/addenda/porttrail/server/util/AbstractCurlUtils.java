package cn.addenda.porttrail.server.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Servlet请求转curl命令工具类
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AbstractCurlUtils {

  protected static final String CURL_TEMPLATE = "curl -X %s '%s'";
  protected static final String HEADER_TEMPLATE = " -H '%s: %s'";
  protected static final String DATA_TEMPLATE = " -d '%s'";
  protected static final String DATA_URLENCODE_TEMPLATE = " --data-urlencode '%s'";
  protected static final String FORM_TEMPLATE = " -F '%s=%s'";
  protected static final String FILE_FORM_TEMPLATE = " -F '%s=@%s;type=%s'";
  protected static final char LF = '\n';

  /**
   * 从请求头中获取Host
   */
  protected static String getHostFromHeaders(Map<String, List<String>> headerMap) {
    if (headerMap == null) {
      return null;
    }

    // 尝试不同的Host头名称
    for (Map.Entry<String, List<String>> entry : headerMap.entrySet()) {
      String key = entry.getKey();
      if ("host".equalsIgnoreCase(key)) {
        List<String> values = entry.getValue();
        if (values != null && !values.isEmpty()) {
          return values.get(0);
        }
      }
    }
    return null;
  }

  /**
   * 转义Header值中的特殊字符
   */
  protected static String escapeHeader(String value) {
    if (value == null) {
      return "";
    }
    // 替换单引号为转义的单引号
    return value.replace("'", "'\\''");
  }

  /**
   * 转义data值中的特殊字符
   */
  protected static String escapeData(String value) {
    if (value == null) {
      return "";
    }
    // 替换单引号为转义的单引号
    return value.replace("'", "'\\''");
  }

  /**
   * 转义表单值中的特殊字符
   */
  protected static String escapeForm(String value) {
    if (value == null) {
      return "";
    }
    // 替换单引号为转义的单引号
    return value.replace("'", "'\\''");
  }

  protected static boolean ifGET(String method) {
    return "GET".equalsIgnoreCase(method);
  }

  protected static boolean ifHEAD(String method) {
    return "HEAD".equalsIgnoreCase(method);
  }

  protected static boolean ifPOST(String method) {
    return "POST".equalsIgnoreCase(method);
  }

  protected static boolean ifPUT(String method) {
    return "PUT".equalsIgnoreCase(method);
  }

  protected static boolean ifDELETE(String method) {
    return "DELETE".equalsIgnoreCase(method);
  }

  protected static boolean ifCONNECT(String method) {
    return "CONNECT".equalsIgnoreCase(method);
  }

  protected static boolean ifOPTIONS(String method) {
    return "OPTIONS".equalsIgnoreCase(method);
  }

  protected static boolean ifTRACE(String method) {
    return "TRACE".equalsIgnoreCase(method);
  }

  protected static boolean ifPATCH(String method) {
    return "PATCH".equalsIgnoreCase(method);
  }

}
