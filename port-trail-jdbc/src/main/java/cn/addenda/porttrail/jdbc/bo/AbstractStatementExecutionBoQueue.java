package cn.addenda.porttrail.jdbc.bo;

import cn.addenda.porttrail.common.pojo.db.bo.DbExecution;
import cn.addenda.porttrail.common.pojo.db.bo.AbstractStatementExecutionBo;
import cn.addenda.porttrail.infrastructure.entrypoint.EntryPointStackContext;
import cn.addenda.porttrail.infrastructure.writer.DbWriter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 存一个Connection里的所有AbstractAbstractStatementExecutionBoBo
 */
public class AbstractStatementExecutionBoQueue {

  private final AtomicInteger orderGenerator = new AtomicInteger(1);
  private final AtomicInteger batchTmpOrderGenerator = new AtomicInteger(1);

  private final List<AbstractStatementExecutionBo> abstractStatementExecutionBoList = new ArrayList<>();

  private final DbWriter dbWriter;

  public AbstractStatementExecutionBoQueue(DbWriter dbWriter) {
    this.dbWriter = dbWriter;
  }

  private void write(AbstractStatementExecutionBo abstractStatementExecutionBo) {
    if (DbExecution.DB_EXECUTION_TYPE_STATEMENT.equals(abstractStatementExecutionBo.getDbExecutionType())) {
      dbWriter.writeStatement(abstractStatementExecutionBo);
    } else if (DbExecution.DB_EXECUTION_TYPE_PREPARED_STATEMENT.equals(abstractStatementExecutionBo.getDbExecutionType())) {
      dbWriter.writePreparedStatement(abstractStatementExecutionBo);
    } else if (DbExecution.DB_EXECUTION_TYPE_DB_CONFIG.equals(abstractStatementExecutionBo.getDbExecutionType())) {
      dbWriter.writeDbConfig(abstractStatementExecutionBo);
    } else {
      throw new UnsupportedOperationException();
    }
  }


  public void propagateCommitted() {
    synchronized (this) {
      Iterator<AbstractStatementExecutionBo> iterator = abstractStatementExecutionBoList.iterator();
      while (iterator.hasNext()) {
        AbstractStatementExecutionBo abstractStatementExecutionBo = iterator.next();
        abstractStatementExecutionBo.setStatementState(AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED);
        iterator.remove();
        write(abstractStatementExecutionBo);
      }
    }
  }

  public void propagateRollback() {
    synchronized (this) {
      Iterator<AbstractStatementExecutionBo> iterator = abstractStatementExecutionBoList.iterator();
      while (iterator.hasNext()) {
        AbstractStatementExecutionBo abstractStatementExecutionBo = iterator.next();
        abstractStatementExecutionBo.setStatementState(AbstractStatementExecutionBo.STATEMENT_STATE_ROLLBACK);
        iterator.remove();
        write(abstractStatementExecutionBo);
      }
    }
  }

  private void outputQuery(AbstractStatementExecutionBo abstractStatementExecutionBo) {
    synchronized (this) {
      abstractStatementExecutionBo.setStatementState(AbstractStatementExecutionBo.STATEMENT_STATE_QUERY);
      write(abstractStatementExecutionBo);
    }
  }

  public void output(AbstractStatementExecutionBo abstractStatementExecutionBo) {
    if (abstractStatementExecutionBo.ifKeepAlive()) {
      return;
    }
    abstractStatementExecutionBo.setEntryPointSnapshot(EntryPointStackContext.snapshot());
    synchronized (this) {
      String statementState = abstractStatementExecutionBo.getStatementState();
      if (AbstractStatementExecutionBo.STATEMENT_STATE_NEW.equals(statementState)) {
        abstractStatementExecutionBoList.add(abstractStatementExecutionBo);
      } else if (AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED.equals(statementState)) {
        abstractStatementExecutionBoList.add(abstractStatementExecutionBo);
        propagateCommitted();
      } else if (AbstractStatementExecutionBo.STATEMENT_STATE_QUERY.equals(statementState)) {
        outputQuery(abstractStatementExecutionBo);
      } else if (AbstractStatementExecutionBo.STATEMENT_STATE_ROLLBACK.equals(statementState)) {
        throw new UnsupportedOperationException(String.format("unsupported statementState: %s.", abstractStatementExecutionBo));
      }
    }
  }

  public void reset() {
    Iterator<AbstractStatementExecutionBo> iterator = abstractStatementExecutionBoList.iterator();
    // 正常情况下，这段循环是进不去的
    while (iterator.hasNext()) {
      AbstractStatementExecutionBo abstractStatementExecutionBo = iterator.next();
      abstractStatementExecutionBo.setStatementState(AbstractStatementExecutionBo.STATEMENT_STATE_UNKNOWN);
      write(abstractStatementExecutionBo);
      iterator.remove();
    }
  }

  public int getNextOrder() {
    return orderGenerator.getAndIncrement();
  }

  public int getNextBatchTmpOrder() {
    return batchTmpOrderGenerator.getAndIncrement();
  }

}
