package cn.addenda.porttrail.server.bo.db;

import cn.addenda.porttrail.server.entity.PreparedStatementParameterEntity;

public class PreparedStatementParameterEntityBo extends PreparedStatementParameterEntity {

  public PreparedStatementParameterEntityBo(PreparedStatementParameterEntity preparedStatementParameterEntity) {
    this.setId(preparedStatementParameterEntity.getId());
    this.setPreparedStatementExecutionId(preparedStatementParameterEntity.getPreparedStatementExecutionId());
    this.setParameterJson(preparedStatementParameterEntity.getParameterJson());
    this.setParameterBytes(preparedStatementParameterEntity.getParameterBytes());
    this.setCapacity(preparedStatementParameterEntity.getCapacity());
    this.setOrderInStatement(preparedStatementParameterEntity.getOrderInStatement());
    this.setOrderInConnection(preparedStatementParameterEntity.getOrderInConnection());
    this.setCreator(preparedStatementParameterEntity.getCreator());
    this.setCreatorName(preparedStatementParameterEntity.getCreatorName());
    this.setCreateDt(preparedStatementParameterEntity.getCreateDt());
    this.setModifier(preparedStatementParameterEntity.getModifier());
    this.setModifierName(preparedStatementParameterEntity.getModifierName());
    this.setModifyDt(preparedStatementParameterEntity.getModifyDt());
    this.setDeleteFlag(preparedStatementParameterEntity.getDeleteFlag());
    this.setDeleteDt(preparedStatementParameterEntity.getDeleteDt());
  }

}
