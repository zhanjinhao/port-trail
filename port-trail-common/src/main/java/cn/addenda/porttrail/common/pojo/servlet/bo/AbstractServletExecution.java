package cn.addenda.porttrail.common.pojo.servlet.bo;

import java.nio.charset.StandardCharsets;

public abstract class AbstractServletExecution implements ServletExecution {
  public static final int UNKNOWN_CONTENT_LENGTH = -2;
  public static final String UNSUPPORTED_CONTENT_TYPE = "@UNSUPPORTED#_$CONTENT%_^TYPE&";
  public static final String UNSUPPORTED_CHARSET_ENCODING = "@UNSUPPORTED#_$CHARSET%_^ENCODING&";
  public static final String BODY_EMPTY = "@BODY#_^EMPTY&";
  public static final String BODY_EXCEED_LENGTH = "@BODY#_$EXCEED%_^LENGTH&";

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
