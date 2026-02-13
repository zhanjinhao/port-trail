package cn.addenda.porttrail.agent.writer.servlet;

import cn.addenda.porttrail.agent.link.LinkFacade;
import cn.addenda.porttrail.common.pojo.servlet.bo.ServletExecution;
import cn.addenda.porttrail.facade.LogFacade;
import cn.addenda.porttrail.infrastructure.writer.ServletWriter;

public class AgentLogServletWriter implements ServletWriter {

  private final LogFacade logFacade;

  public AgentLogServletWriter() {
    logFacade = LinkFacade.createLogFacade(AgentLogServletWriter.class, AgentLogServletWriter.class.getName());
  }

  @Override
  public void writeServletRequest(ServletExecution servletExecution) {
    logFacade.info("request: {}", LinkFacade.toStr(servletExecution));
  }

  @Override
  public void writeServletResponse(ServletExecution servletExecution) {
    logFacade.info("response: {}", LinkFacade.toStr(servletExecution));
  }

}
