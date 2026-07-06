package cn.addenda.porttrail.server.entity;

import java.time.LocalDateTime;
import java.io.Serializable;

import cn.addenda.component.base.jackson.deserializer.LocalDateTimeTsDeSerializer;
import cn.addenda.component.base.jackson.serializer.LocalDateTimeTsSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * RedisExecution(RedisExecutionEntity)实体类
 *
 * @author makejava
 * @since 2026-06-23 22:13:26
 */
@Setter
@Getter
@ToString
public class RedisExecutionEntity extends SimpleBaseModel implements Serializable {
  private static final long serialVersionUID = -92058224563472355L;
  /**
   * 主键
   */
  private Long id;
  /**
   * 系统编码
   */
  private String systemCode;
  /**
   * 服务名
   */
  private String serviceName;
  /**
   * 镜像名
   */
  private String imageName;
  /**
   * 环境
   */
  private String env;
  /**
   * 实例ID
   */
  private String instanceId;
  /**
   * 结果类型
   */
  private String resultType;
  /**
   * 命令
   */
  private String commandName;
  /**
   * 命令参数
   */
  private String commandArgString;
  /**
   * Redis节点
   */
  private String peer;
  /**
   * 结果
   */
  private byte[] result;
  /**
   * 结果文本
   */
  private String resultText;
  /**
   * 异常文本
   */
  private String error;
  /**
   * startTime
   */
  @JsonDeserialize(using = LocalDateTimeTsDeSerializer.class)
  @JsonSerialize(using = LocalDateTimeTsSerializer.class)
  private LocalDateTime startTime;
  /**
   * endTime
   */
  @JsonDeserialize(using = LocalDateTimeTsDeSerializer.class)
  @JsonSerialize(using = LocalDateTimeTsSerializer.class)
  private LocalDateTime endTime;
  /**
   * cost
   */
  private Integer cost;

  public static RedisExecutionEntity ofParam() {
    return new RedisExecutionEntity();
  }

}
