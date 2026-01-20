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

  public static DbConfigDto createByDbConfigBo(DbConfigBo dbConfigBo) {
    DbConfigDto dbConfigDto = new DbConfigDto();
    dbConfigDto.setJdbcUrl(dbConfigBo.getJdbcUrl());
    dbConfigDto.setUser(dbConfigBo.getUser());
    dbConfigDto.setPassword(dbConfigBo.getPassword());
    dbConfigDto.setDataSourcePortTrailId(dbConfigBo.getDataSourcePortTrailId());
    dbConfigDto.setConnectionPortTrailId(dbConfigBo.getConnectionPortTrailId());
    dbConfigDto.setStatementPortTrailId(dbConfigBo.getStatementPortTrailId());
    dbConfigDto.setDriverName(dbConfigBo.getDriverName());
    dbConfigDto.setEntryPointSnapshot(dbConfigBo.getEntryPointSnapshot());
    return dbConfigDto;
  }

}
