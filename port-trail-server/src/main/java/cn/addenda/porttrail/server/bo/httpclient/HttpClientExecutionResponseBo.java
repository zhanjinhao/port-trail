package cn.addenda.porttrail.server.bo.httpclient;

import cn.addenda.porttrail.server.bo.est.EstEntryPointSnapshotBo;
import cn.addenda.porttrail.server.entity.HttpClientExecutionResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class HttpClientExecutionResponseBo extends HttpClientExecutionResponse {

  private EstEntryPointSnapshotBo estEntryPointSnapshotBo;

  public HttpClientExecutionResponseBo(HttpClientExecutionResponse httpClientExecutionResponse) {
    this.setId(httpClientExecutionResponse.getId());
    this.setSystemCode(httpClientExecutionResponse.getSystemCode());
    this.setServiceName(httpClientExecutionResponse.getServiceName());
    this.setImageName(httpClientExecutionResponse.getImageName());
    this.setEnv(httpClientExecutionResponse.getEnv());
    this.setInstanceId(httpClientExecutionResponse.getInstanceId());
    this.setExecutionId(httpClientExecutionResponse.getExecutionId());
    this.setClientName(httpClientExecutionResponse.getClientName());
    this.setContentType(httpClientExecutionResponse.getContentType());
    this.setCharsetEncoding(httpClientExecutionResponse.getCharsetEncoding());
    this.setDateTime(httpClientExecutionResponse.getDateTime());
    this.setContentLength(httpClientExecutionResponse.getContentLength());
    this.setLocale(httpClientExecutionResponse.getLocale());
    this.setHeaders(httpClientExecutionResponse.getHeaders());
    this.setBody(httpClientExecutionResponse.getBody());
    this.setBodyText(httpClientExecutionResponse.getBodyText());
    this.setEntryPointSnapshotId(httpClientExecutionResponse.getEntryPointSnapshotId());
    this.setCreator(httpClientExecutionResponse.getCreator());
    this.setCreatorName(httpClientExecutionResponse.getCreatorName());
    this.setCreateDt(httpClientExecutionResponse.getCreateDt());
    this.setModifier(httpClientExecutionResponse.getModifier());
    this.setModifierName(httpClientExecutionResponse.getModifierName());
    this.setModifyDt(httpClientExecutionResponse.getModifyDt());
    this.setDeleteFlag(httpClientExecutionResponse.getDeleteFlag());
    this.setDeleteDt(httpClientExecutionResponse.getDeleteDt());
  }

}
