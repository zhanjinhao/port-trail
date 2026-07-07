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
 * HttpClient响应(HttpClientExecutionResponseEntity)实体类
 *
 * @author makejava
 * @since 2026-06-07 16:03:56
 */
@Setter
@Getter
@ToString
public class HttpClientExecutionResponseEntity extends SimpleBaseModel implements Serializable {
  private static final long serialVersionUID = -22441990692814162L;
  private Long id;
  private String systemCode;
  private String serviceName;
  private String imageName;
  private String env;
  private String instanceId;
  private String executionId;
  private String clientName;
  private String contentType;
  private String charsetEncoding;
  @JsonDeserialize(using = LocalDateTimeTsDeSerializer.class)
  @JsonSerialize(using = LocalDateTimeTsSerializer.class)
  private LocalDateTime dateTime;
  private Integer contentLength;
  private String locale;
  private Integer status;
  private String headers;
  private byte[] body;
  private String bodyText;
  private Long entryPointSnapshotId;

  public static HttpClientExecutionResponseEntity ofParam() {
    return new HttpClientExecutionResponseEntity();
  }

}
