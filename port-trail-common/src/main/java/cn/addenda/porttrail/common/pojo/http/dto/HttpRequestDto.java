package cn.addenda.porttrail.common.pojo.http.dto;

import cn.addenda.porttrail.common.constant.MediaType;
import cn.addenda.porttrail.common.pojo.http.bo.AbstractHttpExecution;
import cn.addenda.porttrail.common.pojo.http.bo.HttpRequestBo;
import cn.addenda.porttrail.common.pojo.http.bo.HttpRequestFormData;
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

  private LocaleDataDto locale;

  /**
   * {@link MediaType#ifRequestTextContentType(String)} : {@link String}
   * {@link MediaType#ifRequestMultipartFormContentType(String)} : {@link HttpRequestFormDataDto}
   * {@link MediaType#ifRequestBinaryContentType(String)} : {@link HttpRequestBo#BODY_BYTE_ARRAY }
   * 其他：{@link AbstractHttpDto#UNSUPPORTED_CONTENT_TYPE}
   */
  private byte[] body;

  public HttpRequestDto(String requestId) {
    super(requestId);
  }

  public HttpRequestDto(HttpRequestBo httpRequestBo) {
    super(httpRequestBo.getRequestId());
    this.setVersion(httpRequestBo.getVersion());
    this.setScheme(httpRequestBo.getScheme());
    this.setMethod(httpRequestBo.getMethod());
    this.setUri(httpRequestBo.getUri());
    this.setQueryString(httpRequestBo.getQueryString());
    this.setContentType(httpRequestBo.getContentType());
    this.setCharsetEncoding(httpRequestBo.getCharsetEncoding());
    this.setHeaderMap(httpRequestBo.getHeaderMap());
    this.setDatetime(httpRequestBo.getDatetime());
    this.setAllContentLength(httpRequestBo.getContentLength());
    this.setContentLength(httpRequestBo.getContentLength());
    this.setLocale(
            Optional.ofNullable(httpRequestBo.getLocale())
                    .map(LocaleDataDto::new).orElse(null)
    );
    Object bodyOfBo = httpRequestBo.getBody();
    if (Objects.equals(AbstractHttpExecution.UNSUPPORTED_CONTENT_TYPE, bodyOfBo)) {
      this.setBody(UNSUPPORTED_CONTENT_TYPE);
    } else if (bodyOfBo instanceof Serializable) {
      if (bodyOfBo instanceof HttpRequestFormData) {
        this.setBody(JdkSerializationUtils.serialize(new HttpRequestFormDataDto((HttpRequestFormData) bodyOfBo)));
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
