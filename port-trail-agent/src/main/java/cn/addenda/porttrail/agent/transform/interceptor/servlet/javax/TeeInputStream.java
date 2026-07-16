package cn.addenda.porttrail.agent.transform.interceptor.servlet.javax;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.IOException;

/**
 * 从delegate读取数据的同时写入cache的ServletInputStream。
 * <p>委托{@link #isFinished()}、{@link #isReady()}、{@link #setReadListener(ReadListener)}
 * 给delegate。
 */
public class TeeInputStream extends ServletInputStream {

  private final ServletInputStream delegate;

  private final LimitCacheOutputStream cache;

  public TeeInputStream(ServletInputStream delegate, LimitCacheOutputStream cache) {
    this.delegate = delegate;
    this.cache = cache;
  }

  @Override
  public int read() throws IOException {
    int ch = this.delegate.read();
    if (ch != -1) {
      this.cache.write(ch);
    }
    return ch;
  }

  @Override
  public int read(byte[] b) throws IOException {
    int count = this.delegate.read(b);
    if (count > 0) {
      this.cache.write(b, 0, count);
    }
    return count;
  }

  @Override
  public int read(final byte[] b, final int off, final int len) throws IOException {
    int count = this.delegate.read(b, off, len);
    if (count > 0) {
      this.cache.write(b, off, count);
    }
    return count;
  }

  @Override
  public int readLine(final byte[] b, final int off, final int len) throws IOException {
    int count = this.delegate.readLine(b, off, len);
    if (count > 0) {
      this.cache.write(b, off, count);
    }
    return count;
  }

  @Override
  public boolean isFinished() {
    return this.delegate.isFinished();
  }

  @Override
  public boolean isReady() {
    return this.delegate.isReady();
  }

  @Override
  public void setReadListener(ReadListener readListener) {
    this.delegate.setReadListener(readListener);
  }

}