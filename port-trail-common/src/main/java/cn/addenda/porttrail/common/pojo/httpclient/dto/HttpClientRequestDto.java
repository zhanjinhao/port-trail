package cn.addenda.porttrail.common.pojo.httpclient.dto;

import cn.addenda.porttrail.common.constant.MediaType;
import cn.addenda.porttrail.common.pojo.FormDataDto;
import cn.addenda.porttrail.common.pojo.FormDataDtoList;
import cn.addenda.porttrail.common.pojo.FormDataList;
import cn.addenda.porttrail.common.pojo.LocaleDataDto;
import cn.addenda.porttrail.common.pojo.httpclient.bo.AbstractHttpClientExecution;
import cn.addenda.porttrail.common.pojo.httpclient.bo.HttpClientRequestBo;
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
public class HttpClientRequestDto extends AbstractHttpClientDto {

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
   * !(entity instanceOf {@link org.apache.http.HttpEntityEnclosingRequest})：
   *      {@link AbstractHttpClientDto#BODY_EMPTY}
   * entity == null：
   *      {@link AbstractHttpClientDto#BODY_EMPTY}
   * {@link org.apache.http.HttpEntity#getContentLength()} == 0：
   *      {@link AbstractHttpClientDto#BODY_EMPTY}
   * contentType为null：
   *      {@link String}
   *      or {@link AbstractHttpClientDto#UNSUPPORTED_CONTENT_TYPE}
   *      or {@link AbstractHttpClientDto#UNSUPPORTED_CHARSET_ENCODING}
   *      or {@link AbstractHttpClientDto#BODY_EMPTY}
   *      or {@link AbstractHttpClientDto#BODY_EXCEED_LENGTH}
   *      or {@link AbstractHttpClientDto#UNSUPPORTED_CONTENT_ENCODING}
   * {@link MediaType#ifRequestTextContentType(String)}：
   *      {@link String}
   *      or {@link AbstractHttpClientDto#UNSUPPORTED_CHARSET_ENCODING}
   *      or {@link AbstractHttpClientDto#BODY_EMPTY}
   *      or {@link AbstractHttpClientDto#BODY_EXCEED_LENGTH}
   *      or {@link AbstractHttpClientDto#UNSUPPORTED_CONTENT_ENCODING}
   * {@link MediaType#ifRequestMultipartFormContentType(String)}：
   *      {@link FormDataDto}
   *      or {@link AbstractHttpClientDto#UNSUPPORTED_CONTENT_ENCODING}
   * {@link MediaType#ifRequestBinaryContentType(String)}：
   *      {@link HttpClientRequestBo#BODY_BYTE_ARRAY}
   * 其他：
   *      {@link AbstractHttpClientDto#UNSUPPORTED_CONTENT_TYPE}
   * </pre>
   */
  private byte[] body;

  public HttpClientRequestDto(String executionId, String clientName) {
    super(executionId, clientName);
  }

  public HttpClientRequestDto(HttpClientRequestBo httpClientRequestBo) {
    super(httpClientRequestBo.getExecutionId(), httpClientRequestBo.getClientName());
    this.setVersion(httpClientRequestBo.getVersion());
    this.setScheme(httpClientRequestBo.getScheme());
    this.setMethod(httpClientRequestBo.getMethod());
    this.setUri(httpClientRequestBo.getUri());
    this.setQueryString(httpClientRequestBo.getQueryString());
    this.setContentType(httpClientRequestBo.getContentType());
    this.setCharsetEncoding(httpClientRequestBo.getCharsetEncoding());
    this.setHeaderMap(httpClientRequestBo.getHeaderMap());
    this.setDatetime(httpClientRequestBo.getDatetime());
    this.setAllContentLength(httpClientRequestBo.getContentLength());
    this.setContentLength(httpClientRequestBo.getContentLength());
    this.setLocale(
            Optional.ofNullable(httpClientRequestBo.getLocale())
                    .map(LocaleDataDto::new).orElse(null)
    );
    Object bodyOfBo = httpClientRequestBo.getBody();
    if (Objects.equals(AbstractHttpClientExecution.UNSUPPORTED_CONTENT_TYPE, bodyOfBo)) {
      this.setBody(UNSUPPORTED_CONTENT_TYPE);
    } else if (Objects.equals(AbstractHttpClientExecution.UNSUPPORTED_CHARSET_ENCODING, bodyOfBo)) {
      this.setBody(UNSUPPORTED_CHARSET_ENCODING);
    } else if (Objects.equals(AbstractHttpClientExecution.BODY_EMPTY, bodyOfBo)) {
      this.setBody(BODY_EMPTY);
    } else if (Objects.equals(AbstractHttpClientExecution.BODY_EXCEED_LENGTH, bodyOfBo)) {
      this.setBody(BODY_EXCEED_LENGTH);
    } else if (Objects.equals(AbstractHttpClientExecution.UNSUPPORTED_CONTENT_ENCODING, bodyOfBo)) {
      this.setBody(UNSUPPORTED_CONTENT_ENCODING);
    } else if (Objects.equals(HttpClientRequestBo.BODY_BYTE_ARRAY, bodyOfBo)) {
      this.setBody(BODY_BYTE_ARRAY);
    } else if (bodyOfBo instanceof Serializable) {
      if (bodyOfBo instanceof FormDataList) {
        this.setBody(JdkSerializationUtils.serialize(new FormDataDtoList((FormDataList) bodyOfBo)));
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

}
