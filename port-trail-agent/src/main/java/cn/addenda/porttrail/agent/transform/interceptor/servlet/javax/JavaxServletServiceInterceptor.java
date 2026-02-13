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
import cn.addenda.porttrail.common.entrypoint.EntryPointType;
import cn.addenda.porttrail.common.pojo.servlet.bo.*;
import cn.addenda.porttrail.common.util.UuidUtils;
import cn.addenda.porttrail.infrastructure.log.PortTrailLogger;
import cn.addenda.porttrail.infrastructure.writer.ServletWriter;
import net.bytebuddy.implementation.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.*;

public class JavaxServletServiceInterceptor extends AbstractDeduplicationEntryPointInterceptor implements Interceptor {

  private final ServletWriter servletWriter;

  private final int requestMaxBodyLength;

  private final int responseMaxBodyLength;

  private static final PortTrailLogger log =
          AgentPortTrailLoggerFactory.getInstance().getPortTrailLogger(JavaxServletServiceInterceptor.class);

  public JavaxServletServiceInterceptor() {
    this.servletWriter = AgentServletWriter.getInstance();
    this.requestMaxBodyLength = initRequestMaxBodyLength();
    this.responseMaxBodyLength = initResponseMaxBodyLength();
  }

  private int initRequestMaxBodyLength() {
    Properties agentProperties = AgentPackage.getAgentProperties();
    // in kb
    String property = agentProperties.getProperty("servletWriter.request.maxBodyLength");
    try {
      return Integer.parseInt(property) * 1024;
    } catch (Exception e) {
      throw new PortTrailAgentStartException(String.format("加载servletWriter.request.maxBodyLength异常，配置值为：%s", property), e);
    }
  }

  private int initResponseMaxBodyLength() {
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
      if (MediaType.ifRequestContentType(requestContentType)) {
        requestWrapper = new JavaxContentCachingRequestWrapper(request);
        targetMethodArgs[0] = requestWrapper;
      }

      JavaxContentCachingResponseWrapper responseWrapper = new JavaxContentCachingResponseWrapper(response);
      targetMethodArgs[1] = responseWrapper;

      Object call = zuper.call(targetMethodArgs);

      // ------------------
      // 处理ServletRequest
      // ------------------
      ServletRequestBo servletRequestBo = assembleServletRequestBo(request, executionId);
      if (requestWrapper != null) {
        servletRequestBo.setContentLength(requestWrapper.getCachedContent().size());
        if (servletRequestBo.getContentType() == null) {
          servletRequestBo.setBody(null);
        } else {
          if (MediaType.ifRequestTextContentType(requestContentType)) {
            servletRequestBo.setBody(extractTextRequestBody(requestWrapper, executionId));
          } else if (MediaType.ifRequestMultipartFormContentType(requestContentType)) {
            servletRequestBo.setBody(extractMultipartFormRequestBody(requestWrapper.getParts(), requestWrapper));
          } else if (MediaType.ifRequestBinaryContentType(requestContentType)) {
            servletRequestBo.setBody(ServletRequestBo.BODY_BYTE_ARRAY);
          }
        }
      } else {
        servletRequestBo.setContentLength(ServletRequestBo.UNKNOWN_CONTENT_LENGTH);
        servletRequestBo.setBody(AbstractServletExecution.UNSUPPORTED_CONTENT_TYPE);
      }
      servletWriter.writeServletRequest(servletRequestBo);

      // -------------------
      // 处理ServletResponse
      // -------------------
      String responseContentType = response.getContentType();
      ServletResponseBo servletResponseBo = assembleServletResponseBo(response, executionId);
      if (MediaType.ifResponseContentType(responseContentType)) {
        servletResponseBo.setContentLength(responseWrapper.getContentSize());
        if (MediaType.ifResponseTextContentType(responseContentType)) {
          servletResponseBo.setBody(extractTextResponseBody(responseWrapper, executionId));
        } else if (MediaType.ifResponseBinaryContentType(responseContentType)) {
          servletResponseBo.setBody(extractBinaryResponseBody(response, servletRequestBo.getCharsetEncoding()));
        }
      } else {
        servletResponseBo.setContentLength(ServletResponseBo.UNKNOWN_CONTENT_LENGTH);
        servletResponseBo.setBody(AbstractServletExecution.UNSUPPORTED_CONTENT_TYPE);
      }
      servletWriter.writeServletResponse(servletResponseBo);

      return call;
    });

  }

  private ServletRequestBo assembleServletRequestBo(HttpServletRequest request, String executionId) {
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
    return servletRequestBo;
  }

  private String extractTextRequestBody(JavaxContentCachingRequestWrapper request, String executionId) {
    // 如果请求体里有body，但是Controller未使用，body为blank
    byte[] contentAsByteArray = request.getContentAsByteArray();
    if (contentAsByteArray.length > 0) {
      if (request.getRequest().getContentLength() > requestMaxBodyLength) {
        return ServletRequestBo.BODY_EXCEED_LENGTH;
      } else {
        return convertBytesToString(contentAsByteArray, request.getCharacterEncoding(), executionId);
      }
    }
    return ServletRequestBo.BODY_EMPTY;
  }

  private ServletRequestFormDataList extractMultipartFormRequestBody(Collection<Part> parts, HttpServletRequest request) {
    ServletRequestFormDataList servletRequestFormDataList = new ServletRequestFormDataList();
    for (Part part : parts) {
      ServletRequestFormData servletRequestFormData = new ServletRequestFormData();
      servletRequestFormDataList.add(servletRequestFormData);
      servletRequestFormData.setContentType(part.getContentType());
      servletRequestFormData.setName(part.getName());
      servletRequestFormData.setSize(part.getSize());
      Map<String, List<String>> headerMap = new HashMap<>();
      Collection<String> headerNames = part.getHeaderNames();
      for (String headerName : headerNames) {
        headerMap.put(headerName, new ArrayList<>(part.getHeaders(headerName)));
      }
      servletRequestFormData.setHeaderMap(headerMap);
      servletRequestFormData.setSubmittedFileName(part.getSubmittedFileName());
      if (servletRequestFormData.getSubmittedFileName() == null) {
        servletRequestFormData.setValues(request.getParameterValues(part.getName()));
      }
    }
    return servletRequestFormDataList;
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

    return servletResponseBo;
  }

  private String extractTextResponseBody(JavaxContentCachingResponseWrapper response, String executionId) {
    byte[] contentAsByteArray = response.getContentAsByteArray();
    if (contentAsByteArray.length > 0) {
      if (contentAsByteArray.length > responseMaxBodyLength) {
        return ServletResponseBo.BODY_EXCEED_LENGTH;
      } else {
        return convertBytesToString(contentAsByteArray, response.getCharacterEncoding(), executionId);
      }
    }
    return ServletResponseBo.BODY_EMPTY;
  }

  private String extractBinaryResponseBody(HttpServletResponse response, String charsetEncoding) {
    // 解析 ，获取attachment的filename
    String header = response.getHeader("Content-Disposition");
    if (header == null || header.isEmpty()) {
      return ServletResponseBo.DOWNLOAD_UNKNOWN_FILENAME;
    } else {
      String lowerInput = header.toLowerCase();
      String target = "filename=";
      int index = lowerInput.indexOf(target);
      if (index == -1) {
        return ServletResponseBo.DOWNLOAD_UNKNOWN_FILENAME;
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
        return ServletResponseBo.DOWNLOAD_UNKNOWN_FILENAME;
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

  private String convertBytesToString(byte[] bytes, String characterEncoding, String executionId) {
    try {
      return new String(bytes, (characterEncoding == null || characterEncoding.isEmpty()) ? AbstractServletExecution.DEFAULT_CHARSET : characterEncoding);
    } catch (UnsupportedEncodingException e) {
      log.error("unsupported response character encoding [{}:{}], executionId: [{}].", characterEncoding, AbstractServletExecution.DEFAULT_CHARSET, executionId);
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
