package cn.addenda.porttrail.common.pojo.redis.bo;

import cn.addenda.porttrail.common.pojo.redis.dto.RedisDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString(callSuper = true)
public class RedisBo extends AbstractRedisExecution {

  private String command;

  private String commandArgString;

  private String peer;

  private String result;

  private String error;

  private long startTime;

  private long endTime;

  private int cost;

  public RedisBo(String resultType) {
    super(resultType);
  }

  public RedisBo(RedisDto redisDto) {
    super(redisDto.getResultType());
    this.command = redisDto.getCommand();
    this.commandArgString = redisDto.getCommandArgString();
    this.peer = redisDto.getPeer();
    this.result = redisDto.getResult();
    this.error = redisDto.getError();
    this.startTime = redisDto.getStartTime();
    this.endTime = redisDto.getEndTime();
    this.cost = redisDto.getCost();
  }

}
