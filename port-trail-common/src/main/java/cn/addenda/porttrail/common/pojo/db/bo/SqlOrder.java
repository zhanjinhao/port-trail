package cn.addenda.porttrail.common.pojo.db.bo;

import java.sql.PreparedStatement;
import java.sql.Statement;

/**
 * 不是{@link Statement}或{@link PreparedStatement}的execute顺序。是执行的SQL的顺序。
 */
public interface SqlOrder {

  int getOrderInStatement();

  void setOrderInStatement(int orderInStatement);

  int getOrderInConnection();

  void setOrderInConnection(int orderInConnection);

}
