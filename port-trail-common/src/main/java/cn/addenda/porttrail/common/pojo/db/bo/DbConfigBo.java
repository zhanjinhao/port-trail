package cn.addenda.porttrail.common.pojo.db.bo;

import cn.addenda.porttrail.common.entrypoint.EntryPointSnapshot;
import cn.addenda.porttrail.common.pojo.db.DbExecution;
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

  @Override
  public String getDbExecutionType() {
    return DbExecution.DB_EXECUTION_TYPE_CONFIG;
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

  public static DbConfigBo createByDbConfigDto(DbConfigDto dbConfigDto) {
    DbConfigBo dbConfigBo = new DbConfigBo();
    dbConfigBo.setJdbcUrl(dbConfigDto.getJdbcUrl());
    dbConfigBo.setUser(dbConfigDto.getUser());
    dbConfigBo.setPassword(dbConfigDto.getPassword());
    dbConfigBo.setDataSourcePortTrailId(dbConfigDto.getDataSourcePortTrailId());
    dbConfigBo.setConnectionPortTrailId(dbConfigDto.getConnectionPortTrailId());
    dbConfigBo.setStatementPortTrailId(dbConfigDto.getStatementPortTrailId());
    dbConfigBo.setDriverName(dbConfigDto.getDriverName());
    dbConfigBo.setEntryPointSnapshot(dbConfigDto.getEntryPointSnapshot());
    return dbConfigBo;
  }

}
