package cn.addenda.porttrail.server.biz;

import cn.addenda.porttrail.common.entrypoint.EntryPointSnapshot;
import cn.addenda.porttrail.server.bo.EntryPointSnapshotEntityBo;

public interface EntryPointSnapshotEntityBiz {

  EntryPointSnapshotEntityBo insert(EntryPointSnapshot entryPointSnapshot);

}
