package cn.addenda.porttrail.common.pojo.httpclient.dto;

import cn.addenda.porttrail.common.pojo.AbstractDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class AbstractHttpClientDto extends AbstractDto {

  public static final byte[] UNSUPPORTED_CONTENT_TYPE = new byte[]{-1};
  public static final byte[] UNSUPPORTED_CHARSET_ENCODING = new byte[]{-2};
  public static final byte[] BODY_EMPTY = new byte[]{-3};
  public static final byte[] BODY_EXCEED_LENGTH = new byte[]{-4};
  public static final byte[] UNSUPPORTED_CONTENT_ENCODING = new byte[]{-5};

  private final String executionId;

  private final String clientName;

  public AbstractHttpClientDto(String executionId, String clientName) {
    this.executionId = executionId;
    this.clientName = clientName;
  }

}
