package cn.addenda.porttrail.agent.transform.interceptor.servlet.javax;

import cn.addenda.porttrail.agent.AgentPackage;
import cn.addenda.porttrail.agent.PortTrailAgentStartException;
import cn.addenda.porttrail.agent.log.AgentPortTrailLoggerFactory;
import cn.addenda.porttrail.agent.transform.OverrideCallable;
import cn.addenda.porttrail.agent.transform.interceptor.AbstractEntryPointInterceptor;
import cn.addenda.porttrail.agent.transform.interceptor.Interceptor;
import cn.addenda.porttrail.agent.writer.http.AgentHttpWriter;
import cn.addenda.porttrail.common.constant.MediaType;
import cn.addenda.porttrail.common.entrypoint.EntryPoint;
import cn.addenda.porttrail.common.entrypoint.EntryPointType;
import cn.addenda.porttrail.common.pojo.http.HttpRequestFormData;
import cn.addenda.porttrail.common.pojo.http.LocaleData;
import cn.addenda.porttrail.common.pojo.http.bo.AbstractHttpExecution;
import cn.addenda.porttrail.common.pojo.http.bo.HttpRequestBo;
import cn.addenda.porttrail.common.pojo.http.bo.HttpResponseBo;
import cn.addenda.porttrail.common.util.UuidUtils;
import cn.addenda.porttrail.infrastructure.log.PortTrailLogger;
import cn.addenda.porttrail.infrastructure.writer.HttpWriter;
import net.bytebuddy.implementation.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.*;

public class JavaxServletServiceInterceptor extends AbstractEntryPointInterceptor implements Interceptor {

  private final HttpWriter httpWriter;

  // in kb
  private final int requestMaxBodyLength;
  // in kb
  private final int responseMaxBodyLength;

  private static final PortTrailLogger log =
          AgentPortTrailLoggerFactory.getInstance().getPortTrailLogger(JavaxServletServiceInterceptor.class);

  public JavaxServletServiceInterceptor() {
    this.httpWriter = AgentHttpWriter.getInstance();
    this.requestMaxBodyLength = initRequestMaxBodyLength();
    this.responseMaxBodyLength = initResponseMaxBodyLength();
  }

  private int initRequestMaxBodyLength() {
    Properties agentProperties = AgentPackage.getAgentProperties();
    String property = agentProperties.getProperty("httpWriter.request.maxBodyLength");
    try {
      return Integer.parseInt(property) * 1024;
    } catch (Exception e) {
      throw new PortTrailAgentStartException(String.format("加载httpWriter.request.maxBodyLength异常，配置值为：%s", property), e);
    }
  }

  private int initResponseMaxBodyLength() {
    Properties agentProperties = AgentPackage.getAgentProperties();
    String property = agentProperties.getProperty("httpWriter.response.maxBodyLength");
    try {
      return Integer.parseInt(property) * 1024;
    } catch (Exception e) {
      throw new PortTrailAgentStartException(String.format("加载httpWriter.response.maxBodyLength异常，配置值为：%s", property), e);
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
    log.info("TargetObj is [{}] and it's classloader is [{}].", targetObj, targetObj.getClass().getClassLoader());

    HttpServletRequest request = (HttpServletRequest) targetMethodArgs[0];
    HttpServletResponse response = (HttpServletResponse) targetMethodArgs[1];
    String requestId = UuidUtils.generateUuid();

    return callWithEntryPoint(request.getRequestURI(), () -> {

      String requestContentType = request.getContentType();
      JavaxContentCachingRequestWrapper requestWrapper = null;
      if (MediaType.ifRequestContentType(requestContentType)) {
        requestWrapper = new JavaxContentCachingRequestWrapper(request);
        targetMethodArgs[0] = requestWrapper;
      }

      JavaxContentCachingResponseWrapper responseWrapper = new JavaxContentCachingResponseWrapper(response);
      targetMethodArgs[1] = responseWrapper;

      Object call = zuper.call(targetMethodArgs);

      // ---------------
      // 处理HttpRequest
      // ---------------
      HttpRequestBo httpRequestBo = assembleHttpRequestBo(request, requestId);
      if (requestWrapper != null) {
        httpRequestBo.setContentLength(requestWrapper.getCachedContent().size());
        if (MediaType.ifRequestTextContentType(requestContentType)) {
          httpRequestBo.setBody(extractTextRequestBody(requestWrapper, requestId));
        } else if (MediaType.ifRequestMultipartFormContentType(requestContentType)) {
          httpRequestBo.setBody(extractMultipartFormRequestBody(requestWrapper.getParts()));
        } else if (MediaType.ifRequestBinaryContentType(requestContentType)) {
          httpRequestBo.setBody(HttpRequestBo.BODY_BYTE_ARRAY);
        }
      } else {
        httpRequestBo.setContentLength(HttpRequestBo.UNKNOWN_CONTENT_LENGTH);
        httpRequestBo.setBody(HttpRequestBo.UNSUPPORTED_CONTENT_TYPE);
      }
      httpWriter.writeHttpRequest(httpRequestBo);

      // ----------------
      // 处理HttpResponse
      // ----------------
      String responseContentType = response.getContentType();
      HttpResponseBo httpResponseBo = assembleHttpResponseBo(response, requestId);
      if (MediaType.ifResponseContentType(responseContentType)) {
        httpResponseBo.setContentLength(responseWrapper.getContentSize());
        if (MediaType.ifResponseTextContentType(responseContentType)) {
          httpResponseBo.setBody(extractTextResponseBody(responseWrapper, requestId));
        } else if (MediaType.ifResponseBinaryContentType(responseContentType)) {
          httpResponseBo.setBody(extractBinaryResponseBody(response));
        }
      } else {
        httpResponseBo.setContentLength(HttpResponseBo.UNKNOWN_CONTENT_LENGTH);
        httpResponseBo.setBody(HttpResponseBo.UNSUPPORTED_CONTENT_TYPE);
      }
      httpWriter.writeHttpResponse(httpResponseBo);

      return call;
    });

  }

  private HttpRequestBo assembleHttpRequestBo(HttpServletRequest request, String requestId) {
    HttpRequestBo httpRequestBo = new HttpRequestBo(requestId);
    httpRequestBo.setVersion(request.getProtocol());
    httpRequestBo.setScheme(request.getScheme());
    httpRequestBo.setMethod(request.getMethod());
    httpRequestBo.setUri(request.getRequestURI());
    httpRequestBo.setQueryString(request.getQueryString());
    httpRequestBo.setContentType(request.getContentType());
    httpRequestBo.setCharsetEncoding(request.getCharacterEncoding());
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
    httpRequestBo.setHeaderMap(headerMap);
    httpRequestBo.setDatetime(System.currentTimeMillis());
    httpRequestBo.setAllContentLength(request.getContentLength());
    Locale locale = request.getLocale();
    if (locale != null) {
      httpRequestBo.setLocale(new LocaleData(locale.getLanguage(), locale.getCountry(), locale.getVariant()));
    }
    return httpRequestBo;
  }

  private String extractTextRequestBody(JavaxContentCachingRequestWrapper request, String requestId) {
    // 如果请求体里有body，但是Controller未使用，body为blank
    byte[] contentAsByteArray = request.getContentAsByteArray();
    if (contentAsByteArray.length > 0) {
      if (request.getRequest().getContentLength() > requestMaxBodyLength) {
        return HttpRequestBo.BODY_EXCEED_LENGTH;
      } else {
        return convertBytesToString(contentAsByteArray, request.getCharacterEncoding(), requestId);
      }
    }
    return HttpRequestBo.BODY_EMPTY;
  }

  private List<HttpRequestFormData> extractMultipartFormRequestBody(Collection<Part> parts) {
    List<HttpRequestFormData> httpRequestFormDataList = new ArrayList<>();
    for (Part part : parts) {
      HttpRequestFormData httpRequestFormData = new HttpRequestFormData();
      httpRequestFormDataList.add(httpRequestFormData);
      httpRequestFormData.setName(part.getName());
      httpRequestFormData.setSize(part.getSize());
      Map<String, List<String>> headerMap = new HashMap<>();
      Collection<String> headerNames = part.getHeaderNames();
      for (String headerName : headerNames) {
        headerMap.put(headerName, new ArrayList<>(part.getHeaders(headerName)));
      }
      httpRequestFormData.setHeaderMap(headerMap);
    }
    return httpRequestFormDataList;
  }

  private HttpResponseBo assembleHttpResponseBo(HttpServletResponse response, String requestId) {
    HttpResponseBo httpResponseBo = new HttpResponseBo(requestId);
    httpResponseBo.setContentType(response.getContentType());
    httpResponseBo.setDatetime(System.currentTimeMillis());
    Locale locale = response.getLocale();
    if (locale != null) {
      httpResponseBo.setLocale(new LocaleData(locale.getLanguage(), locale.getCountry(), locale.getVariant()));
    }
    httpResponseBo.setCharsetEncoding(response.getCharacterEncoding());
    httpResponseBo.setStatus(response.getStatus());
    // 这里能获取到的header是在程序里设置的。在Tomcat或Jetty里设置的获取不到。
    Map<String, List<String>> headerMap = new HashMap<>();
    Collection<String> headerNames = response.getHeaderNames();
    for (String headerName : headerNames) {
      headerMap.put(headerName, new ArrayList<>(response.getHeaders(headerName)));
    }
    httpResponseBo.setHeaderMap(headerMap);

    return httpResponseBo;
  }

  private String extractTextResponseBody(JavaxContentCachingResponseWrapper response, String requestId) {
    byte[] contentAsByteArray = response.getContentAsByteArray();
    if (contentAsByteArray.length > 0) {
      if (contentAsByteArray.length > responseMaxBodyLength) {
        return HttpResponseBo.BODY_EXCEED_LENGTH;
      } else {
        return convertBytesToString(contentAsByteArray, response.getCharacterEncoding(), requestId);
      }
    }
    return HttpResponseBo.BODY_EMPTY;
  }

  private String extractBinaryResponseBody(HttpServletResponse response) {
    // 解析 ，获取attachment的filename
    String header = response.getHeader("Content-Disposition");
    if (header == null || header.isEmpty()) {
      return HttpResponseBo.DOWNLOAD_UNKNOWN_FILENAME;
    } else {
      String lowerInput = header.toLowerCase();
      String target = "filename=";
      int index = lowerInput.indexOf(target);
      if (index == -1) {
        return HttpResponseBo.DOWNLOAD_UNKNOWN_FILENAME;
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
      return result.isEmpty() ? HttpResponseBo.DOWNLOAD_UNKNOWN_FILENAME : result;
    }
  }

  private String convertBytesToString(byte[] bytes, String characterEncoding, String requestId) {
    try {
      return new String(bytes, (characterEncoding == null || characterEncoding.isEmpty()) ? AbstractHttpExecution.DEFAULT_CHARSET : characterEncoding);
    } catch (UnsupportedEncodingException e) {
      log.error("unsupported response character encoding [{}:{}], requestId: [{}].", characterEncoding, AbstractHttpExecution.DEFAULT_CHARSET, requestId);
    }
    return AbstractHttpExecution.UNSUPPORTED_CHARSET_ENCODING;
  }

  @Override
  public boolean ifOverride() {
    return true;
  }

  @Override
  protected EntryPoint entryPoint(String detail) {
    return EntryPoint.of(EntryPointType.SERVLET_JAVAX, detail);
  }

}
