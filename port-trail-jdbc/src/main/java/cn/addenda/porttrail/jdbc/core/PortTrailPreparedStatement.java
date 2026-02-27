package cn.addenda.porttrail.jdbc.core;

import cn.addenda.porttrail.common.pojo.db.bo.AbstractStatementExecutionBo;
import cn.addenda.porttrail.common.tuple.Binary;
import cn.addenda.porttrail.common.tuple.Ternary;
import cn.addenda.porttrail.common.tuple.Unary;
import cn.addenda.porttrail.jdbc.bo.PortTrailPreparedStatementAttachment;
import cn.addenda.porttrail.common.util.SqlUtils;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;

import static cn.addenda.porttrail.common.pojo.db.bo.PreparedStatementParameter.UN_SUPPORTED_PARAMETER;

/**
 * @author addenda
 * @since 2024/4/13 12:00
 */
public class PortTrailPreparedStatement
        extends AbstractPortTrailStatement<PreparedStatement, PortTrailPreparedStatementAttachment>
        implements PreparedStatement {


  public PortTrailPreparedStatement(PreparedStatement delegate, PortTrailConnection portTrailConnection, String parameterizedSql) {
    super(delegate, portTrailConnection);
    this.portTrailStatementSqlAttachment = new PortTrailPreparedStatementAttachment(parameterizedSql, getExecutionQueue(), getDataSourcePortTrailId(), getConnectionPortTrailId(), getPortTrailId());
  }

  @Override
  public ResultSet executeQuery() throws SQLException {
    long start = curMills();
    ResultSet resultSet = delegate.executeQuery();
    executeQuery(start);
    return resultSet;
  }

  @Override
  public int executeUpdate() throws SQLException {
    long start = curMills();
    int r = delegate.executeUpdate();
    executeUpdate(start);
    return r;
  }

  @Override
  public boolean execute() throws SQLException {
    long start = curMills();
    boolean r = delegate.execute();
    execute(start);
    return r;
  }

  @Override
  public long executeLargeUpdate() throws SQLException {
    long start = curMills();
    long r = delegate.executeLargeUpdate();
    executeUpdate(start);
    return r;
  }

  // ---------------------
  //  下面的方法用于批量更新
  // ---------------------

  @Override
  public void addBatch() throws SQLException {
    delegate.addBatch();
    portTrailStatementSqlAttachment.addBatchPreparedSql();
  }

  // ---------------------
  //  下面的方法需要拦截参数
  // ---------------------

  @Override
  public void clearParameters() throws SQLException {
    delegate.clearParameters();
    portTrailStatementSqlAttachment.clearPreparedStatementParameterWrapper();
  }

  @Override
  public void setObject(int parameterIndex, Object x, SQLType targetSqlType) throws SQLException {
    delegate.setObject(parameterIndex, x, targetSqlType);
    portTrailStatementSqlAttachment.set(parameterIndex, "setObject", Binary.of(x, targetSqlType));
  }

  @Override
  public void setObject(int parameterIndex, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
    delegate.setObject(parameterIndex, x, targetSqlType, scaleOrLength);
    portTrailStatementSqlAttachment.set(parameterIndex, "setObject", Ternary.of(x, targetSqlType, scaleOrLength));
  }

  @Override
  public void setNull(int parameterIndex, int sqlType) throws SQLException {
    delegate.setNull(parameterIndex, sqlType);
    portTrailStatementSqlAttachment.set(parameterIndex, "setNull", Unary.of(sqlType));
  }

  @Override
  public void setBoolean(int parameterIndex, boolean x) throws SQLException {
    delegate.setBoolean(parameterIndex, x);
    portTrailStatementSqlAttachment.set(parameterIndex, "setBoolean", Unary.of(x));
  }

  @Override
  public void setByte(int parameterIndex, byte x) throws SQLException {
    delegate.setByte(parameterIndex, x);
    portTrailStatementSqlAttachment.set(parameterIndex, "setByte", Unary.of(x));
  }

  @Override
  public void setShort(int parameterIndex, short x) throws SQLException {
    delegate.setShort(parameterIndex, x);
    portTrailStatementSqlAttachment.set(parameterIndex, "setShort", Unary.of(x));
  }

  @Override
  public void setInt(int parameterIndex, int x) throws SQLException {
    delegate.setInt(parameterIndex, x);
    portTrailStatementSqlAttachment.set(parameterIndex, "setInt", Unary.of(x));
  }

  @Override
  public void setLong(int parameterIndex, long x) throws SQLException {
    delegate.setLong(parameterIndex, x);
    portTrailStatementSqlAttachment.set(parameterIndex, "setLong", Unary.of(x));
  }

  @Override
  public void setFloat(int parameterIndex, float x) throws SQLException {
    delegate.setFloat(parameterIndex, x);
    portTrailStatementSqlAttachment.set(parameterIndex, "setFloat", Unary.of(x));
  }

  @Override
  public void setDouble(int parameterIndex, double x) throws SQLException {
    delegate.setDouble(parameterIndex, x);
    portTrailStatementSqlAttachment.set(parameterIndex, "setDouble", Unary.of(x));
  }

  @Override
  public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
    delegate.setBigDecimal(parameterIndex, x);
    portTrailStatementSqlAttachment.set(parameterIndex, "setBigDecimal", Unary.of(x));
  }

  @Override
  public void setString(int parameterIndex, String x) throws SQLException {
    delegate.setString(parameterIndex, x);
    portTrailStatementSqlAttachment.set(parameterIndex, "setString", Unary.of(x));
  }

  @Override
  public void setDate(int parameterIndex, Date x) throws SQLException {
    delegate.setDate(parameterIndex, x);
    portTrailStatementSqlAttachment.set(parameterIndex, "setDate", Unary.of(x));
  }

  @Override
  public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
    delegate.setTimestamp(parameterIndex, x);
    portTrailStatementSqlAttachment.set(parameterIndex, "setTimestamp", Unary.of(x));
  }

  @Override
  public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
    delegate.setObject(parameterIndex, x, targetSqlType);
    portTrailStatementSqlAttachment.set(parameterIndex, "setObject", Binary.of(x, targetSqlType));
  }

  @Override
  public void setObject(int parameterIndex, Object x) throws SQLException {
    delegate.setObject(parameterIndex, x);
    portTrailStatementSqlAttachment.set(parameterIndex, "setObject", Unary.of(x));
  }

  @Override
  public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
    delegate.setDate(parameterIndex, x, cal);
    portTrailStatementSqlAttachment.set(parameterIndex, "setDate", Binary.of(x, cal));
  }

  @Override
  public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
    delegate.setTime(parameterIndex, x, cal);
    portTrailStatementSqlAttachment.set(parameterIndex, "setTime", Binary.of(x, cal));
  }

  @Override
  public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
    delegate.setTimestamp(parameterIndex, x, cal);
    portTrailStatementSqlAttachment.set(parameterIndex, "setTimestamp", Binary.of(x, cal));
  }

  @Override
  public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
    delegate.setNull(parameterIndex, sqlType, typeName);
    portTrailStatementSqlAttachment.set(parameterIndex, "setNull", Binary.of(sqlType, typeName));
  }

  @Override
  public void setTime(int parameterIndex, Time x) throws SQLException {
    delegate.setTime(parameterIndex, x);
    portTrailStatementSqlAttachment.set(parameterIndex, "setTime", Unary.of(x));
  }

  @Override
  public void setNString(int parameterIndex, String value) throws SQLException {
    delegate.setNString(parameterIndex, value);
    portTrailStatementSqlAttachment.set(parameterIndex, "setNString", Unary.of(value));
  }

  @Override
  public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
    delegate.setObject(parameterIndex, x, targetSqlType, scaleOrLength);
    portTrailStatementSqlAttachment.set(parameterIndex, "setObject", Ternary.of(x, targetSqlType, scaleOrLength));
  }

  @Override
  public void setBytes(int parameterIndex, byte[] x) throws SQLException {
    delegate.setBytes(parameterIndex, x);
    portTrailStatementSqlAttachment.set(parameterIndex, "setBytes", Unary.of(UN_SUPPORTED_PARAMETER));
  }

  @Override
  public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
    delegate.setAsciiStream(parameterIndex, x, length);
    portTrailStatementSqlAttachment.set(parameterIndex, "setAsciiStream", Binary.of(UN_SUPPORTED_PARAMETER, length));
  }

  @Override
  public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
    delegate.setUnicodeStream(parameterIndex, x, length);
    portTrailStatementSqlAttachment.set(parameterIndex, "setUnicodeStream", Binary.of(UN_SUPPORTED_PARAMETER, length));
  }

  @Override
  public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
    delegate.setBinaryStream(parameterIndex, x, length);
    portTrailStatementSqlAttachment.set(parameterIndex, "setBinaryStream", Binary.of(UN_SUPPORTED_PARAMETER, length));
  }

  @Override
  public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
    delegate.setCharacterStream(parameterIndex, reader, length);
    portTrailStatementSqlAttachment.set(parameterIndex, "setCharacterStream", Binary.of(UN_SUPPORTED_PARAMETER, length));
  }

  @Override
  public void setRef(int parameterIndex, Ref x) throws SQLException {
    delegate.setRef(parameterIndex, x);
    portTrailStatementSqlAttachment.set(parameterIndex, "setRef", Unary.of(UN_SUPPORTED_PARAMETER));
  }

  @Override
  public void setBlob(int parameterIndex, Blob x) throws SQLException {
    delegate.setBlob(parameterIndex, x);
    portTrailStatementSqlAttachment.set(parameterIndex, "setBlob", Unary.of(UN_SUPPORTED_PARAMETER));
  }

  @Override
  public void setClob(int parameterIndex, Clob x) throws SQLException {
    delegate.setClob(parameterIndex, x);
    portTrailStatementSqlAttachment.set(parameterIndex, "setClob", Unary.of(UN_SUPPORTED_PARAMETER));
  }

  @Override
  public void setArray(int parameterIndex, Array x) throws SQLException {
    delegate.setArray(parameterIndex, x);
    portTrailStatementSqlAttachment.set(parameterIndex, "setArray", Unary.of(UN_SUPPORTED_PARAMETER));
  }

  @Override
  public ResultSetMetaData getMetaData() throws SQLException {
    return delegate.getMetaData();
  }

  @Override
  public void setURL(int parameterIndex, URL x) throws SQLException {
    delegate.setURL(parameterIndex, x);
    portTrailStatementSqlAttachment.set(parameterIndex, "setURL", Unary.of(UN_SUPPORTED_PARAMETER));
  }

  @Override
  public ParameterMetaData getParameterMetaData() throws SQLException {
    return delegate.getParameterMetaData();
  }

  @Override
  public void setRowId(int parameterIndex, RowId x) throws SQLException {
    delegate.setRowId(parameterIndex, x);
    portTrailStatementSqlAttachment.set(parameterIndex, "setRowId", Unary.of(UN_SUPPORTED_PARAMETER));
  }

  @Override
  public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
    delegate.setNCharacterStream(parameterIndex, value, length);
    portTrailStatementSqlAttachment.set(parameterIndex, "setNCharacterStream", Binary.of(UN_SUPPORTED_PARAMETER, length));
  }

  @Override
  public void setNClob(int parameterIndex, NClob value) throws SQLException {
    delegate.setNClob(parameterIndex, value);
    portTrailStatementSqlAttachment.set(parameterIndex, "setNClob", Unary.of(UN_SUPPORTED_PARAMETER));
  }

  @Override
  public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
    delegate.setClob(parameterIndex, reader, length);
    portTrailStatementSqlAttachment.set(parameterIndex, "setClob", Binary.of(UN_SUPPORTED_PARAMETER, length));
  }

  @Override
  public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
    delegate.setBlob(parameterIndex, inputStream, length);
    portTrailStatementSqlAttachment.set(parameterIndex, "setBlob", Binary.of(UN_SUPPORTED_PARAMETER, length));
  }

  @Override
  public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
    delegate.setNClob(parameterIndex, reader, length);
    portTrailStatementSqlAttachment.set(parameterIndex, "setNClob", Binary.of(UN_SUPPORTED_PARAMETER, length));
  }

  @Override
  public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
    delegate.setSQLXML(parameterIndex, xmlObject);
    portTrailStatementSqlAttachment.set(parameterIndex, "setSQLXML", Unary.of(UN_SUPPORTED_PARAMETER));
  }

  @Override
  public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
    delegate.setAsciiStream(parameterIndex, x, length);
    portTrailStatementSqlAttachment.set(parameterIndex, "setAsciiStream", Binary.of(UN_SUPPORTED_PARAMETER, length));
  }

  @Override
  public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
    delegate.setBinaryStream(parameterIndex, x, length);
    portTrailStatementSqlAttachment.set(parameterIndex, "setBinaryStream", Binary.of(UN_SUPPORTED_PARAMETER, length));
  }

  @Override
  public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
    delegate.setCharacterStream(parameterIndex, reader, length);
    portTrailStatementSqlAttachment.set(parameterIndex, "setCharacterStream", Binary.of(UN_SUPPORTED_PARAMETER, length));
  }

  @Override
  public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
    delegate.setAsciiStream(parameterIndex, x);
    portTrailStatementSqlAttachment.set(parameterIndex, "setAsciiStream", Unary.of(UN_SUPPORTED_PARAMETER));
  }

  @Override
  public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
    delegate.setBinaryStream(parameterIndex, x);
    portTrailStatementSqlAttachment.set(parameterIndex, "setBinaryStream", Unary.of(UN_SUPPORTED_PARAMETER));
  }

  @Override
  public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
    delegate.setCharacterStream(parameterIndex, reader);
    portTrailStatementSqlAttachment.set(parameterIndex, "setCharacterStream", Unary.of(UN_SUPPORTED_PARAMETER));
  }

  @Override
  public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
    delegate.setNCharacterStream(parameterIndex, value);
    portTrailStatementSqlAttachment.set(parameterIndex, "setNCharacterStream", Unary.of(UN_SUPPORTED_PARAMETER));
  }

  @Override
  public void setClob(int parameterIndex, Reader reader) throws SQLException {
    delegate.setClob(parameterIndex, reader);
    portTrailStatementSqlAttachment.set(parameterIndex, "setClob", Unary.of(UN_SUPPORTED_PARAMETER));
  }

  @Override
  public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
    delegate.setBlob(parameterIndex, inputStream);
    portTrailStatementSqlAttachment.set(parameterIndex, "setBlob", Unary.of(UN_SUPPORTED_PARAMETER));
  }

  @Override
  public void setNClob(int parameterIndex, Reader reader) throws SQLException {
    delegate.setNClob(parameterIndex, reader);
    portTrailStatementSqlAttachment.set(parameterIndex, "setNClob", Unary.of(UN_SUPPORTED_PARAMETER));
  }

  // --------------------
  //   Statement里的方法
  // --------------------

  @Override
  public void close() throws SQLException {
    delegate.close();
    closePortTrail();
  }

  @Override
  public void closePortTrail() throws SQLException {
    // execute的数据，已经写入executionBoQueue了，随着事务的执行会写出去。没有execute的数据会丢失。
    portTrailStatementSqlAttachment.clearBatch();
    getPortTrailConnection().removePortTrailStatement(this);
  }

  @Override
  public void clearBatch() throws SQLException {
    delegate.clearBatch();
    portTrailStatementSqlAttachment.clearBatch();
  }

  @Override
  public int[] executeBatch() throws SQLException {
    long start = curMills();
    int[] r = delegate.executeBatch();
    executeBatchUpdate(start);
    return r;
  }

  @Override
  public long[] executeLargeBatch() throws SQLException {
    long start = curMills();
    long[] r = delegate.executeLargeBatch();
    executeBatchUpdate(start);
    return r;
  }

  // ----------------------------
  //  下面的方法是Statement里的方法
  // ----------------------------

  @Override
  public ResultSet executeQuery(String sql) throws SQLException {
    long start = curMills();
    ResultSet resultSet = delegate.executeQuery(sql);
    executeQuery(sql, start);
    return resultSet;
  }

  @Override
  public int executeUpdate(String sql) throws SQLException {
    long start = curMills();
    int r = delegate.executeUpdate(sql);
    executeUpdate(sql, start);
    return r;
  }

//  @Override
//  public void close() throws SQLException {
//    delegate.close();
//  }

//  @Override
//  public void cancel() throws SQLException {
//    delegate.cancel();
//  }

  @Override
  public boolean execute(String sql) throws SQLException {
    long start = curMills();
    boolean r = delegate.execute(sql);
    execute(sql, start);
    return r;
  }

  @Override
  public void addBatch(String sql) throws SQLException {
    delegate.addBatch(sql);
    portTrailStatementSqlAttachment.addBatchSql(sql);
  }

//  @Override
//  public void clearBatch() throws SQLException {
//    delegate.clearBatch();
//  }

//  @Override
//  public int[] executeBatch() throws SQLException {
//    return delegate.executeBatch();
//  }

  @Override
  public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
    long start = curMills();
    int r = delegate.executeUpdate(sql, autoGeneratedKeys);
    executeUpdate(sql, start);
    return r;
  }

  @Override
  public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
    long start = curMills();
    int r = delegate.executeUpdate(sql, columnIndexes);
    executeUpdate(sql, start);
    return r;
  }

  @Override
  public int executeUpdate(String sql, String[] columnNames) throws SQLException {
    long start = curMills();
    int r = delegate.executeUpdate(sql, columnNames);
    executeUpdate(sql, start);
    return r;
  }

  @Override
  public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
    long start = curMills();
    boolean r = delegate.execute(sql, autoGeneratedKeys);
    execute(sql, start);
    return r;
  }

  @Override
  public boolean execute(String sql, int[] columnIndexes) throws SQLException {
    long start = curMills();
    boolean r = delegate.execute(sql, columnIndexes);
    execute(sql, start);
    return r;
  }

  @Override
  public boolean execute(String sql, String[] columnNames) throws SQLException {
    long start = curMills();
    boolean r = delegate.execute(sql, columnNames);
    execute(sql, start);
    return r;
  }

//  @Override
//  public long[] executeLargeBatch() throws SQLException {
//    return delegate.executeLargeBatch();
//  }

  @Override
  public long executeLargeUpdate(String sql) throws SQLException {
    long start = curMills();
    long r = delegate.executeLargeUpdate(sql);
    executeUpdate(sql, start);
    return r;
  }

  @Override
  public long executeLargeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
    long start = curMills();
    long r = delegate.executeLargeUpdate(sql, autoGeneratedKeys);
    executeUpdate(sql, start);
    return r;
  }

  @Override
  public long executeLargeUpdate(String sql, int[] columnIndexes) throws SQLException {
    long start = curMills();
    long r = delegate.executeLargeUpdate(sql, columnIndexes);
    executeUpdate(sql, start);
    return r;
  }

  @Override
  public long executeLargeUpdate(String sql, String[] columnNames) throws SQLException {
    long start = curMills();
    long r = delegate.executeLargeUpdate(sql, columnNames);
    executeUpdate(sql, start);
    return r;
  }

  private void execute(long start) {
    String sql = portTrailStatementSqlAttachment.getParameterizedSql();
    if (SqlUtils.ifQuerySql(sql)) {
      executeQuery(start);
    } else {
      executeUpdate(start);
    }
  }

  private void executeUpdate(long start) {
    String statementState = getInitialStatementStateByIfAutoCommit();
    portTrailStatementSqlAttachment.executePreparedSql(statementState, getTxId(), start, curMills());
    newTxIdIfAutoCommit();
  }

  private void executeQuery(long start) {
    portTrailStatementSqlAttachment.executePreparedSql(AbstractStatementExecutionBo.STATEMENT_STATE_QUERY, getTxId(), start, curMills());
    newTxIdIfAutoCommit();
  }

}
