package cn.addenda.porttrail.infrastructure.writer;

import cn.addenda.porttrail.common.pojo.http.bo.HttpExecution;

public interface HttpWriter {

  void writeHttpRequest(HttpExecution httpExecution);

  void writeHttpResponse(HttpExecution httpExecution);

}
