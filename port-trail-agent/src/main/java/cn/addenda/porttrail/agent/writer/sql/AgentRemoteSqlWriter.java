package cn.addenda.porttrail.agent.writer.sql;

import cn.addenda.porttrail.agent.link.LinkFacade;
import cn.addenda.porttrail.common.pojo.db.DbExecution;
import cn.addenda.porttrail.common.pojo.db.bo.DbConfigBo;
import cn.addenda.porttrail.common.pojo.db.bo.PreparedSqlExecutionBo;
import cn.addenda.porttrail.common.pojo.db.bo.SqlExecutionBo;
import cn.addenda.porttrail.common.pojo.db.dto.DbConfigDto;
import cn.addenda.porttrail.common.pojo.db.dto.PreparedSqlExecutionDto;
import cn.addenda.porttrail.common.pojo.db.dto.SqlExecutionDto;
import cn.addenda.porttrail.common.util.JdkSerializationUtils;
import cn.addenda.porttrail.infrastructure.context.AgentContext;
import cn.addenda.porttrail.infrastructure.writer.SqlWriter;

public class AgentRemoteSqlWriter implements SqlWriter {

  @Override
  public void writeSql(DbExecution dbExecution) {
    SqlExecutionDto sqlExecutionDto =
            SqlExecutionDto.createBySqlExecutionBo((SqlExecutionBo) dbExecution);
    sqlExecutionDto.setServiceRuntimeInfo(AgentContext.getServiceRuntimeInfo());
    LinkFacade.sendSqlExecution(JdkSerializationUtils.serialize(sqlExecutionDto));
  }

  @Override
  public void writePreparedSql(DbExecution dbExecution) {
    PreparedSqlExecutionDto preparedSqlExecutionDto =
            PreparedSqlExecutionDto.createByPreparedSqlExecutionBo((PreparedSqlExecutionBo) dbExecution);
    preparedSqlExecutionDto.setServiceRuntimeInfo(AgentContext.getServiceRuntimeInfo());
    LinkFacade.sendPreparedSqlExecution(JdkSerializationUtils.serialize(preparedSqlExecutionDto));
  }

  @Override
  public void writeConfig(DbExecution dbExecution) {
    DbConfigDto dbConfigDto =
            DbConfigDto.createByDbConfigBo((DbConfigBo) dbExecution);
    dbConfigDto.setServiceRuntimeInfo(AgentContext.getServiceRuntimeInfo());
    LinkFacade.sendDbConfig(LinkFacade.toStr(dbConfigDto));
  }

}
