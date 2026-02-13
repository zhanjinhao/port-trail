package cn.addenda.porttrail.common.pojo.db.dto;

import cn.addenda.porttrail.common.entrypoint.EntryPointSnapshot;
import cn.addenda.porttrail.common.pojo.db.bo.DbConfigBo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class DbConfigDto extends AbstractDbDto {

  private String jdbcUrl;

  private String user;

  private String password;

  private String driverName;

  private EntryPointSnapshot entryPointSnapshot;

  public DbConfigDto() {
  }

  public DbConfigDto(DbConfigBo dbConfigBo) {
    this.setJdbcUrl(dbConfigBo.getJdbcUrl());
    this.setUser(dbConfigBo.getUser());
    this.setPassword(dbConfigBo.getPassword());
    this.setDataSourcePortTrailId(dbConfigBo.getDataSourcePortTrailId());
    this.setConnectionPortTrailId(dbConfigBo.getConnectionPortTrailId());
    this.setStatementPortTrailId(dbConfigBo.getStatementPortTrailId());
    this.setDriverName(dbConfigBo.getDriverName());
    this.setEntryPointSnapshot(dbConfigBo.getEntryPointSnapshot());
  }

}
