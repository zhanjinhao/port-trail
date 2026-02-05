package cn.addenda.porttrail.agent.writer.sql;

import cn.addenda.porttrail.agent.AgentPackage;
import cn.addenda.porttrail.agent.PortTrailAgentStartException;
import cn.addenda.porttrail.agent.link.LinkFacade;
import cn.addenda.porttrail.agent.log.AgentPortTrailLoggerFactory;
import cn.addenda.porttrail.agent.writer.AbstractAgentWriter;
import cn.addenda.porttrail.common.pojo.db.DbExecution;
import cn.addenda.porttrail.common.util.StringUtils;
import cn.addenda.porttrail.infrastructure.jvm.JVMShutdown;
import cn.addenda.porttrail.infrastructure.jvm.JVMShutdownCallback;
import cn.addenda.porttrail.infrastructure.log.PortTrailLogger;
import cn.addenda.porttrail.infrastructure.writer.SqlWriter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Config需要保证一定先于SQL输出
 * <p>
 * 两个功能：
 * 1、本地缓存 <br/>
 * 2、按ConnectionPortTrailId做Hash <br/>
 */
public class AgentSqlWriter extends AbstractAgentWriter implements SqlWriter {

  private static final PortTrailLogger log =
          AgentPortTrailLoggerFactory.getInstance().getPortTrailLogger(AgentSqlWriter.class);

  private static AgentSqlWriter instance;

  private final List<SqlWriter> sqlWriterList;

  private final int hashSlotCount;

  private final int dbExecutionQueueSize;

  private final List<DbExecutionConsumer> dbExecutionConsumerList;

  private AgentSqlWriter() {
    this.hashSlotCount = initHashSlotCount();
    this.dbExecutionQueueSize = initDbExecutionQueueSize();
    this.sqlWriterList = initSqlWriter();
    this.dbExecutionConsumerList = initExecutionConsumerList();
    initJvmShutdown();
  }

  private int initDbExecutionQueueSize() {
    Properties agentProperties = AgentPackage.getAgentProperties();
    String property = agentProperties.getProperty("sqlWriter.dbExecutionQueue.size");
    try {
      return Integer.parseInt(property);
    } catch (Exception e) {
      throw new PortTrailAgentStartException(String.format("加载sqlWriter.dbExecutionQueue.size异常，配置值为：%s", property), e);
    }
  }

  public static AgentSqlWriter getInstance() {
    synchronized (AgentSqlWriter.class) {
      if (instance == null) {
        instance = new AgentSqlWriter();
      }
      return instance;
    }
  }

  private int initHashSlotCount() {
    Properties agentProperties = AgentPackage.getAgentProperties();
    String property = agentProperties.getProperty("sqlWriter.hashSlotCount");
    try {
      return Integer.parseInt(property);
    } catch (Exception e) {
      throw new PortTrailAgentStartException(String.format("加载sqlWriter.hashSlotCount异常，配置值为：%s", property), e);
    }
  }

  private List<SqlWriter> initSqlWriter() {
    List<SqlWriter> tmpList = new ArrayList<>();
    Properties agentProperties = AgentPackage.getAgentProperties();
    String sqlWriterImplClass = agentProperties.getProperty("sqlWriter.impl");
    if (sqlWriterImplClass == null || sqlWriterImplClass.isEmpty()) {
      return tmpList;
    }

    String[] sqlWriterImplClassnames = sqlWriterImplClass.split(",");
    for (String sqlWriterImplClassname : sqlWriterImplClassnames) {
      log.debug("init sqlWriter.impl[{}] success.", sqlWriterImplClassname);
      Optional.ofNullable(init(sqlWriterImplClassname)).ifPresent(tmpList::add);
    }
    return tmpList;
  }

  private List<DbExecutionConsumer> initExecutionConsumerList() {
    List<DbExecutionConsumer> tmpList = new ArrayList<>();
    for (int i = 0; i < hashSlotCount; i++) {
      tmpList.add(new DbExecutionConsumer(dbExecutionQueueSize, i));
    }
    return tmpList;
  }

  private SqlWriter init(String className) {
    try {
      Class<?> clazz = Class.forName(className);
      return (SqlWriter) clazz.newInstance();
    } catch (Exception e) {
      log.error("初始化SqlWriter[{}]失败。", className, e);
      return null;
    }
  }

  private void initJvmShutdown() {
    for (DbExecutionConsumer dbExecutionConsumer : dbExecutionConsumerList) {
      JVMShutdown.getInstance().addJvmShutdownCallback(dbExecutionConsumer);
    }
  }

  @Override
  public void writeSql(DbExecution dbExecution) {
    offer(dbExecution);
  }

  @Override
  public void writePreparedSql(DbExecution dbExecution) {
    offer(dbExecution);
  }

  @Override
  public void writeConfig(DbExecution dbExecution) {
    offer(dbExecution);
  }

  private void offer(DbExecution dbExecution) {
    String connectionPortTrailId = dbExecution.getConnectionPortTrailId();
    if (connectionPortTrailId == null) {
      log.error("[{}]未设置ConnectionPortTrailId。", LinkFacade.toStr(dbExecution));
      return;
    }

    int hashSlotIndex = Math.abs(connectionPortTrailId.hashCode() % hashSlotCount);
    DbExecutionConsumer dbExecutionConsumer = dbExecutionConsumerList.get(hashSlotIndex);
    dbExecutionConsumer.offer(dbExecution);
  }

  @Override
  protected PortTrailLogger getLogger() {
    return log;
  }

  private class DbExecutionConsumer implements JVMShutdownCallback {

    private final LinkedBlockingQueue<DbExecution> dbExecutionQueue;
    private final String name;
    private final Thread dbExecutionConsumerThread;
    private volatile boolean ifRunning;

    public DbExecutionConsumer(int queueSize, int hashIndex) {
      this.dbExecutionQueue = new LinkedBlockingQueue<>(queueSize);
      this.dbExecutionConsumerThread = new Thread(this::run);
      this.dbExecutionConsumerThread.setDaemon(true);
      this.name = String.format("DbExecutionConsumer-%s)", StringUtils.expandWithSpecifiedChar(String.valueOf(hashIndex), '0', 2));
      this.dbExecutionConsumerThread.setName(String.format("AgentSqlWriter-%s-Thread", name));
      this.dbExecutionConsumerThread.start();
      this.ifRunning = true;
    }

    private final Map<String, DbExecution> offerFailedConfigDbExecutionMap = new ConcurrentHashMap<>();

    public void offer(DbExecution dbExecution) {
      if (!ifRunning) {
        log.error("[{}]未在运行，无法处理DbExecution[{}]。", name, LinkFacade.toStr(dbExecution));
        return;
      }
      boolean offer;
      if (DbExecution.DB_EXECUTION_TYPE_CONFIG.equals(dbExecution.getDbExecutionType())) {
        offer = retryOffer(dbExecutionQueue::offer, dbExecution);
      } else {
        offer = dbExecutionQueue.offer(dbExecution);
      }

      if (!offer) {
        // Config数据如果offer失败，需要缓存起来
        if (DbExecution.DB_EXECUTION_TYPE_CONFIG.equals(dbExecution.getDbExecutionType())) {
          offerFailedConfigDbExecutionMap.put(dbExecution.getConnectionPortTrailId(), dbExecution);
        }
        log.error("[{}]的队列已满，无法接收DbExecution[{}]。", name, LinkFacade.toStr(dbExecution));
      }
    }

    private void run() {
      while (true) {
        DbExecution dbExecution = null;
        try {
          dbExecution = dbExecutionQueue.take();
          writeOfferFailedConfigDbExecution(dbExecution.getConnectionPortTrailId());
          write(dbExecution, true);
        } catch (InterruptedException e) {
          log.debug("[{}]关闭", dbExecutionConsumerThread.getName());
          Thread.currentThread().interrupt();
          break;
        } catch (Throwable t) {
          log.error("unexpected error: [{}].", LinkFacade.toStr(dbExecution), t);
          // 注意，这里不能break：消费某个dbExecution失败的时候，后续的还要继续消费。事实上，代码如果没有bug，这里永远走不到。
        }
      }
    }

    private void writeOfferFailedConfigDbExecution(String connectionPortTrailId) {
      DbExecution offerFailedConfigDbExecution;
      if ((offerFailedConfigDbExecution = offerFailedConfigDbExecutionMap.remove(connectionPortTrailId)) != null) {
        boolean write = write(offerFailedConfigDbExecution, true);
        if (!write) {
          offerFailedConfigDbExecutionMap.put(connectionPortTrailId, offerFailedConfigDbExecution);
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
      if (dbExecutionConsumerThread == null) {
        return;
      }
      dbExecutionConsumerThread.interrupt();
      if (dbExecutionQueue == null) {
        return;
      }
      DbExecution[] dbExecutions = dbExecutionQueue.toArray(new DbExecution[]{});
      if (dbExecutions.length > 0) {
        log.error("[{}]已关闭, 还有[{}]个dbExecution未被执行。", dbExecutionConsumerThread.getName(), dbExecutions.length);
        for (DbExecution dbExecution : dbExecutions) {
          write(dbExecution, false);
        }
      } else {
        log.info("[{}]已关闭, dbExecution都执行完成。", dbExecutionConsumerThread.getName(), dbExecutions.length);
      }
    }

    private boolean write(DbExecution dbExecution, boolean ifEnableRetry) {
      boolean ifWriteSuccess = true;
      for (SqlWriter sqlWriter : sqlWriterList) {
        try {
          if (DbExecution.DB_EXECUTION_TYPE_CONFIG.equals(dbExecution.getDbExecutionType())) {
            if (ifEnableRetry) {
              retrySend(sqlWriter::writeConfig, dbExecution, 10);
            } else {
              sqlWriter.writeConfig(dbExecution);
            }
          } else if (DbExecution.DB_EXECUTION_TYPE_SQL.equals(dbExecution.getDbExecutionType())) {
            if (ifEnableRetry) {
              retrySend(sqlWriter::writeSql, dbExecution, 2);
            } else {
              sqlWriter.writeSql(dbExecution);
            }
          } else if (DbExecution.DB_EXECUTION_TYPE_PREPARED_SQL.equals(dbExecution.getDbExecutionType())) {
            if (ifEnableRetry) {
              retrySend(sqlWriter::writePreparedSql, dbExecution, 2);
            } else {
              sqlWriter.writePreparedSql(dbExecution);
            }
          } else {
            log.error("[{}] dbExecutionType is not support. DbExecution is [{}].", sqlWriter, LinkFacade.toStr(dbExecution));
          }
        } catch (Exception t) {
          log.error("[{}] write error. DbExecution is [{}].", sqlWriter, LinkFacade.toStr(dbExecution), t);
          ifWriteSuccess = false;
        }
      }

      return ifWriteSuccess;
    }

  }

}
