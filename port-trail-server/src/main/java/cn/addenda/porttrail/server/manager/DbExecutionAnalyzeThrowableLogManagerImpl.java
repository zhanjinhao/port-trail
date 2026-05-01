package cn.addenda.porttrail.server.manager;

import cn.addenda.component.base.jackson.util.JacksonUtils;
import cn.addenda.component.transaction.PlatformTransactionHelper;
import cn.addenda.porttrail.common.pojo.ServiceRuntimeInfo;
import cn.addenda.porttrail.common.pojo.db.bo.AbstractStatementExecutionBo;
import cn.addenda.porttrail.server.curd.DbExecutionAnalyzeThrowableLogCurder;
import cn.addenda.porttrail.server.entity.DbExecutionAnalyzeThrowableLog;
import cn.addenda.porttrail.server.util.ThrowableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DbExecutionAnalyzeThrowableLogManagerImpl implements DbExecutionAnalyzeThrowableLogManager {

  @Autowired
  private DbExecutionAnalyzeThrowableLogCurder dbExecutionAnalyzeThrowableLogCurder;

  @Autowired
  private PlatformTransactionHelper platformTransactionHelper;

  @Override
  public void insert(byte[] bytes, String analyzeType, AbstractStatementExecutionBo abstractStatementExecutionBo, ServiceRuntimeInfo serviceRuntimeInfo, Long outerId, Throwable throwable) {
    DbExecutionAnalyzeThrowableLog param = DbExecutionAnalyzeThrowableLog.ofParam();
    param.setSystemCode(serviceRuntimeInfo.getSystemCode());
    param.setServiceName(serviceRuntimeInfo.getServiceName());
    param.setImageName(serviceRuntimeInfo.getImageName());
    param.setEnv(serviceRuntimeInfo.getEnv());
    param.setInstanceId(serviceRuntimeInfo.getInstanceId());
    param.setDataSourcePortTrailId(abstractStatementExecutionBo.getDataSourcePortTrailId());
    param.setConnectionPortTrailId(abstractStatementExecutionBo.getConnectionPortTrailId());
    param.setStatementPortTrailId(abstractStatementExecutionBo.getStatementPortTrailId());
    param.setOuterId(outerId);
    param.setAnalyzeType(analyzeType);
    param.setBytes(bytes);
    param.setBoJson(JacksonUtils.toStr(abstractStatementExecutionBo));
    param.setThrowableStack(ThrowableUtils.getThrowableStr(throwable));
    platformTransactionHelper.doTransaction(() -> {
      dbExecutionAnalyzeThrowableLogCurder.insert(param);
    });
  }

}
