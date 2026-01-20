package cn.addenda.porttrail.jdbc.bo;

import cn.addenda.porttrail.jdbc.core.PortTrailStatement;
import cn.addenda.porttrail.common.pojo.db.SqlWrapper;
import cn.addenda.porttrail.common.pojo.db.bo.SqlExecutionBo;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 存储一个{@link PortTrailStatement}里执行的SQL。
 */
public class PortTrailStatementAttachment {

  protected SqlExecutionBo stashedSqlBo;
  private final AtomicInteger orderGenerator = new AtomicInteger(1);
  private final AtomicInteger batchTmpOrderGenerator = new AtomicInteger(1);
  protected final AbstractSqlBoQueue abstractSqlBoQueue;
  protected final String dataSourcePortTrailId;
  protected final String connectionPortTrailId;
  protected final String statementPortTrailId;

  public PortTrailStatementAttachment(AbstractSqlBoQueue abstractSqlBoQueue,
                                      String dataSourcePortTrailId, String connectionPortTrailId, String statementPortTrailId) {
    this.abstractSqlBoQueue = abstractSqlBoQueue;
    this.dataSourcePortTrailId = dataSourcePortTrailId;
    this.connectionPortTrailId = connectionPortTrailId;
    this.statementPortTrailId = statementPortTrailId;
  }

  public void executeSql(String sqlState,
                         String sql, String txId, long start, long end) {
    synchronized (abstractSqlBoQueue) {
      SqlExecutionBo sqlBo = new SqlExecutionBo(dataSourcePortTrailId, connectionPortTrailId, statementPortTrailId,
              sqlState, SqlWrapper.of(sql, getNextOrder(), abstractSqlBoQueue.getNextOrder()), txId, start, end);
      abstractSqlBoQueue.output(sqlBo);
    }
  }

  public void executeBatch(String sqlState,
                           String txId, long start, long end) {
    synchronized (abstractSqlBoQueue) {
      if (stashedSqlBo != null) {
        for (SqlWrapper sqlWrapper : stashedSqlBo.getSqlWrapperList()) {
          sqlWrapper.setOrderInStatement(getNextOrder());
          sqlWrapper.setOrderInConnection(abstractSqlBoQueue.getNextOrder());
        }
        executeBatch2(sqlState, txId, start, end);
      }
    }
  }

  protected void executeBatch2(String sqlState,
                               String txId, long start, long end) {
    synchronized (abstractSqlBoQueue) {
      stashedSqlBo.setSqlState(sqlState);
      stashedSqlBo.setDataSourcePortTrailId(dataSourcePortTrailId);
      stashedSqlBo.setConnectionPortTrailId(connectionPortTrailId);
      stashedSqlBo.setStatementPortTrailId(statementPortTrailId);
      stashedSqlBo.setTxId(txId);
      stashedSqlBo.setStart(start);
      stashedSqlBo.setEnd(end);
      abstractSqlBoQueue.output(stashedSqlBo);
      stashedSqlBo = null;
    }
  }

  public void clearBatch() {
    synchronized (abstractSqlBoQueue) {
      if (stashedSqlBo != null) {
        stashedSqlBo.clear();
        stashedSqlBo = null;
      }
    }
  }

  public void addBatchSql(String sql) {
    synchronized (abstractSqlBoQueue) {
      if (stashedSqlBo == null) {
        stashedSqlBo = new SqlExecutionBo();
      }
      stashedSqlBo.getSqlWrapperList().add(SqlWrapper.of(sql, getNextBatchTmpOrder(), abstractSqlBoQueue.getNextBatchTmpOrder()));
    }
  }

  protected int getNextOrder() {
    return orderGenerator.getAndIncrement();
  }

  protected int getNextBatchTmpOrder() {
    return batchTmpOrderGenerator.getAndIncrement();
  }

}
