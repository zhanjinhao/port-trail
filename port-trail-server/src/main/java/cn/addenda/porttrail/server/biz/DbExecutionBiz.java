package cn.addenda.porttrail.server.biz;

import cn.addenda.porttrail.common.pojo.db.dto.DbConfigDto;
import cn.addenda.porttrail.common.pojo.db.dto.PreparedStatementExecutionDto;
import cn.addenda.porttrail.common.pojo.db.dto.StatementExecutionDto;

public interface DbExecutionBiz {

  void handleDbConfig(DbConfigDto dbConfigDto);

  void handleStatementExecution(StatementExecutionDto statementExecutionDto);

  void handlePreparedStatementExecution(PreparedStatementExecutionDto preparedStatementExecutionDto);
}
