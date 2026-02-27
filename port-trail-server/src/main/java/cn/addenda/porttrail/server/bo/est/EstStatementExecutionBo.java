package cn.addenda.porttrail.server.bo.est;

import cn.addenda.porttrail.server.entity.EstStatementExecution;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@ToString
public class EstStatementExecutionBo extends EstStatementExecution {

  private List<EstStatementSqlBo> estStatementSqlBoList;

  private EstEntryPointSnapshotBo estEntryPointSnapshotBo;

  public EstStatementExecutionBo(EstStatementExecution estStatementExecution) {
    this.setId(estStatementExecution.getId());
    this.setSystemCode(estStatementExecution.getSystemCode());
    this.setServiceName(estStatementExecution.getServiceName());
    this.setImageName(estStatementExecution.getImageName());
    this.setEnv(estStatementExecution.getEnv());
    this.setInstanceId(estStatementExecution.getInstanceId());
    this.setDataSourcePortTrailId(estStatementExecution.getDataSourcePortTrailId());
    this.setConnectionPortTrailId(estStatementExecution.getConnectionPortTrailId());
    this.setStatementPortTrailId(estStatementExecution.getStatementPortTrailId());
    this.setStatementState(estStatementExecution.getStatementState());
    this.setTxId(estStatementExecution.getTxId());
    this.setStart(estStatementExecution.getStart());
    this.setEnd(estStatementExecution.getEnd());
    this.setCost(estStatementExecution.getCost());
    this.setEntryPointSnapshotId(estStatementExecution.getEntryPointSnapshotId());
    this.setIfAnalyzed(estStatementExecution.getIfAnalyzed());
    this.setCreator(estStatementExecution.getCreator());
    this.setCreatorName(estStatementExecution.getCreatorName());
    this.setCreateDt(estStatementExecution.getCreateDt());
    this.setModifier(estStatementExecution.getModifier());
    this.setModifierName(estStatementExecution.getModifierName());
    this.setModifyDt(estStatementExecution.getModifyDt());
    this.setDeleteFlag(estStatementExecution.getDeleteFlag());
    this.setDeleteDt(estStatementExecution.getDeleteDt());
  }

}
