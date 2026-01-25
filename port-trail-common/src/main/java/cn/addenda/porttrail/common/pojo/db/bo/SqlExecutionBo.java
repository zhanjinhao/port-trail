package cn.addenda.porttrail.common.pojo.db.bo;

import cn.addenda.porttrail.common.pojo.db.SqlWrapper;
import cn.addenda.porttrail.common.pojo.db.dto.SqlExecutionDto;
import cn.addenda.porttrail.common.util.ArrayUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 1、一次{@link Statement#executeBatch()}或{@link Statement#executeLargeBatch()} 执行，orderedSqlList的size()大于1。 <br/>
 * 2、一次{@link Statement}的其它execute执行，orderedSqlList的size()等于1。
 */
@Setter
@Getter
@ToString(callSuper = true)
public class SqlExecutionBo extends AbstractSqlExecutionBo {

  private List<SqlWrapper> sqlWrapperList;

  public SqlExecutionBo() {
    this.sqlWrapperList = new ArrayList<>();
  }

  public SqlExecutionBo(String dataSourcePortTrailId, String connectionPortTrailId, String statementPortTrailId,
                        String sqlState, SqlWrapper sqlWrapper, String txId, long start, long end) {
    super(dataSourcePortTrailId, connectionPortTrailId, statementPortTrailId, sqlState, txId, start, end);
    this.sqlWrapperList = ArrayUtils.asArrayList(sqlWrapper);
  }

  public void clear() {
    this.sqlWrapperList = new ArrayList<>();
  }

  @Override
  public String getDbExecutionType() {
    return DB_EXECUTION_TYPE_SQL;
  }

  public static SqlExecutionBo createBySqlExecutionDto(SqlExecutionDto sqlExecutionDto) {
    SqlExecutionBo sqlExecutionBo = new SqlExecutionBo();
    sqlExecutionBo.setDataSourcePortTrailId(sqlExecutionDto.getDataSourcePortTrailId());
    sqlExecutionBo.setConnectionPortTrailId(sqlExecutionDto.getConnectionPortTrailId());
    sqlExecutionBo.setStatementPortTrailId(sqlExecutionDto.getStatementPortTrailId());
    sqlExecutionBo.setSqlState(sqlExecutionDto.getSqlState());
    sqlExecutionBo.setTxId(sqlExecutionDto.getTxId());
    sqlExecutionBo.setStart(sqlExecutionDto.getStart());
    sqlExecutionBo.setEnd(sqlExecutionDto.getEnd());
    sqlExecutionBo.setEntryPointSnapshot(sqlExecutionDto.getEntryPointSnapshot());

    List<SqlWrapper> sqlWrapperList = sqlExecutionDto.getSqlWrapperList().stream()
            .map(sql -> {
              SqlWrapper sqlWrapper = new SqlWrapper();
              sqlWrapper.setSql(sql.getSql());
              sqlWrapper.setOrderInConnection(sql.getOrderInConnection());
              sqlWrapper.setOrderInStatement(sql.getOrderInStatement());
              return sqlWrapper;
            }).collect(Collectors.toList());

    sqlExecutionBo.setSqlWrapperList(sqlWrapperList);
    return sqlExecutionBo;
  }

  @Override
  public boolean ifKeepAlive() {
    for (SqlWrapper sqlWrapper : sqlWrapperList) {
      String sqlWithoutBlank = sqlWrapper.getSql().replaceAll("\\s+", "");
      if (!"select1".equalsIgnoreCase(sqlWithoutBlank)
              && !"select1fromdual".equalsIgnoreCase(sqlWithoutBlank)) {
        return false;
      }
    }
    return true;
  }

}
