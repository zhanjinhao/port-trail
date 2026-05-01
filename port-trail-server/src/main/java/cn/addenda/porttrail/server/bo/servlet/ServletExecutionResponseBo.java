package cn.addenda.porttrail.server.bo.servlet;

import cn.addenda.porttrail.server.entity.ServletExecutionResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ServletExecutionResponseBo extends ServletExecutionResponse {

  public ServletExecutionResponseBo(ServletExecutionResponse servletExecutionResponse) {
    this.setId(servletExecutionResponse.getId());
    this.setSystemCode(servletExecutionResponse.getSystemCode());
    this.setServiceName(servletExecutionResponse.getServiceName());
    this.setImageName(servletExecutionResponse.getImageName());
    this.setEnv(servletExecutionResponse.getEnv());
    this.setInstanceId(servletExecutionResponse.getInstanceId());
    this.setExecutionId(servletExecutionResponse.getExecutionId());
    this.setContentType(servletExecutionResponse.getContentType());
    this.setCharsetEncoding(servletExecutionResponse.getCharsetEncoding());
    this.setDateTime(servletExecutionResponse.getDateTime());
    this.setContentLength(servletExecutionResponse.getContentLength());
    this.setLocale(servletExecutionResponse.getLocale());
    this.setHeaders(servletExecutionResponse.getHeaders());
    this.setBody(servletExecutionResponse.getBody());
    this.setBodyText(servletExecutionResponse.getBodyText());
    this.setCreator(servletExecutionResponse.getCreator());
    this.setCreatorName(servletExecutionResponse.getCreatorName());
    this.setCreateDt(servletExecutionResponse.getCreateDt());
    this.setModifier(servletExecutionResponse.getModifier());
    this.setModifierName(servletExecutionResponse.getModifierName());
    this.setModifyDt(servletExecutionResponse.getModifyDt());
    this.setDeleteFlag(servletExecutionResponse.getDeleteFlag());
    this.setDeleteDt(servletExecutionResponse.getDeleteDt());
  }

}
