package cn.addenda.porttrail.common.pojo.db.bo;

import cn.addenda.porttrail.common.pojo.db.PreparedSqlParameter;
import cn.addenda.porttrail.common.pojo.db.PreparedStatementParameterWrapper;
import cn.addenda.porttrail.common.pojo.db.dto.PreparedSqlExecutionDto;
import cn.addenda.porttrail.common.tuple.Binary;
import cn.addenda.porttrail.common.tuple.Ternary;
import cn.addenda.porttrail.common.tuple.Tuple;
import cn.addenda.porttrail.common.tuple.Unary;
import cn.addenda.porttrail.common.util.ArrayUtils;
import cn.addenda.porttrail.common.util.SerializationUtils;
import lombok.*;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 1、一次{@link PreparedStatement#executeBatch()}或{@link PreparedStatement#executeLargeBatch()} 执行，preparedStatementParameterWrapperList的size()大于1。 <br/>
 * 2、一次{@link PreparedStatement}的其它execute执行，preparedStatementParameterWrapperList的size()等于1。
 */
@Setter
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class PreparedSqlExecutionBo extends AbstractSqlExecutionBo {

  private String parameterizedSql;

  private List<PreparedStatementParameterWrapper> preparedStatementParameterWrapperList;

  public PreparedSqlExecutionBo(String parameterizedSql) {
    super();
    this.parameterizedSql = parameterizedSql;
    this.preparedStatementParameterWrapperList = new ArrayList<>();
  }

  public PreparedSqlExecutionBo(String dataSourcePortTrailId, String connectionPortTrailId, String statementPortTrailId,
                                String sqlState, String parameterizedSql, PreparedStatementParameterWrapper preparedStatementParameterWrapper, String txId, long start, long end) {
    super(dataSourcePortTrailId, connectionPortTrailId, statementPortTrailId, sqlState, txId, start, end);
    this.parameterizedSql = parameterizedSql;
    this.preparedStatementParameterWrapperList = ArrayUtils.asArrayList(preparedStatementParameterWrapper);
  }

  public void clear() {
    this.preparedStatementParameterWrapperList = new ArrayList<>();
  }

  @Override
  public String getDbExecutionType() {
    return DB_EXECUTION_TYPE_PREPARED_SQL;
  }

  public static PreparedSqlExecutionBo createByPreparedSqlExecutionDto(PreparedSqlExecutionDto preparedSqlExecutionDto) {
    PreparedSqlExecutionBo preparedSqlExecutionBo = new PreparedSqlExecutionBo(preparedSqlExecutionDto.getParameterizedSql());
    preparedSqlExecutionBo.setDataSourcePortTrailId(preparedSqlExecutionDto.getDataSourcePortTrailId());
    preparedSqlExecutionBo.setConnectionPortTrailId(preparedSqlExecutionDto.getConnectionPortTrailId());
    preparedSqlExecutionBo.setStatementPortTrailId(preparedSqlExecutionDto.getStatementPortTrailId());
    preparedSqlExecutionBo.setSqlState(preparedSqlExecutionDto.getSqlState());
    preparedSqlExecutionBo.setTxId(preparedSqlExecutionDto.getTxId());
    preparedSqlExecutionBo.setStart(preparedSqlExecutionDto.getStart());
    preparedSqlExecutionBo.setEnd(preparedSqlExecutionDto.getEnd());
    preparedSqlExecutionBo.setEntryPointSnapshot(preparedSqlExecutionDto.getEntryPointSnapshot());
    List<PreparedSqlParameter> preparedSqlParameterList = preparedSqlExecutionDto.getPreparedSqlParameterList();

    List<PreparedStatementParameterWrapper> preparedStatementParameterWrapperList = new ArrayList<>();
    preparedSqlExecutionBo.setPreparedStatementParameterWrapperList(preparedStatementParameterWrapperList);
    for (PreparedSqlParameter preparedSqlParameter : preparedSqlParameterList) {
      PreparedStatementParameterWrapper preparedStatementParameterWrapper = new PreparedStatementParameterWrapper();
      preparedStatementParameterWrapperList.add(preparedStatementParameterWrapper);

      preparedStatementParameterWrapper.setOrderInStatement(preparedSqlParameter.getOrderInStatement());
      preparedStatementParameterWrapper.setOrderInConnection(preparedSqlParameter.getOrderInConnection());
      preparedStatementParameterWrapper.setSetMethodList(preparedSqlParameter.getSetMethodList());
      preparedStatementParameterWrapper.setCapacity(preparedSqlParameter.getParameterTypeList().size());
      List<Class<?>> parameterTypeList = preparedSqlParameter.getParameterTypeList();
      List<byte[]> parameterValueList = preparedSqlParameter.getParameterValueList();
      List<Tuple> parameterList = preparedStatementParameterWrapper.getParameterList();

      int i = 0;
      for (Class<?> aClass : parameterTypeList) {
        if (aClass.equals(Unary.class)) {
          byte[] f1Bytes = parameterValueList.get(i);
          i++;
          parameterList.add(Unary.of(toObj(f1Bytes)));
        } else if (aClass.equals(Binary.class)) {
          byte[] f1Bytes = parameterValueList.get(i);
          i++;
          byte[] f2Bytes = parameterValueList.get(i);
          i++;
          parameterList.add(Binary.of(toObj(f1Bytes), toObj(f2Bytes)));
        } else if (aClass.equals(Ternary.class)) {
          byte[] f1Bytes = parameterValueList.get(i);
          i++;
          byte[] f2Bytes = parameterValueList.get(i);
          i++;
          byte[] f3Bytes = parameterValueList.get(i);
          i++;
          parameterList.add(Ternary.of(toObj(f1Bytes), toObj(f2Bytes), toObj(f3Bytes)));
        }
      }
    }

    return preparedSqlExecutionBo;
  }

  private static Object toObj(byte[] bytes) {
    if (bytes == null) {
      return null;
    }
    if (Arrays.equals(bytes, PreparedStatementParameterWrapper.UN_SUPPORTED_PARAMETER)) {
      return null;
    }
    return SerializationUtils.deserialize(bytes);
  }

}
