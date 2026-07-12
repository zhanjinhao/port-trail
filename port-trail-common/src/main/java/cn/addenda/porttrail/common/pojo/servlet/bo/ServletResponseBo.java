package cn.addenda.porttrail.common.pojo.servlet.bo;

import cn.addenda.porttrail.common.constant.MediaType;
import cn.addenda.porttrail.common.pojo.LocaleData;
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

  public static final String UNKNOWN_FILENAME = "@UNKNOWN#_$FILENAME%";

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
   * <pre>
   * contentType为null：
   *      {@link String}
   *      or {@link AbstractServletExecution#UNSUPPORTED_CONTENT_TYPE}
   *      or {@link AbstractServletExecution#UNSUPPORTED_CHARSET_ENCODING}
   *      or {@link AbstractServletExecution#BODY_EMPTY}
   *      or {@link AbstractServletExecution#BODY_EXCEED_LENGTH}
   * {@link MediaType#ifResponseTextContentType(String)}：
   *      {@link String}
   *      or {@link AbstractServletExecution#UNSUPPORTED_CHARSET_ENCODING}
   *      or {@link AbstractServletExecution#BODY_EMPTY}
   *      or {@link AbstractServletExecution#BODY_EXCEED_LENGTH}
   * {@link MediaType#ifResponseBinaryContentType(String)}：
   *      {@link String} filename
   *      or {@link ServletResponseBo#UNKNOWN_FILENAME}
   * 其他：
   *      {@link AbstractServletExecution#UNSUPPORTED_CONTENT_TYPE}
   * </pre>
   */
  private Object body;

  public ServletResponseBo(ServletResponseDto servletResponseDto) {
    super(servletResponseDto.getExecutionId());
    this.setEntryPointSnapshot(servletResponseDto.getEntryPointSnapshot());
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
    } else if (Arrays.equals(AbstractServletDto.getUNSUPPORTED_CONTENT_TYPE(), bodyOfDto)) {
      this.setBody(UNSUPPORTED_CONTENT_TYPE);
    } else if (Arrays.equals(AbstractServletDto.getUNSUPPORTED_CHARSET_ENCODING(), bodyOfDto)) {
      this.setBody(UNSUPPORTED_CHARSET_ENCODING);
    } else if (Arrays.equals(AbstractServletDto.getBODY_EMPTY(), bodyOfDto)) {
      this.setBody(BODY_EMPTY);
    } else if (Arrays.equals(AbstractServletDto.getBODY_EXCEED_LENGTH(), bodyOfDto)) {
      this.setBody(BODY_EXCEED_LENGTH);
    } else if (Arrays.equals(ServletResponseDto.getUNKNOWN_FILENAME(), bodyOfDto)) {
      this.setBody(UNKNOWN_FILENAME);
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
