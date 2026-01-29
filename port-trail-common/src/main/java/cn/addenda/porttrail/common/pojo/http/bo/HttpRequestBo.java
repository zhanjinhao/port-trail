package cn.addenda.porttrail.common.pojo.http.bo;

import cn.addenda.porttrail.common.constant.MediaType;
import cn.addenda.porttrail.common.pojo.http.HttpRequestFormData;
import cn.addenda.porttrail.common.pojo.http.LocaleData;
import cn.addenda.porttrail.common.pojo.http.dto.HttpRequestDto;
import cn.addenda.porttrail.common.util.JdkSerializationUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Setter
@Getter
@ToString
public class HttpRequestBo extends AbstractHttpExecution {

  public static final String BODY_EMPTY = "BODY_EMPTY";
  public static final String BODY_EXCEED_LENGTH = "BODY_EXCEED_LENGTH";
  public static final String UNSUPPORTED_CONTENT_TYPE = "UNSUPPORTED_CONTENT_TYPE";
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
   * 其他：{@link HttpRequestBo#UNSUPPORTED_CONTENT_TYPE}
   */
  private Object body;

  public static HttpRequestBo createByHttpRequestDto(HttpRequestDto httpRequestDto) {
    HttpRequestBo httpRequestBo = new HttpRequestBo(httpRequestDto.getRequestId());
    httpRequestBo.setVersion(httpRequestDto.getVersion());
    httpRequestBo.setScheme(httpRequestDto.getScheme());
    httpRequestBo.setMethod(httpRequestDto.getMethod());
    httpRequestBo.setUri(httpRequestDto.getUri());
    httpRequestBo.setQueryString(httpRequestDto.getQueryString());
    httpRequestBo.setContentType(httpRequestDto.getContentType());
    httpRequestBo.setCharsetEncoding(httpRequestDto.getCharsetEncoding());
    httpRequestBo.setHeaderMap(httpRequestDto.getHeaderMap());
    httpRequestBo.setDatetime(httpRequestDto.getDatetime());
    httpRequestBo.setAllContentLength(httpRequestDto.getAllContentLength());
    httpRequestBo.setContentLength(httpRequestDto.getContentLength());
    httpRequestBo.setLocale(httpRequestDto.getLocale());
    httpRequestBo.setBody(JdkSerializationUtils.deserialize(httpRequestDto.getBody()));
    return httpRequestBo;
  }

  public HttpRequestBo(String requestId) {
    super(requestId);
  }

  @Override
  public String getHttpExecutionType() {
    return HTTP_EXECUTION_TYPE_REQUEST;
  }

}
