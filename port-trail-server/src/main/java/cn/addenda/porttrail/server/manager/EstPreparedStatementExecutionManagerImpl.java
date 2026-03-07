package cn.addenda.porttrail.server.manager;

import cn.addenda.component.transaction.PlatformTransactionHelper;
import cn.addenda.porttrail.server.curd.EstPreparedStatementExecutionCurder;
import cn.addenda.porttrail.server.entity.EstPreparedStatementExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EstPreparedStatementExecutionManagerImpl implements EstPreparedStatementExecutionManager {

  @Autowired
  private EstPreparedStatementExecutionCurder estPreparedStatementExecutionCurder;

  @Autowired
  private PlatformTransactionHelper platformTransactionHelper;

  @Override
  public void markStatementExecutionAnalyzed(Long id) {
    EstPreparedStatementExecution param = EstPreparedStatementExecution.ofParam();
    param.setId(id);
    param.setIfAnalyzed(1);
    platformTransactionHelper.doTransaction(() -> {
      estPreparedStatementExecutionCurder.updateById(param);
    });
  }

}
