package cn.addenda.porttrail.server.bo.redis;

import cn.addenda.porttrail.server.bo.est.EstEntryPointSnapshotBo;
import cn.addenda.porttrail.server.entity.RedisExecutionEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class RedisExecutionEntityBo extends RedisExecutionEntity {

  private EstEntryPointSnapshotBo estEntryPointSnapshotBo;

  public RedisExecutionEntityBo(RedisExecutionEntity redisExecutionEntity) {
    this.setId(redisExecutionEntity.getId());
    this.setSystemCode(redisExecutionEntity.getSystemCode());
    this.setServiceName(redisExecutionEntity.getServiceName());
    this.setImageName(redisExecutionEntity.getImageName());
    this.setEnv(redisExecutionEntity.getEnv());
    this.setInstanceId(redisExecutionEntity.getInstanceId());
    this.setResultType(redisExecutionEntity.getResultType());
    this.setCommandName(redisExecutionEntity.getCommandName());
    this.setCommandArgString(redisExecutionEntity.getCommandArgString());
    this.setPeer(redisExecutionEntity.getPeer());
    this.setResult(redisExecutionEntity.getResult());
    this.setResultText(redisExecutionEntity.getResultText());
    this.setError(redisExecutionEntity.getError());
    this.setStartTime(redisExecutionEntity.getStartTime());
    this.setEndTime(redisExecutionEntity.getEndTime());
    this.setCost(redisExecutionEntity.getCost());
    this.setCreateDt(redisExecutionEntity.getCreateDt());
    this.setModifier(redisExecutionEntity.getModifier());
    this.setModifierName(redisExecutionEntity.getModifierName());
    this.setModifyDt(redisExecutionEntity.getModifyDt());
    this.setDeleteFlag(redisExecutionEntity.getDeleteFlag());
    this.setDeleteDt(redisExecutionEntity.getDeleteDt());
  }

}
