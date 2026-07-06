package cn.addenda.porttrail.common.pojo.redis.dto;

import cn.addenda.porttrail.common.pojo.redis.bo.RedisBo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class RedisDto extends AbstractRedisDto {

  private String commandArgString;

  private String peer;

  private String result;

  private String error;

  private long startTime;

  private long endTime;

  private int cost;

  public RedisDto(String resultType, String commandName) {
    super(resultType, commandName);
  }

  public RedisDto(RedisBo redisBo) {
    super(redisBo.getResultType(), redisBo.getCommandName());
    this.commandArgString = redisBo.getCommandArgString();
    this.peer = redisBo.getPeer();
    this.result = redisBo.getResult();
    this.error = redisBo.getError();
    this.startTime = redisBo.getStartTime();
    this.endTime = redisBo.getEndTime();
    this.cost = redisBo.getCost();
    setEntryPointSnapshot(redisBo.getEntryPointSnapshot());
  }

}
