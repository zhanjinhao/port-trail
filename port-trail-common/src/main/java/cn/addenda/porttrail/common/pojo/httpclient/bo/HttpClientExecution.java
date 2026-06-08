package cn.addenda.porttrail.common.pojo.httpclient.bo;

public interface HttpClientExecution {

  String HTTP_CLIENT_EXECUTION_TYPE_REQUEST = "REQUEST";

  String HTTP_CLIENT_EXECUTION_TYPE_RESPONSE = "RESPONSE";

  String getHttpClientExecutionType();

  String getExecutionId();

  String getClientName();

}
