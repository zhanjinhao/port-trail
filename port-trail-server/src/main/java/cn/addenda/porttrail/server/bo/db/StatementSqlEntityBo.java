package cn.addenda.porttrail.server.bo.db;

import cn.addenda.porttrail.server.entity.StatementSqlEntity;

public class StatementSqlEntityBo extends StatementSqlEntity {

  public StatementSqlEntityBo(StatementSqlEntity statementSqlEntity) {
    this.setId(statementSqlEntity.getId());
    this.setStatementExecutionId(statementSqlEntity.getStatementExecutionId());
    this.setSql(statementSqlEntity.getSql());
    this.setOrderInStatement(statementSqlEntity.getOrderInStatement());
    this.setOrderInConnection(statementSqlEntity.getOrderInConnection());
    this.setCreator(statementSqlEntity.getCreator());
    this.setCreatorName(statementSqlEntity.getCreatorName());
    this.setCreateDt(statementSqlEntity.getCreateDt());
    this.setModifier(statementSqlEntity.getModifier());
    this.setModifierName(statementSqlEntity.getModifierName());
    this.setModifyDt(statementSqlEntity.getModifyDt());
    this.setDeleteFlag(statementSqlEntity.getDeleteFlag());
    this.setDeleteDt(statementSqlEntity.getDeleteDt());
  }

}
