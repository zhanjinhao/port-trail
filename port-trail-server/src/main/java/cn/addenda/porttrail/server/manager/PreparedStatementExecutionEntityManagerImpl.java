package cn.addenda.porttrail.server.manager;

import cn.addenda.component.transaction.PlatformTransactionHelper;
import cn.addenda.porttrail.server.curd.PreparedStatementExecutionEntityCurder;
import cn.addenda.porttrail.server.entity.PreparedStatementExecutionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PreparedStatementExecutionEntityManagerImpl implements PreparedStatementExecutionEntityManager {

  @Autowired
  private PreparedStatementExecutionEntityCurder preparedStatementExecutionEntityCurder;

  @Autowired
  private PlatformTransactionHelper platformTransactionHelper;

  @Override
  public void markStatementExecutionAnalyzed(Long id) {
    PreparedStatementExecutionEntity param = PreparedStatementExecutionEntity.ofParam();
    param.setId(id);
    param.setIfAnalyzed(1);
    platformTransactionHelper.doTransaction(() -> {
      preparedStatementExecutionEntityCurder.updateById(param);
    });
  }

}
