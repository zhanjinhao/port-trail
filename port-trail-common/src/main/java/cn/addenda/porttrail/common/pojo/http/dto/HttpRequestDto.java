package cn.addenda.porttrail.common.pojo.http.dto;

import cn.addenda.porttrail.common.constant.MediaType;
import cn.addenda.porttrail.common.pojo.http.HttpRequestFormData;
import cn.addenda.porttrail.common.pojo.http.LocaleData;
import cn.addenda.porttrail.common.pojo.http.bo.HttpRequestBo;
import cn.addenda.porttrail.common.util.JdkSerializationUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Setter
@Getter
@ToString
public class HttpRequestDto extends AbstractHttpDto {

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
  private byte[] body;

  public HttpRequestDto(String requestId) {
    super(requestId);
  }

  public static HttpRequestDto createByHttpRequestBo(HttpRequestBo httpRequestBo) {
    HttpRequestDto httpRequestDto = new HttpRequestDto(httpRequestBo.getRequestId());
    httpRequestDto.setVersion(httpRequestBo.getVersion());
    httpRequestDto.setScheme(httpRequestBo.getScheme());
    httpRequestDto.setMethod(httpRequestBo.getMethod());
    httpRequestDto.setUri(httpRequestBo.getUri());
    httpRequestDto.setQueryString(httpRequestBo.getQueryString());
    httpRequestDto.setContentType(httpRequestBo.getContentType());
    httpRequestDto.setCharsetEncoding(httpRequestBo.getCharsetEncoding());
    httpRequestDto.setHeaderMap(httpRequestBo.getHeaderMap());
    httpRequestDto.setDatetime(httpRequestBo.getDatetime());
    httpRequestDto.setAllContentLength(httpRequestBo.getContentLength());
    httpRequestDto.setContentLength(httpRequestBo.getContentLength());
    httpRequestDto.setLocale(httpRequestBo.getLocale());
    httpRequestDto.setBody(JdkSerializationUtils.serialize(httpRequestBo.getBody()));
    return httpRequestDto;
  }

}
