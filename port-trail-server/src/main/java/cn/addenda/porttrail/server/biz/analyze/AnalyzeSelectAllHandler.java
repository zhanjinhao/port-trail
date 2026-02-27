package cn.addenda.porttrail.server.biz.analyze;

import cn.addenda.porttrail.common.pojo.db.dto.PreparedStatementExecutionDto;
import cn.addenda.porttrail.server.bo.analyze.param.AnalyzePreparedStatementExecutionParam;
import cn.addenda.porttrail.server.bo.analyze.param.AnalyzeStatementExecutionParam;
import cn.addenda.porttrail.server.bo.analyze.result.AnalyzeResult;
import cn.addenda.porttrail.server.bo.analyze.result.AnalyzeSelectAllResult;
import cn.addenda.porttrail.server.bo.est.EstStatementSqlBo;
import cn.addenda.porttrail.server.curd.EstAnalyzeSelectAllResultCurder;
import cn.addenda.porttrail.server.entity.EstAnalyzeSelectAllResult;
import cn.addenda.porttrail.server.helper.SqlHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class AnalyzeSelectAllHandler implements AnalyzeHandler<AnalyzeSelectAllResult> {

  @Autowired
  private SqlHelper sqlHelper;

  @Autowired
  private EstAnalyzeSelectAllResultCurder estAnalyzeSelectAllResultCurder;

  @Override
  public String handlerName() {
    return "AnalyzeSelectAllHandler";
  }

  @Override
  public AnalyzeSelectAllResult handle(AnalyzePreparedStatementExecutionParam analyzeParam) {
    PreparedStatementExecutionDto preparedStatementExecutionDto = analyzeParam.getPreparedStatementExecutionDto();
    String parameterizedSql = preparedStatementExecutionDto.getParameterizedSql();

    AnalyzeSelectAllResult analyzeSelectAllResult = new AnalyzeSelectAllResult();
    analyzeSelectAllResult.setSource(AnalyzeResult.SOURCE_PREPARED_STATEMENT_EXECUTION);

    boolean b = sqlHelper.checkIfHasSelectAll(parameterizedSql);

    // todo 后续可以加一个开关，没有select all的也落库
    if (b) {
      AnalyzeSelectAllResult.AnalyzeSelectAllSingleResult analyzeSelectAllSingleResult = analyzeSelectAllResult.new AnalyzeSelectAllSingleResult();
      analyzeSelectAllSingleResult.setOuterId(analyzeParam.getEstPreparedStatementExecutionBo().getId());
      analyzeSelectAllSingleResult.setIfSelectAll(1);
      analyzeSelectAllResult.getAnalyzeSelectAllSingleResultList().add(analyzeSelectAllSingleResult);
    }

    return analyzeSelectAllResult;
  }

  @Override
  public AnalyzeSelectAllResult handle(AnalyzeStatementExecutionParam analyzeParam) {
    AnalyzeSelectAllResult analyzeSelectAllResult = new AnalyzeSelectAllResult();
    analyzeSelectAllResult.setSource(AnalyzeResult.SOURCE_STATEMENT_SQL);

    for (EstStatementSqlBo estStatementSqlBo : analyzeParam.getEstStatementExecutionBo().getEstStatementSqlBoList()) {
      boolean b = sqlHelper.checkIfHasSelectAll(estStatementSqlBo.getSql());
      // todo 后续可以加一个开关，没有select all的也落库
      if (b) {
        AnalyzeSelectAllResult.AnalyzeSelectAllSingleResult analyzeSelectAllSingleResult = analyzeSelectAllResult.new AnalyzeSelectAllSingleResult();
        analyzeSelectAllSingleResult.setOuterId(estStatementSqlBo.getId());
        analyzeSelectAllSingleResult.setIfSelectAll(1);
        analyzeSelectAllResult.getAnalyzeSelectAllSingleResultList().add(analyzeSelectAllSingleResult);
      }
    }

    return analyzeSelectAllResult;
  }

  @Override
  public boolean canConsume(AnalyzeResult analyzeResult) {
    return Objects.equals(AnalyzeSelectAllResult.class, analyzeResult.getClass());
  }

  @Override
  public void consume(AnalyzeResult analyzeResult) {
    AnalyzeSelectAllResult analyzeSelectAllResult = (AnalyzeSelectAllResult) analyzeResult;

    List<EstAnalyzeSelectAllResult> estAnalyzeSelectAllResultList = new ArrayList<>();
    List<AnalyzeSelectAllResult.AnalyzeSelectAllSingleResult> analyzeSelectAllSingleResultList =
            analyzeSelectAllResult.getAnalyzeSelectAllSingleResultList();

    for (AnalyzeSelectAllResult.AnalyzeSelectAllSingleResult analyzeSelectAllSingleResult : analyzeSelectAllSingleResultList) {
      EstAnalyzeSelectAllResult estAnalyzeSelectAllResult = new EstAnalyzeSelectAllResult();
      estAnalyzeSelectAllResult.setSource(analyzeSelectAllResult.getSource());
      estAnalyzeSelectAllResult.setOuterId(analyzeSelectAllSingleResult.getOuterId());
      estAnalyzeSelectAllResult.setIfSelectAll(analyzeSelectAllSingleResult.getIfSelectAll());
      estAnalyzeSelectAllResultList.add(estAnalyzeSelectAllResult);
    }

    estAnalyzeSelectAllResultCurder.batchInsert(estAnalyzeSelectAllResultList);
  }

}
