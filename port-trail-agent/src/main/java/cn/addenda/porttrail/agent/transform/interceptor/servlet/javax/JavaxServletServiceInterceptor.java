package cn.addenda.porttrail.agent.transform.interceptor.servlet.javax;

import cn.addenda.porttrail.agent.AgentPackage;
import cn.addenda.porttrail.agent.PortTrailAgentStartException;
import cn.addenda.porttrail.agent.log.AgentPortTrailLoggerFactory;
import cn.addenda.porttrail.agent.transform.OverrideCallable;
import cn.addenda.porttrail.agent.transform.interceptor.AbstractDeduplicationEntryPointInterceptor;
import cn.addenda.porttrail.agent.transform.interceptor.Interceptor;
import cn.addenda.porttrail.agent.writer.servlet.AgentServletWriter;
import cn.addenda.porttrail.common.constant.MediaType;
import cn.addenda.porttrail.common.entrypoint.EntryPoint;
import cn.addenda.porttrail.common.entrypoint.EntryPointSnapshot;
import cn.addenda.porttrail.common.entrypoint.EntryPointType;
import cn.addenda.porttrail.common.exception.PortTrailException;
import cn.addenda.porttrail.common.pojo.LocaleData;
import cn.addenda.porttrail.common.pojo.FormData;
import cn.addenda.porttrail.common.pojo.FormDataList;
import cn.addenda.porttrail.common.pojo.servlet.bo.*;
import cn.addenda.porttrail.common.util.UuidUtils;
import cn.addenda.porttrail.infrastructure.entrypoint.EntryPointStackContext;
import cn.addenda.porttrail.infrastructure.log.PortTrailLogger;
import cn.addenda.porttrail.infrastructure.writer.ServletWriter;
import net.bytebuddy.implementation.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.*;

public class JavaxServletServiceInterceptor extends AbstractDeduplicationEntryPointInterceptor implements Interceptor {

  private final ServletWriter servletWriter;

  private static final int requestMaxBodyLength;

  private static final int responseMaxBodyLength;

  private static final PortTrailLogger log =
          AgentPortTrailLoggerFactory.getInstance().getPortTrailLogger(JavaxServletServiceInterceptor.class);

  public JavaxServletServiceInterceptor() {
    this.servletWriter = AgentServletWriter.getInstance();
  }

  static {
    requestMaxBodyLength = initRequestMaxBodyLength();
    responseMaxBodyLength = initResponseMaxBodyLength();
  }

  private static int initRequestMaxBodyLength() {
    Properties agentProperties = AgentPackage.getAgentProperties();
    // in kb
    String property = agentProperties.getProperty("servletWriter.request.maxBodyLength");
    try {
      return Integer.parseInt(property) * 1024;
    } catch (Exception e) {
      throw new PortTrailAgentStartException(String.format("加载servletWriter.request.maxBodyLength异常，配置值为：%s", property), e);
    }
  }

  private static int initResponseMaxBodyLength() {
    Properties agentProperties = AgentPackage.getAgentProperties();
    String property = agentProperties.getProperty("servletWriter.response.maxBodyLength");
    try {
      // in kb
      return Integer.parseInt(property) * 1024;
    } catch (Exception e) {
      throw new PortTrailAgentStartException(String.format("加载servletWriter.response.maxBodyLength异常，配置值为：%s", property), e);
    }
  }

  /**
   * 被@RuntimeType标注的方法就是被委托的方法
   */
  @RuntimeType
  public Object intercept(
          // byteBuddy会在运行期间给被注定注解修饰的方法参数进行赋值:

          // 当前被拦截的、动态生成的那个对象
          @This Object targetObj,
          // 被调用的原始方法
          @Origin Method targetMethod,
          // 被拦截的方法参数
          @AllArguments Object[] targetMethodArgs,
          // 当前被拦截的、动态生成的那个对象的父类对象
          @Super Object concurrentBag,
          // 用于调用父类的方法。
          @Morph OverrideCallable zuper
  ) throws Exception {

    HttpServletRequest request = (HttpServletRequest) targetMethodArgs[0];
    HttpServletResponse response = (HttpServletResponse) targetMethodArgs[1];

    if (!ifPushEntryPoint(request.getRequestURI())) {
      return zuper.call(targetMethodArgs);
    }

    log.info("TargetObj is [{}] and it's classloader is [{}].", targetObj, targetObj.getClass().getClassLoader());

    String executionId = UuidUtils.generateUuid();

    return callWithEntryPoint(request.getRequestURI(), () -> {

      String requestContentType = request.getContentType();
      JavaxContentCachingRequestWrapper requestWrapper = null;
      // requestContentType为null时，虽然规范上不会有body，但也有可能有body
      if (requestContentType == null
              || MediaType.ifRequestContentType(requestContentType)) {
        requestWrapper = new JavaxContentCachingRequestWrapper(request);
        targetMethodArgs[0] = requestWrapper;
      }

      JavaxContentCachingResponseWrapper responseWrapper = new JavaxContentCachingResponseWrapper(response);
      EntryPointSnapshot requestEntryPointSnapshot = EntryPointStackContext.snapshot();
      targetMethodArgs[1] = responseWrapper;

      Object call = zuper.call(targetMethodArgs);

      // ------------------
      // 处理ServletRequest
      // ------------------
      ServletRequestBo servletRequestBo = assembleServletRequestBo(request, requestEntryPointSnapshot, executionId);
      if (requestWrapper != null) {
        servletRequestBo.setContentLength(requestWrapper.getCachedContent().size());
        if (servletRequestBo.getContentType() == null) {
          // 如果没有content-type，按text处理，如果报错就设置为UNSUPPORTED_CONTENT_TYPE
          try {
            servletRequestBo.setBody(extractTextRequestBody(requestWrapper, executionId, requestContentType, servletRequestBo.getQueryString(), true));
          } catch (Throwable t) {
            servletRequestBo.setBody(AbstractServletExecution.UNSUPPORTED_CONTENT_TYPE);
          }
        } else {
          if (MediaType.ifRequestTextContentType(requestContentType)) {
            servletRequestBo.setBody(extractTextRequestBody(requestWrapper, executionId, requestContentType, servletRequestBo.getQueryString(), false));
          } else if (MediaType.ifRequestMultipartFormContentType(requestContentType)) {
            servletRequestBo.setBody(extractMultipartFormRequestBody(getParts(requestWrapper), requestWrapper));
          } else if (MediaType.ifRequestBinaryContentType(requestContentType)) {
            servletRequestBo.setBody(ServletRequestBo.BODY_BYTE_ARRAY);
          }
        }
      } else {
        servletRequestBo.setContentLength(AbstractServletExecution.UNKNOWN_CONTENT_LENGTH);
        servletRequestBo.setBody(AbstractServletExecution.UNSUPPORTED_CONTENT_TYPE);
      }
      servletWriter.writeServletRequest(servletRequestBo);

      // -------------------
      // 处理ServletResponse
      // -------------------
      String responseContentType = response.getContentType();
      ServletResponseBo servletResponseBo = assembleServletResponseBo(response, executionId);
      if (responseContentType == null) {
        servletResponseBo.setContentLength(responseWrapper.getContentSize());
        // 如果没有content-type，按text处理，如果报错就设置为UNSUPPORTED_CONTENT_TYPE
        try {
          servletResponseBo.setBody(extractTextResponseBody(responseWrapper, executionId, true));
        } catch (Throwable t) {
          servletResponseBo.setBody(AbstractServletExecution.UNSUPPORTED_CONTENT_TYPE);
        }
      } else if (MediaType.ifResponseContentType(responseContentType)) {
        servletResponseBo.setContentLength(responseWrapper.getContentSize());
        if (MediaType.ifResponseTextContentType(responseContentType)) {
          servletResponseBo.setBody(extractTextResponseBody(responseWrapper, executionId, false));
        } else if (MediaType.ifResponseBinaryContentType(responseContentType)) {
          servletResponseBo.setBody(extractBinaryResponseBody(response, servletResponseBo.getCharsetEncoding()));
        }
      } else if (MediaType.ifResponseHttpContentType(responseContentType)) {
        try {
          servletResponseBo.setBody(extractTextResponseBody(responseWrapper, executionId, true));
        } catch (Throwable t) {
          servletResponseBo.setBody(AbstractServletExecution.UNSUPPORTED_CONTENT_TYPE);
        }
      } else {
        servletResponseBo.setContentLength(AbstractServletExecution.UNKNOWN_CONTENT_LENGTH);
        servletResponseBo.setBody(AbstractServletExecution.UNSUPPORTED_CONTENT_TYPE);
      }
      servletWriter.writeServletResponse(servletResponseBo);

      responseWrapper.clearContent();
      responseWrapper.flushBuffer();

      return call;
    });

  }

  public static final String MULTIPART_CONFIG_ELEMENT = "org.eclipse.jetty.multipartConfig";
  public static final String MULTIPARTS = "org.eclipse.jetty.multiParts";

  private static final Collection<Part> unsupportedParts = new HashSet<>();

  private Collection<Part> getParts(JavaxContentCachingRequestWrapper requestWrapper)
          throws ServletException, IOException {
    ServletRequest request = requestWrapper.getRequest();
    if (Objects.equals("org.eclipse.jetty.server.Request", request.getClass().getName())) {
      if (request.getAttribute(MULTIPART_CONFIG_ELEMENT) == null && request.getAttribute(MULTIPARTS) == null) {
        // todo resteasy框架在未配置MultipartConfigElement的情况下，也能读取form表单，但是此时Request的getParts()方法是会报异常的
        // todo 暂时不知道怎么处理这种场景
        return unsupportedParts;
      }
    }
    return requestWrapper.getParts();
  }

  private ServletRequestBo assembleServletRequestBo(HttpServletRequest request, EntryPointSnapshot entryPointSnapshot, String executionId) {
    ServletRequestBo servletRequestBo = new ServletRequestBo(executionId);
    servletRequestBo.setVersion(request.getProtocol());
    servletRequestBo.setScheme(request.getScheme());
    servletRequestBo.setMethod(request.getMethod());
    servletRequestBo.setUri(request.getRequestURI());
    servletRequestBo.setQueryString(request.getQueryString());
    servletRequestBo.setContentType(request.getContentType());
    servletRequestBo.setCharsetEncoding(request.getCharacterEncoding());
    Map<String, List<String>> headerMap = new HashMap<>();
    Enumeration<String> headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String headerName = headerNames.nextElement();
      List<String> headerValueList = new ArrayList<>();
      Enumeration<String> headerValues = request.getHeaders(headerName);
      while (headerValues.hasMoreElements()) {
        headerValueList.add(headerValues.nextElement());
      }
      headerMap.put(headerName, headerValueList);
    }
    servletRequestBo.setHeaderMap(headerMap);
    servletRequestBo.setDatetime(System.currentTimeMillis());
    servletRequestBo.setAllContentLength(request.getContentLength());
    Locale locale = request.getLocale();
    if (locale != null) {
      servletRequestBo.setLocale(new LocaleData(locale.getLanguage(), locale.getCountry(), locale.getVariant()));
    }
    servletRequestBo.setEntryPointSnapshot(entryPointSnapshot);
    return servletRequestBo;
  }

  private String extractTextRequestBody(JavaxContentCachingRequestWrapper request, String executionId,
                                        String contentType, String queryString, boolean ifThrow) {
    // 如果请求体里有body，但是Controller未使用，body为blank
    byte[] contentAsByteArray = request.getContentAsByteArray();
    if (contentAsByteArray.length > 0) {
      if (request.getRequest().getContentLength() > requestMaxBodyLength) {
        return AbstractServletExecution.BODY_EXCEED_LENGTH;
      } else {
        // 如果请求体配置了压缩，server(tomcat,jetty...)收到的原始字节是压缩后的字节
        // springboot的执行链路是：filter -> servlet -> interceptor。
        // 对于springboot来说，一般是在filter里进行解压。
        // 所以这里获取到的是解压后的字节。
        String body = convertBytesToString(contentAsByteArray, request.getCharacterEncoding(), executionId, ifThrow);
        if (MediaType.ifRequestFormUrlencodedContentType(contentType)) {
          /**
           * 当contentType是{@link MediaType#APPLICATION_FORM_URLENCODED_VALUE}时。Body里的数据格式是：f1=123
           * queryString的数据格式是：nodeType=3。
           * 此时extract出来的body是nodeType=3&f1=123。
           *
           * 更极端的案例，body里的数据是nodeType=4&f1=123，queryString的数据是nodeType=3。
           * 此时extract出来的body是nodeType=3&nodeType=4&f1=123。
           *
           * 在StringMVC中，使用'@RequestParam('nodeType') String nodeType'得到的数据是3,4；
           * 在StringMVC中，使用'@RequestParam('nodeType') Integer nodeType'得到的数据是3（优先取queryString）
           *
           * 我们这里需要从body中移除queryString。
           */
          body = removeQueryStringFromBody(body, queryString, executionId);
        }
        return body;
      }
    }
    return AbstractServletExecution.BODY_EMPTY;
  }

  /**
   * 从form-urlencoded的body中移除与queryString完全匹配的参数对（key和value都相同）
   * <p>
   * 处理逻辑：
   * - queryString: nodeType=3
   * - body: nodeType=3&nodeType=4&f1=123
   * - 结果: nodeType=4&f1=123 （只移除key=value完全匹配的nodeType=3）
   * <p>
   * queryString中的每个参数对只会从body中移除第一个完全匹配（key和value都相同）的参数对
   *
   * @param body        原始请求体，格式如: nodeType=3&nodeType=4&f1=123
   * @param queryString 查询字符串，格式如: nodeType=3
   * @return 移除重复参数后的body，如: nodeType=4&f1=123
   */
  private String removeQueryStringFromBody(String body, String queryString, String executionId) {
    if (body == null || body.isEmpty()
            || AbstractServletExecution.UNSUPPORTED_CONTENT_TYPE.equals(body)
            || AbstractServletExecution.UNSUPPORTED_CHARSET_ENCODING.equals(body)
            || AbstractServletExecution.BODY_EMPTY.equals(body)
            || AbstractServletExecution.BODY_EXCEED_LENGTH.equals(body)
            || ServletRequestBo.BODY_BYTE_ARRAY.equals(body)
    ) {
      return body;
    }

    if (queryString == null || queryString.isEmpty()) {
      return body;
    }

    try {
      // 解析queryString中的所有参数对（key-value）
      List<String[]> queryParams = parseParameters(queryString);

      if (queryParams.isEmpty()) {
        return body;
      }

      // 解析body中的所有参数对（key-value）
      List<String[]> bodyParams = parseParameters(body);

      // 标记需要移除的body参数索引
      Set<Integer> indicesToRemove = new HashSet<>();

      // 对于queryString中的每个参数对，找到body中第一个完全匹配（key和value都相同）的参数并标记移除
      for (String[] queryParam : queryParams) {
        for (int i = 0; i < bodyParams.size(); i++) {
          // 如果该位置已经被标记移除，跳过
          if (indicesToRemove.contains(i)) {
            continue;
          }

          String[] bodyParam = bodyParams.get(i);
          // 检查key和value是否都相同
          if (isParameterMatch(queryParam, bodyParam)) {
            indicesToRemove.add(i);
            break; // 只移除第一个完全匹配的
          }
        }
      }

      // 收集未被标记移除的参数
      List<String> filteredParams = new ArrayList<>();
      for (int i = 0; i < bodyParams.size(); i++) {
        if (!indicesToRemove.contains(i)) {
          String[] param = bodyParams.get(i);
          if (param.length > 1) {
            filteredParams.add(param[0] + "=" + param[1]);
          } else {
            filteredParams.add(param[0]);
          }
        }
      }

      // 重新组装body
      if (filteredParams.isEmpty()) {
        return AbstractServletExecution.BODY_EMPTY;
      }

      return String.join("&", filteredParams);
    } catch (Exception e) {
      // 如果解析失败，返回原始body
      log.warn("Failed to remove queryString[{}] from body[{}], executionId: {}.",
              queryString, body, executionId, e);
      return body;
    }
  }

  /**
   * 检查两个参数对是否完全匹配（key和value都相同）
   *
   * @param queryParam 查询参数 [name, value] 或 [name]
   * @param bodyParam  body参数 [name, value] 或 [name]
   * @return 是否完全匹配
   */
  private boolean isParameterMatch(String[] queryParam, String[] bodyParam) {
    if (queryParam == null || bodyParam == null) {
      return false;
    }

    // 两者都必须有name
    if (queryParam.length == 0 || bodyParam.length == 0) {
      return false;
    }

    // 比较name
    if (!Objects.equals(queryParam[0], bodyParam[0])) {
      return false;
    }

    // 如果一个有value，一个没有value，则不匹配
    if (queryParam.length != bodyParam.length) {
      return false;
    }

    // 如果都有value，比较value
    if (queryParam.length == 2) {
      return Objects.equals(queryParam[1], bodyParam[1]);
    }

    // 都没有value，name相同就匹配
    return true;
  }

  /**
   * 解析URL编码字符串中的参数对列表
   *
   * @param body 查询字符串，如: nodeType=3&f1=123&nodeType=5
   * @return 参数对列表，如: [[nodeType, 3], [f1, 123], [nodeType, 5]]
   */
  private List<String[]> parseParameters(String body) {
    List<String[]> params = new ArrayList<>();
    if (body == null || body.isEmpty()) {
      return params;
    }

    String[] pairs = body.split("&");
    for (String pair : pairs) {
      if (pair.isEmpty()) {
        continue;
      }
      int equalIndex = pair.indexOf('=');
      if (equalIndex > 0) {
        String paramName = pair.substring(0, equalIndex);
        String paramValue = pair.substring(equalIndex + 1);
        params.add(new String[]{paramName, paramValue});
      } else if (equalIndex == -1) {
        params.add(new String[]{pair});
      }
    }
    return params;
  }

  private FormDataList extractMultipartFormRequestBody(Collection<Part> parts, HttpServletRequest request) {
    FormDataList formDataList = new FormDataList();
    if (parts == unsupportedParts) {
      FormData formData = new FormData();
      formData.setContentType(null);
      formData.setName("unsupportedParts");
      formData.setSize(0L);
      formData.setHeaderMap(new HashMap<>());
      formData.setSubmittedFileName(null);
      formData.setValues(null);
      formDataList.add(formData);
      return formDataList;
    }
    for (Part part : parts) {
      FormData formData = new FormData();
      formDataList.add(formData);
      formData.setContentType(part.getContentType());
      formData.setName(part.getName());
      formData.setSize(part.getSize());
      Map<String, List<String>> headerMap = new HashMap<>();
      Collection<String> headerNames = part.getHeaderNames();
      for (String headerName : headerNames) {
        headerMap.put(headerName, new ArrayList<>(part.getHeaders(headerName)));
      }
      formData.setHeaderMap(headerMap);
      formData.setSubmittedFileName(part.getSubmittedFileName());
      if (formData.getSubmittedFileName() == null) {
        formData.setValues(request.getParameterValues(part.getName()));
      }
    }
    return formDataList;
  }

  private ServletResponseBo assembleServletResponseBo(HttpServletResponse response, String executionId) {
    ServletResponseBo servletResponseBo = new ServletResponseBo(executionId);
    servletResponseBo.setContentType(response.getContentType());
    servletResponseBo.setDatetime(System.currentTimeMillis());
    Locale locale = response.getLocale();
    if (locale != null) {
      servletResponseBo.setLocale(new LocaleData(locale.getLanguage(), locale.getCountry(), locale.getVariant()));
    }
    servletResponseBo.setCharsetEncoding(response.getCharacterEncoding());
    servletResponseBo.setStatus(response.getStatus());
    // 这里能获取到的header是在程序里设置的。在Tomcat或Jetty里设置的获取不到。
    Map<String, List<String>> headerMap = new HashMap<>();
    Collection<String> headerNames = response.getHeaderNames();
    for (String headerName : headerNames) {
      headerMap.put(headerName, new ArrayList<>(response.getHeaders(headerName)));
    }
    servletResponseBo.setHeaderMap(headerMap);
    servletResponseBo.setEntryPointSnapshot(EntryPointStackContext.snapshot());
    return servletResponseBo;
  }

  private String extractTextResponseBody(JavaxContentCachingResponseWrapper response, String executionId, boolean ifThrow) {
    byte[] contentAsByteArray = response.getContentAsByteArray();
    if (contentAsByteArray.length > 0) {
      if (contentAsByteArray.length > responseMaxBodyLength) {
        return AbstractServletExecution.BODY_EXCEED_LENGTH;
      } else {
        return convertBytesToString(contentAsByteArray, response.getCharacterEncoding(), executionId, ifThrow);
      }
    }
    return AbstractServletExecution.BODY_EMPTY;
  }

  private String extractBinaryResponseBody(HttpServletResponse response, String charsetEncoding) {
    // 解析 ，获取attachment的filename
    String header = response.getHeader("Content-Disposition");
    if (header == null || header.isEmpty()) {
      return ServletResponseBo.UNKNOWN_FILENAME;
    } else {
      String lowerInput = header.toLowerCase();
      String target = "filename=";
      int index = lowerInput.indexOf(target);
      if (index == -1) {
        return ServletResponseBo.UNKNOWN_FILENAME;
      }
      // 使用原字符串截取保持原始大小写
      String result = header.substring(index + target.length());
      // 获取第一个分号前的内容
      int semicolonIndex = result.indexOf(';');
      if (semicolonIndex != -1) {
        result = result.substring(0, semicolonIndex);
      }
      // 去除首尾空格和引号
      result = result.trim();
      if (result.startsWith("\"") && result.endsWith("\"") && result.length() > 1) {
        result = result.substring(1, result.length() - 1);
      }
      if (result.isEmpty()) {
        return ServletResponseBo.UNKNOWN_FILENAME;
      }

      if (charsetEncoding == null) {
        return result;
      }
      try {
        return URLDecoder.decode(result, charsetEncoding);
      } catch (UnsupportedEncodingException e) {
        return result;
      }
    }
  }

  private String convertBytesToString(byte[] bytes, String characterEncoding, String executionId, boolean ifThrow) {
    try {
      return new String(bytes, (characterEncoding == null || characterEncoding.isEmpty()) ? AbstractServletExecution.DEFAULT_CHARSET : characterEncoding);
    } catch (UnsupportedEncodingException e) {
      if (ifThrow) {
        throw new PortTrailException(e);
      }
      log.error("unsupported character encoding [{}:{}], executionId: [{}].", characterEncoding, AbstractServletExecution.DEFAULT_CHARSET, executionId);
    }
    return AbstractServletExecution.UNSUPPORTED_CHARSET_ENCODING;
  }

  @Override
  public boolean ifOverride() {
    return true;
  }

  @Override
  protected EntryPoint entryPoint(String detail) {
    return EntryPoint.of(EntryPointType.SERVLET_JAVAX, detail);
  }

  private static final ThreadLocal<Deque<String>> deduplicationStack = new ThreadLocal<Deque<String>>() {
    @Override
    public String toString() {
      return getClass().getName() + "@" + JavaxServletServiceInterceptor.class.getName() + "@" + Integer.toHexString(hashCode());
    }
  };

  @Override
  protected ThreadLocal<Deque<String>> getDeduplicationStack() {
    return deduplicationStack;
  }

}
