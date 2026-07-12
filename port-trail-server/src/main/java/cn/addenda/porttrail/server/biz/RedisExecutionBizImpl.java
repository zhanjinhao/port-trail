package cn.addenda.porttrail.server.biz;

import cn.addenda.component.base.datetime.DateUtils;
import cn.addenda.component.transaction.PlatformTransactionHelper;
import cn.addenda.porttrail.common.pojo.ServiceRuntimeInfo;
import cn.addenda.porttrail.common.pojo.redis.dto.RedisDto;
import cn.addenda.porttrail.common.util.CompressUtils;
import cn.addenda.porttrail.common.util.JdkSerializationUtils;
import cn.addenda.porttrail.server.bo.EntryPointSnapshotEntityBo;
import cn.addenda.porttrail.server.bo.redis.RedisExecutionEntityBo;
import cn.addenda.porttrail.server.curd.RedisExecutionEntityCurder;
import cn.addenda.porttrail.server.entity.RedisExecutionEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class RedisExecutionBizImpl implements RedisExecutionBiz {

  @Autowired
  private PlatformTransactionHelper transactionHelperNew;

  @Autowired
  private RedisExecutionEntityCurder redisExecutionEntityCurder;

  @Autowired
  private EntryPointSnapshotEntityBiz entryPointSnapshotEntityBiz;

  @Override
  public RedisExecutionEntityBo handleRedisExecution(RedisDto redisDto) {
    return transactionHelperNew.doTransaction(() -> {
      EntryPointSnapshotEntityBo entryPointSnapshotEntityBo = entryPointSnapshotEntityBiz.insert(redisDto.getEntryPointSnapshot());

      ServiceRuntimeInfo serviceRuntimeInfo = redisDto.getServiceRuntimeInfo();

      RedisExecutionEntity param = RedisExecutionEntity.ofParam();
      param.setSystemCode(serviceRuntimeInfo.getSystemCode());
      param.setServiceName(serviceRuntimeInfo.getServiceName());
      param.setImageName(serviceRuntimeInfo.getImageName());
      param.setEnv(serviceRuntimeInfo.getEnv());
      param.setInstanceId(serviceRuntimeInfo.getInstanceId());
      param.setResultType(redisDto.getResultType());
      param.setCommandName(redisDto.getCommandName());
      param.setCommandArgString(redisDto.getCommandArgString());
      param.setPeer(redisDto.getPeer());
      param.setResult(Optional.ofNullable(redisDto.getResult()).map(a -> CompressUtils.compress(JdkSerializationUtils.serialize(a))).orElse(null));
      param.setResultText(redisDto.getResult());
      param.setError(redisDto.getError());
      param.setStartTime(DateUtils.timestampToLocalDateTime(redisDto.getStartTime()));
      param.setEndTime(DateUtils.timestampToLocalDateTime(redisDto.getEndTime()));
      param.setCost(redisDto.getCost());
      param.setEntryPointSnapshotId(entryPointSnapshotEntityBo.getId());

      redisExecutionEntityCurder.insert(param);

      RedisExecutionEntityBo redisExecutionEntityBo = new RedisExecutionEntityBo(param);
      redisExecutionEntityBo.setEntryPointSnapshotEntityBo(entryPointSnapshotEntityBo);

      return redisExecutionEntityBo;
    });
  }

}
