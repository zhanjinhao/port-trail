package cn.addenda.porttrail.common.pojo.db.dto;

import cn.addenda.porttrail.common.entrypoint.EntryPointSnapshot;
import cn.addenda.porttrail.common.pojo.db.bo.PreparedStatementExecutionBo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
@ToString
public class PreparedStatementExecutionDto extends AbstractDbDto {

  private String parameterizedSql;

  private String statementState;

  private String txId;

  private Long start;

  private Long end;

  private EntryPointSnapshot entryPointSnapshot;

  private List<PreparedStatementParameterDto> preparedStatementParameterDtoList = new ArrayList<>();

  public PreparedStatementExecutionDto() {
  }

  public PreparedStatementExecutionDto(PreparedStatementExecutionBo preparedStatementExecutionBo) {
    this.setDataSourcePortTrailId(preparedStatementExecutionBo.getDataSourcePortTrailId());
    this.setConnectionPortTrailId(preparedStatementExecutionBo.getConnectionPortTrailId());
    this.setStatementPortTrailId(preparedStatementExecutionBo.getStatementPortTrailId());
    this.setParameterizedSql(preparedStatementExecutionBo.getParameterizedSql());
    this.setStatementState(preparedStatementExecutionBo.getStatementState());
    this.setTxId(preparedStatementExecutionBo.getTxId());
    this.setStart(preparedStatementExecutionBo.getStart());
    this.setEnd(preparedStatementExecutionBo.getEnd());
    this.setEntryPointSnapshot(preparedStatementExecutionBo.getEntryPointSnapshot());
    this.setPreparedStatementParameterDtoList(
            preparedStatementExecutionBo.getPreparedStatementParameterList().stream()
                    .map(PreparedStatementParameterDto::new).collect(Collectors.toList())
    );
  }

}
