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

  public static StatementExecutionDto createByStatementExecutionBo(StatementExecutionBo statementExecutionBo) {
    StatementExecutionDto statementExecutionDto = new StatementExecutionDto();
    statementExecutionDto.setDataSourcePortTrailId(statementExecutionBo.getDataSourcePortTrailId());
    statementExecutionDto.setConnectionPortTrailId(statementExecutionBo.getConnectionPortTrailId());
    statementExecutionDto.setStatementPortTrailId(statementExecutionBo.getStatementPortTrailId());
    statementExecutionDto.setStatementState(statementExecutionBo.getStatementState());
    statementExecutionDto.setTxId(statementExecutionBo.getTxId());
    statementExecutionDto.setStart(statementExecutionBo.getStart());
    statementExecutionDto.setEnd(statementExecutionBo.getEnd());
    statementExecutionDto.setEntryPointSnapshot(statementExecutionBo.getEntryPointSnapshot());

    List<StatementSqlDto> statementSqlDtoList = statementExecutionBo.getStatementSqlList().stream()
            .map(statementSql -> {
              StatementSqlDto statementSqlDto = new StatementSqlDto();
              statementSqlDto.setSql(statementSql.getSql());
              statementSqlDto.setOrderInConnection(statementSql.getOrderInConnection());
              statementSqlDto.setOrderInStatement(statementSql.getOrderInStatement());
              return statementSqlDto;
            }).collect(Collectors.toList());

    statementExecutionDto.setStatementSqlDtoList(statementSqlDtoList);
    return statementExecutionDto;
  }

}
