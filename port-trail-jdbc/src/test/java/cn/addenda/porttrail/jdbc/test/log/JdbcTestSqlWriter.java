package cn.addenda.porttrail.jdbc.test.log;

import cn.addenda.porttrail.common.pojo.db.DbExecution;
import cn.addenda.porttrail.infrastructure.writer.SqlWriter;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class JdbcTestSqlWriter implements SqlWriter {

  @Getter
  private List<DbExecution> dbExecutionList = new ArrayList<>();

  @Override
  public void writeSql(DbExecution dbExecution) {
    dbExecutionList.add(dbExecution);
    log.info("sql: {}", dbExecution);
  }

  @Override
  public void writePreparedSql(DbExecution dbExecution) {
    dbExecutionList.add(dbExecution);
    log.info("preparedSql: {}", dbExecution);
  }

  @Override
  public void writeConfig(DbExecution dbExecution) {
    dbExecutionList.add(dbExecution);
    log.info("config: {}", dbExecution);
  }

}
