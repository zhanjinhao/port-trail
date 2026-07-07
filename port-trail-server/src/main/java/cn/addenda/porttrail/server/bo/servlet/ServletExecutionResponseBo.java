package cn.addenda.porttrail.server.bo.servlet;

import cn.addenda.porttrail.server.entity.ServletExecutionResponseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ServletExecutionResponseBo extends ServletExecutionResponseEntity {

  public ServletExecutionResponseBo(ServletExecutionResponseEntity servletExecutionResponseEntity) {
    this.setId(servletExecutionResponseEntity.getId());
    this.setSystemCode(servletExecutionResponseEntity.getSystemCode());
    this.setServiceName(servletExecutionResponseEntity.getServiceName());
    this.setImageName(servletExecutionResponseEntity.getImageName());
    this.setEnv(servletExecutionResponseEntity.getEnv());
    this.setInstanceId(servletExecutionResponseEntity.getInstanceId());
    this.setExecutionId(servletExecutionResponseEntity.getExecutionId());
    this.setContentType(servletExecutionResponseEntity.getContentType());
    this.setCharsetEncoding(servletExecutionResponseEntity.getCharsetEncoding());
    this.setDateTime(servletExecutionResponseEntity.getDateTime());
    this.setContentLength(servletExecutionResponseEntity.getContentLength());
    this.setLocale(servletExecutionResponseEntity.getLocale());
    this.setHeaders(servletExecutionResponseEntity.getHeaders());
    this.setBody(servletExecutionResponseEntity.getBody());
    this.setBodyText(servletExecutionResponseEntity.getBodyText());
    this.setCreator(servletExecutionResponseEntity.getCreator());
    this.setCreatorName(servletExecutionResponseEntity.getCreatorName());
    this.setCreateDt(servletExecutionResponseEntity.getCreateDt());
    this.setModifier(servletExecutionResponseEntity.getModifier());
    this.setModifierName(servletExecutionResponseEntity.getModifierName());
    this.setModifyDt(servletExecutionResponseEntity.getModifyDt());
    this.setDeleteFlag(servletExecutionResponseEntity.getDeleteFlag());
    this.setDeleteDt(servletExecutionResponseEntity.getDeleteDt());
  }

}
