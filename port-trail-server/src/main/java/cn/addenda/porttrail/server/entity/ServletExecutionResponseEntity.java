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
 * Servlet响应(ServletExecutionResponseEntity)实体类
 *
 * @author makejava
 * @since 2026-05-01 18:48:35
 */
@Setter
@Getter
@ToString
public class ServletExecutionResponseEntity extends SimpleBaseModel implements Serializable {
  private static final long serialVersionUID = 314246435866493824L;
  private Long id;
  private String systemCode;
  private String serviceName;
  private String imageName;
  private String env;
  private String instanceId;
  private String executionId;
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

  public static ServletExecutionResponseEntity ofParam() {
    return new ServletExecutionResponseEntity();
  }

}
