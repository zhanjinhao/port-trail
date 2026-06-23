package cn.addenda.porttrail.server.entity;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * RedisExecution在handle阶段异常时记录的日志(RedisExecutionHandleThrowableLog)实体类
 *
 * @author makejava
 * @since 2026-06-23 21:57:19
 */
@Setter
@Getter
@ToString
public class RedisExecutionHandleThrowableLog extends SimpleBaseModel implements Serializable {
  private static final long serialVersionUID = -65985610975786756L;
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
   * 参数的字节数组
   */
  private byte[] bytes;
  /**
   * 参数的BO的JSON
   */
  private String boJson;
  /**
   * 异常栈
   */
  private String throwableStack;

  public static RedisExecutionHandleThrowableLog ofParam() {
    return new RedisExecutionHandleThrowableLog();
  }

}
