package cn.addenda.porttrail.agent.writer.http;

import cn.addenda.porttrail.agent.link.LinkFacade;
import cn.addenda.porttrail.common.pojo.http.HttpExecution;
import cn.addenda.porttrail.facade.LogFacade;
import cn.addenda.porttrail.infrastructure.writer.HttpWriter;

public class AgentLogHttpWriter implements HttpWriter {

  private final LogFacade logFacade;

  public AgentLogHttpWriter() {
    logFacade = LinkFacade.createLogFacade(AgentLogHttpWriter.class, AgentLogHttpWriter.class.getName());
  }

  @Override
  public void writeHttpRequest(HttpExecution httpExecution) {
    logFacade.info("request: {}", LinkFacade.toStr(httpExecution));
  }

  @Override
  public void writeHttpResponse(HttpExecution httpExecution) {
    logFacade.info("response: {}", LinkFacade.toStr(httpExecution));
  }

}
