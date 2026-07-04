package cn.addenda.porttrail.agent.writer.redis;

import cn.addenda.porttrail.agent.link.LinkFacade;
import cn.addenda.porttrail.common.pojo.redis.bo.RedisExecution;
import cn.addenda.porttrail.facade.LogFacade;
import cn.addenda.porttrail.infrastructure.writer.RedisWriter;

public class AgentLogRedisWriter implements RedisWriter {

  private final LogFacade logFacade;

  public AgentLogRedisWriter() {
    logFacade = LinkFacade.createLogFacade(AgentLogRedisWriter.class, AgentLogRedisWriter.class.getName());
  }

  @Override
  public void writeRedisExecution(RedisExecution redisExecution) {
    logFacade.info("redisExecution: {}", LinkFacade.toStr(redisExecution));
  }

}
