package cn.addenda.porttrail.common.pojo.db;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@ToString
public class SqlWrapper implements SqlOrder, Serializable {

  private static final long serialVersionUID = 1L;

  @Getter
  @Setter
  private String sql;

  private int orderInStatement;

  private int orderInConnection;

  public SqlWrapper() {
  }

  public static SqlWrapper of(String sql, int orderInStatement, int orderInConnection) {
    SqlWrapper sqlWrapper = new SqlWrapper();
    sqlWrapper.setSql(sql);
    sqlWrapper.setOrderInStatement(orderInStatement);
    sqlWrapper.setOrderInConnection(orderInConnection);
    return sqlWrapper;
  }

  public static SqlWrapper of(String sql) {
    SqlWrapper sqlWrapper = new SqlWrapper();
    sqlWrapper.setSql(sql);
    return sqlWrapper;
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
