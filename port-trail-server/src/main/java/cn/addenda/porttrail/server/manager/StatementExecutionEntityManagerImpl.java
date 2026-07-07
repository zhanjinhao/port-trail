package cn.addenda.porttrail.server.manager;

import cn.addenda.component.transaction.PlatformTransactionHelper;
import cn.addenda.porttrail.server.curd.StatementExecutionEntityCurder;
import cn.addenda.porttrail.server.entity.StatementExecutionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StatementExecutionEntityManagerImpl implements StatementExecutionEntityManager {

  @Autowired
  private StatementExecutionEntityCurder statementExecutionEntityCurder;

  @Autowired
  private PlatformTransactionHelper platformTransactionHelper;

  @Override
  public void markStatementExecutionAnalyzed(Long id) {
    StatementExecutionEntity param = StatementExecutionEntity.ofParam();
    param.setId(id);
    param.setIfAnalyzed(1);
    platformTransactionHelper.doTransaction(() -> {
      statementExecutionEntityCurder.updateById(param);
    });
  }

}
