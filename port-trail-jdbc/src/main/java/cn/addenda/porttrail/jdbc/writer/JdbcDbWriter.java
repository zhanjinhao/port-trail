package cn.addenda.porttrail.jdbc.writer;

import cn.addenda.porttrail.common.pojo.db.bo.DbExecution;
import cn.addenda.porttrail.infrastructure.writer.DbWriter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JdbcDbWriter implements DbWriter {

  @Override
  public void writeStatement(DbExecution dbExecution) {
    log.info("statement: {}", dbExecution);
  }

  @Override
  public void writePreparedStatement(DbExecution dbExecution) {
    log.info("preparedStatement: {}", dbExecution);
  }

  @Override
  public void writeDbConfig(DbExecution dbExecution) {
    log.info("dbConfig: {}", dbExecution);
  }

}
