package cn.addenda.porttrail.server.bo.est;

import cn.addenda.porttrail.server.entity.EstEntryPointSnapshot;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class EstEntryPointSnapshotBo extends EstEntryPointSnapshot {

  private List<EstEntryPointBo> estEntryPointBoList;

  public EstEntryPointSnapshotBo(EstEntryPointSnapshot estEntryPointSnapshot) {
    this.setId(estEntryPointSnapshot.getId());
    this.setThreadName(estEntryPointSnapshot.getThreadName());
    this.setCreator(estEntryPointSnapshot.getCreator());
    this.setCreateDt(estEntryPointSnapshot.getCreateDt());
    this.setCreatorName(estEntryPointSnapshot.getCreatorName());
    this.setModifyDt(estEntryPointSnapshot.getModifyDt());
    this.setModifier(estEntryPointSnapshot.getModifier());
    this.setModifierName(estEntryPointSnapshot.getModifierName());
    this.setDeleteDt(estEntryPointSnapshot.getDeleteDt());
    this.setDeleteFlag(estEntryPointSnapshot.getDeleteFlag());
  }

}
