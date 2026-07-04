package cn.addenda.porttrail.infrastructure.writer;

import cn.addenda.porttrail.common.pojo.redis.bo.RedisExecution;

public interface RedisWriter {

  void writeRedisExecution(RedisExecution redisExecution);

}
