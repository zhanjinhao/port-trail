package cn.addenda.porttrail.agent.writer.http;

import cn.addenda.porttrail.agent.link.LinkFacade;
import cn.addenda.porttrail.common.pojo.http.bo.HttpExecution;
import cn.addenda.porttrail.common.pojo.http.bo.HttpRequestBo;
import cn.addenda.porttrail.common.pojo.http.bo.HttpResponseBo;
import cn.addenda.porttrail.common.pojo.http.dto.HttpRequestDto;
import cn.addenda.porttrail.common.pojo.http.dto.HttpResponseDto;
import cn.addenda.porttrail.common.util.JdkSerializationUtils;
import cn.addenda.porttrail.infrastructure.context.AgentContext;
import cn.addenda.porttrail.infrastructure.writer.HttpWriter;

public class AgentRemoteHttpWriter implements HttpWriter {

  @Override
  public void writeHttpRequest(HttpExecution httpExecution) {
    HttpRequestDto httpRequestDto =
            new HttpRequestDto((HttpRequestBo) httpExecution);
    httpRequestDto.setServiceRuntimeInfo(AgentContext.getServiceRuntimeInfo());
    LinkFacade.sendHttpRequest(JdkSerializationUtils.serialize(httpRequestDto));
  }

  @Override
  public void writeHttpResponse(HttpExecution httpExecution) {
    HttpResponseDto httpResponseDto =
            new HttpResponseDto((HttpResponseBo) httpExecution);
    httpResponseDto.setServiceRuntimeInfo(AgentContext.getServiceRuntimeInfo());
    LinkFacade.sendHttpResponse(JdkSerializationUtils.serialize(httpResponseDto));
  }

}
