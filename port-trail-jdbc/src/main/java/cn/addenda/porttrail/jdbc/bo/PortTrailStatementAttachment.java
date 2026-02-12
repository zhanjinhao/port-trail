package cn.addenda.porttrail.jdbc.bo;

import cn.addenda.porttrail.common.pojo.db.bo.StatementExecutionBo;
import cn.addenda.porttrail.common.pojo.db.bo.StatementSql;
import cn.addenda.porttrail.jdbc.core.PortTrailStatement;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 存储一个{@link PortTrailStatement}里执行的DbExecution。
 */
public class PortTrailStatementAttachment {

  protected StatementExecutionBo stashedStatementExecutionBo;
  private final AtomicInteger orderGenerator = new AtomicInteger(1);
  private final AtomicInteger batchTmpOrderGenerator = new AtomicInteger(1);
  protected final AbstractStatementExecutionBoQueue abstractStatementExecutionBoQueue;
  protected final String dataSourcePortTrailId;
  protected final String connectionPortTrailId;
  protected final String statementPortTrailId;

  public PortTrailStatementAttachment(AbstractStatementExecutionBoQueue abstractStatementExecutionBoQueue,
                                      String dataSourcePortTrailId, String connectionPortTrailId, String statementPortTrailId) {
    this.abstractStatementExecutionBoQueue = abstractStatementExecutionBoQueue;
    this.dataSourcePortTrailId = dataSourcePortTrailId;
    this.connectionPortTrailId = connectionPortTrailId;
    this.statementPortTrailId = statementPortTrailId;
  }

  public void executeSql(String statementState,
                         String sql, String txId, long start, long end) {
    synchronized (abstractStatementExecutionBoQueue) {
      StatementExecutionBo statementExecutionBo = new StatementExecutionBo(dataSourcePortTrailId, connectionPortTrailId, statementPortTrailId,
              statementState, new StatementSql(sql, getNextOrder(), abstractStatementExecutionBoQueue.getNextOrder()), txId, start, end);
      abstractStatementExecutionBoQueue.output(statementExecutionBo);
    }
  }

  public void executeBatch(String statementState,
                           String txId, long start, long end) {
    synchronized (abstractStatementExecutionBoQueue) {
      if (stashedStatementExecutionBo != null) {
        for (StatementSql statementSql : stashedStatementExecutionBo.getStatementSqlList()) {
          statementSql.setOrderInStatement(getNextOrder());
          statementSql.setOrderInConnection(abstractStatementExecutionBoQueue.getNextOrder());
        }
        executeBatch2(statementState, txId, start, end);
      }
    }
  }

  protected void executeBatch2(String statementState,
                               String txId, long start, long end) {
    synchronized (abstractStatementExecutionBoQueue) {
      stashedStatementExecutionBo.setStatementState(statementState);
      stashedStatementExecutionBo.setDataSourcePortTrailId(dataSourcePortTrailId);
      stashedStatementExecutionBo.setConnectionPortTrailId(connectionPortTrailId);
      stashedStatementExecutionBo.setStatementPortTrailId(statementPortTrailId);
      stashedStatementExecutionBo.setTxId(txId);
      stashedStatementExecutionBo.setStart(start);
      stashedStatementExecutionBo.setEnd(end);
      abstractStatementExecutionBoQueue.output(stashedStatementExecutionBo);
      stashedStatementExecutionBo = null;
    }
  }

  public void clearBatch() {
    synchronized (abstractStatementExecutionBoQueue) {
      if (stashedStatementExecutionBo != null) {
        stashedStatementExecutionBo.clear();
        stashedStatementExecutionBo = null;
      }
    }
  }

  public void addBatchSql(String sql) {
    synchronized (abstractStatementExecutionBoQueue) {
      if (stashedStatementExecutionBo == null) {
        stashedStatementExecutionBo = new StatementExecutionBo();
      }
      stashedStatementExecutionBo.getStatementSqlList().add(new StatementSql(sql, getNextBatchTmpOrder(), abstractStatementExecutionBoQueue.getNextBatchTmpOrder()));
    }
  }

  protected int getNextOrder() {
    return orderGenerator.getAndIncrement();
  }

  protected int getNextBatchTmpOrder() {
    return batchTmpOrderGenerator.getAndIncrement();
  }

}
