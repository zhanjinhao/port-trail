package cn.addenda.porttrail.common.pojo.db.bo;

import cn.addenda.porttrail.common.entrypoint.EntryPointSnapshot;
import cn.addenda.porttrail.common.pojo.db.DbExecution;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public abstract class AbstractSqlExecutionBo implements DbExecution {

  public static final String SQL_STATE_NEW = "NEW";
  public static final String SQL_STATE_QUERY = "QUERY";
  public static final String SQL_STATE_COMMITTED = "COMMITTED";
  public static final String SQL_STATE_ROLLBACK = "ROLLBACK";
  public static final String SQL_STATE_UNKNOWN = "UNKNOWN";

  private String dataSourcePortTrailId;
  private String connectionPortTrailId;
  private String statementPortTrailId;

  @Setter
  @Getter
  private String sqlState;

  @Setter
  @Getter
  private String txId;

  @Setter
  @Getter
  private Long start;

  @Setter
  @Getter
  private Long end;

  private EntryPointSnapshot entryPointSnapshot;

  protected AbstractSqlExecutionBo(
          String dataSourcePortTrailId, String connectionPortTrailId, String statementPortTrailId,
          String sqlState, String txId, Long start, Long end) {
    this.dataSourcePortTrailId = dataSourcePortTrailId;
    this.connectionPortTrailId = connectionPortTrailId;
    this.statementPortTrailId = statementPortTrailId;
    this.sqlState = sqlState;
    this.txId = txId;
    this.start = start;
    this.end = end;
  }

  protected AbstractSqlExecutionBo() {
  }

  @Override
  public String getDataSourcePortTrailId() {
    return dataSourcePortTrailId;
  }

  @Override
  public void setDataSourcePortTrailId(String dataSourcePortTrailId) {
    this.dataSourcePortTrailId = dataSourcePortTrailId;
  }

  @Override
  public String getConnectionPortTrailId() {
    return connectionPortTrailId;
  }

  @Override
  public void setConnectionPortTrailId(String connectionPortTrailId) {
    this.connectionPortTrailId = connectionPortTrailId;
  }

  @Override
  public String getStatementPortTrailId() {
    return statementPortTrailId;
  }

  @Override
  public void setStatementPortTrailId(String statementPortTrailId) {
    this.statementPortTrailId = statementPortTrailId;
  }

  @Override
  public void setEntryPointSnapshot(EntryPointSnapshot entryPointSnapshot) {
    this.entryPointSnapshot = entryPointSnapshot;
  }

  @Override
  public EntryPointSnapshot getEntryPointSnapshot() {
    return entryPointSnapshot;
  }

  public abstract boolean ifKeepAlive();

}
