package cn.addenda.porttrail.server.test.mapper;

import cn.addenda.component.transaction.PlatformTransactionHelper;
import cn.addenda.porttrail.server.PortTrailServerApplication;
import cn.addenda.porttrail.server.entity.EstPreparedStatementParameter;
import cn.addenda.porttrail.server.mapper.EstPreparedStatementParameterMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = PortTrailServerApplication.class)
class EstPreparedStatementParameterMapperTest {

  @Autowired
  private EstPreparedStatementParameterMapper estPreparedStatementParameterMapper;

  @Autowired
  private PlatformTransactionHelper transactionHelper;

  @Test
  void test() {
    EstPreparedStatementParameter param = EstPreparedStatementParameter.ofParam();
    param.setPreparedStatementExecutionId(1L);
    param.setParameterJson("2");
    param.setParameterBytes(new byte[]{3});
    param.setCapacity(4);
    param.setOrderInStatement(5);
    param.setOrderInConnection(6);
    transactionHelper.doTransaction(() -> {
      estPreparedStatementParameterMapper.insert(param);
    });


    Assertions.assertEquals(1, estPreparedStatementParameterMapper.countByEntity(EstPreparedStatementParameter.ofParam()));
    Assertions.assertEquals(1, estPreparedStatementParameterMapper.deleteByEntity(EstPreparedStatementParameter.ofParam()));
    Assertions.assertEquals(0, estPreparedStatementParameterMapper.countByEntity(EstPreparedStatementParameter.ofParam()));
  }


}
