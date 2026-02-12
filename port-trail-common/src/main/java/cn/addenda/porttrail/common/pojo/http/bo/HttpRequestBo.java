package cn.addenda.porttrail.common.pojo.http.bo;

import cn.addenda.porttrail.common.constant.MediaType;
import cn.addenda.porttrail.common.pojo.http.dto.AbstractHttpDto;
import cn.addenda.porttrail.common.pojo.http.dto.HttpRequestDto;
import cn.addenda.porttrail.common.pojo.http.dto.HttpRequestFormDataDto;
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
public class HttpRequestBo extends AbstractHttpExecution {

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
   * {@link MediaType#ifRequestMultipartFormContentType(String)} : {@link HttpRequestFormData}
   * {@link MediaType#ifRequestBinaryContentType(String)} : {@link HttpRequestBo#BODY_BYTE_ARRAY }
   * 其他：{@link AbstractHttpExecution#UNSUPPORTED_CONTENT_TYPE}
   */
  private Object body;

  public HttpRequestBo(HttpRequestDto httpRequestDto) {
    super(httpRequestDto.getRequestId());
    this.setVersion(httpRequestDto.getVersion());
    this.setScheme(httpRequestDto.getScheme());
    this.setMethod(httpRequestDto.getMethod());
    this.setUri(httpRequestDto.getUri());
    this.setQueryString(httpRequestDto.getQueryString());
    this.setContentType(httpRequestDto.getContentType());
    this.setCharsetEncoding(httpRequestDto.getCharsetEncoding());
    this.setHeaderMap(httpRequestDto.getHeaderMap());
    this.setDatetime(httpRequestDto.getDatetime());
    this.setAllContentLength(httpRequestDto.getAllContentLength());
    this.setContentLength(httpRequestDto.getContentLength());
    this.setLocale(
            Optional.ofNullable(httpRequestDto.getLocale())
                    .map(LocaleData::new).orElse(null)
    );
    byte[] bodyOfDto = httpRequestDto.getBody();
    if (bodyOfDto == null) {
      this.setBody(null);
    } else if (Arrays.equals(AbstractHttpDto.UNSUPPORTED_CONTENT_TYPE, bodyOfDto)) {
      this.setBody(UNSUPPORTED_CONTENT_TYPE);
    } else {
      Object obj = JdkSerializationUtils.deserialize(bodyOfDto);
      if (obj instanceof HttpRequestFormDataDto) {
        this.setBody(new HttpRequestFormData((HttpRequestFormDataDto) obj));
      } else if (obj instanceof String) {
        this.setBody(obj);
      } else {
        // 走不进来
        this.setBody(obj);
      }
    }
  }

  public HttpRequestBo(String requestId) {
    super(requestId);
  }

  @Override
  public String getHttpExecutionType() {
    return HTTP_EXECUTION_TYPE_REQUEST;
  }

}
