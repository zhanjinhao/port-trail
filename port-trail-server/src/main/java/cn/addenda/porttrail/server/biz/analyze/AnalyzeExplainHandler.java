package cn.addenda.porttrail.server.biz.analyze;

import cn.addenda.porttrail.common.util.SqlUtils;
import cn.addenda.porttrail.server.bo.db.analyze.param.AnalyzePreparedStatementExecutionParam;
import cn.addenda.porttrail.server.bo.db.analyze.param.AnalyzeStatementExecutionParam;
import cn.addenda.porttrail.server.bo.db.analyze.result.AnalyzeExplainResult;
import cn.addenda.porttrail.server.bo.db.analyze.result.AnalyzeResult;
import cn.addenda.porttrail.server.bo.db.PreparedStatementExecutionEntityBo;
import cn.addenda.porttrail.server.bo.db.PreparedStatementParameterEntityBo;
import cn.addenda.porttrail.server.curd.AnalyzeExplainResultEntityCurder;
import cn.addenda.porttrail.server.entity.DbConfigEntity;
import cn.addenda.porttrail.server.entity.AnalyzeExplainResultEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class AnalyzeExplainHandler extends AbstractExplainAnalyzeHandler<AnalyzeExplainResult> {

  @Autowired
  private AnalyzeExplainResultEntityCurder analyzeExplainResultEntityCurder;

  @Override
  public String handlerName() {
    return "AnalyzeExplainHandler";
  }

  @Override
  public AnalyzeExplainResult handle(AnalyzePreparedStatementExecutionParam analyzeParam) {
    try {
      return doPreparedHandle(analyzeParam);
    } catch (SQLException e) {
      // todo
      throw new RuntimeException(e);
    }
  }

  private AnalyzeExplainResult doPreparedHandle(AnalyzePreparedStatementExecutionParam analyzeParam) throws SQLException {
    PreparedStatementExecutionEntityBo preparedStatementExecutionEntityBo = analyzeParam.getPreparedStatementExecutionEntityBo();
    String parameterizedSql = preparedStatementExecutionEntityBo.getParameterizedSql();
    if (SqlUtils.SQL_TYPE_INSERT.equals(SqlUtils.getSqlType(parameterizedSql))) {
      return null;
    }

    DbConfigEntity dbConfigEntity = queryByConnectionPortTrailId(preparedStatementExecutionEntityBo.getConnectionPortTrailId());
    if (dbConfigEntity == null) {
      return null;
    }

    try (Connection connection = getConnection(dbConfigEntity)) {
      AnalyzeExplainResult analyzeExplainResult = new AnalyzeExplainResult();
      analyzeExplainResult.setSource(AnalyzeResult.SOURCE_PREPARED_STATEMENT_PARAMETER);
      String sqlType = SqlUtils.getSqlType(parameterizedSql);

      List<PreparedStatementParameterEntityBo> preparedStatementParameterEntityBoList = preparedStatementExecutionEntityBo.getPreparedStatementParameterEntityBoList();
      for (PreparedStatementParameterEntityBo preparedStatementParameterEntityBo : preparedStatementParameterEntityBoList) {
        AnalyzeExplainResult.AnalyzeExplainSqlResult analyzeExplainSqlResult = analyzeExplainResult.new AnalyzeExplainSqlResult();
        analyzeExplainResult.getAnalyzeExplainSqlResultList().add(analyzeExplainSqlResult);

        analyzeExplainSqlResult.setOuterId(preparedStatementParameterEntityBo.getId());
        analyzeExplainSqlResult.setSqlType(sqlType);

        for (ExplainRow explainRow : explainOne(connection, parameterizedSql, preparedStatementParameterEntityBo)) {
          AnalyzeExplainResult.AnalyzeExplainSqlResult.AnalyzeExplainSingleResult analyzeExplainSingleResult = analyzeExplainSqlResult.new AnalyzeExplainSingleResult();
          analyzeExplainSqlResult.getAnalyzeExplainSingleResultList().add(analyzeExplainSingleResult);

          analyzeExplainSingleResult.setExplainId(explainRow.getExplainId());
          analyzeExplainSingleResult.setExplainSelectType(explainRow.getExplainSelectType());
          analyzeExplainSingleResult.setExplainTable(explainRow.getExplainTable());
          analyzeExplainSingleResult.setExplainPartitions(explainRow.getExplainPartitions());
          analyzeExplainSingleResult.setExplainType(explainRow.getExplainType());
          analyzeExplainSingleResult.setExplainPossibleKeys(explainRow.getExplainPossibleKeys());
          analyzeExplainSingleResult.setExplainKey(explainRow.getExplainKey());
          analyzeExplainSingleResult.setExplainKeyLen(explainRow.getExplainKeyLen());
          analyzeExplainSingleResult.setExplainRef(explainRow.getExplainRef());
          analyzeExplainSingleResult.setExplainRows(explainRow.getExplainRows());
          analyzeExplainSingleResult.setExplainFiltered(explainRow.getExplainFiltered());
          analyzeExplainSingleResult.setExplainExtra(explainRow.getExplainExtra());
        }
      }
      return analyzeExplainResult;
    }
  }

  @Override
  public AnalyzeExplainResult handle(AnalyzeStatementExecutionParam analyzeParam) {
    return null;
  }

  @Override
  public boolean canConsume(AnalyzeResult analyzeResult) {
    return Objects.equals(AnalyzeExplainResult.class, analyzeResult.getClass());
  }

  @Override
  public void consume(AnalyzeResult analyzeResult) {
    List<AnalyzeExplainResultEntity> analyzeExplainResultEntityList = new ArrayList<>();
    AnalyzeExplainResult analyzeExplainResult = (AnalyzeExplainResult) analyzeResult;
    for (AnalyzeExplainResult.AnalyzeExplainSqlResult analyzeExplainSqlResult :
            analyzeExplainResult.getAnalyzeExplainSqlResultList()) {
      for (AnalyzeExplainResult.AnalyzeExplainSqlResult.AnalyzeExplainSingleResult analyzeExplainSingleResult :
              analyzeExplainSqlResult.getAnalyzeExplainSingleResultList()) {
        AnalyzeExplainResultEntity analyzeExplainResultEntity = new AnalyzeExplainResultEntity();
        analyzeExplainResultEntityList.add(analyzeExplainResultEntity);
        analyzeExplainResultEntity.setSource(analyzeExplainResult.getSource());
        analyzeExplainResultEntity.setOuterId(analyzeExplainSqlResult.getOuterId());
        analyzeExplainResultEntity.setSqlType(analyzeExplainSqlResult.getSqlType());
        analyzeExplainResultEntity.setExplainId(analyzeExplainSingleResult.getExplainId());
        analyzeExplainResultEntity.setExplainSelectType(analyzeExplainSingleResult.getExplainSelectType());
        analyzeExplainResultEntity.setExplainTable(analyzeExplainSingleResult.getExplainTable());
        analyzeExplainResultEntity.setExplainPartitions(analyzeExplainSingleResult.getExplainPartitions());
        analyzeExplainResultEntity.setExplainType(analyzeExplainSingleResult.getExplainType());
        analyzeExplainResultEntity.setExplainPossibleKeys(analyzeExplainSingleResult.getExplainPossibleKeys());
        analyzeExplainResultEntity.setExplainKey(analyzeExplainSingleResult.getExplainKey());
        analyzeExplainResultEntity.setExplainKeyLen(analyzeExplainSingleResult.getExplainKeyLen());
        analyzeExplainResultEntity.setExplainRef(analyzeExplainSingleResult.getExplainRef());
        analyzeExplainResultEntity.setExplainRows(analyzeExplainSingleResult.getExplainRows());
        analyzeExplainResultEntity.setExplainFiltered(analyzeExplainSingleResult.getExplainFiltered());
        analyzeExplainResultEntity.setExplainExtra(analyzeExplainSingleResult.getExplainExtra());
      }
    }

    // todo 后续可以增加一个开关，是不是包含ALL的才能落库
    Map<Long, List<AnalyzeExplainResultEntity>> analyzeExplainResultEntityGroup =
            analyzeExplainResultEntityList.stream().collect(Collectors.groupingBy(AnalyzeExplainResultEntity::getOuterId));
    analyzeExplainResultEntityGroup.entrySet().removeIf(
            entry -> {
              List<AnalyzeExplainResultEntity> value = entry.getValue();
              for (AnalyzeExplainResultEntity analyzeExplainResultEntity : value) {
                if ("ALL".equalsIgnoreCase(analyzeExplainResultEntity.getExplainType())) {
                  return false;
                }
              }
              return true;
            });

    analyzeExplainResultEntityList = analyzeExplainResultEntityGroup.values().stream()
            .flatMap(Collection::stream)
            .collect(Collectors.toList());

    analyzeExplainResultEntityCurder.batchInsert(analyzeExplainResultEntityList);
  }

}
