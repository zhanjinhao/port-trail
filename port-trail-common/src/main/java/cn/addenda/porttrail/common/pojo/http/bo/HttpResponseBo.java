package cn.addenda.porttrail.common.pojo.http.bo;

import cn.addenda.porttrail.common.constant.MediaType;
import cn.addenda.porttrail.common.pojo.http.dto.AbstractHttpDto;
import cn.addenda.porttrail.common.pojo.http.dto.HttpResponseDto;
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
public class HttpResponseBo extends AbstractHttpExecution {

  public static final String BODY_EMPTY = "BODY_EMPTY";
  public static final String BODY_EXCEED_LENGTH = "BODY_EXCEED_LENGTH";
  public static final String DOWNLOAD_UNKNOWN_FILENAME = "DOWNLOAD_UNKNOWN_FILENAME";
  public static final int UNKNOWN_CONTENT_LENGTH = -2;

  public HttpResponseBo(String requestId) {
    super(requestId);
  }

  // response.getContentType()：优先于headerMap
  private String contentType;

  private int contentLength;

  private long datetime;

  private LocaleData locale;

  // response.getCharacterEncoding()：优先于headerMap
  private String charsetEncoding;

  // response.getStatus()
  private int status;

  // response.getHeaderNames() && response.getHeaders(headerName)
  private Map<String, List<String>> headerMap;

  /**
   * {@link MediaType#ifResponseTextContentType(String)} : {@link String}
   * {@link MediaType#ifResponseBinaryContentType(String)} : {@link String} filename
   * 其他：{@link AbstractHttpExecution#UNSUPPORTED_CONTENT_TYPE}
   */
  private Object body;

  public HttpResponseBo(HttpResponseDto httpResponseDto) {
    super(httpResponseDto.getRequestId());
    this.setContentType(httpResponseDto.getContentType());
    this.setCharsetEncoding(httpResponseDto.getCharsetEncoding());
    this.setContentLength(httpResponseDto.getContentLength());
    this.setDatetime(httpResponseDto.getDatetime());
    this.setLocale(
            Optional.ofNullable(httpResponseDto.getLocale())
                    .map(LocaleData::new).orElse(null)
    );
    this.setStatus(httpResponseDto.getStatus());
    this.setHeaderMap(httpResponseDto.getHeaderMap());
    byte[] bodyOfDto = httpResponseDto.getBody();
    if (bodyOfDto == null) {
      this.setBody(null);
    } else if (Arrays.equals(AbstractHttpDto.UNSUPPORTED_CONTENT_TYPE, bodyOfDto)) {
      this.setBody(UNSUPPORTED_CONTENT_TYPE);
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
  public String getHttpExecutionType() {
    return HTTP_EXECUTION_TYPE_RESPONSE;
  }
}
