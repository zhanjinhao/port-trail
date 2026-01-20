package cn.addenda.porttrail.jdbc.bo;

import cn.addenda.porttrail.common.pojo.db.SqlWrapper;
import cn.addenda.porttrail.common.pojo.db.bo.PreparedSqlExecutionBo;
import cn.addenda.porttrail.common.pojo.db.PreparedStatementParameterWrapper;
import cn.addenda.porttrail.common.pojo.db.SqlOrder;
import cn.addenda.porttrail.common.tuple.Binary;
import cn.addenda.porttrail.common.tuple.Ternary;
import cn.addenda.porttrail.common.tuple.Unary;
import cn.addenda.porttrail.jdbc.core.PortTrailPreparedStatement;
import lombok.Getter;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 存储一个{@link PortTrailPreparedStatement}里执行的SQL。 <br/>
 * 由于{@link PreparedStatement}具有{@link Statement}的功能，所以{@link PortTrailPreparedStatementAttachment}需要具有{@link PortTrailStatementAttachment}的功能。
 */
public class PortTrailPreparedStatementAttachment extends PortTrailStatementAttachment {

  @Getter
  private final String parameterizedSql;
  private final PreparedStatementParameterWrapper preparedStatementParameterWrapper;
  protected PreparedSqlExecutionBo stashedPreparedSqlBo;

  public PortTrailPreparedStatementAttachment(String parameterizedSql, AbstractSqlBoQueue abstractSqlBoQueue,
                                              String dataSourcePortTrailId, String connectionPortTrailId, String statementPortTrailId) {
    super(abstractSqlBoQueue, dataSourcePortTrailId, connectionPortTrailId, statementPortTrailId);
    this.parameterizedSql = parameterizedSql;
    this.preparedStatementParameterWrapper = new PreparedStatementParameterWrapper();
  }

  public void executePreparedSql(String sqlState, String txId, long start, long end) {
    synchronized (abstractSqlBoQueue) {
      PreparedSqlExecutionBo preparedSqlBo =
              new PreparedSqlExecutionBo(dataSourcePortTrailId, connectionPortTrailId, statementPortTrailId,
                      sqlState, parameterizedSql, snapshotParameterForSingle(), txId, start, end);
      abstractSqlBoQueue.output(preparedSqlBo);
    }
  }

  @Override
  public void executeBatch(String sqlState, String txId, long start, long end) {
    synchronized (abstractSqlBoQueue) {
      if (stashedSqlBo == null) {
        if (stashedPreparedSqlBo == null) {
          return;
        } else {
          for (PreparedStatementParameterWrapper wrapper : stashedPreparedSqlBo.getPreparedStatementParameterWrapperList()) {
            wrapper.setOrderInStatement(getNextOrder());
            wrapper.setOrderInConnection(abstractSqlBoQueue.getNextOrder());
          }
          output(sqlState, txId, start, end);
        }
      } else {
        if (stashedPreparedSqlBo == null) {
          super.executeBatch(sqlState, txId, start, end);
        } else {
          setOrder();
          super.executeBatch2(sqlState, txId, start, end);
          output(sqlState, txId, start, end);
        }
      }
    }
  }

  private void output(String sqlState, String txId, long start, long end) {
    stashedPreparedSqlBo.setSqlState(sqlState);
    stashedPreparedSqlBo.setDataSourcePortTrailId(dataSourcePortTrailId);
    stashedPreparedSqlBo.setConnectionPortTrailId(connectionPortTrailId);
    stashedPreparedSqlBo.setStatementPortTrailId(statementPortTrailId);
    stashedPreparedSqlBo.setTxId(txId);
    stashedPreparedSqlBo.setStart(start);
    stashedPreparedSqlBo.setEnd(end);
    abstractSqlBoQueue.output(stashedPreparedSqlBo);
    stashedPreparedSqlBo = null;
  }

  private void setOrder() {
    // stashSqlBo和stashedPreparedSqlBo的order是addBatch的顺序
    List<PreparedStatementParameterWrapper> wrapperList = stashedPreparedSqlBo.getPreparedStatementParameterWrapperList();
    List<SqlWrapper> sqlWrapperList = stashedSqlBo.getSqlWrapperList();
    List<SqlOrder> sqlOrderList = new ArrayList<>();
    sqlOrderList.addAll(wrapperList);
    sqlOrderList.addAll(sqlWrapperList);

    // 处理orderInStatement
    sqlOrderList.sort(Comparator.comparing(SqlOrder::getOrderInStatement));
    for (SqlOrder sqlOrder : sqlOrderList) {
      sqlOrder.setOrderInStatement(getNextOrder());
    }
    // 处理orderInConnection
    sqlOrderList.sort(Comparator.comparing(SqlOrder::getOrderInConnection));
    for (SqlOrder sqlOrder : sqlOrderList) {
      sqlOrder.setOrderInConnection(abstractSqlBoQueue.getNextOrder());
    }
  }

  @Override
  public void clearBatch() {
    synchronized (abstractSqlBoQueue) {
      super.clearBatch();
      if (stashedPreparedSqlBo != null) {
        stashedPreparedSqlBo.clear();
        stashedPreparedSqlBo = null;
      }
    }
  }

  public void addBatchPreparedSql() {
    synchronized (abstractSqlBoQueue) {
      if (stashedPreparedSqlBo == null) {
        stashedPreparedSqlBo = new PreparedSqlExecutionBo(parameterizedSql);
      }
      stashedPreparedSqlBo.getPreparedStatementParameterWrapperList().add(snapshotParameterForBatch());
    }
  }

  /**
   * 有序性指的是：按照execute时的顺序
   */
  private PreparedStatementParameterWrapper snapshotParameterForBatch() {
    preparedStatementParameterWrapper.setOrderInStatement(getNextBatchTmpOrder());
    preparedStatementParameterWrapper.setOrderInConnection(abstractSqlBoQueue.getNextBatchTmpOrder());
    return preparedStatementParameterWrapper.deepClone();
  }

  /**
   * 有序性指的是：按照execute时的顺序
   */
  private PreparedStatementParameterWrapper snapshotParameterForSingle() {
    preparedStatementParameterWrapper.setOrderInStatement(getNextOrder());
    preparedStatementParameterWrapper.setOrderInConnection(abstractSqlBoQueue.getNextOrder());
    return preparedStatementParameterWrapper.deepClone();
  }

  public void set(int parameterIndex, String setMethod, Unary<?> unary) {
    synchronized (abstractSqlBoQueue) {
      preparedStatementParameterWrapper.set(parameterIndex - 1, setMethod, unary);
    }
  }

  public void set(int parameterIndex, String setMethod, Binary<?, ?> binary) {
    synchronized (abstractSqlBoQueue) {
      preparedStatementParameterWrapper.set(parameterIndex - 1, setMethod, binary);
    }
  }

  public void set(int parameterIndex, String setMethod, Ternary<?, ?, ?> ternary) {
    synchronized (abstractSqlBoQueue) {
      preparedStatementParameterWrapper.set(parameterIndex - 1, setMethod, ternary);
    }
  }

  public void clearPreparedStatementParameterWrapper() {
    synchronized (abstractSqlBoQueue) {
      preparedStatementParameterWrapper.clear();
    }
  }

}
