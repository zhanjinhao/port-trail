package cn.addenda.porttrail.server.bo.est;

import cn.addenda.porttrail.server.entity.EstEntryPoint;

public class EstEntryPointBo extends EstEntryPoint {

  public EstEntryPointBo(EstEntryPoint estEntryPoint) {
    this.setId(estEntryPoint.getId());
    this.setEntryPointType(estEntryPoint.getEntryPointType());
    this.setDetail(estEntryPoint.getDetail());
    this.setEntryId(estEntryPoint.getEntryId());
    this.setEntryPointSnapshotId(estEntryPoint.getEntryPointSnapshotId());
    this.setCreator(estEntryPoint.getCreator());
    this.setCreatorName(estEntryPoint.getCreatorName());
    this.setCreateDt(estEntryPoint.getCreateDt());
    this.setModifier(estEntryPoint.getModifier());
    this.setModifierName(estEntryPoint.getModifierName());
    this.setModifyDt(estEntryPoint.getModifyDt());
    this.setDeleteFlag(estEntryPoint.getDeleteFlag());
    this.setDeleteDt(estEntryPoint.getDeleteDt());
  }

}
