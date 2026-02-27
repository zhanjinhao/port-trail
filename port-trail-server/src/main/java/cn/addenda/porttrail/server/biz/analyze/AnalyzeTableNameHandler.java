package cn.addenda.porttrail.server.biz.analyze;

import cn.addenda.porttrail.common.pojo.db.dto.PreparedStatementExecutionDto;
import cn.addenda.porttrail.common.util.SqlUtils;
import cn.addenda.porttrail.server.bo.analyze.param.AnalyzePreparedStatementExecutionParam;
import cn.addenda.porttrail.server.bo.analyze.param.AnalyzeStatementExecutionParam;
import cn.addenda.porttrail.server.bo.analyze.result.AnalyzeResult;
import cn.addenda.porttrail.server.bo.analyze.result.AnalyzeTableNameResult;
import cn.addenda.porttrail.server.bo.est.EstStatementSqlBo;
import cn.addenda.porttrail.server.curd.EstAnalyzeTableNameResultCurder;
import cn.addenda.porttrail.server.entity.EstAnalyzeTableNameResult;
import cn.addenda.porttrail.server.helper.SqlHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class AnalyzeTableNameHandler implements AnalyzeHandler<AnalyzeTableNameResult> {

  @Autowired
  private SqlHelper sqlHelper;

  @Autowired
  private EstAnalyzeTableNameResultCurder estAnalyzeTableNameResultCurder;

  @Override
  public String handlerName() {
    return "AnalyzeTableNameHandler";
  }

  @Override
  public AnalyzeTableNameResult handle(AnalyzePreparedStatementExecutionParam analyzeParam) {
    PreparedStatementExecutionDto preparedStatementExecutionDto = analyzeParam.getPreparedStatementExecutionDto();
    String parameterizedSql = preparedStatementExecutionDto.getParameterizedSql();

    AnalyzeTableNameResult analyzeTableNameResult = new AnalyzeTableNameResult();
    analyzeTableNameResult.setSource(AnalyzeResult.SOURCE_PREPARED_STATEMENT_EXECUTION);

    Set<String> tableNameSet = sqlHelper.getTableNameSet(parameterizedSql);
    AnalyzeTableNameResult.AnalyzeTableNameSingleResult analyzeTableNameSingleResult = analyzeTableNameResult.new AnalyzeTableNameSingleResult();
    analyzeTableNameSingleResult.setSqlType(SqlUtils.getSqlType(parameterizedSql));
    analyzeTableNameSingleResult.setOuterId(analyzeParam.getEstPreparedStatementExecutionBo().getId());
    analyzeTableNameSingleResult.setTableNames(Optional.ofNullable(tableNameSet).map(a -> String.join(",", a)).orElse(null));
    analyzeTableNameSingleResult.setTableCount(Optional.ofNullable(tableNameSet).map(Set::size).orElse(null));

    analyzeTableNameResult.getAnalyzeTableNameSingleResultList().add(analyzeTableNameSingleResult);

    return analyzeTableNameResult;
  }

  @Override
  public AnalyzeTableNameResult handle(AnalyzeStatementExecutionParam analyzeParam) {
    AnalyzeTableNameResult analyzeTableNameResult = new AnalyzeTableNameResult();
    analyzeTableNameResult.setSource(AnalyzeResult.SOURCE_STATEMENT_SQL);

    for (EstStatementSqlBo estStatementSqlBo : analyzeParam.getEstStatementExecutionBo().getEstStatementSqlBoList()) {
      Set<String> tableNameSet = sqlHelper.getTableNameSet(estStatementSqlBo.getSql());
      AnalyzeTableNameResult.AnalyzeTableNameSingleResult analyzeTableNameSingleResult = analyzeTableNameResult.new AnalyzeTableNameSingleResult();
      analyzeTableNameSingleResult.setSqlType(SqlUtils.getSqlType(estStatementSqlBo.getSql()));
      analyzeTableNameSingleResult.setOuterId(estStatementSqlBo.getId());
      analyzeTableNameSingleResult.setTableNames(Optional.ofNullable(tableNameSet).map(a -> String.join(",", a)).orElse(null));
      analyzeTableNameSingleResult.setTableCount(Optional.ofNullable(tableNameSet).map(Set::size).orElse(null));
      analyzeTableNameResult.getAnalyzeTableNameSingleResultList().add(analyzeTableNameSingleResult);
    }

    return analyzeTableNameResult;
  }

  @Override
  public boolean canConsume(AnalyzeResult analyzeResult) {
    return Objects.equals(AnalyzeTableNameResult.class, analyzeResult.getClass());
  }

  @Override
  public void consume(AnalyzeResult analyzeResult) {
    AnalyzeTableNameResult analyzeTableNameResult = (AnalyzeTableNameResult) analyzeResult;

    List<EstAnalyzeTableNameResult> estAnalyzeTableNameResultList = new ArrayList<>();
    List<AnalyzeTableNameResult.AnalyzeTableNameSingleResult> analyzeTableNameSingleResultList =
            analyzeTableNameResult.getAnalyzeTableNameSingleResultList();

    for (AnalyzeTableNameResult.AnalyzeTableNameSingleResult analyzeTableNameSingleResult : analyzeTableNameSingleResultList) {
      EstAnalyzeTableNameResult estAnalyzeTableNameResult = new EstAnalyzeTableNameResult();
      estAnalyzeTableNameResult.setSource(analyzeTableNameResult.getSource());
      estAnalyzeTableNameResult.setSqlType(analyzeTableNameSingleResult.getSqlType());
      estAnalyzeTableNameResult.setOuterId(analyzeTableNameSingleResult.getOuterId());
      estAnalyzeTableNameResult.setTableNames(analyzeTableNameSingleResult.getTableNames());
      estAnalyzeTableNameResult.setTableCount(analyzeTableNameSingleResult.getTableCount());
      estAnalyzeTableNameResultList.add(estAnalyzeTableNameResult);
    }

    estAnalyzeTableNameResultCurder.batchInsert(estAnalyzeTableNameResultList);
  }

}
