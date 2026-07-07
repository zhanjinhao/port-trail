package cn.addenda.porttrail.server.bo.httpclient;

import cn.addenda.porttrail.server.bo.EntryPointSnapshotEntityBo;
import cn.addenda.porttrail.server.entity.HttpClientExecutionRequestEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class HttpClientExecutionRequestBo extends HttpClientExecutionRequestEntity {

  private EntryPointSnapshotEntityBo entryPointSnapshotEntityBo;

  public HttpClientExecutionRequestBo(HttpClientExecutionRequestEntity httpClientExecutionRequestEntity) {
    this.setId(httpClientExecutionRequestEntity.getId());
    this.setSystemCode(httpClientExecutionRequestEntity.getSystemCode());
    this.setServiceName(httpClientExecutionRequestEntity.getServiceName());
    this.setImageName(httpClientExecutionRequestEntity.getImageName());
    this.setEnv(httpClientExecutionRequestEntity.getEnv());
    this.setInstanceId(httpClientExecutionRequestEntity.getInstanceId());
    this.setExecutionId(httpClientExecutionRequestEntity.getExecutionId());
    this.setClientName(httpClientExecutionRequestEntity.getClientName());
    this.setVersion(httpClientExecutionRequestEntity.getVersion());
    this.setScheme(httpClientExecutionRequestEntity.getScheme());
    this.setMethod(httpClientExecutionRequestEntity.getMethod());
    this.setUri(httpClientExecutionRequestEntity.getUri());
    this.setQueryString(httpClientExecutionRequestEntity.getQueryString());
    this.setContentType(httpClientExecutionRequestEntity.getContentType());
    this.setCharsetEncoding(httpClientExecutionRequestEntity.getCharsetEncoding());
    this.setDateTime(httpClientExecutionRequestEntity.getDateTime());
    this.setContentLength(httpClientExecutionRequestEntity.getContentLength());
    this.setLocale(httpClientExecutionRequestEntity.getLocale());
    this.setHeaders(httpClientExecutionRequestEntity.getHeaders());
    this.setBody(httpClientExecutionRequestEntity.getBody());
    this.setBodyText(httpClientExecutionRequestEntity.getBodyText());
    this.setCurl(httpClientExecutionRequestEntity.getCurl());
    this.setEntryPointSnapshotId(httpClientExecutionRequestEntity.getEntryPointSnapshotId());
    this.setCreator(httpClientExecutionRequestEntity.getCreator());
    this.setCreatorName(httpClientExecutionRequestEntity.getCreatorName());
    this.setCreateDt(httpClientExecutionRequestEntity.getCreateDt());
    this.setModifier(httpClientExecutionRequestEntity.getModifier());
    this.setModifierName(httpClientExecutionRequestEntity.getModifierName());
    this.setModifyDt(httpClientExecutionRequestEntity.getModifyDt());
    this.setDeleteFlag(httpClientExecutionRequestEntity.getDeleteFlag());
    this.setDeleteDt(httpClientExecutionRequestEntity.getDeleteDt());
  }

}
