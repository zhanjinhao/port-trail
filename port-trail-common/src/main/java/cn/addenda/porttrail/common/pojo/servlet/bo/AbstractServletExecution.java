package cn.addenda.porttrail.common.pojo.servlet.bo;

import java.nio.charset.StandardCharsets;

public abstract class AbstractServletExecution implements ServletExecution {

  public static final String UNSUPPORTED_CONTENT_TYPE = "UNSUPPORTED_CONTENT_TYPE";

  public static final String UNSUPPORTED_CHARSET_ENCODING = "UNSUPPORTED_CHARSET_ENCODING";

  public static final String DEFAULT_CHARSET = StandardCharsets.UTF_8.name();

  private final String executionId;

  protected AbstractServletExecution(String executionId) {
    this.executionId = executionId;
  }

  @Override
  public String getExecutionId() {
    return executionId;
  }

}
