package cn.addenda.porttrail.common.pojo.servlet.bo;

import cn.addenda.porttrail.common.constant.MediaType;
import cn.addenda.porttrail.common.pojo.servlet.dto.AbstractServletDto;
import cn.addenda.porttrail.common.pojo.servlet.dto.ServletResponseDto;
import cn.addenda.porttrail.common.util.JdkSerializationUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Setter
@Getter
@ToString
public class ServletResponseBo extends AbstractServletExecution {

  public static final String BODY_EMPTY = "BODY_EMPTY";
  public static final String BODY_EXCEED_LENGTH = "BODY_EXCEED_LENGTH";
  public static final String DOWNLOAD_UNKNOWN_FILENAME = "DOWNLOAD_UNKNOWN_FILENAME";
  public static final int UNKNOWN_CONTENT_LENGTH = -2;

  public ServletResponseBo(String executionId) {
    super(executionId);
  }

  // response.getContentType()：优先于headerMap
  private String contentType;

  private int contentLength;

  private long datetime;

  private LocaleData locale;

  // response.getCharacterEncoding()：优先于headerMap
  private String charsetEncoding;

  // response.getStatus()
  private int status;

  // response.getHeaderNames() && response.getHeaders(headerName)
  private Map<String, List<String>> headerMap;

  /**
   * {@link MediaType#ifResponseTextContentType(String)} : {@link String}
   * {@link MediaType#ifResponseBinaryContentType(String)} : {@link String} filename
   * 其他：{@link AbstractServletExecution#UNSUPPORTED_CONTENT_TYPE}
   */
  private Object body;

  public ServletResponseBo(ServletResponseDto servletResponseDto) {
    super(servletResponseDto.getExecutionId());
    this.setContentType(servletResponseDto.getContentType());
    this.setCharsetEncoding(servletResponseDto.getCharsetEncoding());
    this.setContentLength(servletResponseDto.getContentLength());
    this.setDatetime(servletResponseDto.getDatetime());
    this.setLocale(
            Optional.ofNullable(servletResponseDto.getLocale())
                    .map(LocaleData::new).orElse(null)
    );
    this.setStatus(servletResponseDto.getStatus());
    this.setHeaderMap(servletResponseDto.getHeaderMap());
    byte[] bodyOfDto = servletResponseDto.getBody();
    if (bodyOfDto == null) {
      this.setBody(null);
    } else if (Arrays.equals(AbstractServletDto.UNSUPPORTED_CONTENT_TYPE, bodyOfDto)) {
      this.setBody(UNSUPPORTED_CONTENT_TYPE);
    } else {
      Object obj = JdkSerializationUtils.deserialize(bodyOfDto);
      if (obj instanceof String) {
        this.setBody(obj);
      } else {
        // 走不进来
        this.setBody(obj);
      }
    }
  }

  @Override
  public String getServletExecutionType() {
    return SERVLET_EXECUTION_TYPE_RESPONSE;
  }
}
