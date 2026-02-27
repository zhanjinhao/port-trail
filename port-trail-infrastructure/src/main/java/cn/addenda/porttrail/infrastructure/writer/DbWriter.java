package cn.addenda.porttrail.infrastructure.writer;

import cn.addenda.porttrail.common.pojo.db.bo.DbExecution;

public interface DbWriter {

  void writeStatement(DbExecution dbExecution);

  void writePreparedStatement(DbExecution dbExecution);

  void writeDbConfig(DbExecution dbExecution);

}
