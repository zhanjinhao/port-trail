package cn.addenda.porttrail.common.pojo.http.dto;

import cn.addenda.porttrail.common.pojo.AbstractDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class AbstractHttpDto extends AbstractDto {

  public static final byte[] UNSUPPORTED_CONTENT_TYPE = new byte[]{-1};

  private final String requestId;

  public AbstractHttpDto(String requestId) {
    this.requestId = requestId;
  }

}
