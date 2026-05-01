package cn.addenda.porttrail.server.manager;

import cn.addenda.porttrail.common.pojo.ServiceRuntimeInfo;
import cn.addenda.porttrail.common.pojo.db.bo.AbstractStatementExecutionBo;

public interface DbExecutionHandleThrowableLogManager {

  void insert(byte[] bytes, String handleType, AbstractStatementExecutionBo abstractStatementExecutionBo, ServiceRuntimeInfo serviceRuntimeInfo, Throwable throwable);

}
