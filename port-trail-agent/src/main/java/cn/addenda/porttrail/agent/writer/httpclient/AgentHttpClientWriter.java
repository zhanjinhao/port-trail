package cn.addenda.porttrail.agent.writer.httpclient;

import cn.addenda.porttrail.agent.AgentPackage;
import cn.addenda.porttrail.agent.PortTrailAgentStartException;
import cn.addenda.porttrail.agent.link.LinkFacade;
import cn.addenda.porttrail.agent.log.AgentPortTrailLoggerFactory;
import cn.addenda.porttrail.agent.writer.AbstractAgentWriter;
import cn.addenda.porttrail.common.pojo.httpclient.bo.HttpClientExecution;
import cn.addenda.porttrail.common.util.StringUtils;
import cn.addenda.porttrail.infrastructure.jvm.JVMShutdown;
import cn.addenda.porttrail.infrastructure.jvm.JVMShutdownCallback;
import cn.addenda.porttrail.infrastructure.log.PortTrailLogger;
import cn.addenda.porttrail.infrastructure.writer.HttpClientWriter;

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
public class AgentHttpClientWriter extends AbstractAgentWriter implements HttpClientWriter {

  private static final PortTrailLogger log =
          AgentPortTrailLoggerFactory.getInstance().getPortTrailLogger(AgentHttpClientWriter.class);

  private static AgentHttpClientWriter instance;

  private final List<HttpClientWriter> httpClientWriterList;

  private final int hashSlotCount;

  private final int httpClientExecutionQueueSize;

  private final List<HttpClientExecutionConsumer> httpClientExecutionConsumerList;

  private AgentHttpClientWriter() {
    this.hashSlotCount = initHashSlotCount();
    this.httpClientExecutionQueueSize = initHttpClientExecutionQueueSize();
    this.httpClientWriterList = initHttpClientWriter();
    this.httpClientExecutionConsumerList = initExecutionConsumerList();
    initJvmShutdown();
  }

  public static AgentHttpClientWriter getInstance() {
    synchronized (AgentHttpClientWriter.class) {
      if (instance == null) {
        instance = new AgentHttpClientWriter();
      }
      return instance;
    }
  }

  private int initHashSlotCount() {
    Properties agentProperties = AgentPackage.getAgentProperties();
    String property = agentProperties.getProperty("httpClientWriter.hashSlotCount");
    try {
      return Integer.parseInt(property);
    } catch (Exception e) {
      throw new PortTrailAgentStartException(String.format("加载httpClientWriter.hashSlotCount异常，配置值为：%s", property), e);
    }
  }

  private int initHttpClientExecutionQueueSize() {
    Properties agentProperties = AgentPackage.getAgentProperties();
    String property = agentProperties.getProperty("httpClientWriter.httpClientExecutionQueue.size");
    try {
      return Integer.parseInt(property);
    } catch (Exception e) {
      throw new PortTrailAgentStartException(String.format("加载httpClientWriter.httpClientExecutionQueue.size异常，配置值为：%s", property), e);
    }
  }

  private List<HttpClientWriter> initHttpClientWriter() {
    List<HttpClientWriter> tmpList = new ArrayList<>();
    Properties agentProperties = AgentPackage.getAgentProperties();
    String httpClientWriterImplClass = agentProperties.getProperty("httpClientWriter.impl");
    if (httpClientWriterImplClass == null || httpClientWriterImplClass.isEmpty()) {
      return tmpList;
    }

    String[] httpClientWriterImplClassnames = httpClientWriterImplClass.split(",");
    for (String httpClientWriterImplClassname : httpClientWriterImplClassnames) {
      log.debug("init httpClientWriter.impl[{}] success.", httpClientWriterImplClassname);
      Optional.ofNullable(init(httpClientWriterImplClassname)).ifPresent(tmpList::add);
    }
    return tmpList;
  }

  private List<HttpClientExecutionConsumer> initExecutionConsumerList() {
    List<HttpClientExecutionConsumer> tmpList = new ArrayList<>();
    for (int i = 0; i < hashSlotCount; i++) {
      tmpList.add(new HttpClientExecutionConsumer(httpClientExecutionQueueSize, i));
    }
    return tmpList;
  }

  private HttpClientWriter init(String className) {
    try {
      Class<?> clazz = Class.forName(className);
      return (HttpClientWriter) clazz.newInstance();
    } catch (Exception e) {
      log.error("初始化HttpClientWriter[{}]失败。", className, e);
      return null;
    }
  }

  private void initJvmShutdown() {
    for (HttpClientExecutionConsumer httpClientExecutionConsumer : httpClientExecutionConsumerList) {
      JVMShutdown.getInstance().addJvmShutdownCallback(httpClientExecutionConsumer);
    }
  }

  private void offer(HttpClientExecution httpClientExecution) {
    String executionId = httpClientExecution.getExecutionId();
    if (executionId == null) {
      log.error("[{}]未设置executionId。", LinkFacade.toStr(httpClientExecution));
      return;
    }

    int hashSlotIndex = (executionId.hashCode() & Integer.MAX_VALUE) % hashSlotCount;
    HttpClientExecutionConsumer httpClientExecutionConsumer = httpClientExecutionConsumerList.get(hashSlotIndex);
    httpClientExecutionConsumer.offer(httpClientExecution);
  }

  @Override
  protected PortTrailLogger getLogger() {
    return log;
  }

  @Override
  public void writeHttpRequest(HttpClientExecution httpClientExecution) {
    offer(httpClientExecution);
  }

  @Override
  public void writeHttpResponse(HttpClientExecution httpClientExecution) {
    offer(httpClientExecution);
  }

  private class HttpClientExecutionConsumer implements JVMShutdownCallback {

    private final LinkedBlockingQueue<HttpClientExecution> httpClientExecutionQueue;
    private final String name;
    private final Thread httpClientExecutionConsumerThread;
    private volatile boolean ifRunning;

    public HttpClientExecutionConsumer(int queueSize, int hashIndex) {
      this.httpClientExecutionQueue = new LinkedBlockingQueue<>(queueSize);
      this.httpClientExecutionConsumerThread = new Thread(this::run);
      this.httpClientExecutionConsumerThread.setDaemon(true);
      this.name = String.format("HttpClientExecutionConsumer-%s", StringUtils.expandWithSpecifiedChar(String.valueOf(hashIndex), '0', 2));
      this.httpClientExecutionConsumerThread.setName(String.format("AgentHttpClientWriter-%s-Thread", name));
      this.httpClientExecutionConsumerThread.start();
      this.ifRunning = true;
    }

    public void offer(HttpClientExecution httpClientExecution) {
      if (!ifRunning) {
        log.error("[{}]未在运行，无法处理HttpClientExecution[{}]。", name, LinkFacade.toStr(httpClientExecution));
        return;
      }
      boolean offer = httpClientExecutionQueue.offer(httpClientExecution);
      if (!offer) {
        log.error("[{}]的队列已满，无法接收HttpClientExecution[{}]。", name, LinkFacade.toStr(httpClientExecution));
      }
    }

    private void run() {
      while (true) {
        HttpClientExecution httpClientExecution = null;
        try {
          httpClientExecution = httpClientExecutionQueue.take();
          write(httpClientExecution);
        } catch (InterruptedException e) {
          log.debug("[{}]关闭", httpClientExecutionConsumerThread.getName());
          Thread.currentThread().interrupt();
          break;
        } catch (Throwable t) {
          log.error("unexpected error: [{}].", LinkFacade.toStr(httpClientExecution), t);
          // 注意，这里不能break：消费某个HttpClientExecution失败的时候，后续的还要继续消费。事实上，代码如果没有bug，这里永远走不到。
        }
      }
    }

    @Override
    public int getOrder() {
      return 0;
    }

    @Override
    public void shutdown() {
      this.ifRunning = false;
      httpClientExecutionConsumerThread.interrupt();
      List<HttpClientExecution> remaining = new ArrayList<>();
      httpClientExecutionQueue.drainTo(remaining);
      if (!remaining.isEmpty()) {
        log.info("[{}]已关闭, 还有[{}]个HttpClientExecution未被执行。", httpClientExecutionConsumerThread.getName(), remaining.size());
        for (HttpClientExecution httpClientExecution : remaining) {
          write(httpClientExecution);
        }
        HttpClientExecution extra;
        while ((extra = httpClientExecutionQueue.poll()) != null) {
          log.info("[{}]已关闭, 兜底处理残留的HttpClientExecution[{}]。", httpClientExecutionConsumerThread.getName(), LinkFacade.toStr(extra));
          write(extra);
        }
      } else {
        log.info("[{}]已关闭, HttpClientExecution都执行完成。", httpClientExecutionConsumerThread.getName());
      }
    }

    private boolean write(HttpClientExecution httpClientExecution) {
      boolean ifWriteSuccess = true;
      for (HttpClientWriter httpClientWriter : httpClientWriterList) {
        try {
          String httpClientExecutionType = httpClientExecution.getHttpClientExecutionType();
          if (HttpClientExecution.HTTP_CLIENT_EXECUTION_TYPE_REQUEST.equals(httpClientExecutionType)) {
            httpClientWriter.writeHttpRequest(httpClientExecution);
          } else if (HttpClientExecution.HTTP_CLIENT_EXECUTION_TYPE_RESPONSE.equals(httpClientExecutionType)) {
            httpClientWriter.writeHttpResponse(httpClientExecution);
          } else {
            log.error("[{}] httpClientExecutionType is not support. HttpClientExecution is [{}].", httpClientWriter, LinkFacade.toStr(httpClientExecution));
          }
        } catch (Exception t) {
          log.error("[{}] write error. HttpClientExecution is [{}].", httpClientWriter, LinkFacade.toStr(httpClientExecution), t);
          ifWriteSuccess = false;
        }
      }

      return ifWriteSuccess;
    }

  }

}
