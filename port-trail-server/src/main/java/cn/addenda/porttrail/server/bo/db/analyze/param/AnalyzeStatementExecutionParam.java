package cn.addenda.porttrail.server.bo.db.analyze.param;

import cn.addenda.porttrail.common.pojo.db.dto.StatementExecutionDto;
import cn.addenda.porttrail.server.bo.db.StatementExecutionEntityBo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class AnalyzeStatementExecutionParam implements AnalyzeParam {

  private StatementExecutionEntityBo statementExecutionEntityBo;

  private StatementExecutionDto statementExecutionDto;

}
