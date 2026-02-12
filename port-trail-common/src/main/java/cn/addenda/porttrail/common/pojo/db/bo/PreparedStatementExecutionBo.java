package cn.addenda.porttrail.common.pojo.db.bo;

import cn.addenda.porttrail.common.pojo.db.dto.PreparedStatementParameterDto;
import cn.addenda.porttrail.common.pojo.db.dto.PreparedStatementExecutionDto;
import cn.addenda.porttrail.common.tuple.Binary;
import cn.addenda.porttrail.common.tuple.Ternary;
import cn.addenda.porttrail.common.tuple.Tuple;
import cn.addenda.porttrail.common.tuple.Unary;
import cn.addenda.porttrail.common.util.ArrayUtils;
import cn.addenda.porttrail.common.util.JdkSerializationUtils;
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
public class PreparedStatementExecutionBo extends AbstractStatementExecutionBo {

  private String parameterizedSql;

  private List<PreparedStatementParameter> preparedStatementParameterList;

  public PreparedStatementExecutionBo(String parameterizedSql) {
    super();
    this.parameterizedSql = parameterizedSql;
    this.preparedStatementParameterList = new ArrayList<>();
  }

  public PreparedStatementExecutionBo(String dataSourcePortTrailId, String connectionPortTrailId, String statementPortTrailId,
                                      String statementState, String parameterizedSql, PreparedStatementParameter preparedStatementParameter, String txId, long start, long end) {
    super(dataSourcePortTrailId, connectionPortTrailId, statementPortTrailId, statementState, txId, start, end);
    this.parameterizedSql = parameterizedSql;
    this.preparedStatementParameterList = ArrayUtils.asArrayList(preparedStatementParameter);
  }

  public void clear() {
    this.preparedStatementParameterList = new ArrayList<>();
  }

  @Override
  public String getDbExecutionType() {
    return DB_EXECUTION_TYPE_PREPARED_STATEMENT;
  }

  public static PreparedStatementExecutionBo createByPreparedStatementExecutionDto(PreparedStatementExecutionDto preparedStatementExecutionDto) {
    PreparedStatementExecutionBo preparedStatementExecutionBo = new PreparedStatementExecutionBo(preparedStatementExecutionDto.getParameterizedSql());
    preparedStatementExecutionBo.setDataSourcePortTrailId(preparedStatementExecutionDto.getDataSourcePortTrailId());
    preparedStatementExecutionBo.setConnectionPortTrailId(preparedStatementExecutionDto.getConnectionPortTrailId());
    preparedStatementExecutionBo.setStatementPortTrailId(preparedStatementExecutionDto.getStatementPortTrailId());
    preparedStatementExecutionBo.setStatementState(preparedStatementExecutionDto.getStatementState());
    preparedStatementExecutionBo.setTxId(preparedStatementExecutionDto.getTxId());
    preparedStatementExecutionBo.setStart(preparedStatementExecutionDto.getStart());
    preparedStatementExecutionBo.setEnd(preparedStatementExecutionDto.getEnd());
    preparedStatementExecutionBo.setEntryPointSnapshot(preparedStatementExecutionDto.getEntryPointSnapshot());
    List<PreparedStatementParameterDto> preparedStatementParameterDtoList = preparedStatementExecutionDto.getPreparedStatementParameterDtoList();

    List<PreparedStatementParameter> preparedStatementParameterList = new ArrayList<>();
    preparedStatementExecutionBo.setPreparedStatementParameterList(preparedStatementParameterList);
    for (PreparedStatementParameterDto preparedStatementParameterDto : preparedStatementParameterDtoList) {
      PreparedStatementParameter preparedStatementParameter = new PreparedStatementParameter();
      preparedStatementParameterList.add(preparedStatementParameter);

      preparedStatementParameter.setOrderInStatement(preparedStatementParameterDto.getOrderInStatement());
      preparedStatementParameter.setOrderInConnection(preparedStatementParameterDto.getOrderInConnection());
      preparedStatementParameter.setSetMethodList(preparedStatementParameterDto.getSetMethodList());
      preparedStatementParameter.setCapacity(preparedStatementParameterDto.getParameterTypeList().size());
      List<Class<?>> parameterTypeList = preparedStatementParameterDto.getParameterTypeList();
      List<byte[]> parameterValueList = preparedStatementParameterDto.getParameterValueList();
      List<Tuple> parameterList = preparedStatementParameter.getParameterList();

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

    return preparedStatementExecutionBo;
  }

  private static Object toObj(byte[] bytes) {
    if (bytes == null) {
      return null;
    }
    if (Arrays.equals(bytes, PreparedStatementParameter.UN_SUPPORTED_PARAMETER)) {
      return null;
    }
    return JdkSerializationUtils.deserialize(bytes);
  }

  @Override
  public boolean ifKeepAlive() {
    String parameterizedSqlWithoutBlank = parameterizedSql.replaceAll("\\s+", "");
    return "select1".equalsIgnoreCase(parameterizedSqlWithoutBlank) ||
            "select1fromdual".equalsIgnoreCase(parameterizedSqlWithoutBlank);
  }

}
