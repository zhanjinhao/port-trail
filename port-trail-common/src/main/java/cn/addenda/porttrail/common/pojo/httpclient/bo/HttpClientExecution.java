package cn.addenda.porttrail.common.pojo.httpclient.bo;

import cn.addenda.porttrail.common.entrypoint.EntryPointSnapshot;

public interface HttpClientExecution {

  String HTTP_CLIENT_EXECUTION_TYPE_REQUEST = "REQUEST";

  String HTTP_CLIENT_EXECUTION_TYPE_RESPONSE = "RESPONSE";

  String getHttpClientExecutionType();

  String getExecutionId();

  String getClientName();

  void setEntryPointSnapshot(EntryPointSnapshot entryPointSnapshot);

  EntryPointSnapshot getEntryPointSnapshot();

}
