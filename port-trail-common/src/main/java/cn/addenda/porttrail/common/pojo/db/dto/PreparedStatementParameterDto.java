package cn.addenda.porttrail.common.pojo.db.dto;

import cn.addenda.porttrail.common.pojo.db.bo.PreparedStatementParameter;
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
public class PreparedStatementParameterDto implements Serializable {

  private static final long serialVersionUID = 1L;

  private int orderInStatement;

  private int orderInConnection;

  private List<String> setMethodList;

  private List<Class<?>> parameterTypeList;

  private List<byte[]> parameterValueList;

  public PreparedStatementParameterDto() {
  }

  public PreparedStatementParameterDto(PreparedStatementParameter preparedStatementParameter) {
    this.setOrderInConnection(preparedStatementParameter.getOrderInConnection());
    this.setOrderInStatement(preparedStatementParameter.getOrderInStatement());
    this.setSetMethodList(preparedStatementParameter.getSetMethodList());
    this.setParameterTypeList(
            preparedStatementParameter.getParameterList().stream()
                    .map(Tuple::getClass)
                    .collect(Collectors.toList())
    );

    List<Tuple> parameterList = preparedStatementParameter.getParameterList();
    List<byte[]> _parameterValueList = new ArrayList<>();
    for (Tuple parameter : parameterList) {
      // parameter是不会为null的
      if (parameter instanceof Unary) {
        Object f1 = ((Unary<?>) parameter).getF1();
        _parameterValueList.add(toBytes(f1));
      } else if (parameter instanceof Binary) {
        Object f1 = ((Binary<?, ?>) parameter).getF1();
        Object f2 = ((Binary<?, ?>) parameter).getF2();
        _parameterValueList.add(toBytes(f1));
        _parameterValueList.add(toBytes(f2));
      } else if (parameter instanceof Ternary) {
        Object f1 = ((Ternary<?, ?, ?>) parameter).getF1();
        Object f2 = ((Ternary<?, ?, ?>) parameter).getF2();
        Object f3 = ((Ternary<?, ?, ?>) parameter).getF3();
        _parameterValueList.add(toBytes(f1));
        _parameterValueList.add(toBytes(f2));
        _parameterValueList.add(toBytes(f3));
      }
    }
    this.setParameterValueList(_parameterValueList);
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
