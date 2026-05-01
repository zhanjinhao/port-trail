package cn.addenda.porttrail.server.biz;

import cn.addenda.porttrail.common.pojo.servlet.dto.ServletRequestDto;
import cn.addenda.porttrail.common.pojo.servlet.dto.ServletResponseDto;
import cn.addenda.porttrail.server.bo.servlet.ServletExecutionRequestBo;
import cn.addenda.porttrail.server.bo.servlet.ServletExecutionResponseBo;

public interface ServletExecutionBiz {

  ServletExecutionRequestBo handleServletRequest(ServletRequestDto servletRequestDto);

  ServletExecutionResponseBo handleServletResponse(ServletResponseDto servletResponseDto);

}
