package cn.addenda.porttrail.jdbc.test.porttrail;

import cn.addenda.porttrail.common.pojo.db.bo.DbExecution;
import cn.addenda.porttrail.common.tuple.Unary;
import cn.addenda.porttrail.common.pojo.db.bo.AbstractStatementExecutionBo;
import cn.addenda.porttrail.common.pojo.db.bo.PreparedStatementExecutionBo;
import cn.addenda.porttrail.common.pojo.db.bo.StatementExecutionBo;
import cn.addenda.porttrail.jdbc.core.PortTrailDataSource;
import cn.addenda.porttrail.jdbc.log.JdbcPortTrailLoggerFactory;
import cn.addenda.porttrail.jdbc.test.DbUtils;
import cn.addenda.porttrail.jdbc.test.jdbc.SpecialJDBCTest;
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
 * 特殊执行场景的单元测试。对应{@link SpecialJDBCTest}里的场景。
 */
class SpecialPortTrailTest extends SpecialJDBCTest {

  private DataSource dataSource;

  private JdbcTestDbWriter dbWriter;

  @BeforeEach
  void before() {
    dataSource = DbUtils.getDataSource();
    dbWriter = new JdbcTestDbWriter();
    dataSource = new PortTrailDataSource(dataSource, JdbcPortTrailLoggerFactory.getInstance(), dbWriter);
  }

  @Test
  void testExecutionOrderOfSingleAndBatchMode() throws Exception {
    Connection connection = dataSource.getConnection();
    testExecutionOrderOfSingleAndBatchMode(connection);


    List<DbExecution> dbExecutionList = dbWriter.getDbExecutionList();
    Assertions.assertEquals(5, dbExecutionList.size());

    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(0));
    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(1));
    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(2));
    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(3));
    Assertions.assertInstanceOf(PreparedStatementExecutionBo.class, dbExecutionList.get(4));

    List<AbstractStatementExecutionBo> abstractStatementExecutionBoList = dbExecutionList.stream().map(a -> ((AbstractStatementExecutionBo) a)).collect(Collectors.toList());

    Set<String> txIdSet = new HashSet<>();
    txIdSet.add(abstractStatementExecutionBoList.get(0).getTxId());
    txIdSet.add(abstractStatementExecutionBoList.get(1).getTxId());
    txIdSet.add(abstractStatementExecutionBoList.get(2).getTxId());
    txIdSet.add(abstractStatementExecutionBoList.get(3).getTxId());
    txIdSet.add(abstractStatementExecutionBoList.get(4).getTxId());
    Assertions.assertEquals(1, txIdSet.size());

    Set<String> dataSourcePortTrailIdSet = new HashSet<>();
    dataSourcePortTrailIdSet.add(abstractStatementExecutionBoList.get(0).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(abstractStatementExecutionBoList.get(1).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(abstractStatementExecutionBoList.get(2).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(abstractStatementExecutionBoList.get(3).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(abstractStatementExecutionBoList.get(4).getDataSourcePortTrailId());
    Assertions.assertEquals(1, dataSourcePortTrailIdSet.size());

    Set<String> connectionPortTrailIdSet = new HashSet<>();
    connectionPortTrailIdSet.add(abstractStatementExecutionBoList.get(0).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(abstractStatementExecutionBoList.get(1).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(abstractStatementExecutionBoList.get(2).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(abstractStatementExecutionBoList.get(3).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(abstractStatementExecutionBoList.get(4).getConnectionPortTrailId());
    Assertions.assertEquals(1, connectionPortTrailIdSet.size());

    Set<String> statementPortTrailIdSet = new HashSet<>();
    statementPortTrailIdSet.add(abstractStatementExecutionBoList.get(0).getStatementPortTrailId());
    statementPortTrailIdSet.add(abstractStatementExecutionBoList.get(1).getStatementPortTrailId());
    statementPortTrailIdSet.add(abstractStatementExecutionBoList.get(2).getStatementPortTrailId());
    statementPortTrailIdSet.add(abstractStatementExecutionBoList.get(3).getStatementPortTrailId());
    statementPortTrailIdSet.add(abstractStatementExecutionBoList.get(4).getStatementPortTrailId());
    Assertions.assertEquals(1, statementPortTrailIdSet.size());

    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED, abstractStatementExecutionBoList.get(0).getStatementState());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED, abstractStatementExecutionBoList.get(1).getStatementState());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED, abstractStatementExecutionBoList.get(2).getStatementState());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED, abstractStatementExecutionBoList.get(3).getStatementState());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED, abstractStatementExecutionBoList.get(4).getStatementState());

    Assertions.assertEquals(1, ((StatementExecutionBo) abstractStatementExecutionBoList.get(0)).getStatementSqlList().size());
    Assertions.assertEquals(1, ((StatementExecutionBo) abstractStatementExecutionBoList.get(1)).getStatementSqlList().size());
    Assertions.assertEquals(1, ((StatementExecutionBo) abstractStatementExecutionBoList.get(2)).getStatementSqlList().size());
    Assertions.assertEquals(2, ((StatementExecutionBo) abstractStatementExecutionBoList.get(3)).getStatementSqlList().size());
    Assertions.assertEquals(1, ((PreparedStatementExecutionBo) abstractStatementExecutionBoList.get(4)).getPreparedStatementParameterList().size());

    Assertions.assertEquals(1, ((StatementExecutionBo) abstractStatementExecutionBoList.get(0)).getStatementSqlList().get(0).getOrderInStatement());
    Assertions.assertEquals(2, ((StatementExecutionBo) abstractStatementExecutionBoList.get(1)).getStatementSqlList().get(0).getOrderInStatement());
    Assertions.assertEquals(3, ((StatementExecutionBo) abstractStatementExecutionBoList.get(2)).getStatementSqlList().get(0).getOrderInStatement());
    Assertions.assertEquals(4, ((StatementExecutionBo) abstractStatementExecutionBoList.get(3)).getStatementSqlList().get(0).getOrderInStatement());
    Assertions.assertEquals(6, ((StatementExecutionBo) abstractStatementExecutionBoList.get(3)).getStatementSqlList().get(1).getOrderInStatement());
    Assertions.assertEquals(5, ((PreparedStatementExecutionBo) abstractStatementExecutionBoList.get(4)).getPreparedStatementParameterList().get(0).getOrderInStatement());

    Assertions.assertEquals(1, ((StatementExecutionBo) abstractStatementExecutionBoList.get(0)).getStatementSqlList().get(0).getOrderInConnection());
    Assertions.assertEquals(2, ((StatementExecutionBo) abstractStatementExecutionBoList.get(1)).getStatementSqlList().get(0).getOrderInConnection());
    Assertions.assertEquals(3, ((StatementExecutionBo) abstractStatementExecutionBoList.get(2)).getStatementSqlList().get(0).getOrderInConnection());
    Assertions.assertEquals(4, ((StatementExecutionBo) abstractStatementExecutionBoList.get(3)).getStatementSqlList().get(0).getOrderInConnection());
    Assertions.assertEquals(6, ((StatementExecutionBo) abstractStatementExecutionBoList.get(3)).getStatementSqlList().get(1).getOrderInConnection());
    Assertions.assertEquals(5, ((PreparedStatementExecutionBo) abstractStatementExecutionBoList.get(4)).getPreparedStatementParameterList().get(0).getOrderInConnection());

    Assertions.assertTrue(((StatementExecutionBo) abstractStatementExecutionBoList.get(0)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("0"));
    Assertions.assertTrue(((StatementExecutionBo) abstractStatementExecutionBoList.get(1)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("1"));
    Assertions.assertTrue(((StatementExecutionBo) abstractStatementExecutionBoList.get(2)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("2"));
    Assertions.assertTrue(((StatementExecutionBo) abstractStatementExecutionBoList.get(3)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("3"));
    Assertions.assertTrue(((StatementExecutionBo) abstractStatementExecutionBoList.get(3)).getStatementSqlList().get(1).getSql().replace("'", "").endsWith("5"));
    Assertions.assertTrue(((Unary<String>) (((PreparedStatementExecutionBo) abstractStatementExecutionBoList.get(4)).getPreparedStatementParameterList().get(0).getParameterList().get(0))).getF1().endsWith("4"));

  }

  @Test
  void testClearBatchMethodDoesNotAffectParameters() throws Exception {
    Connection connection = dataSource.getConnection();
    testClearBatchMethodDoesNotAffectParameters(connection);


    List<DbExecution> dbExecutionList = dbWriter.getDbExecutionList();
    Assertions.assertEquals(1, dbExecutionList.size());

    Assertions.assertInstanceOf(PreparedStatementExecutionBo.class, dbExecutionList.get(0));

    List<PreparedStatementExecutionBo> preparedStatementExecutionBoList = dbExecutionList.stream().map(a -> ((PreparedStatementExecutionBo) a)).collect(Collectors.toList());

    Set<String> txIdSet = new HashSet<>();
    txIdSet.add(preparedStatementExecutionBoList.get(0).getTxId());
    Assertions.assertEquals(1, txIdSet.size());

    Set<String> dataSourcePortTrailIdSet = new HashSet<>();
    dataSourcePortTrailIdSet.add(preparedStatementExecutionBoList.get(0).getDataSourcePortTrailId());
    Assertions.assertEquals(1, dataSourcePortTrailIdSet.size());

    Set<String> connectionPortTrailIdSet = new HashSet<>();
    connectionPortTrailIdSet.add(preparedStatementExecutionBoList.get(0).getConnectionPortTrailId());
    Assertions.assertEquals(1, connectionPortTrailIdSet.size());

    Set<String> statementPortTrailIdSet = new HashSet<>();
    statementPortTrailIdSet.add(preparedStatementExecutionBoList.get(0).getStatementPortTrailId());
    Assertions.assertEquals(1, statementPortTrailIdSet.size());

    Assertions.assertEquals(1, preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().size());

    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED, preparedStatementExecutionBoList.get(0).getStatementState());

    Assertions.assertEquals(1, preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().get(0).getOrderInStatement());
    Assertions.assertEquals(1, preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().get(0).getOrderInConnection());
  }

  @Test
  void testClearParametersMethodDoesNotAffectDataThatHasAddBatched() throws Exception {
    Connection connection = dataSource.getConnection();
    testClearParametersMethodDoesNotAffectDataThatHasAddBatched(connection);


    List<DbExecution> dbExecutionList = dbWriter.getDbExecutionList();
    Assertions.assertEquals(2, dbExecutionList.size());

    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(0));
    Assertions.assertInstanceOf(PreparedStatementExecutionBo.class, dbExecutionList.get(1));

    List<AbstractStatementExecutionBo> abstractStatementExecutionBoList = dbExecutionList.stream().map(a -> ((AbstractStatementExecutionBo) a)).collect(Collectors.toList());

    Set<String> txIdSet = new HashSet<>();
    txIdSet.add(abstractStatementExecutionBoList.get(0).getTxId());
    txIdSet.add(abstractStatementExecutionBoList.get(1).getTxId());
    Assertions.assertEquals(1, txIdSet.size());

    Set<String> dataSourcePortTrailIdSet = new HashSet<>();
    dataSourcePortTrailIdSet.add(abstractStatementExecutionBoList.get(0).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(abstractStatementExecutionBoList.get(1).getDataSourcePortTrailId());
    Assertions.assertEquals(1, dataSourcePortTrailIdSet.size());

    Set<String> connectionPortTrailIdSet = new HashSet<>();
    connectionPortTrailIdSet.add(abstractStatementExecutionBoList.get(0).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(abstractStatementExecutionBoList.get(1).getConnectionPortTrailId());
    Assertions.assertEquals(1, connectionPortTrailIdSet.size());

    Set<String> statementPortTrailIdSet = new HashSet<>();
    statementPortTrailIdSet.add(abstractStatementExecutionBoList.get(0).getStatementPortTrailId());
    statementPortTrailIdSet.add(abstractStatementExecutionBoList.get(1).getStatementPortTrailId());
    Assertions.assertEquals(1, statementPortTrailIdSet.size());

    Assertions.assertEquals(2, ((StatementExecutionBo) abstractStatementExecutionBoList.get(0)).getStatementSqlList().size());
    Assertions.assertEquals(2, ((PreparedStatementExecutionBo) abstractStatementExecutionBoList.get(1)).getPreparedStatementParameterList().size());

    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED, abstractStatementExecutionBoList.get(0).getStatementState());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED, abstractStatementExecutionBoList.get(1).getStatementState());

    Assertions.assertEquals(1, ((StatementExecutionBo) abstractStatementExecutionBoList.get(0)).getStatementSqlList().get(0).getOrderInStatement());
    Assertions.assertEquals(3, ((StatementExecutionBo) abstractStatementExecutionBoList.get(0)).getStatementSqlList().get(1).getOrderInStatement());
    Assertions.assertEquals(2, ((PreparedStatementExecutionBo) abstractStatementExecutionBoList.get(1)).getPreparedStatementParameterList().get(0).getOrderInStatement());
    Assertions.assertEquals(4, ((PreparedStatementExecutionBo) abstractStatementExecutionBoList.get(1)).getPreparedStatementParameterList().get(1).getOrderInStatement());

    Assertions.assertEquals(1, ((StatementExecutionBo) abstractStatementExecutionBoList.get(0)).getStatementSqlList().get(0).getOrderInConnection());
    Assertions.assertEquals(3, ((StatementExecutionBo) abstractStatementExecutionBoList.get(0)).getStatementSqlList().get(1).getOrderInConnection());
    Assertions.assertEquals(2, ((PreparedStatementExecutionBo) abstractStatementExecutionBoList.get(1)).getPreparedStatementParameterList().get(0).getOrderInConnection());
    Assertions.assertEquals(4, ((PreparedStatementExecutionBo) abstractStatementExecutionBoList.get(1)).getPreparedStatementParameterList().get(1).getOrderInConnection());

    Assertions.assertTrue(((StatementExecutionBo) abstractStatementExecutionBoList.get(0)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("0"));
    Assertions.assertTrue(((StatementExecutionBo) abstractStatementExecutionBoList.get(0)).getStatementSqlList().get(1).getSql().replace("'", "").endsWith("2"));
    Assertions.assertTrue(((Unary<String>) (((PreparedStatementExecutionBo) abstractStatementExecutionBoList.get(1)).getPreparedStatementParameterList().get(0).getParameterList().get(0))).getF1().endsWith("1"));
    Assertions.assertTrue(((Unary<String>) (((PreparedStatementExecutionBo) abstractStatementExecutionBoList.get(1)).getPreparedStatementParameterList().get(1).getParameterList().get(0))).getF1().endsWith("3"));
  }

  @Test
  void testCloseMethodDoesNotCommitDataThatHasExecutedButNotCommitted() throws Exception {
    Connection connection = dataSource.getConnection();
    testCloseMethodDoesNotCommitDataThatHasExecutedAndDoNotCommitted(connection);


    List<DbExecution> dbExecutionList = dbWriter.getDbExecutionList();
    Assertions.assertEquals(0, dbExecutionList.size());
  }

  @Test
  void testCommitDataWhenUpdateAutoCommitFromFalseToTrue() throws Exception {
    Connection connection = dataSource.getConnection();
    testCommitDataWhenUpdateAutoCommitFromFalseToTrue(connection);


    List<DbExecution> dbExecutionList = dbWriter.getDbExecutionList();
    Assertions.assertEquals(1, dbExecutionList.size());

    Assertions.assertInstanceOf(PreparedStatementExecutionBo.class, dbExecutionList.get(0));

    List<PreparedStatementExecutionBo> preparedStatementExecutionBoList = dbExecutionList.stream().map(a -> ((PreparedStatementExecutionBo) a)).collect(Collectors.toList());

    Set<String> txIdSet = new HashSet<>();
    txIdSet.add(preparedStatementExecutionBoList.get(0).getTxId());
    Assertions.assertEquals(1, txIdSet.size());

    Set<String> dataSourcePortTrailIdSet = new HashSet<>();
    dataSourcePortTrailIdSet.add(preparedStatementExecutionBoList.get(0).getDataSourcePortTrailId());
    Assertions.assertEquals(1, dataSourcePortTrailIdSet.size());

    Set<String> connectionPortTrailIdSet = new HashSet<>();
    connectionPortTrailIdSet.add(preparedStatementExecutionBoList.get(0).getConnectionPortTrailId());
    Assertions.assertEquals(1, connectionPortTrailIdSet.size());

    Set<String> statementPortTrailIdSet = new HashSet<>();
    statementPortTrailIdSet.add(preparedStatementExecutionBoList.get(0).getStatementPortTrailId());
    Assertions.assertEquals(1, statementPortTrailIdSet.size());

    Assertions.assertEquals(1, preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().size());

    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED, preparedStatementExecutionBoList.get(0).getStatementState());

    Assertions.assertEquals(1, preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().get(0).getOrderInStatement());
    Assertions.assertEquals(1, preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().get(0).getOrderInConnection());
  }

  @Test
  void testCommitDataWhenUpdateAutoCommitFromFalseToFalse() throws Exception {
    Connection connection = dataSource.getConnection();
    testCommitDataWhenUpdateAutoCommitFromFalseToFalse(connection);


    List<DbExecution> dbExecutionList = dbWriter.getDbExecutionList();
    Assertions.assertEquals(0, dbExecutionList.size());
  }

  @Test
  void testClearBatchMethodDoesNotClearDataThatHasExecuted() throws Exception {
    Connection connection = dataSource.getConnection();
    testClearBatchMethodDoesNotClearDataThatHasExecuted(connection);


    List<DbExecution> dbExecutionList = dbWriter.getDbExecutionList();
    Assertions.assertEquals(2, dbExecutionList.size());

    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(0));
    Assertions.assertInstanceOf(PreparedStatementExecutionBo.class, dbExecutionList.get(1));

    List<AbstractStatementExecutionBo> abstractStatementExecutionBoList = dbExecutionList.stream().map(a -> ((AbstractStatementExecutionBo) a)).collect(Collectors.toList());

    Set<String> txIdSet = new HashSet<>();
    txIdSet.add(abstractStatementExecutionBoList.get(0).getTxId());
    txIdSet.add(abstractStatementExecutionBoList.get(1).getTxId());
    Assertions.assertEquals(1, txIdSet.size());

    Set<String> dataSourcePortTrailIdSet = new HashSet<>();
    dataSourcePortTrailIdSet.add(abstractStatementExecutionBoList.get(0).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(abstractStatementExecutionBoList.get(1).getDataSourcePortTrailId());
    Assertions.assertEquals(1, dataSourcePortTrailIdSet.size());

    Set<String> connectionPortTrailIdSet = new HashSet<>();
    connectionPortTrailIdSet.add(abstractStatementExecutionBoList.get(0).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(abstractStatementExecutionBoList.get(1).getConnectionPortTrailId());
    Assertions.assertEquals(1, connectionPortTrailIdSet.size());

    Set<String> statementPortTrailIdSet = new HashSet<>();
    statementPortTrailIdSet.add(abstractStatementExecutionBoList.get(0).getStatementPortTrailId());
    statementPortTrailIdSet.add(abstractStatementExecutionBoList.get(1).getStatementPortTrailId());
    Assertions.assertEquals(1, statementPortTrailIdSet.size());

    Assertions.assertEquals(1, ((StatementExecutionBo) abstractStatementExecutionBoList.get(0)).getStatementSqlList().size());
    Assertions.assertEquals(1, ((PreparedStatementExecutionBo) abstractStatementExecutionBoList.get(1)).getPreparedStatementParameterList().size());

    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED, abstractStatementExecutionBoList.get(0).getStatementState());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED, abstractStatementExecutionBoList.get(1).getStatementState());

    Assertions.assertEquals(2, ((StatementExecutionBo) abstractStatementExecutionBoList.get(0)).getStatementSqlList().get(0).getOrderInStatement());
    Assertions.assertEquals(1, ((PreparedStatementExecutionBo) abstractStatementExecutionBoList.get(1)).getPreparedStatementParameterList().get(0).getOrderInStatement());

    Assertions.assertEquals(2, ((StatementExecutionBo) abstractStatementExecutionBoList.get(0)).getStatementSqlList().get(0).getOrderInConnection());
    Assertions.assertEquals(1, ((PreparedStatementExecutionBo) abstractStatementExecutionBoList.get(1)).getPreparedStatementParameterList().get(0).getOrderInConnection());

    Assertions.assertTrue(((StatementExecutionBo) abstractStatementExecutionBoList.get(0)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("1"));
    Assertions.assertTrue(((Unary<String>) (((PreparedStatementExecutionBo) abstractStatementExecutionBoList.get(1)).getPreparedStatementParameterList().get(0).getParameterList().get(0))).getF1().endsWith("0"));
  }

  @Test
  void testCloseMethodDoesNotThrowExceptionInAnyExecutionOrder() throws Exception {
    Connection connection = dataSource.getConnection();
    testCloseMethodDoesNotThrowExceptionInAnyExecutionOrder(connection);


    List<DbExecution> dbExecutionList = dbWriter.getDbExecutionList();
    Assertions.assertEquals(0, dbExecutionList.size());
  }

  @Test
  void testExecuteDoesNotClearParameterWhenSetAutoCommitTrue() throws Exception {
    Connection connection = dataSource.getConnection();
    testExecuteDoesNotClearParameterWhenSetAutoCommitTrue(connection);


    List<DbExecution> dbExecutionList = dbWriter.getDbExecutionList();
    Assertions.assertEquals(4, dbExecutionList.size());

    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(0));
    Assertions.assertInstanceOf(PreparedStatementExecutionBo.class, dbExecutionList.get(1));
    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(2));
    Assertions.assertInstanceOf(PreparedStatementExecutionBo.class, dbExecutionList.get(3));

    List<AbstractStatementExecutionBo> abstractStatementExecutionBoList = dbExecutionList.stream().map(a -> ((AbstractStatementExecutionBo) a)).collect(Collectors.toList());

    Set<String> txIdSet = new HashSet<>();
    txIdSet.add(abstractStatementExecutionBoList.get(0).getTxId());
    txIdSet.add(abstractStatementExecutionBoList.get(1).getTxId());
    txIdSet.add(abstractStatementExecutionBoList.get(2).getTxId());
    txIdSet.add(abstractStatementExecutionBoList.get(3).getTxId());
    Assertions.assertEquals(2, txIdSet.size());

    Set<String> dataSourcePortTrailIdSet = new HashSet<>();
    dataSourcePortTrailIdSet.add(abstractStatementExecutionBoList.get(0).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(abstractStatementExecutionBoList.get(1).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(abstractStatementExecutionBoList.get(2).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(abstractStatementExecutionBoList.get(3).getDataSourcePortTrailId());
    Assertions.assertEquals(1, dataSourcePortTrailIdSet.size());

    Set<String> connectionPortTrailIdSet = new HashSet<>();
    connectionPortTrailIdSet.add(abstractStatementExecutionBoList.get(0).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(abstractStatementExecutionBoList.get(1).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(abstractStatementExecutionBoList.get(2).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(abstractStatementExecutionBoList.get(3).getConnectionPortTrailId());
    Assertions.assertEquals(1, connectionPortTrailIdSet.size());

    Set<String> statementPortTrailIdSet = new HashSet<>();
    statementPortTrailIdSet.add(abstractStatementExecutionBoList.get(0).getStatementPortTrailId());
    statementPortTrailIdSet.add(abstractStatementExecutionBoList.get(1).getStatementPortTrailId());
    statementPortTrailIdSet.add(abstractStatementExecutionBoList.get(2).getStatementPortTrailId());
    statementPortTrailIdSet.add(abstractStatementExecutionBoList.get(3).getStatementPortTrailId());
    Assertions.assertEquals(1, statementPortTrailIdSet.size());

    Assertions.assertEquals(1, ((StatementExecutionBo) abstractStatementExecutionBoList.get(0)).getStatementSqlList().size());
    Assertions.assertEquals(2, ((PreparedStatementExecutionBo) abstractStatementExecutionBoList.get(1)).getPreparedStatementParameterList().size());
    Assertions.assertEquals(1, ((StatementExecutionBo) abstractStatementExecutionBoList.get(2)).getStatementSqlList().size());
    Assertions.assertEquals(2, ((PreparedStatementExecutionBo) abstractStatementExecutionBoList.get(3)).getPreparedStatementParameterList().size());

    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED, abstractStatementExecutionBoList.get(0).getStatementState());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED, abstractStatementExecutionBoList.get(1).getStatementState());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED, abstractStatementExecutionBoList.get(2).getStatementState());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED, abstractStatementExecutionBoList.get(3).getStatementState());

    Assertions.assertEquals(3, ((StatementExecutionBo) abstractStatementExecutionBoList.get(0)).getStatementSqlList().get(0).getOrderInStatement());
    Assertions.assertEquals(1, ((PreparedStatementExecutionBo) abstractStatementExecutionBoList.get(1)).getPreparedStatementParameterList().get(0).getOrderInStatement());
    Assertions.assertEquals(2, ((PreparedStatementExecutionBo) abstractStatementExecutionBoList.get(1)).getPreparedStatementParameterList().get(1).getOrderInStatement());
    Assertions.assertEquals(6, ((StatementExecutionBo) abstractStatementExecutionBoList.get(2)).getStatementSqlList().get(0).getOrderInStatement());
    Assertions.assertEquals(4, ((PreparedStatementExecutionBo) abstractStatementExecutionBoList.get(3)).getPreparedStatementParameterList().get(0).getOrderInStatement());
    Assertions.assertEquals(5, ((PreparedStatementExecutionBo) abstractStatementExecutionBoList.get(3)).getPreparedStatementParameterList().get(1).getOrderInStatement());

    Assertions.assertEquals(3, ((StatementExecutionBo) abstractStatementExecutionBoList.get(0)).getStatementSqlList().get(0).getOrderInConnection());
    Assertions.assertEquals(1, ((PreparedStatementExecutionBo) abstractStatementExecutionBoList.get(1)).getPreparedStatementParameterList().get(0).getOrderInConnection());
    Assertions.assertEquals(2, ((PreparedStatementExecutionBo) abstractStatementExecutionBoList.get(1)).getPreparedStatementParameterList().get(1).getOrderInConnection());
    Assertions.assertEquals(6, ((StatementExecutionBo) abstractStatementExecutionBoList.get(2)).getStatementSqlList().get(0).getOrderInConnection());
    Assertions.assertEquals(4, ((PreparedStatementExecutionBo) abstractStatementExecutionBoList.get(3)).getPreparedStatementParameterList().get(0).getOrderInConnection());
    Assertions.assertEquals(5, ((PreparedStatementExecutionBo) abstractStatementExecutionBoList.get(3)).getPreparedStatementParameterList().get(1).getOrderInConnection());

    Assertions.assertTrue(((StatementExecutionBo) abstractStatementExecutionBoList.get(0)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("1"));
    Assertions.assertTrue(((Unary<String>) (((PreparedStatementExecutionBo) abstractStatementExecutionBoList.get(1)).getPreparedStatementParameterList().get(0).getParameterList().get(0))).getF1().endsWith("0"));
    Assertions.assertTrue(((Unary<String>) (((PreparedStatementExecutionBo) abstractStatementExecutionBoList.get(1)).getPreparedStatementParameterList().get(1).getParameterList().get(0))).getF1().endsWith("0"));
    Assertions.assertTrue(((StatementExecutionBo) abstractStatementExecutionBoList.get(2)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("1"));
    Assertions.assertTrue(((Unary<String>) (((PreparedStatementExecutionBo) abstractStatementExecutionBoList.get(3)).getPreparedStatementParameterList().get(0).getParameterList().get(0))).getF1().endsWith("0"));
    Assertions.assertTrue(((Unary<String>) (((PreparedStatementExecutionBo) abstractStatementExecutionBoList.get(3)).getPreparedStatementParameterList().get(1).getParameterList().get(0))).getF1().endsWith("0"));

  }

  @Test
  void testExecuteDoesNotClearParameterWhenSetAutoCommitFalse() throws Exception {
    Connection connection = dataSource.getConnection();
    testExecuteDoesNotClearParameterWhenSetAutoCommitFalse(connection);


    List<DbExecution> dbExecutionList = dbWriter.getDbExecutionList();
    Assertions.assertEquals(4, dbExecutionList.size());

    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(0));
    Assertions.assertInstanceOf(PreparedStatementExecutionBo.class, dbExecutionList.get(1));
    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(2));
    Assertions.assertInstanceOf(PreparedStatementExecutionBo.class, dbExecutionList.get(3));

    List<AbstractStatementExecutionBo> abstractStatementExecutionBoList = dbExecutionList.stream().map(a -> ((AbstractStatementExecutionBo) a)).collect(Collectors.toList());

    Set<String> txIdSet = new HashSet<>();
    txIdSet.add(abstractStatementExecutionBoList.get(0).getTxId());
    txIdSet.add(abstractStatementExecutionBoList.get(1).getTxId());
    txIdSet.add(abstractStatementExecutionBoList.get(2).getTxId());
    txIdSet.add(abstractStatementExecutionBoList.get(3).getTxId());
    Assertions.assertEquals(1, txIdSet.size());

    Set<String> dataSourcePortTrailIdSet = new HashSet<>();
    dataSourcePortTrailIdSet.add(abstractStatementExecutionBoList.get(0).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(abstractStatementExecutionBoList.get(1).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(abstractStatementExecutionBoList.get(2).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(abstractStatementExecutionBoList.get(3).getDataSourcePortTrailId());
    Assertions.assertEquals(1, dataSourcePortTrailIdSet.size());

    Set<String> connectionPortTrailIdSet = new HashSet<>();
    connectionPortTrailIdSet.add(abstractStatementExecutionBoList.get(0).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(abstractStatementExecutionBoList.get(1).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(abstractStatementExecutionBoList.get(2).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(abstractStatementExecutionBoList.get(3).getConnectionPortTrailId());
    Assertions.assertEquals(1, connectionPortTrailIdSet.size());

    Set<String> statementPortTrailIdSet = new HashSet<>();
    statementPortTrailIdSet.add(abstractStatementExecutionBoList.get(0).getStatementPortTrailId());
    statementPortTrailIdSet.add(abstractStatementExecutionBoList.get(1).getStatementPortTrailId());
    statementPortTrailIdSet.add(abstractStatementExecutionBoList.get(2).getStatementPortTrailId());
    statementPortTrailIdSet.add(abstractStatementExecutionBoList.get(3).getStatementPortTrailId());
    Assertions.assertEquals(1, statementPortTrailIdSet.size());

    Assertions.assertEquals(1, ((StatementExecutionBo) abstractStatementExecutionBoList.get(0)).getStatementSqlList().size());
    Assertions.assertEquals(2, ((PreparedStatementExecutionBo) abstractStatementExecutionBoList.get(1)).getPreparedStatementParameterList().size());
    Assertions.assertEquals(1, ((StatementExecutionBo) abstractStatementExecutionBoList.get(2)).getStatementSqlList().size());
    Assertions.assertEquals(2, ((PreparedStatementExecutionBo) abstractStatementExecutionBoList.get(3)).getPreparedStatementParameterList().size());

    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED, abstractStatementExecutionBoList.get(0).getStatementState());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED, abstractStatementExecutionBoList.get(1).getStatementState());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED, abstractStatementExecutionBoList.get(2).getStatementState());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED, abstractStatementExecutionBoList.get(3).getStatementState());

    Assertions.assertEquals(3, ((StatementExecutionBo) abstractStatementExecutionBoList.get(0)).getStatementSqlList().get(0).getOrderInStatement());
    Assertions.assertEquals(1, ((PreparedStatementExecutionBo) abstractStatementExecutionBoList.get(1)).getPreparedStatementParameterList().get(0).getOrderInStatement());
    Assertions.assertEquals(2, ((PreparedStatementExecutionBo) abstractStatementExecutionBoList.get(1)).getPreparedStatementParameterList().get(1).getOrderInStatement());
    Assertions.assertEquals(6, ((StatementExecutionBo) abstractStatementExecutionBoList.get(2)).getStatementSqlList().get(0).getOrderInStatement());
    Assertions.assertEquals(4, ((PreparedStatementExecutionBo) abstractStatementExecutionBoList.get(3)).getPreparedStatementParameterList().get(0).getOrderInStatement());
    Assertions.assertEquals(5, ((PreparedStatementExecutionBo) abstractStatementExecutionBoList.get(3)).getPreparedStatementParameterList().get(1).getOrderInStatement());

    Assertions.assertEquals(3, ((StatementExecutionBo) abstractStatementExecutionBoList.get(0)).getStatementSqlList().get(0).getOrderInConnection());
    Assertions.assertEquals(1, ((PreparedStatementExecutionBo) abstractStatementExecutionBoList.get(1)).getPreparedStatementParameterList().get(0).getOrderInConnection());
    Assertions.assertEquals(2, ((PreparedStatementExecutionBo) abstractStatementExecutionBoList.get(1)).getPreparedStatementParameterList().get(1).getOrderInConnection());
    Assertions.assertEquals(6, ((StatementExecutionBo) abstractStatementExecutionBoList.get(2)).getStatementSqlList().get(0).getOrderInConnection());
    Assertions.assertEquals(4, ((PreparedStatementExecutionBo) abstractStatementExecutionBoList.get(3)).getPreparedStatementParameterList().get(0).getOrderInConnection());
    Assertions.assertEquals(5, ((PreparedStatementExecutionBo) abstractStatementExecutionBoList.get(3)).getPreparedStatementParameterList().get(1).getOrderInConnection());

    Assertions.assertTrue(((StatementExecutionBo) abstractStatementExecutionBoList.get(0)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("1"));
    Assertions.assertTrue(((Unary<String>) (((PreparedStatementExecutionBo) abstractStatementExecutionBoList.get(1)).getPreparedStatementParameterList().get(0).getParameterList().get(0))).getF1().endsWith("0"));
    Assertions.assertTrue(((Unary<String>) (((PreparedStatementExecutionBo) abstractStatementExecutionBoList.get(1)).getPreparedStatementParameterList().get(1).getParameterList().get(0))).getF1().endsWith("0"));
    Assertions.assertTrue(((StatementExecutionBo) abstractStatementExecutionBoList.get(2)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("1"));
    Assertions.assertTrue(((Unary<String>) (((PreparedStatementExecutionBo) abstractStatementExecutionBoList.get(3)).getPreparedStatementParameterList().get(0).getParameterList().get(0))).getF1().endsWith("0"));
    Assertions.assertTrue(((Unary<String>) (((PreparedStatementExecutionBo) abstractStatementExecutionBoList.get(3)).getPreparedStatementParameterList().get(1).getParameterList().get(0))).getF1().endsWith("0"));

  }

  @Test
  void testPreparedStatementCloseMethodDoesNotDiscardData() throws Exception {
    Connection connection = dataSource.getConnection();
    testPreparedStatementCloseMethodDoesNotDiscardData(connection);


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
    Assertions.assertEquals(2, statementPortTrailIdSet.size());

    Assertions.assertEquals(1, preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().size());
    Assertions.assertEquals(1, preparedStatementExecutionBoList.get(1).getPreparedStatementParameterList().size());

    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED, preparedStatementExecutionBoList.get(0).getStatementState());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED, preparedStatementExecutionBoList.get(1).getStatementState());

    Assertions.assertEquals(1, preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().get(0).getOrderInStatement());
    Assertions.assertEquals(1, preparedStatementExecutionBoList.get(1).getPreparedStatementParameterList().get(0).getOrderInStatement());

    Assertions.assertEquals(1, preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().get(0).getOrderInConnection());
    Assertions.assertEquals(2, preparedStatementExecutionBoList.get(1).getPreparedStatementParameterList().get(0).getOrderInConnection());

    Assertions.assertTrue(((Unary<String>) (preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().get(0).getParameterList().get(0))).getF1().endsWith("0"));
    Assertions.assertTrue(((Unary<String>) (preparedStatementExecutionBoList.get(1).getPreparedStatementParameterList().get(0).getParameterList().get(0))).getF1().endsWith("1"));

  }

  @Test
  void testRollbackMethodWillRollBackAllDataOfPreparedStatement() throws Exception {
    Connection connection = dataSource.getConnection();
    testRollbackMethodWillRollBackAllDataOfPreparedStatement(connection);


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
    Assertions.assertEquals(2, statementPortTrailIdSet.size());

    Assertions.assertEquals(1, preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().size());
    Assertions.assertEquals(1, preparedStatementExecutionBoList.get(1).getPreparedStatementParameterList().size());

    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_ROLLBACK, preparedStatementExecutionBoList.get(0).getStatementState());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_ROLLBACK, preparedStatementExecutionBoList.get(1).getStatementState());

    Assertions.assertEquals(1, preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().get(0).getOrderInStatement());
    Assertions.assertEquals(1, preparedStatementExecutionBoList.get(1).getPreparedStatementParameterList().get(0).getOrderInStatement());

    Assertions.assertEquals(1, preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().get(0).getOrderInConnection());
    Assertions.assertEquals(2, preparedStatementExecutionBoList.get(1).getPreparedStatementParameterList().get(0).getOrderInConnection());

    Assertions.assertTrue(((Unary<String>) (preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().get(0).getParameterList().get(0))).getF1().endsWith("0"));
    Assertions.assertTrue(((Unary<String>) (preparedStatementExecutionBoList.get(1).getPreparedStatementParameterList().get(0).getParameterList().get(0))).getF1().endsWith("1"));

  }

  @Test
  void testRollbackMethodDoesNotClearDataThatHasNotExecuted() throws Exception {
    Connection connection = dataSource.getConnection();
    testRollbackMethodDoesNotClearDataThatHasNotExecuted(connection);


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
    Assertions.assertEquals(2, statementPortTrailIdSet.size());

    Assertions.assertEquals(1, preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().size());
    Assertions.assertEquals(1, preparedStatementExecutionBoList.get(1).getPreparedStatementParameterList().size());

    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED, preparedStatementExecutionBoList.get(0).getStatementState());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED, preparedStatementExecutionBoList.get(1).getStatementState());

    Assertions.assertEquals(1, preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().get(0).getOrderInStatement());
    Assertions.assertEquals(1, preparedStatementExecutionBoList.get(1).getPreparedStatementParameterList().get(0).getOrderInStatement());

    Assertions.assertEquals(1, preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().get(0).getOrderInConnection());
    Assertions.assertEquals(2, preparedStatementExecutionBoList.get(1).getPreparedStatementParameterList().get(0).getOrderInConnection());

    Assertions.assertTrue(((Unary<String>) (preparedStatementExecutionBoList.get(0).getPreparedStatementParameterList().get(0).getParameterList().get(0))).getF1().endsWith("0"));
    Assertions.assertTrue(((Unary<String>) (preparedStatementExecutionBoList.get(1).getPreparedStatementParameterList().get(0).getParameterList().get(0))).getF1().endsWith("1"));

  }

  @Test
  void testCommitDoesNotCommitDataThatOnlyAddBatched() throws Exception {
    Connection connection = dataSource.getConnection();
    testCommitDoesNotCommitDataThatOnlyAddBatched(connection);


    List<DbExecution> dbExecutionList = dbWriter.getDbExecutionList();
    Assertions.assertEquals(0, dbExecutionList.size());
  }

  @Test
  void testUpdateAutoCommitFromFalseToTrueDoesNotCommitDataThatOnlyAddBatched() throws Exception {
    Connection connection = dataSource.getConnection();
    testUpdateAutoCommitFromFalseToTrueDoesNotCommitDataThatOnlyAddBatched(connection);

    List<DbExecution> dbExecutionList = dbWriter.getDbExecutionList();
    Assertions.assertEquals(0, dbExecutionList.size());
  }


  @Test
  void testExecuteUpdateDoesNotAffectDataThatHasAddBatched() throws Exception {
    Connection connection = dataSource.getConnection();
    testExecuteUpdateDoesNotAffectDataThatHasAddBatched(connection);


    List<DbExecution> dbExecutionList = dbWriter.getDbExecutionList();
    Assertions.assertEquals(5, dbExecutionList.size());

    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(0));
    Assertions.assertInstanceOf(PreparedStatementExecutionBo.class, dbExecutionList.get(1));
    Assertions.assertInstanceOf(StatementExecutionBo.class, dbExecutionList.get(2));
    Assertions.assertInstanceOf(PreparedStatementExecutionBo.class, dbExecutionList.get(3));
    Assertions.assertInstanceOf(PreparedStatementExecutionBo.class, dbExecutionList.get(4));

    List<AbstractStatementExecutionBo> abstractStatementExecutionBoList = dbExecutionList.stream().map(a -> ((AbstractStatementExecutionBo) a)).collect(Collectors.toList());

    Set<String> txIdSet = new HashSet<>();
    txIdSet.add(abstractStatementExecutionBoList.get(0).getTxId());
    txIdSet.add(abstractStatementExecutionBoList.get(1).getTxId());
    txIdSet.add(abstractStatementExecutionBoList.get(2).getTxId());
    txIdSet.add(abstractStatementExecutionBoList.get(3).getTxId());
    txIdSet.add(abstractStatementExecutionBoList.get(4).getTxId());
    Assertions.assertEquals(1, txIdSet.size());

    Set<String> dataSourcePortTrailIdSet = new HashSet<>();
    dataSourcePortTrailIdSet.add(abstractStatementExecutionBoList.get(0).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(abstractStatementExecutionBoList.get(1).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(abstractStatementExecutionBoList.get(2).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(abstractStatementExecutionBoList.get(3).getDataSourcePortTrailId());
    dataSourcePortTrailIdSet.add(abstractStatementExecutionBoList.get(4).getDataSourcePortTrailId());
    Assertions.assertEquals(1, dataSourcePortTrailIdSet.size());

    Set<String> connectionPortTrailIdSet = new HashSet<>();
    connectionPortTrailIdSet.add(abstractStatementExecutionBoList.get(0).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(abstractStatementExecutionBoList.get(1).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(abstractStatementExecutionBoList.get(2).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(abstractStatementExecutionBoList.get(3).getConnectionPortTrailId());
    connectionPortTrailIdSet.add(abstractStatementExecutionBoList.get(4).getConnectionPortTrailId());
    Assertions.assertEquals(1, connectionPortTrailIdSet.size());

    Set<String> statementPortTrailIdSet = new HashSet<>();
    statementPortTrailIdSet.add(abstractStatementExecutionBoList.get(0).getStatementPortTrailId());
    statementPortTrailIdSet.add(abstractStatementExecutionBoList.get(1).getStatementPortTrailId());
    statementPortTrailIdSet.add(abstractStatementExecutionBoList.get(2).getStatementPortTrailId());
    statementPortTrailIdSet.add(abstractStatementExecutionBoList.get(3).getStatementPortTrailId());
    statementPortTrailIdSet.add(abstractStatementExecutionBoList.get(4).getStatementPortTrailId());
    Assertions.assertEquals(2, statementPortTrailIdSet.size());

    Assertions.assertEquals(1, ((StatementExecutionBo) abstractStatementExecutionBoList.get(0)).getStatementSqlList().size());
    Assertions.assertEquals(1, ((PreparedStatementExecutionBo) abstractStatementExecutionBoList.get(1)).getPreparedStatementParameterList().size());
    Assertions.assertEquals(1, ((StatementExecutionBo) abstractStatementExecutionBoList.get(2)).getStatementSqlList().size());
    Assertions.assertEquals(1, ((PreparedStatementExecutionBo) abstractStatementExecutionBoList.get(3)).getPreparedStatementParameterList().size());
    Assertions.assertEquals(1, ((PreparedStatementExecutionBo) abstractStatementExecutionBoList.get(4)).getPreparedStatementParameterList().size());

    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED, abstractStatementExecutionBoList.get(0).getStatementState());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED, abstractStatementExecutionBoList.get(1).getStatementState());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED, abstractStatementExecutionBoList.get(2).getStatementState());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED, abstractStatementExecutionBoList.get(3).getStatementState());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED, abstractStatementExecutionBoList.get(4).getStatementState());

    Assertions.assertEquals(1, ((StatementExecutionBo) abstractStatementExecutionBoList.get(0)).getStatementSqlList().get(0).getOrderInStatement());
    Assertions.assertEquals(1, ((PreparedStatementExecutionBo) abstractStatementExecutionBoList.get(1)).getPreparedStatementParameterList().get(0).getOrderInStatement());
    Assertions.assertEquals(3, ((StatementExecutionBo) abstractStatementExecutionBoList.get(2)).getStatementSqlList().get(0).getOrderInStatement());
    Assertions.assertEquals(2, ((PreparedStatementExecutionBo) abstractStatementExecutionBoList.get(3)).getPreparedStatementParameterList().get(0).getOrderInStatement());
    Assertions.assertEquals(2, ((PreparedStatementExecutionBo) abstractStatementExecutionBoList.get(4)).getPreparedStatementParameterList().get(0).getOrderInStatement());

    Assertions.assertEquals(1, ((StatementExecutionBo) abstractStatementExecutionBoList.get(0)).getStatementSqlList().get(0).getOrderInConnection());
    Assertions.assertEquals(2, ((PreparedStatementExecutionBo) abstractStatementExecutionBoList.get(1)).getPreparedStatementParameterList().get(0).getOrderInConnection());
    Assertions.assertEquals(4, ((StatementExecutionBo) abstractStatementExecutionBoList.get(2)).getStatementSqlList().get(0).getOrderInConnection());
    Assertions.assertEquals(3, ((PreparedStatementExecutionBo) abstractStatementExecutionBoList.get(3)).getPreparedStatementParameterList().get(0).getOrderInConnection());
    Assertions.assertEquals(5, ((PreparedStatementExecutionBo) abstractStatementExecutionBoList.get(4)).getPreparedStatementParameterList().get(0).getOrderInConnection());

    Assertions.assertTrue(((StatementExecutionBo) abstractStatementExecutionBoList.get(0)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("0"));
    Assertions.assertTrue(((Unary<String>) (((PreparedStatementExecutionBo) abstractStatementExecutionBoList.get(1)).getPreparedStatementParameterList().get(0).getParameterList().get(0))).getF1().endsWith("1"));
    Assertions.assertTrue(((StatementExecutionBo) abstractStatementExecutionBoList.get(2)).getStatementSqlList().get(0).getSql().replace("'", "").endsWith("3"));
    Assertions.assertTrue(((Unary<String>) (((PreparedStatementExecutionBo) abstractStatementExecutionBoList.get(3)).getPreparedStatementParameterList().get(0).getParameterList().get(0))).getF1().endsWith("2"));
    Assertions.assertTrue(((Unary<String>) (((PreparedStatementExecutionBo) abstractStatementExecutionBoList.get(4)).getPreparedStatementParameterList().get(0).getParameterList().get(0))).getF1().endsWith("4"));
  }


  @AfterEach
  void after() throws Exception {
    if (dataSource instanceof AutoCloseable) {
      ((AutoCloseable) dataSource).close();
    }
  }

}
