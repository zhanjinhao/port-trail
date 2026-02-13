package cn.addenda.porttrail.common.pojo.db.bo;

import cn.addenda.porttrail.common.entrypoint.EntryPointSnapshot;
import cn.addenda.porttrail.common.pojo.db.dto.DbConfigDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class DbConfigBo implements DbExecution {

  @Setter
  @Getter
  private String jdbcUrl;

  @Setter
  @Getter
  private String user;

  @Setter
  @Getter
  private String password;

  private String dataSourcePortTrailId;

  private String connectionPortTrailId;

  private String statementPortTrailId;

  @Setter
  @Getter
  private String driverName;

  private EntryPointSnapshot entryPointSnapshot;

  public DbConfigBo() {
  }

  public DbConfigBo(DbConfigDto dbConfigDto) {
    this.setJdbcUrl(dbConfigDto.getJdbcUrl());
    this.setUser(dbConfigDto.getUser());
    this.setPassword(dbConfigDto.getPassword());
    this.setDataSourcePortTrailId(dbConfigDto.getDataSourcePortTrailId());
    this.setConnectionPortTrailId(dbConfigDto.getConnectionPortTrailId());
    this.setStatementPortTrailId(dbConfigDto.getStatementPortTrailId());
    this.setDriverName(dbConfigDto.getDriverName());
    this.setEntryPointSnapshot(dbConfigDto.getEntryPointSnapshot());
  }

  @Override
  public String getDbExecutionType() {
    return DbExecution.DB_EXECUTION_TYPE_DB_CONFIG;
  }

  @Override
  public String getDataSourcePortTrailId() {
    return dataSourcePortTrailId;
  }

  @Override
  public void setDataSourcePortTrailId(String dataSourcePortTrailId) {
    this.dataSourcePortTrailId = dataSourcePortTrailId;
  }

  @Override
  public String getConnectionPortTrailId() {
    return connectionPortTrailId;
  }

  @Override
  public void setConnectionPortTrailId(String connectionPortTrailId) {
    this.connectionPortTrailId = connectionPortTrailId;
  }

  @Override
  public String getStatementPortTrailId() {
    return statementPortTrailId;
  }

  @Override
  public void setStatementPortTrailId(String statementPortTrailId) {
    this.statementPortTrailId = statementPortTrailId;
  }

  @Override
  public void setEntryPointSnapshot(EntryPointSnapshot entryPointSnapshot) {
    this.entryPointSnapshot = entryPointSnapshot;
  }

  @Override
  public EntryPointSnapshot getEntryPointSnapshot() {
    return entryPointSnapshot;
  }

}
