package cn.addenda.porttrail.agent.writer.redis;

import cn.addenda.porttrail.agent.AgentPackage;
import cn.addenda.porttrail.agent.PortTrailAgentStartException;
import cn.addenda.porttrail.agent.link.LinkFacade;
import cn.addenda.porttrail.agent.log.AgentPortTrailLoggerFactory;
import cn.addenda.porttrail.agent.writer.AbstractAgentWriter;
import cn.addenda.porttrail.common.pojo.redis.bo.RedisExecution;
import cn.addenda.porttrail.common.util.StringUtils;
import cn.addenda.porttrail.infrastructure.jvm.JVMShutdown;
import cn.addenda.porttrail.infrastructure.jvm.JVMShutdownCallback;
import cn.addenda.porttrail.infrastructure.log.PortTrailLogger;
import cn.addenda.porttrail.infrastructure.writer.RedisWriter;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 两个功能：
 * 1、本地缓存 <br/>
 * 2、做Hash <br/>
 */
public class AgentRedisWriter extends AbstractAgentWriter implements RedisWriter {

  private static final PortTrailLogger log =
          AgentPortTrailLoggerFactory.getInstance().getPortTrailLogger(AgentRedisWriter.class);

  private static AgentRedisWriter instance;

  private final List<RedisWriter> redisWriterList;

  private final int hashSlotCount;

  private final int redisExecutionQueueSize;

  private final List<RedisExecutionConsumer> redisExecutionConsumerList;

  private AgentRedisWriter() {
    this.hashSlotCount = initHashSlotCount();
    this.redisExecutionQueueSize = initRedisExecutionQueueSize();
    this.redisWriterList = initRedisWriter();
    this.redisExecutionConsumerList = initExecutionConsumerList();
    initJvmShutdown();
  }

  public static AgentRedisWriter getInstance() {
    synchronized (AgentRedisWriter.class) {
      if (instance == null) {
        instance = new AgentRedisWriter();
      }
      return instance;
    }
  }

  private int initHashSlotCount() {
    Properties agentProperties = AgentPackage.getAgentProperties();
    String property = agentProperties.getProperty("redisWriter.hashSlotCount");
    try {
      return Integer.parseInt(property);
    } catch (Exception e) {
      throw new PortTrailAgentStartException(String.format("加载redisWriter.hashSlotCount异常, 配置值为：%s", property), e);
    }
  }

  private int initRedisExecutionQueueSize() {
    Properties agentProperties = AgentPackage.getAgentProperties();
    String property = agentProperties.getProperty("redisWriter.redisExecutionQueue.size");
    try {
      return Integer.parseInt(property);
    } catch (Exception e) {
      throw new PortTrailAgentStartException(String.format("加载redisWriter.redisExecutionQueue.size异常, 配置值为：%s", property), e);
    }
  }

  private List<RedisWriter> initRedisWriter() {
    List<RedisWriter> tmpList = new ArrayList<>();
    Properties agentProperties = AgentPackage.getAgentProperties();
    String redisWriterImplClass = agentProperties.getProperty("redisWriter.impl");
    if (redisWriterImplClass == null || redisWriterImplClass.isEmpty()) {
      return tmpList;
    }

    String[] redisWriterImplClassnames = redisWriterImplClass.split(",");
    for (String redisWriterImplClassname : redisWriterImplClassnames) {
      log.debug("init redisWriter.impl[{}] success.", redisWriterImplClassname);
      Optional.ofNullable(init(redisWriterImplClassname)).ifPresent(tmpList::add);
    }
    return tmpList;
  }

  private List<RedisExecutionConsumer> initExecutionConsumerList() {
    List<RedisExecutionConsumer> tmpList = new ArrayList<>();
    for (int i = 0; i < hashSlotCount; i++) {
      tmpList.add(new RedisExecutionConsumer(redisExecutionQueueSize, i));
    }
    return tmpList;
  }

  private RedisWriter init(String className) {
    try {
      Class<?> clazz = Class.forName(className);
      return (RedisWriter) clazz.newInstance();
    } catch (Exception e) {
      log.error("初始化RedisWriter[{}]失败.", className, e);
      return null;
    }
  }

  private void initJvmShutdown() {
    for (RedisExecutionConsumer redisExecutionConsumer : redisExecutionConsumerList) {
      JVMShutdown.getInstance().addJvmShutdownCallback(redisExecutionConsumer);
    }
  }

  private void offer(RedisExecution redisExecution) {
    int hashSlotIndex = Math.abs(UUID.randomUUID().toString().replace("-", "").hashCode() % hashSlotCount);
    RedisExecutionConsumer redisExecutionConsumer = redisExecutionConsumerList.get(hashSlotIndex);
    redisExecutionConsumer.offer(redisExecution);
  }

  @Override
  protected PortTrailLogger getLogger() {
    return log;
  }

  @Override
  public void writeRedisExecution(RedisExecution redisExecution) {
    // todo 可配置
    if ("INFO".equals(redisExecution.getCommandName())
            || "CLIENT".equals(redisExecution.getCommandName())
            || "CLUSTER".equals(redisExecution.getCommandName())) {
      return;
    }
    offer(redisExecution);
  }

  private class RedisExecutionConsumer implements JVMShutdownCallback {

    private final LinkedBlockingQueue<RedisExecution> redisExecutionQueue;
    private final String name;
    private final Thread redisExecutionConsumerThread;
    private volatile boolean ifRunning;

    public RedisExecutionConsumer(int queueSize, int hashIndex) {
      this.redisExecutionQueue = new LinkedBlockingQueue<>(queueSize);
      this.redisExecutionConsumerThread = new Thread(this::run);
      this.redisExecutionConsumerThread.setDaemon(true);
      this.name = String.format("redisExecutionConsumer-%s", StringUtils.expandWithSpecifiedChar(String.valueOf(hashIndex), '0', 2));
      this.redisExecutionConsumerThread.setName(String.format("AgentRedisWriter-%s-Thread", name));
      this.redisExecutionConsumerThread.start();
      this.ifRunning = true;
    }

    public void offer(RedisExecution redisExecution) {
      if (!ifRunning) {
        log.error("[{}]未在运行, 无法处理RedisExecution[{}].", name, LinkFacade.toStr(redisExecution));
        return;
      }
      boolean offer = redisExecutionQueue.offer(redisExecution);
      if (!offer) {
        log.error("[{}]的队列已满, 无法接收RedisExecution[{}].", name, LinkFacade.toStr(redisExecution));
      }
    }

    private void run() {
      while (true) {
        RedisExecution redisExecution = null;
        try {
          redisExecution = redisExecutionQueue.take();
          write(redisExecution);
        } catch (InterruptedException e) {
          log.debug("[{}]关闭", redisExecutionConsumerThread.getName());
          Thread.currentThread().interrupt();
          break;
        } catch (Throwable t) {
          log.error("unexpected error: [{}].", LinkFacade.toStr(redisExecution), t);
          // 注意，这里不能break：消费某个RedisExecution失败的时候，后续的还要继续消费。事实上，代码如果没有bug，这里永远走不到。
        }
      }
    }

    @Override
    public Integer getOrder() {
      return 0;
    }

    @Override
    public void shutdown() {
      this.ifRunning = false;
      redisExecutionConsumerThread.interrupt();
      List<RedisExecution> remaining = new ArrayList<>();
      redisExecutionQueue.drainTo(remaining);
      if (!remaining.isEmpty()) {
        log.info("[{}]已关闭, 还有[{}]个RedisExecution未被执行.", redisExecutionConsumerThread.getName(), remaining.size());
        for (RedisExecution redisExecution : remaining) {
          write(redisExecution);
        }
        RedisExecution extra;
        while ((extra = redisExecutionQueue.poll()) != null) {
          log.info("[{}]已关闭, 兜底处理残留的RedisExecution[{}].", redisExecutionConsumerThread.getName(), LinkFacade.toStr(extra));
          write(extra);
        }
      } else {
        log.info("[{}]已关闭, RedisExecution都执行完成.", redisExecutionConsumerThread.getName());
      }
    }

    private boolean write(RedisExecution redisExecution) {
      boolean ifWriteSuccess = true;
      for (RedisWriter redisWriter : redisWriterList) {
        try {
          redisWriter.writeRedisExecution(redisExecution);
        } catch (Exception t) {
          log.error("[{}] write error. RedisExecution is [{}].", redisWriter, LinkFacade.toStr(redisExecution), t);
          ifWriteSuccess = false;
        }
      }

      return ifWriteSuccess;
    }

  }

}
