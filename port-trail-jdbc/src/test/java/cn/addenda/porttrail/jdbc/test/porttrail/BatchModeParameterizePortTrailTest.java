package cn.addenda.porttrail.jdbc.test.porttrail;

import cn.addenda.porttrail.common.pojo.db.bo.DbExecution;
import cn.addenda.porttrail.common.pojo.db.bo.AbstractStatementExecutionBo;
import cn.addenda.porttrail.common.pojo.db.bo.PreparedStatementExecutionBo;
import cn.addenda.porttrail.common.tuple.Unary;
import cn.addenda.porttrail.jdbc.core.PortTrailDataSource;
import cn.addenda.porttrail.jdbc.log.JdbcPortTrailLoggerFactory;
import cn.addenda.porttrail.jdbc.test.DbUtils;
import cn.addenda.porttrail.jdbc.test.jdbc.BatchModeJDBCTest;
import cn.addenda.porttrail.jdbc.test.jdbc.BatchModeParameterizeJDBCTest;
import cn.addenda.porttrail.jdbc.test.log.JdbcTestDbWriter;
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

  private JdbcTestDbWriter dbWriter;

  @BeforeEach
  void before() {
    dataSource = DbUtils.getDataSource();
    dbWriter = new JdbcTestDbWriter();
    dataSource = new PortTrailDataSource(dataSource, JdbcPortTrailLoggerFactory.getInstance(), dbWriter);
  }


  @Test
  void test_preparedStatement_execute_parameterize_batch_autoCommit_false_commit() throws Exception {
    Connection connection = dataSource.getConnection();
    test_preparedStatement_execute_parameterize_batch_autoCommit_false_commit(connection);


    List<DbExecution> dbExecutionList = dbWriter.getDbExecutionList();
    Assertions.assertEquals(2, dbExecutionList.size());

    Assertions.assertInstanceOf(PreparedStatementExecutionBo.class, dbExecutionList.get(0));
    Assertions.assertInstanceOf(PreparedStatementExecutionBo.class, dbExecutionList.get(1));

    List<PreparedStatementExecutionBo> preparedStatementExecutionBoList = dbExecutionList.stream().map(a -> ((PreparedStatementExecutionBo) a)).collect(Collectors.toList());

    Set<String> txIdSet = new HashSet<>();
    txIdSet.add(preparedStatementExecutionBoList.get(0).getTxId());
    txIdSet.add(preparedStatementExecutionBoList.get(1).getTxId());
    Assertions.assertEquals(1, txIdSet.size());

    Set<String> dataSourcePortTrailIdSet = new HashSet<>();
    dataSourcePortTrailIdSet.add(preparedStatementExecutionBoList.get(0).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(preparedStatementExecutionBoList.get(1).getDataSourcePortTrailId());
    Assertions.assertEquals(1, dataSourcePortTrailIdSet.size());

    Set<String> connectionPortTrailIdSet = new HashSet<>();
    connectionPortTrailIdSet.add(preparedStatementExecutionBoList.get(0).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(preparedStatementExecutionBoList.get(1).getConnectionPortTrailId());
    Assertions.assertEquals(1, connectionPortTrailIdSet.size());

    Set<String> statementPortTrailIdSet = new HashSet<>();
    statementPortTrailIdSet.add(preparedStatementExecutionBoList.get(0).getStatementPortTrailId());
    statementPortTrailIdSet.add(preparedStatementExecutionBoList.get(1).getStatementPortTrailId());
    Assertions.assertEquals(1, statementPortTrailIdSet.size());

    Assertions.assertEquals(3, preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().size());
    Assertions.assertEquals(3, preparedStatementExecutionBoList.get(1).getPreparedStatementParameterList().size());

    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED, preparedStatementExecutionBoList.get(0).getStatementState());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED, preparedStatementExecutionBoList.get(1).getStatementState());

    Assertions.assertEquals(1, preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().get(0).getOrderInStatement());
    Assertions.assertEquals(2, preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().get(1).getOrderInStatement());
    Assertions.assertEquals(3, preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().get(2).getOrderInStatement());
    Assertions.assertEquals(4, preparedStatementExecutionBoList.get(1).getPreparedStatementParameterList().get(0).getOrderInStatement());
    Assertions.assertEquals(5, preparedStatementExecutionBoList.get(1).getPreparedStatementParameterList().get(1).getOrderInStatement());
    Assertions.assertEquals(6, preparedStatementExecutionBoList.get(1).getPreparedStatementParameterList().get(2).getOrderInStatement());

    Assertions.assertEquals(1, preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().get(0).getOrderInConnection());
    Assertions.assertEquals(2, preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().get(1).getOrderInConnection());
    Assertions.assertEquals(3, preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().get(2).getOrderInConnection());
    Assertions.assertEquals(4, preparedStatementExecutionBoList.get(1).getPreparedStatementParameterList().get(0).getOrderInConnection());
    Assertions.assertEquals(5, preparedStatementExecutionBoList.get(1).getPreparedStatementParameterList().get(1).getOrderInConnection());
    Assertions.assertEquals(6, preparedStatementExecutionBoList.get(1).getPreparedStatementParameterList().get(2).getOrderInConnection());

    Assertions.assertTrue(((Unary<String>) preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().get(0).getParameterList().get(0)).getF1().replace("'", "").endsWith("0"));
    Assertions.assertTrue(((Unary<String>) preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().get(1).getParameterList().get(0)).getF1().replace("'", "").endsWith("1"));
    Assertions.assertTrue(((Unary<String>) preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().get(2).getParameterList().get(0)).getF1().replace("'", "").endsWith("2"));
    Assertions.assertTrue(((Unary<String>) preparedStatementExecutionBoList.get(1).getPreparedStatementParameterList().get(0).getParameterList().get(0)).getF1().replace("'", "").endsWith("3"));
    Assertions.assertTrue(((Unary<String>) preparedStatementExecutionBoList.get(1).getPreparedStatementParameterList().get(1).getParameterList().get(0)).getF1().replace("'", "").endsWith("4"));
    Assertions.assertTrue(((Unary<String>) preparedStatementExecutionBoList.get(1).getPreparedStatementParameterList().get(2).getParameterList().get(0)).getF1().replace("'", "").endsWith("5"));

  }

  @Test
  void test_preparedStatement_execute_parameterize_batch_autoCommit_false_rollback() throws Exception {
    Connection connection = dataSource.getConnection();
    test_preparedStatement_execute_parameterize_batch_autoCommit_false_rollback(connection);


    List<DbExecution> dbExecutionList = dbWriter.getDbExecutionList();
    Assertions.assertEquals(2, dbExecutionList.size());

    Assertions.assertInstanceOf(PreparedStatementExecutionBo.class, dbExecutionList.get(0));
    Assertions.assertInstanceOf(PreparedStatementExecutionBo.class, dbExecutionList.get(1));

    List<PreparedStatementExecutionBo> preparedStatementExecutionBoList = dbExecutionList.stream().map(a -> ((PreparedStatementExecutionBo) a)).collect(Collectors.toList());

    Set<String> txIdSet = new HashSet<>();
    txIdSet.add(preparedStatementExecutionBoList.get(0).getTxId());
    txIdSet.add(preparedStatementExecutionBoList.get(1).getTxId());
    Assertions.assertEquals(1, txIdSet.size());

    Set<String> dataSourcePortTrailIdSet = new HashSet<>();
    dataSourcePortTrailIdSet.add(preparedStatementExecutionBoList.get(0).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(preparedStatementExecutionBoList.get(1).getDataSourcePortTrailId());
    Assertions.assertEquals(1, dataSourcePortTrailIdSet.size());

    Set<String> connectionPortTrailIdSet = new HashSet<>();
    connectionPortTrailIdSet.add(preparedStatementExecutionBoList.get(0).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(preparedStatementExecutionBoList.get(1).getConnectionPortTrailId());
    Assertions.assertEquals(1, connectionPortTrailIdSet.size());

    Set<String> statementPortTrailIdSet = new HashSet<>();
    statementPortTrailIdSet.add(preparedStatementExecutionBoList.get(0).getStatementPortTrailId());
    statementPortTrailIdSet.add(preparedStatementExecutionBoList.get(1).getStatementPortTrailId());
    Assertions.assertEquals(1, statementPortTrailIdSet.size());

    Assertions.assertEquals(3, preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().size());
    Assertions.assertEquals(3, preparedStatementExecutionBoList.get(1).getPreparedStatementParameterList().size());

    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_ROLLBACK, preparedStatementExecutionBoList.get(0).getStatementState());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_ROLLBACK, preparedStatementExecutionBoList.get(1).getStatementState());

    Assertions.assertEquals(1, preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().get(0).getOrderInStatement());
    Assertions.assertEquals(2, preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().get(1).getOrderInStatement());
    Assertions.assertEquals(3, preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().get(2).getOrderInStatement());
    Assertions.assertEquals(4, preparedStatementExecutionBoList.get(1).getPreparedStatementParameterList().get(0).getOrderInStatement());
    Assertions.assertEquals(5, preparedStatementExecutionBoList.get(1).getPreparedStatementParameterList().get(1).getOrderInStatement());
    Assertions.assertEquals(6, preparedStatementExecutionBoList.get(1).getPreparedStatementParameterList().get(2).getOrderInStatement());

    Assertions.assertEquals(1, preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().get(0).getOrderInConnection());
    Assertions.assertEquals(2, preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().get(1).getOrderInConnection());
    Assertions.assertEquals(3, preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().get(2).getOrderInConnection());
    Assertions.assertEquals(4, preparedStatementExecutionBoList.get(1).getPreparedStatementParameterList().get(0).getOrderInConnection());
    Assertions.assertEquals(5, preparedStatementExecutionBoList.get(1).getPreparedStatementParameterList().get(1).getOrderInConnection());
    Assertions.assertEquals(6, preparedStatementExecutionBoList.get(1).getPreparedStatementParameterList().get(2).getOrderInConnection());


    Assertions.assertTrue(((Unary<String>) preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().get(0).getParameterList().get(0)).getF1().replace("'", "").endsWith("0"));
    Assertions.assertTrue(((Unary<String>) preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().get(1).getParameterList().get(0)).getF1().replace("'", "").endsWith("1"));
    Assertions.assertTrue(((Unary<String>) preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().get(2).getParameterList().get(0)).getF1().replace("'", "").endsWith("2"));
    Assertions.assertTrue(((Unary<String>) preparedStatementExecutionBoList.get(1).getPreparedStatementParameterList().get(0).getParameterList().get(0)).getF1().replace("'", "").endsWith("3"));
    Assertions.assertTrue(((Unary<String>) preparedStatementExecutionBoList.get(1).getPreparedStatementParameterList().get(1).getParameterList().get(0)).getF1().replace("'", "").endsWith("4"));
    Assertions.assertTrue(((Unary<String>) preparedStatementExecutionBoList.get(1).getPreparedStatementParameterList().get(2).getParameterList().get(0)).getF1().replace("'", "").endsWith("5"));

  }

  @Test
  void test_preparedStatement_execute_parameterize_batch_autoCommit_true_commit() throws Exception {
    Connection connection = dataSource.getConnection();
    test_preparedStatement_execute_parameterize_batch_autoCommit_true_commit(connection);


    List<DbExecution> dbExecutionList = dbWriter.getDbExecutionList();
    Assertions.assertEquals(2, dbExecutionList.size());

    Assertions.assertInstanceOf(PreparedStatementExecutionBo.class, dbExecutionList.get(0));
    Assertions.assertInstanceOf(PreparedStatementExecutionBo.class, dbExecutionList.get(1));

    List<PreparedStatementExecutionBo> preparedStatementExecutionBoList = dbExecutionList.stream().map(a -> ((PreparedStatementExecutionBo) a)).collect(Collectors.toList());

    Set<String> txIdSet = new HashSet<>();
    txIdSet.add(preparedStatementExecutionBoList.get(0).getTxId());
    txIdSet.add(preparedStatementExecutionBoList.get(1).getTxId());
    Assertions.assertEquals(2, txIdSet.size());

    Set<String> dataSourcePortTrailIdSet = new HashSet<>();
    dataSourcePortTrailIdSet.add(preparedStatementExecutionBoList.get(0).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(preparedStatementExecutionBoList.get(1).getDataSourcePortTrailId());
    Assertions.assertEquals(1, dataSourcePortTrailIdSet.size());

    Set<String> connectionPortTrailIdSet = new HashSet<>();
    connectionPortTrailIdSet.add(preparedStatementExecutionBoList.get(0).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(preparedStatementExecutionBoList.get(1).getConnectionPortTrailId());
    Assertions.assertEquals(1, connectionPortTrailIdSet.size());

    Set<String> statementPortTrailIdSet = new HashSet<>();
    statementPortTrailIdSet.add(preparedStatementExecutionBoList.get(0).getStatementPortTrailId());
    statementPortTrailIdSet.add(preparedStatementExecutionBoList.get(1).getStatementPortTrailId());
    Assertions.assertEquals(1, statementPortTrailIdSet.size());

    Assertions.assertEquals(3, preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().size());
    Assertions.assertEquals(3, preparedStatementExecutionBoList.get(1).getPreparedStatementParameterList().size());

    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED, preparedStatementExecutionBoList.get(0).getStatementState());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED, preparedStatementExecutionBoList.get(1).getStatementState());

    Assertions.assertEquals(1, preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().get(0).getOrderInStatement());
    Assertions.assertEquals(2, preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().get(1).getOrderInStatement());
    Assertions.assertEquals(3, preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().get(2).getOrderInStatement());
    Assertions.assertEquals(4, preparedStatementExecutionBoList.get(1).getPreparedStatementParameterList().get(0).getOrderInStatement());
    Assertions.assertEquals(5, preparedStatementExecutionBoList.get(1).getPreparedStatementParameterList().get(1).getOrderInStatement());
    Assertions.assertEquals(6, preparedStatementExecutionBoList.get(1).getPreparedStatementParameterList().get(2).getOrderInStatement());

    Assertions.assertEquals(1, preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().get(0).getOrderInConnection());
    Assertions.assertEquals(2, preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().get(1).getOrderInConnection());
    Assertions.assertEquals(3, preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().get(2).getOrderInConnection());
    Assertions.assertEquals(4, preparedStatementExecutionBoList.get(1).getPreparedStatementParameterList().get(0).getOrderInConnection());
    Assertions.assertEquals(5, preparedStatementExecutionBoList.get(1).getPreparedStatementParameterList().get(1).getOrderInConnection());
    Assertions.assertEquals(6, preparedStatementExecutionBoList.get(1).getPreparedStatementParameterList().get(2).getOrderInConnection());

    Assertions.assertTrue(((Unary<String>) preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().get(0).getParameterList().get(0)).getF1().replace("'", "").endsWith("0"));
    Assertions.assertTrue(((Unary<String>) preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().get(1).getParameterList().get(0)).getF1().replace("'", "").endsWith("1"));
    Assertions.assertTrue(((Unary<String>) preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().get(2).getParameterList().get(0)).getF1().replace("'", "").endsWith("2"));
    Assertions.assertTrue(((Unary<String>) preparedStatementExecutionBoList.get(1).getPreparedStatementParameterList().get(0).getParameterList().get(0)).getF1().replace("'", "").endsWith("3"));
    Assertions.assertTrue(((Unary<String>) preparedStatementExecutionBoList.get(1).getPreparedStatementParameterList().get(1).getParameterList().get(0)).getF1().replace("'", "").endsWith("4"));
    Assertions.assertTrue(((Unary<String>) preparedStatementExecutionBoList.get(1).getPreparedStatementParameterList().get(2).getParameterList().get(0)).getF1().replace("'", "").endsWith("5"));

  }

  @Test
  void test_preparedStatement_execute_parameterize_batch_autoCommit_true_rollback() throws Exception {
    Connection connection = dataSource.getConnection();
    test_preparedStatement_execute_parameterize_batch_autoCommit_true_rollback(connection);


    List<DbExecution> dbExecutionList = dbWriter.getDbExecutionList();
    Assertions.assertEquals(2, dbExecutionList.size());

    Assertions.assertInstanceOf(PreparedStatementExecutionBo.class, dbExecutionList.get(0));
    Assertions.assertInstanceOf(PreparedStatementExecutionBo.class, dbExecutionList.get(1));

    List<PreparedStatementExecutionBo> preparedStatementExecutionBoList = dbExecutionList.stream().map(a -> ((PreparedStatementExecutionBo) a)).collect(Collectors.toList());

    Set<String> txIdSet = new HashSet<>();
    txIdSet.add(preparedStatementExecutionBoList.get(0).getTxId());
    txIdSet.add(preparedStatementExecutionBoList.get(1).getTxId());
    Assertions.assertEquals(2, txIdSet.size());

    Set<String> dataSourcePortTrailIdSet = new HashSet<>();
    dataSourcePortTrailIdSet.add(preparedStatementExecutionBoList.get(0).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(preparedStatementExecutionBoList.get(1).getDataSourcePortTrailId());
    Assertions.assertEquals(1, dataSourcePortTrailIdSet.size());

    Set<String> connectionPortTrailIdSet = new HashSet<>();
    connectionPortTrailIdSet.add(preparedStatementExecutionBoList.get(0).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(preparedStatementExecutionBoList.get(1).getConnectionPortTrailId());
    Assertions.assertEquals(1, connectionPortTrailIdSet.size());

    Set<String> statementPortTrailIdSet = new HashSet<>();
    statementPortTrailIdSet.add(preparedStatementExecutionBoList.get(0).getStatementPortTrailId());
    statementPortTrailIdSet.add(preparedStatementExecutionBoList.get(1).getStatementPortTrailId());
    Assertions.assertEquals(1, statementPortTrailIdSet.size());

    Assertions.assertEquals(3, preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().size());
    Assertions.assertEquals(3, preparedStatementExecutionBoList.get(1).getPreparedStatementParameterList().size());

    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED, preparedStatementExecutionBoList.get(0).getStatementState());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED, preparedStatementExecutionBoList.get(1).getStatementState());

    Assertions.assertEquals(1, preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().get(0).getOrderInStatement());
    Assertions.assertEquals(2, preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().get(1).getOrderInStatement());
    Assertions.assertEquals(3, preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().get(2).getOrderInStatement());
    Assertions.assertEquals(4, preparedStatementExecutionBoList.get(1).getPreparedStatementParameterList().get(0).getOrderInStatement());
    Assertions.assertEquals(5, preparedStatementExecutionBoList.get(1).getPreparedStatementParameterList().get(1).getOrderInStatement());
    Assertions.assertEquals(6, preparedStatementExecutionBoList.get(1).getPreparedStatementParameterList().get(2).getOrderInStatement());

    Assertions.assertEquals(1, preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().get(0).getOrderInConnection());
    Assertions.assertEquals(2, preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().get(1).getOrderInConnection());
    Assertions.assertEquals(3, preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().get(2).getOrderInConnection());
    Assertions.assertEquals(4, preparedStatementExecutionBoList.get(1).getPreparedStatementParameterList().get(0).getOrderInConnection());
    Assertions.assertEquals(5, preparedStatementExecutionBoList.get(1).getPreparedStatementParameterList().get(1).getOrderInConnection());
    Assertions.assertEquals(6, preparedStatementExecutionBoList.get(1).getPreparedStatementParameterList().get(2).getOrderInConnection());

    Assertions.assertTrue(((Unary<String>) preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().get(0).getParameterList().get(0)).getF1().replace("'", "").endsWith("0"));
    Assertions.assertTrue(((Unary<String>) preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().get(1).getParameterList().get(0)).getF1().replace("'", "").endsWith("1"));
    Assertions.assertTrue(((Unary<String>) preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().get(2).getParameterList().get(0)).getF1().replace("'", "").endsWith("2"));
    Assertions.assertTrue(((Unary<String>) preparedStatementExecutionBoList.get(1).getPreparedStatementParameterList().get(0).getParameterList().get(0)).getF1().replace("'", "").endsWith("3"));
    Assertions.assertTrue(((Unary<String>) preparedStatementExecutionBoList.get(1).getPreparedStatementParameterList().get(1).getParameterList().get(0)).getF1().replace("'", "").endsWith("4"));
    Assertions.assertTrue(((Unary<String>) preparedStatementExecutionBoList.get(1).getPreparedStatementParameterList().get(2).getParameterList().get(0)).getF1().replace("'", "").endsWith("5"));

  }

  @AfterEach
  void after() throws Exception {
    if (dataSource instanceof AutoCloseable) {
      ((AutoCloseable) dataSource).close();
    }
  }

}
