package cn.addenda.porttrail.common.pojo.redis.bo;

import cn.addenda.porttrail.common.entrypoint.EntryPointSnapshot;
import lombok.ToString;

@ToString
public abstract class AbstractRedisExecution implements RedisExecution {

  private EntryPointSnapshot entryPointSnapshot;

  private final String resultType;

  protected AbstractRedisExecution(String resultType) {
    this.resultType = resultType;
  }

  @Override
  public String getResultType() {
    return resultType;
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
