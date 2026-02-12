package cn.addenda.porttrail.common.pojo.db;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@ToString
public class StatementSql implements SqlOrder, Serializable {

  private static final long serialVersionUID = 1L;

  @Getter
  @Setter
  private String sql;

  private int orderInStatement;

  private int orderInConnection;

  public StatementSql() {
  }

  public static StatementSql of(String sql, int orderInStatement, int orderInConnection) {
    StatementSql statementSql = new StatementSql();
    statementSql.setSql(sql);
    statementSql.setOrderInStatement(orderInStatement);
    statementSql.setOrderInConnection(orderInConnection);
    return statementSql;
  }

  public static StatementSql of(String sql) {
    StatementSql statementSql = new StatementSql();
    statementSql.setSql(sql);
    return statementSql;
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
