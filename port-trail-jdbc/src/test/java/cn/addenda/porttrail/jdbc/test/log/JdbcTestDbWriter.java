package cn.addenda.porttrail.jdbc.test.log;

import cn.addenda.porttrail.common.pojo.db.bo.DbExecution;
import cn.addenda.porttrail.infrastructure.writer.DbWriter;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class JdbcTestDbWriter implements DbWriter {

  @Getter
  private List<DbExecution> dbExecutionList = new ArrayList<>();

  @Override
  public void writeStatement(DbExecution dbExecution) {
    dbExecutionList.add(dbExecution);
    log.info("statement: {}", dbExecution);
  }

  @Override
  public void writePreparedStatement(DbExecution dbExecution) {
    dbExecutionList.add(dbExecution);
    log.info("preparedStatement: {}", dbExecution);
  }

  @Override
  public void writeDbConfig(DbExecution dbExecution) {
    dbExecutionList.add(dbExecution);
    log.info("dbConfig: {}", dbExecution);
  }

}
