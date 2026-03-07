package cn.addenda.porttrail.server.manager;

import cn.addenda.component.transaction.PlatformTransactionHelper;
import cn.addenda.porttrail.server.curd.EstStatementExecutionCurder;
import cn.addenda.porttrail.server.entity.EstStatementExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EstStatementExecutionManagerImpl implements EstStatementExecutionManager {

  @Autowired
  private EstStatementExecutionCurder estStatementExecutionCurder;

  @Autowired
  private PlatformTransactionHelper platformTransactionHelper;

  @Override
  public void markStatementExecutionAnalyzed(Long id) {
    EstStatementExecution param = EstStatementExecution.ofParam();
    param.setId(id);
    param.setIfAnalyzed(1);
    platformTransactionHelper.doTransaction(() -> {
      estStatementExecutionCurder.updateById(param);
    });
  }

}
