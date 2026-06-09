package cn.addenda.porttrail.agent.writer.httpclient;

import cn.addenda.porttrail.agent.link.LinkFacade;
import cn.addenda.porttrail.common.pojo.httpclient.bo.HttpClientExecution;
import cn.addenda.porttrail.facade.LogFacade;
import cn.addenda.porttrail.infrastructure.writer.HttpClientWriter;

public class AgentLogHttpClientWriter implements HttpClientWriter {

  private final LogFacade logFacade;

  public AgentLogHttpClientWriter() {
    logFacade = LinkFacade.createLogFacade(AgentLogHttpClientWriter.class, AgentLogHttpClientWriter.class.getName());
  }

  @Override
  public void writeHttpRequest(HttpClientExecution httpClientExecution) {
    logFacade.info("request: {}", LinkFacade.toStr(httpClientExecution));
  }

  @Override
  public void writeHttpResponse(HttpClientExecution httpClientExecution) {
    logFacade.info("response: {}", LinkFacade.toStr(httpClientExecution));
  }

}
