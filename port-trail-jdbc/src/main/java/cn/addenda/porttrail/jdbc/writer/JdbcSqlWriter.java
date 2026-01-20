package cn.addenda.porttrail.jdbc.writer;

import cn.addenda.porttrail.common.pojo.db.DbExecution;
import cn.addenda.porttrail.infrastructure.writer.SqlWriter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JdbcSqlWriter implements SqlWriter {

  @Override
  public void writeSql(DbExecution dbExecution) {
    log.info("sql: {}", dbExecution);
  }

  @Override
  public void writePreparedSql(DbExecution dbExecution) {
    log.info("preparedSql: {}", dbExecution);
  }

  @Override
  public void writeConfig(DbExecution dbExecution) {
    log.info("config: {}", dbExecution);
  }

}
