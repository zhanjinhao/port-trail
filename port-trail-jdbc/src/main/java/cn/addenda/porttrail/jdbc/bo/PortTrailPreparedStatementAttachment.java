package cn.addenda.porttrail.jdbc.bo;

import cn.addenda.porttrail.common.pojo.db.bo.StatementSql;
import cn.addenda.porttrail.common.pojo.db.bo.PreparedStatementExecutionBo;
import cn.addenda.porttrail.common.pojo.db.bo.PreparedStatementParameter;
import cn.addenda.porttrail.common.pojo.db.bo.SqlOrder;
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
 * 存储一个{@link PortTrailPreparedStatement}里执行的DbExecution。 <br/>
 * 由于{@link PreparedStatement}具有{@link Statement}的功能，所以{@link PortTrailPreparedStatementAttachment}需要具有{@link PortTrailStatementAttachment}的功能。
 */
public class PortTrailPreparedStatementAttachment extends PortTrailStatementAttachment {

  @Getter
  private final String parameterizedSql;
  private final PreparedStatementParameter preparedStatementParameter;
  protected PreparedStatementExecutionBo stashedPreparedStatementExecutionBo;

  public PortTrailPreparedStatementAttachment(String parameterizedSql, AbstractStatementExecutionBoQueue abstractStatementExecutionBoQueue,
                                              String dataSourcePortTrailId, String connectionPortTrailId, String statementPortTrailId) {
    super(abstractStatementExecutionBoQueue, dataSourcePortTrailId, connectionPortTrailId, statementPortTrailId);
    this.parameterizedSql = parameterizedSql;
    this.preparedStatementParameter = new PreparedStatementParameter();
  }

  public void executePreparedSql(String statementState, String txId, long start, long end) {
    synchronized (abstractStatementExecutionBoQueue) {
      PreparedStatementExecutionBo preparedStatementExecutionBo =
              new PreparedStatementExecutionBo(dataSourcePortTrailId, connectionPortTrailId, statementPortTrailId,
                      statementState, parameterizedSql, snapshotParameterForSingle(), txId, start, end);
      abstractStatementExecutionBoQueue.output(preparedStatementExecutionBo);
    }
  }

  @Override
  public void executeBatch(String statementState, String txId, long start, long end) {
    synchronized (abstractStatementExecutionBoQueue) {
      if (stashedStatementExecutionBo == null) {
        if (stashedPreparedStatementExecutionBo == null) {
          return;
        } else {
          for (PreparedStatementParameter wrapper : stashedPreparedStatementExecutionBo.getPreparedStatementParameterList()) {
            wrapper.setOrderInStatement(getNextOrder());
            wrapper.setOrderInConnection(abstractStatementExecutionBoQueue.getNextOrder());
          }
          output(statementState, txId, start, end);
        }
      } else {
        if (stashedPreparedStatementExecutionBo == null) {
          super.executeBatch(statementState, txId, start, end);
        } else {
          setOrder();
          super.executeBatch2(statementState, txId, start, end);
          output(statementState, txId, start, end);
        }
      }
    }
  }

  private void output(String statementState, String txId, long start, long end) {
    stashedPreparedStatementExecutionBo.setStatementState(statementState);
    stashedPreparedStatementExecutionBo.setDataSourcePortTrailId(dataSourcePortTrailId);
    stashedPreparedStatementExecutionBo.setConnectionPortTrailId(connectionPortTrailId);
    stashedPreparedStatementExecutionBo.setStatementPortTrailId(statementPortTrailId);
    stashedPreparedStatementExecutionBo.setTxId(txId);
    stashedPreparedStatementExecutionBo.setStart(start);
    stashedPreparedStatementExecutionBo.setEnd(end);
    abstractStatementExecutionBoQueue.output(stashedPreparedStatementExecutionBo);
    stashedPreparedStatementExecutionBo = null;
  }

  private void setOrder() {
    // stashStatementExecutionBo和stashedPreparedStatementExecutionBo的order是addBatch的顺序
    List<PreparedStatementParameter> preparedStatementParameterList = stashedPreparedStatementExecutionBo.getPreparedStatementParameterList();
    List<StatementSql> statementSqlList = stashedStatementExecutionBo.getStatementSqlList();
    List<SqlOrder> sqlOrderList = new ArrayList<>();
    sqlOrderList.addAll(preparedStatementParameterList);
    sqlOrderList.addAll(statementSqlList);

    // 处理orderInStatement
    sqlOrderList.sort(Comparator.comparing(SqlOrder::getOrderInStatement));
    for (SqlOrder sqlOrder : sqlOrderList) {
      sqlOrder.setOrderInStatement(getNextOrder());
    }
    // 处理orderInConnection
    sqlOrderList.sort(Comparator.comparing(SqlOrder::getOrderInConnection));
    for (SqlOrder sqlOrder : sqlOrderList) {
      sqlOrder.setOrderInConnection(abstractStatementExecutionBoQueue.getNextOrder());
    }
  }

  @Override
  public void clearBatch() {
    synchronized (abstractStatementExecutionBoQueue) {
      super.clearBatch();
      if (stashedPreparedStatementExecutionBo != null) {
        stashedPreparedStatementExecutionBo.clear();
        stashedPreparedStatementExecutionBo = null;
      }
    }
  }

  public void addBatchPreparedSql() {
    synchronized (abstractStatementExecutionBoQueue) {
      if (stashedPreparedStatementExecutionBo == null) {
        stashedPreparedStatementExecutionBo = new PreparedStatementExecutionBo(parameterizedSql);
      }
      stashedPreparedStatementExecutionBo.getPreparedStatementParameterList().add(snapshotParameterForBatch());
    }
  }

  /**
   * 有序性指的是：按照execute时的顺序
   */
  private PreparedStatementParameter snapshotParameterForBatch() {
    preparedStatementParameter.setOrderInStatement(getNextBatchTmpOrder());
    preparedStatementParameter.setOrderInConnection(abstractStatementExecutionBoQueue.getNextBatchTmpOrder());
    return preparedStatementParameter.deepClone();
  }

  /**
   * 有序性指的是：按照execute时的顺序
   */
  private PreparedStatementParameter snapshotParameterForSingle() {
    preparedStatementParameter.setOrderInStatement(getNextOrder());
    preparedStatementParameter.setOrderInConnection(abstractStatementExecutionBoQueue.getNextOrder());
    return preparedStatementParameter.deepClone();
  }

  public void set(int parameterIndex, String setMethod, Unary<?> unary) {
    synchronized (abstractStatementExecutionBoQueue) {
      preparedStatementParameter.set(parameterIndex - 1, setMethod, unary);
    }
  }

  public void set(int parameterIndex, String setMethod, Binary<?, ?> binary) {
    synchronized (abstractStatementExecutionBoQueue) {
      preparedStatementParameter.set(parameterIndex - 1, setMethod, binary);
    }
  }

  public void set(int parameterIndex, String setMethod, Ternary<?, ?, ?> ternary) {
    synchronized (abstractStatementExecutionBoQueue) {
      preparedStatementParameter.set(parameterIndex - 1, setMethod, ternary);
    }
  }

  public void clearPreparedStatementParameterWrapper() {
    synchronized (abstractStatementExecutionBoQueue) {
      preparedStatementParameter.clear();
    }
  }

}
