package cn.addenda.porttrail.server.entity;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * deserialize阶段异常时记录的日志(PortTrailDeserializeThrowableLog)实体类
 *
 * @author makejava
 * @since 2026-03-06 22:35:59
 */
@Setter
@Getter
@ToString
public class PortTrailDeserializeThrowableLog extends SimpleBaseModel implements Serializable {

  public static final String DESERIALIZE_TYPE_STATEMENT_EXECUTION = "STATEMENT_EXECUTION";
  public static final String DESERIALIZE_TYPE_PREPARED_STATEMENT_EXECUTION = "PREPARED_STATEMENT_EXECUTION";

  private static final long serialVersionUID = 539331745922513630L;
  /**
   * 主键
   */
  private Long id;
  /**
   * 反序列化类型
   */
  private String deserializeType;
  /**
   * 参数的字节数组
   */
  private byte[] bytes;
  /**
   * 异常栈
   */
  private String throwableStack;

  public static PortTrailDeserializeThrowableLog ofParam() {
    return new PortTrailDeserializeThrowableLog();
  }

}
