package cn.addenda.porttrail.jdbc.test.porttrail;

import cn.addenda.porttrail.common.pojo.db.DbExecution;
import cn.addenda.porttrail.common.tuple.Unary;
import cn.addenda.porttrail.common.pojo.db.bo.AbstractSqlExecutionBo;
import cn.addenda.porttrail.common.pojo.db.bo.PreparedSqlExecutionBo;
import cn.addenda.porttrail.common.pojo.db.bo.SqlExecutionBo;
import cn.addenda.porttrail.jdbc.core.PortTrailDataSource;
import cn.addenda.porttrail.jdbc.log.JdbcPortTrailLoggerFactory;
import cn.addenda.porttrail.jdbc.test.DbUtils;
import cn.addenda.porttrail.jdbc.test.jdbc.SpecialJDBCTest;
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
 * 特殊执行场景的单元测试。对应{@link SpecialJDBCTest}里的场景。
 */
class SpecialPortTrailTest extends SpecialJDBCTest {

  private DataSource dataSource;

  private JdbcTestSqlWriter sqlWriter;

  @BeforeEach
  void before() {
    dataSource = DbUtils.getDataSource();
    sqlWriter = new JdbcTestSqlWriter();
    dataSource = new PortTrailDataSource(dataSource, JdbcPortTrailLoggerFactory.getInstance(), sqlWriter);
  }

  @Test
  void testExecutionOrderOfSingleAndBatchMode() throws Exception {
    Connection connection = dataSource.getConnection();
    testExecutionOrderOfSingleAndBatchMode(connection);


    List<DbExecution> dbExecutionList = sqlWriter.getDbExecutionList();
    Assertions.assertEquals(5, dbExecutionList.size());

    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(0));
    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(1));
    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(2));
    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(3));
    Assertions.assertInstanceOf(PreparedSqlExecutionBo.class, dbExecutionList.get(4));

    List<AbstractSqlExecutionBo> sqlBoList = dbExecutionList.stream().map(a -> ((AbstractSqlExecutionBo) a)).collect(Collectors.toList());

    Set<String> txIdSet = new HashSet<>();
    txIdSet.add(sqlBoList.get(0).getTxId());
    txIdSet.add(sqlBoList.get(1).getTxId());
    txIdSet.add(sqlBoList.get(2).getTxId());
    txIdSet.add(sqlBoList.get(3).getTxId());
    txIdSet.add(sqlBoList.get(4).getTxId());
    Assertions.assertEquals(1, txIdSet.size());

    Set<String> dataSourcePortTrailIdSet = new HashSet<>();
    dataSourcePortTrailIdSet.add(sqlBoList.get(0).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(sqlBoList.get(1).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(sqlBoList.get(2).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(sqlBoList.get(3).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(sqlBoList.get(4).getDataSourcePortTrailId());
    Assertions.assertEquals(1, dataSourcePortTrailIdSet.size());

    Set<String> connectionPortTrailIdSet = new HashSet<>();
    connectionPortTrailIdSet.add(sqlBoList.get(0).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(sqlBoList.get(1).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(sqlBoList.get(2).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(sqlBoList.get(3).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(sqlBoList.get(4).getConnectionPortTrailId());
    Assertions.assertEquals(1, connectionPortTrailIdSet.size());

    Set<String> statementPortTrailIdSet = new HashSet<>();
    statementPortTrailIdSet.add(sqlBoList.get(0).getStatementPortTrailId());
    statementPortTrailIdSet.add(sqlBoList.get(1).getStatementPortTrailId());
    statementPortTrailIdSet.add(sqlBoList.get(2).getStatementPortTrailId());
    statementPortTrailIdSet.add(sqlBoList.get(3).getStatementPortTrailId());
    statementPortTrailIdSet.add(sqlBoList.get(4).getStatementPortTrailId());
    Assertions.assertEquals(1, statementPortTrailIdSet.size());

    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_COMMITTED, sqlBoList.get(0).getSqlState());
    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_COMMITTED, sqlBoList.get(1).getSqlState());
    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_COMMITTED, sqlBoList.get(2).getSqlState());
    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_COMMITTED, sqlBoList.get(3).getSqlState());
    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_COMMITTED, sqlBoList.get(4).getSqlState());

    Assertions.assertEquals(1, ((SqlExecutionBo) sqlBoList.get(0)).getSqlWrapperList().size());
    Assertions.assertEquals(1, ((SqlExecutionBo) sqlBoList.get(1)).getSqlWrapperList().size());
    Assertions.assertEquals(1, ((SqlExecutionBo) sqlBoList.get(2)).getSqlWrapperList().size());
    Assertions.assertEquals(2, ((SqlExecutionBo) sqlBoList.get(3)).getSqlWrapperList().size());
    Assertions.assertEquals(1, ((PreparedSqlExecutionBo) sqlBoList.get(4)).getPreparedStatementParameterWrapperList().size());

    Assertions.assertEquals(0, ((SqlExecutionBo) sqlBoList.get(0)).getSqlWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(1, ((SqlExecutionBo) sqlBoList.get(1)).getSqlWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(2, ((SqlExecutionBo) sqlBoList.get(2)).getSqlWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(3, ((SqlExecutionBo) sqlBoList.get(3)).getSqlWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(5, ((SqlExecutionBo) sqlBoList.get(3)).getSqlWrapperList().get(1).getOrderInStatement());
    Assertions.assertEquals(4, ((PreparedSqlExecutionBo) sqlBoList.get(4)).getPreparedStatementParameterWrapperList().get(0).getOrderInStatement());

    Assertions.assertEquals(0, ((SqlExecutionBo) sqlBoList.get(0)).getSqlWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(1, ((SqlExecutionBo) sqlBoList.get(1)).getSqlWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(2, ((SqlExecutionBo) sqlBoList.get(2)).getSqlWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(3, ((SqlExecutionBo) sqlBoList.get(3)).getSqlWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(5, ((SqlExecutionBo) sqlBoList.get(3)).getSqlWrapperList().get(1).getOrderInConnection());
    Assertions.assertEquals(4, ((PreparedSqlExecutionBo) sqlBoList.get(4)).getPreparedStatementParameterWrapperList().get(0).getOrderInConnection());

    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(0)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("0"));
    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(1)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("1"));
    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(2)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("2"));
    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(3)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("3"));
    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(3)).getSqlWrapperList().get(1).getSql().replace("'", "").endsWith("5"));
    Assertions.assertTrue(((Unary<String>) (((PreparedSqlExecutionBo) sqlBoList.get(4)).getPreparedStatementParameterWrapperList().get(0).getParameterList().get(0))).getF1().endsWith("4"));

  }

  @Test
  void testClearBatchMethodDoesNotAffectParameters() throws Exception {
    Connection connection = dataSource.getConnection();
    testClearBatchMethodDoesNotAffectParameters(connection);


    List<DbExecution> dbExecutionList = sqlWriter.getDbExecutionList();
    Assertions.assertEquals(1, dbExecutionList.size());

    Assertions.assertInstanceOf(PreparedSqlExecutionBo.class, dbExecutionList.get(0));

    List<PreparedSqlExecutionBo> sqlBoList = dbExecutionList.stream().map(a -> ((PreparedSqlExecutionBo) a)).collect(Collectors.toList());

    Set<String> txIdSet = new HashSet<>();
    txIdSet.add(sqlBoList.get(0).getTxId());
    Assertions.assertEquals(1, txIdSet.size());

    Set<String> dataSourcePortTrailIdSet = new HashSet<>();
    dataSourcePortTrailIdSet.add(sqlBoList.get(0).getDataSourcePortTrailId());
    Assertions.assertEquals(1, dataSourcePortTrailIdSet.size());

    Set<String> connectionPortTrailIdSet = new HashSet<>();
    connectionPortTrailIdSet.add(sqlBoList.get(0).getConnectionPortTrailId());
    Assertions.assertEquals(1, connectionPortTrailIdSet.size());

    Set<String> statementPortTrailIdSet = new HashSet<>();
    statementPortTrailIdSet.add(sqlBoList.get(0).getStatementPortTrailId());
    Assertions.assertEquals(1, statementPortTrailIdSet.size());

    Assertions.assertEquals(1, sqlBoList.get(0).getPreparedStatementParameterWrapperList().size());

    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_COMMITTED, sqlBoList.get(0).getSqlState());

    Assertions.assertEquals(0, sqlBoList.get(0).getPreparedStatementParameterWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(0, sqlBoList.get(0).getPreparedStatementParameterWrapperList().get(0).getOrderInConnection());
  }

  @Test
  void testClearParametersMethodDoesNotAffectDataThatHasAddBatched() throws Exception {
    Connection connection = dataSource.getConnection();
    testClearParametersMethodDoesNotAffectDataThatHasAddBatched(connection);


    List<DbExecution> dbExecutionList = sqlWriter.getDbExecutionList();
    Assertions.assertEquals(2, dbExecutionList.size());

    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(0));
    Assertions.assertInstanceOf(PreparedSqlExecutionBo.class, dbExecutionList.get(1));

    List<AbstractSqlExecutionBo> sqlBoList = dbExecutionList.stream().map(a -> ((AbstractSqlExecutionBo) a)).collect(Collectors.toList());

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

    Assertions.assertEquals(2, ((SqlExecutionBo) sqlBoList.get(0)).getSqlWrapperList().size());
    Assertions.assertEquals(2, ((PreparedSqlExecutionBo) sqlBoList.get(1)).getPreparedStatementParameterWrapperList().size());

    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_COMMITTED, sqlBoList.get(0).getSqlState());
    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_COMMITTED, sqlBoList.get(1).getSqlState());

    Assertions.assertEquals(0, ((SqlExecutionBo) sqlBoList.get(0)).getSqlWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(2, ((SqlExecutionBo) sqlBoList.get(0)).getSqlWrapperList().get(1).getOrderInStatement());
    Assertions.assertEquals(1, ((PreparedSqlExecutionBo) sqlBoList.get(1)).getPreparedStatementParameterWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(3, ((PreparedSqlExecutionBo) sqlBoList.get(1)).getPreparedStatementParameterWrapperList().get(1).getOrderInStatement());

    Assertions.assertEquals(0, ((SqlExecutionBo) sqlBoList.get(0)).getSqlWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(2, ((SqlExecutionBo) sqlBoList.get(0)).getSqlWrapperList().get(1).getOrderInConnection());
    Assertions.assertEquals(1, ((PreparedSqlExecutionBo) sqlBoList.get(1)).getPreparedStatementParameterWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(3, ((PreparedSqlExecutionBo) sqlBoList.get(1)).getPreparedStatementParameterWrapperList().get(1).getOrderInConnection());

    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(0)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("0"));
    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(0)).getSqlWrapperList().get(1).getSql().replace("'", "").endsWith("2"));
    Assertions.assertTrue(((Unary<String>) (((PreparedSqlExecutionBo) sqlBoList.get(1)).getPreparedStatementParameterWrapperList().get(0).getParameterList().get(0))).getF1().endsWith("1"));
    Assertions.assertTrue(((Unary<String>) (((PreparedSqlExecutionBo) sqlBoList.get(1)).getPreparedStatementParameterWrapperList().get(1).getParameterList().get(0))).getF1().endsWith("3"));
  }

  @Test
  void testCloseMethodDoesNotCommitDataThatHasExecutedButNotCommitted() throws Exception {
    Connection connection = dataSource.getConnection();
    testCloseMethodDoesNotCommitDataThatHasExecutedAndDoNotCommitted(connection);


    List<DbExecution> dbExecutionList = sqlWriter.getDbExecutionList();
    Assertions.assertEquals(0, dbExecutionList.size());
  }

  @Test
  void testCommitDataWhenUpdateAutoCommitFromFalseToTrue() throws Exception {
    Connection connection = dataSource.getConnection();
    testCommitDataWhenUpdateAutoCommitFromFalseToTrue(connection);


    List<DbExecution> dbExecutionList = sqlWriter.getDbExecutionList();
    Assertions.assertEquals(1, dbExecutionList.size());

    Assertions.assertInstanceOf(PreparedSqlExecutionBo.class, dbExecutionList.get(0));

    List<PreparedSqlExecutionBo> sqlBoList = dbExecutionList.stream().map(a -> ((PreparedSqlExecutionBo) a)).collect(Collectors.toList());

    Set<String> txIdSet = new HashSet<>();
    txIdSet.add(sqlBoList.get(0).getTxId());
    Assertions.assertEquals(1, txIdSet.size());

    Set<String> dataSourcePortTrailIdSet = new HashSet<>();
    dataSourcePortTrailIdSet.add(sqlBoList.get(0).getDataSourcePortTrailId());
    Assertions.assertEquals(1, dataSourcePortTrailIdSet.size());

    Set<String> connectionPortTrailIdSet = new HashSet<>();
    connectionPortTrailIdSet.add(sqlBoList.get(0).getConnectionPortTrailId());
    Assertions.assertEquals(1, connectionPortTrailIdSet.size());

    Set<String> statementPortTrailIdSet = new HashSet<>();
    statementPortTrailIdSet.add(sqlBoList.get(0).getStatementPortTrailId());
    Assertions.assertEquals(1, statementPortTrailIdSet.size());

    Assertions.assertEquals(1, sqlBoList.get(0).getPreparedStatementParameterWrapperList().size());

    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_COMMITTED, sqlBoList.get(0).getSqlState());

    Assertions.assertEquals(0, sqlBoList.get(0).getPreparedStatementParameterWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(0, sqlBoList.get(0).getPreparedStatementParameterWrapperList().get(0).getOrderInConnection());
  }

  @Test
  void testCommitDataWhenUpdateAutoCommitFromFalseToFalse() throws Exception {
    Connection connection = dataSource.getConnection();
    testCommitDataWhenUpdateAutoCommitFromFalseToFalse(connection);


    List<DbExecution> dbExecutionList = sqlWriter.getDbExecutionList();
    Assertions.assertEquals(0, dbExecutionList.size());
  }

  @Test
  void testClearBatchMethodDoesNotClearDataThatHasExecuted() throws Exception {
    Connection connection = dataSource.getConnection();
    testClearBatchMethodDoesNotClearDataThatHasExecuted(connection);


    List<DbExecution> dbExecutionList = sqlWriter.getDbExecutionList();
    Assertions.assertEquals(2, dbExecutionList.size());

    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(0));
    Assertions.assertInstanceOf(PreparedSqlExecutionBo.class, dbExecutionList.get(1));

    List<AbstractSqlExecutionBo> sqlBoList = dbExecutionList.stream().map(a -> ((AbstractSqlExecutionBo) a)).collect(Collectors.toList());

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

    Assertions.assertEquals(1, ((SqlExecutionBo) sqlBoList.get(0)).getSqlWrapperList().size());
    Assertions.assertEquals(1, ((PreparedSqlExecutionBo) sqlBoList.get(1)).getPreparedStatementParameterWrapperList().size());

    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_COMMITTED, sqlBoList.get(0).getSqlState());
    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_COMMITTED, sqlBoList.get(1).getSqlState());

    Assertions.assertEquals(1, ((SqlExecutionBo) sqlBoList.get(0)).getSqlWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(0, ((PreparedSqlExecutionBo) sqlBoList.get(1)).getPreparedStatementParameterWrapperList().get(0).getOrderInStatement());

    Assertions.assertEquals(1, ((SqlExecutionBo) sqlBoList.get(0)).getSqlWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(0, ((PreparedSqlExecutionBo) sqlBoList.get(1)).getPreparedStatementParameterWrapperList().get(0).getOrderInConnection());

    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(0)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("1"));
    Assertions.assertTrue(((Unary<String>) (((PreparedSqlExecutionBo) sqlBoList.get(1)).getPreparedStatementParameterWrapperList().get(0).getParameterList().get(0))).getF1().endsWith("0"));
  }

  @Test
  void testCloseMethodDoesNotThrowExceptionInAnyExecutionOrder() throws Exception {
    Connection connection = dataSource.getConnection();
    testCloseMethodDoesNotThrowExceptionInAnyExecutionOrder(connection);


    List<DbExecution> dbExecutionList = sqlWriter.getDbExecutionList();
    Assertions.assertEquals(0, dbExecutionList.size());
  }

  @Test
  void testExecuteDoesNotClearParameterWhenSetAutoCommitTrue() throws Exception {
    Connection connection = dataSource.getConnection();
    testExecuteDoesNotClearParameterWhenSetAutoCommitTrue(connection);


    List<DbExecution> dbExecutionList = sqlWriter.getDbExecutionList();
    Assertions.assertEquals(4, dbExecutionList.size());

    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(0));
    Assertions.assertInstanceOf(PreparedSqlExecutionBo.class, dbExecutionList.get(1));
    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(2));
    Assertions.assertInstanceOf(PreparedSqlExecutionBo.class, dbExecutionList.get(3));

    List<AbstractSqlExecutionBo> sqlBoList = dbExecutionList.stream().map(a -> ((AbstractSqlExecutionBo) a)).collect(Collectors.toList());

    Set<String> txIdSet = new HashSet<>();
    txIdSet.add(sqlBoList.get(0).getTxId());
    txIdSet.add(sqlBoList.get(1).getTxId());
    txIdSet.add(sqlBoList.get(2).getTxId());
    txIdSet.add(sqlBoList.get(3).getTxId());
    Assertions.assertEquals(2, txIdSet.size());

    Set<String> dataSourcePortTrailIdSet = new HashSet<>();
    dataSourcePortTrailIdSet.add(sqlBoList.get(0).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(sqlBoList.get(1).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(sqlBoList.get(2).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(sqlBoList.get(3).getDataSourcePortTrailId());
    Assertions.assertEquals(1, dataSourcePortTrailIdSet.size());

    Set<String> connectionPortTrailIdSet = new HashSet<>();
    connectionPortTrailIdSet.add(sqlBoList.get(0).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(sqlBoList.get(1).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(sqlBoList.get(2).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(sqlBoList.get(3).getConnectionPortTrailId());
    Assertions.assertEquals(1, connectionPortTrailIdSet.size());

    Set<String> statementPortTrailIdSet = new HashSet<>();
    statementPortTrailIdSet.add(sqlBoList.get(0).getStatementPortTrailId());
    statementPortTrailIdSet.add(sqlBoList.get(1).getStatementPortTrailId());
    statementPortTrailIdSet.add(sqlBoList.get(2).getStatementPortTrailId());
    statementPortTrailIdSet.add(sqlBoList.get(3).getStatementPortTrailId());
    Assertions.assertEquals(1, statementPortTrailIdSet.size());

    Assertions.assertEquals(1, ((SqlExecutionBo) sqlBoList.get(0)).getSqlWrapperList().size());
    Assertions.assertEquals(2, ((PreparedSqlExecutionBo) sqlBoList.get(1)).getPreparedStatementParameterWrapperList().size());
    Assertions.assertEquals(1, ((SqlExecutionBo) sqlBoList.get(2)).getSqlWrapperList().size());
    Assertions.assertEquals(2, ((PreparedSqlExecutionBo) sqlBoList.get(3)).getPreparedStatementParameterWrapperList().size());

    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_COMMITTED, sqlBoList.get(0).getSqlState());
    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_COMMITTED, sqlBoList.get(1).getSqlState());
    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_COMMITTED, sqlBoList.get(2).getSqlState());
    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_COMMITTED, sqlBoList.get(3).getSqlState());

    Assertions.assertEquals(2, ((SqlExecutionBo) sqlBoList.get(0)).getSqlWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(0, ((PreparedSqlExecutionBo) sqlBoList.get(1)).getPreparedStatementParameterWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(1, ((PreparedSqlExecutionBo) sqlBoList.get(1)).getPreparedStatementParameterWrapperList().get(1).getOrderInStatement());
    Assertions.assertEquals(5, ((SqlExecutionBo) sqlBoList.get(2)).getSqlWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(3, ((PreparedSqlExecutionBo) sqlBoList.get(3)).getPreparedStatementParameterWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(4, ((PreparedSqlExecutionBo) sqlBoList.get(3)).getPreparedStatementParameterWrapperList().get(1).getOrderInStatement());

    Assertions.assertEquals(2, ((SqlExecutionBo) sqlBoList.get(0)).getSqlWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(0, ((PreparedSqlExecutionBo) sqlBoList.get(1)).getPreparedStatementParameterWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(1, ((PreparedSqlExecutionBo) sqlBoList.get(1)).getPreparedStatementParameterWrapperList().get(1).getOrderInConnection());
    Assertions.assertEquals(5, ((SqlExecutionBo) sqlBoList.get(2)).getSqlWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(3, ((PreparedSqlExecutionBo) sqlBoList.get(3)).getPreparedStatementParameterWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(4, ((PreparedSqlExecutionBo) sqlBoList.get(3)).getPreparedStatementParameterWrapperList().get(1).getOrderInConnection());

    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(0)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("1"));
    Assertions.assertTrue(((Unary<String>) (((PreparedSqlExecutionBo) sqlBoList.get(1)).getPreparedStatementParameterWrapperList().get(0).getParameterList().get(0))).getF1().endsWith("0"));
    Assertions.assertTrue(((Unary<String>) (((PreparedSqlExecutionBo) sqlBoList.get(1)).getPreparedStatementParameterWrapperList().get(1).getParameterList().get(0))).getF1().endsWith("0"));
    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(2)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("1"));
    Assertions.assertTrue(((Unary<String>) (((PreparedSqlExecutionBo) sqlBoList.get(3)).getPreparedStatementParameterWrapperList().get(0).getParameterList().get(0))).getF1().endsWith("0"));
    Assertions.assertTrue(((Unary<String>) (((PreparedSqlExecutionBo) sqlBoList.get(3)).getPreparedStatementParameterWrapperList().get(1).getParameterList().get(0))).getF1().endsWith("0"));

  }

  @Test
  void testExecuteDoesNotClearParameterWhenSetAutoCommitFalse() throws Exception {
    Connection connection = dataSource.getConnection();
    testExecuteDoesNotClearParameterWhenSetAutoCommitFalse(connection);


    List<DbExecution> dbExecutionList = sqlWriter.getDbExecutionList();
    Assertions.assertEquals(4, dbExecutionList.size());

    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(0));
    Assertions.assertInstanceOf(PreparedSqlExecutionBo.class, dbExecutionList.get(1));
    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(2));
    Assertions.assertInstanceOf(PreparedSqlExecutionBo.class, dbExecutionList.get(3));

    List<AbstractSqlExecutionBo> sqlBoList = dbExecutionList.stream().map(a -> ((AbstractSqlExecutionBo) a)).collect(Collectors.toList());

    Set<String> txIdSet = new HashSet<>();
    txIdSet.add(sqlBoList.get(0).getTxId());
    txIdSet.add(sqlBoList.get(1).getTxId());
    txIdSet.add(sqlBoList.get(2).getTxId());
    txIdSet.add(sqlBoList.get(3).getTxId());
    Assertions.assertEquals(1, txIdSet.size());

    Set<String> dataSourcePortTrailIdSet = new HashSet<>();
    dataSourcePortTrailIdSet.add(sqlBoList.get(0).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(sqlBoList.get(1).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(sqlBoList.get(2).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(sqlBoList.get(3).getDataSourcePortTrailId());
    Assertions.assertEquals(1, dataSourcePortTrailIdSet.size());

    Set<String> connectionPortTrailIdSet = new HashSet<>();
    connectionPortTrailIdSet.add(sqlBoList.get(0).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(sqlBoList.get(1).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(sqlBoList.get(2).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(sqlBoList.get(3).getConnectionPortTrailId());
    Assertions.assertEquals(1, connectionPortTrailIdSet.size());

    Set<String> statementPortTrailIdSet = new HashSet<>();
    statementPortTrailIdSet.add(sqlBoList.get(0).getStatementPortTrailId());
    statementPortTrailIdSet.add(sqlBoList.get(1).getStatementPortTrailId());
    statementPortTrailIdSet.add(sqlBoList.get(2).getStatementPortTrailId());
    statementPortTrailIdSet.add(sqlBoList.get(3).getStatementPortTrailId());
    Assertions.assertEquals(1, statementPortTrailIdSet.size());

    Assertions.assertEquals(1, ((SqlExecutionBo) sqlBoList.get(0)).getSqlWrapperList().size());
    Assertions.assertEquals(2, ((PreparedSqlExecutionBo) sqlBoList.get(1)).getPreparedStatementParameterWrapperList().size());
    Assertions.assertEquals(1, ((SqlExecutionBo) sqlBoList.get(2)).getSqlWrapperList().size());
    Assertions.assertEquals(2, ((PreparedSqlExecutionBo) sqlBoList.get(3)).getPreparedStatementParameterWrapperList().size());

    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_COMMITTED, sqlBoList.get(0).getSqlState());
    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_COMMITTED, sqlBoList.get(1).getSqlState());
    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_COMMITTED, sqlBoList.get(2).getSqlState());
    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_COMMITTED, sqlBoList.get(3).getSqlState());

    Assertions.assertEquals(2, ((SqlExecutionBo) sqlBoList.get(0)).getSqlWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(0, ((PreparedSqlExecutionBo) sqlBoList.get(1)).getPreparedStatementParameterWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(1, ((PreparedSqlExecutionBo) sqlBoList.get(1)).getPreparedStatementParameterWrapperList().get(1).getOrderInStatement());
    Assertions.assertEquals(5, ((SqlExecutionBo) sqlBoList.get(2)).getSqlWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(3, ((PreparedSqlExecutionBo) sqlBoList.get(3)).getPreparedStatementParameterWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(4, ((PreparedSqlExecutionBo) sqlBoList.get(3)).getPreparedStatementParameterWrapperList().get(1).getOrderInStatement());

    Assertions.assertEquals(2, ((SqlExecutionBo) sqlBoList.get(0)).getSqlWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(0, ((PreparedSqlExecutionBo) sqlBoList.get(1)).getPreparedStatementParameterWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(1, ((PreparedSqlExecutionBo) sqlBoList.get(1)).getPreparedStatementParameterWrapperList().get(1).getOrderInConnection());
    Assertions.assertEquals(5, ((SqlExecutionBo) sqlBoList.get(2)).getSqlWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(3, ((PreparedSqlExecutionBo) sqlBoList.get(3)).getPreparedStatementParameterWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(4, ((PreparedSqlExecutionBo) sqlBoList.get(3)).getPreparedStatementParameterWrapperList().get(1).getOrderInConnection());

    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(0)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("1"));
    Assertions.assertTrue(((Unary<String>) (((PreparedSqlExecutionBo) sqlBoList.get(1)).getPreparedStatementParameterWrapperList().get(0).getParameterList().get(0))).getF1().endsWith("0"));
    Assertions.assertTrue(((Unary<String>) (((PreparedSqlExecutionBo) sqlBoList.get(1)).getPreparedStatementParameterWrapperList().get(1).getParameterList().get(0))).getF1().endsWith("0"));
    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(2)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("1"));
    Assertions.assertTrue(((Unary<String>) (((PreparedSqlExecutionBo) sqlBoList.get(3)).getPreparedStatementParameterWrapperList().get(0).getParameterList().get(0))).getF1().endsWith("0"));
    Assertions.assertTrue(((Unary<String>) (((PreparedSqlExecutionBo) sqlBoList.get(3)).getPreparedStatementParameterWrapperList().get(1).getParameterList().get(0))).getF1().endsWith("0"));

  }

  @Test
  void testPreparedStatementCloseMethodDoesNotDiscardData() throws Exception {
    Connection connection = dataSource.getConnection();
    testPreparedStatementCloseMethodDoesNotDiscardData(connection);


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
    Assertions.assertEquals(2, statementPortTrailIdSet.size());

    Assertions.assertEquals(1, sqlBoList.get(0).getPreparedStatementParameterWrapperList().size());
    Assertions.assertEquals(1, sqlBoList.get(1).getPreparedStatementParameterWrapperList().size());

    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_COMMITTED, sqlBoList.get(0).getSqlState());
    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_COMMITTED, sqlBoList.get(1).getSqlState());

    Assertions.assertEquals(0, sqlBoList.get(0).getPreparedStatementParameterWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(0, sqlBoList.get(1).getPreparedStatementParameterWrapperList().get(0).getOrderInStatement());

    Assertions.assertEquals(0, sqlBoList.get(0).getPreparedStatementParameterWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(1, sqlBoList.get(1).getPreparedStatementParameterWrapperList().get(0).getOrderInConnection());

    Assertions.assertTrue(((Unary<String>) (sqlBoList.get(0).getPreparedStatementParameterWrapperList().get(0).getParameterList().get(0))).getF1().endsWith("0"));
    Assertions.assertTrue(((Unary<String>) (sqlBoList.get(1).getPreparedStatementParameterWrapperList().get(0).getParameterList().get(0))).getF1().endsWith("1"));

  }

  @Test
  void testRollbackMethodWillRollBackAllDataOfPreparedStatement() throws Exception {
    Connection connection = dataSource.getConnection();
    testRollbackMethodWillRollBackAllDataOfPreparedStatement(connection);


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
    Assertions.assertEquals(2, statementPortTrailIdSet.size());

    Assertions.assertEquals(1, sqlBoList.get(0).getPreparedStatementParameterWrapperList().size());
    Assertions.assertEquals(1, sqlBoList.get(1).getPreparedStatementParameterWrapperList().size());

    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_ROLLBACK, sqlBoList.get(0).getSqlState());
    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_ROLLBACK, sqlBoList.get(1).getSqlState());

    Assertions.assertEquals(0, sqlBoList.get(0).getPreparedStatementParameterWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(0, sqlBoList.get(1).getPreparedStatementParameterWrapperList().get(0).getOrderInStatement());

    Assertions.assertEquals(0, sqlBoList.get(0).getPreparedStatementParameterWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(1, sqlBoList.get(1).getPreparedStatementParameterWrapperList().get(0).getOrderInConnection());

    Assertions.assertTrue(((Unary<String>) (sqlBoList.get(0).getPreparedStatementParameterWrapperList().get(0).getParameterList().get(0))).getF1().endsWith("0"));
    Assertions.assertTrue(((Unary<String>) (sqlBoList.get(1).getPreparedStatementParameterWrapperList().get(0).getParameterList().get(0))).getF1().endsWith("1"));

  }

  @Test
  void testRollbackMethodDoesNotClearDataThatHasNotExecuted() throws Exception {
    Connection connection = dataSource.getConnection();
    testRollbackMethodDoesNotClearDataThatHasNotExecuted(connection);


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
    Assertions.assertEquals(2, statementPortTrailIdSet.size());

    Assertions.assertEquals(1, sqlBoList.get(0).getPreparedStatementParameterWrapperList().size());
    Assertions.assertEquals(1, sqlBoList.get(1).getPreparedStatementParameterWrapperList().size());

    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_COMMITTED, sqlBoList.get(0).getSqlState());
    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_COMMITTED, sqlBoList.get(1).getSqlState());

    Assertions.assertEquals(0, sqlBoList.get(0).getPreparedStatementParameterWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(0, sqlBoList.get(1).getPreparedStatementParameterWrapperList().get(0).getOrderInStatement());

    Assertions.assertEquals(0, sqlBoList.get(0).getPreparedStatementParameterWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(1, sqlBoList.get(1).getPreparedStatementParameterWrapperList().get(0).getOrderInConnection());

    Assertions.assertTrue(((Unary<String>) (sqlBoList.get(0).getPreparedStatementParameterWrapperList().get(0).getParameterList().get(0))).getF1().endsWith("0"));
    Assertions.assertTrue(((Unary<String>) (sqlBoList.get(1).getPreparedStatementParameterWrapperList().get(0).getParameterList().get(0))).getF1().endsWith("1"));

  }

  @Test
  void testCommitDoesNotCommitDataThatOnlyAddBatched() throws Exception {
    Connection connection = dataSource.getConnection();
    testCommitDoesNotCommitDataThatOnlyAddBatched(connection);


    List<DbExecution> dbExecutionList = sqlWriter.getDbExecutionList();
    Assertions.assertEquals(0, dbExecutionList.size());
  }

  @Test
  void testUpdateAutoCommitFromFalseToTrueDoesNotCommitDataThatOnlyAddBatched() throws Exception {
    Connection connection = dataSource.getConnection();
    testUpdateAutoCommitFromFalseToTrueDoesNotCommitDataThatOnlyAddBatched(connection);

    List<DbExecution> dbExecutionList = sqlWriter.getDbExecutionList();
    Assertions.assertEquals(0, dbExecutionList.size());
  }


  @Test
  void testExecuteUpdateDoesNotAffectDataThatHasAddBatched() throws Exception {
    Connection connection = dataSource.getConnection();
    testExecuteUpdateDoesNotAffectDataThatHasAddBatched(connection);


    List<DbExecution> dbExecutionList = sqlWriter.getDbExecutionList();
    Assertions.assertEquals(5, dbExecutionList.size());

    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(0));
    Assertions.assertInstanceOf(PreparedSqlExecutionBo.class, dbExecutionList.get(1));
    Assertions.assertInstanceOf(SqlExecutionBo.class, dbExecutionList.get(2));
    Assertions.assertInstanceOf(PreparedSqlExecutionBo.class, dbExecutionList.get(3));
    Assertions.assertInstanceOf(PreparedSqlExecutionBo.class, dbExecutionList.get(4));

    List<AbstractSqlExecutionBo> sqlBoList = dbExecutionList.stream().map(a -> ((AbstractSqlExecutionBo) a)).collect(Collectors.toList());

    Set<String> txIdSet = new HashSet<>();
    txIdSet.add(sqlBoList.get(0).getTxId());
    txIdSet.add(sqlBoList.get(1).getTxId());
    txIdSet.add(sqlBoList.get(2).getTxId());
    txIdSet.add(sqlBoList.get(3).getTxId());
    txIdSet.add(sqlBoList.get(4).getTxId());
    Assertions.assertEquals(1, txIdSet.size());

    Set<String> dataSourcePortTrailIdSet = new HashSet<>();
    dataSourcePortTrailIdSet.add(sqlBoList.get(0).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(sqlBoList.get(1).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(sqlBoList.get(2).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(sqlBoList.get(3).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(sqlBoList.get(4).getDataSourcePortTrailId());
    Assertions.assertEquals(1, dataSourcePortTrailIdSet.size());

    Set<String> connectionPortTrailIdSet = new HashSet<>();
    connectionPortTrailIdSet.add(sqlBoList.get(0).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(sqlBoList.get(1).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(sqlBoList.get(2).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(sqlBoList.get(3).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(sqlBoList.get(4).getConnectionPortTrailId());
    Assertions.assertEquals(1, connectionPortTrailIdSet.size());

    Set<String> statementPortTrailIdSet = new HashSet<>();
    statementPortTrailIdSet.add(sqlBoList.get(0).getStatementPortTrailId());
    statementPortTrailIdSet.add(sqlBoList.get(1).getStatementPortTrailId());
    statementPortTrailIdSet.add(sqlBoList.get(2).getStatementPortTrailId());
    statementPortTrailIdSet.add(sqlBoList.get(3).getStatementPortTrailId());
    statementPortTrailIdSet.add(sqlBoList.get(4).getStatementPortTrailId());
    Assertions.assertEquals(2, statementPortTrailIdSet.size());

    Assertions.assertEquals(1, ((SqlExecutionBo) sqlBoList.get(0)).getSqlWrapperList().size());
    Assertions.assertEquals(1, ((PreparedSqlExecutionBo) sqlBoList.get(1)).getPreparedStatementParameterWrapperList().size());
    Assertions.assertEquals(1, ((SqlExecutionBo) sqlBoList.get(2)).getSqlWrapperList().size());
    Assertions.assertEquals(1, ((PreparedSqlExecutionBo) sqlBoList.get(3)).getPreparedStatementParameterWrapperList().size());
    Assertions.assertEquals(1, ((PreparedSqlExecutionBo) sqlBoList.get(4)).getPreparedStatementParameterWrapperList().size());

    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_COMMITTED, sqlBoList.get(0).getSqlState());
    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_COMMITTED, sqlBoList.get(1).getSqlState());
    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_COMMITTED, sqlBoList.get(2).getSqlState());
    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_COMMITTED, sqlBoList.get(3).getSqlState());
    Assertions.assertEquals(AbstractSqlExecutionBo.SQL_STATE_COMMITTED, sqlBoList.get(4).getSqlState());

    Assertions.assertEquals(0, ((SqlExecutionBo) sqlBoList.get(0)).getSqlWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(0, ((PreparedSqlExecutionBo) sqlBoList.get(1)).getPreparedStatementParameterWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(2, ((SqlExecutionBo) sqlBoList.get(2)).getSqlWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(1, ((PreparedSqlExecutionBo) sqlBoList.get(3)).getPreparedStatementParameterWrapperList().get(0).getOrderInStatement());
    Assertions.assertEquals(1, ((PreparedSqlExecutionBo) sqlBoList.get(4)).getPreparedStatementParameterWrapperList().get(0).getOrderInStatement());

    Assertions.assertEquals(0, ((SqlExecutionBo) sqlBoList.get(0)).getSqlWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(1, ((PreparedSqlExecutionBo) sqlBoList.get(1)).getPreparedStatementParameterWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(3, ((SqlExecutionBo) sqlBoList.get(2)).getSqlWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(2, ((PreparedSqlExecutionBo) sqlBoList.get(3)).getPreparedStatementParameterWrapperList().get(0).getOrderInConnection());
    Assertions.assertEquals(4, ((PreparedSqlExecutionBo) sqlBoList.get(4)).getPreparedStatementParameterWrapperList().get(0).getOrderInConnection());

    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(0)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("0"));
    Assertions.assertTrue(((Unary<String>) (((PreparedSqlExecutionBo) sqlBoList.get(1)).getPreparedStatementParameterWrapperList().get(0).getParameterList().get(0))).getF1().endsWith("1"));
    Assertions.assertTrue(((SqlExecutionBo) sqlBoList.get(2)).getSqlWrapperList().get(0).getSql().replace("'", "").endsWith("3"));
    Assertions.assertTrue(((Unary<String>) (((PreparedSqlExecutionBo) sqlBoList.get(3)).getPreparedStatementParameterWrapperList().get(0).getParameterList().get(0))).getF1().endsWith("2"));
    Assertions.assertTrue(((Unary<String>) (((PreparedSqlExecutionBo) sqlBoList.get(4)).getPreparedStatementParameterWrapperList().get(0).getParameterList().get(0))).getF1().endsWith("4"));
  }


  @AfterEach
  void after() throws Exception {
    if (dataSource instanceof AutoCloseable) {
      ((AutoCloseable) dataSource).close();
    }
  }

}
