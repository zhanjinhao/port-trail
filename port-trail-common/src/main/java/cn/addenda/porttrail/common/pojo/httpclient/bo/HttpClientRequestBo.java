package cn.addenda.porttrail.common.pojo.httpclient.bo;

import cn.addenda.porttrail.common.constant.MediaType;
import cn.addenda.porttrail.common.pojo.FormDataDtoList;
import cn.addenda.porttrail.common.pojo.FormDataList;
import cn.addenda.porttrail.common.pojo.LocaleData;
import cn.addenda.porttrail.common.pojo.httpclient.dto.AbstractHttpClientDto;
import cn.addenda.porttrail.common.pojo.httpclient.dto.HttpClientRequestDto;
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
public class HttpClientRequestBo extends AbstractHttpClientExecution {

  public static final String BODY_BYTE_ARRAY = "@BYTE#_$ARRAY%";

  // HttpMessage.getProtocolVersion().getProtocol()
  private String version;

  // HttpRequestWrapper.original.getRequestLine().getUri()
  private String scheme;

  // HttpRequest.getRequestLine().getMethod()
  private String method;

  // HttpRequest.getRequestLine().getUri()
  private String uri;

  // HttpRequest.getRequestLine().getUri()
  private String queryString;

  // HttpMessage.getFirstHeader("Content-Type")
  private String contentType;

  // HttpMessage.getFirstHeader("Content-Type")
  private String charsetEncoding;

  // HttpMessage.getAllHeaders()
  private Map<String, List<String>> headerMap;

  private long datetime;

  // HttpMessage.getFirstHeader("Content-Length")
  private int contentLength;

  // HttpMessage.getFirstHeader("Accept-Language")
  private LocaleData locale;

  /**
   * <pre>
   * !(entity instanceOf {@link org.apache.http.HttpEntityEnclosingRequest})：
   *      {@link AbstractHttpClientExecution#BODY_EMPTY}
   * entity == null：
   *      {@link AbstractHttpClientExecution#BODY_EMPTY}
   * {@link org.apache.http.HttpEntity#getContentLength()} == 0：
   *      {@link AbstractHttpClientExecution#BODY_EMPTY}
   * contentType为null：
   *      {@link String}
   *      or {@link AbstractHttpClientExecution#UNSUPPORTED_CONTENT_TYPE}
   *      or {@link AbstractHttpClientExecution#UNSUPPORTED_CHARSET_ENCODING}
   *      or {@link AbstractHttpClientExecution#BODY_EMPTY}
   *      or {@link AbstractHttpClientExecution#BODY_EXCEED_LENGTH}
   *      or {@link AbstractHttpClientExecution#UNSUPPORTED_CONTENT_ENCODING}
   * {@link MediaType#ifRequestTextContentType(String)}：
   *      {@link String}
   *      or {@link AbstractHttpClientExecution#UNSUPPORTED_CHARSET_ENCODING}
   *      or {@link AbstractHttpClientExecution#BODY_EMPTY}
   *      or {@link AbstractHttpClientExecution#BODY_EXCEED_LENGTH}
   *      or {@link AbstractHttpClientExecution#UNSUPPORTED_CONTENT_ENCODING}
   * {@link MediaType#ifRequestMultipartFormContentType(String)}：
   *      {@link FormDataList}
   *      or {@link AbstractHttpClientExecution#UNSUPPORTED_CONTENT_TYPE}
   * {@link MediaType#ifRequestBinaryContentType(String)}：
   *      {@link HttpClientRequestBo#BODY_BYTE_ARRAY}
   * 其他：
   *      {@link AbstractHttpClientExecution#UNSUPPORTED_CONTENT_TYPE}
   * </pre>
   */
  private Object body;

  public HttpClientRequestBo(HttpClientRequestDto httpClientRequestDto) {
    super(httpClientRequestDto.getExecutionId(), httpClientRequestDto.getClientName());
    this.setVersion(httpClientRequestDto.getVersion());
    this.setScheme(httpClientRequestDto.getScheme());
    this.setMethod(httpClientRequestDto.getMethod());
    this.setUri(httpClientRequestDto.getUri());
    this.setQueryString(httpClientRequestDto.getQueryString());
    this.setContentType(httpClientRequestDto.getContentType());
    this.setCharsetEncoding(httpClientRequestDto.getCharsetEncoding());
    this.setHeaderMap(httpClientRequestDto.getHeaderMap());
    this.setDatetime(httpClientRequestDto.getDatetime());
    this.setContentLength(httpClientRequestDto.getAllContentLength());
    this.setContentLength(httpClientRequestDto.getContentLength());
    this.setLocale(
            Optional.ofNullable(httpClientRequestDto.getLocale())
                    .map(LocaleData::new).orElse(null)
    );
    byte[] bodyOfDto = httpClientRequestDto.getBody();
    if (bodyOfDto == null) {
      this.setBody(null);
    } else if (Arrays.equals(AbstractHttpClientDto.UNSUPPORTED_CONTENT_TYPE, bodyOfDto)) {
      this.setBody(UNSUPPORTED_CONTENT_TYPE);
    } else if (Arrays.equals(AbstractHttpClientDto.UNSUPPORTED_CHARSET_ENCODING, bodyOfDto)) {
      this.setBody(UNSUPPORTED_CHARSET_ENCODING);
    } else if (Arrays.equals(AbstractHttpClientDto.BODY_EMPTY, bodyOfDto)) {
      this.setBody(BODY_EMPTY);
    } else if (Arrays.equals(AbstractHttpClientDto.BODY_EXCEED_LENGTH, bodyOfDto)) {
      this.setBody(BODY_EXCEED_LENGTH);
    } else if (Arrays.equals(AbstractHttpClientDto.UNSUPPORTED_CONTENT_ENCODING, bodyOfDto)) {
      this.setBody(UNSUPPORTED_CONTENT_ENCODING);
    } else if (Arrays.equals(HttpClientRequestDto.BODY_BYTE_ARRAY, bodyOfDto)) {
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

  public HttpClientRequestBo(String executionId, String clientName) {
    super(executionId, clientName);
  }

  @Override
  public String getHttpClientExecutionType() {
    return HTTP_CLIENT_EXECUTION_TYPE_REQUEST;
  }

}
