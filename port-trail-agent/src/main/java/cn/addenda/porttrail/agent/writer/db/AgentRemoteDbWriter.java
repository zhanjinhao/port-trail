package cn.addenda.porttrail.agent.writer.db;

import cn.addenda.porttrail.agent.link.LinkFacade;
import cn.addenda.porttrail.common.pojo.db.DbExecution;
import cn.addenda.porttrail.common.pojo.db.bo.DbConfigBo;
import cn.addenda.porttrail.common.pojo.db.bo.PreparedStatementExecutionBo;
import cn.addenda.porttrail.common.pojo.db.bo.StatementExecutionBo;
import cn.addenda.porttrail.common.pojo.db.dto.DbConfigDto;
import cn.addenda.porttrail.common.pojo.db.dto.PreparedStatementExecutionDto;
import cn.addenda.porttrail.common.pojo.db.dto.StatementExecutionDto;
import cn.addenda.porttrail.common.util.JdkSerializationUtils;
import cn.addenda.porttrail.infrastructure.context.AgentContext;
import cn.addenda.porttrail.infrastructure.writer.DbWriter;

public class AgentRemoteDbWriter implements DbWriter {

  @Override
  public void writeStatement(DbExecution dbExecution) {
    StatementExecutionDto statementExecutionDto =
            StatementExecutionDto.createByStatementExecutionBo((StatementExecutionBo) dbExecution);
    statementExecutionDto.setServiceRuntimeInfo(AgentContext.getServiceRuntimeInfo());
    LinkFacade.sendStatementExecution(JdkSerializationUtils.serialize(statementExecutionDto));
  }

  @Override
  public void writePreparedStatement(DbExecution dbExecution) {
    PreparedStatementExecutionDto preparedStatementExecutionDto =
            PreparedStatementExecutionDto.createByPreparedStatementExecutionBo((PreparedStatementExecutionBo) dbExecution);
    preparedStatementExecutionDto.setServiceRuntimeInfo(AgentContext.getServiceRuntimeInfo());
    LinkFacade.sendPreparedStatementExecution(JdkSerializationUtils.serialize(preparedStatementExecutionDto));
  }

  @Override
  public void writeDbConfig(DbExecution dbExecution) {
    DbConfigDto dbConfigDto =
            DbConfigDto.createByDbConfigBo((DbConfigBo) dbExecution);
    dbConfigDto.setServiceRuntimeInfo(AgentContext.getServiceRuntimeInfo());
    LinkFacade.sendDbConfig(LinkFacade.toStr(dbConfigDto));
  }

}
