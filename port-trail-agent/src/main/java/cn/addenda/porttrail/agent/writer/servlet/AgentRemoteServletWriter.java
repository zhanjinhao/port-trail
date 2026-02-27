package cn.addenda.porttrail.agent.writer.servlet;

import cn.addenda.porttrail.agent.link.LinkFacade;
import cn.addenda.porttrail.common.pojo.servlet.bo.ServletExecution;
import cn.addenda.porttrail.common.pojo.servlet.bo.ServletRequestBo;
import cn.addenda.porttrail.common.pojo.servlet.bo.ServletResponseBo;
import cn.addenda.porttrail.common.pojo.servlet.dto.ServletRequestDto;
import cn.addenda.porttrail.common.pojo.servlet.dto.ServletResponseDto;
import cn.addenda.porttrail.common.util.JdkSerializationUtils;
import cn.addenda.porttrail.infrastructure.context.AgentContext;
import cn.addenda.porttrail.infrastructure.writer.ServletWriter;

public class AgentRemoteServletWriter implements ServletWriter {

  @Override
  public void writeServletRequest(ServletExecution servletExecution) {
    ServletRequestDto servletRequestDto =
            new ServletRequestDto((ServletRequestBo) servletExecution);
    servletRequestDto.setServiceRuntimeInfo(AgentContext.getServiceRuntimeInfo());
    LinkFacade.sendServletRequest(JdkSerializationUtils.serialize(servletRequestDto));
  }

  @Override
  public void writeServletResponse(ServletExecution servletExecution) {
    ServletResponseDto servletResponseDto =
            new ServletResponseDto((ServletResponseBo) servletExecution);
    servletResponseDto.setServiceRuntimeInfo(AgentContext.getServiceRuntimeInfo());
    LinkFacade.sendServletResponse(JdkSerializationUtils.serialize(servletResponseDto));
  }

}
