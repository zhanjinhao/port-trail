package cn.addenda.porttrail.server.test.mapper;

import cn.addenda.component.transaction.PlatformTransactionHelper;
import cn.addenda.porttrail.server.PortTrailServerApplication;
import cn.addenda.porttrail.server.entity.PreparedStatementExecutionEntity;
import cn.addenda.porttrail.server.mapper.PreparedStatementExecutionEntityMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest(classes = PortTrailServerApplication.class)
class PreparedStatementExecutionEntityMapperTest {

  @Autowired
  private PreparedStatementExecutionEntityMapper preparedStatementExecutionEntityMapper;

  @Autowired
  private PlatformTransactionHelper transactionHelper;

  @Test
  void test() {
    PreparedStatementExecutionEntity param = PreparedStatementExecutionEntity.ofParam();
    param.setSystemCode("1");
    param.setServiceName("2");
    param.setImageName("3");
    param.setEnv("4");
    param.setInstanceId("5");
    param.setDataSourcePortTrailId("6");
    param.setConnectionPortTrailId("7");
    param.setStatementPortTrailId("8");
    param.setParameterizedSql("9");
    param.setStatementState("10");
    param.setTxId("11");
    param.setStart(LocalDateTime.now());
    param.setEnd(LocalDateTime.now());
    param.setCost(0);
    param.setEntryPointSnapshotId(13L);
    transactionHelper.doTransaction(() -> {
      preparedStatementExecutionEntityMapper.insert(param);
    });


    Assertions.assertNotNull(preparedStatementExecutionEntityMapper.queryById(param.getId()));
    Assertions.assertNotNull(preparedStatementExecutionEntityMapper.deleteById(param.getId()));
    Assertions.assertNull(preparedStatementExecutionEntityMapper.queryById(param.getId()));
  }


}
