package cn.addenda.porttrail.common.pojo.servlet.bo;

import cn.addenda.porttrail.common.constant.MediaType;
import cn.addenda.porttrail.common.pojo.servlet.dto.AbstractServletDto;
import cn.addenda.porttrail.common.pojo.servlet.dto.ServletRequestDto;
import cn.addenda.porttrail.common.pojo.servlet.dto.ServletRequestFormDataDtoList;
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

  public static final String BODY_EMPTY = "BODY_EMPTY";
  public static final String BODY_EXCEED_LENGTH = "BODY_EXCEED_LENGTH";
  public static final int UNKNOWN_CONTENT_LENGTH = -2;
  public static final String BODY_BYTE_ARRAY = "BYTE_ARRAY";

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
   * {@link MediaType#ifRequestTextContentType(String)} : {@link String}
   * {@link MediaType#ifRequestMultipartFormContentType(String)} : {@link ServletRequestFormDataList}
   * {@link MediaType#ifRequestBinaryContentType(String)} : {@link ServletRequestBo#BODY_BYTE_ARRAY }
   * 其他：{@link AbstractServletExecution#UNSUPPORTED_CONTENT_TYPE}
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
    } else if (Arrays.equals(AbstractServletDto.UNSUPPORTED_CONTENT_TYPE, bodyOfDto)) {
      this.setBody(UNSUPPORTED_CONTENT_TYPE);
    } else {
      Object obj = JdkSerializationUtils.deserialize(bodyOfDto);
      if (obj instanceof ServletRequestFormDataDtoList) {
        this.setBody(new ServletRequestFormDataList((ServletRequestFormDataDtoList) obj));
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
