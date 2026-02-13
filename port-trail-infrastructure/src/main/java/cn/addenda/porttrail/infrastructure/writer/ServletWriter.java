package cn.addenda.porttrail.infrastructure.writer;

import cn.addenda.porttrail.common.pojo.servlet.bo.ServletExecution;

public interface ServletWriter {

  void writeServletRequest(ServletExecution servletExecution);

  void writeServletResponse(ServletExecution servletExecution);

}
