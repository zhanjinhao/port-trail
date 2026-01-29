package cn.addenda.porttrail.common.pojo.http.bo;

import cn.addenda.porttrail.common.constant.MediaType;
import cn.addenda.porttrail.common.pojo.http.LocaleData;
import cn.addenda.porttrail.common.pojo.http.dto.HttpResponseDto;
import cn.addenda.porttrail.common.util.JdkSerializationUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Setter
@Getter
@ToString
public class HttpResponseBo extends AbstractHttpExecution {

  public static final String BODY_EMPTY = "BODY_EMPTY";
  public static final String BODY_EXCEED_LENGTH = "BODY_EXCEED_LENGTH";
  public static final String UNSUPPORTED_CONTENT_TYPE = "UNSUPPORTED_CONTENT_TYPE";
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
   * 其他：{@link HttpResponseBo#UNSUPPORTED_CONTENT_TYPE}
   */
  private Object body;

  public static HttpResponseBo createByHttpResponseDto(HttpResponseDto httpResponseDto) {
    HttpResponseBo httpResponseBo = new HttpResponseBo(httpResponseDto.getRequestId());
    httpResponseBo.setContentType(httpResponseDto.getContentType());
    httpResponseBo.setCharsetEncoding(httpResponseDto.getCharsetEncoding());
    httpResponseBo.setContentLength(httpResponseDto.getContentLength());
    httpResponseBo.setDatetime(httpResponseDto.getDatetime());
    httpResponseBo.setLocale(httpResponseDto.getLocale());
    httpResponseBo.setStatus(httpResponseDto.getStatus());
    httpResponseBo.setHeaderMap(httpResponseDto.getHeaderMap());
    httpResponseBo.setBody(JdkSerializationUtils.deserialize(httpResponseDto.getBody()));
    return httpResponseBo;
  }

  @Override
  public String getHttpExecutionType() {
    return HTTP_EXECUTION_TYPE_RESPONSE;
  }
}
