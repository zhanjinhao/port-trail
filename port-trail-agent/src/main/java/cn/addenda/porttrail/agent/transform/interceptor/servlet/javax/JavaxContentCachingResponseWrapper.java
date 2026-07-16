package cn.addenda.porttrail.agent.transform.interceptor.servlet.javax;

import cn.addenda.porttrail.common.util.Assert;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.*;

/**
 * 采用tee模式：数据在读取的同时被缓存，应用层正常消费数据流不受影响。
 * 通过contentCacheLimit控制缓存上限，超过上限后不再缓存，通过{@link #isContentExceedLimit()}
 * 判断是否超限。{@link #getContentSize()}始终返回实际写出的总字节数。
 *
 * @author Juergen Hoeller
 * @see JavaxContentCachingRequestWrapper
 * @since 4.1.3
 */
public class JavaxContentCachingResponseWrapper extends HttpServletResponseWrapper {

  private final LimitCacheOutputStream content;

  private ServletOutputStream outputStream;

  private PrintWriter writer;

  private Integer contentLength;


  /**
   * Create a new ContentCachingResponseWrapper for the given servlet response.
   *
   * @param response     the original servlet response
   * @param limitContent 缓存上限（字节），超过此值后不再缓存。-1表示不限制。
   */
  public JavaxContentCachingResponseWrapper(HttpServletResponse response, int limitContent) {
    super(response);
    this.content = new LimitCacheOutputStream(1024, limitContent);
  }


  @Override
  public void sendError(int sc) throws IOException {
    try {
      super.sendError(sc);
    } catch (IllegalStateException ex) {
      // Possibly on Tomcat when called too late: fall back to silent setStatus
      super.setStatus(sc);
    }
  }

  @Override
  @SuppressWarnings("deprecation")
  public void sendError(int sc, String msg) throws IOException {
    try {
      super.sendError(sc, msg);
    } catch (IllegalStateException ex) {
      // Possibly on Tomcat when called too late: fall back to silent setStatus
      super.setStatus(sc, msg);
    }
  }

  @Override
  public void sendRedirect(String location) throws IOException {
    super.sendRedirect(location);
  }

  @Override
  public ServletOutputStream getOutputStream() throws IOException {
    if (this.outputStream == null) {
      this.outputStream = new TeeOutputStream(getResponse().getOutputStream(), content);
    }
    return this.outputStream;
  }

  private static final String DEFAULT_CHARACTER_ENCODING = "ISO-8859-1";

  @Override
  public PrintWriter getWriter() throws IOException {
    if (this.writer == null) {
      String characterEncoding = getCharacterEncoding();
      this.writer = new PrintWriter(new OutputStreamWriter(getOutputStream(),
              characterEncoding != null ? characterEncoding : DEFAULT_CHARACTER_ENCODING), true);
    }
    return this.writer;
  }

  @Override
  public void flushBuffer() throws IOException {
    // 数据已通过tee模式直接写到客户端，这里只需flush底层响应
    super.flushBuffer();
  }

  @Override
  public void setContentLength(int len) {
    if (len > this.content.size()) {
      this.content.resize(len);
    }
    this.contentLength = len;

    HttpServletResponse rawResponse = (HttpServletResponse) getResponse();
    if ((this.contentLength != null) && !rawResponse.isCommitted()) {
      if (rawResponse.getHeader(TRANSFER_ENCODING) == null) {
        rawResponse.setContentLength(this.contentLength);
      }
    }
  }

  // Overrides Servlet 3.1 setContentLengthLong(long) at runtime
  @Override
  public void setContentLengthLong(long len) {
    Assert.isTrue(len <= Integer.MAX_VALUE,
            "Content-Length exceeds JavaxContentCachingResponseWrapper's maximum (" + Integer.MAX_VALUE + "): " + len);
    int lenInt = (int) len;
    if (lenInt > this.content.size()) {
      this.content.resize(lenInt);
    }
    this.contentLength = lenInt;

    HttpServletResponse rawResponse = (HttpServletResponse) getResponse();
    if ((this.contentLength != null) && !rawResponse.isCommitted()) {
      if (rawResponse.getHeader(TRANSFER_ENCODING) == null) {
        rawResponse.setContentLength(this.contentLength);
      }
    }
  }

  @Override
  public void setBufferSize(int size) {
    if (size > this.content.size()) {
      this.content.resize(size);
    }
  }

  @Override
  public void resetBuffer() {
    this.content.reset();
  }

  @Override
  public void reset() {
    super.reset();
    this.content.reset();
  }

  /**
   * Return the cached response content as a byte array.
   */
  public byte[] getContentAsByteArray() {
    return this.content.toByteArray();
  }

  /**
   * Return an {@link InputStream} to the cached content.
   *
   * @since 4.2
   */
  public InputStream getContentInputStream() {
    return this.content.getInputStream();
  }

  /**
   * Return the current size of the cached content.
   *
   * @since 4.2
   */
  public int getContentSize() {
    return this.content.getContentWritten();
  }

  /**
   * 写入的数据是否超过了limitContent限制。
   */
  public boolean isContentExceedLimit() {
    return this.content.isExceedLimit();
  }

  public void clearContent() {
    this.content.reset();
  }

  private static final String TRANSFER_ENCODING = "Transfer-Encoding";

}
