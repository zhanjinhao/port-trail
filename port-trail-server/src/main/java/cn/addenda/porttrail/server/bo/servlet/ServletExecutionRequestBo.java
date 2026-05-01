package cn.addenda.porttrail.server.bo.servlet;

import cn.addenda.porttrail.server.entity.ServletExecutionRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ServletExecutionRequestBo extends ServletExecutionRequest {

  public ServletExecutionRequestBo(ServletExecutionRequest servletExecutionRequest) {
    this.setId(servletExecutionRequest.getId());
    this.setSystemCode(servletExecutionRequest.getSystemCode());
    this.setServiceName(servletExecutionRequest.getServiceName());
    this.setImageName(servletExecutionRequest.getImageName());
    this.setEnv(servletExecutionRequest.getEnv());
    this.setInstanceId(servletExecutionRequest.getInstanceId());
    this.setExecutionId(servletExecutionRequest.getExecutionId());
    this.setVersion(servletExecutionRequest.getVersion());
    this.setScheme(servletExecutionRequest.getScheme());
    this.setMethod(servletExecutionRequest.getMethod());
    this.setUri(servletExecutionRequest.getUri());
    this.setQueryString(servletExecutionRequest.getQueryString());
    this.setContentType(servletExecutionRequest.getContentType());
    this.setCharsetEncoding(servletExecutionRequest.getCharsetEncoding());
    this.setDateTime(servletExecutionRequest.getDateTime());
    this.setAllContentLength(servletExecutionRequest.getAllContentLength());
    this.setContentLength(servletExecutionRequest.getContentLength());
    this.setLocale(servletExecutionRequest.getLocale());
    this.setHeaders(servletExecutionRequest.getHeaders());
    this.setBody(servletExecutionRequest.getBody());
    this.setBodyText(servletExecutionRequest.getBodyText());
    this.setCurl(servletExecutionRequest.getCurl());
    this.setCreator(servletExecutionRequest.getCreator());
    this.setCreatorName(servletExecutionRequest.getCreatorName());
    this.setCreateDt(servletExecutionRequest.getCreateDt());
    this.setModifier(servletExecutionRequest.getModifier());
    this.setModifierName(servletExecutionRequest.getModifierName());
    this.setModifyDt(servletExecutionRequest.getModifyDt());
    this.setDeleteFlag(servletExecutionRequest.getDeleteFlag());
    this.setDeleteDt(servletExecutionRequest.getDeleteDt());
  }

}
