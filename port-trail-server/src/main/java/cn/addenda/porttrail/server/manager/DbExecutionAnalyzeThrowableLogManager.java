package cn.addenda.porttrail.server.manager;

import cn.addenda.porttrail.common.pojo.ServiceRuntimeInfo;
import cn.addenda.porttrail.common.pojo.db.bo.AbstractStatementExecutionBo;

public interface DbExecutionAnalyzeThrowableLogManager {

  void insert(byte[] bytes, String analyzeType, AbstractStatementExecutionBo abstractStatementExecutionBo, ServiceRuntimeInfo serviceRuntimeInfo, Long outerId, Throwable throwable);

}
