package cn.addenda.porttrail.common.pojo.httpclient.bo;

import cn.addenda.porttrail.common.constant.MediaType;
import cn.addenda.porttrail.common.pojo.LocaleData;
import cn.addenda.porttrail.common.pojo.httpclient.dto.AbstractHttpClientDto;
import cn.addenda.porttrail.common.pojo.httpclient.dto.HttpClientResponseDto;
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
public class HttpClientResponseBo extends AbstractHttpClientExecution {

  public static final String UNKNOWN_FILENAME = "@UNKNOWN#_$FILENAME%";

  public HttpClientResponseBo(String executionId, String clientName) {
    super(executionId, clientName);
  }

  // HttpMessage.getFirstHeader("Content-Type")
  private String contentType;

  private int contentLength;

  private long datetime;

  private LocaleData locale;

  // HttpMessage.getFirstHeader("Content-Type")
  private String charsetEncoding;

  // HttpResponse.getStatusLine().getStatusCode()
  private int status;

  // HttpMessage.getAllHeaders()
  private Map<String, List<String>> headerMap;

  /**
   * <pre>
   * contentType为null：
   *      {@link String}
   *      or {@link AbstractHttpClientExecution#UNSUPPORTED_CONTENT_TYPE}
   *      or {@link AbstractHttpClientExecution#UNSUPPORTED_CHARSET_ENCODING}
   *      or {@link AbstractHttpClientExecution#BODY_EMPTY}
   *      or {@link AbstractHttpClientExecution#BODY_EXCEED_LENGTH}
   * {@link MediaType#ifResponseTextContentType(String)}：
   *      {@link String}
   *      or {@link AbstractHttpClientExecution#UNSUPPORTED_CHARSET_ENCODING}
   *      or {@link AbstractHttpClientExecution#BODY_EMPTY}
   *      or {@link AbstractHttpClientExecution#BODY_EXCEED_LENGTH}
   * {@link MediaType#ifResponseBinaryContentType(String)}：
   *      {@link String} filename
   *      or {@link HttpClientResponseBo#UNKNOWN_FILENAME}
   * 其他：
   *      {@link AbstractHttpClientExecution#UNSUPPORTED_CONTENT_TYPE}
   * </pre>
   */
  private Object body;

  public HttpClientResponseBo(HttpClientResponseDto httpClientResponseDto) {
    super(httpClientResponseDto.getExecutionId(), httpClientResponseDto.getClientName());
    this.setContentType(httpClientResponseDto.getContentType());
    this.setCharsetEncoding(httpClientResponseDto.getCharsetEncoding());
    this.setContentLength(httpClientResponseDto.getContentLength());
    this.setDatetime(httpClientResponseDto.getDatetime());
    this.setLocale(
            Optional.ofNullable(httpClientResponseDto.getLocale())
                    .map(LocaleData::new).orElse(null)
    );
    this.setStatus(httpClientResponseDto.getStatus());
    this.setHeaderMap(httpClientResponseDto.getHeaderMap());
    byte[] bodyOfDto = httpClientResponseDto.getBody();
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
    } else if (Arrays.equals(HttpClientResponseDto.UNKNOWN_FILENAME, bodyOfDto)) {
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
  public String getHttpClientExecutionType() {
    return HTTP_CLIENT_EXECUTION_TYPE_RESPONSE;
  }
}
