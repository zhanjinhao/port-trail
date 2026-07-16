package cn.addenda.porttrail.agent.transform.interceptor.servlet.javax;

import cn.addenda.porttrail.common.helper.FastByteArrayOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 带容量上限的缓存包装类，继承{@link OutputStream}。
 * <p>内部用{@link FastByteArrayOutputStream}做delegate。
 * 每次write都记录总写入量{@code contentWritten}；
 * 当总量超过limit后，后续write不再写入底层缓存，从而避免无效内存分配。
 *
 * @see FastByteArrayOutputStream
 */
public class LimitCacheOutputStream extends OutputStream {

  private final FastByteArrayOutputStream delegate;

  /**
   * 缓存上限（字节）。{@code null}表示不限。
   */
  private final Integer limit;

  /**
   * 实际写入的总字节数（始终递增，不受limit影响）。
   */
  private int contentWritten = 0;

  /**
   * 创建一个新的 LimitCacheOutputStream。
   *
   * @param initialCapacity delegate的初始容量（in b）
   * @param limit           缓存上限，{@code null}表示不限（in b）
   */
  public LimitCacheOutputStream(int initialCapacity, Integer limit) {
    if (limit != null && initialCapacity > limit) {
      throw new IllegalArgumentException("initialCapacity must be less than limit");
    }
    this.delegate = new FastByteArrayOutputStream(initialCapacity);
    this.limit = limit;
  }

  @Override
  public void write(int b) throws IOException {
    contentWritten++;
    if (!isExceedLimit()) {
      delegate.write(b);
    } else if (delegate.size() > 0) {
      delegate.reset();
    }
  }

  @Override
  public void write(byte[] b, int off, int len) throws IOException {
    contentWritten += len;
    if (!isExceedLimit()) {
      delegate.write(b, off, len);
    } else if (delegate.size() > 0) {
      delegate.reset();
    }
  }

  /**
   * 实际写入的数据量是否超过了缓存上限。
   */
  public boolean isExceedLimit() {
    return limit != null && contentWritten > limit;
  }

  /**
   * 返回实际写入的总字节数（始终反映真实数据量）。
   */
  public int getContentWritten() {
    return contentWritten;
  }

  /**
   * 返回缓存的字节数
   */
  public int getCachedContentSize() {
    return delegate.size();
  }

  /**
   * 将当前缓存内容转为新分配的byte数组。
   */
  public byte[] toByteArray() {
    return delegate.toByteArray();
  }

  /**
   * 返回一个InputStream来读取缓存的内容。
   */
  public InputStream getInputStream() {
    return delegate.getInputStream();
  }

  /**
   * 返回delegate当前缓存的字节数。
   */
  public int size() {
    return delegate.size();
  }

  /**
   * 清空缓存，重置delegate。
   */
  public void reset() {
    delegate.reset();
  }

  /**
   * 调整delegate内部缓冲区的大小。
   *
   * @param targetCapacity 目标容量
   * @see FastByteArrayOutputStream#resize(int)
   */
  public void resize(int targetCapacity) {
    delegate.resize(targetCapacity);
  }

}