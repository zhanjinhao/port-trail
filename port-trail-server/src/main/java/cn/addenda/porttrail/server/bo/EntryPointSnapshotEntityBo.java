package cn.addenda.porttrail.server.bo;

import cn.addenda.porttrail.server.entity.EntryPointSnapshotEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class EntryPointSnapshotEntityBo extends EntryPointSnapshotEntity {

  private List<EntryPointEntityBo> entryPointEntityBoList;

  public EntryPointSnapshotEntityBo(EntryPointSnapshotEntity entryPointSnapshotEntity) {
    this.setId(entryPointSnapshotEntity.getId());
    this.setThreadName(entryPointSnapshotEntity.getThreadName());
    this.setTraceId(entryPointSnapshotEntity.getTraceId());
    this.setSeqId(entryPointSnapshotEntity.getSeqId());
    this.setCreator(entryPointSnapshotEntity.getCreator());
    this.setCreateDt(entryPointSnapshotEntity.getCreateDt());
    this.setCreatorName(entryPointSnapshotEntity.getCreatorName());
    this.setModifyDt(entryPointSnapshotEntity.getModifyDt());
    this.setModifier(entryPointSnapshotEntity.getModifier());
    this.setModifierName(entryPointSnapshotEntity.getModifierName());
    this.setDeleteDt(entryPointSnapshotEntity.getDeleteDt());
    this.setDeleteFlag(entryPointSnapshotEntity.getDeleteFlag());
  }

}
