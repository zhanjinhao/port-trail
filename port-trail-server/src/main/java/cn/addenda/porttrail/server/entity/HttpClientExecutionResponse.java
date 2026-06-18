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
 * HttpClient响应(HttpClientExecutionResponse)实体类
 *
 * @author makejava
 * @since 2026-06-07 16:03:56
 */
@Setter
@Getter
@ToString
public class HttpClientExecutionResponse extends SimpleBaseModel implements Serializable {
  private static final long serialVersionUID = -22441990692814162L;
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
   * 客户端名称
   */
  private String clientName;
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
  @JsonDeserialize(using = LocalDateTimeTsDeSerializer.class)
  @JsonSerialize(using = LocalDateTimeTsSerializer.class)
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
  /**
   * t_est_entry_point_snapshot的ID
   */
  private Long entryPointSnapshotId;

  public static HttpClientExecutionResponse ofParam() {
    return new HttpClientExecutionResponse();
  }

}
