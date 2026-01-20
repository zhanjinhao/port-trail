package cn.addenda.porttrail.agent.writer.sql;

import cn.addenda.porttrail.agent.link.LinkFacade;
import cn.addenda.porttrail.common.pojo.db.DbExecution;
import cn.addenda.porttrail.facade.LogFacade;
import cn.addenda.porttrail.infrastructure.writer.SqlWriter;

public class AgentLogSqlWriter implements SqlWriter {

  private final LogFacade logFacade;

  public AgentLogSqlWriter() {
    logFacade = LinkFacade.createLogFacade(AgentLogSqlWriter.class, AgentLogSqlWriter.class.getName());
  }

  @Override
  public void writeSql(DbExecution dbExecution) {
    logFacade.info("sql: {}", LinkFacade.toStr(dbExecution));
  }

  @Override
  public void writePreparedSql(DbExecution dbExecution) {
    logFacade.info("preparedSql: {}", LinkFacade.toStr(dbExecution));
  }

  @Override
  public void writeConfig(DbExecution dbExecution) {
    logFacade.info("config: {}", LinkFacade.toStr(dbExecution));
  }

}
