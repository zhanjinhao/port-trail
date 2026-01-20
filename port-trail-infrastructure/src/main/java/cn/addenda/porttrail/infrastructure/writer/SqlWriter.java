package cn.addenda.porttrail.infrastructure.writer;

import cn.addenda.porttrail.common.pojo.db.DbExecution;

public interface SqlWriter {

  void writeSql(DbExecution dbExecution);

  void writePreparedSql(DbExecution dbExecution);

  void writeConfig(DbExecution dbExecution);

}
