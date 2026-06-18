package cn.addenda.porttrail.server.biz;

import cn.addenda.component.transaction.PlatformTransactionHelper;
import cn.addenda.porttrail.common.entrypoint.EntryPointSnapshot;
import cn.addenda.porttrail.server.bo.est.EstEntryPointBo;
import cn.addenda.porttrail.server.bo.est.EstEntryPointSnapshotBo;
import cn.addenda.porttrail.server.curd.EstEntryPointCurder;
import cn.addenda.porttrail.server.curd.EstEntryPointSnapshotCurder;
import cn.addenda.porttrail.server.entity.EstEntryPoint;
import cn.addenda.porttrail.server.entity.EstEntryPointSnapshot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EstEntryPointSnapshotBizImpl implements EstEntryPointSnapshotBiz {

  @Autowired
  private EstEntryPointCurder estEntryPointCurder;

  @Autowired
  private EstEntryPointSnapshotCurder estEntryPointSnapshotCurder;

  @Autowired
  private PlatformTransactionHelper transactionHelperNew;

  @Override
  public EstEntryPointSnapshotBo insert(EntryPointSnapshot entryPointSnapshot) {
    return transactionHelperNew.doTransaction(() -> {
      EstEntryPointSnapshot estEntryPointSnapshot = EstEntryPointSnapshot.ofParam();
      estEntryPointSnapshot.setThreadName(entryPointSnapshot.getThreadName());
      estEntryPointSnapshotCurder.insert(estEntryPointSnapshot);

      List<EstEntryPoint> estEntryPointList = entryPointSnapshot.getEntryPointList()
              .stream().map(entryPoint -> {
                EstEntryPoint estEntryPoint = EstEntryPoint.ofParam();
                estEntryPoint.setEntryPointType(entryPoint.getEntryPointType().toString());
                estEntryPoint.setDetail(entryPoint.getDetail());
                estEntryPoint.setEntryId(entryPoint.getEntryId());
                estEntryPoint.setEntryPointSnapshotId(estEntryPointSnapshot.getId());
                return estEntryPoint;
              }).collect(Collectors.toList());
      estEntryPointCurder.batchInsert(estEntryPointList);

      List<EstEntryPointBo> estEntryPointBoList = estEntryPointList.stream()
              .map(EstEntryPointBo::new)
              .collect(Collectors.toList());
      EstEntryPointSnapshotBo estEntryPointSnapshotBo = new EstEntryPointSnapshotBo(estEntryPointSnapshot);
      estEntryPointSnapshotBo.setEstEntryPointBoList(estEntryPointBoList);

      return estEntryPointSnapshotBo;
    });
  }

}
