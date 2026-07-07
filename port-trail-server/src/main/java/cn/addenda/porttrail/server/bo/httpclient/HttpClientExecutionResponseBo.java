package cn.addenda.porttrail.server.bo.httpclient;

import cn.addenda.porttrail.server.bo.EntryPointSnapshotEntityBo;
import cn.addenda.porttrail.server.entity.HttpClientExecutionResponseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class HttpClientExecutionResponseBo extends HttpClientExecutionResponseEntity {

  private EntryPointSnapshotEntityBo entryPointSnapshotEntityBo;

  public HttpClientExecutionResponseBo(HttpClientExecutionResponseEntity httpClientExecutionResponseEntity) {
    this.setId(httpClientExecutionResponseEntity.getId());
    this.setSystemCode(httpClientExecutionResponseEntity.getSystemCode());
    this.setServiceName(httpClientExecutionResponseEntity.getServiceName());
    this.setImageName(httpClientExecutionResponseEntity.getImageName());
    this.setEnv(httpClientExecutionResponseEntity.getEnv());
    this.setInstanceId(httpClientExecutionResponseEntity.getInstanceId());
    this.setExecutionId(httpClientExecutionResponseEntity.getExecutionId());
    this.setClientName(httpClientExecutionResponseEntity.getClientName());
    this.setContentType(httpClientExecutionResponseEntity.getContentType());
    this.setCharsetEncoding(httpClientExecutionResponseEntity.getCharsetEncoding());
    this.setDateTime(httpClientExecutionResponseEntity.getDateTime());
    this.setContentLength(httpClientExecutionResponseEntity.getContentLength());
    this.setLocale(httpClientExecutionResponseEntity.getLocale());
    this.setHeaders(httpClientExecutionResponseEntity.getHeaders());
    this.setBody(httpClientExecutionResponseEntity.getBody());
    this.setBodyText(httpClientExecutionResponseEntity.getBodyText());
    this.setEntryPointSnapshotId(httpClientExecutionResponseEntity.getEntryPointSnapshotId());
    this.setCreator(httpClientExecutionResponseEntity.getCreator());
    this.setCreatorName(httpClientExecutionResponseEntity.getCreatorName());
    this.setCreateDt(httpClientExecutionResponseEntity.getCreateDt());
    this.setModifier(httpClientExecutionResponseEntity.getModifier());
    this.setModifierName(httpClientExecutionResponseEntity.getModifierName());
    this.setModifyDt(httpClientExecutionResponseEntity.getModifyDt());
    this.setDeleteFlag(httpClientExecutionResponseEntity.getDeleteFlag());
    this.setDeleteDt(httpClientExecutionResponseEntity.getDeleteDt());
  }

}
