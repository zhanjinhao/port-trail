package cn.addenda.porttrail.common.pojo.servlet.dto;

import cn.addenda.porttrail.common.constant.MediaType;
import cn.addenda.porttrail.common.pojo.servlet.bo.AbstractServletExecution;
import cn.addenda.porttrail.common.pojo.servlet.bo.ServletRequestBo;
import cn.addenda.porttrail.common.pojo.servlet.bo.ServletRequestFormDataList;
import cn.addenda.porttrail.common.util.JdkSerializationUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.*;

@Setter
@Getter
@ToString
public class ServletRequestDto extends AbstractServletDto {

  public static final byte[] BODY_BYTE_ARRAY = new byte[]{-11};

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

  private LocaleDataDto locale;

  /**
   * <pre>
   * requestType为null：
   *      {@link String}
   *      or {@link AbstractServletDto#UNSUPPORTED_CONTENT_TYPE}
   *      or {@link AbstractServletDto#UNSUPPORTED_CHARSET_ENCODING}
   *      or {@link AbstractServletDto#BODY_EMPTY}
   *      or {@link AbstractServletDto#BODY_EXCEED_LENGTH}
   * {@link MediaType#ifRequestTextContentType(String)}：
   *      {@link String}
   *      or {@link AbstractServletDto#UNSUPPORTED_CHARSET_ENCODING}
   *      or {@link AbstractServletDto#BODY_EMPTY}
   *      or {@link AbstractServletDto#BODY_EXCEED_LENGTH}
   * {@link MediaType#ifRequestMultipartFormContentType(String)}：
   *      {@link ServletRequestFormDataDto}
   * {@link MediaType#ifRequestBinaryContentType(String)}：
   *      {@link ServletRequestBo#BODY_BYTE_ARRAY}
   * 其他：
   *      {@link AbstractServletDto#UNSUPPORTED_CONTENT_TYPE}
   * </pre>
   */
  private byte[] body;

  public ServletRequestDto(String executionId) {
    super(executionId);
  }

  public ServletRequestDto(ServletRequestBo servletRequestBo) {
    super(servletRequestBo.getExecutionId());
    this.setVersion(servletRequestBo.getVersion());
    this.setScheme(servletRequestBo.getScheme());
    this.setMethod(servletRequestBo.getMethod());
    this.setUri(servletRequestBo.getUri());
    this.setQueryString(servletRequestBo.getQueryString());
    this.setContentType(servletRequestBo.getContentType());
    this.setCharsetEncoding(servletRequestBo.getCharsetEncoding());
    this.setHeaderMap(servletRequestBo.getHeaderMap());
    this.setDatetime(servletRequestBo.getDatetime());
    this.setAllContentLength(servletRequestBo.getContentLength());
    this.setContentLength(servletRequestBo.getContentLength());
    this.setLocale(
            Optional.ofNullable(servletRequestBo.getLocale())
                    .map(LocaleDataDto::new).orElse(null)
    );
    Object bodyOfBo = servletRequestBo.getBody();
    if (Objects.equals(AbstractServletExecution.UNSUPPORTED_CONTENT_TYPE, bodyOfBo)) {
      this.setBody(UNSUPPORTED_CONTENT_TYPE);
    } else if (Objects.equals(AbstractServletExecution.UNSUPPORTED_CHARSET_ENCODING, bodyOfBo)) {
      this.setBody(UNSUPPORTED_CHARSET_ENCODING);
    } else if (Objects.equals(AbstractServletExecution.BODY_EMPTY, bodyOfBo)) {
      this.setBody(BODY_EMPTY);
    } else if (Objects.equals(AbstractServletExecution.BODY_EXCEED_LENGTH, bodyOfBo)) {
      this.setBody(BODY_EXCEED_LENGTH);
    } else if (Objects.equals(ServletRequestBo.BODY_BYTE_ARRAY, bodyOfBo)) {
      this.setBody(BODY_BYTE_ARRAY);
    } else if (bodyOfBo instanceof Serializable) {
      if (bodyOfBo instanceof ServletRequestFormDataList) {
        this.setBody(JdkSerializationUtils.serialize(new ServletRequestFormDataDtoList((ServletRequestFormDataList) bodyOfBo)));
      } else if (bodyOfBo instanceof String) {
        this.setBody(JdkSerializationUtils.serialize(bodyOfBo));
      } else {
        // 走不进来
        this.setBody(null);
      }
    } else {
      // 走不进来
      this.setBody(null);
    }
  }

//  public String getBodyAsBoString() {
//    byte[] bodyOfDto = getBody();
//    if (bodyOfDto == null) {
//      return null;
//    } else if (Arrays.equals(UNSUPPORTED_CONTENT_TYPE, bodyOfDto)) {
//      return AbstractServletExecution.UNSUPPORTED_CONTENT_TYPE;
//    } else if (Arrays.equals(UNSUPPORTED_CHARSET_ENCODING, bodyOfDto)) {
//      return AbstractServletExecution.UNSUPPORTED_CHARSET_ENCODING;
//    } else if (Arrays.equals(BODY_EMPTY, bodyOfDto)) {
//      return AbstractServletExecution.BODY_EMPTY;
//    } else if (Arrays.equals(BODY_EXCEED_LENGTH, bodyOfDto)) {
//      return AbstractServletExecution.BODY_EXCEED_LENGTH;
//    } else if (Arrays.equals(ServletRequestDto.BODY_BYTE_ARRAY, bodyOfDto)) {
//      return ServletRequestBo.BODY_BYTE_ARRAY;
//    } else {
//      Object obj = JdkSerializationUtils.deserialize(bodyOfDto);
//      if (obj instanceof String) {
//        return (String) obj;
//      } else {
//        // 走不进来
//        return null;
//      }
//    }
//  }

}
