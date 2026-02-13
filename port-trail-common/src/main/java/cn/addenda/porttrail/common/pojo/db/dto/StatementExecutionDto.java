package cn.addenda.porttrail.common.pojo.db.dto;

import cn.addenda.porttrail.common.entrypoint.EntryPointSnapshot;
import cn.addenda.porttrail.common.pojo.db.bo.StatementExecutionBo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
@ToString(callSuper = true)
public class StatementExecutionDto extends AbstractDbDto {

  private String statementState;

  private String txId;

  private Long start;

  private Long end;

  private EntryPointSnapshot entryPointSnapshot;

  private List<StatementSqlDto> statementSqlDtoList = new ArrayList<>();

  public StatementExecutionDto() {
  }

  public StatementExecutionDto(StatementExecutionBo statementExecutionBo) {
    this.setDataSourcePortTrailId(statementExecutionBo.getDataSourcePortTrailId());
    this.setConnectionPortTrailId(statementExecutionBo.getConnectionPortTrailId());
    this.setStatementPortTrailId(statementExecutionBo.getStatementPortTrailId());
    this.setStatementState(statementExecutionBo.getStatementState());
    this.setTxId(statementExecutionBo.getTxId());
    this.setStart(statementExecutionBo.getStart());
    this.setEnd(statementExecutionBo.getEnd());
    this.setEntryPointSnapshot(statementExecutionBo.getEntryPointSnapshot());

    this.setStatementSqlDtoList(
            statementExecutionBo.getStatementSqlList().stream()
                    .map(StatementSqlDto::new).collect(Collectors.toList())
    );
  }

}
