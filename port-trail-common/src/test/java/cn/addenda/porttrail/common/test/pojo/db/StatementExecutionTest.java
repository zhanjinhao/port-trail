package cn.addenda.porttrail.common.test.pojo.db;

import cn.addenda.porttrail.common.pojo.db.bo.AbstractStatementExecutionBo;
import cn.addenda.porttrail.common.pojo.db.bo.DbExecution;
import cn.addenda.porttrail.common.pojo.db.bo.StatementExecutionBo;
import cn.addenda.porttrail.common.pojo.db.bo.StatementSql;
import cn.addenda.porttrail.common.pojo.db.dto.StatementExecutionDto;
import cn.addenda.porttrail.common.pojo.db.dto.StatementSqlDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class StatementExecutionTest {

  @Test
  void testDtoFromBo() {
    List<StatementSql> sqlList = new ArrayList<>();
    sqlList.add(new StatementSql("SELECT * FROM t1", 1, 1));
    sqlList.add(new StatementSql("UPDATE t1 SET a = 1", 2, 1));
    sqlList.add(new StatementSql("DELETE FROM t1", 3, 2));

    StatementExecutionBo bo = new StatementExecutionBo(
            "ds-001", "conn-001", "stmt-001",
            AbstractStatementExecutionBo.STATEMENT_STATE_QUERY,
            sqlList.get(0), "tx-001", 1000L, 2000L
    );
    bo.setStatementSqlList(sqlList);

    StatementExecutionDto dto = new StatementExecutionDto(bo);

    Assertions.assertEquals("ds-001", dto.getDataSourcePortTrailId());
    Assertions.assertEquals("conn-001", dto.getConnectionPortTrailId());
    Assertions.assertEquals("stmt-001", dto.getStatementPortTrailId());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_QUERY, dto.getStatementState());
    Assertions.assertEquals("tx-001", dto.getTxId());
    Assertions.assertEquals(Long.valueOf(1000L), dto.getStart());
    Assertions.assertEquals(Long.valueOf(2000L), dto.getEnd());
    Assertions.assertEquals(3, dto.getStatementSqlDtoList().size());
    Assertions.assertEquals("SELECT * FROM t1", dto.getStatementSqlDtoList().get(0).getSql());
    Assertions.assertEquals("UPDATE t1 SET a = 1", dto.getStatementSqlDtoList().get(1).getSql());
    Assertions.assertEquals("DELETE FROM t1", dto.getStatementSqlDtoList().get(2).getSql());
    Assertions.assertEquals(DbExecution.DB_EXECUTION_TYPE_STATEMENT, bo.getDbExecutionType());
  }

  @Test
  void testBoFromDto() {
    List<StatementSqlDto> sqlDtoList = new ArrayList<>();
    StatementSqlDto dto1 = new StatementSqlDto();
    dto1.setSql("INSERT INTO t1 VALUES (1)");
    dto1.setOrderInStatement(1);
    dto1.setOrderInConnection(1);
    sqlDtoList.add(dto1);

    StatementSqlDto dto2 = new StatementSqlDto();
    dto2.setSql("SELECT 1 FROM DUAL");
    dto2.setOrderInStatement(2);
    dto2.setOrderInConnection(2);
    sqlDtoList.add(dto2);

    StatementExecutionDto dto = new StatementExecutionDto();
    dto.setDataSourcePortTrailId("ds-002");
    dto.setConnectionPortTrailId("conn-002");
    dto.setStatementPortTrailId("stmt-002");
    dto.setStatementState(AbstractStatementExecutionBo.STATEMENT_STATE_NEW);
    dto.setTxId("tx-002");
    dto.setStart(3000L);
    dto.setEnd(4000L);
    dto.setStatementSqlDtoList(sqlDtoList);

    StatementExecutionBo bo = new StatementExecutionBo(dto);

    Assertions.assertEquals("ds-002", bo.getDataSourcePortTrailId());
    Assertions.assertEquals("conn-002", bo.getConnectionPortTrailId());
    Assertions.assertEquals("stmt-002", bo.getStatementPortTrailId());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_NEW, bo.getStatementState());
    Assertions.assertEquals("tx-002", bo.getTxId());
    Assertions.assertEquals(Long.valueOf(3000L), bo.getStart());
    Assertions.assertEquals(Long.valueOf(4000L), bo.getEnd());
    Assertions.assertEquals(2, bo.getStatementSqlList().size());
    Assertions.assertEquals("INSERT INTO t1 VALUES (1)", bo.getStatementSqlList().get(0).getSql());
    Assertions.assertEquals("SELECT 1 FROM DUAL", bo.getStatementSqlList().get(1).getSql());
  }

  @Test
  void testWithEmptySqlList() {
    StatementExecutionDto dto = new StatementExecutionDto();
    dto.setDataSourcePortTrailId("ds-003");
    dto.setConnectionPortTrailId("conn-003");
    dto.setStatementPortTrailId("stmt-003");
    dto.setStatementState(AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED);
    dto.setStatementSqlDtoList(new ArrayList<>());

    StatementExecutionBo bo = new StatementExecutionBo(dto);

    Assertions.assertTrue(bo.getStatementSqlList().isEmpty());
  }

  @Test
  void testFullRoundTrip() {
    List<StatementSql> sqlList = new ArrayList<>();
    sqlList.add(new StatementSql("SELECT * FROM t1", 1, 1));
    sqlList.add(new StatementSql("UPDATE t1 SET a = 1", 2, 1));
    sqlList.add(new StatementSql("DELETE FROM t1 WHERE id = ?", 3, 2));

    StatementExecutionBo bo1 = new StatementExecutionBo(
            "ds-001", "conn-001", "stmt-001",
            AbstractStatementExecutionBo.STATEMENT_STATE_QUERY,
            sqlList.get(0), "tx-001", 1000L, 2000L
    );
    bo1.setStatementSqlList(sqlList);

    StatementExecutionDto dto1 = new StatementExecutionDto(bo1);
    StatementExecutionBo bo2 = new StatementExecutionBo(dto1);
    StatementExecutionDto dto2 = new StatementExecutionDto(bo2);

    Assertions.assertEquals(bo1.getDataSourcePortTrailId(), bo2.getDataSourcePortTrailId());
    Assertions.assertEquals(bo1.getConnectionPortTrailId(), bo2.getConnectionPortTrailId());
    Assertions.assertEquals(bo1.getStatementPortTrailId(), bo2.getStatementPortTrailId());
    Assertions.assertEquals(bo1.getStatementState(), bo2.getStatementState());
    Assertions.assertEquals(bo1.getTxId(), bo2.getTxId());
    Assertions.assertEquals(bo1.getStart(), bo2.getStart());
    Assertions.assertEquals(bo1.getEnd(), bo2.getEnd());
    Assertions.assertEquals(bo1.getStatementSqlList().size(), bo2.getStatementSqlList().size());
    for (int i = 0; i < bo1.getStatementSqlList().size(); i++) {
      StatementSql s1 = bo1.getStatementSqlList().get(i);
      StatementSql s2 = bo2.getStatementSqlList().get(i);
      Assertions.assertEquals(s1.getSql(), s2.getSql());
      Assertions.assertEquals(s1.getOrderInStatement(), s2.getOrderInStatement());
      Assertions.assertEquals(s1.getOrderInConnection(), s2.getOrderInConnection());
    }

    Assertions.assertEquals(dto1.getDataSourcePortTrailId(), dto2.getDataSourcePortTrailId());
    Assertions.assertEquals(dto1.getConnectionPortTrailId(), dto2.getConnectionPortTrailId());
    Assertions.assertEquals(dto1.getStatementPortTrailId(), dto2.getStatementPortTrailId());
    Assertions.assertEquals(dto1.getStatementState(), dto2.getStatementState());
    Assertions.assertEquals(dto1.getTxId(), dto2.getTxId());
    Assertions.assertEquals(dto1.getStart(), dto2.getStart());
    Assertions.assertEquals(dto1.getEnd(), dto2.getEnd());
    Assertions.assertEquals(dto1.getStatementSqlDtoList().size(), dto2.getStatementSqlDtoList().size());
    for (int i = 0; i < dto1.getStatementSqlDtoList().size(); i++) {
      StatementSqlDto s1 = dto1.getStatementSqlDtoList().get(i);
      StatementSqlDto s2 = dto2.getStatementSqlDtoList().get(i);
      Assertions.assertEquals(s1.getSql(), s2.getSql());
      Assertions.assertEquals(s1.getOrderInStatement(), s2.getOrderInStatement());
      Assertions.assertEquals(s1.getOrderInConnection(), s2.getOrderInConnection());
    }
  }

  @Test
  void testIfKeepAlive() {
    StatementExecutionBo bo = new StatementExecutionBo();
    bo.setStatementSqlList(Collections.singletonList(
            new StatementSql(" select 1 ", 1, 1)
    ));
    Assertions.assertTrue(bo.ifKeepAlive());

    bo.setStatementSqlList(Collections.singletonList(
            new StatementSql("SELECT 1 FROM DUAL", 1, 1)
    ));
    Assertions.assertTrue(bo.ifKeepAlive());

    bo.setStatementSqlList(Collections.singletonList(
            new StatementSql("SELECT * FROM t", 1, 1)
    ));
    Assertions.assertFalse(bo.ifKeepAlive());
  }

}
