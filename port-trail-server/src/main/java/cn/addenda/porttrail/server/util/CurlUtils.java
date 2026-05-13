package cn.addenda.porttrail.server.util;

import cn.addenda.porttrail.common.constant.MediaType;
import cn.addenda.porttrail.common.pojo.servlet.bo.AbstractServletExecution;
import cn.addenda.porttrail.common.pojo.servlet.bo.ServletRequestBo;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.*;

/**
 * Servlet请求转curl命令工具类
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CurlUtils {

  private static final String CURL_TEMPLATE = "curl -X %s '%s'";
  private static final String HEADER_TEMPLATE = " -H '%s: %s'";
  private static final String DATA_TEMPLATE = " -d '%s'";
  private static final String DATA_URLENCODE_TEMPLATE = " --data-urlencode '%s'";
  private static final String FORM_TEMPLATE = " -F '%s=%s'";
  private static final String FILE_FORM_TEMPLATE = " -F '%s=@%s;type=%s'";
  private static final char LF = '\n';

  /**
   * 将ServletRequestBo转换为curl命令
   *
   * @param requestBo Servlet请求业务对象
   * @return curl命令字符串
   */
  public static String toCurl(ServletRequestBo requestBo) {
    if (requestBo == null || !ifSupport(requestBo)) {
      return null;
    }

    StringBuilder curl = new StringBuilder();

    // 1. 构建基础URL
    String url = buildUrl(requestBo);

    // 2. 添加HTTP方法
    curl.append(String.format(CURL_TEMPLATE, requestBo.getMethod(), url)).append(LF);

    // 3. 添加请求头。 todo excludedHeaderList
    appendHeaders(curl, requestBo, new ArrayList<>());

    // 4. 添加请求体（仅当body为String类型时可以添加请求体）
    if (requestBo.getBody() instanceof String) {
      appendBody(curl, requestBo);
    }

    if (curl.charAt(curl.length() - 1) == LF) {
      curl.deleteCharAt(curl.length() - 1);
    }
    return curl.toString();
  }

  /**
   * 构建完整URL
   */
  private static String buildUrl(ServletRequestBo requestBo) {
    StringBuilder url = new StringBuilder();

    // 协议
    url.append(requestBo.getScheme()).append("://");

    // 从header中获取host，如果没有则使用默认值
    String host = getHostFromHeaders(requestBo.getHeaderMap());
    url.append(host != null ? host : "localhost");

    // URI
    if (requestBo.getUri() != null) {
      url.append(requestBo.getUri());
    }

    // 查询参数
    if (requestBo.getQueryString() != null && !requestBo.getQueryString().isEmpty()) {
      String queryString = requestBo.getQueryString();
      // 移除开头的所有 & 符号
      while (queryString.startsWith("&")) {
        queryString = queryString.substring(1);
      }
      url.append("?").append(queryString);
    }

    return url.toString();
  }

  /**
   * 从请求头中获取Host
   */
  private static String getHostFromHeaders(Map<String, List<String>> headerMap) {
    if (headerMap == null) {
      return null;
    }

    // 尝试不同的Host头名称
    for (String key : headerMap.keySet()) {
      if ("host".equalsIgnoreCase(key)) {
        List<String> values = headerMap.get(key);
        if (values != null && !values.isEmpty()) {
          return values.get(0);
        }
      }
    }
    return null;
  }

  /**
   * 添加请求头
   */
  private static void appendHeaders(StringBuilder curl, ServletRequestBo requestBo, List<String> excludedHeaderList) {
    Map<String, List<String>> headerMap = requestBo.getHeaderMap();
    if (headerMap == null || headerMap.isEmpty()) {
      return;
    }

    boolean ifAddContentLength = false;
    for (Map.Entry<String, List<String>> entry : headerMap.entrySet()) {
      String headerName = entry.getKey();
      if (headerName == null) {
        continue;
      }

      boolean ifExcluded = false;
      for (String excludedHeader : excludedHeaderList) {
        if (headerName.equalsIgnoreCase(excludedHeader)) {
          ifExcluded = true;
          break;
        }
      }
      if (ifExcluded) {
        continue;
      }

      List<String> values = entry.getValue();

      if ("content-type".equalsIgnoreCase(headerName) && requestBo.getContentType() != null) {
        curl.append(String.format(HEADER_TEMPLATE, escapeHeader(headerName), escapeHeader(requestBo.getContentType()))).append(LF);
        continue;
      }

      if (values != null) {
        for (String value : values) {
          // 假如headerName是content-length，如果header的值和requestBo的contentLength不一致，则跳过。
          // header的值是原始请求里客户端向服务端发送的请求头的数据；requestBo的contentLength是服务端实际解析过的数据长度。
          if ("content-length".equalsIgnoreCase(headerName)) {
            if (!Objects.equals(String.valueOf(requestBo.getContentLength()), value)) {
              continue;
            } else {
              ifAddContentLength = true;
            }
          }
          curl.append(String.format(HEADER_TEMPLATE, escapeHeader(headerName), escapeHeader(value))).append(LF);
        }
      }
    }

    // 如果在遍历header时没有添加content-length头，但是requestBo的contentLength不为0，添加content-length头。
    // 如果在遍历header时没有添加content-length头，但是请求是POST、PUT、PATCH、DELETE，添加content-length头。
    if (!ifAddContentLength && (requestBo.getContentLength() > 0 || ifPOST(requestBo) || ifPUT(requestBo) || ifPATCH(requestBo) || ifDELETE(requestBo))) {
      curl.append(String.format(HEADER_TEMPLATE, "content-length", requestBo.getContentLength())).append(LF);
    }
  }

  /**
   * 添加请求体
   */
  private static void appendBody(StringBuilder curl, ServletRequestBo requestBo) {
    if ("GET".equalsIgnoreCase(requestBo.getMethod())) {
      return;
    }

    Object body = requestBo.getBody();

    if (body == null || AbstractServletExecution.BODY_EMPTY.equals(body)) {
      return;
    }

    // 不支持的内容类型
    if (MediaType.ifRequestTextContentType(requestBo.getContentType())) {
      // 超出长度限制
      if (AbstractServletExecution.BODY_EXCEED_LENGTH.equals(body)) {
        curl.append(" # Body exceeded length limit");
        return;
      }

      // 文本类型（JSON、XML、表单等）
      String bodyStr = (String) body;
      if (!bodyStr.isEmpty()) {
//        if (MediaType.ifRequestFormUrlencodedContentType(requestBo.getContentType())) {
//          Arrays.stream(bodyStr.split("&")).forEach(
//                  param -> {
//                    int equalIndex = param.indexOf('=');
//                     如果使用--data-urlencode，需要将value转义
//                    if (equalIndex > 0) {
//                      String key = param.substring(0, equalIndex);
//                      String value = param.substring(equalIndex + 1);
//                      String enc = (requestBo.getCharsetEncoding() == null || requestBo.getCharsetEncoding().isEmpty()) ? AbstractServletExecution.DEFAULT_CHARSET : requestBo.getCharsetEncoding();
//                      curl.append(String.format(DATA_URLENCODE_TEMPLATE, key + "=" + UrlUtils.decode(value, enc))).append(LF);
//                    } else {
//                      curl.append(String.format(DATA_URLENCODE_TEMPLATE, param)).append(LF);
//                    }
//                  });
//        } else {
//          curl.append(String.format(DATA_TEMPLATE, escapeData(bodyStr))).append(LF);
//        }

        curl.append(String.format(DATA_TEMPLATE, escapeData(bodyStr))).append(LF);
      }
    }

  }


  /**
   * 转义Header值中的特殊字符
   */
  private static String escapeHeader(String value) {
    if (value == null) {
      return "";
    }
    // 替换单引号为转义的单引号
    return value.replace("'", "'\\''");
  }

  /**
   * 转义data值中的特殊字符
   */
  private static String escapeData(String value) {
    if (value == null) {
      return "";
    }
    // 替换单引号为转义的单引号
    return value.replace("'", "'\\''");
  }

  /**
   * 转义表单值中的特殊字符
   */
  private static String escapeForm(String value) {
    if (value == null) {
      return "";
    }
    // 替换单引号为转义的单引号
    return value.replace("'", "'\\''");
  }

  /**
   * <pre>
   * 请求可以带body、也可以不带body：
   *    1.如果带body，则body类型是String时可以处理。
   *    2.如果不带body，直接可以处理。
   * </pre>
   */
  private static boolean ifSupport(ServletRequestBo servletRequestBo) {
    Object body = servletRequestBo.getBody();
    if (body == null
            || AbstractServletExecution.BODY_EMPTY.equals(body)) {
      return true;
    }
    if (AbstractServletExecution.UNSUPPORTED_CONTENT_TYPE.equals(body)
            || AbstractServletExecution.UNSUPPORTED_CHARSET_ENCODING.equals(body)
            || AbstractServletExecution.BODY_EXCEED_LENGTH.equals(body)
            || ServletRequestBo.BODY_BYTE_ARRAY.equals(body)
    ) {
      return false;
    }
    if (body instanceof String) {
      return true;
    }
    return false;
  }

  private static boolean ifGET(ServletRequestBo servletRequestBo) {
    return "GET".equalsIgnoreCase(servletRequestBo.getMethod());
  }

  private static boolean ifHEAD(ServletRequestBo servletRequestBo) {
    return "HEAD".equalsIgnoreCase(servletRequestBo.getMethod());
  }

  private static boolean ifPOST(ServletRequestBo servletRequestBo) {
    return "POST".equalsIgnoreCase(servletRequestBo.getMethod());
  }

  private static boolean ifPUT(ServletRequestBo servletRequestBo) {
    return "PUT".equalsIgnoreCase(servletRequestBo.getMethod());
  }

  private static boolean ifDELETE(ServletRequestBo servletRequestBo) {
    return "DELETE".equalsIgnoreCase(servletRequestBo.getMethod());
  }

  private static boolean ifCONNECT(ServletRequestBo servletRequestBo) {
    return "CONNECT".equalsIgnoreCase(servletRequestBo.getMethod());
  }

  private static boolean ifOPTIONS(ServletRequestBo servletRequestBo) {
    return "OPTIONS".equalsIgnoreCase(servletRequestBo.getMethod());
  }

  private static boolean ifTRACE(ServletRequestBo servletRequestBo) {
    return "TRACE".equalsIgnoreCase(servletRequestBo.getMethod());
  }

  private static boolean ifPATCH(ServletRequestBo servletRequestBo) {
    return "PATCH".equalsIgnoreCase(servletRequestBo.getMethod());
  }

}
