package cn.addenda.porttrail.common.pojo.db.bo;

import cn.addenda.porttrail.common.pojo.db.SqlOrder;
import cn.addenda.porttrail.common.tuple.Tuple;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
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

}
