package cn.addenda.porttrail.jdbc.test.porttrail;

import cn.addenda.porttrail.common.pojo.db.bo.DbExecution;
import cn.addenda.porttrail.common.pojo.db.bo.AbstractStatementExecutionBo;
import cn.addenda.porttrail.common.pojo.db.bo.StatementExecutionBo;
import cn.addenda.porttrail.jdbc.core.PortTrailDataSource;
import cn.addenda.porttrail.jdbc.log.JdbcPortTrailLoggerFactory;
import cn.addenda.porttrail.jdbc.test.DbUtils;
import cn.addenda.porttrail.jdbc.test.jdbc.SingleModeJDBCTest;
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
 * 单个执行场景的单元测试。对应{@link SingleModeJDBCTest}里的场景。
 */
class SingleModePortTrailTest extends SingleModeJDBCTest {

  private DataSource dataSource;

  private JdbcTestDbWriter dbWriter;

  @BeforeEach
  void before() {
    dataSource = DbUtils.getDataSource();
    dbWriter = new JdbcTestDbWriter();
    dataSource = new PortTrailDataSource(dataSource, JdbcPortTrailLoggerFactory.getInstance(), dbWriter);
  }

  @Test
  void test_statement_execute_update_autoCommit_false_commit() throws Exception {
    Connection connection = dataSource.getConnection();
    test_statement_execute_update_autoCommit_false_commit(connection);


    List<DbExecution> dbExecutionList = dbWriter.getDbExecutionList();
    Assertions.assertEquals(3, dbExecutionList.size());

    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(0));
    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(1));
    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(2));

    List<StatementExecutionBo> statementExecutionBoList = dbExecutionList.stream().map(a -> ((StatementExecutionBo) a)).collect(Collectors.toList());

    Set<String> txIdSet = new HashSet<>();
    txIdSet.add(statementExecutionBoList.get(0).getTxId());
    txIdSet.add(statementExecutionBoList.get(1).getTxId());
    txIdSet.add(statementExecutionBoList.get(2).getTxId());
    Assertions.assertEquals(1, txIdSet.size());

    Set<String> dataSourcePortTrailIdSet = new HashSet<>();
    dataSourcePortTrailIdSet.add(statementExecutionBoList.get(0).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(statementExecutionBoList.get(1).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(statementExecutionBoList.get(2).getDataSourcePortTrailId());
    Assertions.assertEquals(1, dataSourcePortTrailIdSet.size());

    Set<String> connectionPortTrailIdSet = new HashSet<>();
    connectionPortTrailIdSet.add(statementExecutionBoList.get(0).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(statementExecutionBoList.get(1).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(statementExecutionBoList.get(2).getConnectionPortTrailId());
    Assertions.assertEquals(1, connectionPortTrailIdSet.size());

    Set<String> statementPortTrailIdSet = new HashSet<>();
    statementPortTrailIdSet.add(statementExecutionBoList.get(0).getStatementPortTrailId());
    statementPortTrailIdSet.add(statementExecutionBoList.get(1).getStatementPortTrailId());
    statementPortTrailIdSet.add(statementExecutionBoList.get(2).getStatementPortTrailId());
    Assertions.assertEquals(1, statementPortTrailIdSet.size());

    Assertions.assertEquals(1, statementExecutionBoList.get(0).getStatementSqlList().size());
    Assertions.assertEquals(1, statementExecutionBoList.get(1).getStatementSqlList().size());
    Assertions.assertEquals(1, statementExecutionBoList.get(2).getStatementSqlList().size());

    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED, statementExecutionBoList.get(0).getStatementState());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED, statementExecutionBoList.get(1).getStatementState());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED, statementExecutionBoList.get(2).getStatementState());

    Assertions.assertEquals(1, statementExecutionBoList.get(0).getStatementSqlList().get(0).getOrderInStatement());
    Assertions.assertEquals(2, statementExecutionBoList.get(1).getStatementSqlList().get(0).getOrderInStatement());
    Assertions.assertEquals(3, statementExecutionBoList.get(2).getStatementSqlList().get(0).getOrderInStatement());

    Assertions.assertEquals(1, statementExecutionBoList.get(0).getStatementSqlList().get(0).getOrderInConnection());
    Assertions.assertEquals(2, statementExecutionBoList.get(1).getStatementSqlList().get(0).getOrderInConnection());
    Assertions.assertEquals(3, statementExecutionBoList.get(2).getStatementSqlList().get(0).getOrderInConnection());

    Assertions.assertTrue(((StatementExecutionBo) statementExecutionBoList.get(0)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("0"));
    Assertions.assertTrue(((StatementExecutionBo) statementExecutionBoList.get(1)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("1"));
    Assertions.assertTrue(((StatementExecutionBo) statementExecutionBoList.get(2)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("2"));

  }

  @Test
  void test_statement_execute_update_autoCommit_false_rollback() throws Exception {
    Connection connection = dataSource.getConnection();
    test_statement_execute_update_autoCommit_false_rollback(connection);

    List<DbExecution> dbExecutionList = dbWriter.getDbExecutionList();
    Assertions.assertEquals(3, dbExecutionList.size());

    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(0));
    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(1));
    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(2));

    List<StatementExecutionBo> statementExecutionBoList = dbExecutionList.stream().map(a -> ((StatementExecutionBo) a)).collect(Collectors.toList());

    Set<String> txIdSet = new HashSet<>();
    txIdSet.add(statementExecutionBoList.get(0).getTxId());
    txIdSet.add(statementExecutionBoList.get(1).getTxId());
    txIdSet.add(statementExecutionBoList.get(2).getTxId());
    Assertions.assertEquals(1, txIdSet.size());

    Set<String> dataSourcePortTrailIdSet = new HashSet<>();
    dataSourcePortTrailIdSet.add(statementExecutionBoList.get(0).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(statementExecutionBoList.get(1).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(statementExecutionBoList.get(2).getDataSourcePortTrailId());
    Assertions.assertEquals(1, dataSourcePortTrailIdSet.size());

    Set<String> connectionPortTrailIdSet = new HashSet<>();
    connectionPortTrailIdSet.add(statementExecutionBoList.get(0).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(statementExecutionBoList.get(1).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(statementExecutionBoList.get(2).getConnectionPortTrailId());
    Assertions.assertEquals(1, connectionPortTrailIdSet.size());

    Set<String> statementPortTrailIdSet = new HashSet<>();
    statementPortTrailIdSet.add(statementExecutionBoList.get(0).getStatementPortTrailId());
    statementPortTrailIdSet.add(statementExecutionBoList.get(1).getStatementPortTrailId());
    statementPortTrailIdSet.add(statementExecutionBoList.get(2).getStatementPortTrailId());
    Assertions.assertEquals(1, statementPortTrailIdSet.size());

    Assertions.assertEquals(1, statementExecutionBoList.get(0).getStatementSqlList().size());
    Assertions.assertEquals(1, statementExecutionBoList.get(1).getStatementSqlList().size());
    Assertions.assertEquals(1, statementExecutionBoList.get(2).getStatementSqlList().size());

    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_ROLLBACK, statementExecutionBoList.get(0).getStatementState());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_ROLLBACK, statementExecutionBoList.get(1).getStatementState());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_ROLLBACK, statementExecutionBoList.get(2).getStatementState());

    Assertions.assertEquals(1, statementExecutionBoList.get(0).getStatementSqlList().get(0).getOrderInStatement());
    Assertions.assertEquals(2, statementExecutionBoList.get(1).getStatementSqlList().get(0).getOrderInStatement());
    Assertions.assertEquals(3, statementExecutionBoList.get(2).getStatementSqlList().get(0).getOrderInStatement());

    Assertions.assertEquals(1, statementExecutionBoList.get(0).getStatementSqlList().get(0).getOrderInConnection());
    Assertions.assertEquals(2, statementExecutionBoList.get(1).getStatementSqlList().get(0).getOrderInConnection());
    Assertions.assertEquals(3, statementExecutionBoList.get(2).getStatementSqlList().get(0).getOrderInConnection());

    Assertions.assertTrue(((StatementExecutionBo) statementExecutionBoList.get(0)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("0"));
    Assertions.assertTrue(((StatementExecutionBo) statementExecutionBoList.get(1)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("1"));
    Assertions.assertTrue(((StatementExecutionBo) statementExecutionBoList.get(2)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("2"));

  }

  @Test
  void test_statement_execute_update_autoCommit_true_commit() throws Exception {
    Connection connection = dataSource.getConnection();
    test_statement_execute_update_autoCommit_true_commit(connection);


    List<DbExecution> dbExecutionList = dbWriter.getDbExecutionList();
    Assertions.assertEquals(3, dbExecutionList.size());

    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(0));
    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(1));
    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(2));

    List<StatementExecutionBo> statementExecutionBoList = dbExecutionList.stream().map(a -> ((StatementExecutionBo) a)).collect(Collectors.toList());

    Set<String> txIdSet = new HashSet<>();
    txIdSet.add(statementExecutionBoList.get(0).getTxId());
    txIdSet.add(statementExecutionBoList.get(1).getTxId());
    txIdSet.add(statementExecutionBoList.get(2).getTxId());
    Assertions.assertEquals(3, txIdSet.size());

    Set<String> dataSourcePortTrailIdSet = new HashSet<>();
    dataSourcePortTrailIdSet.add(statementExecutionBoList.get(0).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(statementExecutionBoList.get(1).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(statementExecutionBoList.get(2).getDataSourcePortTrailId());
    Assertions.assertEquals(1, dataSourcePortTrailIdSet.size());

    Set<String> connectionPortTrailIdSet = new HashSet<>();
    connectionPortTrailIdSet.add(statementExecutionBoList.get(0).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(statementExecutionBoList.get(1).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(statementExecutionBoList.get(2).getConnectionPortTrailId());
    Assertions.assertEquals(1, connectionPortTrailIdSet.size());

    Set<String> statementPortTrailIdSet = new HashSet<>();
    statementPortTrailIdSet.add(statementExecutionBoList.get(0).getStatementPortTrailId());
    statementPortTrailIdSet.add(statementExecutionBoList.get(1).getStatementPortTrailId());
    statementPortTrailIdSet.add(statementExecutionBoList.get(2).getStatementPortTrailId());
    Assertions.assertEquals(1, statementPortTrailIdSet.size());

    Assertions.assertEquals(1, statementExecutionBoList.get(0).getStatementSqlList().size());
    Assertions.assertEquals(1, statementExecutionBoList.get(1).getStatementSqlList().size());
    Assertions.assertEquals(1, statementExecutionBoList.get(2).getStatementSqlList().size());

    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED, statementExecutionBoList.get(0).getStatementState());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED, statementExecutionBoList.get(1).getStatementState());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED, statementExecutionBoList.get(2).getStatementState());

    Assertions.assertEquals(1, statementExecutionBoList.get(0).getStatementSqlList().get(0).getOrderInStatement());
    Assertions.assertEquals(2, statementExecutionBoList.get(1).getStatementSqlList().get(0).getOrderInStatement());
    Assertions.assertEquals(3, statementExecutionBoList.get(2).getStatementSqlList().get(0).getOrderInStatement());

    Assertions.assertEquals(1, statementExecutionBoList.get(0).getStatementSqlList().get(0).getOrderInConnection());
    Assertions.assertEquals(2, statementExecutionBoList.get(1).getStatementSqlList().get(0).getOrderInConnection());
    Assertions.assertEquals(3, statementExecutionBoList.get(2).getStatementSqlList().get(0).getOrderInConnection());

    Assertions.assertTrue(((StatementExecutionBo) statementExecutionBoList.get(0)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("0"));
    Assertions.assertTrue(((StatementExecutionBo) statementExecutionBoList.get(1)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("1"));
    Assertions.assertTrue(((StatementExecutionBo) statementExecutionBoList.get(2)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("2"));

  }

  @Test
  void test_statement_execute_update_autoCommit_true_rollback() throws Exception {
    Connection connection = dataSource.getConnection();
    test_statement_execute_update_autoCommit_true_rollback(connection);


    List<DbExecution> dbExecutionList = dbWriter.getDbExecutionList();
    Assertions.assertEquals(3, dbExecutionList.size());

    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(0));
    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(1));
    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(2));

    List<StatementExecutionBo> statementExecutionBoList = dbExecutionList.stream().map(a -> ((StatementExecutionBo) a)).collect(Collectors.toList());

    Set<String> txIdSet = new HashSet<>();
    txIdSet.add(statementExecutionBoList.get(0).getTxId());
    txIdSet.add(statementExecutionBoList.get(1).getTxId());
    txIdSet.add(statementExecutionBoList.get(2).getTxId());
    Assertions.assertEquals(3, txIdSet.size());

    Set<String> dataSourcePortTrailIdSet = new HashSet<>();
    dataSourcePortTrailIdSet.add(statementExecutionBoList.get(0).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(statementExecutionBoList.get(1).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(statementExecutionBoList.get(2).getDataSourcePortTrailId());
    Assertions.assertEquals(1, dataSourcePortTrailIdSet.size());

    Set<String> connectionPortTrailIdSet = new HashSet<>();
    connectionPortTrailIdSet.add(statementExecutionBoList.get(0).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(statementExecutionBoList.get(1).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(statementExecutionBoList.get(2).getConnectionPortTrailId());
    Assertions.assertEquals(1, connectionPortTrailIdSet.size());

    Set<String> statementPortTrailIdSet = new HashSet<>();
    statementPortTrailIdSet.add(statementExecutionBoList.get(0).getStatementPortTrailId());
    statementPortTrailIdSet.add(statementExecutionBoList.get(1).getStatementPortTrailId());
    statementPortTrailIdSet.add(statementExecutionBoList.get(2).getStatementPortTrailId());
    Assertions.assertEquals(1, statementPortTrailIdSet.size());

    Assertions.assertEquals(1, statementExecutionBoList.get(0).getStatementSqlList().size());
    Assertions.assertEquals(1, statementExecutionBoList.get(1).getStatementSqlList().size());
    Assertions.assertEquals(1, statementExecutionBoList.get(2).getStatementSqlList().size());

    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED, statementExecutionBoList.get(0).getStatementState());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED, statementExecutionBoList.get(1).getStatementState());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED, statementExecutionBoList.get(2).getStatementState());

    Assertions.assertEquals(1, statementExecutionBoList.get(0).getStatementSqlList().get(0).getOrderInStatement());
    Assertions.assertEquals(2, statementExecutionBoList.get(1).getStatementSqlList().get(0).getOrderInStatement());
    Assertions.assertEquals(3, statementExecutionBoList.get(2).getStatementSqlList().get(0).getOrderInStatement());

    Assertions.assertEquals(1, statementExecutionBoList.get(0).getStatementSqlList().get(0).getOrderInConnection());
    Assertions.assertEquals(2, statementExecutionBoList.get(1).getStatementSqlList().get(0).getOrderInConnection());
    Assertions.assertEquals(3, statementExecutionBoList.get(2).getStatementSqlList().get(0).getOrderInConnection());

    Assertions.assertTrue(((StatementExecutionBo) statementExecutionBoList.get(0)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("0"));
    Assertions.assertTrue(((StatementExecutionBo) statementExecutionBoList.get(1)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("1"));
    Assertions.assertTrue(((StatementExecutionBo) statementExecutionBoList.get(2)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("2"));

  }

  @Test
  void test_statement_execute_query_autoCommit_false_commit() throws Exception {
    Connection connection = dataSource.getConnection();
    test_statement_execute_query_autoCommit_false_commit(connection);


    List<DbExecution> dbExecutionList = dbWriter.getDbExecutionList();
    Assertions.assertEquals(3, dbExecutionList.size());

    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(0));
    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(1));
    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(2));

    List<StatementExecutionBo> statementExecutionBoList = dbExecutionList.stream().map(a -> ((StatementExecutionBo) a)).collect(Collectors.toList());

    Set<String> txIdSet = new HashSet<>();
    txIdSet.add(statementExecutionBoList.get(0).getTxId());
    txIdSet.add(statementExecutionBoList.get(1).getTxId());
    txIdSet.add(statementExecutionBoList.get(2).getTxId());
    Assertions.assertEquals(1, txIdSet.size());

    Set<String> dataSourcePortTrailIdSet = new HashSet<>();
    dataSourcePortTrailIdSet.add(statementExecutionBoList.get(0).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(statementExecutionBoList.get(1).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(statementExecutionBoList.get(2).getDataSourcePortTrailId());
    Assertions.assertEquals(1, dataSourcePortTrailIdSet.size());

    Set<String> connectionPortTrailIdSet = new HashSet<>();
    connectionPortTrailIdSet.add(statementExecutionBoList.get(0).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(statementExecutionBoList.get(1).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(statementExecutionBoList.get(2).getConnectionPortTrailId());
    Assertions.assertEquals(1, connectionPortTrailIdSet.size());

    Set<String> statementPortTrailIdSet = new HashSet<>();
    statementPortTrailIdSet.add(statementExecutionBoList.get(0).getStatementPortTrailId());
    statementPortTrailIdSet.add(statementExecutionBoList.get(1).getStatementPortTrailId());
    statementPortTrailIdSet.add(statementExecutionBoList.get(2).getStatementPortTrailId());
    Assertions.assertEquals(1, statementPortTrailIdSet.size());

    Assertions.assertEquals(1, statementExecutionBoList.get(0).getStatementSqlList().size());
    Assertions.assertEquals(1, statementExecutionBoList.get(1).getStatementSqlList().size());
    Assertions.assertEquals(1, statementExecutionBoList.get(2).getStatementSqlList().size());

    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_QUERY, statementExecutionBoList.get(0).getStatementState());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_QUERY, statementExecutionBoList.get(1).getStatementState());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_QUERY, statementExecutionBoList.get(2).getStatementState());

    Assertions.assertEquals(1, statementExecutionBoList.get(0).getStatementSqlList().get(0).getOrderInStatement());
    Assertions.assertEquals(2, statementExecutionBoList.get(1).getStatementSqlList().get(0).getOrderInStatement());
    Assertions.assertEquals(3, statementExecutionBoList.get(2).getStatementSqlList().get(0).getOrderInStatement());

    Assertions.assertEquals(1, statementExecutionBoList.get(0).getStatementSqlList().get(0).getOrderInConnection());
    Assertions.assertEquals(2, statementExecutionBoList.get(1).getStatementSqlList().get(0).getOrderInConnection());
    Assertions.assertEquals(3, statementExecutionBoList.get(2).getStatementSqlList().get(0).getOrderInConnection());

    Assertions.assertTrue(((StatementExecutionBo) statementExecutionBoList.get(0)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("0"));
    Assertions.assertTrue(((StatementExecutionBo) statementExecutionBoList.get(1)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("1"));
    Assertions.assertTrue(((StatementExecutionBo) statementExecutionBoList.get(2)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("2"));

  }

  @Test
  void test_statement_execute_query_autoCommit_false_rollback() throws Exception {
    Connection connection = dataSource.getConnection();
    test_statement_execute_query_autoCommit_false_rollback(connection);


    List<DbExecution> dbExecutionList = dbWriter.getDbExecutionList();
    Assertions.assertEquals(3, dbExecutionList.size());

    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(0));
    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(1));
    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(2));

    List<StatementExecutionBo> statementExecutionBoList = dbExecutionList.stream().map(a -> ((StatementExecutionBo) a)).collect(Collectors.toList());

    Set<String> txIdSet = new HashSet<>();
    txIdSet.add(statementExecutionBoList.get(0).getTxId());
    txIdSet.add(statementExecutionBoList.get(1).getTxId());
    txIdSet.add(statementExecutionBoList.get(2).getTxId());
    Assertions.assertEquals(1, txIdSet.size());

    Set<String> dataSourcePortTrailIdSet = new HashSet<>();
    dataSourcePortTrailIdSet.add(statementExecutionBoList.get(0).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(statementExecutionBoList.get(1).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(statementExecutionBoList.get(2).getDataSourcePortTrailId());
    Assertions.assertEquals(1, dataSourcePortTrailIdSet.size());

    Set<String> connectionPortTrailIdSet = new HashSet<>();
    connectionPortTrailIdSet.add(statementExecutionBoList.get(0).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(statementExecutionBoList.get(1).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(statementExecutionBoList.get(2).getConnectionPortTrailId());
    Assertions.assertEquals(1, connectionPortTrailIdSet.size());

    Set<String> statementPortTrailIdSet = new HashSet<>();
    statementPortTrailIdSet.add(statementExecutionBoList.get(0).getStatementPortTrailId());
    statementPortTrailIdSet.add(statementExecutionBoList.get(1).getStatementPortTrailId());
    statementPortTrailIdSet.add(statementExecutionBoList.get(2).getStatementPortTrailId());
    Assertions.assertEquals(1, statementPortTrailIdSet.size());

    Assertions.assertEquals(1, statementExecutionBoList.get(0).getStatementSqlList().size());
    Assertions.assertEquals(1, statementExecutionBoList.get(1).getStatementSqlList().size());
    Assertions.assertEquals(1, statementExecutionBoList.get(2).getStatementSqlList().size());

    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_QUERY, statementExecutionBoList.get(0).getStatementState());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_QUERY, statementExecutionBoList.get(1).getStatementState());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_QUERY, statementExecutionBoList.get(2).getStatementState());

    Assertions.assertEquals(1, statementExecutionBoList.get(0).getStatementSqlList().get(0).getOrderInStatement());
    Assertions.assertEquals(2, statementExecutionBoList.get(1).getStatementSqlList().get(0).getOrderInStatement());
    Assertions.assertEquals(3, statementExecutionBoList.get(2).getStatementSqlList().get(0).getOrderInStatement());

    Assertions.assertEquals(1, statementExecutionBoList.get(0).getStatementSqlList().get(0).getOrderInConnection());
    Assertions.assertEquals(2, statementExecutionBoList.get(1).getStatementSqlList().get(0).getOrderInConnection());
    Assertions.assertEquals(3, statementExecutionBoList.get(2).getStatementSqlList().get(0).getOrderInConnection());


    Assertions.assertTrue(((StatementExecutionBo) statementExecutionBoList.get(0)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("0"));
    Assertions.assertTrue(((StatementExecutionBo) statementExecutionBoList.get(1)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("1"));
    Assertions.assertTrue(((StatementExecutionBo) statementExecutionBoList.get(2)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("2"));

  }

  @Test
  void test_statement_execute_query_autoCommit_true_commit() throws Exception {
    Connection connection = dataSource.getConnection();
    test_statement_execute_query_autoCommit_true_commit(connection);


    List<DbExecution> dbExecutionList = dbWriter.getDbExecutionList();
    Assertions.assertEquals(3, dbExecutionList.size());

    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(0));
    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(1));
    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(2));

    List<StatementExecutionBo> statementExecutionBoList = dbExecutionList.stream().map(a -> ((StatementExecutionBo) a)).collect(Collectors.toList());

    Set<String> txIdSet = new HashSet<>();
    txIdSet.add(statementExecutionBoList.get(0).getTxId());
    txIdSet.add(statementExecutionBoList.get(1).getTxId());
    txIdSet.add(statementExecutionBoList.get(2).getTxId());
    Assertions.assertEquals(3, txIdSet.size());

    Set<String> dataSourcePortTrailIdSet = new HashSet<>();
    dataSourcePortTrailIdSet.add(statementExecutionBoList.get(0).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(statementExecutionBoList.get(1).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(statementExecutionBoList.get(2).getDataSourcePortTrailId());
    Assertions.assertEquals(1, dataSourcePortTrailIdSet.size());

    Set<String> connectionPortTrailIdSet = new HashSet<>();
    connectionPortTrailIdSet.add(statementExecutionBoList.get(0).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(statementExecutionBoList.get(1).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(statementExecutionBoList.get(2).getConnectionPortTrailId());
    Assertions.assertEquals(1, connectionPortTrailIdSet.size());

    Set<String> statementPortTrailIdSet = new HashSet<>();
    statementPortTrailIdSet.add(statementExecutionBoList.get(0).getStatementPortTrailId());
    statementPortTrailIdSet.add(statementExecutionBoList.get(1).getStatementPortTrailId());
    statementPortTrailIdSet.add(statementExecutionBoList.get(2).getStatementPortTrailId());
    Assertions.assertEquals(1, statementPortTrailIdSet.size());

    Assertions.assertEquals(1, statementExecutionBoList.get(0).getStatementSqlList().size());
    Assertions.assertEquals(1, statementExecutionBoList.get(1).getStatementSqlList().size());
    Assertions.assertEquals(1, statementExecutionBoList.get(2).getStatementSqlList().size());

    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_QUERY, statementExecutionBoList.get(0).getStatementState());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_QUERY, statementExecutionBoList.get(1).getStatementState());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_QUERY, statementExecutionBoList.get(2).getStatementState());

    Assertions.assertEquals(1, statementExecutionBoList.get(0).getStatementSqlList().get(0).getOrderInStatement());
    Assertions.assertEquals(2, statementExecutionBoList.get(1).getStatementSqlList().get(0).getOrderInStatement());
    Assertions.assertEquals(3, statementExecutionBoList.get(2).getStatementSqlList().get(0).getOrderInStatement());

    Assertions.assertEquals(1, statementExecutionBoList.get(0).getStatementSqlList().get(0).getOrderInConnection());
    Assertions.assertEquals(2, statementExecutionBoList.get(1).getStatementSqlList().get(0).getOrderInConnection());
    Assertions.assertEquals(3, statementExecutionBoList.get(2).getStatementSqlList().get(0).getOrderInConnection());


    Assertions.assertTrue(((StatementExecutionBo) statementExecutionBoList.get(0)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("0"));
    Assertions.assertTrue(((StatementExecutionBo) statementExecutionBoList.get(1)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("1"));
    Assertions.assertTrue(((StatementExecutionBo) statementExecutionBoList.get(2)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("2"));

  }

  @Test
  void test_statement_execute_query_auto_commit_true_rollback() throws Exception {
    Connection connection = dataSource.getConnection();
    test_statement_execute_query_autoCommit_true_rollback(connection);

    List<DbExecution> dbExecutionList = dbWriter.getDbExecutionList();
    Assertions.assertEquals(3, dbExecutionList.size());

    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(0));
    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(1));
    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(2));

    List<StatementExecutionBo> statementExecutionBoList = dbExecutionList.stream().map(a -> ((StatementExecutionBo) a)).collect(Collectors.toList());

    Set<String> txIdSet = new HashSet<>();
    txIdSet.add(statementExecutionBoList.get(0).getTxId());
    txIdSet.add(statementExecutionBoList.get(1).getTxId());
    txIdSet.add(statementExecutionBoList.get(2).getTxId());
    Assertions.assertEquals(3, txIdSet.size());

    Set<String> dataSourcePortTrailIdSet = new HashSet<>();
    dataSourcePortTrailIdSet.add(statementExecutionBoList.get(0).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(statementExecutionBoList.get(1).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(statementExecutionBoList.get(2).getDataSourcePortTrailId());
    Assertions.assertEquals(1, dataSourcePortTrailIdSet.size());

    Set<String> connectionPortTrailIdSet = new HashSet<>();
    connectionPortTrailIdSet.add(statementExecutionBoList.get(0).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(statementExecutionBoList.get(1).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(statementExecutionBoList.get(2).getConnectionPortTrailId());
    Assertions.assertEquals(1, connectionPortTrailIdSet.size());

    Set<String> statementPortTrailIdSet = new HashSet<>();
    statementPortTrailIdSet.add(statementExecutionBoList.get(0).getStatementPortTrailId());
    statementPortTrailIdSet.add(statementExecutionBoList.get(1).getStatementPortTrailId());
    statementPortTrailIdSet.add(statementExecutionBoList.get(2).getStatementPortTrailId());
    Assertions.assertEquals(1, statementPortTrailIdSet.size());

    Assertions.assertEquals(1, statementExecutionBoList.get(0).getStatementSqlList().size());
    Assertions.assertEquals(1, statementExecutionBoList.get(1).getStatementSqlList().size());
    Assertions.assertEquals(1, statementExecutionBoList.get(2).getStatementSqlList().size());

    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_QUERY, statementExecutionBoList.get(0).getStatementState());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_QUERY, statementExecutionBoList.get(1).getStatementState());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_QUERY, statementExecutionBoList.get(2).getStatementState());

    Assertions.assertEquals(1, statementExecutionBoList.get(0).getStatementSqlList().get(0).getOrderInStatement());
    Assertions.assertEquals(2, statementExecutionBoList.get(1).getStatementSqlList().get(0).getOrderInStatement());
    Assertions.assertEquals(3, statementExecutionBoList.get(2).getStatementSqlList().get(0).getOrderInStatement());

    Assertions.assertEquals(1, statementExecutionBoList.get(0).getStatementSqlList().get(0).getOrderInConnection());
    Assertions.assertEquals(2, statementExecutionBoList.get(1).getStatementSqlList().get(0).getOrderInConnection());
    Assertions.assertEquals(3, statementExecutionBoList.get(2).getStatementSqlList().get(0).getOrderInConnection());


    Assertions.assertTrue(((StatementExecutionBo) statementExecutionBoList.get(0)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("0"));
    Assertions.assertTrue(((StatementExecutionBo) statementExecutionBoList.get(1)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("1"));
    Assertions.assertTrue(((StatementExecutionBo) statementExecutionBoList.get(2)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("2"));

  }

  @Test
  void test_preparedStatement_execute_update_autoCommit_false_commit() throws Exception {
    Connection connection = dataSource.getConnection();
    test_preparedStatement_execute_update_autoCommit_false_commit(connection);


    List<DbExecution> dbExecutionList = dbWriter.getDbExecutionList();
    Assertions.assertEquals(3, dbExecutionList.size());

    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(0));
    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(1));
    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(2));

    List<StatementExecutionBo> statementExecutionBoList = dbExecutionList.stream().map(a -> ((StatementExecutionBo) a)).collect(Collectors.toList());

    Set<String> txIdSet = new HashSet<>();
    txIdSet.add(statementExecutionBoList.get(0).getTxId());
    txIdSet.add(statementExecutionBoList.get(1).getTxId());
    txIdSet.add(statementExecutionBoList.get(2).getTxId());
    Assertions.assertEquals(1, txIdSet.size());

    Set<String> dataSourcePortTrailIdSet = new HashSet<>();
    dataSourcePortTrailIdSet.add(statementExecutionBoList.get(0).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(statementExecutionBoList.get(1).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(statementExecutionBoList.get(2).getDataSourcePortTrailId());
    Assertions.assertEquals(1, dataSourcePortTrailIdSet.size());

    Set<String> connectionPortTrailIdSet = new HashSet<>();
    connectionPortTrailIdSet.add(statementExecutionBoList.get(0).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(statementExecutionBoList.get(1).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(statementExecutionBoList.get(2).getConnectionPortTrailId());
    Assertions.assertEquals(1, connectionPortTrailIdSet.size());

    Set<String> statementPortTrailIdSet = new HashSet<>();
    statementPortTrailIdSet.add(statementExecutionBoList.get(0).getStatementPortTrailId());
    statementPortTrailIdSet.add(statementExecutionBoList.get(1).getStatementPortTrailId());
    statementPortTrailIdSet.add(statementExecutionBoList.get(2).getStatementPortTrailId());
    Assertions.assertEquals(1, statementPortTrailIdSet.size());

    Assertions.assertEquals(1, statementExecutionBoList.get(0).getStatementSqlList().size());
    Assertions.assertEquals(1, statementExecutionBoList.get(1).getStatementSqlList().size());
    Assertions.assertEquals(1, statementExecutionBoList.get(2).getStatementSqlList().size());

    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED, statementExecutionBoList.get(0).getStatementState());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED, statementExecutionBoList.get(1).getStatementState());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED, statementExecutionBoList.get(2).getStatementState());

    Assertions.assertEquals(1, statementExecutionBoList.get(0).getStatementSqlList().get(0).getOrderInStatement());
    Assertions.assertEquals(2, statementExecutionBoList.get(1).getStatementSqlList().get(0).getOrderInStatement());
    Assertions.assertEquals(3, statementExecutionBoList.get(2).getStatementSqlList().get(0).getOrderInStatement());

    Assertions.assertEquals(1, statementExecutionBoList.get(0).getStatementSqlList().get(0).getOrderInConnection());
    Assertions.assertEquals(2, statementExecutionBoList.get(1).getStatementSqlList().get(0).getOrderInConnection());
    Assertions.assertEquals(3, statementExecutionBoList.get(2).getStatementSqlList().get(0).getOrderInConnection());

    Assertions.assertTrue(((StatementExecutionBo) statementExecutionBoList.get(0)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("0"));
    Assertions.assertTrue(((StatementExecutionBo) statementExecutionBoList.get(1)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("1"));
    Assertions.assertTrue(((StatementExecutionBo) statementExecutionBoList.get(2)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("2"));

  }

  @Test
  void test_preparedStatement_execute_update_autoCommit_false_rollback() throws Exception {
    Connection connection = dataSource.getConnection();
    test_preparedStatement_execute_update_autoCommit_false_rollback(connection);


    List<DbExecution> dbExecutionList = dbWriter.getDbExecutionList();
    Assertions.assertEquals(3, dbExecutionList.size());

    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(0));
    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(1));
    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(2));

    List<StatementExecutionBo> statementExecutionBoList = dbExecutionList.stream().map(a -> ((StatementExecutionBo) a)).collect(Collectors.toList());

    Set<String> txIdSet = new HashSet<>();
    txIdSet.add(statementExecutionBoList.get(0).getTxId());
    txIdSet.add(statementExecutionBoList.get(1).getTxId());
    txIdSet.add(statementExecutionBoList.get(2).getTxId());
    Assertions.assertEquals(1, txIdSet.size());

    Set<String> dataSourcePortTrailIdSet = new HashSet<>();
    dataSourcePortTrailIdSet.add(statementExecutionBoList.get(0).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(statementExecutionBoList.get(1).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(statementExecutionBoList.get(2).getDataSourcePortTrailId());
    Assertions.assertEquals(1, dataSourcePortTrailIdSet.size());

    Set<String> connectionPortTrailIdSet = new HashSet<>();
    connectionPortTrailIdSet.add(statementExecutionBoList.get(0).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(statementExecutionBoList.get(1).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(statementExecutionBoList.get(2).getConnectionPortTrailId());
    Assertions.assertEquals(1, connectionPortTrailIdSet.size());

    Set<String> statementPortTrailIdSet = new HashSet<>();
    statementPortTrailIdSet.add(statementExecutionBoList.get(0).getStatementPortTrailId());
    statementPortTrailIdSet.add(statementExecutionBoList.get(1).getStatementPortTrailId());
    statementPortTrailIdSet.add(statementExecutionBoList.get(2).getStatementPortTrailId());
    Assertions.assertEquals(1, statementPortTrailIdSet.size());

    Assertions.assertEquals(1, statementExecutionBoList.get(0).getStatementSqlList().size());
    Assertions.assertEquals(1, statementExecutionBoList.get(1).getStatementSqlList().size());
    Assertions.assertEquals(1, statementExecutionBoList.get(2).getStatementSqlList().size());

    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_ROLLBACK, statementExecutionBoList.get(0).getStatementState());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_ROLLBACK, statementExecutionBoList.get(1).getStatementState());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_ROLLBACK, statementExecutionBoList.get(2).getStatementState());

    Assertions.assertEquals(1, statementExecutionBoList.get(0).getStatementSqlList().get(0).getOrderInStatement());
    Assertions.assertEquals(2, statementExecutionBoList.get(1).getStatementSqlList().get(0).getOrderInStatement());
    Assertions.assertEquals(3, statementExecutionBoList.get(2).getStatementSqlList().get(0).getOrderInStatement());

    Assertions.assertEquals(1, statementExecutionBoList.get(0).getStatementSqlList().get(0).getOrderInConnection());
    Assertions.assertEquals(2, statementExecutionBoList.get(1).getStatementSqlList().get(0).getOrderInConnection());
    Assertions.assertEquals(3, statementExecutionBoList.get(2).getStatementSqlList().get(0).getOrderInConnection());


    Assertions.assertTrue(((StatementExecutionBo) statementExecutionBoList.get(0)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("0"));
    Assertions.assertTrue(((StatementExecutionBo) statementExecutionBoList.get(1)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("1"));
    Assertions.assertTrue(((StatementExecutionBo) statementExecutionBoList.get(2)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("2"));

  }

  @Test
  void test_preparedStatement_execute_update_autoCommit_true_commit() throws Exception {
    Connection connection = dataSource.getConnection();
    test_preparedStatement_execute_update_autoCommit_true_commit(connection);


    List<DbExecution> dbExecutionList = dbWriter.getDbExecutionList();
    Assertions.assertEquals(3, dbExecutionList.size());

    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(0));
    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(1));
    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(2));

    List<StatementExecutionBo> statementExecutionBoList = dbExecutionList.stream().map(a -> ((StatementExecutionBo) a)).collect(Collectors.toList());

    Set<String> txIdSet = new HashSet<>();
    txIdSet.add(statementExecutionBoList.get(0).getTxId());
    txIdSet.add(statementExecutionBoList.get(1).getTxId());
    txIdSet.add(statementExecutionBoList.get(2).getTxId());
    Assertions.assertEquals(3, txIdSet.size());

    Set<String> dataSourcePortTrailIdSet = new HashSet<>();
    dataSourcePortTrailIdSet.add(statementExecutionBoList.get(0).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(statementExecutionBoList.get(1).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(statementExecutionBoList.get(2).getDataSourcePortTrailId());
    Assertions.assertEquals(1, dataSourcePortTrailIdSet.size());

    Set<String> connectionPortTrailIdSet = new HashSet<>();
    connectionPortTrailIdSet.add(statementExecutionBoList.get(0).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(statementExecutionBoList.get(1).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(statementExecutionBoList.get(2).getConnectionPortTrailId());
    Assertions.assertEquals(1, connectionPortTrailIdSet.size());

    Set<String> statementPortTrailIdSet = new HashSet<>();
    statementPortTrailIdSet.add(statementExecutionBoList.get(0).getStatementPortTrailId());
    statementPortTrailIdSet.add(statementExecutionBoList.get(1).getStatementPortTrailId());
    statementPortTrailIdSet.add(statementExecutionBoList.get(2).getStatementPortTrailId());
    Assertions.assertEquals(1, statementPortTrailIdSet.size());

    Assertions.assertEquals(1, statementExecutionBoList.get(0).getStatementSqlList().size());
    Assertions.assertEquals(1, statementExecutionBoList.get(1).getStatementSqlList().size());
    Assertions.assertEquals(1, statementExecutionBoList.get(2).getStatementSqlList().size());

    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED, statementExecutionBoList.get(0).getStatementState());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED, statementExecutionBoList.get(1).getStatementState());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED, statementExecutionBoList.get(2).getStatementState());

    Assertions.assertEquals(1, statementExecutionBoList.get(0).getStatementSqlList().get(0).getOrderInStatement());
    Assertions.assertEquals(2, statementExecutionBoList.get(1).getStatementSqlList().get(0).getOrderInStatement());
    Assertions.assertEquals(3, statementExecutionBoList.get(2).getStatementSqlList().get(0).getOrderInStatement());

    Assertions.assertEquals(1, statementExecutionBoList.get(0).getStatementSqlList().get(0).getOrderInConnection());
    Assertions.assertEquals(2, statementExecutionBoList.get(1).getStatementSqlList().get(0).getOrderInConnection());
    Assertions.assertEquals(3, statementExecutionBoList.get(2).getStatementSqlList().get(0).getOrderInConnection());


    Assertions.assertTrue(((StatementExecutionBo) statementExecutionBoList.get(0)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("0"));
    Assertions.assertTrue(((StatementExecutionBo) statementExecutionBoList.get(1)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("1"));
    Assertions.assertTrue(((StatementExecutionBo) statementExecutionBoList.get(2)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("2"));

  }

  @Test
  void test_preparedStatement_execute_update_autoCommit_true_rollback() throws Exception {
    Connection connection = dataSource.getConnection();
    test_preparedStatement_execute_update_autoCommit_true_rollback(connection);


    List<DbExecution> dbExecutionList = dbWriter.getDbExecutionList();
    Assertions.assertEquals(3, dbExecutionList.size());

    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(0));
    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(1));
    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(2));

    List<StatementExecutionBo> statementExecutionBoList = dbExecutionList.stream().map(a -> ((StatementExecutionBo) a)).collect(Collectors.toList());

    Set<String> txIdSet = new HashSet<>();
    txIdSet.add(statementExecutionBoList.get(0).getTxId());
    txIdSet.add(statementExecutionBoList.get(1).getTxId());
    txIdSet.add(statementExecutionBoList.get(2).getTxId());
    Assertions.assertEquals(3, txIdSet.size());

    Set<String> dataSourcePortTrailIdSet = new HashSet<>();
    dataSourcePortTrailIdSet.add(statementExecutionBoList.get(0).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(statementExecutionBoList.get(1).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(statementExecutionBoList.get(2).getDataSourcePortTrailId());
    Assertions.assertEquals(1, dataSourcePortTrailIdSet.size());

    Set<String> connectionPortTrailIdSet = new HashSet<>();
    connectionPortTrailIdSet.add(statementExecutionBoList.get(0).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(statementExecutionBoList.get(1).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(statementExecutionBoList.get(2).getConnectionPortTrailId());
    Assertions.assertEquals(1, connectionPortTrailIdSet.size());

    Set<String> statementPortTrailIdSet = new HashSet<>();
    statementPortTrailIdSet.add(statementExecutionBoList.get(0).getStatementPortTrailId());
    statementPortTrailIdSet.add(statementExecutionBoList.get(1).getStatementPortTrailId());
    statementPortTrailIdSet.add(statementExecutionBoList.get(2).getStatementPortTrailId());
    Assertions.assertEquals(1, statementPortTrailIdSet.size());

    Assertions.assertEquals(1, statementExecutionBoList.get(0).getStatementSqlList().size());
    Assertions.assertEquals(1, statementExecutionBoList.get(1).getStatementSqlList().size());
    Assertions.assertEquals(1, statementExecutionBoList.get(2).getStatementSqlList().size());

    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED, statementExecutionBoList.get(0).getStatementState());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED, statementExecutionBoList.get(1).getStatementState());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED, statementExecutionBoList.get(2).getStatementState());

    Assertions.assertEquals(1, statementExecutionBoList.get(0).getStatementSqlList().get(0).getOrderInStatement());
    Assertions.assertEquals(2, statementExecutionBoList.get(1).getStatementSqlList().get(0).getOrderInStatement());
    Assertions.assertEquals(3, statementExecutionBoList.get(2).getStatementSqlList().get(0).getOrderInStatement());

    Assertions.assertEquals(1, statementExecutionBoList.get(0).getStatementSqlList().get(0).getOrderInConnection());
    Assertions.assertEquals(2, statementExecutionBoList.get(1).getStatementSqlList().get(0).getOrderInConnection());
    Assertions.assertEquals(3, statementExecutionBoList.get(2).getStatementSqlList().get(0).getOrderInConnection());

    Assertions.assertTrue(((StatementExecutionBo) statementExecutionBoList.get(0)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("0"));
    Assertions.assertTrue(((StatementExecutionBo) statementExecutionBoList.get(1)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("1"));
    Assertions.assertTrue(((StatementExecutionBo) statementExecutionBoList.get(2)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("2"));

  }

  @Test
  void test_preparedStatement_execute_query_autoCommit_false_commit() throws Exception {
    Connection connection = dataSource.getConnection();
    test_preparedStatement_execute_query_autoCommit_false_commit(connection);


    List<DbExecution> dbExecutionList = dbWriter.getDbExecutionList();
    Assertions.assertEquals(3, dbExecutionList.size());

    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(0));
    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(1));
    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(2));

    List<StatementExecutionBo> statementExecutionBoList = dbExecutionList.stream().map(a -> ((StatementExecutionBo) a)).collect(Collectors.toList());

    Set<String> txIdSet = new HashSet<>();
    txIdSet.add(statementExecutionBoList.get(0).getTxId());
    txIdSet.add(statementExecutionBoList.get(1).getTxId());
    txIdSet.add(statementExecutionBoList.get(2).getTxId());
    Assertions.assertEquals(1, txIdSet.size());

    Set<String> dataSourcePortTrailIdSet = new HashSet<>();
    dataSourcePortTrailIdSet.add(statementExecutionBoList.get(0).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(statementExecutionBoList.get(1).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(statementExecutionBoList.get(2).getDataSourcePortTrailId());
    Assertions.assertEquals(1, dataSourcePortTrailIdSet.size());

    Set<String> connectionPortTrailIdSet = new HashSet<>();
    connectionPortTrailIdSet.add(statementExecutionBoList.get(0).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(statementExecutionBoList.get(1).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(statementExecutionBoList.get(2).getConnectionPortTrailId());
    Assertions.assertEquals(1, connectionPortTrailIdSet.size());

    Set<String> statementPortTrailIdSet = new HashSet<>();
    statementPortTrailIdSet.add(statementExecutionBoList.get(0).getStatementPortTrailId());
    statementPortTrailIdSet.add(statementExecutionBoList.get(1).getStatementPortTrailId());
    statementPortTrailIdSet.add(statementExecutionBoList.get(2).getStatementPortTrailId());
    Assertions.assertEquals(1, statementPortTrailIdSet.size());

    Assertions.assertEquals(1, statementExecutionBoList.get(0).getStatementSqlList().size());
    Assertions.assertEquals(1, statementExecutionBoList.get(1).getStatementSqlList().size());
    Assertions.assertEquals(1, statementExecutionBoList.get(2).getStatementSqlList().size());

    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_QUERY, statementExecutionBoList.get(0).getStatementState());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_QUERY, statementExecutionBoList.get(1).getStatementState());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_QUERY, statementExecutionBoList.get(2).getStatementState());

    Assertions.assertEquals(1, statementExecutionBoList.get(0).getStatementSqlList().get(0).getOrderInStatement());
    Assertions.assertEquals(2, statementExecutionBoList.get(1).getStatementSqlList().get(0).getOrderInStatement());
    Assertions.assertEquals(3, statementExecutionBoList.get(2).getStatementSqlList().get(0).getOrderInStatement());

    Assertions.assertEquals(1, statementExecutionBoList.get(0).getStatementSqlList().get(0).getOrderInConnection());
    Assertions.assertEquals(2, statementExecutionBoList.get(1).getStatementSqlList().get(0).getOrderInConnection());
    Assertions.assertEquals(3, statementExecutionBoList.get(2).getStatementSqlList().get(0).getOrderInConnection());

    Assertions.assertTrue(((StatementExecutionBo) statementExecutionBoList.get(0)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("0"));
    Assertions.assertTrue(((StatementExecutionBo) statementExecutionBoList.get(1)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("1"));
    Assertions.assertTrue(((StatementExecutionBo) statementExecutionBoList.get(2)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("2"));

  }

  @Test
  void test_preparedStatement_execute_query_autoCommit_false_rollback() throws Exception {
    Connection connection = dataSource.getConnection();
    test_preparedStatement_execute_query_autoCommit_false_rollback(connection);


    List<DbExecution> dbExecutionList = dbWriter.getDbExecutionList();
    Assertions.assertEquals(3, dbExecutionList.size());

    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(0));
    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(1));
    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(2));

    List<StatementExecutionBo> statementExecutionBoList = dbExecutionList.stream().map(a -> ((StatementExecutionBo) a)).collect(Collectors.toList());

    Set<String> txIdSet = new HashSet<>();
    txIdSet.add(statementExecutionBoList.get(0).getTxId());
    txIdSet.add(statementExecutionBoList.get(1).getTxId());
    txIdSet.add(statementExecutionBoList.get(2).getTxId());
    Assertions.assertEquals(1, txIdSet.size());

    Set<String> dataSourcePortTrailIdSet = new HashSet<>();
    dataSourcePortTrailIdSet.add(statementExecutionBoList.get(0).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(statementExecutionBoList.get(1).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(statementExecutionBoList.get(2).getDataSourcePortTrailId());
    Assertions.assertEquals(1, dataSourcePortTrailIdSet.size());

    Set<String> connectionPortTrailIdSet = new HashSet<>();
    connectionPortTrailIdSet.add(statementExecutionBoList.get(0).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(statementExecutionBoList.get(1).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(statementExecutionBoList.get(2).getConnectionPortTrailId());
    Assertions.assertEquals(1, connectionPortTrailIdSet.size());

    Set<String> statementPortTrailIdSet = new HashSet<>();
    statementPortTrailIdSet.add(statementExecutionBoList.get(0).getStatementPortTrailId());
    statementPortTrailIdSet.add(statementExecutionBoList.get(1).getStatementPortTrailId());
    statementPortTrailIdSet.add(statementExecutionBoList.get(2).getStatementPortTrailId());
    Assertions.assertEquals(1, statementPortTrailIdSet.size());

    Assertions.assertEquals(1, statementExecutionBoList.get(0).getStatementSqlList().size());
    Assertions.assertEquals(1, statementExecutionBoList.get(1).getStatementSqlList().size());
    Assertions.assertEquals(1, statementExecutionBoList.get(2).getStatementSqlList().size());

    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_QUERY, statementExecutionBoList.get(0).getStatementState());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_QUERY, statementExecutionBoList.get(1).getStatementState());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_QUERY, statementExecutionBoList.get(2).getStatementState());

    Assertions.assertEquals(1, statementExecutionBoList.get(0).getStatementSqlList().get(0).getOrderInStatement());
    Assertions.assertEquals(2, statementExecutionBoList.get(1).getStatementSqlList().get(0).getOrderInStatement());
    Assertions.assertEquals(3, statementExecutionBoList.get(2).getStatementSqlList().get(0).getOrderInStatement());

    Assertions.assertEquals(1, statementExecutionBoList.get(0).getStatementSqlList().get(0).getOrderInConnection());
    Assertions.assertEquals(2, statementExecutionBoList.get(1).getStatementSqlList().get(0).getOrderInConnection());
    Assertions.assertEquals(3, statementExecutionBoList.get(2).getStatementSqlList().get(0).getOrderInConnection());

    Assertions.assertTrue(((StatementExecutionBo) statementExecutionBoList.get(0)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("0"));
    Assertions.assertTrue(((StatementExecutionBo) statementExecutionBoList.get(1)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("1"));
    Assertions.assertTrue(((StatementExecutionBo) statementExecutionBoList.get(2)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("2"));

  }

  @Test
  void test_preparedStatement_execute_query_autoCommit_true_commit() throws Exception {
    Connection connection = dataSource.getConnection();
    test_preparedStatement_execute_query_autoCommit_true_commit(connection);


    List<DbExecution> dbExecutionList = dbWriter.getDbExecutionList();
    Assertions.assertEquals(3, dbExecutionList.size());

    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(0));
    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(1));
    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(2));

    List<StatementExecutionBo> statementExecutionBoList = dbExecutionList.stream().map(a -> ((StatementExecutionBo) a)).collect(Collectors.toList());

    Set<String> txIdSet = new HashSet<>();
    txIdSet.add(statementExecutionBoList.get(0).getTxId());
    txIdSet.add(statementExecutionBoList.get(1).getTxId());
    txIdSet.add(statementExecutionBoList.get(2).getTxId());
    Assertions.assertEquals(3, txIdSet.size());

    Set<String> dataSourcePortTrailIdSet = new HashSet<>();
    dataSourcePortTrailIdSet.add(statementExecutionBoList.get(0).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(statementExecutionBoList.get(1).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(statementExecutionBoList.get(2).getDataSourcePortTrailId());
    Assertions.assertEquals(1, dataSourcePortTrailIdSet.size());

    Set<String> connectionPortTrailIdSet = new HashSet<>();
    connectionPortTrailIdSet.add(statementExecutionBoList.get(0).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(statementExecutionBoList.get(1).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(statementExecutionBoList.get(2).getConnectionPortTrailId());
    Assertions.assertEquals(1, connectionPortTrailIdSet.size());

    Set<String> statementPortTrailIdSet = new HashSet<>();
    statementPortTrailIdSet.add(statementExecutionBoList.get(0).getStatementPortTrailId());
    statementPortTrailIdSet.add(statementExecutionBoList.get(1).getStatementPortTrailId());
    statementPortTrailIdSet.add(statementExecutionBoList.get(2).getStatementPortTrailId());
    Assertions.assertEquals(1, statementPortTrailIdSet.size());

    Assertions.assertEquals(1, statementExecutionBoList.get(0).getStatementSqlList().size());
    Assertions.assertEquals(1, statementExecutionBoList.get(1).getStatementSqlList().size());
    Assertions.assertEquals(1, statementExecutionBoList.get(2).getStatementSqlList().size());

    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_QUERY, statementExecutionBoList.get(0).getStatementState());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_QUERY, statementExecutionBoList.get(1).getStatementState());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_QUERY, statementExecutionBoList.get(2).getStatementState());

    Assertions.assertEquals(1, statementExecutionBoList.get(0).getStatementSqlList().get(0).getOrderInStatement());
    Assertions.assertEquals(2, statementExecutionBoList.get(1).getStatementSqlList().get(0).getOrderInStatement());
    Assertions.assertEquals(3, statementExecutionBoList.get(2).getStatementSqlList().get(0).getOrderInStatement());

    Assertions.assertEquals(1, statementExecutionBoList.get(0).getStatementSqlList().get(0).getOrderInConnection());
    Assertions.assertEquals(2, statementExecutionBoList.get(1).getStatementSqlList().get(0).getOrderInConnection());
    Assertions.assertEquals(3, statementExecutionBoList.get(2).getStatementSqlList().get(0).getOrderInConnection());

    Assertions.assertTrue(((StatementExecutionBo) statementExecutionBoList.get(0)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("0"));
    Assertions.assertTrue(((StatementExecutionBo) statementExecutionBoList.get(1)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("1"));
    Assertions.assertTrue(((StatementExecutionBo) statementExecutionBoList.get(2)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("2"));

  }

  @Test
  void test_preparedStatement_execute_query_auto_commit_true_rollback() throws Exception {
    Connection connection = dataSource.getConnection();
    test_preparedStatement_execute_query_autoCommit_true_rollback(connection);


    List<DbExecution> dbExecutionList = dbWriter.getDbExecutionList();
    Assertions.assertEquals(3, dbExecutionList.size());

    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(0));
    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(1));
    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(2));

    List<StatementExecutionBo> statementExecutionBoList = dbExecutionList.stream().map(a -> ((StatementExecutionBo) a)).collect(Collectors.toList());

    Set<String> txIdSet = new HashSet<>();
    txIdSet.add(statementExecutionBoList.get(0).getTxId());
    txIdSet.add(statementExecutionBoList.get(1).getTxId());
    txIdSet.add(statementExecutionBoList.get(2).getTxId());
    Assertions.assertEquals(3, txIdSet.size());

    Set<String> dataSourcePortTrailIdSet = new HashSet<>();
    dataSourcePortTrailIdSet.add(statementExecutionBoList.get(0).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(statementExecutionBoList.get(1).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(statementExecutionBoList.get(2).getDataSourcePortTrailId());
    Assertions.assertEquals(1, dataSourcePortTrailIdSet.size());

    Set<String> connectionPortTrailIdSet = new HashSet<>();
    connectionPortTrailIdSet.add(statementExecutionBoList.get(0).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(statementExecutionBoList.get(1).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(statementExecutionBoList.get(2).getConnectionPortTrailId());
    Assertions.assertEquals(1, connectionPortTrailIdSet.size());

    Set<String> statementPortTrailIdSet = new HashSet<>();
    statementPortTrailIdSet.add(statementExecutionBoList.get(0).getStatementPortTrailId());
    statementPortTrailIdSet.add(statementExecutionBoList.get(1).getStatementPortTrailId());
    statementPortTrailIdSet.add(statementExecutionBoList.get(2).getStatementPortTrailId());
    Assertions.assertEquals(1, statementPortTrailIdSet.size());

    Assertions.assertEquals(1, statementExecutionBoList.get(0).getStatementSqlList().size());
    Assertions.assertEquals(1, statementExecutionBoList.get(1).getStatementSqlList().size());
    Assertions.assertEquals(1, statementExecutionBoList.get(2).getStatementSqlList().size());

    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_QUERY, statementExecutionBoList.get(0).getStatementState());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_QUERY, statementExecutionBoList.get(1).getStatementState());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_QUERY, statementExecutionBoList.get(2).getStatementState());

    Assertions.assertEquals(1, statementExecutionBoList.get(0).getStatementSqlList().get(0).getOrderInStatement());
    Assertions.assertEquals(2, statementExecutionBoList.get(1).getStatementSqlList().get(0).getOrderInStatement());
    Assertions.assertEquals(3, statementExecutionBoList.get(2).getStatementSqlList().get(0).getOrderInStatement());

    Assertions.assertEquals(1, statementExecutionBoList.get(0).getStatementSqlList().get(0).getOrderInConnection());
    Assertions.assertEquals(2, statementExecutionBoList.get(1).getStatementSqlList().get(0).getOrderInConnection());
    Assertions.assertEquals(3, statementExecutionBoList.get(2).getStatementSqlList().get(0).getOrderInConnection());


    Assertions.assertTrue(((StatementExecutionBo) statementExecutionBoList.get(0)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("0"));
    Assertions.assertTrue(((StatementExecutionBo) statementExecutionBoList.get(1)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("1"));
    Assertions.assertTrue(((StatementExecutionBo) statementExecutionBoList.get(2)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("2"));

  }

  @AfterEach
  void after() throws Exception {
    if (dataSource instanceof AutoCloseable) {
      ((AutoCloseable) dataSource).close();
    }
  }

}
