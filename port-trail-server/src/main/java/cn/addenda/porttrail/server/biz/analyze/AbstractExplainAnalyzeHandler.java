package cn.addenda.porttrail.server.biz.analyze;

import cn.addenda.component.base.collection.IterableUtils;
import cn.addenda.component.cache.helper.CacheHelper;
import cn.addenda.porttrail.common.pojo.db.bo.PreparedStatementParameter;
import cn.addenda.porttrail.common.pojo.db.dto.PreparedStatementParameterDto;
import cn.addenda.porttrail.common.tuple.Binary;
import cn.addenda.porttrail.common.tuple.Ternary;
import cn.addenda.porttrail.common.tuple.Tuple;
import cn.addenda.porttrail.common.tuple.Unary;
import cn.addenda.porttrail.common.util.CompressUtils;
import cn.addenda.porttrail.common.util.JdkSerializationUtils;
import cn.addenda.porttrail.server.bo.db.PreparedStatementParameterEntityBo;
import cn.addenda.porttrail.server.bo.db.analyze.result.AnalyzeResult;
import cn.addenda.porttrail.server.curd.DbConfigEntityCurder;
import cn.addenda.porttrail.server.entity.DbConfigEntity;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 提供「连业务库执行 explain」能力的抽象基类。
 *
 * <p>负责：按 connectionPortTrailId 定位 DbConfig（带 Redis 缓存）、从连接池取连接、
 * 把 porttrail 记录的参数回填到 PreparedStatement、执行 explain 并把结果解析成 {@link ExplainRow}。
 *
 * <p>子类只需决定拿到 {@link ExplainRow} 之后如何组装成自己的 {@link AnalyzeResult}。
 */
public abstract class AbstractExplainAnalyzeHandler<T extends AnalyzeResult>
        extends AbstractDataSourceAnalyzeHandler<T> {

  @Autowired
  protected DbConfigEntityCurder dbConfigEntityCurder;

  @Autowired
  protected CacheHelper cacheHelper;

  private static final TypeReference<DbConfigEntity> EST_DB_CONFIG_TYPE_REFERENCE = new TypeReference<DbConfigEntity>() {
  };

  /**
   * 对单条参数化SQL执行一次 explain。
   *
   * @param connection       业务库连接
   * @param parameterizedSql 参数化SQL
   * @param paramBo          该次执行记录的参数，用于回填 ?
   * @return explain 的每一行，无结果时返回空列表
   */
  protected List<ExplainRow> explainOne(Connection connection, String parameterizedSql,
                                        PreparedStatementParameterEntityBo paramBo) throws SQLException {
    PreparedStatement preparedStatement = connection.prepareStatement("explain " + parameterizedSql);
    applyParameters(preparedStatement, paramBo);
    return executeExplain(preparedStatement);
  }

  /**
   * 把 porttrail 记录的参数回填到 PreparedStatement 上。
   */
  protected void applyParameters(PreparedStatement preparedStatement,
                                 PreparedStatementParameterEntityBo paramBo) throws SQLException {
    byte[] parameterBytes = paramBo.getParameterBytes();
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
  }

  /**
   * 执行 explain 并把 ResultSet 解析为 {@link ExplainRow}。
   */
  protected List<ExplainRow> executeExplain(PreparedStatement preparedStatement) throws SQLException {
    List<ExplainRow> explainRowList = new ArrayList<>();
    try (ResultSet resultSet = preparedStatement.executeQuery()) {
      while (resultSet.next()) {
        ExplainRow explainRow = new ExplainRow();
        explainRow.setExplainId(resultSet.getLong("id"));
        explainRow.setExplainSelectType(resultSet.getString("select_type"));
        explainRow.setExplainTable(resultSet.getString("table"));
        explainRow.setExplainPartitions(resultSet.getString("partitions"));
        explainRow.setExplainType(resultSet.getString("type"));
        explainRow.setExplainPossibleKeys(resultSet.getString("possible_keys"));
        explainRow.setExplainKey(resultSet.getString("key"));
        explainRow.setExplainKeyLen(resultSet.getInt("key_len"));
        explainRow.setExplainRef(resultSet.getString("ref"));
        explainRow.setExplainRows(resultSet.getInt("rows"));
        explainRow.setExplainFiltered(resultSet.getInt("filtered"));
        explainRow.setExplainExtra(resultSet.getString("Extra"));
        explainRowList.add(explainRow);
      }
    }
    return explainRowList;
  }

  /**
   * 按 connectionPortTrailId 查询业务库配置，优先走 Redis 缓存。
   */
  protected DbConfigEntity queryByConnectionPortTrailId(String connectionPortTrailId) {
    return cacheHelper.queryWithPpf("AnalyzeHandler", connectionPortTrailId, EST_DB_CONFIG_TYPE_REFERENCE,
            i -> {
              DbConfigEntity param = DbConfigEntity.ofParam();
              param.setConnectionPortTrailId(i);
              List<DbConfigEntity> dbConfigEntityList = dbConfigEntityCurder.queryByEntity(param);
              return IterableUtils.oneOrNull(dbConfigEntityList);
            }, 60000L);
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

}