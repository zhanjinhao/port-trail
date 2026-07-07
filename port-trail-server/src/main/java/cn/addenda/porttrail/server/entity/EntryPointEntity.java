package cn.addenda.porttrail.server.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * EntryPoint(EntryPointEntity)实体类
 *
 * @author addenda
 * @since 2026-02-16 18:40:58
 */
@Setter
@Getter
@ToString
public class EntryPointEntity extends SimpleBaseModel implements Serializable {
  private static final long serialVersionUID = 850274240866886640L;
  /**
   * 主键
   */
  private Long id;
  /**
   * 线程名
   */
  private String entryPointType;
  /**
   * 线程名
   */
  private String detail;
  /**
   * 线程名
   */
  private Long entryId;
  /**
   * t_entry_point_entity_snapshot的ID
   */
  private Long entryPointSnapshotId;

  public static EntryPointEntity ofParam() {
    return new EntryPointEntity();
  }

}
