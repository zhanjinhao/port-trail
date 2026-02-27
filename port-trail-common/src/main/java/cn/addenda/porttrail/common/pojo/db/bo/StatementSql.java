package cn.addenda.porttrail.common.pojo.db.bo;

import cn.addenda.porttrail.common.pojo.db.dto.StatementSqlDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class StatementSql implements SqlOrder {

  @Getter
  @Setter
  private String sql;

  private int orderInStatement;

  private int orderInConnection;

  public StatementSql() {
  }

  public StatementSql(String sql, int orderInStatement, int orderInConnection) {
    this.setSql(sql);
    this.setOrderInStatement(orderInStatement);
    this.setOrderInConnection(orderInConnection);
  }

  public StatementSql(StatementSqlDto statementSqlDto) {
    this.setSql(statementSqlDto.getSql());
    this.setOrderInStatement(statementSqlDto.getOrderInStatement());
    this.setOrderInConnection(statementSqlDto.getOrderInConnection());
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
