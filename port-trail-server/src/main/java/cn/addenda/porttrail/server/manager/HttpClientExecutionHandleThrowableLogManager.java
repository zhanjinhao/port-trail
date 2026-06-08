package cn.addenda.porttrail.server.manager;

import cn.addenda.porttrail.common.pojo.ServiceRuntimeInfo;
import cn.addenda.porttrail.common.pojo.httpclient.bo.AbstractHttpClientExecution;

public interface HttpClientExecutionHandleThrowableLogManager {

  void insert(byte[] bytes, String handleType, AbstractHttpClientExecution abstractHttpClientExecution, ServiceRuntimeInfo serviceRuntimeInfo, Throwable throwable);

}
