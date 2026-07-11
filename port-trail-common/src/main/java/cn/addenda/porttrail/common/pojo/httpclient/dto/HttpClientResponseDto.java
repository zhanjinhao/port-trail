package cn.addenda.porttrail.common.pojo.httpclient.dto;

import cn.addenda.porttrail.common.constant.MediaType;
import cn.addenda.porttrail.common.pojo.httpclient.bo.AbstractHttpClientExecution;
import cn.addenda.porttrail.common.pojo.httpclient.bo.HttpClientResponseBo;
import cn.addenda.porttrail.common.pojo.LocaleDataDto;
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
public class HttpClientResponseDto extends AbstractHttpClientDto {

  private static final byte[] UNKNOWN_FILENAME = new byte[]{-21};

  public static byte[] getUNKNOWN_FILENAME() {
    return UNKNOWN_FILENAME.clone();
  }

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
   * <pre>
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
   * {@link MediaType#ifResponseTextContentType(String)}：
   *      {@link String}
   *      or {@link AbstractHttpClientDto#UNSUPPORTED_CHARSET_ENCODING}
   *      or {@link AbstractHttpClientDto#BODY_EMPTY}
   *      or {@link AbstractHttpClientDto#BODY_EXCEED_LENGTH}
   *      or {@link AbstractHttpClientDto#UNSUPPORTED_CONTENT_ENCODING}
   * {@link MediaType#ifResponseBinaryContentType(String)}：
   *      {@link String} filename
   *      or {@link HttpClientResponseDto#UNKNOWN_FILENAME}
   * 其他：
   *      {@link AbstractHttpClientDto#UNSUPPORTED_CONTENT_TYPE}
   * </pre>
   */
  private byte[] body;

  public HttpClientResponseDto(String executionId, String clientName) {
    super(executionId, clientName);
  }

  public HttpClientResponseDto(HttpClientResponseBo httpClientResponseBo) {
    super(httpClientResponseBo.getExecutionId(), httpClientResponseBo.getClientName());
    this.setEntryPointSnapshot(httpClientResponseBo.getEntryPointSnapshot());
    this.setContentType(httpClientResponseBo.getContentType());
    this.setCharsetEncoding(httpClientResponseBo.getCharsetEncoding());
    this.setContentLength(httpClientResponseBo.getContentLength());
    this.setDatetime(httpClientResponseBo.getDatetime());
    this.setLocale(
            Optional.ofNullable(httpClientResponseBo.getLocale())
                    .map(LocaleDataDto::new).orElse(null)
    );
    this.setStatus(httpClientResponseBo.getStatus());
    this.setHeaderMap(httpClientResponseBo.getHeaderMap());
    Object bodyOfBo = httpClientResponseBo.getBody();
    if (Objects.equals(AbstractHttpClientExecution.UNSUPPORTED_CONTENT_TYPE, bodyOfBo)) {
      this.setBody(getUNSUPPORTED_CONTENT_TYPE());
    } else if (Objects.equals(AbstractHttpClientExecution.UNSUPPORTED_CHARSET_ENCODING, bodyOfBo)) {
      this.setBody(getUNSUPPORTED_CHARSET_ENCODING());
    } else if (Objects.equals(AbstractHttpClientExecution.BODY_EMPTY, bodyOfBo)) {
      this.setBody(getBODY_EMPTY());
    } else if (Objects.equals(AbstractHttpClientExecution.BODY_EXCEED_LENGTH, bodyOfBo)) {
      this.setBody(getBODY_EXCEED_LENGTH());
    } else if (Objects.equals(AbstractHttpClientExecution.UNSUPPORTED_CONTENT_ENCODING, bodyOfBo)) {
      this.setBody(getUNSUPPORTED_CONTENT_ENCODING());
    } else if (Objects.equals(HttpClientResponseBo.UNKNOWN_FILENAME, bodyOfBo)) {
      this.setBody(getUNKNOWN_FILENAME());
    } else if (bodyOfBo instanceof Serializable) {
      this.setBody(JdkSerializationUtils.serialize(bodyOfBo));
    } else {
      // 走不进来
      this.setBody(null);
    }
  }

}
