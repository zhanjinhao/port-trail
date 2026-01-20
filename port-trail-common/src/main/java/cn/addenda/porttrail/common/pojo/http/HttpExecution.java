package cn.addenda.porttrail.common.pojo.http;

public interface HttpExecution {

  String HTTP_EXECUTION_TYPE_REQUEST = "REQUEST";

  String HTTP_EXECUTION_TYPE_RESPONSE = "RESPONSE";

  String getHttpExecutionType();

  String getRequestId();

}
