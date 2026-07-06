package cn.addenda.porttrail.common.pojo.redis.bo;

import cn.addenda.porttrail.common.entrypoint.EntryPointSnapshot;
import lombok.ToString;

@ToString
public abstract class AbstractRedisExecution implements RedisExecution {

  private EntryPointSnapshot entryPointSnapshot;

  private final String resultType;

  private final String commandName;

  protected AbstractRedisExecution(String resultType, String commandName) {
    this.resultType = resultType;
    this.commandName = commandName;
  }

  @Override
  public String getResultType() {
    return resultType;
  }

  @Override
  public String getCommandName() {
    return commandName;
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
