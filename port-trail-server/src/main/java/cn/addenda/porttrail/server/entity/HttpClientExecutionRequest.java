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
 * HttpClient请求(HttpClientExecutionRequest)实体类
 *
 * @author makejava
 * @since 2026-06-07 16:03:20
 */
@Setter
@Getter
@ToString
public class HttpClientExecutionRequest extends SimpleBaseModel implements Serializable {
  private static final long serialVersionUID = 422794593793485792L;
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
   * HTTP协议的版本
   */
  private String version;
  /**
   * HTTP|HTTPS
   */
  private String scheme;
  /**
   * POST|GET|...
   */
  private String method;
  /**
   * /planning/version/pageQuery
   */
  private String uri;
  /**
   * uri后面跟的参数
   */
  private String queryString;
  /**
   * application/json|...
   */
  private String contentType;
  /**
   * UTF-8|...
   */
  private String charsetEncoding;
  /**
   * 请求时间
   */
  @JsonDeserialize(using = LocalDateTimeTsDeSerializer.class)
  @JsonSerialize(using = LocalDateTimeTsSerializer.class)
  private LocalDateTime dateTime;
  /**
   * 请求体的大小
   */
  private Integer contentLength;
  /**
   * 当地时区
   */
  private String locale;
  /**
   * 请求头
   */
  private String headers;
  /**
   * 请求体
   */
  private byte[] body;
  /**
   * 如果请求体是文本，此字段有值
   */
  private String bodyText;
  /**
   * 请求的curl
   */
  private String curl;
  /**
   * t_est_entry_point_snapshot的ID
   */
  private Long entryPointSnapshotId;

  public static HttpClientExecutionRequest ofParam() {
    return new HttpClientExecutionRequest();
  }

}
