package cn.addenda.porttrail.jdbc.test.jdbc;

import cn.addenda.porttrail.jdbc.test.DbUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class SingleModeParameterizeJDBCTest {

  private DataSource dataSource;

  @BeforeEach
  void before() {
    dataSource = DbUtils.getDataSource();
  }

  @Test
  void test_preparedStatement_execute_parameterize_update_autoCommit_false_commit() throws Exception {
    Connection connection = dataSource.getConnection();
    test_preparedStatement_execute_parameterize_update_autoCommit_false_commit(connection);
  }

  protected void test_preparedStatement_execute_parameterize_update_autoCommit_false_commit(Connection connection) throws Exception {
    connection.setAutoCommit(false);
    PreparedStatement statement = connection.prepareStatement("insert into t_jdbc_test set name = ?");

    statement.setString(1, "SingleParameterizeModeJDBCTest-test_preparedStatement_execute_parameterize_update_autoCommit_false_commit-0");
    statement.executeUpdate();

    statement.setString(1, "SingleParameterizeModeJDBCTest-test_preparedStatement_execute_parameterize_update_autoCommit_false_commit-1");
    statement.executeUpdate();

    statement.setString(1, "SingleParameterizeModeJDBCTest-test_preparedStatement_execute_parameterize_update_autoCommit_false_commit-2");
    statement.executeUpdate();

    connection.commit();

    statement.close();
    connection.close();
  }

  @Test
  void test_preparedStatement_execute_parameterize_update_autoCommit_false_rollback() throws Exception {
    Connection connection = dataSource.getConnection();
    test_preparedStatement_execute_parameterize_update_autoCommit_false_rollback(connection);
  }

  protected void test_preparedStatement_execute_parameterize_update_autoCommit_false_rollback(Connection connection) throws Exception {
    connection.setAutoCommit(false);
    PreparedStatement statement = connection.prepareStatement("insert into t_jdbc_test set name = ?");

    statement.setString(1, "SingleParameterizeModeJDBCTest-test_preparedStatement_execute_parameterize_update_autoCommit_false_rollback-0");
    statement.executeUpdate();

    statement.setString(1, "SingleParameterizeModeJDBCTest-test_preparedStatement_execute_parameterize_update_autoCommit_false_rollback-1");
    statement.executeUpdate();

    statement.setString(1, "SingleParameterizeModeJDBCTest-test_preparedStatement_execute_parameterize_update_autoCommit_false_rollback-2");
    statement.executeUpdate();

    connection.rollback();

    statement.close();
    connection.close();
  }

  @Test
  void test_preparedStatement_execute_parameterize_update_autoCommit_true_commit() throws Exception {
    Connection connection = dataSource.getConnection();
    test_preparedStatement_execute_parameterize_update_autoCommit_true_commit(connection);
  }

  protected void test_preparedStatement_execute_parameterize_update_autoCommit_true_commit(Connection connection) throws Exception {
    connection.setAutoCommit(true);
    PreparedStatement statement = connection.prepareStatement("insert into t_jdbc_test set name = ?");

    statement.setString(1, "SingleParameterizeModeJDBCTest-test_preparedStatement_execute_parameterize_update_autoCommit_true_commit-0");
    statement.executeUpdate();

    statement.setString(1, "SingleParameterizeModeJDBCTest-test_preparedStatement_execute_parameterize_update_autoCommit_true_commit-1");
    statement.executeUpdate();

    statement.setString(1, "SingleParameterizeModeJDBCTest-test_preparedStatement_execute_parameterize_update_autoCommit_true_commit-2");
    statement.executeUpdate();

    statement.close();
    connection.close();
  }

  @Test
  void test_preparedStatement_execute_parameterize_update_autoCommit_true_rollback() throws Exception {
    Connection connection = dataSource.getConnection();
    test_preparedStatement_execute_parameterize_update_autoCommit_true_rollback(connection);
  }

  protected void test_preparedStatement_execute_parameterize_update_autoCommit_true_rollback(Connection connection) throws Exception {
    connection.setAutoCommit(true);
    PreparedStatement statement = connection.prepareStatement("insert into t_jdbc_test set name = ?");

    statement.setString(1, "SingleParameterizeModeJDBCTest-test_preparedStatement_execute_parameterize_update_autoCommit_true_rollback-0");
    statement.executeUpdate();

    statement.setString(1, "SingleParameterizeModeJDBCTest-test_preparedStatement_execute_parameterize_update_autoCommit_true_rollback-1");
    statement.executeUpdate();

    statement.setString(1, "SingleParameterizeModeJDBCTest-test_preparedStatement_execute_parameterize_update_autoCommit_true_rollback-2");
    statement.executeUpdate();

    connection.rollback();

    statement.close();
    connection.close();
  }

  @Test
  void test_preparedStatement_execute_parameterize_query_autoCommit_false_commit() throws Exception {
    Connection connection = dataSource.getConnection();
    test_preparedStatement_execute_parameterize_query_autoCommit_false_commit(connection);
  }

  protected void test_preparedStatement_execute_parameterize_query_autoCommit_false_commit(Connection connection) throws Exception {
    connection.setAutoCommit(false);
    PreparedStatement statement = connection.prepareStatement("select * from t_jdbc_test where name = ?");

    statement.setString(1, "SingleParameterizeModeJDBCTest-test_preparedStatement_execute_parameterize_query_autoCommit_false_commit-0");
    statement.executeQuery();

    statement.setString(1, "SingleParameterizeModeJDBCTest-test_preparedStatement_execute_parameterize_query_autoCommit_false_commit-1");
    statement.executeQuery();

    statement.setString(1, "SingleParameterizeModeJDBCTest-test_preparedStatement_execute_parameterize_query_autoCommit_false_commit-2");
    statement.executeQuery();

    connection.commit();

    statement.close();
    connection.close();
  }

  @Test
  void test_preparedStatement_execute_parameterize_query_autoCommit_false_rollback() throws Exception {
    Connection connection = dataSource.getConnection();
    test_preparedStatement_execute_parameterize_query_autoCommit_false_rollback(connection);
  }

  protected void test_preparedStatement_execute_parameterize_query_autoCommit_false_rollback(Connection connection) throws Exception {
    connection.setAutoCommit(false);
    PreparedStatement statement = connection.prepareStatement("select * from t_jdbc_test where name = ?");


    statement.setString(1, "SingleParameterizeModeJDBCTest-test_preparedStatement_execute_parameterize_query_autoCommit_false_rollback-0");
    statement.executeQuery();

    statement.setString(1, "SingleParameterizeModeJDBCTest-test_preparedStatement_execute_parameterize_query_autoCommit_false_rollback-1");
    statement.executeQuery();

    statement.setString(1, "SingleParameterizeModeJDBCTest-test_preparedStatement_execute_parameterize_query_autoCommit_false_rollback-2");
    statement.executeQuery();

    connection.rollback();

    statement.close();
    connection.close();
  }

  @Test
  void test_preparedStatement_execute_parameterize_query_autoCommit_true_commit() throws Exception {
    Connection connection = dataSource.getConnection();
    test_preparedStatement_execute_parameterize_query_autoCommit_true_commit(connection);
  }

  protected void test_preparedStatement_execute_parameterize_query_autoCommit_true_commit(Connection connection) throws Exception {
    connection.setAutoCommit(true);
    PreparedStatement statement = connection.prepareStatement("select * from t_jdbc_test where name = ?");

    statement.setString(1, "SingleParameterizeModeJDBCTest-test_preparedStatement_execute_parameterize_query_autoCommit_true_commit-0");
    statement.executeQuery();

    statement.setString(1, "SingleParameterizeModeJDBCTest-test_preparedStatement_execute_parameterize_query_autoCommit_true_commit-1");
    statement.executeQuery();

    statement.setString(1, "SingleParameterizeModeJDBCTest-test_preparedStatement_execute_parameterize_query_autoCommit_true_commit-2");
    statement.executeQuery();

    statement.close();
    connection.close();
  }

  @Test
  void test_preparedStatement_execute_parameterize_query_autoCommit_true_rollback() throws Exception {
    Connection connection = dataSource.getConnection();
    test_preparedStatement_execute_parameterize_query_autoCommit_true_rollback(connection);
  }

  protected void test_preparedStatement_execute_parameterize_query_autoCommit_true_rollback(Connection connection) throws Exception {
    connection.setAutoCommit(true);
    PreparedStatement statement = connection.prepareStatement("select * from t_jdbc_test where name = ?");

    statement.setString(1, "SingleParameterizeModeJDBCTest-test_preparedStatement_execute_parameterize_query_autoCommit_true_rollback-0");
    statement.executeQuery();

    statement.setString(1, "SingleParameterizeModeJDBCTest-test_preparedStatement_execute_parameterize_query_autoCommit_true_rollback-1");
    statement.executeQuery();

    statement.setString(1, "SingleParameterizeModeJDBCTest-test_preparedStatement_execute_parameterize_query_autoCommit_true_rollback-2");
    statement.executeQuery();

    connection.rollback();

    statement.close();
    connection.close();
  }

  @AfterEach
  void after() throws Exception {
    if (dataSource instanceof AutoCloseable) {
      ((AutoCloseable) dataSource).close();
    }
  }

}
