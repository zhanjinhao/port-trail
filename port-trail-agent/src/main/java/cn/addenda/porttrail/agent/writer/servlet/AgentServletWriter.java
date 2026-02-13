package cn.addenda.porttrail.agent.writer.servlet;

import cn.addenda.porttrail.agent.AgentPackage;
import cn.addenda.porttrail.agent.PortTrailAgentStartException;
import cn.addenda.porttrail.agent.link.LinkFacade;
import cn.addenda.porttrail.agent.log.AgentPortTrailLoggerFactory;
import cn.addenda.porttrail.agent.writer.AbstractAgentWriter;
import cn.addenda.porttrail.common.pojo.servlet.bo.ServletExecution;
import cn.addenda.porttrail.common.util.StringUtils;
import cn.addenda.porttrail.infrastructure.jvm.JVMShutdown;
import cn.addenda.porttrail.infrastructure.jvm.JVMShutdownCallback;
import cn.addenda.porttrail.infrastructure.log.PortTrailLogger;
import cn.addenda.porttrail.infrastructure.writer.ServletWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 两个功能：
 * 1、本地缓存 <br/>
 * 2、按executionId做Hash <br/>
 */
public class AgentServletWriter extends AbstractAgentWriter implements ServletWriter {

  private static final PortTrailLogger log =
          AgentPortTrailLoggerFactory.getInstance().getPortTrailLogger(AgentServletWriter.class);

  private static AgentServletWriter instance;

  private final List<ServletWriter> servletWriterList;

  private final int hashSlotCount;

  private final int servletExecutionQueueSize;

  private final List<ServletExecutionConsumer> servletExecutionConsumerList;

  private AgentServletWriter() {
    this.hashSlotCount = initHashSlotCount();
    this.servletExecutionQueueSize = initServletExecutionQueueSize();
    this.servletWriterList = initServletWriter();
    this.servletExecutionConsumerList = initExecutionConsumerList();
    initJvmShutdown();
  }

  public static AgentServletWriter getInstance() {
    synchronized (AgentServletWriter.class) {
      if (instance == null) {
        instance = new AgentServletWriter();
      }
      return instance;
    }
  }

  private int initHashSlotCount() {
    Properties agentProperties = AgentPackage.getAgentProperties();
    String property = agentProperties.getProperty("servletWriter.hashSlotCount");
    try {
      return Integer.parseInt(property);
    } catch (Exception e) {
      throw new PortTrailAgentStartException(String.format("加载servletWriter.hashSlotCount异常，配置值为：%s", property), e);
    }
  }

  private int initServletExecutionQueueSize() {
    Properties agentProperties = AgentPackage.getAgentProperties();
    String property = agentProperties.getProperty("servletWriter.servletExecutionQueue.size");
    try {
      return Integer.parseInt(property);
    } catch (Exception e) {
      throw new PortTrailAgentStartException(String.format("加载servletWriter.servletExecutionQueue.size异常，配置值为：%s", property), e);
    }
  }

  private List<ServletWriter> initServletWriter() {
    List<ServletWriter> tmpList = new ArrayList<>();
    Properties agentProperties = AgentPackage.getAgentProperties();
    String servletWriterImplClass = agentProperties.getProperty("servletWriter.impl");
    if (servletWriterImplClass == null || servletWriterImplClass.isEmpty()) {
      return tmpList;
    }

    String[] servletWriterImplClassnames = servletWriterImplClass.split(",");
    for (String servletWriterImplClassname : servletWriterImplClassnames) {
      log.debug("init servletWriter.impl[{}] success.", servletWriterImplClassname);
      Optional.ofNullable(init(servletWriterImplClassname)).ifPresent(tmpList::add);
    }
    return tmpList;
  }

  private List<ServletExecutionConsumer> initExecutionConsumerList() {
    List<ServletExecutionConsumer> tmpList = new ArrayList<>();
    for (int i = 0; i < hashSlotCount; i++) {
      tmpList.add(new ServletExecutionConsumer(servletExecutionQueueSize, i));
    }
    return tmpList;
  }

  private ServletWriter init(String className) {
    try {
      Class<?> clazz = Class.forName(className);
      return (ServletWriter) clazz.newInstance();
    } catch (Exception e) {
      log.error("初始化ServletWriter[{}]失败。", className, e);
      return null;
    }
  }

  private void initJvmShutdown() {
    for (ServletExecutionConsumer servletExecutionConsumer : servletExecutionConsumerList) {
      JVMShutdown.getInstance().addJvmShutdownCallback(servletExecutionConsumer);
    }
  }

  private void offer(ServletExecution servletExecution) {
    String executionId = servletExecution.getExecutionId();
    if (executionId == null) {
      log.error("[{}]未设置executionId。", LinkFacade.toStr(servletExecution));
      return;
    }

    int hashSlotIndex = Math.abs(executionId.hashCode() % hashSlotCount);
    ServletExecutionConsumer servletExecutionConsumer = servletExecutionConsumerList.get(hashSlotIndex);
    servletExecutionConsumer.offer(servletExecution);
  }

  @Override
  protected PortTrailLogger getLogger() {
    return log;
  }

  @Override
  public void writeServletRequest(ServletExecution servletExecution) {
    offer(servletExecution);
  }

  @Override
  public void writeServletResponse(ServletExecution servletExecution) {
    offer(servletExecution);
  }

  private class ServletExecutionConsumer implements JVMShutdownCallback {

    private final LinkedBlockingQueue<ServletExecution> servletExecutionQueue;
    private final String name;
    private final Thread servletExecutionConsumerThread;
    private volatile boolean ifRunning;

    public ServletExecutionConsumer(int queueSize, int hashIndex) {
      this.servletExecutionQueue = new LinkedBlockingQueue<>(queueSize);
      this.servletExecutionConsumerThread = new Thread(this::run);
      this.servletExecutionConsumerThread.setDaemon(true);
      this.name = String.format("ServletExecutionConsumer-%s)", StringUtils.expandWithSpecifiedChar(String.valueOf(hashIndex), '0', 2));
      this.servletExecutionConsumerThread.setName(String.format("AgentServletWriter-%s-Thread", name));
      this.servletExecutionConsumerThread.start();
      this.ifRunning = true;
    }

    public void offer(ServletExecution servletExecution) {
      if (!ifRunning) {
        log.error("[{}]未在运行，无法处理ServletExecution[{}]。", name, LinkFacade.toStr(servletExecution));
        return;
      }
      boolean offer = servletExecutionQueue.offer(servletExecution);
      if (!offer) {
        log.error("[{}]的队列已满，无法接收ServletExecution[{}]。", name, LinkFacade.toStr(servletExecution));
      }
    }

    private void run() {
      while (true) {
        ServletExecution servletExecution = null;
        try {
          servletExecution = servletExecutionQueue.take();
          write(servletExecution);
        } catch (InterruptedException e) {
          log.debug("[{}]关闭", servletExecutionConsumerThread.getName());
          Thread.currentThread().interrupt();
          break;
        } catch (Throwable t) {
          log.error("unexpected error: [{}].", LinkFacade.toStr(servletExecution), t);
          // 注意，这里不能break：消费某个servletExecution失败的时候，后续的还要继续消费。事实上，代码如果没有bug，这里永远走不到。
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
      if (servletExecutionConsumerThread == null) {
        return;
      }
      servletExecutionConsumerThread.interrupt();
      if (servletExecutionQueue == null) {
        return;
      }
      ServletExecution[] servletExecutions = servletExecutionQueue.toArray(new ServletExecution[]{});
      if (servletExecutions.length > 0) {
        log.error("[{}]已关闭, 还有[{}]个ServletExecution未被执行。", servletExecutionConsumerThread.getName(), servletExecutions.length);
        for (ServletExecution servletExecution : servletExecutions) {
          write(servletExecution);
        }
      } else {
        log.info("[{}]已关闭, ServletExecution都执行完成。", servletExecutionConsumerThread.getName(), servletExecutions.length);
      }
    }

    private boolean write(ServletExecution servletExecution) {
      boolean ifWriteSuccess = true;
      for (ServletWriter servletWriter : servletWriterList) {
        try {
          String servletExecutionType = servletExecution.getServletExecutionType();
          if (ServletExecution.SERVLET_EXECUTION_TYPE_REQUEST.equals(servletExecutionType)) {
            servletWriter.writeServletRequest(servletExecution);
          } else if (ServletExecution.SERVLET_EXECUTION_TYPE_RESPONSE.equals(servletExecutionType)) {
            servletWriter.writeServletResponse(servletExecution);
          } else {
            log.error("[{}] servletExecutionType is not support. ServletExecution is [{}].", servletWriter, LinkFacade.toStr(servletExecution));
          }
        } catch (Exception t) {
          log.error("[{}] write error. ServletExecution is [{}].", servletWriter, LinkFacade.toStr(servletExecution), t);
          ifWriteSuccess = false;
        }
      }

      return ifWriteSuccess;
    }

  }

}
