package cn.addenda.porttrail.common.pojo.db;

import cn.addenda.porttrail.common.entrypoint.EntryPointSnapshot;

import java.beans.Statement;
import java.sql.PreparedStatement;

/**
 * 一个实例代表一次{@link Statement}或{@link PreparedStatement}的execute
 */
public interface DbExecution {

  String DB_EXECUTION_TYPE_SQL = "SQL";
  String DB_EXECUTION_TYPE_PREPARED_SQL = "PREPARED_SQL";
  String DB_EXECUTION_TYPE_CONFIG = "CONFIG";

  String getDbExecutionType();

  String getDataSourcePortTrailId();

  void setDataSourcePortTrailId(String dataSourcePortTrailId);

  String getConnectionPortTrailId();

  void setConnectionPortTrailId(String connectionPortTrailId);

  String getStatementPortTrailId();

  void setStatementPortTrailId(String statementPortTrailId);

  void setEntryPointSnapshot(EntryPointSnapshot entryPointSnapshot);

  EntryPointSnapshot getEntryPointSnapshot();

}
