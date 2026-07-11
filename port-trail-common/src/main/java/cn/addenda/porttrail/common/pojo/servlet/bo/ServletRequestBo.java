package cn.addenda.porttrail.common.pojo.servlet.bo;

import cn.addenda.porttrail.common.constant.MediaType;
import cn.addenda.porttrail.common.pojo.LocaleData;
import cn.addenda.porttrail.common.pojo.FormDataList;
import cn.addenda.porttrail.common.pojo.servlet.dto.AbstractServletDto;
import cn.addenda.porttrail.common.pojo.servlet.dto.ServletRequestDto;
import cn.addenda.porttrail.common.pojo.FormDataDtoList;
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
public class ServletRequestBo extends AbstractServletExecution {

  public static final String BODY_BYTE_ARRAY = "@BYTE#_$ARRAY%";

  // request.getProtocol()
  private String version;

  // request.getScheme()
  private String scheme;

  // request.getMethod()
  private String method;

  // request.getRequestURI()
  private String uri;

  // request.getQueryString()
  private String queryString;

  // request.getContentType()：优先于headerMap
  private String contentType;

  // request.getCharacterEncoding()：优先于headerMap
  private String charsetEncoding;

  // request.getHeaderNames() && request.getHeaders(headerName)
  private Map<String, List<String>> headerMap;

  private long datetime;

  // request.getContentLength
  private int allContentLength;

  // 解析过的contentLength
  private int contentLength;

  private LocaleData locale;


  /**
   * <pre>
   * contentType为null：
   *      {@link String}
   *      or {@link AbstractServletExecution#UNSUPPORTED_CONTENT_TYPE}
   *      or {@link AbstractServletExecution#UNSUPPORTED_CHARSET_ENCODING}
   *      or {@link AbstractServletExecution#BODY_EMPTY}
   *      or {@link AbstractServletExecution#BODY_EXCEED_LENGTH}
   * {@link MediaType#ifRequestTextContentType(String)}：
   *      {@link String}
   *      or {@link AbstractServletExecution#UNSUPPORTED_CHARSET_ENCODING}
   *      or {@link AbstractServletExecution#BODY_EMPTY}
   *      or {@link AbstractServletExecution#BODY_EXCEED_LENGTH}
   * {@link MediaType#ifRequestMultipartFormContentType(String)}：
   *      {@link FormDataList}
   * {@link MediaType#ifRequestBinaryContentType(String)}：
   *      {@link ServletRequestBo#BODY_BYTE_ARRAY}
   * 其他：
   *      {@link AbstractServletExecution#UNSUPPORTED_CONTENT_TYPE}
   * </pre>
   */
  private Object body;

  public ServletRequestBo(ServletRequestDto servletRequestDto) {
    super(servletRequestDto.getExecutionId());
    this.setVersion(servletRequestDto.getVersion());
    this.setScheme(servletRequestDto.getScheme());
    this.setMethod(servletRequestDto.getMethod());
    this.setUri(servletRequestDto.getUri());
    this.setQueryString(servletRequestDto.getQueryString());
    this.setContentType(servletRequestDto.getContentType());
    this.setCharsetEncoding(servletRequestDto.getCharsetEncoding());
    this.setHeaderMap(servletRequestDto.getHeaderMap());
    this.setDatetime(servletRequestDto.getDatetime());
    this.setAllContentLength(servletRequestDto.getAllContentLength());
    this.setContentLength(servletRequestDto.getContentLength());
    this.setLocale(
            Optional.ofNullable(servletRequestDto.getLocale())
                    .map(LocaleData::new).orElse(null)
    );
    byte[] bodyOfDto = servletRequestDto.getBody();
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
    } else if (Arrays.equals(ServletRequestDto.getBODY_BYTE_ARRAY(), bodyOfDto)) {
      this.setBody(BODY_BYTE_ARRAY);
    } else {
      Object obj = JdkSerializationUtils.deserialize(bodyOfDto);
      if (obj instanceof FormDataDtoList) {
        this.setBody(new FormDataList((FormDataDtoList) obj));
      } else if (obj instanceof String) {
        this.setBody(obj);
      } else {
        // 走不进来
        this.setBody(obj);
      }
    }
  }

  public ServletRequestBo(String executionId) {
    super(executionId);
  }

  @Override
  public String getServletExecutionType() {
    return SERVLET_EXECUTION_TYPE_REQUEST;
  }

}
