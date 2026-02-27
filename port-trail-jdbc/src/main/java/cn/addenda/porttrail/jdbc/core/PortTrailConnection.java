package cn.addenda.porttrail.jdbc.core;

import cn.addenda.porttrail.common.util.UuidUtils;
import cn.addenda.porttrail.infrastructure.log.PortTrailLogger;
import cn.addenda.porttrail.infrastructure.tx.TxContext;
import cn.addenda.porttrail.infrastructure.writer.DbWriter;
import cn.addenda.porttrail.jdbc.bo.AbstractStatementExecutionBoQueue;
import lombok.Getter;

import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author addenda
 * @since 2024/8/24 17:03
 */
public class PortTrailConnection extends AbstractPortTrailConnection implements Connection, PortTrailIded {

  private final String portTrailId;

  @Getter
  private String txId;

  private final PortTrailLogger portTrailLogger;

  private final DbWriter dbWriter;

  @Getter
  private AbstractStatementExecutionBoQueue abstractStatementExecutionBoQueue;

  @Getter
  private boolean ifAutoCommit;

  public PortTrailConnection(Connection connection, PortTrailLogger portTrailLogger, DbWriter dbWriter) throws SQLException {
    super(connection);
    this.portTrailId = UuidUtils.generateUuid();
    newTxId();
    this.portTrailLogger = portTrailLogger;
    this.dbWriter = dbWriter;
    this.abstractStatementExecutionBoQueue = new AbstractStatementExecutionBoQueue(dbWriter);
    this.ifAutoCommit = connection.getAutoCommit();
  }

  public void reset() throws SQLException {
    newTxId();
//    this.portTrailId = UuidUtils.generateUuid();
    this.abstractStatementExecutionBoQueue.reset();
    this.ifAutoCommit = connection.getAutoCommit();
    closePortTrail();
  }

  public void newTxId() {
    this.txId = UuidUtils.generateUuid();
    TxContext.setTxId(txId);
  }

  @Override
  public void setAutoCommit(boolean autoCommit) throws SQLException {
    connection.setAutoCommit(autoCommit);
    // true -> false: execute的数据都提交完成了
    // false -> true: 提交execute但还未提交的数据
    if (ifAutoCommit != autoCommit) {
      if (autoCommit) {
        abstractStatementExecutionBoQueue.propagateCommitted();
      }
    }
    // true -> true
    // false -> false
    else {
      // no-op
    }
    newTxId();
    ifAutoCommit = autoCommit;
  }

  @Override
  public boolean getAutoCommit() throws SQLException {
    ifAutoCommit = connection.getAutoCommit();
    return ifAutoCommit;
  }

  @Override
  public void commit() throws SQLException {
    connection.commit();
    newTxId();
    abstractStatementExecutionBoQueue.propagateCommitted();
  }

  @Override
  public void rollback() throws SQLException {
    connection.rollback();
    newTxId();
    abstractStatementExecutionBoQueue.propagateRollback();
  }

  @Override
  public void close() throws SQLException {
    connection.close();
    closePortTrail();
  }

  @Override
  public Statement createStatement() throws SQLException {
    Statement statement = connection.createStatement();
    PortTrailStatement portTrailStatement = new PortTrailStatement(statement, this);

    addPortTrailStatement(portTrailStatement);

    return portTrailStatement;
  }

  @Override
  public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
    Statement statement = connection.createStatement(resultSetType, resultSetConcurrency);
    PortTrailStatement portTrailStatement = new PortTrailStatement(statement, this);

    addPortTrailStatement(portTrailStatement);

    return portTrailStatement;
  }

  @Override
  public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
    Statement statement = connection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    PortTrailStatement portTrailStatement = new PortTrailStatement(statement, this);

    addPortTrailStatement(portTrailStatement);

    return portTrailStatement;
  }

  @Override
  public PreparedStatement prepareStatement(String sql) throws SQLException {
    PreparedStatement ps = connection.prepareStatement(sql);
    PortTrailPreparedStatement portTrailPreparedStatement = new PortTrailPreparedStatement(ps, this, sql);

    addPortTrailPreparedStatement(portTrailPreparedStatement);

    return portTrailPreparedStatement;
  }

  @Override
  public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
    PreparedStatement ps = connection.prepareStatement(sql, resultSetType, resultSetConcurrency);
    PortTrailPreparedStatement portTrailPreparedStatement = new PortTrailPreparedStatement(ps, this, sql);

    addPortTrailPreparedStatement(portTrailPreparedStatement);

    return portTrailPreparedStatement;
  }

  @Override
  public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
    PreparedStatement ps = connection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    PortTrailPreparedStatement portTrailPreparedStatement = new PortTrailPreparedStatement(ps, this, sql);

    addPortTrailPreparedStatement(portTrailPreparedStatement);

    return portTrailPreparedStatement;
  }

  @Override
  public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
    PreparedStatement ps = connection.prepareStatement(sql, autoGeneratedKeys);
    PortTrailPreparedStatement portTrailPreparedStatement = new PortTrailPreparedStatement(ps, this, sql);

    addPortTrailPreparedStatement(portTrailPreparedStatement);

    return portTrailPreparedStatement;
  }

  @Override
  public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
    PreparedStatement ps = connection.prepareStatement(sql, columnIndexes);
    PortTrailPreparedStatement portTrailPreparedStatement = new PortTrailPreparedStatement(ps, this, sql);

    addPortTrailPreparedStatement(portTrailPreparedStatement);

    return portTrailPreparedStatement;
  }

  @Override
  public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
    PreparedStatement ps = connection.prepareStatement(sql, columnNames);
    PortTrailPreparedStatement portTrailPreparedStatement = new PortTrailPreparedStatement(ps, this, sql);

    addPortTrailPreparedStatement(portTrailPreparedStatement);

    return portTrailPreparedStatement;
  }

  // --------------------------------------------------------------
  //  不支持存储过程，因为存储过程隐藏了具体的sql，无法解析出受影响的表和字段。
  // --------------------------------------------------------------

  @Override
  public CallableStatement prepareCall(String sql) throws SQLException {
    // todo 暂不支持存储过程
    return connection.prepareCall(sql);
  }

  @Override
  public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
    // todo 暂不支持存储过程
    return connection.prepareCall(sql, resultSetType, resultSetConcurrency);
  }

  @Override
  public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
    // todo 暂不支持存储过程
    return connection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
  }

  @Override
  public Savepoint setSavepoint() throws SQLException {
    // todo 不知道怎么处理
    return connection.setSavepoint();
  }

  @Override
  public Savepoint setSavepoint(String name) throws SQLException {
    // todo 不知道怎么处理
    return connection.setSavepoint(name);
  }

  @Override
  public void rollback(Savepoint savepoint) throws SQLException {
    // todo 不知道怎么处理
    connection.rollback(savepoint);
  }

  @Override
  public void releaseSavepoint(Savepoint savepoint) throws SQLException {
    // todo 不知道怎么处理
    connection.releaseSavepoint(savepoint);
  }

  // -----------------
  //   PortTrailIded
  // -----------------

  @Override
  public String getPortTrailId() {
    return portTrailId;
  }

  // ------------------------------------------------------------
  //   portTrailPreparedStatement&portTrailStatement Management
  // ------------------------------------------------------------

  @Override
  public void closePortTrail() throws SQLException {
    synchronized (abstractStatementExecutionBoQueue) {
      // 按照先申请资源后释放的步骤，在Connection关闭的时候，其创造的Statement和PreparedStatement一定都关闭完成了。
      // 但是，为了在遇到异常步骤时尽可能减少内存泄漏，在close这里还是释放一下
      closeAllPortTrailStatement();
      closeAllPortTrailPreparedStatement();
    }
  }

  private final Map<String, PortTrailPreparedStatement> portTrailPreparedStatementMap = new HashMap<>();

  private void addPortTrailPreparedStatement(PortTrailPreparedStatement portTrailPreparedStatement) {
    synchronized (abstractStatementExecutionBoQueue) {
      portTrailPreparedStatementMap.put(portTrailPreparedStatement.getPortTrailId(), portTrailPreparedStatement);
    }
  }

  public void removePortTrailStatement(PortTrailPreparedStatement portTrailPreparedStatement) {
    synchronized (abstractStatementExecutionBoQueue) {
      portTrailPreparedStatementMap.remove(portTrailPreparedStatement.getPortTrailId());
    }
  }

  private void closeAllPortTrailPreparedStatement() throws SQLException {
    synchronized (abstractStatementExecutionBoQueue) {
      Set<Map.Entry<String, PortTrailPreparedStatement>> entries = new HashSet<>(portTrailPreparedStatementMap.entrySet());
      for (Map.Entry<String, PortTrailPreparedStatement> entry : entries) {
        PortTrailPreparedStatement portTrailPreparedStatement = entry.getValue();
        try {
          // close()方法执行的时候，会执行closePortTrail()方法
          portTrailPreparedStatement.closePortTrail();
        } catch (SQLException e) {
          portTrailLogger.error("exception occurred when {} close, {}.", PortTrailPreparedStatement.class, portTrailPreparedStatement, e);
          throw e;
        }
      }
    }
  }

  private final Map<String, PortTrailStatement> portTrailStatementMap = new HashMap<>();

  private void addPortTrailStatement(PortTrailStatement portTrailStatement) {
    synchronized (abstractStatementExecutionBoQueue) {
      portTrailStatementMap.put(portTrailStatement.getPortTrailId(), portTrailStatement);
    }
  }

  public void removePortTrailStatement(PortTrailStatement portTrailStatement) {
    synchronized (abstractStatementExecutionBoQueue) {
      portTrailStatementMap.remove(portTrailStatement.getPortTrailId());
    }
  }

  private void closeAllPortTrailStatement() throws SQLException {
    synchronized (abstractStatementExecutionBoQueue) {
      Set<Map.Entry<String, PortTrailStatement>> entries = new HashSet<>(portTrailStatementMap.entrySet());
      for (Map.Entry<String, PortTrailStatement> entry : entries) {
        PortTrailStatement portTrailStatement = entry.getValue();
        try {
          // close()方法执行的时候，会执行closePortTrail()方法
          portTrailStatement.closePortTrail();
        } catch (SQLException e) {
          portTrailLogger.error("exception occurred when {} close, {}.", PortTrailStatement.class, portTrailStatement, e);
          throw e;
        }
      }
    }
  }

  // -----------
  //   基础方法
  // -----------

  @Override
  public String toString() {
    return "PortTrailConnection{" +
            "portTrailId='" + portTrailId + '\'' +
            "} " + super.toString();
  }

}
