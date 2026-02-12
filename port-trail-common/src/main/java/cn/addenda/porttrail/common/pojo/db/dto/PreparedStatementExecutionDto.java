package cn.addenda.porttrail.common.pojo.db.dto;

import cn.addenda.porttrail.common.entrypoint.EntryPointSnapshot;
import cn.addenda.porttrail.common.pojo.db.bo.PreparedStatementParameter;
import cn.addenda.porttrail.common.pojo.db.bo.PreparedStatementExecutionBo;
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
public class PreparedStatementExecutionDto extends AbstractDbDto {

  private String parameterizedSql;

  private String statementState;

  private String txId;

  private Long start;

  private Long end;

  private EntryPointSnapshot entryPointSnapshot;

  private List<PreparedStatementParameterDto> preparedStatementParameterDtoList = new ArrayList<>();

  public static PreparedStatementExecutionDto createByPreparedStatementExecutionBo(PreparedStatementExecutionBo preparedStatementExecutionBo) {
    PreparedStatementExecutionDto preparedStatementExecutionDto = new PreparedStatementExecutionDto();
    preparedStatementExecutionDto.setParameterizedSql(preparedStatementExecutionBo.getParameterizedSql());
    preparedStatementExecutionDto.setDataSourcePortTrailId(preparedStatementExecutionBo.getDataSourcePortTrailId());
    preparedStatementExecutionDto.setConnectionPortTrailId(preparedStatementExecutionBo.getConnectionPortTrailId());
    preparedStatementExecutionDto.setStatementPortTrailId(preparedStatementExecutionBo.getStatementPortTrailId());
    preparedStatementExecutionDto.setStatementState(preparedStatementExecutionBo.getStatementState());
    preparedStatementExecutionDto.setTxId(preparedStatementExecutionBo.getTxId());
    preparedStatementExecutionDto.setStart(preparedStatementExecutionBo.getStart());
    preparedStatementExecutionDto.setEnd(preparedStatementExecutionBo.getEnd());
    preparedStatementExecutionDto.setEntryPointSnapshot(preparedStatementExecutionBo.getEntryPointSnapshot());

    List<PreparedStatementParameterDto> preparedStatementParameterDtoList = preparedStatementExecutionBo.getPreparedStatementParameterList().stream()
            .map(preparedStatementParameterWrapper -> {
              PreparedStatementParameterDto preparedStatementParameterDto = new PreparedStatementParameterDto();
              preparedStatementParameterDto.setOrderInConnection(preparedStatementParameterWrapper.getOrderInConnection());
              preparedStatementParameterDto.setOrderInStatement(preparedStatementParameterWrapper.getOrderInStatement());
              preparedStatementParameterDto.setSetMethodList(preparedStatementParameterWrapper.getSetMethodList());
              List<Class<?>> parameterTypeList = preparedStatementParameterWrapper.getParameterList().stream()
                      .map(Tuple::getClass)
                      .collect(Collectors.toList());
              preparedStatementParameterDto.setParameterTypeList(parameterTypeList);

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
              preparedStatementParameterDto.setParameterValueList(parameterValueList);
              return preparedStatementParameterDto;
            }).collect(Collectors.toList());
    preparedStatementExecutionDto.setPreparedStatementParameterDtoList(preparedStatementParameterDtoList);

    return preparedStatementExecutionDto;
  }

  private static byte[] toBytes(Object f1) {
    if (f1 == null) {
      return null;
    } else {
      if (f1 instanceof Serializable) {
        return JdkSerializationUtils.serialize(f1);
      } else {
        return PreparedStatementParameter.UN_SUPPORTED_PARAMETER;
      }
    }
  }

}
