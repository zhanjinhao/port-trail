package cn.addenda.porttrail.server.biz;

import cn.addenda.porttrail.common.pojo.redis.dto.RedisDto;
import cn.addenda.porttrail.server.bo.redis.RedisExecutionEntityBo;

public interface RedisExecutionBiz {

  RedisExecutionEntityBo handleRedisExecution(RedisDto redisDto);

}
