package cn.addenda.porttrail.common.pojo.db.dto;

import cn.addenda.porttrail.common.entrypoint.EntryPointSnapshot;
import cn.addenda.porttrail.common.pojo.db.SqlWrapper;
import cn.addenda.porttrail.common.pojo.db.bo.SqlExecutionBo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
@ToString(callSuper = true)
public class SqlExecutionDto extends AbstractDbDto {

  private String sqlState;

  private String txId;

  private Long start;

  private Long end;

  private EntryPointSnapshot entryPointSnapshot;

  private List<SqlWrapper> sqlWrapperList = new ArrayList<>();

  public static SqlExecutionDto createBySqlExecutionBo(SqlExecutionBo sqlExecutionBo) {
    SqlExecutionDto sqlExecutionDto = new SqlExecutionDto();
    sqlExecutionDto.setDataSourcePortTrailId(sqlExecutionBo.getDataSourcePortTrailId());
    sqlExecutionDto.setConnectionPortTrailId(sqlExecutionBo.getConnectionPortTrailId());
    sqlExecutionDto.setStatementPortTrailId(sqlExecutionBo.getStatementPortTrailId());
    sqlExecutionDto.setSqlState(sqlExecutionBo.getSqlState());
    sqlExecutionDto.setTxId(sqlExecutionBo.getTxId());
    sqlExecutionDto.setStart(sqlExecutionBo.getStart());
    sqlExecutionDto.setEnd(sqlExecutionBo.getEnd());
    sqlExecutionDto.setEntryPointSnapshot(sqlExecutionBo.getEntryPointSnapshot());

    List<SqlWrapper> sqlWrapperList = sqlExecutionBo.getSqlWrapperList().stream()
            .map(orderedSql -> {
              SqlWrapper sqlWrapper = new SqlWrapper();
              sqlWrapper.setSql(orderedSql.getSql());
              sqlWrapper.setOrderInConnection(orderedSql.getOrderInConnection());
              sqlWrapper.setOrderInStatement(orderedSql.getOrderInStatement());
              return sqlWrapper;
            }).collect(Collectors.toList());

    sqlExecutionDto.setSqlWrapperList(sqlWrapperList);
    return sqlExecutionDto;
  }

}
