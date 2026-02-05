package cn.addenda.porttrail.agent.writer.http;

import cn.addenda.porttrail.agent.AgentPackage;
import cn.addenda.porttrail.agent.PortTrailAgentStartException;
import cn.addenda.porttrail.agent.link.LinkFacade;
import cn.addenda.porttrail.agent.log.AgentPortTrailLoggerFactory;
import cn.addenda.porttrail.agent.writer.AbstractAgentWriter;
import cn.addenda.porttrail.common.pojo.http.HttpExecution;
import cn.addenda.porttrail.common.util.StringUtils;
import cn.addenda.porttrail.infrastructure.jvm.JVMShutdown;
import cn.addenda.porttrail.infrastructure.jvm.JVMShutdownCallback;
import cn.addenda.porttrail.infrastructure.log.PortTrailLogger;
import cn.addenda.porttrail.infrastructure.writer.HttpWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 两个功能：
 * 1、本地缓存 <br/>
 * 2、按RequestId做Hash <br/>
 */
public class AgentHttpWriter extends AbstractAgentWriter implements HttpWriter {

  private static final PortTrailLogger log =
          AgentPortTrailLoggerFactory.getInstance().getPortTrailLogger(AgentHttpWriter.class);

  private static AgentHttpWriter instance;

  private final List<HttpWriter> httpWriterList;

  private final int hashSlotCount;

  private final int httpExecutionQueueSize;

  private final List<HttpExecutionConsumer> httpExecutionConsumerList;

  private AgentHttpWriter() {
    this.hashSlotCount = initHashSlotCount();
    this.httpExecutionQueueSize = initHttpExecutionQueueSize();
    this.httpWriterList = initHttpWriter();
    this.httpExecutionConsumerList = initExecutionConsumerList();
    initJvmShutdown();
  }

  public static AgentHttpWriter getInstance() {
    synchronized (AgentHttpWriter.class) {
      if (instance == null) {
        instance = new AgentHttpWriter();
      }
      return instance;
    }
  }

  private int initHashSlotCount() {
    Properties agentProperties = AgentPackage.getAgentProperties();
    String property = agentProperties.getProperty("httpWriter.hashSlotCount");
    try {
      return Integer.parseInt(property);
    } catch (Exception e) {
      throw new PortTrailAgentStartException(String.format("加载httpWriter.hashSlotCount异常，配置值为：%s", property), e);
    }
  }

  private int initHttpExecutionQueueSize() {
    Properties agentProperties = AgentPackage.getAgentProperties();
    String property = agentProperties.getProperty("httpWriter.httpExecutionQueue.size");
    try {
      return Integer.parseInt(property);
    } catch (Exception e) {
      throw new PortTrailAgentStartException(String.format("加载httpWriter.httpExecutionQueue.size异常，配置值为：%s", property), e);
    }
  }

  private List<HttpWriter> initHttpWriter() {
    List<HttpWriter> tmpList = new ArrayList<>();
    Properties agentProperties = AgentPackage.getAgentProperties();
    String httpWriterImplClass = agentProperties.getProperty("httpWriter.impl");
    if (httpWriterImplClass == null || httpWriterImplClass.isEmpty()) {
      return tmpList;
    }

    String[] httpWriterImplClassnames = httpWriterImplClass.split(",");
    for (String httpWriterImplClassname : httpWriterImplClassnames) {
      log.debug("init httpWriter.impl[{}] success.", httpWriterImplClassname);
      Optional.ofNullable(init(httpWriterImplClassname)).ifPresent(tmpList::add);
    }
    return tmpList;
  }

  private List<HttpExecutionConsumer> initExecutionConsumerList() {
    List<HttpExecutionConsumer> tmpList = new ArrayList<>();
    for (int i = 0; i < hashSlotCount; i++) {
      tmpList.add(new HttpExecutionConsumer(httpExecutionQueueSize, i));
    }
    return tmpList;
  }

  private HttpWriter init(String className) {
    try {
      Class<?> clazz = Class.forName(className);
      return (HttpWriter) clazz.newInstance();
    } catch (Exception e) {
      log.error("初始化HttpWriter[{}]失败。", className, e);
      return null;
    }
  }

  private void initJvmShutdown() {
    for (HttpExecutionConsumer httpExecutionConsumer : httpExecutionConsumerList) {
      JVMShutdown.getInstance().addJvmShutdownCallback(httpExecutionConsumer);
    }
  }

  private void offer(HttpExecution httpExecution) {
    String requestId = httpExecution.getRequestId();
    if (requestId == null) {
      log.error("[{}]未设置requestId。", LinkFacade.toStr(httpExecution));
      return;
    }

    int hashSlotIndex = Math.abs(requestId.hashCode() % hashSlotCount);
    HttpExecutionConsumer httpExecutionConsumer = httpExecutionConsumerList.get(hashSlotIndex);
    httpExecutionConsumer.offer(httpExecution);
  }

  @Override
  protected PortTrailLogger getLogger() {
    return log;
  }

  @Override
  public void writeHttpRequest(HttpExecution httpExecution) {
    offer(httpExecution);
  }

  @Override
  public void writeHttpResponse(HttpExecution httpExecution) {
    offer(httpExecution);
  }

  private class HttpExecutionConsumer implements JVMShutdownCallback {

    private final LinkedBlockingQueue<HttpExecution> httpExecutionQueue;
    private final String name;
    private final Thread httpExecutionConsumerThread;
    private volatile boolean ifRunning;

    public HttpExecutionConsumer(int queueSize, int hashIndex) {
      this.httpExecutionQueue = new LinkedBlockingQueue<>(queueSize);
      this.httpExecutionConsumerThread = new Thread(this::run);
      this.httpExecutionConsumerThread.setDaemon(true);
      this.name = String.format("HttpExecutionConsumer-%s)", StringUtils.expandWithSpecifiedChar(String.valueOf(hashIndex), '0', 2));
      this.httpExecutionConsumerThread.setName(String.format("AgentHttpWriter-%s-Thread", name));
      this.httpExecutionConsumerThread.start();
      this.ifRunning = true;
    }

    public void offer(HttpExecution httpExecution) {
      if (!ifRunning) {
        log.error("[{}]未在运行，无法处理HttpExecution[{}]。", name, LinkFacade.toStr(httpExecution));
        return;
      }
      boolean offer = httpExecutionQueue.offer(httpExecution);
      if (!offer) {
        log.error("[{}]的队列已满，无法接收HttpExecution[{}]。", name, LinkFacade.toStr(httpExecution));
      }
    }

    private void run() {
      while (true) {
        HttpExecution httpExecution = null;
        try {
          httpExecution = httpExecutionQueue.take();
          write(httpExecution);
        } catch (InterruptedException e) {
          log.debug("[{}]关闭", httpExecutionConsumerThread.getName());
          Thread.currentThread().interrupt();
          break;
        } catch (Throwable t) {
          log.error("unexpected error: [{}].", LinkFacade.toStr(httpExecution), t);
          // 注意，这里不能break：消费某个httpExecution失败的时候，后续的还要继续消费。事实上，代码如果没有bug，这里永远走不到。
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
      if (httpExecutionConsumerThread == null) {
        return;
      }
      httpExecutionConsumerThread.interrupt();
      if (httpExecutionQueue == null) {
        return;
      }
      HttpExecution[] httpExecutions = httpExecutionQueue.toArray(new HttpExecution[]{});
      if (httpExecutions.length > 0) {
        log.error("[{}]已关闭, 还有[{}]个httpExecution未被执行。", httpExecutionConsumerThread.getName(), httpExecutions.length);
        for (HttpExecution httpExecution : httpExecutions) {
          write(httpExecution);
        }
      } else {
        log.info("[{}]已关闭, httpExecution都执行完成。", httpExecutionConsumerThread.getName(), httpExecutions.length);
      }
    }

    private boolean write(HttpExecution httpExecution) {
      boolean ifWriteSuccess = true;
      for (HttpWriter httpWriter : httpWriterList) {
        try {
          String httpExecutionType = httpExecution.getHttpExecutionType();
          if (HttpExecution.HTTP_EXECUTION_TYPE_REQUEST.equals(httpExecutionType)) {
            httpWriter.writeHttpRequest(httpExecution);
          } else if (HttpExecution.HTTP_EXECUTION_TYPE_RESPONSE.equals(httpExecutionType)) {
            httpWriter.writeHttpResponse(httpExecution);
          } else {
            log.error("[{}] httpExecutionType is not support. HttpExecution is [{}].", httpWriter, LinkFacade.toStr(httpExecution));
          }
        } catch (Exception t) {
          log.error("[{}] write error. HttpExecution is [{}].", httpWriter, LinkFacade.toStr(httpExecution), t);
          ifWriteSuccess = false;
        }
      }

      return ifWriteSuccess;
    }

  }

}
