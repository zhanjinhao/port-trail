package cn.addenda.porttrail.common.pojo.redis.dto;

import cn.addenda.porttrail.common.entrypoint.EntryPointSnapshot;
import cn.addenda.porttrail.common.pojo.AbstractDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class AbstractRedisDto extends AbstractDto {

  private EntryPointSnapshot entryPointSnapshot;

  private final String resultType;

  private String commandName;

  protected AbstractRedisDto(String resultType, String commandName) {
    this.resultType = resultType;
    this.commandName = commandName;
  }

}
