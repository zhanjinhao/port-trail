package cn.addenda.porttrail.jdbc.test.porttrail;

import cn.addenda.porttrail.common.pojo.db.DbExecution;
import cn.addenda.porttrail.common.tuple.Unary;
import cn.addenda.porttrail.common.pojo.db.bo.AbstractSqlExecutionBo;
import cn.addenda.porttrail.common.pojo.db.bo.PreparedSqlExecutionBo;
import cn.addenda.porttrail.jdbc.core.PortTrailDataSource;
import cn.addenda.porttrail.jdbc.log.JdbcPortTrailLoggerFactory;
import cn.addenda.porttrail.jdbc.test.DbUtils;
import cn.addenda.porttrail.jdbc.test.jdbc.BatchModeJDBCTest;
import cn.addenda.porttrail.jdbc.test.jdbc.BatchModeParameterizeJDBCTest;
import cn.addenda.porttrail.jdbc.test.log.JdbcTestSqlWriter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 批量执行场景的单元测试。对应{@link BatchModeJDBCTest}里的场景。
 */
class BatchModeParameterizePortTrailTest extends BatchModeParameterizeJDBCTest {

  private DataSource dataSource;

  private JdbcTestSqlWriter sqlWriter;

  @BeforeEach
  void before() {
    dataSource = DbUtils.getDataSource();
    sqlWriter = new JdbcTestSqlWriter();
    dataSource = new PortTrailDataSource(dataSource, JdbcPortTrailLoggerFactory.getInstance(), sqlWriter);
  }


  @Test
  void test_preparedStatement_execute_parameterize_batch_autoCommit_false_commit() throws Exception {
    Connection connection = dataSource.getConnection();
    test_preparedStatement_execute_parameterize_batch_autoCommit_false_commit(connection);


    List<DbExecution> dbExecutionList = sqlWriter.getDbExecutionList();
    Assertions.assertEquals(2, dbExecutionList.size());

    Assertions.assertInstanceOf(PreparedSqlExecutionBo.class, dbExecutionList.get(0));
    Assertions.assertInstanceOf(PreparedSqlExecutionBo.class, dbExecutionList.get(1));

    List<PreparedSqlExecutionBo> sqlBoList = dbExecutionList.stream().map(a -> ((PreparedSqlExecutionBo) a)).collect(Collectors.toList());

    Set<String> txIdSet = new HashSet<>();
    txIdSet.add(sqlBoList.get(0).getTxId());
    txIdSet.add(sqlBoList.get(1).getTxId());
    Assertions.assertEquals(1, txIdSet.size());

    Set<String> dataSourcePortTrailIdSet = new HashSet<>();
    dataSourcePortTrailIdSet.add(sqlBoList.get(0).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(sqlBoList.get(1).getDataSourcePortTrailId());
    Assertions.assertEquals(1, dataSourcePortTrailIdSet.size());

    Set<String> connectionPortTrailIdSet = new HashSet<>();
    connectionPortTrailIdSet.add(sqlBoList.get(0).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(sqlBoList.get(1).getConnectionPortTrailId());
    Assertions.assertEquals(1, connectionPortTrailIdSet.size());

    Set<String> statementPortTrailIdSet = new HashSet<>();
    statementPortTrailIdSet.add(sqlBoList.get(0).getStatementPortTrailId());
    statementPortTrailIdSet.add(sqlBoList.get(1).getStatementPortTrailId());
    Assertions.assertEquals(1, statementPortTrailIdSet.size());

    Assertions.assertEquals(3, sqlBoList.get(0).getPreparedStatementParameterWrapperList().size());
    Assertions.assertEquals(3, sqlBoList.get(1).getPreparedStatementParameterWrapperList().size());

    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_COMMITTED, sqlBoList.get(0).getSqlState());
    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_COMMITTED, sqlBoList.get(1).getSqlState());

    Assertions.assertEquals(0, sqlBoList.get(0).getPreparedStatementParameterWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(1, sqlBoList.get(0).getPreparedStatementParameterWrapperList().get(1).getOrderInStatement());
    Assertions.assertEquals(2, sqlBoList.get(0).getPreparedStatementParameterWrapperList().get(2).getOrderInStatement());
    Assertions.assertEquals(3, sqlBoList.get(1).getPreparedStatementParameterWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(4, sqlBoList.get(1).getPreparedStatementParameterWrapperList().get(1).getOrderInStatement());
    Assertions.assertEquals(5, sqlBoList.get(1).getPreparedStatementParameterWrapperList().get(2).getOrderInStatement());

    Assertions.assertEquals(0, sqlBoList.get(0).getPreparedStatementParameterWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(1, sqlBoList.get(0).getPreparedStatementParameterWrapperList().get(1).getOrderInConnection());
    Assertions.assertEquals(2, sqlBoList.get(0).getPreparedStatementParameterWrapperList().get(2).getOrderInConnection());
    Assertions.assertEquals(3, sqlBoList.get(1).getPreparedStatementParameterWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(4, sqlBoList.get(1).getPreparedStatementParameterWrapperList().get(1).getOrderInConnection());
    Assertions.assertEquals(5, sqlBoList.get(1).getPreparedStatementParameterWrapperList().get(2).getOrderInConnection());

    Assertions.assertTrue(((Unary<String>) sqlBoList.get(0).getPreparedStatementParameterWrapperList().get(0).getParameterList().get(0)).getF1().replace("'", "").endsWith("0"));
    Assertions.assertTrue(((Unary<String>) sqlBoList.get(0).getPreparedStatementParameterWrapperList().get(1).getParameterList().get(0)).getF1().replace("'", "").endsWith("1"));
    Assertions.assertTrue(((Unary<String>) sqlBoList.get(0).getPreparedStatementParameterWrapperList().get(2).getParameterList().get(0)).getF1().replace("'", "").endsWith("2"));
    Assertions.assertTrue(((Unary<String>) sqlBoList.get(1).getPreparedStatementParameterWrapperList().get(0).getParameterList().get(0)).getF1().replace("'", "").endsWith("3"));
    Assertions.assertTrue(((Unary<String>) sqlBoList.get(1).getPreparedStatementParameterWrapperList().get(1).getParameterList().get(0)).getF1().replace("'", "").endsWith("4"));
    Assertions.assertTrue(((Unary<String>) sqlBoList.get(1).getPreparedStatementParameterWrapperList().get(2).getParameterList().get(0)).getF1().replace("'", "").endsWith("5"));

  }

  @Test
  void test_preparedStatement_execute_parameterize_batch_autoCommit_false_rollback() throws Exception {
    Connection connection = dataSource.getConnection();
    test_preparedStatement_execute_parameterize_batch_autoCommit_false_rollback(connection);


    List<DbExecution> dbExecutionList = sqlWriter.getDbExecutionList();
    Assertions.assertEquals(2, dbExecutionList.size());

    Assertions.assertInstanceOf(PreparedSqlExecutionBo.class, dbExecutionList.get(0));
    Assertions.assertInstanceOf(PreparedSqlExecutionBo.class, dbExecutionList.get(1));

    List<PreparedSqlExecutionBo> sqlBoList = dbExecutionList.stream().map(a -> ((PreparedSqlExecutionBo) a)).collect(Collectors.toList());

    Set<String> txIdSet = new HashSet<>();
    txIdSet.add(sqlBoList.get(0).getTxId());
    txIdSet.add(sqlBoList.get(1).getTxId());
    Assertions.assertEquals(1, txIdSet.size());

    Set<String> dataSourcePortTrailIdSet = new HashSet<>();
    dataSourcePortTrailIdSet.add(sqlBoList.get(0).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(sqlBoList.get(1).getDataSourcePortTrailId());
    Assertions.assertEquals(1, dataSourcePortTrailIdSet.size());

    Set<String> connectionPortTrailIdSet = new HashSet<>();
    connectionPortTrailIdSet.add(sqlBoList.get(0).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(sqlBoList.get(1).getConnectionPortTrailId());
    Assertions.assertEquals(1, connectionPortTrailIdSet.size());

    Set<String> statementPortTrailIdSet = new HashSet<>();
    statementPortTrailIdSet.add(sqlBoList.get(0).getStatementPortTrailId());
    statementPortTrailIdSet.add(sqlBoList.get(1).getStatementPortTrailId());
    Assertions.assertEquals(1, statementPortTrailIdSet.size());

    Assertions.assertEquals(3, sqlBoList.get(0).getPreparedStatementParameterWrapperList().size());
    Assertions.assertEquals(3, sqlBoList.get(1).getPreparedStatementParameterWrapperList().size());

    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_ROLLBACK, sqlBoList.get(0).getSqlState());
    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_ROLLBACK, sqlBoList.get(1).getSqlState());

    Assertions.assertEquals(0, sqlBoList.get(0).getPreparedStatementParameterWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(1, sqlBoList.get(0).getPreparedStatementParameterWrapperList().get(1).getOrderInStatement());
    Assertions.assertEquals(2, sqlBoList.get(0).getPreparedStatementParameterWrapperList().get(2).getOrderInStatement());
    Assertions.assertEquals(3, sqlBoList.get(1).getPreparedStatementParameterWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(4, sqlBoList.get(1).getPreparedStatementParameterWrapperList().get(1).getOrderInStatement());
    Assertions.assertEquals(5, sqlBoList.get(1).getPreparedStatementParameterWrapperList().get(2).getOrderInStatement());

    Assertions.assertEquals(0, sqlBoList.get(0).getPreparedStatementParameterWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(1, sqlBoList.get(0).getPreparedStatementParameterWrapperList().get(1).getOrderInConnection());
    Assertions.assertEquals(2, sqlBoList.get(0).getPreparedStatementParameterWrapperList().get(2).getOrderInConnection());
    Assertions.assertEquals(3, sqlBoList.get(1).getPreparedStatementParameterWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(4, sqlBoList.get(1).getPreparedStatementParameterWrapperList().get(1).getOrderInConnection());
    Assertions.assertEquals(5, sqlBoList.get(1).getPreparedStatementParameterWrapperList().get(2).getOrderInConnection());


    Assertions.assertTrue(((Unary<String>) sqlBoList.get(0).getPreparedStatementParameterWrapperList().get(0).getParameterList().get(0)).getF1().replace("'", "").endsWith("0"));
    Assertions.assertTrue(((Unary<String>) sqlBoList.get(0).getPreparedStatementParameterWrapperList().get(1).getParameterList().get(0)).getF1().replace("'", "").endsWith("1"));
    Assertions.assertTrue(((Unary<String>) sqlBoList.get(0).getPreparedStatementParameterWrapperList().get(2).getParameterList().get(0)).getF1().replace("'", "").endsWith("2"));
    Assertions.assertTrue(((Unary<String>) sqlBoList.get(1).getPreparedStatementParameterWrapperList().get(0).getParameterList().get(0)).getF1().replace("'", "").endsWith("3"));
    Assertions.assertTrue(((Unary<String>) sqlBoList.get(1).getPreparedStatementParameterWrapperList().get(1).getParameterList().get(0)).getF1().replace("'", "").endsWith("4"));
    Assertions.assertTrue(((Unary<String>) sqlBoList.get(1).getPreparedStatementParameterWrapperList().get(2).getParameterList().get(0)).getF1().replace("'", "").endsWith("5"));

  }

  @Test
  void test_preparedStatement_execute_parameterize_batch_autoCommit_true_commit() throws Exception {
    Connection connection = dataSource.getConnection();
    test_preparedStatement_execute_parameterize_batch_autoCommit_true_commit(connection);


    List<DbExecution> dbExecutionList = sqlWriter.getDbExecutionList();
    Assertions.assertEquals(2, dbExecutionList.size());

    Assertions.assertInstanceOf(PreparedSqlExecutionBo.class, dbExecutionList.get(0));
    Assertions.assertInstanceOf(PreparedSqlExecutionBo.class, dbExecutionList.get(1));

    List<PreparedSqlExecutionBo> sqlBoList = dbExecutionList.stream().map(a -> ((PreparedSqlExecutionBo) a)).collect(Collectors.toList());

    Set<String> txIdSet = new HashSet<>();
    txIdSet.add(sqlBoList.get(0).getTxId());
    txIdSet.add(sqlBoList.get(1).getTxId());
    Assertions.assertEquals(2, txIdSet.size());

    Set<String> dataSourcePortTrailIdSet = new HashSet<>();
    dataSourcePortTrailIdSet.add(sqlBoList.get(0).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(sqlBoList.get(1).getDataSourcePortTrailId());
    Assertions.assertEquals(1, dataSourcePortTrailIdSet.size());

    Set<String> connectionPortTrailIdSet = new HashSet<>();
    connectionPortTrailIdSet.add(sqlBoList.get(0).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(sqlBoList.get(1).getConnectionPortTrailId());
    Assertions.assertEquals(1, connectionPortTrailIdSet.size());

    Set<String> statementPortTrailIdSet = new HashSet<>();
    statementPortTrailIdSet.add(sqlBoList.get(0).getStatementPortTrailId());
    statementPortTrailIdSet.add(sqlBoList.get(1).getStatementPortTrailId());
    Assertions.assertEquals(1, statementPortTrailIdSet.size());

    Assertions.assertEquals(3, sqlBoList.get(0).getPreparedStatementParameterWrapperList().size());
    Assertions.assertEquals(3, sqlBoList.get(1).getPreparedStatementParameterWrapperList().size());

    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_COMMITTED, sqlBoList.get(0).getSqlState());
    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_COMMITTED, sqlBoList.get(1).getSqlState());

    Assertions.assertEquals(0, sqlBoList.get(0).getPreparedStatementParameterWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(1, sqlBoList.get(0).getPreparedStatementParameterWrapperList().get(1).getOrderInStatement());
    Assertions.assertEquals(2, sqlBoList.get(0).getPreparedStatementParameterWrapperList().get(2).getOrderInStatement());
    Assertions.assertEquals(3, sqlBoList.get(1).getPreparedStatementParameterWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(4, sqlBoList.get(1).getPreparedStatementParameterWrapperList().get(1).getOrderInStatement());
    Assertions.assertEquals(5, sqlBoList.get(1).getPreparedStatementParameterWrapperList().get(2).getOrderInStatement());

    Assertions.assertEquals(0, sqlBoList.get(0).getPreparedStatementParameterWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(1, sqlBoList.get(0).getPreparedStatementParameterWrapperList().get(1).getOrderInConnection());
    Assertions.assertEquals(2, sqlBoList.get(0).getPreparedStatementParameterWrapperList().get(2).getOrderInConnection());
    Assertions.assertEquals(3, sqlBoList.get(1).getPreparedStatementParameterWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(4, sqlBoList.get(1).getPreparedStatementParameterWrapperList().get(1).getOrderInConnection());
    Assertions.assertEquals(5, sqlBoList.get(1).getPreparedStatementParameterWrapperList().get(2).getOrderInConnection());

    Assertions.assertTrue(((Unary<String>) sqlBoList.get(0).getPreparedStatementParameterWrapperList().get(0).getParameterList().get(0)).getF1().replace("'", "").endsWith("0"));
    Assertions.assertTrue(((Unary<String>) sqlBoList.get(0).getPreparedStatementParameterWrapperList().get(1).getParameterList().get(0)).getF1().replace("'", "").endsWith("1"));
    Assertions.assertTrue(((Unary<String>) sqlBoList.get(0).getPreparedStatementParameterWrapperList().get(2).getParameterList().get(0)).getF1().replace("'", "").endsWith("2"));
    Assertions.assertTrue(((Unary<String>) sqlBoList.get(1).getPreparedStatementParameterWrapperList().get(0).getParameterList().get(0)).getF1().replace("'", "").endsWith("3"));
    Assertions.assertTrue(((Unary<String>) sqlBoList.get(1).getPreparedStatementParameterWrapperList().get(1).getParameterList().get(0)).getF1().replace("'", "").endsWith("4"));
    Assertions.assertTrue(((Unary<String>) sqlBoList.get(1).getPreparedStatementParameterWrapperList().get(2).getParameterList().get(0)).getF1().replace("'", "").endsWith("5"));

  }

  @Test
  void test_preparedStatement_execute_parameterize_batch_autoCommit_true_rollback() throws Exception {
    Connection connection = dataSource.getConnection();
    test_preparedStatement_execute_parameterize_batch_autoCommit_true_rollback(connection);


    List<DbExecution> dbExecutionList = sqlWriter.getDbExecutionList();
    Assertions.assertEquals(2, dbExecutionList.size());

    Assertions.assertInstanceOf(PreparedSqlExecutionBo.class, dbExecutionList.get(0));
    Assertions.assertInstanceOf(PreparedSqlExecutionBo.class, dbExecutionList.get(1));

    List<PreparedSqlExecutionBo> sqlBoList = dbExecutionList.stream().map(a -> ((PreparedSqlExecutionBo) a)).collect(Collectors.toList());

    Set<String> txIdSet = new HashSet<>();
    txIdSet.add(sqlBoList.get(0).getTxId());
    txIdSet.add(sqlBoList.get(1).getTxId());
    Assertions.assertEquals(2, txIdSet.size());

    Set<String> dataSourcePortTrailIdSet = new HashSet<>();
    dataSourcePortTrailIdSet.add(sqlBoList.get(0).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(sqlBoList.get(1).getDataSourcePortTrailId());
    Assertions.assertEquals(1, dataSourcePortTrailIdSet.size());

    Set<String> connectionPortTrailIdSet = new HashSet<>();
    connectionPortTrailIdSet.add(sqlBoList.get(0).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(sqlBoList.get(1).getConnectionPortTrailId());
    Assertions.assertEquals(1, connectionPortTrailIdSet.size());

    Set<String> statementPortTrailIdSet = new HashSet<>();
    statementPortTrailIdSet.add(sqlBoList.get(0).getStatementPortTrailId());
    statementPortTrailIdSet.add(sqlBoList.get(1).getStatementPortTrailId());
    Assertions.assertEquals(1, statementPortTrailIdSet.size());

    Assertions.assertEquals(3, sqlBoList.get(0).getPreparedStatementParameterWrapperList().size());
    Assertions.assertEquals(3, sqlBoList.get(1).getPreparedStatementParameterWrapperList().size());

    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_COMMITTED, sqlBoList.get(0).getSqlState());
    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_COMMITTED, sqlBoList.get(1).getSqlState());

    Assertions.assertEquals(0, sqlBoList.get(0).getPreparedStatementParameterWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(1, sqlBoList.get(0).getPreparedStatementParameterWrapperList().get(1).getOrderInStatement());
    Assertions.assertEquals(2, sqlBoList.get(0).getPreparedStatementParameterWrapperList().get(2).getOrderInStatement());
    Assertions.assertEquals(3, sqlBoList.get(1).getPreparedStatementParameterWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(4, sqlBoList.get(1).getPreparedStatementParameterWrapperList().get(1).getOrderInStatement());
    Assertions.assertEquals(5, sqlBoList.get(1).getPreparedStatementParameterWrapperList().get(2).getOrderInStatement());

    Assertions.assertEquals(0, sqlBoList.get(0).getPreparedStatementParameterWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(1, sqlBoList.get(0).getPreparedStatementParameterWrapperList().get(1).getOrderInConnection());
    Assertions.assertEquals(2, sqlBoList.get(0).getPreparedStatementParameterWrapperList().get(2).getOrderInConnection());
    Assertions.assertEquals(3, sqlBoList.get(1).getPreparedStatementParameterWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(4, sqlBoList.get(1).getPreparedStatementParameterWrapperList().get(1).getOrderInConnection());
    Assertions.assertEquals(5, sqlBoList.get(1).getPreparedStatementParameterWrapperList().get(2).getOrderInConnection());

    Assertions.assertTrue(((Unary<String>) sqlBoList.get(0).getPreparedStatementParameterWrapperList().get(0).getParameterList().get(0)).getF1().replace("'", "").endsWith("0"));
    Assertions.assertTrue(((Unary<String>) sqlBoList.get(0).getPreparedStatementParameterWrapperList().get(1).getParameterList().get(0)).getF1().replace("'", "").endsWith("1"));
    Assertions.assertTrue(((Unary<String>) sqlBoList.get(0).getPreparedStatementParameterWrapperList().get(2).getParameterList().get(0)).getF1().replace("'", "").endsWith("2"));
    Assertions.assertTrue(((Unary<String>) sqlBoList.get(1).getPreparedStatementParameterWrapperList().get(0).getParameterList().get(0)).getF1().replace("'", "").endsWith("3"));
    Assertions.assertTrue(((Unary<String>) sqlBoList.get(1).getPreparedStatementParameterWrapperList().get(1).getParameterList().get(0)).getF1().replace("'", "").endsWith("4"));
    Assertions.assertTrue(((Unary<String>) sqlBoList.get(1).getPreparedStatementParameterWrapperList().get(2).getParameterList().get(0)).getF1().replace("'", "").endsWith("5"));

  }

  @AfterEach
  void after() throws Exception {
    if (dataSource instanceof AutoCloseable) {
      ((AutoCloseable) dataSource).close();
    }
  }

}
