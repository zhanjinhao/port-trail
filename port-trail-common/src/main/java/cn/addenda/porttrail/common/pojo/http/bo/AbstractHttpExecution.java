package cn.addenda.porttrail.common.pojo.http.bo;

import cn.addenda.porttrail.common.pojo.http.HttpExecution;

import java.nio.charset.StandardCharsets;

public abstract class AbstractHttpExecution implements HttpExecution {

  public static final String UNSUPPORTED_CHARSET_ENCODING = "UNSUPPORTED_CHARSET_ENCODING";

  public static final String DEFAULT_CHARSET = StandardCharsets.UTF_8.name();

  private final String requestId;

  protected AbstractHttpExecution(String requestId) {
    this.requestId = requestId;
  }

  @Override
  public String getRequestId() {
    return requestId;
  }

}
