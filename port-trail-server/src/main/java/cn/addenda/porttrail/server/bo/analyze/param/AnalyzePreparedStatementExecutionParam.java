package cn.addenda.porttrail.server.bo.analyze.param;

import cn.addenda.porttrail.common.pojo.db.dto.PreparedStatementExecutionDto;
import cn.addenda.porttrail.server.bo.est.EstPreparedStatementExecutionBo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class AnalyzePreparedStatementExecutionParam implements AnalyzeParam {

  private EstPreparedStatementExecutionBo estPreparedStatementExecutionBo;

  private PreparedStatementExecutionDto preparedStatementExecutionDto;

}
