package cn.addenda.porttrail.server.biz.analyze;

import cn.addenda.porttrail.server.bo.db.analyze.result.AnalyzeResult;
import cn.addenda.porttrail.server.entity.DbConfigEntity;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.util.IsolationLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.DisposableBean;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractDataSourceAnalyzeHandler<T extends AnalyzeResult>
        implements AnalyzeHandler<T>, DisposableBean {

  private final Map<DataSourceKey, HikariDataSource> dataSourceMap = new ConcurrentHashMap<>();

  protected Connection getConnection(DbConfigEntity dbConfigEntity) throws SQLException {
    return getDataSource(dbConfigEntity).getConnection();
  }

  protected DataSource getDataSource(DbConfigEntity dbConfigEntity) {
    return getDataSource(new DataSourceKey(dbConfigEntity.getJdbcUrl(), dbConfigEntity.getUser(), dbConfigEntity.getPassword(), dbConfigEntity.getDriverName()));
  }

  private DataSource getDataSource(DataSourceKey dataSourceKey) {
    return dataSourceMap.computeIfAbsent(dataSourceKey,
            s -> {
              HikariDataSource hikariDataSource = new HikariDataSource();
              hikariDataSource.setPoolName(s.toString());
              hikariDataSource.setAutoCommit(false);
              hikariDataSource.setTransactionIsolation(IsolationLevel.TRANSACTION_READ_COMMITTED.name());
              // todo 这里要考虑连接不上的场景
              hikariDataSource.setDriverClassName(s.getDriverName());
              hikariDataSource.setJdbcUrl(s.getJdbcUrl());
              hikariDataSource.setUsername(s.getUser());
              hikariDataSource.setPassword(s.getPassword());
              hikariDataSource.setMinimumIdle(0);
              hikariDataSource.setMaxLifetime(1800000);
              hikariDataSource.setMaximumPoolSize(50);
              hikariDataSource.setIdleTimeout(60000);
              hikariDataSource.setConnectionTimeout(3000);
              hikariDataSource.setConnectionTestQuery("select 1");
              hikariDataSource.setAutoCommit(false);
              return hikariDataSource;
            });
  }

  @Setter
  @Getter
  @ToString
  public static class DataSourceKey {
    /**
     * jdbcUrl
     */
    private String jdbcUrl;
    /**
     * user
     */
    private String user;
    /**
     * password
     */
    private String password;
    /**
     * driverName
     */
    private String driverName;

    public DataSourceKey() {
    }

    public DataSourceKey(String jdbcUrl, String user, String password, String driverName) {
      this.jdbcUrl = jdbcUrl;
      this.user = user;
      this.password = password;
      this.driverName = driverName;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      DataSourceKey that = (DataSourceKey) o;
      return Objects.equals(getJdbcUrl(), that.getJdbcUrl())
              && Objects.equals(getUser(), that.getUser())
              && Objects.equals(getPassword(), that.getPassword())
              && Objects.equals(getDriverName(), that.getDriverName());
    }

    @Override
    public int hashCode() {
      return Objects.hash(getJdbcUrl(), getUser(), getPassword(), getDriverName());
    }

  }

  @Override
  public void destroy() throws Exception {
    Set<Map.Entry<DataSourceKey, HikariDataSource>> entrySet = dataSourceMap.entrySet();
    for (Map.Entry<DataSourceKey, HikariDataSource> entry : entrySet) {
      entry.getValue().close();
    }
  }

}
