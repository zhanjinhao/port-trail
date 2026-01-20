package cn.addenda.porttrail.agent.writer.http;

import cn.addenda.porttrail.agent.link.LinkFacade;
import cn.addenda.porttrail.common.pojo.http.HttpExecution;
import cn.addenda.porttrail.common.pojo.http.bo.HttpRequestBo;
import cn.addenda.porttrail.common.pojo.http.bo.HttpResponseBo;
import cn.addenda.porttrail.common.pojo.http.dto.HttpRequestDto;
import cn.addenda.porttrail.common.pojo.http.dto.HttpResponseDto;
import cn.addenda.porttrail.common.util.SerializationUtils;
import cn.addenda.porttrail.infrastructure.context.AgentContext;
import cn.addenda.porttrail.infrastructure.writer.HttpWriter;

public class AgentRemoteHttpWriter implements HttpWriter {

  @Override
  public void writeHttpRequest(HttpExecution httpExecution) {
    HttpRequestDto httpRequestDto = HttpRequestDto.createByHttpRequestBo((HttpRequestBo) httpExecution);
    httpRequestDto.setServiceRuntimeInfo(AgentContext.getServiceRuntimeInfo());
    LinkFacade.sendHttpRequest(SerializationUtils.serialize(httpRequestDto));
  }

  @Override
  public void writeHttpResponse(HttpExecution httpExecution) {
    HttpResponseDto httpResponseDto = HttpResponseDto.createByHttpResponseBo((HttpResponseBo) httpExecution);
    httpResponseDto.setServiceRuntimeInfo(AgentContext.getServiceRuntimeInfo());
    LinkFacade.sendHttpResponse(SerializationUtils.serialize(httpResponseDto));
  }

}
