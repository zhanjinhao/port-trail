package cn.addenda.porttrail.common.pojo.db.bo;

import cn.addenda.porttrail.common.pojo.db.dto.PreparedStatementParameterDto;
import cn.addenda.porttrail.common.tuple.Binary;
import cn.addenda.porttrail.common.tuple.Ternary;
import cn.addenda.porttrail.common.tuple.Tuple;
import cn.addenda.porttrail.common.tuple.Unary;
import cn.addenda.porttrail.common.util.JdkSerializationUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ToString
public class PreparedStatementParameter implements SqlOrder {

  public static final byte[] UN_SUPPORTED_PARAMETER = new byte[]{-1};

  @Getter
  @Setter
  private int capacity = 0;

  @Getter
  @Setter
  private List<String> setMethodList = new ArrayList<>();

  @Getter
  @Setter
  private List<Tuple> parameterList = new ArrayList<>();

  private int orderInStatement;

  private int orderInConnection;

  public PreparedStatementParameter() {
  }

  public PreparedStatementParameter(PreparedStatementParameterDto preparedStatementParameterDto) {
    this.setOrderInStatement(preparedStatementParameterDto.getOrderInStatement());
    this.setOrderInConnection(preparedStatementParameterDto.getOrderInConnection());
    this.setSetMethodList(preparedStatementParameterDto.getSetMethodList());
    this.setCapacity(preparedStatementParameterDto.getParameterTypeList().size());
    List<Class<?>> parameterTypeList = preparedStatementParameterDto.getParameterTypeList();
    List<byte[]> parameterValueList = preparedStatementParameterDto.getParameterValueList();

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

  public void set(int index, String setMethod, Tuple parameter) {
    if (index >= capacity) {
      for (int i = capacity; i <= index; i++) {
        setMethodList.add(null);
        parameterList.add(null);
      }
      capacity = index + 1;
    }
    setMethodList.set(index, setMethod);
    parameterList.set(index, parameter);
  }

  public void clear() {
    capacity = 0;
    setMethodList = new ArrayList<>();
    parameterList = new ArrayList<>();
  }

  public PreparedStatementParameter deepClone() {
    PreparedStatementParameter preparedStatementParameter = new PreparedStatementParameter();
    preparedStatementParameter.orderInStatement = orderInStatement;
    preparedStatementParameter.orderInConnection = orderInConnection;
    preparedStatementParameter.capacity = capacity;
    preparedStatementParameter.setMethodList = new ArrayList<>(setMethodList);
    // parameterList不需要再deepClone了，因为PreparedStatementParameterWrapper只能替换，不能更新，所以parameter不会被修改
    preparedStatementParameter.parameterList = new ArrayList<>(parameterList);

    return preparedStatementParameter;
  }

  @Override
  public int getOrderInStatement() {
    return orderInStatement;
  }

  @Override
  public void setOrderInStatement(int orderInStatement) {
    this.orderInStatement = orderInStatement;
  }

  @Override
  public int getOrderInConnection() {
    return orderInConnection;
  }

  @Override
  public void setOrderInConnection(int orderInConnection) {
    this.orderInConnection = orderInConnection;
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

}
