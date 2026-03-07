package cn.addenda.porttrail.server.manager;

import cn.addenda.component.transaction.PlatformTransactionHelper;
import cn.addenda.porttrail.server.curd.PortTrailDeserializeThrowableLogCurder;
import cn.addenda.porttrail.server.entity.PortTrailDeserializeThrowableLog;
import cn.addenda.porttrail.server.util.ThrowableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PortTrailDeserializeThrowableLogManagerImpl implements PortTrailDeserializeThrowableLogManager {

  @Autowired
  private PortTrailDeserializeThrowableLogCurder portTrailDeserializeThrowableLogCurder;

  @Autowired
  private PlatformTransactionHelper platformTransactionHelper;

  @Override
  public void insert(byte[] bytes, String deserializeType, Throwable throwable) {
    PortTrailDeserializeThrowableLog param = PortTrailDeserializeThrowableLog.ofParam();
    param.setBytes(bytes);
    param.setDeserializeType(deserializeType);
    param.setThrowableStack(ThrowableUtils.getThrowableStr(throwable));
    platformTransactionHelper.doTransaction(() -> {
      portTrailDeserializeThrowableLogCurder.insert(param);
    });
  }

}
