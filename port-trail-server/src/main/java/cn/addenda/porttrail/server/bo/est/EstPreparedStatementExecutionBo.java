package cn.addenda.porttrail.server.bo.est;

import cn.addenda.porttrail.server.entity.EstPreparedStatementExecution;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@ToString
public class EstPreparedStatementExecutionBo extends EstPreparedStatementExecution {

  private EstEntryPointSnapshotBo estEntryPointSnapshotBo;

  private List<EstPreparedStatementParameterBo> estPreparedStatementParameterBoList;

  public EstPreparedStatementExecutionBo(EstPreparedStatementExecution estPreparedStatementExecution) {
    this.setId(estPreparedStatementExecution.getId());
    this.setSystemCode(estPreparedStatementExecution.getSystemCode());
    this.setServiceName(estPreparedStatementExecution.getServiceName());
    this.setImageName(estPreparedStatementExecution.getImageName());
    this.setEnv(estPreparedStatementExecution.getEnv());
    this.setInstanceId(estPreparedStatementExecution.getInstanceId());
    this.setDataSourcePortTrailId(estPreparedStatementExecution.getDataSourcePortTrailId());
    this.setConnectionPortTrailId(estPreparedStatementExecution.getConnectionPortTrailId());
    this.setStatementPortTrailId(estPreparedStatementExecution.getStatementPortTrailId());
    this.setParameterizedSql(estPreparedStatementExecution.getParameterizedSql());
    this.setStatementState(estPreparedStatementExecution.getStatementState());
    this.setTxId(estPreparedStatementExecution.getTxId());
    this.setStart(estPreparedStatementExecution.getStart());
    this.setEnd(estPreparedStatementExecution.getEnd());
    this.setCost(estPreparedStatementExecution.getCost());
    this.setEntryPointSnapshotId(estPreparedStatementExecution.getEntryPointSnapshotId());
    this.setIfAnalyzed(estPreparedStatementExecution.getIfAnalyzed());
    this.setCreator(estPreparedStatementExecution.getCreator());
    this.setCreatorName(estPreparedStatementExecution.getCreatorName());
    this.setCreateDt(estPreparedStatementExecution.getCreateDt());
    this.setModifier(estPreparedStatementExecution.getModifier());
    this.setModifierName(estPreparedStatementExecution.getModifierName());
    this.setModifyDt(estPreparedStatementExecution.getModifyDt());
    this.setDeleteFlag(estPreparedStatementExecution.getDeleteFlag());
    this.setDeleteDt(estPreparedStatementExecution.getDeleteDt());
  }
}
