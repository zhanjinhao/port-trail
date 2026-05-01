package cn.addenda.porttrail.server.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Servlet请求(ServletExecutionRequest)实体类
 *
 * @author makejava
 * @since 2026-05-01 15:24:43
 */
@Setter
@Getter
@ToString
public class ServletExecutionRequest extends SimpleBaseModel implements Serializable {

  private static final long serialVersionUID = 995457649369073773L;

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
   * '/planning/version/pageQuery'
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
  private LocalDateTime dateTime;
  /**
   * 整个请求体的大小
   */
  private Integer allContentLength;
  /**
   * 解析过的请求体的大小
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

  public static ServletExecutionRequest ofParam() {
    return new ServletExecutionRequest();
  }

}
