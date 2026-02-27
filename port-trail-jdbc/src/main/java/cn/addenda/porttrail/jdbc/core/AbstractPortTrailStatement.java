package cn.addenda.porttrail.jdbc.core;


import cn.addenda.porttrail.common.util.UuidUtils;
import cn.addenda.porttrail.common.pojo.db.bo.AbstractStatementExecutionBo;
import cn.addenda.porttrail.jdbc.bo.PortTrailStatementAttachment;
import cn.addenda.porttrail.jdbc.bo.AbstractStatementExecutionBoQueue;
import cn.addenda.porttrail.common.util.SqlUtils;

import java.sql.*;

/**
 * @author addenda
 * @since 2024/8/24 20:41
 */
public abstract class AbstractPortTrailStatement<T extends Statement, P extends PortTrailStatementAttachment>
        extends WrapperAdapter implements Statement, PortTrailIded {

  protected final T delegate;

  private final PortTrailConnection portTrailConnection;

  protected P portTrailStatementSqlAttachment;

  private final String portTrailId;

  protected AbstractPortTrailStatement(T delegate, PortTrailConnection portTrailConnection) {
    this.delegate = delegate;
    this.portTrailConnection = portTrailConnection;
    this.portTrailId = UuidUtils.generateUuid();
  }

  @Override
  public int getMaxFieldSize() throws SQLException {
    return delegate.getMaxFieldSize();
  }

  @Override
  public void setMaxFieldSize(int max) throws SQLException {
    delegate.setMaxFieldSize(max);
  }

  @Override
  public int getMaxRows() throws SQLException {
    return delegate.getMaxRows();
  }

  @Override
  public void setMaxRows(int max) throws SQLException {
    delegate.setMaxRows(max);
  }

  @Override
  public void setEscapeProcessing(boolean enable) throws SQLException {
    delegate.setEscapeProcessing(enable);
  }

  @Override
  public int getQueryTimeout() throws SQLException {
    return delegate.getQueryTimeout();
  }

  @Override
  public void setQueryTimeout(int seconds) throws SQLException {
    delegate.setQueryTimeout(seconds);
  }

  @Override
  public void cancel() throws SQLException {
    delegate.cancel();
  }

  @Override
  public SQLWarning getWarnings() throws SQLException {
    return delegate.getWarnings();
  }

  @Override
  public void clearWarnings() throws SQLException {
    delegate.clearWarnings();
  }

  @Override
  public void setCursorName(String name) throws SQLException {
    delegate.setCursorName(name);
  }

  @Override
  public ResultSet getResultSet() throws SQLException {
    return delegate.getResultSet();
  }

  @Override
  public int getUpdateCount() throws SQLException {
    return delegate.getUpdateCount();
  }

  @Override
  public boolean getMoreResults() throws SQLException {
    return delegate.getMoreResults();
  }

  @Override
  public void setFetchDirection(int direction) throws SQLException {
    delegate.setFetchDirection(direction);
  }

  @Override
  public int getFetchDirection() throws SQLException {
    return delegate.getFetchDirection();
  }

  @Override
  public void setFetchSize(int rows) throws SQLException {
    delegate.setFetchSize(rows);
  }

  @Override
  public int getFetchSize() throws SQLException {
    return delegate.getFetchSize();
  }

  @Override
  public int getResultSetConcurrency() throws SQLException {
    return delegate.getResultSetConcurrency();
  }

  @Override
  public int getResultSetType() throws SQLException {
    return delegate.getResultSetType();
  }

  @Override
  public boolean getMoreResults(int current) throws SQLException {
    return delegate.getMoreResults(current);
  }

  @Override
  public ResultSet getGeneratedKeys() throws SQLException {
    return delegate.getGeneratedKeys();
  }

  @Override
  public int getResultSetHoldability() throws SQLException {
    return delegate.getResultSetHoldability();
  }

  @Override
  public boolean isClosed() throws SQLException {
    return delegate.isClosed();
  }

  @Override
  public void setPoolable(boolean poolable) throws SQLException {
    delegate.setPoolable(poolable);
  }

  @Override
  public boolean isPoolable() throws SQLException {
    return delegate.isPoolable();
  }

  @Override
  public void closeOnCompletion() throws SQLException {
    delegate.closeOnCompletion();
  }

  @Override
  public boolean isCloseOnCompletion() throws SQLException {
    return delegate.isCloseOnCompletion();
  }

  @Override
  public long getLargeUpdateCount() throws SQLException {
    return delegate.getLargeUpdateCount();
  }

  @Override
  public void setLargeMaxRows(long max) throws SQLException {
    delegate.setLargeMaxRows(max);
  }

  @Override
  public long getLargeMaxRows() throws SQLException {
    return delegate.getLargeMaxRows();
  }

  public void newTxIdIfAutoCommit() {
    if (getIfAutoCommit()) {
      getPortTrailConnection().newTxId();
    }
  }

  protected String getInitialStatementStateByIfAutoCommit() {
    return getIfAutoCommit() ? AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED : AbstractStatementExecutionBo.STATEMENT_STATE_NEW;
  }

  protected boolean getIfAutoCommit() {
    return getPortTrailConnection().isIfAutoCommit();
  }

  protected String getTxId() {
    return getPortTrailConnection().getTxId();
  }

  // -----------------
  //   获取Connection
  // -----------------

  public PortTrailConnection getPortTrailConnection() {
    return portTrailConnection;
  }

  @Override
  public Connection getConnection() throws SQLException {
    return getPortTrailConnection().getConnection();
  }

  // -----------------
  //   PortTrailIded
  // -----------------

  @Override
  public String getPortTrailId() {
    return portTrailId;
  }

  protected long curMills() {
    return System.currentTimeMillis();
  }

  /**
   * batch update
   */
  protected void executeBatchUpdate(long start) {
    String statementState = getInitialStatementStateByIfAutoCommit();
    portTrailStatementSqlAttachment.executeBatch(statementState, getTxId(), start, curMills());
    newTxIdIfAutoCommit();
  }

  protected void execute(String sql, long start) {
    if (SqlUtils.ifQuerySql(sql)) {
      executeQuery(sql, start);
    } else {
      executeUpdate(sql, start);
    }
  }

  protected void executeUpdate(String sql, long start) {
    String statementState = getInitialStatementStateByIfAutoCommit();
    portTrailStatementSqlAttachment.executeSql(statementState, sql, getTxId(), start, curMills());
    newTxIdIfAutoCommit();
  }

  protected void executeQuery(String sql, long start) {
    portTrailStatementSqlAttachment.executeSql(AbstractStatementExecutionBo.STATEMENT_STATE_QUERY, sql, getTxId(), start, curMills());
    newTxIdIfAutoCommit();
  }

  protected String getDataSourcePortTrailId() {
    if (portTrailConnection instanceof PortTrailDataSourceConnection) {
      return ((PortTrailDataSourceConnection) portTrailConnection).getPortTrailDataSource().getPortTrailId();
    }
    return null;
  }

  protected String getConnectionPortTrailId() {
    return portTrailConnection.getPortTrailId();
  }

  protected AbstractStatementExecutionBoQueue getExecutionQueue() {
    return portTrailConnection.getAbstractStatementExecutionBoQueue();
  }

}
