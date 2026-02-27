package cn.addenda.porttrail.common.pojo.db.dto;

import cn.addenda.porttrail.common.pojo.db.bo.StatementSql;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Setter
@Getter
@ToString
public class StatementSqlDto implements Serializable {

  private static final long serialVersionUID = 1L;

  @Getter
  @Setter
  private String sql;

  private int orderInStatement;

  private int orderInConnection;

  public StatementSqlDto() {
  }

  public StatementSqlDto(StatementSql statementSql) {
    this.setSql(statementSql.getSql());
    this.setOrderInStatement(statementSql.getOrderInStatement());
    this.setOrderInConnection(statementSql.getOrderInConnection());
  }

}
