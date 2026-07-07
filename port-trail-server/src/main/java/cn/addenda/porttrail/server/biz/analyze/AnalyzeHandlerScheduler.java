package cn.addenda.porttrail.server.biz.analyze;

import cn.addenda.component.transaction.PlatformTransactionHelper;
import cn.addenda.porttrail.server.bo.db.analyze.param.AnalyzePreparedStatementExecutionParam;
import cn.addenda.porttrail.server.bo.db.analyze.param.AnalyzeStatementExecutionParam;
import cn.addenda.porttrail.server.bo.db.analyze.result.AnalyzeResult;
import cn.addenda.porttrail.server.manager.PreparedStatementExecutionEntityManager;
import cn.addenda.porttrail.server.manager.StatementExecutionEntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class AnalyzeHandlerScheduler {

  @Autowired
  private List<AnalyzeHandler<?>> analyzeHandlerList;

  @Autowired
  private PlatformTransactionHelper platformTransactionHelper;

  @Autowired
  private StatementExecutionEntityManager statementExecutionEntityManager;

  @Autowired
  private PreparedStatementExecutionEntityManager preparedStatementExecutionEntityManager;

  public void handle(AnalyzePreparedStatementExecutionParam analyzeParam) {

    List<AnalyzeResult> analyzeResultList = new ArrayList<>();
    for (AnalyzeHandler<?> analyzeHandler : analyzeHandlerList) {
      Optional.ofNullable(analyzeHandler.handle(analyzeParam))
              .ifPresent(analyzeResultList::add);
    }

    platformTransactionHelper.doTransaction(() -> {
      for (AnalyzeResult analyzeResult : analyzeResultList) {
        for (AnalyzeHandler<?> analyzeHandler : analyzeHandlerList) {
          if (analyzeHandler.canConsume(analyzeResult)) {
            analyzeHandler.consume(analyzeResult);
          }
        }
      }
      preparedStatementExecutionEntityManager.markStatementExecutionAnalyzed(analyzeParam.getPreparedStatementExecutionEntityBo().getId());
    });
  }

  public void handle(AnalyzeStatementExecutionParam analyzeParam) {

    List<AnalyzeResult> analyzeResultList = new ArrayList<>();
    for (AnalyzeHandler<?> analyzeHandler : analyzeHandlerList) {
      Optional.ofNullable(analyzeHandler.handle(analyzeParam))
              .ifPresent(analyzeResultList::add);
    }

    platformTransactionHelper.doTransaction(() -> {
      for (AnalyzeResult analyzeResult : analyzeResultList) {
        for (AnalyzeHandler<?> analyzeHandler : analyzeHandlerList) {
          if (analyzeHandler.canConsume(analyzeResult)) {
            analyzeHandler.consume(analyzeResult);
          }
        }
      }
      statementExecutionEntityManager.markStatementExecutionAnalyzed(analyzeParam.getStatementExecutionEntityBo().getId());
    });

  }

}
