package cn.addenda.porttrail.server.controller;

import cn.addenda.porttrail.common.pojo.redis.bo.RedisBo;
import cn.addenda.porttrail.common.pojo.redis.dto.RedisDto;
import cn.addenda.porttrail.common.util.CompressUtils;
import cn.addenda.porttrail.common.util.JdkSerializationUtils;
import cn.addenda.porttrail.server.biz.RedisExecutionBiz;
import cn.addenda.porttrail.server.bo.redis.RedisExecutionEntityBo;
import cn.addenda.porttrail.server.entity.PortTrailDeserializeThrowableLog;
import cn.addenda.porttrail.server.manager.PortTrailDeserializeThrowableLogManager;
import cn.addenda.porttrail.server.manager.RedisExecutionHandleThrowableLogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("redisExecution")
public class RedisExecutionController {

  @Autowired
  private RedisExecutionBiz redisExecutionBiz;

  @Autowired
  private PortTrailDeserializeThrowableLogManager portTrailDeserializeThrowableLogManager;

  @Autowired
  private RedisExecutionHandleThrowableLogManager redisExecutionHandleThrowableLogManager;

  @PostMapping(value = "receiveRedisExecution", consumes = "application/octet-stream")
  public void receiveRedisExecution(@RequestBody byte[] bytes) {
    RedisDto redisDto;
    RedisBo redisBo;
    try {
      // 处理接收到的字节数组
      bytes = CompressUtils.decompress(bytes);
      redisDto = (RedisDto) JdkSerializationUtils.deserialize(bytes);
      redisBo = new RedisBo(redisDto);
    } catch (Throwable throwable) {
      portTrailDeserializeThrowableLogManager.insert(bytes, PortTrailDeserializeThrowableLog.DESERIALIZE_TYPE_REDIS_EXECUTION, throwable);
      return;
    }

    RedisExecutionEntityBo redisExecutionEntityBo;
    try {
      redisExecutionEntityBo = redisExecutionBiz.handleRedisExecution(redisDto);
    } catch (Throwable throwable) {
      redisExecutionHandleThrowableLogManager.insert(bytes, redisBo, redisDto.getServiceRuntimeInfo(), throwable);
      return;
    }
  }

}
