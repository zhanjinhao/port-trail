package cn.addenda.porttrail.server.bo.db;

import cn.addenda.porttrail.server.bo.EntryPointSnapshotEntityBo;
import cn.addenda.porttrail.server.entity.StatementExecutionEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@ToString
public class StatementExecutionEntityBo extends StatementExecutionEntity {

  private List<StatementSqlEntityBo> statementSqlEntityBoList;

  private EntryPointSnapshotEntityBo entryPointSnapshotEntityBo;

  public StatementExecutionEntityBo(StatementExecutionEntity statementExecutionEntity) {
    this.setId(statementExecutionEntity.getId());
    this.setSystemCode(statementExecutionEntity.getSystemCode());
    this.setServiceName(statementExecutionEntity.getServiceName());
    this.setImageName(statementExecutionEntity.getImageName());
    this.setEnv(statementExecutionEntity.getEnv());
    this.setInstanceId(statementExecutionEntity.getInstanceId());
    this.setDataSourcePortTrailId(statementExecutionEntity.getDataSourcePortTrailId());
    this.setConnectionPortTrailId(statementExecutionEntity.getConnectionPortTrailId());
    this.setStatementPortTrailId(statementExecutionEntity.getStatementPortTrailId());
    this.setStatementState(statementExecutionEntity.getStatementState());
    this.setTxId(statementExecutionEntity.getTxId());
    this.setStart(statementExecutionEntity.getStart());
    this.setEnd(statementExecutionEntity.getEnd());
    this.setCost(statementExecutionEntity.getCost());
    this.setEntryPointSnapshotId(statementExecutionEntity.getEntryPointSnapshotId());
    this.setIfAnalyzed(statementExecutionEntity.getIfAnalyzed());
    this.setCreator(statementExecutionEntity.getCreator());
    this.setCreatorName(statementExecutionEntity.getCreatorName());
    this.setCreateDt(statementExecutionEntity.getCreateDt());
    this.setModifier(statementExecutionEntity.getModifier());
    this.setModifierName(statementExecutionEntity.getModifierName());
    this.setModifyDt(statementExecutionEntity.getModifyDt());
    this.setDeleteFlag(statementExecutionEntity.getDeleteFlag());
    this.setDeleteDt(statementExecutionEntity.getDeleteDt());
  }

}
