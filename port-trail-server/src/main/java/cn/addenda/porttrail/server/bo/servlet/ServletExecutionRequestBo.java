package cn.addenda.porttrail.server.bo.servlet;

import cn.addenda.porttrail.server.bo.EntryPointSnapshotEntityBo;
import cn.addenda.porttrail.server.entity.ServletExecutionRequestEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ServletExecutionRequestBo extends ServletExecutionRequestEntity {

  private EntryPointSnapshotEntityBo entryPointSnapshotEntityBo;

  public ServletExecutionRequestBo(ServletExecutionRequestEntity servletExecutionRequestEntity) {
    this.setId(servletExecutionRequestEntity.getId());
    this.setSystemCode(servletExecutionRequestEntity.getSystemCode());
    this.setServiceName(servletExecutionRequestEntity.getServiceName());
    this.setImageName(servletExecutionRequestEntity.getImageName());
    this.setEnv(servletExecutionRequestEntity.getEnv());
    this.setInstanceId(servletExecutionRequestEntity.getInstanceId());
    this.setExecutionId(servletExecutionRequestEntity.getExecutionId());
    this.setVersion(servletExecutionRequestEntity.getVersion());
    this.setScheme(servletExecutionRequestEntity.getScheme());
    this.setMethod(servletExecutionRequestEntity.getMethod());
    this.setUri(servletExecutionRequestEntity.getUri());
    this.setQueryString(servletExecutionRequestEntity.getQueryString());
    this.setContentType(servletExecutionRequestEntity.getContentType());
    this.setCharsetEncoding(servletExecutionRequestEntity.getCharsetEncoding());
    this.setDateTime(servletExecutionRequestEntity.getDateTime());
    this.setAllContentLength(servletExecutionRequestEntity.getAllContentLength());
    this.setContentLength(servletExecutionRequestEntity.getContentLength());
    this.setLocale(servletExecutionRequestEntity.getLocale());
    this.setHeaders(servletExecutionRequestEntity.getHeaders());
    this.setBody(servletExecutionRequestEntity.getBody());
    this.setBodyText(servletExecutionRequestEntity.getBodyText());
    this.setCurl(servletExecutionRequestEntity.getCurl());
    this.setEntryPointSnapshotId(servletExecutionRequestEntity.getEntryPointSnapshotId());
    this.setCreator(servletExecutionRequestEntity.getCreator());
    this.setCreatorName(servletExecutionRequestEntity.getCreatorName());
    this.setCreateDt(servletExecutionRequestEntity.getCreateDt());
    this.setModifier(servletExecutionRequestEntity.getModifier());
    this.setModifierName(servletExecutionRequestEntity.getModifierName());
    this.setModifyDt(servletExecutionRequestEntity.getModifyDt());
    this.setDeleteFlag(servletExecutionRequestEntity.getDeleteFlag());
    this.setDeleteDt(servletExecutionRequestEntity.getDeleteDt());
  }

}
