package cn.addenda.porttrail.server.bo.est;

import cn.addenda.porttrail.server.entity.EstStatementSql;

public class EstStatementSqlBo extends EstStatementSql {

  public EstStatementSqlBo(EstStatementSql estStatementSql) {
    this.setId(estStatementSql.getId());
    this.setStatementExecutionId(estStatementSql.getStatementExecutionId());
    this.setSql(estStatementSql.getSql());
    this.setOrderInStatement(estStatementSql.getOrderInStatement());
    this.setOrderInConnection(estStatementSql.getOrderInConnection());
    this.setCreator(estStatementSql.getCreator());
    this.setCreatorName(estStatementSql.getCreatorName());
    this.setCreateDt(estStatementSql.getCreateDt());
    this.setModifier(estStatementSql.getModifier());
    this.setModifierName(estStatementSql.getModifierName());
    this.setModifyDt(estStatementSql.getModifyDt());
    this.setDeleteFlag(estStatementSql.getDeleteFlag());
    this.setDeleteDt(estStatementSql.getDeleteDt());
  }

}
