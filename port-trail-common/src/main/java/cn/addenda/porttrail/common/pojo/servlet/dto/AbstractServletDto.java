package cn.addenda.porttrail.common.pojo.servlet.dto;

import cn.addenda.porttrail.common.pojo.AbstractDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class AbstractServletDto extends AbstractDto {

  public static final byte[] UNSUPPORTED_CONTENT_TYPE = new byte[]{-1};
  public static final byte[] UNSUPPORTED_CHARSET_ENCODING = new byte[]{-2};
  public static final byte[] BODY_EMPTY = new byte[]{-3};
  public static final byte[] BODY_EXCEED_LENGTH = new byte[]{-4};

  private final String executionId;

  public AbstractServletDto(String executionId) {
    this.executionId = executionId;
  }

}
