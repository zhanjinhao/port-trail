package cn.addenda.porttrail.common.pojo.db;

import cn.addenda.porttrail.common.tuple.Tuple;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
public class PreparedStatementParameterWrapper implements SqlOrder {

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

  public PreparedStatementParameterWrapper() {
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

  public PreparedStatementParameterWrapper deepClone() {
    PreparedStatementParameterWrapper preparedStatementParameterWrapper = new PreparedStatementParameterWrapper();
    preparedStatementParameterWrapper.orderInStatement = orderInStatement;
    preparedStatementParameterWrapper.orderInConnection = orderInConnection;
    preparedStatementParameterWrapper.capacity = capacity;
    preparedStatementParameterWrapper.setMethodList = new ArrayList<>(setMethodList);
    // parameterList不需要再deepClone了，因为PreparedStatementParameterWrapper只能替换，不能更新，所以parameter不会被修改
    preparedStatementParameterWrapper.parameterList = new ArrayList<>(parameterList);

    return preparedStatementParameterWrapper;
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

}
