package cn.addenda.porttrail.server.biz.analyze;

import cn.addenda.porttrail.common.pojo.db.dto.PreparedStatementExecutionDto;
import cn.addenda.porttrail.common.util.SqlUtils;
import cn.addenda.porttrail.server.bo.db.analyze.param.AnalyzePreparedStatementExecutionParam;
import cn.addenda.porttrail.server.bo.db.analyze.param.AnalyzeStatementExecutionParam;
import cn.addenda.porttrail.server.bo.db.analyze.result.AnalyzeResult;
import cn.addenda.porttrail.server.bo.db.analyze.result.AnalyzeTableNameResult;
import cn.addenda.porttrail.server.bo.db.StatementSqlEntityBo;
import cn.addenda.porttrail.server.curd.AnalyzeTableNameResultEntityCurder;
import cn.addenda.porttrail.server.entity.AnalyzeTableNameResultEntity;
import cn.addenda.porttrail.server.helper.SqlHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class AnalyzeTableNameHandler implements AnalyzeHandler<AnalyzeTableNameResult> {

  @Autowired
  private SqlHelper sqlHelper;

  @Autowired
  private AnalyzeTableNameResultEntityCurder analyzeTableNameResultEntityCurder;

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
    analyzeTableNameSingleResult.setOuterId(analyzeParam.getPreparedStatementExecutionEntityBo().getId());
    analyzeTableNameSingleResult.setTableNames(Optional.ofNullable(tableNameSet).map(a -> String.join(",", a)).orElse(null));
    analyzeTableNameSingleResult.setTableCount(Optional.ofNullable(tableNameSet).map(Set::size).orElse(null));

    analyzeTableNameResult.getAnalyzeTableNameSingleResultList().add(analyzeTableNameSingleResult);

    return analyzeTableNameResult;
  }

  @Override
  public AnalyzeTableNameResult handle(AnalyzeStatementExecutionParam analyzeParam) {
    AnalyzeTableNameResult analyzeTableNameResult = new AnalyzeTableNameResult();
    analyzeTableNameResult.setSource(AnalyzeResult.SOURCE_STATEMENT_SQL);

    for (StatementSqlEntityBo statementSqlEntityBo : analyzeParam.getStatementExecutionEntityBo().getStatementSqlEntityBoList()) {
      Set<String> tableNameSet = sqlHelper.getTableNameSet(statementSqlEntityBo.getSql());
      AnalyzeTableNameResult.AnalyzeTableNameSingleResult analyzeTableNameSingleResult = analyzeTableNameResult.new AnalyzeTableNameSingleResult();
      analyzeTableNameSingleResult.setSqlType(SqlUtils.getSqlType(statementSqlEntityBo.getSql()));
      analyzeTableNameSingleResult.setOuterId(statementSqlEntityBo.getId());
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

    List<AnalyzeTableNameResultEntity> analyzeTableNameResultEntityList = new ArrayList<>();
    List<AnalyzeTableNameResult.AnalyzeTableNameSingleResult> analyzeTableNameSingleResultList =
            analyzeTableNameResult.getAnalyzeTableNameSingleResultList();

    for (AnalyzeTableNameResult.AnalyzeTableNameSingleResult analyzeTableNameSingleResult : analyzeTableNameSingleResultList) {
      AnalyzeTableNameResultEntity analyzeTableNameResultEntity = new AnalyzeTableNameResultEntity();
      analyzeTableNameResultEntity.setSource(analyzeTableNameResult.getSource());
      analyzeTableNameResultEntity.setSqlType(analyzeTableNameSingleResult.getSqlType());
      analyzeTableNameResultEntity.setOuterId(analyzeTableNameSingleResult.getOuterId());
      analyzeTableNameResultEntity.setTableNames(analyzeTableNameSingleResult.getTableNames());
      analyzeTableNameResultEntity.setTableCount(analyzeTableNameSingleResult.getTableCount());
      analyzeTableNameResultEntityList.add(analyzeTableNameResultEntity);
    }

    analyzeTableNameResultEntityCurder.batchInsert(analyzeTableNameResultEntityList);
  }

}
