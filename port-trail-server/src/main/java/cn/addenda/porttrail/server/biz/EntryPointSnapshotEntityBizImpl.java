package cn.addenda.porttrail.server.biz;

import cn.addenda.component.transaction.PlatformTransactionHelper;
import cn.addenda.porttrail.common.entrypoint.EntryPointSnapshot;
import cn.addenda.porttrail.server.bo.EntryPointEntityBo;
import cn.addenda.porttrail.server.bo.EntryPointSnapshotEntityBo;
import cn.addenda.porttrail.server.curd.EntryPointEntityCurder;
import cn.addenda.porttrail.server.curd.EntryPointSnapshotEntityCurder;
import cn.addenda.porttrail.server.entity.EntryPointEntity;
import cn.addenda.porttrail.server.entity.EntryPointSnapshotEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EntryPointSnapshotEntityBizImpl implements EntryPointSnapshotEntityBiz {

  @Autowired
  private EntryPointEntityCurder entryPointEntityCurder;

  @Autowired
  private EntryPointSnapshotEntityCurder entryPointSnapshotEntityCurder;

  @Autowired
  private PlatformTransactionHelper transactionHelperNew;

  @Override
  public EntryPointSnapshotEntityBo insert(EntryPointSnapshot entryPointSnapshot) {
    return transactionHelperNew.doTransaction(() -> {
      EntryPointSnapshotEntity entryPointSnapshotEntity = EntryPointSnapshotEntity.ofParam();
      entryPointSnapshotEntity.setThreadName(entryPointSnapshot.getThreadName());
      entryPointSnapshotEntity.setTraceId(entryPointSnapshot.getTraceId());
      entryPointSnapshotEntity.setSeqId(entryPointSnapshot.getSeqId());
      entryPointSnapshotEntityCurder.insert(entryPointSnapshotEntity);

      List<EntryPointEntity> entryPointEntityList = entryPointSnapshot.getEntryPointList()
              .stream().map(entryPoint -> {
                EntryPointEntity entryPointEntity = EntryPointEntity.ofParam();
                entryPointEntity.setEntryPointType(entryPoint.getEntryPointType().toString());
                entryPointEntity.setDetail(entryPoint.getDetail());
                entryPointEntity.setEntryId(entryPoint.getEntryId());
                entryPointEntity.setEntryPointSnapshotId(entryPointSnapshotEntity.getId());
                return entryPointEntity;
              }).collect(Collectors.toList());
      entryPointEntityCurder.batchInsert(entryPointEntityList);

      List<EntryPointEntityBo> entryPointEntityBoList = entryPointEntityList.stream()
              .map(EntryPointEntityBo::new)
              .collect(Collectors.toList());
      EntryPointSnapshotEntityBo entryPointSnapshotEntityBo = new EntryPointSnapshotEntityBo(entryPointSnapshotEntity);
      entryPointSnapshotEntityBo.setEntryPointEntityBoList(entryPointEntityBoList);

      return entryPointSnapshotEntityBo;
    });
  }

}
