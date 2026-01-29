package cn.addenda.porttrail.common.pojo.http.dto;

import cn.addenda.porttrail.common.constant.MediaType;
import cn.addenda.porttrail.common.pojo.http.LocaleData;
import cn.addenda.porttrail.common.pojo.http.bo.HttpResponseBo;
import cn.addenda.porttrail.common.util.JdkSerializationUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

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

  private LocaleData locale;

  // response.getStatus()
  private int status;

  // response.getHeaderNames() && response.getHeaders(headerName)
  private Map<String, List<String>> headerMap;

  /**
   * {@link MediaType#ifResponseTextContentType(String)} : {@link String}
   * {@link MediaType#ifResponseBinaryContentType(String)} : {@link String} filename
   * 其他：{@link HttpResponseBo#UNSUPPORTED_CONTENT_TYPE}
   */
  private byte[] body;

  public HttpResponseDto(String requestId) {
    super(requestId);
  }

  public static HttpResponseDto createByHttpResponseBo(HttpResponseBo httpResponseBo) {
    HttpResponseDto httpResponseDto = new HttpResponseDto(httpResponseBo.getRequestId());
    httpResponseDto.setContentType(httpResponseBo.getContentType());
    httpResponseDto.setCharsetEncoding(httpResponseBo.getCharsetEncoding());
    httpResponseDto.setContentLength(httpResponseBo.getContentLength());
    httpResponseDto.setDatetime(httpResponseBo.getDatetime());
    httpResponseDto.setLocale(httpResponseBo.getLocale());
    httpResponseDto.setStatus(httpResponseBo.getStatus());
    httpResponseDto.setHeaderMap(httpResponseBo.getHeaderMap());
    httpResponseDto.setBody(JdkSerializationUtils.serialize(httpResponseBo.getBody()));
    return httpResponseDto;
  }

}
