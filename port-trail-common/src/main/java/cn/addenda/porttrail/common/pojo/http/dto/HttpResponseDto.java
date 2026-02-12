package cn.addenda.porttrail.common.pojo.http.dto;

import cn.addenda.porttrail.common.constant.MediaType;
import cn.addenda.porttrail.common.pojo.http.bo.AbstractHttpExecution;
import cn.addenda.porttrail.common.pojo.http.bo.HttpResponseBo;
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
public class HttpResponseDto extends AbstractHttpDto {

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
   * {@link MediaType#ifResponseTextContentType(String)} : {@link String}
   * {@link MediaType#ifResponseBinaryContentType(String)} : {@link String} filename
   * 其他：{@link AbstractHttpDto#UNSUPPORTED_CONTENT_TYPE}
   */
  private byte[] body;

  public HttpResponseDto(String requestId) {
    super(requestId);
  }

  public HttpResponseDto(HttpResponseBo httpResponseBo) {
    super(httpResponseBo.getRequestId());
    this.setContentType(httpResponseBo.getContentType());
    this.setCharsetEncoding(httpResponseBo.getCharsetEncoding());
    this.setContentLength(httpResponseBo.getContentLength());
    this.setDatetime(httpResponseBo.getDatetime());
    this.setLocale(
            Optional.ofNullable(httpResponseBo.getLocale())
                    .map(LocaleDataDto::new).orElse(null)
    );
    this.setStatus(httpResponseBo.getStatus());
    this.setHeaderMap(httpResponseBo.getHeaderMap());
    Object bodyOfBo = httpResponseBo.getBody();
    if (Objects.equals(AbstractHttpExecution.UNSUPPORTED_CONTENT_TYPE, bodyOfBo)) {
      this.setBody(UNSUPPORTED_CONTENT_TYPE);
    } else if (bodyOfBo instanceof Serializable) {
      this.setBody(JdkSerializationUtils.serialize(bodyOfBo));
    } else {
      // 走不进来
      this.setBody(null);
    }
  }

}
