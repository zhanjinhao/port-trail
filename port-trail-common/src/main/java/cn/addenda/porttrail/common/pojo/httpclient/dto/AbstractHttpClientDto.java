package cn.addenda.porttrail.common.pojo.httpclient.dto;

import cn.addenda.porttrail.common.entrypoint.EntryPointSnapshot;
import cn.addenda.porttrail.common.pojo.AbstractDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class AbstractHttpClientDto extends AbstractDto {

  private static final byte[] UNSUPPORTED_CONTENT_TYPE = new byte[]{-1};
  private static final byte[] UNSUPPORTED_CHARSET_ENCODING = new byte[]{-2};
  private static final byte[] BODY_EMPTY = new byte[]{-3};
  private static final byte[] BODY_EXCEED_LENGTH = new byte[]{-4};
  private static final byte[] UNSUPPORTED_CONTENT_ENCODING = new byte[]{-5};

  public static byte[] getUNSUPPORTED_CONTENT_TYPE() {
    return UNSUPPORTED_CONTENT_TYPE.clone();
  }

  public static byte[] getUNSUPPORTED_CHARSET_ENCODING() {
    return UNSUPPORTED_CHARSET_ENCODING.clone();
  }

  public static byte[] getBODY_EMPTY() {
    return BODY_EMPTY.clone();
  }

  public static byte[] getBODY_EXCEED_LENGTH() {
    return BODY_EXCEED_LENGTH.clone();
  }

  public static byte[] getUNSUPPORTED_CONTENT_ENCODING() {
    return UNSUPPORTED_CONTENT_ENCODING.clone();
  }

  private final String executionId;

  private final String clientName;

  private EntryPointSnapshot entryPointSnapshot;

  public AbstractHttpClientDto(String executionId, String clientName) {
    this.executionId = executionId;
    this.clientName = clientName;
  }

}
