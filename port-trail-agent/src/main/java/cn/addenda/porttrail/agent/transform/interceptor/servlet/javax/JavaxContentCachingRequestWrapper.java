package cn.addenda.porttrail.agent.transform.interceptor.servlet.javax;

import cn.addenda.porttrail.common.constant.MediaType;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.*;

/**
 * 采用tee模式：数据在读取的同时被缓存，应用层正常消费数据流不受影响。
 * 通过limitContent控制缓存上限，超过上限后不再缓存，通过{@link #isContentExceedLimit()}
 * 判断是否超限。{@link #getContentSize()}始终返回实际读取的总字节数。
 *
 * @author addenda
 * @see JavaxContentCachingResponseWrapper
 */
public class JavaxContentCachingRequestWrapper extends HttpServletRequestWrapper {

  private final LimitCacheOutputStream cachedContent;

  private ServletInputStream inputStream;

  private BufferedReader reader;

  /**
   * Create a new ContentCachingRequestWrapper for the given servlet request.
   *
   * @param request      the original servlet request
   * @param limitContent the maximum number of bytes to cache per request
   */
  public JavaxContentCachingRequestWrapper(HttpServletRequest request, int limitContent) {
    super(request);
    int contentLength = request.getContentLength();
    this.cachedContent = new LimitCacheOutputStream(contentLength > 0 ? Math.min(1024, contentLength) : 1024, limitContent);
  }


  @Override
  public ServletInputStream getInputStream() throws IOException {
    if (this.inputStream == null) {
      this.inputStream = new TeeInputStream(getRequest().getInputStream(), cachedContent);
    }
    return this.inputStream;
  }

  private static final String DEFAULT_CHARACTER_ENCODING = "ISO-8859-1";

  @Override
  public String getCharacterEncoding() {
    String enc = super.getCharacterEncoding();
    return (enc != null ? enc : DEFAULT_CHARACTER_ENCODING);
  }

  @Override
  public BufferedReader getReader() throws IOException {
    if (this.reader == null) {
      this.reader = new BufferedReader(new InputStreamReader(getInputStream(), getCharacterEncoding()));
    }
    return this.reader;
  }

  @Override
  public String getParameter(String name) {
    if (this.cachedContent.getContentWritten() == 0 && isFormPost()) {
      writeRequestParametersToCachedContent();
    }
    return super.getParameter(name);
  }

  @Override
  public Map<String, String[]> getParameterMap() {
    if (this.cachedContent.getContentWritten() == 0 && isFormPost()) {
      writeRequestParametersToCachedContent();
    }
    return super.getParameterMap();
  }

  @Override
  public Enumeration<String> getParameterNames() {
    if (this.cachedContent.getContentWritten() == 0 && isFormPost()) {
      writeRequestParametersToCachedContent();
    }
    return super.getParameterNames();
  }

  @Override
  public String[] getParameterValues(String name) {
    if (this.cachedContent.getContentWritten() == 0 && isFormPost()) {
      writeRequestParametersToCachedContent();
    }
    return super.getParameterValues(name);
  }


  private boolean isFormPost() {
    String contentType = getContentType();
    return (contentType != null && contentType.contains(MediaType.APPLICATION_FORM_URLENCODED_VALUE) &&
            "POST".equals(getMethod()));
  }

  private void writeRequestParametersToCachedContent() {
    try {
      if (this.cachedContent.getContentWritten() == 0) {
        String requestEncoding = getCharacterEncoding();
        Map<String, String[]> form = super.getParameterMap();
        for (Iterator<String> nameIterator = form.keySet().iterator(); nameIterator.hasNext(); ) {
          String name = nameIterator.next();
          List<String> values = Arrays.asList(form.get(name));
          for (Iterator<String> valueIterator = values.iterator(); valueIterator.hasNext(); ) {
            String value = valueIterator.next();
            this.cachedContent.write(URLEncoder.encode(name, requestEncoding).getBytes());
            if (value != null) {
              this.cachedContent.write('=');
              this.cachedContent.write(URLEncoder.encode(value, requestEncoding).getBytes());
              if (valueIterator.hasNext()) {
                this.cachedContent.write('&');
              }
            }
          }
          if (nameIterator.hasNext()) {
            this.cachedContent.write('&');
          }
        }
      }
    } catch (IOException ex) {
      throw new IllegalStateException("Failed to write request parameters to cached content", ex);
    }
  }

  /**
   * Return the cached request content as a byte array.
   *
   * @see #JavaxContentCachingRequestWrapper(HttpServletRequest, int)
   */
  public byte[] getContentAsByteArray() {
    return this.cachedContent.toByteArray();
  }

  /**
   * 返回实际读取的总字节数（始终反映真实数据量）。
   */
  public int getContentSize() {
    return this.cachedContent.getContentWritten();
  }

  /**
   * 实际读取的数据量是否超过了缓存上限。
   */
  public boolean isContentExceedLimit() {
    return this.cachedContent.isExceedLimit();
  }

}
