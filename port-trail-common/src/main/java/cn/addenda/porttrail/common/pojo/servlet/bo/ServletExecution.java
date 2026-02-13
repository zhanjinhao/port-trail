package cn.addenda.porttrail.common.pojo.servlet.bo;

public interface ServletExecution {

  String SERVLET_EXECUTION_TYPE_REQUEST = "REQUEST";

  String SERVLET_EXECUTION_TYPE_RESPONSE = "RESPONSE";

  String getServletExecutionType();

  String getExecutionId();

}
