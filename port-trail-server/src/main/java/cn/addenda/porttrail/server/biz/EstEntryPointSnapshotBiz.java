package cn.addenda.porttrail.server.biz;

import cn.addenda.porttrail.common.entrypoint.EntryPointSnapshot;
import cn.addenda.porttrail.server.bo.est.EstEntryPointSnapshotBo;

public interface EstEntryPointSnapshotBiz {

  EstEntryPointSnapshotBo insert(EntryPointSnapshot entryPointSnapshot);

}
