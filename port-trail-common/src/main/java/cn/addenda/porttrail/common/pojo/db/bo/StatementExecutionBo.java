package cn.addenda.porttrail.common.pojo.db.bo;

import cn.addenda.porttrail.common.pojo.db.dto.StatementExecutionDto;
import cn.addenda.porttrail.common.util.ArrayUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 1、一次{@link Statement#executeBatch()}或{@link Statement#executeLargeBatch()} 执行，statementExecutionList的size()大于1。 <br/>
 * 2、一次{@link Statement}的其它execute执行，statementExecutionList的size()等于1。
 */
@Setter
@Getter
@ToString(callSuper = true)
public class StatementExecutionBo extends AbstractStatementExecutionBo {

  private List<StatementSql> statementSqlList;

  public StatementExecutionBo() {
    this.statementSqlList = new ArrayList<>();
  }

  public StatementExecutionBo(String dataSourcePortTrailId, String connectionPortTrailId, String statementPortTrailId,
                              String statementState, StatementSql statementSql, String txId, long start, long end) {
    super(dataSourcePortTrailId, connectionPortTrailId, statementPortTrailId, statementState, txId, start, end);
    this.statementSqlList = ArrayUtils.asArrayList(statementSql);
  }

  public void clear() {
    this.statementSqlList = new ArrayList<>();
  }

  @Override
  public String getDbExecutionType() {
    return DB_EXECUTION_TYPE_STATEMENT;
  }

  public StatementExecutionBo(StatementExecutionDto statementExecutionDto) {
    this.setDataSourcePortTrailId(statementExecutionDto.getDataSourcePortTrailId());
    this.setConnectionPortTrailId(statementExecutionDto.getConnectionPortTrailId());
    this.setStatementPortTrailId(statementExecutionDto.getStatementPortTrailId());
    this.setStatementState(statementExecutionDto.getStatementState());
    this.setTxId(statementExecutionDto.getTxId());
    this.setStart(statementExecutionDto.getStart());
    this.setEnd(statementExecutionDto.getEnd());
    this.setEntryPointSnapshot(statementExecutionDto.getEntryPointSnapshot());

    this.setStatementSqlList(
            statementExecutionDto.getStatementSqlDtoList().stream()
                    .map(StatementSql::new).collect(Collectors.toList())
    );
  }

  @Override
  public boolean ifKeepAlive() {
    for (StatementSql statementSql : statementSqlList) {
      String sqlWithoutBlank = statementSql.getSql().replaceAll("\\s+", "");
      if (!"select1".equalsIgnoreCase(sqlWithoutBlank)
              && !"select1fromdual".equalsIgnoreCase(sqlWithoutBlank)) {
        return false;
      }
    }
    return true;
  }

}
