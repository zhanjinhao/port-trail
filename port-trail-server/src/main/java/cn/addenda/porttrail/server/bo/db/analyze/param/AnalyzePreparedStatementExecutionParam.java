package cn.addenda.porttrail.server.bo.db.analyze.param;

import cn.addenda.porttrail.common.pojo.db.dto.PreparedStatementExecutionDto;
import cn.addenda.porttrail.server.bo.db.PreparedStatementExecutionEntityBo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class AnalyzePreparedStatementExecutionParam implements AnalyzeParam {

  private PreparedStatementExecutionEntityBo preparedStatementExecutionEntityBo;

  private PreparedStatementExecutionDto preparedStatementExecutionDto;

}
