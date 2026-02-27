package cn.addenda.porttrail.server.bo.est;

import cn.addenda.porttrail.server.entity.EstPreparedStatementParameter;

public class EstPreparedStatementParameterBo extends EstPreparedStatementParameter {

  public EstPreparedStatementParameterBo(EstPreparedStatementParameter estPreparedStatementParameter) {
    this.setId(estPreparedStatementParameter.getId());
    this.setPreparedStatementExecutionId(estPreparedStatementParameter.getPreparedStatementExecutionId());
    this.setParameterJson(estPreparedStatementParameter.getParameterJson());
    this.setParameterBytes(estPreparedStatementParameter.getParameterBytes());
    this.setCapacity(estPreparedStatementParameter.getCapacity());
    this.setOrderInStatement(estPreparedStatementParameter.getOrderInStatement());
    this.setOrderInConnection(estPreparedStatementParameter.getOrderInConnection());
    this.setCreator(estPreparedStatementParameter.getCreator());
    this.setCreatorName(estPreparedStatementParameter.getCreatorName());
    this.setCreateDt(estPreparedStatementParameter.getCreateDt());
    this.setModifier(estPreparedStatementParameter.getModifier());
    this.setModifierName(estPreparedStatementParameter.getModifierName());
    this.setModifyDt(estPreparedStatementParameter.getModifyDt());
    this.setDeleteFlag(estPreparedStatementParameter.getDeleteFlag());
    this.setDeleteDt(estPreparedStatementParameter.getDeleteDt());
  }

}
