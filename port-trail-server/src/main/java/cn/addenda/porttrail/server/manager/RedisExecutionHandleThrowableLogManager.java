package cn.addenda.porttrail.server.manager;

import cn.addenda.porttrail.common.pojo.ServiceRuntimeInfo;
import cn.addenda.porttrail.common.pojo.redis.bo.RedisBo;

public interface RedisExecutionHandleThrowableLogManager {

  void insert(byte[] bytes, RedisBo redisBo, ServiceRuntimeInfo serviceRuntimeInfo, Throwable throwable);

}
