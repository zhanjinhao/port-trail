package cn.addenda.porttrail.common.pojo.httpclient.bo;

import cn.addenda.porttrail.common.entrypoint.EntryPointSnapshot;

import java.nio.charset.StandardCharsets;

public abstract class AbstractHttpClientExecution implements HttpClientExecution {

  public static final String UNSUPPORTED_CONTENT_TYPE = "@UNSUPPORTED#_$CONTENT%_^TYPE&";
  public static final String UNSUPPORTED_CHARSET_ENCODING = "@UNSUPPORTED#_$CHARSET%_^ENCODING&";
  public static final String BODY_EMPTY = "@BODY#_^EMPTY&";
  public static final String BODY_EXCEED_LENGTH = "@BODY#_$EXCEED%_^LENGTH&";
  public static final String UNSUPPORTED_CONTENT_ENCODING = "@UNSUPPORTED#_$CONTENT%_^ENCODING&";

  public static final String DEFAULT_CHARSET = StandardCharsets.UTF_8.name();

  private final String executionId;

  private final String clientName;

  private EntryPointSnapshot entryPointSnapshot;

  protected AbstractHttpClientExecution(String executionId, String clientName) {
    this.executionId = executionId;
    this.clientName = clientName;
  }

  @Override
  public String getExecutionId() {
    return executionId;
  }

  @Override
  public String getClientName() {
    return clientName;
  }

  @Override
  public void setEntryPointSnapshot(EntryPointSnapshot entryPointSnapshot) {
    this.entryPointSnapshot = entryPointSnapshot;
  }

  @Override
  public EntryPointSnapshot getEntryPointSnapshot() {
    return entryPointSnapshot;
  }

}
