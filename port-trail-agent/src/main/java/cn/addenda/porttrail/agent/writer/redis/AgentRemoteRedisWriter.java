package cn.addenda.porttrail.agent.writer.redis;

import cn.addenda.porttrail.agent.link.LinkFacade;
import cn.addenda.porttrail.common.pojo.redis.bo.RedisBo;
import cn.addenda.porttrail.common.pojo.redis.bo.RedisExecution;
import cn.addenda.porttrail.common.pojo.redis.dto.RedisDto;
import cn.addenda.porttrail.common.util.JdkSerializationUtils;
import cn.addenda.porttrail.infrastructure.context.AgentContext;
import cn.addenda.porttrail.infrastructure.writer.RedisWriter;

public class AgentRemoteRedisWriter implements RedisWriter {

  @Override
  public void writeRedisExecution(RedisExecution redisExecution) {
    RedisDto redisDto =
            new RedisDto((RedisBo) redisExecution);
    redisDto.setServiceRuntimeInfo(AgentContext.getServiceRuntimeInfo());
    LinkFacade.sendRedisExecution(JdkSerializationUtils.serialize(redisDto));
  }

}
