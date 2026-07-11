package cn.addenda.porttrail.common.test.pojo.db;

import cn.addenda.porttrail.common.entrypoint.EntryPoint;
import cn.addenda.porttrail.common.entrypoint.EntryPointSnapshot;
import cn.addenda.porttrail.common.entrypoint.EntryPointType;
import cn.addenda.porttrail.common.pojo.db.bo.DbConfigBo;
import cn.addenda.porttrail.common.pojo.db.bo.DbExecution;
import cn.addenda.porttrail.common.pojo.db.dto.DbConfigDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class DbConfigTest {

  @Test
  void testDtoFromBo() {
    EntryPointSnapshot snapshot = EntryPointSnapshot.of(
            Arrays.asList(
                    EntryPoint.of(EntryPointType.WEB_SPRINGMVC, "testController"),
                    EntryPoint.of(EntryPointType.ORM_MYBATIS, "insertUser")
            ), "main", "trace-1", 1L);
    DbConfigBo bo = new DbConfigBo();
    bo.setJdbcUrl("jdbc:mysql://localhost:3306/test");
    bo.setUser("root");
    bo.setPassword("123456");
    bo.setDataSourcePortTrailId("ds-001");
    bo.setConnectionPortTrailId("conn-001");
    bo.setStatementPortTrailId("stmt-001");
    bo.setDriverName("com.mysql.cj.jdbc.Driver");
    bo.setEntryPointSnapshot(snapshot);

    DbConfigDto dto = new DbConfigDto(bo);

    Assertions.assertEquals("jdbc:mysql://localhost:3306/test", dto.getJdbcUrl());
    Assertions.assertEquals("root", dto.getUser());
    Assertions.assertEquals("123456", dto.getPassword());
    Assertions.assertEquals("ds-001", dto.getDataSourcePortTrailId());
    Assertions.assertEquals("conn-001", dto.getConnectionPortTrailId());
    Assertions.assertEquals("stmt-001", dto.getStatementPortTrailId());
    Assertions.assertEquals("com.mysql.cj.jdbc.Driver", dto.getDriverName());
    Assertions.assertEquals(snapshot, dto.getEntryPointSnapshot());
  }

  @Test
  void testBoFromDto() {
    EntryPointSnapshot snapshot = EntryPointSnapshot.of(
            Arrays.asList(
                    EntryPoint.of(EntryPointType.SERVLET_JAVAX, "dispatcherServlet"),
                    EntryPoint.of(EntryPointType.TX_TRANSACTIONAL, "transferMoney"),
                    EntryPoint.of(EntryPointType.REMOTE_HTTPCLIENT, "callExternalApi")
            ), "worker", "trace-2", 2L);
    DbConfigDto dto = new DbConfigDto();
    dto.setJdbcUrl("jdbc:postgresql://localhost:5432/mydb");
    dto.setUser("admin");
    dto.setPassword("secret");
    dto.setDataSourcePortTrailId("ds-002");
    dto.setConnectionPortTrailId("conn-002");
    dto.setStatementPortTrailId("stmt-002");
    dto.setDriverName("org.postgresql.Driver");
    dto.setEntryPointSnapshot(snapshot);

    DbConfigBo bo = new DbConfigBo(dto);

    Assertions.assertEquals("jdbc:postgresql://localhost:5432/mydb", bo.getJdbcUrl());
    Assertions.assertEquals("admin", bo.getUser());
    Assertions.assertEquals("secret", bo.getPassword());
    Assertions.assertEquals("ds-002", bo.getDataSourcePortTrailId());
    Assertions.assertEquals("conn-002", bo.getConnectionPortTrailId());
    Assertions.assertEquals("stmt-002", bo.getStatementPortTrailId());
    Assertions.assertEquals("org.postgresql.Driver", bo.getDriverName());
    Assertions.assertEquals(snapshot, bo.getEntryPointSnapshot());
    Assertions.assertEquals(DbExecution.DB_EXECUTION_TYPE_DB_CONFIG, bo.getDbExecutionType());
  }

  @Test
  void testRoundTrip() {
    EntryPointSnapshot snapshot = EntryPointSnapshot.of(
            Arrays.asList(
                    EntryPoint.of(EntryPointType.SERVER_TOMCAT, "8080"),
                    EntryPoint.of(EntryPointType.REMOTE_JDBC, "queryUser")
            ), "roundtrip", "trace-3", 3L);
    DbConfigBo bo = new DbConfigBo();
    bo.setJdbcUrl("jdbc:h2:mem:test");
    bo.setUser("sa");
    bo.setPassword("");
    bo.setDataSourcePortTrailId("ds-003");
    bo.setConnectionPortTrailId("conn-003");
    bo.setStatementPortTrailId("stmt-003");
    bo.setDriverName("org.h2.Driver");
    bo.setEntryPointSnapshot(snapshot);

    DbConfigDto dto = new DbConfigDto(bo);
    DbConfigBo bo2 = new DbConfigBo(dto);

    Assertions.assertEquals(bo.getJdbcUrl(), bo2.getJdbcUrl());
    Assertions.assertEquals(bo.getUser(), bo2.getUser());
    Assertions.assertEquals(bo.getPassword(), bo2.getPassword());
    Assertions.assertEquals(bo.getDataSourcePortTrailId(), bo2.getDataSourcePortTrailId());
    Assertions.assertEquals(bo.getConnectionPortTrailId(), bo2.getConnectionPortTrailId());
    Assertions.assertEquals(bo.getStatementPortTrailId(), bo2.getStatementPortTrailId());
    Assertions.assertEquals(bo.getDriverName(), bo2.getDriverName());
    Assertions.assertEquals(bo.getEntryPointSnapshot(), bo2.getEntryPointSnapshot());
  }

}
