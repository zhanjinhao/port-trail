package cn.addenda.porttrail.jdbc.test.porttrail;

import cn.addenda.porttrail.common.pojo.db.DbExecution;
import cn.addenda.porttrail.common.pojo.db.bo.AbstractSqlExecutionBo;
import cn.addenda.porttrail.common.pojo.db.bo.SqlExecutionBo;
import cn.addenda.porttrail.jdbc.core.PortTrailDataSource;
import cn.addenda.porttrail.jdbc.log.JdbcPortTrailLoggerFactory;
import cn.addenda.porttrail.jdbc.test.DbUtils;
import cn.addenda.porttrail.jdbc.test.jdbc.SingleModeJDBCTest;
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
 * 单个执行场景的单元测试。对应{@link SingleModeJDBCTest}里的场景。
 */
class SingleModePortTrailTest extends SingleModeJDBCTest {

  private DataSource dataSource;

  private JdbcTestSqlWriter sqlWriter;

  @BeforeEach
  void before() {
    dataSource = DbUtils.getDataSource();
    sqlWriter = new JdbcTestSqlWriter();
    dataSource = new PortTrailDataSource(dataSource, JdbcPortTrailLoggerFactory.getInstance(), sqlWriter);
  }

  @Test
  void test_statement_execute_update_autoCommit_false_commit() throws Exception {
    Connection connection = dataSource.getConnection();
    test_statement_execute_update_autoCommit_false_commit(connection);


    List<DbExecution> dbExecutionList = sqlWriter.getDbExecutionList();
    Assertions.assertEquals(3, dbExecutionList.size());

    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(0));
    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(1));
    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(2));

    List<SqlExecutionBo> sqlBoList = dbExecutionList.stream().map(a -> ((SqlExecutionBo) a)).collect(Collectors.toList());

    Set<String> txIdSet = new HashSet<>();
    txIdSet.add(sqlBoList.get(0).getTxId());
    txIdSet.add(sqlBoList.get(1).getTxId());
    txIdSet.add(sqlBoList.get(2).getTxId());
    Assertions.assertEquals(1, txIdSet.size());

    Set<String> dataSourcePortTrailIdSet = new HashSet<>();
    dataSourcePortTrailIdSet.add(sqlBoList.get(0).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(sqlBoList.get(1).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(sqlBoList.get(2).getDataSourcePortTrailId());
    Assertions.assertEquals(1, dataSourcePortTrailIdSet.size());

    Set<String> connectionPortTrailIdSet = new HashSet<>();
    connectionPortTrailIdSet.add(sqlBoList.get(0).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(sqlBoList.get(1).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(sqlBoList.get(2).getConnectionPortTrailId());
    Assertions.assertEquals(1, connectionPortTrailIdSet.size());

    Set<String> statementPortTrailIdSet = new HashSet<>();
    statementPortTrailIdSet.add(sqlBoList.get(0).getStatementPortTrailId());
    statementPortTrailIdSet.add(sqlBoList.get(1).getStatementPortTrailId());
    statementPortTrailIdSet.add(sqlBoList.get(2).getStatementPortTrailId());
    Assertions.assertEquals(1, statementPortTrailIdSet.size());

    Assertions.assertEquals(1, sqlBoList.get(0).getSqlWrapperList().size());
    Assertions.assertEquals(1, sqlBoList.get(1).getSqlWrapperList().size());
    Assertions.assertEquals(1, sqlBoList.get(2).getSqlWrapperList().size());

    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_COMMITTED, sqlBoList.get(0).getSqlState());
    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_COMMITTED, sqlBoList.get(1).getSqlState());
    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_COMMITTED, sqlBoList.get(2).getSqlState());

    Assertions.assertEquals(0, sqlBoList.get(0).getSqlWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(1, sqlBoList.get(1).getSqlWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(2, sqlBoList.get(2).getSqlWrapperList().get(0).getOrderInStatement());

    Assertions.assertEquals(0, sqlBoList.get(0).getSqlWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(1, sqlBoList.get(1).getSqlWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(2, sqlBoList.get(2).getSqlWrapperList().get(0).getOrderInConnection());

    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(0)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("0"));
    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(1)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("1"));
    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(2)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("2"));

  }

  @Test
  void test_statement_execute_update_autoCommit_false_rollback() throws Exception {
    Connection connection = dataSource.getConnection();
    test_statement_execute_update_autoCommit_false_rollback(connection);

    List<DbExecution> dbExecutionList = sqlWriter.getDbExecutionList();
    Assertions.assertEquals(3, dbExecutionList.size());

    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(0));
    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(1));
    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(2));

    List<SqlExecutionBo> sqlBoList = dbExecutionList.stream().map(a -> ((SqlExecutionBo) a)).collect(Collectors.toList());

    Set<String> txIdSet = new HashSet<>();
    txIdSet.add(sqlBoList.get(0).getTxId());
    txIdSet.add(sqlBoList.get(1).getTxId());
    txIdSet.add(sqlBoList.get(2).getTxId());
    Assertions.assertEquals(1, txIdSet.size());

    Set<String> dataSourcePortTrailIdSet = new HashSet<>();
    dataSourcePortTrailIdSet.add(sqlBoList.get(0).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(sqlBoList.get(1).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(sqlBoList.get(2).getDataSourcePortTrailId());
    Assertions.assertEquals(1, dataSourcePortTrailIdSet.size());

    Set<String> connectionPortTrailIdSet = new HashSet<>();
    connectionPortTrailIdSet.add(sqlBoList.get(0).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(sqlBoList.get(1).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(sqlBoList.get(2).getConnectionPortTrailId());
    Assertions.assertEquals(1, connectionPortTrailIdSet.size());

    Set<String> statementPortTrailIdSet = new HashSet<>();
    statementPortTrailIdSet.add(sqlBoList.get(0).getStatementPortTrailId());
    statementPortTrailIdSet.add(sqlBoList.get(1).getStatementPortTrailId());
    statementPortTrailIdSet.add(sqlBoList.get(2).getStatementPortTrailId());
    Assertions.assertEquals(1, statementPortTrailIdSet.size());

    Assertions.assertEquals(1, sqlBoList.get(0).getSqlWrapperList().size());
    Assertions.assertEquals(1, sqlBoList.get(1).getSqlWrapperList().size());
    Assertions.assertEquals(1, sqlBoList.get(2).getSqlWrapperList().size());

    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_ROLLBACK, sqlBoList.get(0).getSqlState());
    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_ROLLBACK, sqlBoList.get(1).getSqlState());
    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_ROLLBACK, sqlBoList.get(2).getSqlState());

    Assertions.assertEquals(0, sqlBoList.get(0).getSqlWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(1, sqlBoList.get(1).getSqlWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(2, sqlBoList.get(2).getSqlWrapperList().get(0).getOrderInStatement());

    Assertions.assertEquals(0, sqlBoList.get(0).getSqlWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(1, sqlBoList.get(1).getSqlWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(2, sqlBoList.get(2).getSqlWrapperList().get(0).getOrderInConnection());

    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(0)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("0"));
    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(1)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("1"));
    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(2)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("2"));

  }

  @Test
  void test_statement_execute_update_autoCommit_true_commit() throws Exception {
    Connection connection = dataSource.getConnection();
    test_statement_execute_update_autoCommit_true_commit(connection);


    List<DbExecution> dbExecutionList = sqlWriter.getDbExecutionList();
    Assertions.assertEquals(3, dbExecutionList.size());

    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(0));
    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(1));
    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(2));

    List<SqlExecutionBo> sqlBoList = dbExecutionList.stream().map(a -> ((SqlExecutionBo) a)).collect(Collectors.toList());

    Set<String> txIdSet = new HashSet<>();
    txIdSet.add(sqlBoList.get(0).getTxId());
    txIdSet.add(sqlBoList.get(1).getTxId());
    txIdSet.add(sqlBoList.get(2).getTxId());
    Assertions.assertEquals(3, txIdSet.size());

    Set<String> dataSourcePortTrailIdSet = new HashSet<>();
    dataSourcePortTrailIdSet.add(sqlBoList.get(0).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(sqlBoList.get(1).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(sqlBoList.get(2).getDataSourcePortTrailId());
    Assertions.assertEquals(1, dataSourcePortTrailIdSet.size());

    Set<String> connectionPortTrailIdSet = new HashSet<>();
    connectionPortTrailIdSet.add(sqlBoList.get(0).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(sqlBoList.get(1).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(sqlBoList.get(2).getConnectionPortTrailId());
    Assertions.assertEquals(1, connectionPortTrailIdSet.size());

    Set<String> statementPortTrailIdSet = new HashSet<>();
    statementPortTrailIdSet.add(sqlBoList.get(0).getStatementPortTrailId());
    statementPortTrailIdSet.add(sqlBoList.get(1).getStatementPortTrailId());
    statementPortTrailIdSet.add(sqlBoList.get(2).getStatementPortTrailId());
    Assertions.assertEquals(1, statementPortTrailIdSet.size());

    Assertions.assertEquals(1, sqlBoList.get(0).getSqlWrapperList().size());
    Assertions.assertEquals(1, sqlBoList.get(1).getSqlWrapperList().size());
    Assertions.assertEquals(1, sqlBoList.get(2).getSqlWrapperList().size());

    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_COMMITTED, sqlBoList.get(0).getSqlState());
    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_COMMITTED, sqlBoList.get(1).getSqlState());
    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_COMMITTED, sqlBoList.get(2).getSqlState());

    Assertions.assertEquals(0, sqlBoList.get(0).getSqlWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(1, sqlBoList.get(1).getSqlWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(2, sqlBoList.get(2).getSqlWrapperList().get(0).getOrderInStatement());

    Assertions.assertEquals(0, sqlBoList.get(0).getSqlWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(1, sqlBoList.get(1).getSqlWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(2, sqlBoList.get(2).getSqlWrapperList().get(0).getOrderInConnection());

    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(0)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("0"));
    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(1)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("1"));
    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(2)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("2"));

  }

  @Test
  void test_statement_execute_update_autoCommit_true_rollback() throws Exception {
    Connection connection = dataSource.getConnection();
    test_statement_execute_update_autoCommit_true_rollback(connection);


    List<DbExecution> dbExecutionList = sqlWriter.getDbExecutionList();
    Assertions.assertEquals(3, dbExecutionList.size());

    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(0));
    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(1));
    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(2));

    List<SqlExecutionBo> sqlBoList = dbExecutionList.stream().map(a -> ((SqlExecutionBo) a)).collect(Collectors.toList());

    Set<String> txIdSet = new HashSet<>();
    txIdSet.add(sqlBoList.get(0).getTxId());
    txIdSet.add(sqlBoList.get(1).getTxId());
    txIdSet.add(sqlBoList.get(2).getTxId());
    Assertions.assertEquals(3, txIdSet.size());

    Set<String> dataSourcePortTrailIdSet = new HashSet<>();
    dataSourcePortTrailIdSet.add(sqlBoList.get(0).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(sqlBoList.get(1).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(sqlBoList.get(2).getDataSourcePortTrailId());
    Assertions.assertEquals(1, dataSourcePortTrailIdSet.size());

    Set<String> connectionPortTrailIdSet = new HashSet<>();
    connectionPortTrailIdSet.add(sqlBoList.get(0).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(sqlBoList.get(1).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(sqlBoList.get(2).getConnectionPortTrailId());
    Assertions.assertEquals(1, connectionPortTrailIdSet.size());

    Set<String> statementPortTrailIdSet = new HashSet<>();
    statementPortTrailIdSet.add(sqlBoList.get(0).getStatementPortTrailId());
    statementPortTrailIdSet.add(sqlBoList.get(1).getStatementPortTrailId());
    statementPortTrailIdSet.add(sqlBoList.get(2).getStatementPortTrailId());
    Assertions.assertEquals(1, statementPortTrailIdSet.size());

    Assertions.assertEquals(1, sqlBoList.get(0).getSqlWrapperList().size());
    Assertions.assertEquals(1, sqlBoList.get(1).getSqlWrapperList().size());
    Assertions.assertEquals(1, sqlBoList.get(2).getSqlWrapperList().size());

    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_COMMITTED, sqlBoList.get(0).getSqlState());
    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_COMMITTED, sqlBoList.get(1).getSqlState());
    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_COMMITTED, sqlBoList.get(2).getSqlState());

    Assertions.assertEquals(0, sqlBoList.get(0).getSqlWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(1, sqlBoList.get(1).getSqlWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(2, sqlBoList.get(2).getSqlWrapperList().get(0).getOrderInStatement());

    Assertions.assertEquals(0, sqlBoList.get(0).getSqlWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(1, sqlBoList.get(1).getSqlWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(2, sqlBoList.get(2).getSqlWrapperList().get(0).getOrderInConnection());

    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(0)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("0"));
    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(1)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("1"));
    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(2)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("2"));

  }

  @Test
  void test_statement_execute_query_autoCommit_false_commit() throws Exception {
    Connection connection = dataSource.getConnection();
    test_statement_execute_query_autoCommit_false_commit(connection);


    List<DbExecution> dbExecutionList = sqlWriter.getDbExecutionList();
    Assertions.assertEquals(3, dbExecutionList.size());

    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(0));
    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(1));
    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(2));

    List<SqlExecutionBo> sqlBoList = dbExecutionList.stream().map(a -> ((SqlExecutionBo) a)).collect(Collectors.toList());

    Set<String> txIdSet = new HashSet<>();
    txIdSet.add(sqlBoList.get(0).getTxId());
    txIdSet.add(sqlBoList.get(1).getTxId());
    txIdSet.add(sqlBoList.get(2).getTxId());
    Assertions.assertEquals(1, txIdSet.size());

    Set<String> dataSourcePortTrailIdSet = new HashSet<>();
    dataSourcePortTrailIdSet.add(sqlBoList.get(0).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(sqlBoList.get(1).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(sqlBoList.get(2).getDataSourcePortTrailId());
    Assertions.assertEquals(1, dataSourcePortTrailIdSet.size());

    Set<String> connectionPortTrailIdSet = new HashSet<>();
    connectionPortTrailIdSet.add(sqlBoList.get(0).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(sqlBoList.get(1).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(sqlBoList.get(2).getConnectionPortTrailId());
    Assertions.assertEquals(1, connectionPortTrailIdSet.size());

    Set<String> statementPortTrailIdSet = new HashSet<>();
    statementPortTrailIdSet.add(sqlBoList.get(0).getStatementPortTrailId());
    statementPortTrailIdSet.add(sqlBoList.get(1).getStatementPortTrailId());
    statementPortTrailIdSet.add(sqlBoList.get(2).getStatementPortTrailId());
    Assertions.assertEquals(1, statementPortTrailIdSet.size());

    Assertions.assertEquals(1, sqlBoList.get(0).getSqlWrapperList().size());
    Assertions.assertEquals(1, sqlBoList.get(1).getSqlWrapperList().size());
    Assertions.assertEquals(1, sqlBoList.get(2).getSqlWrapperList().size());

    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_QUERY, sqlBoList.get(0).getSqlState());
    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_QUERY, sqlBoList.get(1).getSqlState());
    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_QUERY, sqlBoList.get(2).getSqlState());

    Assertions.assertEquals(0, sqlBoList.get(0).getSqlWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(1, sqlBoList.get(1).getSqlWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(2, sqlBoList.get(2).getSqlWrapperList().get(0).getOrderInStatement());

    Assertions.assertEquals(0, sqlBoList.get(0).getSqlWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(1, sqlBoList.get(1).getSqlWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(2, sqlBoList.get(2).getSqlWrapperList().get(0).getOrderInConnection());

    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(0)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("0"));
    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(1)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("1"));
    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(2)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("2"));

  }

  @Test
  void test_statement_execute_query_autoCommit_false_rollback() throws Exception {
    Connection connection = dataSource.getConnection();
    test_statement_execute_query_autoCommit_false_rollback(connection);


    List<DbExecution> dbExecutionList = sqlWriter.getDbExecutionList();
    Assertions.assertEquals(3, dbExecutionList.size());

    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(0));
    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(1));
    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(2));

    List<SqlExecutionBo> sqlBoList = dbExecutionList.stream().map(a -> ((SqlExecutionBo) a)).collect(Collectors.toList());

    Set<String> txIdSet = new HashSet<>();
    txIdSet.add(sqlBoList.get(0).getTxId());
    txIdSet.add(sqlBoList.get(1).getTxId());
    txIdSet.add(sqlBoList.get(2).getTxId());
    Assertions.assertEquals(1, txIdSet.size());

    Set<String> dataSourcePortTrailIdSet = new HashSet<>();
    dataSourcePortTrailIdSet.add(sqlBoList.get(0).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(sqlBoList.get(1).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(sqlBoList.get(2).getDataSourcePortTrailId());
    Assertions.assertEquals(1, dataSourcePortTrailIdSet.size());

    Set<String> connectionPortTrailIdSet = new HashSet<>();
    connectionPortTrailIdSet.add(sqlBoList.get(0).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(sqlBoList.get(1).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(sqlBoList.get(2).getConnectionPortTrailId());
    Assertions.assertEquals(1, connectionPortTrailIdSet.size());

    Set<String> statementPortTrailIdSet = new HashSet<>();
    statementPortTrailIdSet.add(sqlBoList.get(0).getStatementPortTrailId());
    statementPortTrailIdSet.add(sqlBoList.get(1).getStatementPortTrailId());
    statementPortTrailIdSet.add(sqlBoList.get(2).getStatementPortTrailId());
    Assertions.assertEquals(1, statementPortTrailIdSet.size());

    Assertions.assertEquals(1, sqlBoList.get(0).getSqlWrapperList().size());
    Assertions.assertEquals(1, sqlBoList.get(1).getSqlWrapperList().size());
    Assertions.assertEquals(1, sqlBoList.get(2).getSqlWrapperList().size());

    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_QUERY, sqlBoList.get(0).getSqlState());
    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_QUERY, sqlBoList.get(1).getSqlState());
    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_QUERY, sqlBoList.get(2).getSqlState());

    Assertions.assertEquals(0, sqlBoList.get(0).getSqlWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(1, sqlBoList.get(1).getSqlWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(2, sqlBoList.get(2).getSqlWrapperList().get(0).getOrderInStatement());

    Assertions.assertEquals(0, sqlBoList.get(0).getSqlWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(1, sqlBoList.get(1).getSqlWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(2, sqlBoList.get(2).getSqlWrapperList().get(0).getOrderInConnection());


    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(0)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("0"));
    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(1)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("1"));
    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(2)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("2"));

  }

  @Test
  void test_statement_execute_query_autoCommit_true_commit() throws Exception {
    Connection connection = dataSource.getConnection();
    test_statement_execute_query_autoCommit_true_commit(connection);


    List<DbExecution> dbExecutionList = sqlWriter.getDbExecutionList();
    Assertions.assertEquals(3, dbExecutionList.size());

    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(0));
    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(1));
    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(2));

    List<SqlExecutionBo> sqlBoList = dbExecutionList.stream().map(a -> ((SqlExecutionBo) a)).collect(Collectors.toList());

    Set<String> txIdSet = new HashSet<>();
    txIdSet.add(sqlBoList.get(0).getTxId());
    txIdSet.add(sqlBoList.get(1).getTxId());
    txIdSet.add(sqlBoList.get(2).getTxId());
    Assertions.assertEquals(3, txIdSet.size());

    Set<String> dataSourcePortTrailIdSet = new HashSet<>();
    dataSourcePortTrailIdSet.add(sqlBoList.get(0).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(sqlBoList.get(1).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(sqlBoList.get(2).getDataSourcePortTrailId());
    Assertions.assertEquals(1, dataSourcePortTrailIdSet.size());

    Set<String> connectionPortTrailIdSet = new HashSet<>();
    connectionPortTrailIdSet.add(sqlBoList.get(0).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(sqlBoList.get(1).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(sqlBoList.get(2).getConnectionPortTrailId());
    Assertions.assertEquals(1, connectionPortTrailIdSet.size());

    Set<String> statementPortTrailIdSet = new HashSet<>();
    statementPortTrailIdSet.add(sqlBoList.get(0).getStatementPortTrailId());
    statementPortTrailIdSet.add(sqlBoList.get(1).getStatementPortTrailId());
    statementPortTrailIdSet.add(sqlBoList.get(2).getStatementPortTrailId());
    Assertions.assertEquals(1, statementPortTrailIdSet.size());

    Assertions.assertEquals(1, sqlBoList.get(0).getSqlWrapperList().size());
    Assertions.assertEquals(1, sqlBoList.get(1).getSqlWrapperList().size());
    Assertions.assertEquals(1, sqlBoList.get(2).getSqlWrapperList().size());

    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_QUERY, sqlBoList.get(0).getSqlState());
    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_QUERY, sqlBoList.get(1).getSqlState());
    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_QUERY, sqlBoList.get(2).getSqlState());

    Assertions.assertEquals(0, sqlBoList.get(0).getSqlWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(1, sqlBoList.get(1).getSqlWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(2, sqlBoList.get(2).getSqlWrapperList().get(0).getOrderInStatement());

    Assertions.assertEquals(0, sqlBoList.get(0).getSqlWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(1, sqlBoList.get(1).getSqlWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(2, sqlBoList.get(2).getSqlWrapperList().get(0).getOrderInConnection());


    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(0)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("0"));
    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(1)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("1"));
    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(2)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("2"));

  }

  @Test
  void test_statement_execute_query_auto_commit_true_rollback() throws Exception {
    Connection connection = dataSource.getConnection();
    test_statement_execute_query_autoCommit_true_rollback(connection);

    List<DbExecution> dbExecutionList = sqlWriter.getDbExecutionList();
    Assertions.assertEquals(3, dbExecutionList.size());

    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(0));
    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(1));
    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(2));

    List<SqlExecutionBo> sqlBoList = dbExecutionList.stream().map(a -> ((SqlExecutionBo) a)).collect(Collectors.toList());

    Set<String> txIdSet = new HashSet<>();
    txIdSet.add(sqlBoList.get(0).getTxId());
    txIdSet.add(sqlBoList.get(1).getTxId());
    txIdSet.add(sqlBoList.get(2).getTxId());
    Assertions.assertEquals(3, txIdSet.size());

    Set<String> dataSourcePortTrailIdSet = new HashSet<>();
    dataSourcePortTrailIdSet.add(sqlBoList.get(0).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(sqlBoList.get(1).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(sqlBoList.get(2).getDataSourcePortTrailId());
    Assertions.assertEquals(1, dataSourcePortTrailIdSet.size());

    Set<String> connectionPortTrailIdSet = new HashSet<>();
    connectionPortTrailIdSet.add(sqlBoList.get(0).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(sqlBoList.get(1).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(sqlBoList.get(2).getConnectionPortTrailId());
    Assertions.assertEquals(1, connectionPortTrailIdSet.size());

    Set<String> statementPortTrailIdSet = new HashSet<>();
    statementPortTrailIdSet.add(sqlBoList.get(0).getStatementPortTrailId());
    statementPortTrailIdSet.add(sqlBoList.get(1).getStatementPortTrailId());
    statementPortTrailIdSet.add(sqlBoList.get(2).getStatementPortTrailId());
    Assertions.assertEquals(1, statementPortTrailIdSet.size());

    Assertions.assertEquals(1, sqlBoList.get(0).getSqlWrapperList().size());
    Assertions.assertEquals(1, sqlBoList.get(1).getSqlWrapperList().size());
    Assertions.assertEquals(1, sqlBoList.get(2).getSqlWrapperList().size());

    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_QUERY, sqlBoList.get(0).getSqlState());
    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_QUERY, sqlBoList.get(1).getSqlState());
    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_QUERY, sqlBoList.get(2).getSqlState());

    Assertions.assertEquals(0, sqlBoList.get(0).getSqlWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(1, sqlBoList.get(1).getSqlWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(2, sqlBoList.get(2).getSqlWrapperList().get(0).getOrderInStatement());

    Assertions.assertEquals(0, sqlBoList.get(0).getSqlWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(1, sqlBoList.get(1).getSqlWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(2, sqlBoList.get(2).getSqlWrapperList().get(0).getOrderInConnection());


    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(0)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("0"));
    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(1)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("1"));
    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(2)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("2"));

  }

  @Test
  void test_preparedStatement_execute_update_autoCommit_false_commit() throws Exception {
    Connection connection = dataSource.getConnection();
    test_preparedStatement_execute_update_autoCommit_false_commit(connection);


    List<DbExecution> dbExecutionList = sqlWriter.getDbExecutionList();
    Assertions.assertEquals(3, dbExecutionList.size());

    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(0));
    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(1));
    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(2));

    List<SqlExecutionBo> sqlBoList = dbExecutionList.stream().map(a -> ((SqlExecutionBo) a)).collect(Collectors.toList());

    Set<String> txIdSet = new HashSet<>();
    txIdSet.add(sqlBoList.get(0).getTxId());
    txIdSet.add(sqlBoList.get(1).getTxId());
    txIdSet.add(sqlBoList.get(2).getTxId());
    Assertions.assertEquals(1, txIdSet.size());

    Set<String> dataSourcePortTrailIdSet = new HashSet<>();
    dataSourcePortTrailIdSet.add(sqlBoList.get(0).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(sqlBoList.get(1).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(sqlBoList.get(2).getDataSourcePortTrailId());
    Assertions.assertEquals(1, dataSourcePortTrailIdSet.size());

    Set<String> connectionPortTrailIdSet = new HashSet<>();
    connectionPortTrailIdSet.add(sqlBoList.get(0).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(sqlBoList.get(1).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(sqlBoList.get(2).getConnectionPortTrailId());
    Assertions.assertEquals(1, connectionPortTrailIdSet.size());

    Set<String> statementPortTrailIdSet = new HashSet<>();
    statementPortTrailIdSet.add(sqlBoList.get(0).getStatementPortTrailId());
    statementPortTrailIdSet.add(sqlBoList.get(1).getStatementPortTrailId());
    statementPortTrailIdSet.add(sqlBoList.get(2).getStatementPortTrailId());
    Assertions.assertEquals(1, statementPortTrailIdSet.size());

    Assertions.assertEquals(1, sqlBoList.get(0).getSqlWrapperList().size());
    Assertions.assertEquals(1, sqlBoList.get(1).getSqlWrapperList().size());
    Assertions.assertEquals(1, sqlBoList.get(2).getSqlWrapperList().size());

    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_COMMITTED, sqlBoList.get(0).getSqlState());
    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_COMMITTED, sqlBoList.get(1).getSqlState());
    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_COMMITTED, sqlBoList.get(2).getSqlState());

    Assertions.assertEquals(0, sqlBoList.get(0).getSqlWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(1, sqlBoList.get(1).getSqlWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(2, sqlBoList.get(2).getSqlWrapperList().get(0).getOrderInStatement());

    Assertions.assertEquals(0, sqlBoList.get(0).getSqlWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(1, sqlBoList.get(1).getSqlWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(2, sqlBoList.get(2).getSqlWrapperList().get(0).getOrderInConnection());

    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(0)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("0"));
    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(1)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("1"));
    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(2)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("2"));

  }

  @Test
  void test_preparedStatement_execute_update_autoCommit_false_rollback() throws Exception {
    Connection connection = dataSource.getConnection();
    test_preparedStatement_execute_update_autoCommit_false_rollback(connection);


    List<DbExecution> dbExecutionList = sqlWriter.getDbExecutionList();
    Assertions.assertEquals(3, dbExecutionList.size());

    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(0));
    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(1));
    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(2));

    List<SqlExecutionBo> sqlBoList = dbExecutionList.stream().map(a -> ((SqlExecutionBo) a)).collect(Collectors.toList());

    Set<String> txIdSet = new HashSet<>();
    txIdSet.add(sqlBoList.get(0).getTxId());
    txIdSet.add(sqlBoList.get(1).getTxId());
    txIdSet.add(sqlBoList.get(2).getTxId());
    Assertions.assertEquals(1, txIdSet.size());

    Set<String> dataSourcePortTrailIdSet = new HashSet<>();
    dataSourcePortTrailIdSet.add(sqlBoList.get(0).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(sqlBoList.get(1).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(sqlBoList.get(2).getDataSourcePortTrailId());
    Assertions.assertEquals(1, dataSourcePortTrailIdSet.size());

    Set<String> connectionPortTrailIdSet = new HashSet<>();
    connectionPortTrailIdSet.add(sqlBoList.get(0).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(sqlBoList.get(1).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(sqlBoList.get(2).getConnectionPortTrailId());
    Assertions.assertEquals(1, connectionPortTrailIdSet.size());

    Set<String> statementPortTrailIdSet = new HashSet<>();
    statementPortTrailIdSet.add(sqlBoList.get(0).getStatementPortTrailId());
    statementPortTrailIdSet.add(sqlBoList.get(1).getStatementPortTrailId());
    statementPortTrailIdSet.add(sqlBoList.get(2).getStatementPortTrailId());
    Assertions.assertEquals(1, statementPortTrailIdSet.size());

    Assertions.assertEquals(1, sqlBoList.get(0).getSqlWrapperList().size());
    Assertions.assertEquals(1, sqlBoList.get(1).getSqlWrapperList().size());
    Assertions.assertEquals(1, sqlBoList.get(2).getSqlWrapperList().size());

    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_ROLLBACK, sqlBoList.get(0).getSqlState());
    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_ROLLBACK, sqlBoList.get(1).getSqlState());
    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_ROLLBACK, sqlBoList.get(2).getSqlState());

    Assertions.assertEquals(0, sqlBoList.get(0).getSqlWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(1, sqlBoList.get(1).getSqlWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(2, sqlBoList.get(2).getSqlWrapperList().get(0).getOrderInStatement());

    Assertions.assertEquals(0, sqlBoList.get(0).getSqlWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(1, sqlBoList.get(1).getSqlWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(2, sqlBoList.get(2).getSqlWrapperList().get(0).getOrderInConnection());


    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(0)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("0"));
    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(1)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("1"));
    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(2)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("2"));

  }

  @Test
  void test_preparedStatement_execute_update_autoCommit_true_commit() throws Exception {
    Connection connection = dataSource.getConnection();
    test_preparedStatement_execute_update_autoCommit_true_commit(connection);


    List<DbExecution> dbExecutionList = sqlWriter.getDbExecutionList();
    Assertions.assertEquals(3, dbExecutionList.size());

    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(0));
    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(1));
    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(2));

    List<SqlExecutionBo> sqlBoList = dbExecutionList.stream().map(a -> ((SqlExecutionBo) a)).collect(Collectors.toList());

    Set<String> txIdSet = new HashSet<>();
    txIdSet.add(sqlBoList.get(0).getTxId());
    txIdSet.add(sqlBoList.get(1).getTxId());
    txIdSet.add(sqlBoList.get(2).getTxId());
    Assertions.assertEquals(3, txIdSet.size());

    Set<String> dataSourcePortTrailIdSet = new HashSet<>();
    dataSourcePortTrailIdSet.add(sqlBoList.get(0).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(sqlBoList.get(1).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(sqlBoList.get(2).getDataSourcePortTrailId());
    Assertions.assertEquals(1, dataSourcePortTrailIdSet.size());

    Set<String> connectionPortTrailIdSet = new HashSet<>();
    connectionPortTrailIdSet.add(sqlBoList.get(0).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(sqlBoList.get(1).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(sqlBoList.get(2).getConnectionPortTrailId());
    Assertions.assertEquals(1, connectionPortTrailIdSet.size());

    Set<String> statementPortTrailIdSet = new HashSet<>();
    statementPortTrailIdSet.add(sqlBoList.get(0).getStatementPortTrailId());
    statementPortTrailIdSet.add(sqlBoList.get(1).getStatementPortTrailId());
    statementPortTrailIdSet.add(sqlBoList.get(2).getStatementPortTrailId());
    Assertions.assertEquals(1, statementPortTrailIdSet.size());

    Assertions.assertEquals(1, sqlBoList.get(0).getSqlWrapperList().size());
    Assertions.assertEquals(1, sqlBoList.get(1).getSqlWrapperList().size());
    Assertions.assertEquals(1, sqlBoList.get(2).getSqlWrapperList().size());

    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_COMMITTED, sqlBoList.get(0).getSqlState());
    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_COMMITTED, sqlBoList.get(1).getSqlState());
    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_COMMITTED, sqlBoList.get(2).getSqlState());

    Assertions.assertEquals(0, sqlBoList.get(0).getSqlWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(1, sqlBoList.get(1).getSqlWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(2, sqlBoList.get(2).getSqlWrapperList().get(0).getOrderInStatement());

    Assertions.assertEquals(0, sqlBoList.get(0).getSqlWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(1, sqlBoList.get(1).getSqlWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(2, sqlBoList.get(2).getSqlWrapperList().get(0).getOrderInConnection());


    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(0)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("0"));
    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(1)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("1"));
    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(2)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("2"));

  }

  @Test
  void test_preparedStatement_execute_update_autoCommit_true_rollback() throws Exception {
    Connection connection = dataSource.getConnection();
    test_preparedStatement_execute_update_autoCommit_true_rollback(connection);


    List<DbExecution> dbExecutionList = sqlWriter.getDbExecutionList();
    Assertions.assertEquals(3, dbExecutionList.size());

    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(0));
    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(1));
    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(2));

    List<SqlExecutionBo> sqlBoList = dbExecutionList.stream().map(a -> ((SqlExecutionBo) a)).collect(Collectors.toList());

    Set<String> txIdSet = new HashSet<>();
    txIdSet.add(sqlBoList.get(0).getTxId());
    txIdSet.add(sqlBoList.get(1).getTxId());
    txIdSet.add(sqlBoList.get(2).getTxId());
    Assertions.assertEquals(3, txIdSet.size());

    Set<String> dataSourcePortTrailIdSet = new HashSet<>();
    dataSourcePortTrailIdSet.add(sqlBoList.get(0).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(sqlBoList.get(1).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(sqlBoList.get(2).getDataSourcePortTrailId());
    Assertions.assertEquals(1, dataSourcePortTrailIdSet.size());

    Set<String> connectionPortTrailIdSet = new HashSet<>();
    connectionPortTrailIdSet.add(sqlBoList.get(0).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(sqlBoList.get(1).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(sqlBoList.get(2).getConnectionPortTrailId());
    Assertions.assertEquals(1, connectionPortTrailIdSet.size());

    Set<String> statementPortTrailIdSet = new HashSet<>();
    statementPortTrailIdSet.add(sqlBoList.get(0).getStatementPortTrailId());
    statementPortTrailIdSet.add(sqlBoList.get(1).getStatementPortTrailId());
    statementPortTrailIdSet.add(sqlBoList.get(2).getStatementPortTrailId());
    Assertions.assertEquals(1, statementPortTrailIdSet.size());

    Assertions.assertEquals(1, sqlBoList.get(0).getSqlWrapperList().size());
    Assertions.assertEquals(1, sqlBoList.get(1).getSqlWrapperList().size());
    Assertions.assertEquals(1, sqlBoList.get(2).getSqlWrapperList().size());

    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_COMMITTED, sqlBoList.get(0).getSqlState());
    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_COMMITTED, sqlBoList.get(1).getSqlState());
    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_COMMITTED, sqlBoList.get(2).getSqlState());

    Assertions.assertEquals(0, sqlBoList.get(0).getSqlWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(1, sqlBoList.get(1).getSqlWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(2, sqlBoList.get(2).getSqlWrapperList().get(0).getOrderInStatement());

    Assertions.assertEquals(0, sqlBoList.get(0).getSqlWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(1, sqlBoList.get(1).getSqlWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(2, sqlBoList.get(2).getSqlWrapperList().get(0).getOrderInConnection());

    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(0)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("0"));
    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(1)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("1"));
    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(2)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("2"));

  }

  @Test
  void test_preparedStatement_execute_query_autoCommit_false_commit() throws Exception {
    Connection connection = dataSource.getConnection();
    test_preparedStatement_execute_query_autoCommit_false_commit(connection);


    List<DbExecution> dbExecutionList = sqlWriter.getDbExecutionList();
    Assertions.assertEquals(3, dbExecutionList.size());

    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(0));
    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(1));
    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(2));

    List<SqlExecutionBo> sqlBoList = dbExecutionList.stream().map(a -> ((SqlExecutionBo) a)).collect(Collectors.toList());

    Set<String> txIdSet = new HashSet<>();
    txIdSet.add(sqlBoList.get(0).getTxId());
    txIdSet.add(sqlBoList.get(1).getTxId());
    txIdSet.add(sqlBoList.get(2).getTxId());
    Assertions.assertEquals(1, txIdSet.size());

    Set<String> dataSourcePortTrailIdSet = new HashSet<>();
    dataSourcePortTrailIdSet.add(sqlBoList.get(0).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(sqlBoList.get(1).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(sqlBoList.get(2).getDataSourcePortTrailId());
    Assertions.assertEquals(1, dataSourcePortTrailIdSet.size());

    Set<String> connectionPortTrailIdSet = new HashSet<>();
    connectionPortTrailIdSet.add(sqlBoList.get(0).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(sqlBoList.get(1).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(sqlBoList.get(2).getConnectionPortTrailId());
    Assertions.assertEquals(1, connectionPortTrailIdSet.size());

    Set<String> statementPortTrailIdSet = new HashSet<>();
    statementPortTrailIdSet.add(sqlBoList.get(0).getStatementPortTrailId());
    statementPortTrailIdSet.add(sqlBoList.get(1).getStatementPortTrailId());
    statementPortTrailIdSet.add(sqlBoList.get(2).getStatementPortTrailId());
    Assertions.assertEquals(1, statementPortTrailIdSet.size());

    Assertions.assertEquals(1, sqlBoList.get(0).getSqlWrapperList().size());
    Assertions.assertEquals(1, sqlBoList.get(1).getSqlWrapperList().size());
    Assertions.assertEquals(1, sqlBoList.get(2).getSqlWrapperList().size());

    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_QUERY, sqlBoList.get(0).getSqlState());
    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_QUERY, sqlBoList.get(1).getSqlState());
    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_QUERY, sqlBoList.get(2).getSqlState());

    Assertions.assertEquals(0, sqlBoList.get(0).getSqlWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(1, sqlBoList.get(1).getSqlWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(2, sqlBoList.get(2).getSqlWrapperList().get(0).getOrderInStatement());

    Assertions.assertEquals(0, sqlBoList.get(0).getSqlWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(1, sqlBoList.get(1).getSqlWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(2, sqlBoList.get(2).getSqlWrapperList().get(0).getOrderInConnection());

    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(0)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("0"));
    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(1)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("1"));
    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(2)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("2"));

  }

  @Test
  void test_preparedStatement_execute_query_autoCommit_false_rollback() throws Exception {
    Connection connection = dataSource.getConnection();
    test_preparedStatement_execute_query_autoCommit_false_rollback(connection);


    List<DbExecution> dbExecutionList = sqlWriter.getDbExecutionList();
    Assertions.assertEquals(3, dbExecutionList.size());

    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(0));
    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(1));
    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(2));

    List<SqlExecutionBo> sqlBoList = dbExecutionList.stream().map(a -> ((SqlExecutionBo) a)).collect(Collectors.toList());

    Set<String> txIdSet = new HashSet<>();
    txIdSet.add(sqlBoList.get(0).getTxId());
    txIdSet.add(sqlBoList.get(1).getTxId());
    txIdSet.add(sqlBoList.get(2).getTxId());
    Assertions.assertEquals(1, txIdSet.size());

    Set<String> dataSourcePortTrailIdSet = new HashSet<>();
    dataSourcePortTrailIdSet.add(sqlBoList.get(0).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(sqlBoList.get(1).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(sqlBoList.get(2).getDataSourcePortTrailId());
    Assertions.assertEquals(1, dataSourcePortTrailIdSet.size());

    Set<String> connectionPortTrailIdSet = new HashSet<>();
    connectionPortTrailIdSet.add(sqlBoList.get(0).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(sqlBoList.get(1).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(sqlBoList.get(2).getConnectionPortTrailId());
    Assertions.assertEquals(1, connectionPortTrailIdSet.size());

    Set<String> statementPortTrailIdSet = new HashSet<>();
    statementPortTrailIdSet.add(sqlBoList.get(0).getStatementPortTrailId());
    statementPortTrailIdSet.add(sqlBoList.get(1).getStatementPortTrailId());
    statementPortTrailIdSet.add(sqlBoList.get(2).getStatementPortTrailId());
    Assertions.assertEquals(1, statementPortTrailIdSet.size());

    Assertions.assertEquals(1, sqlBoList.get(0).getSqlWrapperList().size());
    Assertions.assertEquals(1, sqlBoList.get(1).getSqlWrapperList().size());
    Assertions.assertEquals(1, sqlBoList.get(2).getSqlWrapperList().size());

    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_QUERY, sqlBoList.get(0).getSqlState());
    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_QUERY, sqlBoList.get(1).getSqlState());
    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_QUERY, sqlBoList.get(2).getSqlState());

    Assertions.assertEquals(0, sqlBoList.get(0).getSqlWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(1, sqlBoList.get(1).getSqlWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(2, sqlBoList.get(2).getSqlWrapperList().get(0).getOrderInStatement());

    Assertions.assertEquals(0, sqlBoList.get(0).getSqlWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(1, sqlBoList.get(1).getSqlWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(2, sqlBoList.get(2).getSqlWrapperList().get(0).getOrderInConnection());

    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(0)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("0"));
    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(1)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("1"));
    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(2)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("2"));

  }

  @Test
  void test_preparedStatement_execute_query_autoCommit_true_commit() throws Exception {
    Connection connection = dataSource.getConnection();
    test_preparedStatement_execute_query_autoCommit_true_commit(connection);


    List<DbExecution> dbExecutionList = sqlWriter.getDbExecutionList();
    Assertions.assertEquals(3, dbExecutionList.size());

    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(0));
    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(1));
    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(2));

    List<SqlExecutionBo> sqlBoList = dbExecutionList.stream().map(a -> ((SqlExecutionBo) a)).collect(Collectors.toList());

    Set<String> txIdSet = new HashSet<>();
    txIdSet.add(sqlBoList.get(0).getTxId());
    txIdSet.add(sqlBoList.get(1).getTxId());
    txIdSet.add(sqlBoList.get(2).getTxId());
    Assertions.assertEquals(3, txIdSet.size());

    Set<String> dataSourcePortTrailIdSet = new HashSet<>();
    dataSourcePortTrailIdSet.add(sqlBoList.get(0).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(sqlBoList.get(1).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(sqlBoList.get(2).getDataSourcePortTrailId());
    Assertions.assertEquals(1, dataSourcePortTrailIdSet.size());

    Set<String> connectionPortTrailIdSet = new HashSet<>();
    connectionPortTrailIdSet.add(sqlBoList.get(0).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(sqlBoList.get(1).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(sqlBoList.get(2).getConnectionPortTrailId());
    Assertions.assertEquals(1, connectionPortTrailIdSet.size());

    Set<String> statementPortTrailIdSet = new HashSet<>();
    statementPortTrailIdSet.add(sqlBoList.get(0).getStatementPortTrailId());
    statementPortTrailIdSet.add(sqlBoList.get(1).getStatementPortTrailId());
    statementPortTrailIdSet.add(sqlBoList.get(2).getStatementPortTrailId());
    Assertions.assertEquals(1, statementPortTrailIdSet.size());

    Assertions.assertEquals(1, sqlBoList.get(0).getSqlWrapperList().size());
    Assertions.assertEquals(1, sqlBoList.get(1).getSqlWrapperList().size());
    Assertions.assertEquals(1, sqlBoList.get(2).getSqlWrapperList().size());

    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_QUERY, sqlBoList.get(0).getSqlState());
    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_QUERY, sqlBoList.get(1).getSqlState());
    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_QUERY, sqlBoList.get(2).getSqlState());

    Assertions.assertEquals(0, sqlBoList.get(0).getSqlWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(1, sqlBoList.get(1).getSqlWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(2, sqlBoList.get(2).getSqlWrapperList().get(0).getOrderInStatement());

    Assertions.assertEquals(0, sqlBoList.get(0).getSqlWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(1, sqlBoList.get(1).getSqlWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(2, sqlBoList.get(2).getSqlWrapperList().get(0).getOrderInConnection());

    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(0)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("0"));
    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(1)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("1"));
    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(2)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("2"));

  }

  @Test
  void test_preparedStatement_execute_query_auto_commit_true_rollback() throws Exception {
    Connection connection = dataSource.getConnection();
    test_preparedStatement_execute_query_autoCommit_true_rollback(connection);


    List<DbExecution> dbExecutionList = sqlWriter.getDbExecutionList();
    Assertions.assertEquals(3, dbExecutionList.size());

    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(0));
    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(1));
    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(2));

    List<SqlExecutionBo> sqlBoList = dbExecutionList.stream().map(a -> ((SqlExecutionBo) a)).collect(Collectors.toList());

    Set<String> txIdSet = new HashSet<>();
    txIdSet.add(sqlBoList.get(0).getTxId());
    txIdSet.add(sqlBoList.get(1).getTxId());
    txIdSet.add(sqlBoList.get(2).getTxId());
    Assertions.assertEquals(3, txIdSet.size());

    Set<String> dataSourcePortTrailIdSet = new HashSet<>();
    dataSourcePortTrailIdSet.add(sqlBoList.get(0).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(sqlBoList.get(1).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(sqlBoList.get(2).getDataSourcePortTrailId());
    Assertions.assertEquals(1, dataSourcePortTrailIdSet.size());

    Set<String> connectionPortTrailIdSet = new HashSet<>();
    connectionPortTrailIdSet.add(sqlBoList.get(0).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(sqlBoList.get(1).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(sqlBoList.get(2).getConnectionPortTrailId());
    Assertions.assertEquals(1, connectionPortTrailIdSet.size());

    Set<String> statementPortTrailIdSet = new HashSet<>();
    statementPortTrailIdSet.add(sqlBoList.get(0).getStatementPortTrailId());
    statementPortTrailIdSet.add(sqlBoList.get(1).getStatementPortTrailId());
    statementPortTrailIdSet.add(sqlBoList.get(2).getStatementPortTrailId());
    Assertions.assertEquals(1, statementPortTrailIdSet.size());

    Assertions.assertEquals(1, sqlBoList.get(0).getSqlWrapperList().size());
    Assertions.assertEquals(1, sqlBoList.get(1).getSqlWrapperList().size());
    Assertions.assertEquals(1, sqlBoList.get(2).getSqlWrapperList().size());

    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_QUERY, sqlBoList.get(0).getSqlState());
    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_QUERY, sqlBoList.get(1).getSqlState());
    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_QUERY, sqlBoList.get(2).getSqlState());

    Assertions.assertEquals(0, sqlBoList.get(0).getSqlWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(1, sqlBoList.get(1).getSqlWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(2, sqlBoList.get(2).getSqlWrapperList().get(0).getOrderInStatement());

    Assertions.assertEquals(0, sqlBoList.get(0).getSqlWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(1, sqlBoList.get(1).getSqlWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(2, sqlBoList.get(2).getSqlWrapperList().get(0).getOrderInConnection());


    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(0)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("0"));
    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(1)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("1"));
    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(2)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("2"));

  }

  @AfterEach
  void after() throws Exception {
    if (dataSource instanceof AutoCloseable) {
      ((AutoCloseable) dataSource).close();
    }
  }

}
