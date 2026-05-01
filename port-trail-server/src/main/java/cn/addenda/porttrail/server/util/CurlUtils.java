package cn.addenda.porttrail.server.util;

import cn.addenda.component.base.util.UrlUtils;
import cn.addenda.porttrail.common.constant.MediaType;
import cn.addenda.porttrail.common.pojo.servlet.bo.AbstractServletExecution;
import cn.addenda.porttrail.common.pojo.servlet.bo.ServletRequestBo;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

  /**
   * 将ServletRequestBo转换为curl命令
   *
   * @param requestBo Servlet请求业务对象
   * @return curl命令字符串
   */
  public static String toCurl(ServletRequestBo requestBo) {
    if (requestBo == null) {
      return "";
    }

    StringBuilder curl = new StringBuilder();

    // 1. 构建基础URL
    String url = buildUrl(requestBo);

    // 2. 添加HTTP方法
    curl.append(String.format(CURL_TEMPLATE, requestBo.getMethod(), url)).append("\n");

    // 3. 添加请求头
    appendHeaders(curl, requestBo);

    // 4. 添加请求体
    appendBody(curl, requestBo);

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
      url.append("?").append(requestBo.getQueryString());
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
  private static void appendHeaders(StringBuilder curl, ServletRequestBo requestBo) {
    Map<String, List<String>> headerMap = requestBo.getHeaderMap();
    if (headerMap == null || headerMap.isEmpty()) {
      return;
    }

    for (Map.Entry<String, List<String>> entry : headerMap.entrySet()) {
      String headerName = entry.getKey();
      List<String> values = entry.getValue();

      if ("content-type".equalsIgnoreCase(headerName) && requestBo.getContentType() != null) {
        curl.append(String.format(HEADER_TEMPLATE, escapeHeader(headerName), escapeHeader(requestBo.getContentType()))).append("\n");
        continue;
      }

      if (values != null) {
        for (String value : values) {
          curl.append(String.format(HEADER_TEMPLATE, escapeHeader(headerName), escapeHeader(value))).append("\n");
        }
      }
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

    if (body == null || ServletRequestBo.BODY_EMPTY.equals(body)) {
      return;
    }

    // 不支持的内容类型
    if (MediaType.ifRequestTextContentType(requestBo.getContentType())) {
      // 超出长度限制
      if (ServletRequestBo.BODY_EXCEED_LENGTH.equals(body)) {
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
//                      curl.append(String.format(DATA_URLENCODE_TEMPLATE, key + "=" + UrlUtils.decode(value, enc))).append("\n");
//                    } else {
//                      curl.append(String.format(DATA_URLENCODE_TEMPLATE, param)).append("\n");
//                    }
//                  });
//        } else {
//          curl.append(String.format(DATA_TEMPLATE, escapeData(bodyStr))).append("\n");
//        }

        curl.append(String.format(DATA_TEMPLATE, escapeData(bodyStr))).append("\n");
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

}
