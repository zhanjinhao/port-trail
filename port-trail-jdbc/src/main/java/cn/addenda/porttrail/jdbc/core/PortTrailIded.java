package cn.addenda.porttrail.jdbc.core;

import java.sql.SQLException;

public interface PortTrailIded {

  String getPortTrailId();

  /**
   * 这个方法必须保证幂等性
   */
  void closePortTrail() throws SQLException;

}
