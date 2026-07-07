package cn.addenda.porttrail.server.test.mapper;

import cn.addenda.component.transaction.PlatformTransactionHelper;
import cn.addenda.porttrail.server.PortTrailServerApplication;
import cn.addenda.porttrail.server.entity.PreparedStatementParameterEntity;
import cn.addenda.porttrail.server.mapper.PreparedStatementParameterEntityMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = PortTrailServerApplication.class)
class PreparedStatementParameterEntityMapperTest {

  @Autowired
  private PreparedStatementParameterEntityMapper preparedStatementParameterEntityMapper;

  @Autowired
  private PlatformTransactionHelper transactionHelper;

  @Test
  void test() {
    PreparedStatementParameterEntity param = PreparedStatementParameterEntity.ofParam();
    param.setPreparedStatementExecutionId(1L);
    param.setParameterJson("1");
    param.setCapacity(1);
    param.setOrderInStatement(1);
    param.setOrderInConnection(1);
    transactionHelper.doTransaction(() -> {
      preparedStatementParameterEntityMapper.insert(param);
    });


    Assertions.assertNotNull(preparedStatementParameterEntityMapper.queryById(param.getId()));
    Assertions.assertNotNull(preparedStatementParameterEntityMapper.deleteById(param.getId()));
    Assertions.assertNull(preparedStatementParameterEntityMapper.queryById(param.getId()));
  }


}
