package cn.addenda.porttrail.facade;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class HttpRequestResult {

  private final int status;
  private final long cost;
  private final String content;

  public HttpRequestResult(final int status, final long cost, final String content) {
    this.status = status;
    this.cost = cost;
    this.content = content;
  }

}
