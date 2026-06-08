package cn.addenda.porttrail.server.entity;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * HttpClientExecution在handle阶段异常时记录的日志(HttpClientExecutionHandleThrowableLog)实体类
 *
 * @author makejava
 * @since 2026-06-08 18:20:15
 */
@Setter
@Getter
@ToString
public class HttpClientExecutionHandleThrowableLog extends SimpleBaseModel implements Serializable {

  public static final String HANDLE_TYPE_HTTP_CLIENT_REQUEST = "HTTP_CLIENT_REQUEST";
  public static final String HANDLE_TYPE_HTTP_CLIENT_RESPONSE = "HTTP_CLIENT_RESPONSE";

  private static final long serialVersionUID = -23396554221354613L;
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
   * ExecutionId
   */
  private String executionId;
  /**
   * 处理类型
   */
  private String handleType;
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

  public static HttpClientExecutionHandleThrowableLog ofParam() {
    return new HttpClientExecutionHandleThrowableLog();
  }

}
