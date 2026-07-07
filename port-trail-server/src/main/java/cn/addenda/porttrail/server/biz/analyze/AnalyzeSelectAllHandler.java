package cn.addenda.porttrail.server.biz.analyze;

import cn.addenda.porttrail.common.pojo.db.dto.PreparedStatementExecutionDto;
import cn.addenda.porttrail.server.bo.db.analyze.param.AnalyzePreparedStatementExecutionParam;
import cn.addenda.porttrail.server.bo.db.analyze.param.AnalyzeStatementExecutionParam;
import cn.addenda.porttrail.server.bo.db.analyze.result.AnalyzeResult;
import cn.addenda.porttrail.server.bo.db.analyze.result.AnalyzeSelectAllResult;
import cn.addenda.porttrail.server.bo.db.StatementSqlEntityBo;
import cn.addenda.porttrail.server.curd.AnalyzeSelectAllResultEntityCurder;
import cn.addenda.porttrail.server.entity.AnalyzeSelectAllResultEntity;
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
  private AnalyzeSelectAllResultEntityCurder analyzeSelectAllResultEntityCurder;

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
      analyzeSelectAllSingleResult.setOuterId(analyzeParam.getPreparedStatementExecutionEntityBo().getId());
      analyzeSelectAllSingleResult.setIfSelectAll(1);
      analyzeSelectAllResult.getAnalyzeSelectAllSingleResultList().add(analyzeSelectAllSingleResult);
    }

    return analyzeSelectAllResult;
  }

  @Override
  public AnalyzeSelectAllResult handle(AnalyzeStatementExecutionParam analyzeParam) {
    AnalyzeSelectAllResult analyzeSelectAllResult = new AnalyzeSelectAllResult();
    analyzeSelectAllResult.setSource(AnalyzeResult.SOURCE_STATEMENT_SQL);

    for (StatementSqlEntityBo statementSqlEntityBo : analyzeParam.getStatementExecutionEntityBo().getStatementSqlEntityBoList()) {
      boolean b = sqlHelper.checkIfHasSelectAll(statementSqlEntityBo.getSql());
      // todo 后续可以加一个开关，没有select all的也落库
      if (b) {
        AnalyzeSelectAllResult.AnalyzeSelectAllSingleResult analyzeSelectAllSingleResult = analyzeSelectAllResult.new AnalyzeSelectAllSingleResult();
        analyzeSelectAllSingleResult.setOuterId(statementSqlEntityBo.getId());
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

    List<AnalyzeSelectAllResultEntity> analyzeSelectAllResultEntityList = new ArrayList<>();
    List<AnalyzeSelectAllResult.AnalyzeSelectAllSingleResult> analyzeSelectAllSingleResultList =
            analyzeSelectAllResult.getAnalyzeSelectAllSingleResultList();

    for (AnalyzeSelectAllResult.AnalyzeSelectAllSingleResult analyzeSelectAllSingleResult : analyzeSelectAllSingleResultList) {
      AnalyzeSelectAllResultEntity analyzeSelectAllResultEntity = new AnalyzeSelectAllResultEntity();
      analyzeSelectAllResultEntity.setSource(analyzeSelectAllResult.getSource());
      analyzeSelectAllResultEntity.setOuterId(analyzeSelectAllSingleResult.getOuterId());
      analyzeSelectAllResultEntity.setIfSelectAll(analyzeSelectAllSingleResult.getIfSelectAll());
      analyzeSelectAllResultEntityList.add(analyzeSelectAllResultEntity);
    }

    analyzeSelectAllResultEntityCurder.batchInsert(analyzeSelectAllResultEntityList);
  }

}
