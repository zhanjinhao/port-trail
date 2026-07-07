package cn.addenda.porttrail.server.bo;

import cn.addenda.porttrail.server.entity.EntryPointEntity;

public class EntryPointEntityBo extends EntryPointEntity {

  public EntryPointEntityBo(EntryPointEntity entryPointEntity) {
    this.setId(entryPointEntity.getId());
    this.setEntryPointType(entryPointEntity.getEntryPointType());
    this.setDetail(entryPointEntity.getDetail());
    this.setEntryId(entryPointEntity.getEntryId());
    this.setEntryPointSnapshotId(entryPointEntity.getEntryPointSnapshotId());
    this.setCreator(entryPointEntity.getCreator());
    this.setCreatorName(entryPointEntity.getCreatorName());
    this.setCreateDt(entryPointEntity.getCreateDt());
    this.setModifier(entryPointEntity.getModifier());
    this.setModifierName(entryPointEntity.getModifierName());
    this.setModifyDt(entryPointEntity.getModifyDt());
    this.setDeleteFlag(entryPointEntity.getDeleteFlag());
    this.setDeleteDt(entryPointEntity.getDeleteDt());
  }

}
