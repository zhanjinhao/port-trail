package cn.addenda.porttrail.agent.test.servlet.javax;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.*;

/**
 * 轻量级 fake {@link HttpServletResponse} 用于单元测试。
 *
 * <ul>
 *   <li>{@code getOutputStream()} 返回的流会捕获所有写入的字节（即"客户端收到的"）。</li>
 *   <li>{@code sendError} / {@code sendRedirect} 记录状态码/location，committed 时抛出
 *   {@link IllegalStateException} 以模拟真实容器行为。</li>
 *   <li>{@code isCommitted()} 可手动置 {@code true} 来测试 committed 后的兜底路径。</li>
 * </ul>
 */
public class FakeHttpServletResponse implements HttpServletResponse {

  private final ByteArrayOutputStream capturedBytes = new ByteArrayOutputStream();
  private ServletOutputStream outputStream;
  private PrintWriter writer;
  private boolean committed;
  private int status = 200;
  private String errorMessage;
  private String redirectLocation;
  private String characterEncoding = "ISO-8859-1";
  private String contentType;
  private int bufferSize;
  private final Map<String, List<String>> headers = new HashMap<>();

  // ---------- Custom accessors for test assertions ----------

  public byte[] getCapturedBytes() {
    return capturedBytes.toByteArray();
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public String getRedirectLocation() {
    return redirectLocation;
  }

  public void setCommitted(boolean committed) {
    this.committed = committed;
  }

  // ---------- Key servlet methods the wrapper calls ----------

  @Override
  public ServletOutputStream getOutputStream() {
    if (outputStream == null) {
      outputStream = new ServletOutputStream() {
        @Override
        public void write(int b) throws IOException {
          capturedBytes.write(b);
          committed = true;
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
          capturedBytes.write(b, off, len);
          committed = true;
        }

        @Override
        public boolean isReady() {
          return true;
        }

        @Override
        public void setWriteListener(WriteListener listener) {
        }
      };
    }
    return outputStream;
  }

  @Override
  public PrintWriter getWriter() throws IOException {
    if (writer == null) {
      writer = new PrintWriter(
              new OutputStreamWriter(getOutputStream(), getCharacterEncoding()));
    }
    return writer;
  }

  @Override
  public void sendError(int sc) throws IOException {
    if (isCommitted()) {
      throw new IllegalStateException("Already committed");
    }
    this.status = sc;
    this.committed = true;
  }

  @Override
  public void sendError(int sc, String msg) throws IOException {
    if (isCommitted()) {
      throw new IllegalStateException("Already committed");
    }
    this.status = sc;
    this.errorMessage = msg;
    this.committed = true;
  }

  @Override
  public void sendRedirect(String location) throws IOException {
    this.redirectLocation = location;
    this.committed = true;
  }

  @Override
  public void setStatus(int sc) {
    this.status = sc;
  }

  @Override
  @SuppressWarnings("deprecation")
  public void setStatus(int sc, String sm) {
    this.status = sc;
    this.errorMessage = sm;
  }

  @Override
  public boolean isCommitted() {
    return committed;
  }

  @Override
  public String getCharacterEncoding() {
    return characterEncoding;
  }

  @Override
  public void setCharacterEncoding(String charset) {
    this.characterEncoding = charset;
  }

  @Override
  public void flushBuffer() {
    // no-op: bytes are already written directly
  }

  @Override
  public void resetBuffer() {
    capturedBytes.reset();
  }

  @Override
  public void reset() {
    capturedBytes.reset();
  }

  @Override
  public int getBufferSize() {
    return bufferSize;
  }

  @Override
  public void setBufferSize(int size) {
    this.bufferSize = size;
  }

  // ---------- Remaining HttpServletResponse / ServletResponse stubs ----------

  @Override
  public String getContentType() {
    return contentType;
  }

  @Override
  public void setContentType(String type) {
    this.contentType = type;
  }

  @Override
  public void setContentLength(int len) {
    setHeader("Content-Length", String.valueOf(len));
  }

  @Override
  public void setContentLengthLong(long len) {
    setHeader("Content-Length", String.valueOf(len));
  }

  @Override
  public boolean containsHeader(String name) {
    return headers.containsKey(name);
  }

  @Override
  public String getHeader(String name) {
    List<String> values = headers.get(name);
    return values != null && !values.isEmpty() ? values.get(0) : null;
  }

  @Override
  public Collection<String> getHeaders(String name) {
    List<String> values = headers.get(name);
    return values != null ? values : Collections.emptyList();
  }

  @Override
  public Collection<String> getHeaderNames() {
    return headers.keySet();
  }

  @Override
  public void setHeader(String name, String value) {
    headers.put(name, new ArrayList<>(Collections.singletonList(value)));
  }

  @Override
  public void addHeader(String name, String value) {
    headers.computeIfAbsent(name, k -> new ArrayList<>()).add(value);
  }

  @Override
  public void setIntHeader(String name, int value) {
    setHeader(name, String.valueOf(value));
  }

  @Override
  public void addIntHeader(String name, int value) {
    addHeader(name, String.valueOf(value));
  }

  @Override
  public void setDateHeader(String name, long date) {
    setHeader(name, String.valueOf(date));
  }

  @Override
  public void addDateHeader(String name, long date) {
    addHeader(name, String.valueOf(date));
  }

  @Override
  public Locale getLocale() {
    return Locale.getDefault();
  }

  @Override
  public void setLocale(Locale loc) {
  }

  @Override
  public String encodeURL(String url) {
    return url;
  }

  @Override
  public String encodeRedirectURL(String url) {
    return url;
  }

  @Override
  @SuppressWarnings("deprecation")
  public String encodeUrl(String url) {
    return url;
  }

  @Override
  @SuppressWarnings("deprecation")
  public String encodeRedirectUrl(String url) {
    return url;
  }

  @Override
  public void addCookie(Cookie cookie) {
  }

  @Override
  @SuppressWarnings("deprecation")
  public int getStatus() {
    return status;
  }
}
