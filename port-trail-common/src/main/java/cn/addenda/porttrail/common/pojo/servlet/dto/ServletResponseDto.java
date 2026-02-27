package cn.addenda.porttrail.common.pojo.servlet.dto;

import cn.addenda.porttrail.common.constant.MediaType;
import cn.addenda.porttrail.common.pojo.servlet.bo.AbstractServletExecution;
import cn.addenda.porttrail.common.pojo.servlet.bo.ServletResponseBo;
import cn.addenda.porttrail.common.util.JdkSerializationUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Setter
@Getter
@ToString
public class ServletResponseDto extends AbstractServletDto {

  // response.getContentType()：优先于headerMap
  private String contentType;

  // response.getCharacterEncoding()：优先于headerMap
  private String charsetEncoding;

  private int contentLength;

  private long datetime;

  private LocaleDataDto locale;

  // response.getStatus()
  private int status;

  // response.getHeaderNames() && response.getHeaders(headerName)
  private Map<String, List<String>> headerMap;

  /**
   * {@link MediaType#ifResponseTextContentType(String)} : {@link String}
   * {@link MediaType#ifResponseBinaryContentType(String)} : {@link String} filename
   * 其他：{@link AbstractServletDto#UNSUPPORTED_CONTENT_TYPE}
   */
  private byte[] body;

  public ServletResponseDto(String executionId) {
    super(executionId);
  }

  public ServletResponseDto(ServletResponseBo servletResponseBo) {
    super(servletResponseBo.getExecutionId());
    this.setContentType(servletResponseBo.getContentType());
    this.setCharsetEncoding(servletResponseBo.getCharsetEncoding());
    this.setContentLength(servletResponseBo.getContentLength());
    this.setDatetime(servletResponseBo.getDatetime());
    this.setLocale(
            Optional.ofNullable(servletResponseBo.getLocale())
                    .map(LocaleDataDto::new).orElse(null)
    );
    this.setStatus(servletResponseBo.getStatus());
    this.setHeaderMap(servletResponseBo.getHeaderMap());
    Object bodyOfBo = servletResponseBo.getBody();
    if (Objects.equals(AbstractServletExecution.UNSUPPORTED_CONTENT_TYPE, bodyOfBo)) {
      this.setBody(UNSUPPORTED_CONTENT_TYPE);
    } else if (bodyOfBo instanceof Serializable) {
      this.setBody(JdkSerializationUtils.serialize(bodyOfBo));
    } else {
      // 走不进来
      this.setBody(null);
    }
  }

}
