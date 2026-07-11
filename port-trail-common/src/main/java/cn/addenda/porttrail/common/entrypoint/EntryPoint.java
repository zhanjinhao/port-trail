package cn.addenda.porttrail.common.entrypoint;

import lombok.*;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

@Setter
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class EntryPoint implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * 进入点的类型
   */
  private EntryPointType entryPointType;
  /**
   * 进入点的名称
   */
  private String detail;
  /**
   * 同一个JVM实例下entryId不会相同
   */
  private long entryId;

  private static final AtomicLong inc = new AtomicLong(0L);

  private EntryPoint(EntryPointType entryPointType, String detail) {
    this.entryPointType = entryPointType;
    this.detail = detail;
    this.entryId = inc.getAndIncrement();
  }

  private EntryPoint(EntryPointType entryPointType, String detail, long entryId) {
    this.entryPointType = entryPointType;
    this.detail = detail;
    this.entryId = entryId;
  }

  public static EntryPoint of(EntryPointType entryPointType, String detail) {
    return new EntryPoint(entryPointType, detail);
  }

  public static EntryPoint of(EntryPoint entryPoint) {
    return new EntryPoint(entryPoint.getEntryPointType(), entryPoint.getDetail(), entryPoint.getEntryId());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    EntryPoint that = (EntryPoint) o;
    return entryId == that.entryId &&
            entryPointType == that.entryPointType &&
            java.util.Objects.equals(detail, that.detail);
  }

  @Override
  public int hashCode() {
    return java.util.Objects.hash(entryPointType, detail, entryId);
  }

}
