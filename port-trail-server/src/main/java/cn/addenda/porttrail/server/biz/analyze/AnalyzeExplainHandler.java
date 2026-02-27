package cn.addenda.porttrail.server.biz.analyze;

import cn.addenda.component.base.collection.IterableUtils;
import cn.addenda.porttrail.common.pojo.db.bo.PreparedStatementParameter;
import cn.addenda.porttrail.common.pojo.db.dto.PreparedStatementParameterDto;
import cn.addenda.porttrail.common.tuple.Binary;
import cn.addenda.porttrail.common.tuple.Ternary;
import cn.addenda.porttrail.common.tuple.Tuple;
import cn.addenda.porttrail.common.tuple.Unary;
import cn.addenda.porttrail.common.util.CompressUtils;
import cn.addenda.porttrail.common.util.JdkSerializationUtils;
import cn.addenda.porttrail.common.util.SqlUtils;
import cn.addenda.porttrail.server.bo.analyze.param.AnalyzePreparedStatementExecutionParam;
import cn.addenda.porttrail.server.bo.analyze.param.AnalyzeStatementExecutionParam;
import cn.addenda.porttrail.server.bo.analyze.result.AnalyzeExplainResult;
import cn.addenda.porttrail.server.bo.analyze.result.AnalyzeResult;
import cn.addenda.porttrail.server.bo.est.EstPreparedStatementExecutionBo;
import cn.addenda.porttrail.server.bo.est.EstPreparedStatementParameterBo;
import cn.addenda.porttrail.server.curd.EstAnalyzeExplainResultCurder;
import cn.addenda.porttrail.server.curd.EstDbConfigCurder;
import cn.addenda.porttrail.server.entity.EstAnalyzeExplainResult;
import cn.addenda.porttrail.server.entity.EstDbConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class AnalyzeExplainHandler extends AbstractDataSourceAnalyzeHandler<AnalyzeExplainResult> {

  @Autowired
  private EstDbConfigCurder estDbConfigCurder;

  @Autowired
  private EstAnalyzeExplainResultCurder estAnalyzeExplainResultCurder;

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
    EstPreparedStatementExecutionBo estPreparedStatementExecutionBo = analyzeParam.getEstPreparedStatementExecutionBo();
    String parameterizedSql = estPreparedStatementExecutionBo.getParameterizedSql();

    EstDbConfig estDbConfig = queryByConnectionPortTrailId(estPreparedStatementExecutionBo.getConnectionPortTrailId());
    Connection connection = getConnection(estDbConfig);

    AnalyzeExplainResult analyzeExplainResult = new AnalyzeExplainResult();
    analyzeExplainResult.setSource(AnalyzeResult.SOURCE_PREPARED_STATEMENT_PARAMETER);
    String sqlType = SqlUtils.getSqlType(parameterizedSql);

    List<EstPreparedStatementParameterBo> estPreparedStatementParameterBoList =
            estPreparedStatementExecutionBo.getEstPreparedStatementParameterBoList();
    for (EstPreparedStatementParameterBo estPreparedStatementParameterBo : estPreparedStatementParameterBoList) {
      PreparedStatement preparedStatement = connection.prepareStatement("explain " + parameterizedSql);
      byte[] parameterBytes = estPreparedStatementParameterBo.getParameterBytes();
      PreparedStatementParameterDto preparedStatementParameterDto =
              (PreparedStatementParameterDto) JdkSerializationUtils.deserialize(CompressUtils.decompress(parameterBytes));

      PreparedStatementParameter preparedStatementParameter = new PreparedStatementParameter(preparedStatementParameterDto);
      int capacity = preparedStatementParameter.getCapacity();
      List<Tuple> parameterList = preparedStatementParameter.getParameterList();
      List<String> setMethodList = preparedStatementParameter.getSetMethodList();
      for (int i = 0; i < capacity; i++) {
        Tuple tuple = parameterList.get(i);
        String setMethod = setMethodList.get(i);
        set(preparedStatement, i, tuple, setMethod);
      }

      AnalyzeExplainResult.AnalyzeExplainSqlResult analyzeExplainSqlResult = analyzeExplainResult.new AnalyzeExplainSqlResult();
      analyzeExplainResult.getAnalyzeExplainSqlResultList().add(analyzeExplainSqlResult);

      analyzeExplainSqlResult.setOuterId(estPreparedStatementParameterBo.getId());
      analyzeExplainSqlResult.setSqlType(sqlType);

      addAnalyzeExplainSingleResult(preparedStatement, analyzeExplainSqlResult);
    }

    return analyzeExplainResult;
  }

  private EstDbConfig queryByConnectionPortTrailId(String connectionPortTrailId) {
    EstDbConfig param = EstDbConfig.ofParam();
    param.setConnectionPortTrailId(connectionPortTrailId);
    List<EstDbConfig> estDbConfigList = estDbConfigCurder.queryByEntity(param);
    return IterableUtils.oneOrNull(estDbConfigList);
  }

  private void addAnalyzeExplainSingleResult(
          PreparedStatement preparedStatement, AnalyzeExplainResult.AnalyzeExplainSqlResult analyzeExplainSqlResult)
          throws SQLException {
    try (ResultSet resultSet = preparedStatement.executeQuery()) {
      while (resultSet.next()) {
        AnalyzeExplainResult.AnalyzeExplainSqlResult.AnalyzeExplainSingleResult analyzeExplainSingleResult =
                analyzeExplainSqlResult.new AnalyzeExplainSingleResult();
        analyzeExplainSqlResult.getAnalyzeExplainSingleResultList().add(analyzeExplainSingleResult);

        analyzeExplainSingleResult.setExplainId(resultSet.getLong("id"));
        analyzeExplainSingleResult.setExplainSelectType(resultSet.getString("select_type"));
        analyzeExplainSingleResult.setExplainTable(resultSet.getString("table"));
        analyzeExplainSingleResult.setExplainPartitions(resultSet.getString("partitions"));
        analyzeExplainSingleResult.setExplainType(resultSet.getString("type"));
        analyzeExplainSingleResult.setExplainPossibleKeys(resultSet.getString("possible_keys"));
        analyzeExplainSingleResult.setExplainKey(resultSet.getString("key"));
        analyzeExplainSingleResult.setExplainKeyLen(resultSet.getInt("key_len"));
        analyzeExplainSingleResult.setExplainRef(resultSet.getString("ref"));
        analyzeExplainSingleResult.setExplainRows(resultSet.getInt("rows"));
        analyzeExplainSingleResult.setExplainFiltered(resultSet.getInt("filtered"));
        analyzeExplainSingleResult.setExplainExtra(resultSet.getString("Extra"));
      }
    }

  }

  private void set(PreparedStatement preparedStatement, int i, Tuple tuple, String setMethod)
          throws SQLException {
    int parameterIndex = i + 1;
    if ("setObject".equals(setMethod)) {
      setObject(preparedStatement, parameterIndex, tuple);
    } else if ("setNull".equals(setMethod)) {
      setNull(preparedStatement, parameterIndex, tuple);
    } else if ("setBoolean".equals(setMethod)) {
      setBoolean(preparedStatement, parameterIndex, tuple);
    } else if ("setByte".equals(setMethod)) {
      setByte(preparedStatement, parameterIndex, tuple);
    } else if ("setShort".equals(setMethod)) {
      setShort(preparedStatement, parameterIndex, tuple);
    } else if ("setInt".equals(setMethod)) {
      setInt(preparedStatement, parameterIndex, tuple);
    } else if ("setLong".equals(setMethod)) {
      setLong(preparedStatement, parameterIndex, tuple);
    } else if ("setFloat".equals(setMethod)) {
      setFloat(preparedStatement, parameterIndex, tuple);
    } else if ("setDouble".equals(setMethod)) {
      setDouble(preparedStatement, parameterIndex, tuple);
    } else if ("setBigDecimal".equals(setMethod)) {
      setBigDecimal(preparedStatement, parameterIndex, tuple);
    } else if ("setString".equals(setMethod)) {
      setString(preparedStatement, parameterIndex, tuple);
    } else if ("setDate".equals(setMethod)) {
      setDate(preparedStatement, parameterIndex, tuple);
    } else if ("setTimestamp".equals(setMethod)) {
      setTimestamp(preparedStatement, parameterIndex, tuple);
    } else if ("setTime".equals(setMethod)) {
      setTime(preparedStatement, parameterIndex, tuple);
    } else if ("setNString".equals(setMethod)) {
      setNString(preparedStatement, parameterIndex, tuple);
    } else if ("setBytes".equals(setMethod)
            || "setAsciiStream".equals(setMethod)
            || "setUnicodeStream".equals(setMethod)
            || "setBinaryStream".equals(setMethod)
            || "setCharacterStream".equals(setMethod)
            || "setRef".equals(setMethod)
            || "setBlob".equals(setMethod)
            || "setClob".equals(setMethod)
            || "setArray".equals(setMethod)
            || "setURL".equals(setMethod)
            || "setRowId".equals(setMethod)
            || "setNCharacterStream".equals(setMethod)
            || "setNClob".equals(setMethod)
            || "setSQLXML".equals(setMethod)
    ) {
      setNullOther(preparedStatement, parameterIndex);
    }
  }

  private void setObject(PreparedStatement preparedStatement, int parameterIndex, Tuple tuple)
          throws SQLException {
    if (tuple instanceof Unary) {
      Unary<Object> unary = (Unary<Object>) tuple;
      preparedStatement.setObject(parameterIndex, unary.getF1());
    } else if (tuple instanceof Binary) {
      Binary<Object, ?> binary = (Binary<Object, ?>) tuple;
      Object f2 = binary.getF2();
      if (f2 instanceof SQLType) {
        preparedStatement.setObject(parameterIndex, binary.getF1(), (SQLType) f2);
      } else if (f2 instanceof Integer) {
        preparedStatement.setObject(parameterIndex, binary.getF1(), (Integer) binary.getF2());
      }
    } else if (tuple instanceof Ternary) {
      Ternary<Object, ?, Integer> ternary = (Ternary<Object, ?, Integer>) tuple;
      Object f2 = ternary.getF2();
      if (f2 instanceof SQLType) {
        preparedStatement.setObject(parameterIndex, ternary.getF1(), (SQLType) ternary.getF2(), ternary.getF3());
      } else if (f2 instanceof Integer) {
        preparedStatement.setObject(parameterIndex, ternary.getF1(), (Integer) ternary.getF2(), ternary.getF3());
      }
    }
  }

  private void setNull(PreparedStatement preparedStatement, int parameterIndex, Tuple tuple)
          throws SQLException {
    if (tuple instanceof Unary) {
      Unary<Integer> unary = (Unary<Integer>) tuple;
      preparedStatement.setNull(parameterIndex, unary.getF1());
    } else if (tuple instanceof Binary) {
      Binary<Integer, String> binary = (Binary<Integer, String>) tuple;
      preparedStatement.setNull(parameterIndex, binary.getF1(), binary.getF2());
    }
  }

  private void setBoolean(PreparedStatement preparedStatement, int parameterIndex, Tuple tuple)
          throws SQLException {
    if (tuple instanceof Unary) {
      Unary<Boolean> unary = (Unary<Boolean>) tuple;
      preparedStatement.setBoolean(parameterIndex, unary.getF1());
    }
  }

  private void setByte(PreparedStatement preparedStatement, int parameterIndex, Tuple tuple)
          throws SQLException {
    if (tuple instanceof Unary) {
      Unary<Byte> unary = (Unary<Byte>) tuple;
      preparedStatement.setByte(parameterIndex, unary.getF1());
    }
  }

  private void setShort(PreparedStatement preparedStatement, int parameterIndex, Tuple tuple)
          throws SQLException {
    if (tuple instanceof Unary) {
      Unary<Short> unary = (Unary<Short>) tuple;
      preparedStatement.setShort(parameterIndex, unary.getF1());
    }
  }

  private void setInt(PreparedStatement preparedStatement, int parameterIndex, Tuple tuple)
          throws SQLException {
    if (tuple instanceof Unary) {
      Unary<Integer> unary = (Unary<Integer>) tuple;
      preparedStatement.setInt(parameterIndex, unary.getF1());
    }
  }

  private void setLong(PreparedStatement preparedStatement, int parameterIndex, Tuple tuple)
          throws SQLException {
    if (tuple instanceof Unary) {
      Unary<Long> unary = (Unary<Long>) tuple;
      preparedStatement.setLong(parameterIndex, unary.getF1());
    }
  }

  private void setFloat(PreparedStatement preparedStatement, int parameterIndex, Tuple tuple)
          throws SQLException {
    if (tuple instanceof Unary) {
      Unary<Float> unary = (Unary<Float>) tuple;
      preparedStatement.setFloat(parameterIndex, unary.getF1());
    }
  }

  private void setDouble(PreparedStatement preparedStatement, int parameterIndex, Tuple tuple)
          throws SQLException {
    if (tuple instanceof Unary) {
      Unary<Double> unary = (Unary<Double>) tuple;
      preparedStatement.setDouble(parameterIndex, unary.getF1());
    }
  }

  private void setBigDecimal(PreparedStatement preparedStatement, int parameterIndex, Tuple tuple)
          throws SQLException {
    if (tuple instanceof Unary) {
      Unary<BigDecimal> unary = (Unary<BigDecimal>) tuple;
      preparedStatement.setBigDecimal(parameterIndex, unary.getF1());
    }
  }

  private void setString(PreparedStatement preparedStatement, int parameterIndex, Tuple tuple)
          throws SQLException {
    if (tuple instanceof Unary) {
      Unary<String> unary = (Unary<String>) tuple;
      preparedStatement.setString(parameterIndex, unary.getF1());
    }
  }

  private void setDate(PreparedStatement preparedStatement, int parameterIndex, Tuple tuple)
          throws SQLException {
    if (tuple instanceof Unary) {
      Unary<Date> unary = (Unary<Date>) tuple;
      preparedStatement.setDate(parameterIndex, unary.getF1());
    }
  }

  private void setTimestamp(PreparedStatement preparedStatement, int parameterIndex, Tuple tuple)
          throws SQLException {
    if (tuple instanceof Unary) {
      Unary<Timestamp> unary = (Unary<Timestamp>) tuple;
      preparedStatement.setTimestamp(parameterIndex, unary.getF1());
    } else if (tuple instanceof Binary) {
      Binary<Timestamp, Calendar> binary = (Binary<Timestamp, Calendar>) tuple;
      preparedStatement.setTimestamp(parameterIndex, binary.getF1(), binary.getF2());
    }
  }

  private void setTime(PreparedStatement preparedStatement, int parameterIndex, Tuple tuple)
          throws SQLException {
    if (tuple instanceof Unary) {
      Unary<Time> unary = (Unary<Time>) tuple;
      preparedStatement.setTime(parameterIndex, unary.getF1());
    } else if (tuple instanceof Binary) {
      Binary<Time, Calendar> binary = (Binary<Time, Calendar>) tuple;
      preparedStatement.setTime(parameterIndex, binary.getF1(), binary.getF2());
    }
  }

  private void setNString(PreparedStatement preparedStatement, int parameterIndex, Tuple tuple)
          throws SQLException {
    if (tuple instanceof Unary) {
      Unary<String> unary = (Unary<String>) tuple;
      preparedStatement.setNString(parameterIndex, unary.getF1());
    }
  }

  private void setNullOther(PreparedStatement preparedStatement, int parameterIndex)
          throws SQLException {
    preparedStatement.setNull(parameterIndex, Types.OTHER);
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
    List<EstAnalyzeExplainResult> estAnalyzeExplainResultList = new ArrayList<>();
    AnalyzeExplainResult analyzeExplainResult = (AnalyzeExplainResult) analyzeResult;
    for (AnalyzeExplainResult.AnalyzeExplainSqlResult analyzeExplainSqlResult :
            analyzeExplainResult.getAnalyzeExplainSqlResultList()) {
      for (AnalyzeExplainResult.AnalyzeExplainSqlResult.AnalyzeExplainSingleResult analyzeExplainSingleResult :
              analyzeExplainSqlResult.getAnalyzeExplainSingleResultList()) {
        EstAnalyzeExplainResult estAnalyzeExplainResult = new EstAnalyzeExplainResult();
        estAnalyzeExplainResultList.add(estAnalyzeExplainResult);
        estAnalyzeExplainResult.setSource(analyzeExplainResult.getSource());
        estAnalyzeExplainResult.setOuterId(analyzeExplainSqlResult.getOuterId());
        estAnalyzeExplainResult.setSqlType(analyzeExplainSqlResult.getSqlType());
        estAnalyzeExplainResult.setExplainId(analyzeExplainSingleResult.getExplainId());
        estAnalyzeExplainResult.setExplainSelectType(analyzeExplainSingleResult.getExplainSelectType());
        estAnalyzeExplainResult.setExplainTable(analyzeExplainSingleResult.getExplainTable());
        estAnalyzeExplainResult.setExplainPartitions(analyzeExplainSingleResult.getExplainPartitions());
        estAnalyzeExplainResult.setExplainType(analyzeExplainSingleResult.getExplainType());
        estAnalyzeExplainResult.setExplainPossibleKeys(analyzeExplainSingleResult.getExplainPossibleKeys());
        estAnalyzeExplainResult.setExplainKey(analyzeExplainSingleResult.getExplainKey());
        estAnalyzeExplainResult.setExplainKeyLen(analyzeExplainSingleResult.getExplainKeyLen());
        estAnalyzeExplainResult.setExplainRef(analyzeExplainSingleResult.getExplainRef());
        estAnalyzeExplainResult.setExplainRows(analyzeExplainSingleResult.getExplainRows());
        estAnalyzeExplainResult.setExplainFiltered(analyzeExplainSingleResult.getExplainFiltered());
        estAnalyzeExplainResult.setExplainExtra(analyzeExplainSingleResult.getExplainExtra());
      }
    }

    // todo 后续可以增加一个开关，是不是包含ALL的才能洛克
    Map<Long, List<EstAnalyzeExplainResult>> estAnalyzeExplainResultGroup =
            estAnalyzeExplainResultList.stream().collect(Collectors.groupingBy(EstAnalyzeExplainResult::getOuterId));
    estAnalyzeExplainResultGroup.entrySet().removeIf(
            entry -> {
              List<EstAnalyzeExplainResult> value = entry.getValue();
              for (EstAnalyzeExplainResult estAnalyzeExplainResult : value) {
                if ("ALL".equalsIgnoreCase(estAnalyzeExplainResult.getExplainType())) {
                  return false;
                }
              }
              return true;
            });

    estAnalyzeExplainResultList = estAnalyzeExplainResultGroup.values().stream()
            .flatMap(Collection::stream)
            .collect(Collectors.toList());

    estAnalyzeExplainResultCurder.batchInsert(estAnalyzeExplainResultList);
  }

}
