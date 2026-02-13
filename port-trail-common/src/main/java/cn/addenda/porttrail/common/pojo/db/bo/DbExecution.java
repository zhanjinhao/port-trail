package cn.addenda.porttrail.common.pojo.db.bo;

import cn.addenda.porttrail.common.entrypoint.EntryPointSnapshot;

import java.beans.Statement;
import java.sql.PreparedStatement;

/**
 * 一个实例代表一次{@link Statement}或{@link PreparedStatement}的execute
 */
public interface DbExecution {

  String DB_EXECUTION_TYPE_STATEMENT = "STATEMENT";
  String DB_EXECUTION_TYPE_PREPARED_STATEMENT = "PREPARED_STATEMENT";
  String DB_EXECUTION_TYPE_DB_CONFIG = "DB_CONFIG";

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
