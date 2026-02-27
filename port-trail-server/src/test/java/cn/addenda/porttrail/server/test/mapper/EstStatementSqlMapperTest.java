package cn.addenda.porttrail.server.test.mapper;

import cn.addenda.component.transaction.PlatformTransactionHelper;
import cn.addenda.porttrail.server.PortTrailServerApplication;
import cn.addenda.porttrail.server.entity.EstStatementSql;
import cn.addenda.porttrail.server.mapper.EstStatementSqlMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = PortTrailServerApplication.class)
class EstStatementSqlMapperTest {

  @Autowired
  private EstStatementSqlMapper estStatementSqlMapper;

  @Autowired
  private PlatformTransactionHelper transactionHelper;

  @Test
  void test() {
    EstStatementSql param = EstStatementSql.ofParam();
    param.setStatementExecutionId(1L);
    param.setSql("2");
    param.setOrderInStatement(3);
    param.setOrderInConnection(4);
    transactionHelper.doTransaction(() -> {
      estStatementSqlMapper.insert(param);
    });

    Assertions.assertEquals(1, estStatementSqlMapper.countByEntity(EstStatementSql.ofParam()));
    Assertions.assertEquals(1, estStatementSqlMapper.deleteByEntity(EstStatementSql.ofParam()));
    Assertions.assertEquals(0, estStatementSqlMapper.countByEntity(EstStatementSql.ofParam()));
  }

}
