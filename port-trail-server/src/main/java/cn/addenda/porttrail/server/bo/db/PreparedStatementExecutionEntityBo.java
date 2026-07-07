package cn.addenda.porttrail.server.bo.db;

import cn.addenda.porttrail.server.bo.EntryPointSnapshotEntityBo;
import cn.addenda.porttrail.server.entity.PreparedStatementExecutionEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@ToString
public class PreparedStatementExecutionEntityBo extends PreparedStatementExecutionEntity {

  private EntryPointSnapshotEntityBo entryPointSnapshotEntityBo;

  private List<PreparedStatementParameterEntityBo> preparedStatementParameterEntityBoList;

  public PreparedStatementExecutionEntityBo(PreparedStatementExecutionEntity preparedStatementExecutionEntity) {
    this.setId(preparedStatementExecutionEntity.getId());
    this.setSystemCode(preparedStatementExecutionEntity.getSystemCode());
    this.setServiceName(preparedStatementExecutionEntity.getServiceName());
    this.setImageName(preparedStatementExecutionEntity.getImageName());
    this.setEnv(preparedStatementExecutionEntity.getEnv());
    this.setInstanceId(preparedStatementExecutionEntity.getInstanceId());
    this.setDataSourcePortTrailId(preparedStatementExecutionEntity.getDataSourcePortTrailId());
    this.setConnectionPortTrailId(preparedStatementExecutionEntity.getConnectionPortTrailId());
    this.setStatementPortTrailId(preparedStatementExecutionEntity.getStatementPortTrailId());
    this.setParameterizedSql(preparedStatementExecutionEntity.getParameterizedSql());
    this.setStatementState(preparedStatementExecutionEntity.getStatementState());
    this.setTxId(preparedStatementExecutionEntity.getTxId());
    this.setStart(preparedStatementExecutionEntity.getStart());
    this.setEnd(preparedStatementExecutionEntity.getEnd());
    this.setCost(preparedStatementExecutionEntity.getCost());
    this.setEntryPointSnapshotId(preparedStatementExecutionEntity.getEntryPointSnapshotId());
    this.setIfAnalyzed(preparedStatementExecutionEntity.getIfAnalyzed());
    this.setCreator(preparedStatementExecutionEntity.getCreator());
    this.setCreatorName(preparedStatementExecutionEntity.getCreatorName());
    this.setCreateDt(preparedStatementExecutionEntity.getCreateDt());
    this.setModifier(preparedStatementExecutionEntity.getModifier());
    this.setModifierName(preparedStatementExecutionEntity.getModifierName());
    this.setModifyDt(preparedStatementExecutionEntity.getModifyDt());
    this.setDeleteFlag(preparedStatementExecutionEntity.getDeleteFlag());
    this.setDeleteDt(preparedStatementExecutionEntity.getDeleteDt());
  }
}
