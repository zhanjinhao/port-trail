package cn.addenda.porttrail.server.biz;

import cn.addenda.porttrail.common.pojo.db.dto.DbConfigDto;
import cn.addenda.porttrail.common.pojo.db.dto.PreparedStatementExecutionDto;
import cn.addenda.porttrail.common.pojo.db.dto.StatementExecutionDto;
import cn.addenda.porttrail.server.bo.est.EstPreparedStatementExecutionBo;
import cn.addenda.porttrail.server.bo.est.EstStatementExecutionBo;

public interface DbExecutionBiz {

  void handleDbConfig(DbConfigDto dbConfigDto);

  EstPreparedStatementExecutionBo handlePreparedStatementExecution(PreparedStatementExecutionDto preparedStatementExecutionDto);

  EstStatementExecutionBo handleStatementExecution(StatementExecutionDto statementExecutionDto);

}
