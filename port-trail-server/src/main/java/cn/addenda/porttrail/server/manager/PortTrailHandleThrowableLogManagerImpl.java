package cn.addenda.porttrail.server.manager;

import cn.addenda.component.base.jackson.util.JacksonUtils;
import cn.addenda.component.transaction.PlatformTransactionHelper;
import cn.addenda.porttrail.common.pojo.ServiceRuntimeInfo;
import cn.addenda.porttrail.common.pojo.db.bo.AbstractStatementExecutionBo;
import cn.addenda.porttrail.server.curd.PortTrailHandleThrowableLogCurder;
import cn.addenda.porttrail.server.entity.PortTrailHandleThrowableLog;
import cn.addenda.porttrail.server.util.ThrowableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PortTrailHandleThrowableLogManagerImpl implements PortTrailHandleThrowableLogManager {

  @Autowired
  private PortTrailHandleThrowableLogCurder portTrailHandleThrowableLogCurder;

  @Autowired
  private PlatformTransactionHelper platformTransactionHelper;

  @Override
  public void insert(byte[] bytes, String handleType, AbstractStatementExecutionBo abstractStatementExecutionBo, ServiceRuntimeInfo serviceRuntimeInfo, Throwable throwable) {
    PortTrailHandleThrowableLog param = PortTrailHandleThrowableLog.ofParam();
    param.setSystemCode(serviceRuntimeInfo.getSystemCode());
    param.setServiceName(serviceRuntimeInfo.getServiceName());
    param.setImageName(serviceRuntimeInfo.getImageName());
    param.setEnv(serviceRuntimeInfo.getEnv());
    param.setInstanceId(serviceRuntimeInfo.getInstanceId());
    param.setDataSourcePortTrailId(abstractStatementExecutionBo.getDataSourcePortTrailId());
    param.setConnectionPortTrailId(abstractStatementExecutionBo.getConnectionPortTrailId());
    param.setStatementPortTrailId(abstractStatementExecutionBo.getStatementPortTrailId());
    param.setHandleType(handleType);
    param.setBytes(bytes);
    param.setBoJson(JacksonUtils.toStr(abstractStatementExecutionBo));
    param.setThrowableStack(ThrowableUtils.getThrowableStr(throwable));
    platformTransactionHelper.doTransaction(() -> {
      portTrailHandleThrowableLogCurder.insert(param);
    });
  }

}
