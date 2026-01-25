package cn.addenda.porttrail.jdbc.bo;

import cn.addenda.porttrail.common.pojo.db.DbExecution;
import cn.addenda.porttrail.common.pojo.db.bo.AbstractSqlExecutionBo;
import cn.addenda.porttrail.infrastructure.entrypoint.EntryPointStackContext;
import cn.addenda.porttrail.infrastructure.writer.SqlWriter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 存一个Connection里的所有AbstractSqlBo
 */
public class AbstractSqlBoQueue {

  private final AtomicInteger orderGenerator = new AtomicInteger(1);
  private final AtomicInteger batchTmpOrderGenerator = new AtomicInteger(1);

  private final List<AbstractSqlExecutionBo> abstractSqlExecutionBoList = new ArrayList<>();

  private final SqlWriter sqlWriter;

  public AbstractSqlBoQueue(SqlWriter sqlWriter) {
    this.sqlWriter = sqlWriter;
  }

  private void write(AbstractSqlExecutionBo abstractSqlExecutionBo) {
    if (DbExecution.DB_EXECUTION_TYPE_SQL.equals(abstractSqlExecutionBo.getDbExecutionType())) {
      sqlWriter.writeSql(abstractSqlExecutionBo);
    } else if (DbExecution.DB_EXECUTION_TYPE_PREPARED_SQL.equals(abstractSqlExecutionBo.getDbExecutionType())) {
      sqlWriter.writePreparedSql(abstractSqlExecutionBo);
    } else if (DbExecution.DB_EXECUTION_TYPE_CONFIG.equals(abstractSqlExecutionBo.getDbExecutionType())) {
      sqlWriter.writeConfig(abstractSqlExecutionBo);
    } else {
      throw new UnsupportedOperationException();
    }
  }


  public void propagateCommitted() {
    synchronized (this) {
      Iterator<AbstractSqlExecutionBo> iterator = abstractSqlExecutionBoList.iterator();
      while (iterator.hasNext()) {
        AbstractSqlExecutionBo abstractSqlExecutionBo = iterator.next();
        abstractSqlExecutionBo.setSqlState(AbstractSqlExecutionBo.SQL_STATE_COMMITTED);
        iterator.remove();
        write(abstractSqlExecutionBo);
      }
    }
  }

  public void propagateRollback() {
    synchronized (this) {
      Iterator<AbstractSqlExecutionBo> iterator = abstractSqlExecutionBoList.iterator();
      while (iterator.hasNext()) {
        AbstractSqlExecutionBo abstractSqlExecutionBo = iterator.next();
        abstractSqlExecutionBo.setSqlState(AbstractSqlExecutionBo.SQL_STATE_ROLLBACK);
        iterator.remove();
        write(abstractSqlExecutionBo);
      }
    }
  }

  private void outputQuery(AbstractSqlExecutionBo abstractSqlExecutionBo) {
    synchronized (this) {
      abstractSqlExecutionBo.setSqlState(AbstractSqlExecutionBo.SQL_STATE_QUERY);
      write(abstractSqlExecutionBo);
    }
  }

  public void output(AbstractSqlExecutionBo abstractSqlExecutionBo) {
    if (abstractSqlExecutionBo.ifKeepAlive()) {
      return;
    }
    abstractSqlExecutionBo.setEntryPointSnapshot(EntryPointStackContext.snapshot());
    synchronized (this) {
      String sqlState = abstractSqlExecutionBo.getSqlState();
      if (AbstractSqlExecutionBo.SQL_STATE_NEW.equals(sqlState)) {
        abstractSqlExecutionBoList.add(abstractSqlExecutionBo);
      } else if (AbstractSqlExecutionBo.SQL_STATE_COMMITTED.equals(sqlState)) {
        abstractSqlExecutionBoList.add(abstractSqlExecutionBo);
        propagateCommitted();
      } else if (AbstractSqlExecutionBo.SQL_STATE_QUERY.equals(sqlState)) {
        outputQuery(abstractSqlExecutionBo);
      } else if (AbstractSqlExecutionBo.SQL_STATE_ROLLBACK.equals(sqlState)) {
        throw new UnsupportedOperationException(String.format("unsupported sqlState: %s.", abstractSqlExecutionBo));
      }
    }
  }

  public void reset() {
    Iterator<AbstractSqlExecutionBo> iterator = abstractSqlExecutionBoList.iterator();
    // 正常情况下，这段循环是进不去的
    while (iterator.hasNext()) {
      AbstractSqlExecutionBo abstractSqlExecutionBo = iterator.next();
      abstractSqlExecutionBo.setSqlState(AbstractSqlExecutionBo.SQL_STATE_UNKNOWN);
      write(abstractSqlExecutionBo);
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
