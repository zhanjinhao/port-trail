package cn.addenda.porttrail.agent.transform.interceptor.servlet.javax;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import java.io.IOException;

/**
 * 写入时同时写到delegate和cache的ServletOutputStream。
 * <p>委托{@link #flush()}、{@link #close()}、{@link #isReady()}、{@link #setWriteListener(WriteListener)}
 * 给delegate。
 */
public class TeeOutputStream extends ServletOutputStream {

  private final ServletOutputStream delegate;

  private final LimitCacheOutputStream cache;

  public TeeOutputStream(ServletOutputStream delegate, LimitCacheOutputStream cache) {
    this.delegate = delegate;
    this.cache = cache;
  }

  @Override
  public void write(int b) throws IOException {
    this.cache.write(b);
    this.delegate.write(b);
  }

  @Override
  public void write(byte[] b, int off, int len) throws IOException {
    this.cache.write(b, off, len);
    this.delegate.write(b, off, len);
  }

  @Override
  public void flush() throws IOException {
    this.delegate.flush();
  }

  @Override
  public void close() throws IOException {
    this.delegate.close();
  }

  @Override
  public boolean isReady() {
    return this.delegate.isReady();
  }

  @Override
  public void setWriteListener(WriteListener writeListener) {
    this.delegate.setWriteListener(writeListener);
  }

}