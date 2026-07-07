package cn.addenda.porttrail.server.test.mapper;

import cn.addenda.component.transaction.PlatformTransactionHelper;
import cn.addenda.porttrail.server.PortTrailServerApplication;
import cn.addenda.porttrail.server.entity.StatementSqlEntity;
import cn.addenda.porttrail.server.mapper.StatementSqlEntityMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = PortTrailServerApplication.class)
class StatementSqlEntityMapperTest {

  @Autowired
  private StatementSqlEntityMapper statementSqlEntityMapper;

  @Autowired
  private PlatformTransactionHelper transactionHelper;

  @Test
  void test() {
    StatementSqlEntity param = StatementSqlEntity.ofParam();
    param.setStatementExecutionId(1L);
    param.setSql("select 1");
    param.setOrderInStatement(1);
    param.setOrderInConnection(1);
    transactionHelper.doTransaction(() -> {
      statementSqlEntityMapper.insert(param);
    });


    Assertions.assertNotNull(statementSqlEntityMapper.queryById(param.getId()));
    Assertions.assertNotNull(statementSqlEntityMapper.deleteById(param.getId()));
    Assertions.assertNull(statementSqlEntityMapper.queryById(param.getId()));
  }


}
