package cn.addenda.porttrail.agent.writer.db;

import cn.addenda.porttrail.agent.link.LinkFacade;
import cn.addenda.porttrail.common.pojo.db.bo.DbExecution;
import cn.addenda.porttrail.facade.LogFacade;
import cn.addenda.porttrail.infrastructure.writer.DbWriter;

public class AgentLogDbWriter implements DbWriter {

  private final LogFacade logFacade;

  public AgentLogDbWriter() {
    logFacade = LinkFacade.createLogFacade(AgentLogDbWriter.class, AgentLogDbWriter.class.getName());
  }

  @Override
  public void writeStatement(DbExecution dbExecution) {
    logFacade.info("statement: {}", LinkFacade.toStr(dbExecution));
  }

  @Override
  public void writePreparedStatement(DbExecution dbExecution) {
    logFacade.info("preparedStatement: {}", LinkFacade.toStr(dbExecution));
  }

  @Override
  public void writeDbConfig(DbExecution dbExecution) {
    logFacade.info("dbConfig: {}", LinkFacade.toStr(dbExecution));
  }

}
