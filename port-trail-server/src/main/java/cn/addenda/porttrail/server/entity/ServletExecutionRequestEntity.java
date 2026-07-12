package cn.addenda.porttrail.server.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import cn.addenda.component.base.jackson.deserializer.LocalDateTimeTsDeSerializer;
import cn.addenda.component.base.jackson.serializer.LocalDateTimeTsSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Servlet请求(ServletExecutionRequestEntity)实体类
 *
 * @author makejava
 * @since 2026-05-01 15:24:43
 */
@Setter
@Getter
@ToString
public class ServletExecutionRequestEntity extends SimpleBaseModel implements Serializable {

  private static final long serialVersionUID = 995457649369073773L;

  private Long id;
  private String systemCode;
  private String serviceName;
  private String imageName;
  private String env;
  private String instanceId;
  private String executionId;
  private String version;
  private String scheme;
  private String method;
  private String uri;
  private String queryString;
  private String contentType;
  private String charsetEncoding;
  @JsonDeserialize(using = LocalDateTimeTsDeSerializer.class)
  @JsonSerialize(using = LocalDateTimeTsSerializer.class)
  private LocalDateTime dateTime;
  private Integer allContentLength;
  private Integer contentLength;
  private String locale;
  private String headers;
  private byte[] body;
  private String bodyText;
  private String curl;

  private Long entryPointSnapshotId;

  public static ServletExecutionRequestEntity ofParam() {
    return new ServletExecutionRequestEntity();
  }

}
