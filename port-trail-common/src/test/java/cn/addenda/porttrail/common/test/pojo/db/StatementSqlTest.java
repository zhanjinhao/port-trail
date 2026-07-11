package cn.addenda.porttrail.common.test.pojo.db;

import cn.addenda.porttrail.common.pojo.db.bo.StatementSql;
import cn.addenda.porttrail.common.pojo.db.dto.StatementSqlDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class StatementSqlTest {

  @Test
  void testDtoFromBo() {
    StatementSql bo = new StatementSql("SELECT * FROM t", 1, 2);
    StatementSqlDto dto = new StatementSqlDto(bo);

    Assertions.assertEquals("SELECT * FROM t", dto.getSql());
    Assertions.assertEquals(1, dto.getOrderInStatement());
    Assertions.assertEquals(2, dto.getOrderInConnection());
  }

  @Test
  void testBoFromDto() {
    StatementSqlDto dto = new StatementSqlDto();
    dto.setSql("UPDATE t SET a = 1");
    dto.setOrderInStatement(3);
    dto.setOrderInConnection(4);
    StatementSql bo = new StatementSql(dto);

    Assertions.assertEquals("UPDATE t SET a = 1", bo.getSql());
    Assertions.assertEquals(3, bo.getOrderInStatement());
    Assertions.assertEquals(4, bo.getOrderInConnection());
  }

  @Test
  void testRoundTrip() {
    StatementSql bo1 = new StatementSql("DELETE FROM t", 5, 6);
    StatementSqlDto dto1 = new StatementSqlDto(bo1);
    StatementSql bo2 = new StatementSql(dto1);
    StatementSqlDto dto2 = new StatementSqlDto(bo2);

    Assertions.assertEquals(bo1.getSql(), bo2.getSql());
    Assertions.assertEquals(bo1.getOrderInStatement(), bo2.getOrderInStatement());
    Assertions.assertEquals(bo1.getOrderInConnection(), bo2.getOrderInConnection());

    Assertions.assertEquals(dto1.getSql(), dto2.getSql());
    Assertions.assertEquals(dto1.getOrderInStatement(), dto2.getOrderInStatement());
    Assertions.assertEquals(dto1.getOrderInConnection(), dto2.getOrderInConnection());
  }

}
