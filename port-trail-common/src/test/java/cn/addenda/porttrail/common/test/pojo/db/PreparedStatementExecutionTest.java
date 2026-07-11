package cn.addenda.porttrail.common.test.pojo.db;

import cn.addenda.porttrail.common.pojo.db.bo.AbstractStatementExecutionBo;
import cn.addenda.porttrail.common.pojo.db.bo.DbExecution;
import cn.addenda.porttrail.common.pojo.db.bo.PreparedStatementExecutionBo;
import cn.addenda.porttrail.common.pojo.db.bo.PreparedStatementParameter;
import cn.addenda.porttrail.common.pojo.db.dto.PreparedStatementExecutionDto;
import cn.addenda.porttrail.common.pojo.db.dto.PreparedStatementParameterDto;
import cn.addenda.porttrail.common.tuple.Binary;
import cn.addenda.porttrail.common.tuple.Ternary;
import cn.addenda.porttrail.common.tuple.Unary;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class PreparedStatementExecutionTest {

  @Test
  void testDtoFromBo() {
    List<PreparedStatementParameter> paramList = new ArrayList<>();
    PreparedStatementParameter param1 = new PreparedStatementParameter();
    param1.setOrderInStatement(1);
    param1.setOrderInConnection(1);
    param1.set(0, "setString", Unary.of("hello"));
    paramList.add(param1);

    PreparedStatementParameter param2 = new PreparedStatementParameter();
    param2.setOrderInStatement(2);
    param2.setOrderInConnection(2);
    param2.set(0, "setInt", Unary.of(42));
    paramList.add(param2);

    PreparedStatementExecutionBo bo = new PreparedStatementExecutionBo(
            "ds-001", "conn-001", "stmt-001",
            AbstractStatementExecutionBo.STATEMENT_STATE_QUERY,
            "SELECT * FROM t WHERE id = ?", paramList.get(0),
            "tx-001", 1000L, 2000L
    );
    bo.setPreparedStatementParameterList(paramList);

    PreparedStatementExecutionDto dto = new PreparedStatementExecutionDto(bo);

    Assertions.assertEquals("ds-001", dto.getDataSourcePortTrailId());
    Assertions.assertEquals("conn-001", dto.getConnectionPortTrailId());
    Assertions.assertEquals("stmt-001", dto.getStatementPortTrailId());
    Assertions.assertEquals("SELECT * FROM t WHERE id = ?", dto.getParameterizedSql());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_QUERY, dto.getStatementState());
    Assertions.assertEquals("tx-001", dto.getTxId());
    Assertions.assertEquals(Long.valueOf(1000L), dto.getStart());
    Assertions.assertEquals(Long.valueOf(2000L), dto.getEnd());
    Assertions.assertEquals(2, dto.getPreparedStatementParameterDtoList().size());
    Assertions.assertEquals(1, dto.getPreparedStatementParameterDtoList().get(0).getOrderInStatement());
    Assertions.assertEquals(2, dto.getPreparedStatementParameterDtoList().get(1).getOrderInStatement());
    Assertions.assertEquals(DbExecution.DB_EXECUTION_TYPE_PREPARED_STATEMENT, bo.getDbExecutionType());
  }

  @Test
  void testBoFromDto() {
    List<PreparedStatementParameterDto> paramDtoList = new ArrayList<>();

    PreparedStatementParameter param1 = new PreparedStatementParameter();
    param1.setOrderInStatement(1);
    param1.setOrderInConnection(1);
    param1.set(0, "setString", Unary.of("test"));
    PreparedStatementParameterDto paramDto1 = new PreparedStatementParameterDto(param1);
    paramDtoList.add(paramDto1);

    PreparedStatementParameter param2 = new PreparedStatementParameter();
    param2.setOrderInStatement(2);
    param2.setOrderInConnection(2);
    param2.set(0, "setInt", Unary.of(88));
    PreparedStatementParameterDto paramDto2 = new PreparedStatementParameterDto(param2);
    paramDtoList.add(paramDto2);

    PreparedStatementExecutionDto dto = new PreparedStatementExecutionDto();
    dto.setDataSourcePortTrailId("ds-002");
    dto.setConnectionPortTrailId("conn-002");
    dto.setStatementPortTrailId("stmt-002");
    dto.setParameterizedSql("UPDATE t SET a = ? WHERE b = ?");
    dto.setStatementState(AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED);
    dto.setTxId("tx-002");
    dto.setStart(3000L);
    dto.setEnd(4000L);
    dto.setPreparedStatementParameterDtoList(paramDtoList);

    PreparedStatementExecutionBo bo = new PreparedStatementExecutionBo(dto);

    Assertions.assertEquals("ds-002", bo.getDataSourcePortTrailId());
    Assertions.assertEquals("conn-002", bo.getConnectionPortTrailId());
    Assertions.assertEquals("stmt-002", bo.getStatementPortTrailId());
    Assertions.assertEquals("UPDATE t SET a = ? WHERE b = ?", bo.getParameterizedSql());
    Assertions.assertEquals(AbstractStatementExecutionBo.STATEMENT_STATE_COMMITTED, bo.getStatementState());
    Assertions.assertEquals("tx-002", bo.getTxId());
    Assertions.assertEquals(Long.valueOf(3000L), bo.getStart());
    Assertions.assertEquals(Long.valueOf(4000L), bo.getEnd());
    Assertions.assertEquals(2, bo.getPreparedStatementParameterList().size());
    Assertions.assertEquals(1, bo.getPreparedStatementParameterList().get(0).getOrderInStatement());
    Assertions.assertEquals(2, bo.getPreparedStatementParameterList().get(1).getOrderInStatement());
    Assertions.assertEquals("setString", bo.getPreparedStatementParameterList().get(0).getSetMethodList().get(0));
    Assertions.assertEquals("setInt", bo.getPreparedStatementParameterList().get(1).getSetMethodList().get(0));
  }

  @Test
  void testWithEmptyParamList() {
    PreparedStatementExecutionDto dto = new PreparedStatementExecutionDto();
    dto.setDataSourcePortTrailId("ds-003");
    dto.setConnectionPortTrailId("conn-003");
    dto.setStatementPortTrailId("stmt-003");
    dto.setParameterizedSql("SELECT 1");
    dto.setStatementState(AbstractStatementExecutionBo.STATEMENT_STATE_NEW);
    dto.setPreparedStatementParameterDtoList(new ArrayList<>());

    PreparedStatementExecutionBo bo = new PreparedStatementExecutionBo(dto);

    Assertions.assertTrue(bo.getPreparedStatementParameterList().isEmpty());
  }

  @Test
  void testFullRoundTrip() {
    List<PreparedStatementParameter> paramList = new ArrayList<>();
    PreparedStatementParameter param1 = new PreparedStatementParameter();
    param1.setOrderInStatement(1);
    param1.setOrderInConnection(1);
    param1.set(0, "setString", Unary.of("hello"));
    param1.set(1, "setObject", Binary.of("key1", 123L));
    paramList.add(param1);

    PreparedStatementParameter param2 = new PreparedStatementParameter();
    param2.setOrderInStatement(2);
    param2.setOrderInConnection(2);
    param2.set(0, "setInt", Unary.of(42));
    param2.set(1, "setValues", Ternary.of("x", 1, 3.14));
    paramList.add(param2);

    PreparedStatementExecutionBo bo1 = new PreparedStatementExecutionBo(
            "ds-001", "conn-001", "stmt-001",
            AbstractStatementExecutionBo.STATEMENT_STATE_QUERY,
            "SELECT * FROM t WHERE id = ? AND name = ?", paramList.get(0),
            "tx-001", 1000L, 2000L
    );
    bo1.setPreparedStatementParameterList(paramList);

    PreparedStatementExecutionDto dto1 = new PreparedStatementExecutionDto(bo1);
    PreparedStatementExecutionBo bo2 = new PreparedStatementExecutionBo(dto1);
    PreparedStatementExecutionDto dto2 = new PreparedStatementExecutionDto(bo2);

    Assertions.assertEquals(bo1.getDataSourcePortTrailId(), bo2.getDataSourcePortTrailId());
    Assertions.assertEquals(bo1.getConnectionPortTrailId(), bo2.getConnectionPortTrailId());
    Assertions.assertEquals(bo1.getStatementPortTrailId(), bo2.getStatementPortTrailId());
    Assertions.assertEquals(bo1.getParameterizedSql(), bo2.getParameterizedSql());
    Assertions.assertEquals(bo1.getStatementState(), bo2.getStatementState());
    Assertions.assertEquals(bo1.getTxId(), bo2.getTxId());
    Assertions.assertEquals(bo1.getStart(), bo2.getStart());
    Assertions.assertEquals(bo1.getEnd(), bo2.getEnd());
    Assertions.assertEquals(bo1.getPreparedStatementParameterList().size(), bo2.getPreparedStatementParameterList().size());
    for (int i = 0; i < bo1.getPreparedStatementParameterList().size(); i++) {
      PreparedStatementParameter p1 = bo1.getPreparedStatementParameterList().get(i);
      PreparedStatementParameter p2 = bo2.getPreparedStatementParameterList().get(i);
      Assertions.assertEquals(p1.getOrderInStatement(), p2.getOrderInStatement());
      Assertions.assertEquals(p1.getOrderInConnection(), p2.getOrderInConnection());
      Assertions.assertEquals(p1.getCapacity(), p2.getCapacity());
      Assertions.assertEquals(p1.getSetMethodList(), p2.getSetMethodList());
      Assertions.assertEquals(p1.getParameterList(), p2.getParameterList());
    }

    Assertions.assertEquals(dto1.getDataSourcePortTrailId(), dto2.getDataSourcePortTrailId());
    Assertions.assertEquals(dto1.getConnectionPortTrailId(), dto2.getConnectionPortTrailId());
    Assertions.assertEquals(dto1.getStatementPortTrailId(), dto2.getStatementPortTrailId());
    Assertions.assertEquals(dto1.getParameterizedSql(), dto2.getParameterizedSql());
    Assertions.assertEquals(dto1.getStatementState(), dto2.getStatementState());
    Assertions.assertEquals(dto1.getTxId(), dto2.getTxId());
    Assertions.assertEquals(dto1.getStart(), dto2.getStart());
    Assertions.assertEquals(dto1.getEnd(), dto2.getEnd());
    Assertions.assertEquals(dto1.getPreparedStatementParameterDtoList().size(), dto2.getPreparedStatementParameterDtoList().size());
    for (int i = 0; i < dto1.getPreparedStatementParameterDtoList().size(); i++) {
      PreparedStatementParameterDto d1 = dto1.getPreparedStatementParameterDtoList().get(i);
      PreparedStatementParameterDto d2 = dto2.getPreparedStatementParameterDtoList().get(i);
      Assertions.assertEquals(d1.getOrderInStatement(), d2.getOrderInStatement());
      Assertions.assertEquals(d1.getOrderInConnection(), d2.getOrderInConnection());
      Assertions.assertEquals(d1.getSetMethodList(), d2.getSetMethodList());
      Assertions.assertEquals(d1.getParameterTypeList(), d2.getParameterTypeList());
      Assertions.assertEquals(d1.getParameterValueList().size(), d2.getParameterValueList().size());
      for (int j = 0; j < d1.getParameterValueList().size(); j++) {
        Assertions.assertArrayEquals(d1.getParameterValueList().get(j), d2.getParameterValueList().get(j));
      }
    }
  }

  @Test
  void testIfKeepAlive() {
    PreparedStatementExecutionBo bo = new PreparedStatementExecutionBo(" select 1 ");
    Assertions.assertTrue(bo.ifKeepAlive());

    bo.setParameterizedSql("SELECT 1 FROM DUAL");
    Assertions.assertTrue(bo.ifKeepAlive());

    bo.setParameterizedSql("SELECT * FROM t WHERE id = ?");
    Assertions.assertFalse(bo.ifKeepAlive());
  }

}
