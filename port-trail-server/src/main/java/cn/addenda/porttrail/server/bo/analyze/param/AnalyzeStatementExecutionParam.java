package cn.addenda.porttrail.server.bo.analyze.param;

import cn.addenda.porttrail.common.pojo.db.dto.StatementExecutionDto;
import cn.addenda.porttrail.server.bo.est.EstStatementExecutionBo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class AnalyzeStatementExecutionParam implements AnalyzeParam {

  private EstStatementExecutionBo estStatementExecutionBo;

  private StatementExecutionDto statementExecutionDto;

}
