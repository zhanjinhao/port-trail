package cn.addenda.porttrail.server.biz;

import cn.addenda.porttrail.common.pojo.db.dto.DbConfigDto;
import cn.addenda.porttrail.common.pojo.db.dto.PreparedStatementExecutionDto;
import cn.addenda.porttrail.common.pojo.db.dto.StatementExecutionDto;
import cn.addenda.porttrail.server.bo.db.PreparedStatementExecutionEntityBo;
import cn.addenda.porttrail.server.bo.db.StatementExecutionEntityBo;

public interface DbExecutionBiz {

  void handleDbConfig(DbConfigDto dbConfigDto);

  PreparedStatementExecutionEntityBo handlePreparedStatementExecution(PreparedStatementExecutionDto preparedStatementExecutionDto);

  StatementExecutionEntityBo handleStatementExecution(StatementExecutionDto statementExecutionDto);

}
