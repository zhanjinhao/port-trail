package cn.addenda.porttrail.server.biz;

import cn.addenda.porttrail.common.pojo.servlet.dto.ServletRequestDto;
import cn.addenda.porttrail.server.bo.servlet.ServletExecutionRequestBo;

public interface ServletExecutionBiz {

  ServletExecutionRequestBo handleServletRequest(ServletRequestDto servletRequestDto);

}
