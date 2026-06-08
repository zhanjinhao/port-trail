package cn.addenda.porttrail.server.bo.httpclient;

import cn.addenda.porttrail.server.entity.HttpClientExecutionRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class HttpClientExecutionRequestBo extends HttpClientExecutionRequest {

  public HttpClientExecutionRequestBo(HttpClientExecutionRequest httpClientExecutionRequest) {
    this.setId(httpClientExecutionRequest.getId());
    this.setSystemCode(httpClientExecutionRequest.getSystemCode());
    this.setServiceName(httpClientExecutionRequest.getServiceName());
    this.setImageName(httpClientExecutionRequest.getImageName());
    this.setEnv(httpClientExecutionRequest.getEnv());
    this.setInstanceId(httpClientExecutionRequest.getInstanceId());
    this.setExecutionId(httpClientExecutionRequest.getExecutionId());
    this.setClientName(httpClientExecutionRequest.getClientName());
    this.setVersion(httpClientExecutionRequest.getVersion());
    this.setScheme(httpClientExecutionRequest.getScheme());
    this.setMethod(httpClientExecutionRequest.getMethod());
    this.setUri(httpClientExecutionRequest.getUri());
    this.setQueryString(httpClientExecutionRequest.getQueryString());
    this.setContentType(httpClientExecutionRequest.getContentType());
    this.setCharsetEncoding(httpClientExecutionRequest.getCharsetEncoding());
    this.setDateTime(httpClientExecutionRequest.getDateTime());
    this.setContentLength(httpClientExecutionRequest.getContentLength());
    this.setLocale(httpClientExecutionRequest.getLocale());
    this.setHeaders(httpClientExecutionRequest.getHeaders());
    this.setBody(httpClientExecutionRequest.getBody());
    this.setBodyText(httpClientExecutionRequest.getBodyText());
    this.setCurl(httpClientExecutionRequest.getCurl());
    this.setCreator(httpClientExecutionRequest.getCreator());
    this.setCreatorName(httpClientExecutionRequest.getCreatorName());
    this.setCreateDt(httpClientExecutionRequest.getCreateDt());
    this.setModifier(httpClientExecutionRequest.getModifier());
    this.setModifierName(httpClientExecutionRequest.getModifierName());
    this.setModifyDt(httpClientExecutionRequest.getModifyDt());
    this.setDeleteFlag(httpClientExecutionRequest.getDeleteFlag());
    this.setDeleteDt(httpClientExecutionRequest.getDeleteDt());
  }

}
