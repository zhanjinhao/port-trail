package cn.addenda.porttrail.agent.test.servlet.javax;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.security.Principal;
import java.util.*;

/**
 * 轻量级 fake {@link HttpServletRequest} 用于测试 {@code JavaxContentCachingRequestWrapper}。
 * <p>
 * 只实现 wrapper 实际会调用的方法，其余返回 null / 0 / false / 空集合。
 *
 * <ul>
 *   <li>{@code getInputStream()} 返回读取预置 byte[] 的 {@link ServletInputStream}。</li>
 *   <li>{@code getContentLength()} 返回预置 byte[].length。</li>
 *   <li>每次调用 {@code getInputStream()} 创建新的流实例，支持重复读取（模拟容器行为）。</li>
 * </ul>
 */
public class FakeHttpServletRequest implements HttpServletRequest {

  private final byte[] input;
  private String characterEncoding = "UTF-8";
  private String contentType;
  private String method = "GET";
  private Map<String, String[]> parameterMap = Collections.emptyMap();

  public FakeHttpServletRequest(byte[] input) {
    this.input = (input != null) ? input : new byte[0];
  }

  public FakeHttpServletRequest(byte[] input, String contentType) {
    this(input);
    this.contentType = contentType;
  }

  // ---------- key methods used by wrapper ----------

  @Override
  public ServletInputStream getInputStream() {
    return new ServletInputStream() {
      private final ByteArrayInputStream bais = new ByteArrayInputStream(input);
      private boolean finished;

      @Override
      public int read() {
        int b = bais.read();
        if (b == -1) finished = true;
        return b;
      }

      @Override
      public int read(byte[] b, int off, int len) {
        int count = bais.read(b, off, len);
        if (count == -1) finished = true;
        return count;
      }

      @Override
      public boolean isFinished() {
        return finished || bais.available() == 0;
      }

      @Override
      public boolean isReady() {
        return true;
      }

      @Override
      public void setReadListener(ReadListener listener) {
      }
    };
  }

  @Override
  public String getCharacterEncoding() {
    return characterEncoding;
  }

  @Override
  public void setCharacterEncoding(String env) {
    this.characterEncoding = env;
  }

  @Override
  public int getContentLength() {
    // 空 body 返回 -1，模拟无 Content-Length 头的 form POST，避免 FastByteArrayOutputStream(0) 异常
    return input.length > 0 ? input.length : -1;
  }

  @Override
  public long getContentLengthLong() {
    return input.length;
  }

  @Override
  public String getContentType() {
    return contentType;
  }

  @Override
  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public void setParameterMap(Map<String, String[]> parameterMap) {
    this.parameterMap = (parameterMap != null) ? parameterMap : Collections.emptyMap();
  }

  @Override
  public BufferedReader getReader() {
    try {
      return new BufferedReader(new InputStreamReader(getInputStream(), getCharacterEncoding()));
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  // ---------- remaining stubs ----------

  @Override public String getAuthType() { return null; }
  @Override public Cookie[] getCookies() { return null; }
  @Override public long getDateHeader(String name) { return -1; }
  @Override public String getHeader(String name) { return null; }
  @Override public Enumeration<String> getHeaders(String name) { return Collections.emptyEnumeration(); }
  @Override public Enumeration<String> getHeaderNames() { return Collections.emptyEnumeration(); }
  @Override public int getIntHeader(String name) { return -1; }
  @Override public String getPathInfo() { return null; }
  @Override public String getPathTranslated() { return null; }
  @Override public String getContextPath() { return ""; }
  @Override public String getQueryString() { return null; }
  @Override public String getRemoteUser() { return null; }
  @Override public boolean isUserInRole(String role) { return false; }
  @Override public Principal getUserPrincipal() { return null; }
  @Override public String getRequestedSessionId() { return null; }
  @Override public String getRequestURI() { return "/"; }
  @Override public StringBuffer getRequestURL() { return new StringBuffer("/"); }
  @Override public String getServletPath() { return "/"; }
  @Override public HttpSession getSession(boolean create) { return null; }
  @Override public HttpSession getSession() { return null; }
  @Override public String changeSessionId() { return null; }
  @Override public boolean isRequestedSessionIdValid() { return false; }
  @Override public boolean isRequestedSessionIdFromCookie() { return false; }
  @Override public boolean isRequestedSessionIdFromURL() { return false; }
  @Override @SuppressWarnings("deprecation") public boolean isRequestedSessionIdFromUrl() { return false; }
  @Override public boolean authenticate(HttpServletResponse response) { return false; }
  @Override public void login(String username, String password) { }
  @Override public void logout() { }
  @Override public Collection<Part> getParts() { return Collections.emptyList(); }
  @Override public Part getPart(String name) { return null; }
  @Override public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) { return null; }
  @Override public String getLocalName() { return "localhost"; }
  @Override public String getLocalAddr() { return "127.0.0.1"; }
  @Override public int getLocalPort() { return 8080; }
  @Override public String getRemoteAddr() { return "127.0.0.1"; }
  @Override public String getRemoteHost() { return "localhost"; }
  @Override public int getRemotePort() { return 12345; }
  @Override public String getScheme() { return "http"; }
  @Override public String getServerName() { return "localhost"; }
  @Override public int getServerPort() { return 8080; }
  @Override public String getProtocol() { return "HTTP/1.1"; }
  @Override public Locale getLocale() { return Locale.getDefault(); }
  @Override public Enumeration<Locale> getLocales() { return Collections.enumeration(Collections.singletonList(Locale.getDefault())); }
  @Override public boolean isSecure() { return false; }
  @Override public RequestDispatcher getRequestDispatcher(String path) { return null; }
  @Override @SuppressWarnings("deprecation") public String getRealPath(String path) { return null; }
  @Override public DispatcherType getDispatcherType() { return DispatcherType.REQUEST; }
  @Override public AsyncContext startAsync() { return null; }
  @Override public AsyncContext startAsync(ServletRequest req, ServletResponse res) { return null; }
  @Override public boolean isAsyncStarted() { return false; }
  @Override public boolean isAsyncSupported() { return false; }
  @Override public AsyncContext getAsyncContext() { return null; }
  @Override public ServletContext getServletContext() { return null; }
  @Override public Object getAttribute(String name) { return null; }
  @Override public Enumeration<String> getAttributeNames() { return Collections.emptyEnumeration(); }
  @Override public void setAttribute(String name, Object o) { }
  @Override public void removeAttribute(String name) { }
  @Override public String getParameter(String name) {
    String[] values = parameterMap.get(name);
    return (values != null && values.length > 0) ? values[0] : null;
  }
  @Override public Enumeration<String> getParameterNames() { return Collections.enumeration(parameterMap.keySet()); }
  @Override public String[] getParameterValues(String name) { return parameterMap.get(name); }
  @Override public Map<String, String[]> getParameterMap() { return parameterMap; }
}
