package cn.addenda.porttrail.server.manager;

import cn.addenda.component.base.jackson.util.JacksonUtils;
import cn.addenda.component.transaction.PlatformTransactionHelper;
import cn.addenda.porttrail.common.pojo.ServiceRuntimeInfo;
import cn.addenda.porttrail.common.pojo.httpclient.bo.AbstractHttpClientExecution;
import cn.addenda.porttrail.common.util.ThrowableUtils;
import cn.addenda.porttrail.server.curd.HttpClientExecutionHandleThrowableLogCurder;
import cn.addenda.porttrail.server.entity.HttpClientExecutionHandleThrowableLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HttpClientExecutionHandleThrowableLogManagerImpl implements HttpClientExecutionHandleThrowableLogManager {

  @Autowired
  private PlatformTransactionHelper platformTransactionHelper;

  @Autowired
  private HttpClientExecutionHandleThrowableLogCurder httpClientExecutionHandleThrowableLogCurder;

  @Override
  public void insert(byte[] bytes, String handleType, AbstractHttpClientExecution abstractHttpClientExecution, ServiceRuntimeInfo serviceRuntimeInfo, Throwable throwable) {
    HttpClientExecutionHandleThrowableLog param = HttpClientExecutionHandleThrowableLog.ofParam();
    param.setSystemCode(serviceRuntimeInfo.getSystemCode());
    param.setServiceName(serviceRuntimeInfo.getServiceName());
    param.setImageName(serviceRuntimeInfo.getImageName());
    param.setEnv(serviceRuntimeInfo.getEnv());
    param.setInstanceId(serviceRuntimeInfo.getInstanceId());
    param.setExecutionId(abstractHttpClientExecution.getExecutionId());
    param.setHandleType(handleType);
    param.setBytes(bytes);
    param.setBoJson(JacksonUtils.toStr(abstractHttpClientExecution));
    param.setThrowableStack(ThrowableUtils.getThrowableStr(throwable));
    platformTransactionHelper.doTransaction(() -> {
      httpClientExecutionHandleThrowableLogCurder.insert(param);
    });
  }

}
