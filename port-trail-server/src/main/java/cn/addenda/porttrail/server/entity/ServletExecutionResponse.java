package cn.addenda.porttrail.server.entity;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Servlet响应(ServletExecutionResponse)实体类
 *
 * @author makejava
 * @since 2026-05-01 18:48:35
 */
@Setter
@Getter
@ToString
public class ServletExecutionResponse extends SimpleBaseModel implements Serializable {
  private static final long serialVersionUID = 314246435866493824L;
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
   * 执行ID
   */
  private String executionId;
  /**
   * application/json|...
   */
  private String contentType;
  /**
   * UTF-8|...
   */
  private String charsetEncoding;
  /**
   * 响应时间
   */
  private LocalDateTime dateTime;
  /**
   * 整个响应体的大小
   */
  private Integer contentLength;
  /**
   * 当地时区
   */
  private String locale;
  /**
   * 响应状态码
   */
  private Integer status;
  /**
   * 响应头
   */
  private String headers;
  /**
   * 响应体
   */
  private byte[] body;
  /**
   * 如果响应体是文本，此字段有值
   */
  private String bodyText;

  public static ServletExecutionResponse ofParam() {
    return new ServletExecutionResponse();
  }

}
