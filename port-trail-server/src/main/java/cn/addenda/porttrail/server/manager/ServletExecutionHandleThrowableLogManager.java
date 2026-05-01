package cn.addenda.porttrail.server.manager;

import cn.addenda.porttrail.common.pojo.ServiceRuntimeInfo;
import cn.addenda.porttrail.common.pojo.servlet.bo.AbstractServletExecution;

public interface ServletExecutionHandleThrowableLogManager {

  void insert(byte[] bytes, String handleType, AbstractServletExecution abstractServletExecution, ServiceRuntimeInfo serviceRuntimeInfo, Throwable throwable);

}
