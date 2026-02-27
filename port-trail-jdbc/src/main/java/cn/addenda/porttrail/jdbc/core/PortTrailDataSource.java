package cn.addenda.porttrail.jdbc.core;

import cn.addenda.porttrail.infrastructure.log.PortTrailLogger;
import cn.addenda.porttrail.infrastructure.log.PortTrailLoggerFactory;
import cn.addenda.porttrail.common.util.UuidUtils;
import cn.addenda.porttrail.infrastructure.writer.DbWriter;
import lombok.Getter;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * @author addenda
 * @since 2024/8/24 17:03
 */
public class PortTrailDataSource extends WrapperAdapter implements DataSource, PortTrailIded, AutoCloseable {

  @Getter
  private final DataSource dataSource;

  private final PortTrailLoggerFactory portTrailLoggerFactory;

  private final PortTrailLogger portTrailLogger;

  private final DbWriter dbWriter;

  private final String portTrailId;

  private final PortTrailLogger portTrailDataSourceConnectionPortTrailLogger;

  public PortTrailDataSource(DataSource dataSource, PortTrailLoggerFactory portTrailLoggerFactory, DbWriter dbWriter) {
    this.dataSource = dataSource;
    this.portTrailLoggerFactory = portTrailLoggerFactory;
    this.portTrailLogger = portTrailLoggerFactory.getPortTrailLogger(PortTrailDataSource.class);
    this.dbWriter = dbWriter;
    this.portTrailId = UuidUtils.generateUuid();

    this.portTrailDataSourceConnectionPortTrailLogger = portTrailLoggerFactory.getPortTrailLogger(PortTrailDataSourceConnection.class);
  }

  @Override
  public Connection getConnection() throws SQLException {
    Connection connection = dataSource.getConnection();
    PortTrailConnection portTrailConnection = new PortTrailDataSourceConnection(
            connection, this, portTrailDataSourceConnectionPortTrailLogger, dbWriter);

    addPortTrailConnection(portTrailConnection);

    return portTrailConnection;
  }

  @Override
  public Connection getConnection(String username, String password) throws SQLException {
    Connection connection = dataSource.getConnection(username, password);
    PortTrailConnection portTrailConnection = new PortTrailDataSourceConnection(
            connection, this, portTrailDataSourceConnectionPortTrailLogger, dbWriter);

    addPortTrailConnection(portTrailConnection);

    return portTrailConnection;
  }

  protected PrintWriter logWriter = new PrintWriter(System.out);

  @Override
  public PrintWriter getLogWriter() throws SQLException {
    return logWriter;
  }

  @Override
  public void setLogWriter(PrintWriter out) throws SQLException {
    this.logWriter = out;
  }

  @Override
  public void setLoginTimeout(int seconds) throws SQLException {
    dataSource.setLoginTimeout(seconds);
  }

  @Override
  public int getLoginTimeout() throws SQLException {
    return dataSource.getLoginTimeout();
  }

  @Override
  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    throw new SQLFeatureNotSupportedException();
  }

  // -----------------
  //   PortTrailIded
  // ----------------

  public String getPortTrailId() {
    return portTrailId;
  }

  @Override
  public void close() throws Exception {
    // 如果DataSource是AutoCloseable的实现，调用close()方法
    if (dataSource instanceof AutoCloseable) {
      ((AutoCloseable) dataSource).close();
    }
    closePortTrail();
  }

  // ----------------------------------
  //   PortTrailConnection Management
  // ----------------------------------

  @Override
  public synchronized void closePortTrail() throws SQLException {
    // 按照先申请资源后释放的步骤，在DataSource关闭的时候，其创造的Connection一定都关闭完成了。
    // 但是，为了在遇到异常步骤时尽可能减少内存泄漏，在close这里还是释放一下
    closeAllPortTrailConnection();
  }

  private final Map<String, PortTrailConnection> portTrailConnectionMap = new HashMap<>();

  private synchronized void addPortTrailConnection(PortTrailConnection portTrailConnection) {
    portTrailConnectionMap.put(portTrailConnection.getPortTrailId(), portTrailConnection);
  }

  public synchronized void removePortTrailConnection(PortTrailConnection portTrailConnection) {
    portTrailConnectionMap.remove(portTrailConnection.getPortTrailId());
  }

  private synchronized void closeAllPortTrailConnection() throws SQLException {
    // 使用 entrySet 的副本进行遍历，避免并发修改异常
    Set<Map.Entry<String, PortTrailConnection>> entries = new HashSet<>(portTrailConnectionMap.entrySet());

    for (Map.Entry<String, PortTrailConnection> entry : entries) {
      PortTrailConnection portTrailConnection = entry.getValue();
      try {
        // close()方法执行的时候，会执行closePortTrail()方法
        portTrailConnection.closePortTrail();
      } catch (SQLException e) {
        portTrailLogger.error("exception occurred when {} close, {}.", PortTrailConnection.class, portTrailConnection, e);
        throw e;
      }
    }
  }

  // -----------
  //   基础方法
  // -----------

  @Override
  public String toString() {
    return "PortTrailDataSource{" +
            "dataSource=" + dataSource +
            ", portTrailId='" + portTrailId + '\'' +
            "} " + super.toString();
  }

}
