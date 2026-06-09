package cn.addenda.porttrail.infrastructure.writer;

import cn.addenda.porttrail.common.pojo.httpclient.bo.HttpClientExecution;

public interface HttpClientWriter {

  void writeHttpRequest(HttpClientExecution httpClientExecution);

  void writeHttpResponse(HttpClientExecution httpClientExecution);

}
