package cn.addenda.porttrail.server.manager;

import cn.addenda.component.base.jackson.util.JacksonUtils;
import cn.addenda.component.transaction.PlatformTransactionHelper;
import cn.addenda.porttrail.common.pojo.ServiceRuntimeInfo;
import cn.addenda.porttrail.common.pojo.servlet.bo.AbstractServletExecution;
import cn.addenda.porttrail.server.curd.ServletExecutionHandleThrowableLogCurder;
import cn.addenda.porttrail.server.entity.ServletExecutionHandleThrowableLog;
import cn.addenda.porttrail.server.util.ThrowableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServletExecutionHandleThrowableLogManagerImpl implements ServletExecutionHandleThrowableLogManager {

  @Autowired
  private PlatformTransactionHelper platformTransactionHelper;

  @Autowired
  private ServletExecutionHandleThrowableLogCurder servletExecutionHandleThrowableLogCurder;

  @Override
  public void insert(byte[] bytes, String handleType, AbstractServletExecution abstractServletExecution, ServiceRuntimeInfo serviceRuntimeInfo, Throwable throwable) {
    ServletExecutionHandleThrowableLog param = ServletExecutionHandleThrowableLog.ofParam();
    param.setSystemCode(serviceRuntimeInfo.getSystemCode());
    param.setServiceName(serviceRuntimeInfo.getServiceName());
    param.setImageName(serviceRuntimeInfo.getImageName());
    param.setEnv(serviceRuntimeInfo.getEnv());
    param.setInstanceId(serviceRuntimeInfo.getInstanceId());
    param.setExecutionId(abstractServletExecution.getExecutionId());
    param.setHandleType(handleType);
    param.setBytes(bytes);
    param.setBoJson(JacksonUtils.toStr(abstractServletExecution));
    param.setThrowableStack(ThrowableUtils.getThrowableStr(throwable));
    platformTransactionHelper.doTransaction(() -> {
      servletExecutionHandleThrowableLogCurder.insert(param);
    });
  }

}
