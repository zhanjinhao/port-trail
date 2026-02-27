package cn.addenda.porttrail.server.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * EntryPoint快照(EstEntryPointSnapshot)实体类
 *
 * @author addenda
 * @since 2026-02-16 19:03:56
 */
@Setter
@Getter
@ToString
public class EstEntryPointSnapshot extends SimpleBaseModel implements Serializable {
  private static final long serialVersionUID = -21142827940900819L;
  /**
   * 主键
   */
  private Long id;
  /**
   * 线程名
   */
  private String threadName;

  public static EstEntryPointSnapshot ofParam() {
    return new EstEntryPointSnapshot();
  }

}

