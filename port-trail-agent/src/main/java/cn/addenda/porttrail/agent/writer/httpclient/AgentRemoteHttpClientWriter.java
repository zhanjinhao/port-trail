package cn.addenda.porttrail.agent.writer.httpclient;

import cn.addenda.porttrail.agent.link.LinkFacade;
import cn.addenda.porttrail.common.pojo.httpclient.bo.HttpClientExecution;
import cn.addenda.porttrail.common.pojo.httpclient.bo.HttpClientRequestBo;
import cn.addenda.porttrail.common.pojo.httpclient.bo.HttpClientResponseBo;
import cn.addenda.porttrail.common.pojo.httpclient.dto.HttpClientRequestDto;
import cn.addenda.porttrail.common.pojo.httpclient.dto.HttpClientResponseDto;
import cn.addenda.porttrail.common.util.JdkSerializationUtils;
import cn.addenda.porttrail.infrastructure.context.AgentContext;
import cn.addenda.porttrail.infrastructure.writer.HttpClientWriter;

public class AgentRemoteHttpClientWriter implements HttpClientWriter {

  @Override
  public void writeHttpRequest(HttpClientExecution httpClientExecution) {
    HttpClientRequestDto httpClientRequestDto =
            new HttpClientRequestDto((HttpClientRequestBo) httpClientExecution);
    httpClientRequestDto.setServiceRuntimeInfo(AgentContext.getServiceRuntimeInfo());
    LinkFacade.sendHttpClientRequest(JdkSerializationUtils.serialize(httpClientRequestDto));
  }

  @Override
  public void writeHttpResponse(HttpClientExecution httpClientExecution) {
    HttpClientResponseDto httpClientResponseDto =
            new HttpClientResponseDto((HttpClientResponseBo) httpClientExecution);
    httpClientResponseDto.setServiceRuntimeInfo(AgentContext.getServiceRuntimeInfo());
    LinkFacade.sendHttpClientResponse(JdkSerializationUtils.serialize(httpClientResponseDto));
  }

}
