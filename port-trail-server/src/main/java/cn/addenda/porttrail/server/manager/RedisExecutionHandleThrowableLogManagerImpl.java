package cn.addenda.porttrail.server.manager;

import cn.addenda.component.base.jackson.util.JacksonUtils;
import cn.addenda.component.transaction.PlatformTransactionHelper;
import cn.addenda.porttrail.common.pojo.ServiceRuntimeInfo;
import cn.addenda.porttrail.common.pojo.redis.bo.RedisBo;
import cn.addenda.porttrail.common.util.ThrowableUtils;
import cn.addenda.porttrail.server.curd.RedisExecutionHandleThrowableLogCurder;
import cn.addenda.porttrail.server.entity.RedisExecutionHandleThrowableLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RedisExecutionHandleThrowableLogManagerImpl implements RedisExecutionHandleThrowableLogManager {

  @Autowired
  private RedisExecutionHandleThrowableLogCurder redisExecutionHandleThrowableLogCurder;

  @Autowired
  private PlatformTransactionHelper platformTransactionHelper;

  @Override
  public void insert(byte[] bytes, RedisBo redisBo, ServiceRuntimeInfo serviceRuntimeInfo, Throwable throwable) {
    RedisExecutionHandleThrowableLog param = RedisExecutionHandleThrowableLog.ofParam();
    param.setSystemCode(serviceRuntimeInfo.getSystemCode());
    param.setServiceName(serviceRuntimeInfo.getServiceName());
    param.setImageName(serviceRuntimeInfo.getImageName());
    param.setEnv(serviceRuntimeInfo.getEnv());
    param.setInstanceId(serviceRuntimeInfo.getInstanceId());
    param.setResultType(redisBo.getResultType());
    param.setBytes(bytes);
    param.setBoJson(JacksonUtils.toStr(redisBo));
    param.setThrowableStack(ThrowableUtils.getThrowableStr(throwable));
    platformTransactionHelper.doTransaction(() -> {
      redisExecutionHandleThrowableLogCurder.insert(param);
    });
  }

}
