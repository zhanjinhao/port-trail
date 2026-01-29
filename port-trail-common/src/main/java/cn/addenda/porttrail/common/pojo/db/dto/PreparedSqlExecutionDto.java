package cn.addenda.porttrail.common.pojo.db.dto;

import cn.addenda.porttrail.common.entrypoint.EntryPointSnapshot;
import cn.addenda.porttrail.common.pojo.db.PreparedSqlParameter;
import cn.addenda.porttrail.common.pojo.db.PreparedStatementParameterWrapper;
import cn.addenda.porttrail.common.pojo.db.bo.PreparedSqlExecutionBo;
import cn.addenda.porttrail.common.tuple.Binary;
import cn.addenda.porttrail.common.tuple.Ternary;
import cn.addenda.porttrail.common.tuple.Tuple;
import cn.addenda.porttrail.common.tuple.Unary;
import cn.addenda.porttrail.common.util.JdkSerializationUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
@ToString
public class PreparedSqlExecutionDto extends AbstractDbDto {

  private String parameterizedSql;

  private String sqlState;

  private String txId;

  private Long start;

  private Long end;

  private EntryPointSnapshot entryPointSnapshot;

  private List<PreparedSqlParameter> preparedSqlParameterList = new ArrayList<>();

  public static PreparedSqlExecutionDto createByPreparedSqlExecutionBo(PreparedSqlExecutionBo preparedSqlExecutionBo) {
    PreparedSqlExecutionDto preparedSqlExecutionDto = new PreparedSqlExecutionDto();
    preparedSqlExecutionDto.setParameterizedSql(preparedSqlExecutionBo.getParameterizedSql());
    preparedSqlExecutionDto.setDataSourcePortTrailId(preparedSqlExecutionBo.getDataSourcePortTrailId());
    preparedSqlExecutionDto.setConnectionPortTrailId(preparedSqlExecutionBo.getConnectionPortTrailId());
    preparedSqlExecutionDto.setStatementPortTrailId(preparedSqlExecutionBo.getStatementPortTrailId());
    preparedSqlExecutionDto.setSqlState(preparedSqlExecutionBo.getSqlState());
    preparedSqlExecutionDto.setTxId(preparedSqlExecutionBo.getTxId());
    preparedSqlExecutionDto.setStart(preparedSqlExecutionBo.getStart());
    preparedSqlExecutionDto.setEnd(preparedSqlExecutionBo.getEnd());
    preparedSqlExecutionDto.setEntryPointSnapshot(preparedSqlExecutionBo.getEntryPointSnapshot());

    List<PreparedSqlParameter> preparedSqlParameterList = preparedSqlExecutionBo.getPreparedStatementParameterWrapperList().stream()
            .map(preparedStatementParameterWrapper -> {
              PreparedSqlParameter preparedSqlParameter = new PreparedSqlParameter();
              preparedSqlParameter.setOrderInConnection(preparedStatementParameterWrapper.getOrderInConnection());
              preparedSqlParameter.setOrderInStatement(preparedStatementParameterWrapper.getOrderInStatement());
              preparedSqlParameter.setSetMethodList(preparedStatementParameterWrapper.getSetMethodList());
              List<Class<?>> parameterTypeList = preparedStatementParameterWrapper.getParameterList().stream()
                      .map(Tuple::getClass)
                      .collect(Collectors.toList());
              preparedSqlParameter.setParameterTypeList(parameterTypeList);

              List<Tuple> parameterList = preparedStatementParameterWrapper.getParameterList();
              List<byte[]> parameterValueList = new ArrayList<>();
              for (Tuple parameter : parameterList) {
                // parameter是不会为null的
                if (parameter instanceof Unary) {
                  Object f1 = ((Unary<?>) parameter).getF1();
                  parameterValueList.add(toBytes(f1));
                } else if (parameter instanceof Binary) {
                  Object f1 = ((Binary<?, ?>) parameter).getF1();
                  Object f2 = ((Binary<?, ?>) parameter).getF2();
                  parameterValueList.add(toBytes(f1));
                  parameterValueList.add(toBytes(f2));
                } else if (parameter instanceof Ternary) {
                  Object f1 = ((Ternary<?, ?, ?>) parameter).getF1();
                  Object f2 = ((Ternary<?, ?, ?>) parameter).getF2();
                  Object f3 = ((Ternary<?, ?, ?>) parameter).getF3();
                  parameterValueList.add(toBytes(f1));
                  parameterValueList.add(toBytes(f2));
                  parameterValueList.add(toBytes(f3));
                }
              }
              preparedSqlParameter.setParameterValueList(parameterValueList);
              return preparedSqlParameter;
            }).collect(Collectors.toList());
    preparedSqlExecutionDto.setPreparedSqlParameterList(preparedSqlParameterList);

    return preparedSqlExecutionDto;
  }

  private static byte[] toBytes(Object f1) {
    if (f1 == null) {
      return null;
    } else {
      if (f1 instanceof Serializable) {
        return JdkSerializationUtils.serialize(f1);
      } else {
        return PreparedStatementParameterWrapper.UN_SUPPORTED_PARAMETER;
      }
    }
  }

}
