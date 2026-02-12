package cn.addenda.porttrail.jdbc.core;

import cn.addenda.porttrail.infrastructure.log.PortTrailLogger;
import cn.addenda.porttrail.infrastructure.writer.DbWriter;
import lombok.Getter;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author addenda
 * @since 2024/8/24 17:03
 */
public class PortTrailDataSourceConnection extends PortTrailConnection {

  @Getter
  private final PortTrailDataSource portTrailDataSource;

  public PortTrailDataSourceConnection(Connection connection, PortTrailDataSource portTrailDataSource,
                                       PortTrailLogger portTrailLogger, DbWriter dbWriter) throws SQLException {
    super(connection, portTrailLogger, dbWriter);
    this.portTrailDataSource = portTrailDataSource;
  }

  // ------------------------------------------------------------
  //   PortTrailPreparedStatement&PortTrailStatement Management
  // ------------------------------------------------------------

  @Override
  public synchronized void closePortTrail() throws SQLException {
    super.closePortTrail();
    // 从DataSource的ConnectionManager中移除当前Connection
    portTrailDataSource.removePortTrailConnection(this);
  }

  // -----------
  //   基础方法
  // -----------

  @Override
  public String toString() {
    return "PortTrailDataSourceConnection{" +
            "portTrailDataSource=" + portTrailDataSource +
            "} " + super.toString();
  }

}
