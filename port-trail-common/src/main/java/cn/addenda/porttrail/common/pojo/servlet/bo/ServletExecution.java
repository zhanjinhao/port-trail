package cn.addenda.porttrail.common.pojo.servlet.bo;

import cn.addenda.porttrail.common.entrypoint.EntryPointSnapshot;

public interface ServletExecution {

  String SERVLET_EXECUTION_TYPE_REQUEST = "REQUEST";

  String SERVLET_EXECUTION_TYPE_RESPONSE = "RESPONSE";

  String getServletExecutionType();

  String getExecutionId();

  void setEntryPointSnapshot(EntryPointSnapshot entryPointSnapshot);

  EntryPointSnapshot getEntryPointSnapshot();

}
