package cn.addenda.porttrail.server.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * EntryPoint快照(EntryPointSnapshotEntity)实体类
 *
 * @author addenda
 * @since 2026-02-16 19:03:56
 */
@Setter
@Getter
@ToString
public class EntryPointSnapshotEntity extends SimpleBaseModel implements Serializable {
  private static final long serialVersionUID = -21142827940900819L;
  /**
   * 主键
   */
  private Long id;
  /**
   * 线程名
   */
  private String threadName;
  /**
   * 链路追踪ID
   */
  private String traceId;
  /**
   * 快照序号
   */
  private Long seqId;

  public static EntryPointSnapshotEntity ofParam() {
    return new EntryPointSnapshotEntity();
  }

}
