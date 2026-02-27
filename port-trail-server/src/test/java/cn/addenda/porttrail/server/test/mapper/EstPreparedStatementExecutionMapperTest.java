package cn.addenda.porttrail.server.test.mapper;

import cn.addenda.component.transaction.PlatformTransactionHelper;
import cn.addenda.porttrail.server.PortTrailServerApplication;
import cn.addenda.porttrail.server.entity.EstPreparedStatementExecution;
import cn.addenda.porttrail.server.mapper.EstPreparedStatementExecutionMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest(classes = PortTrailServerApplication.class)
class EstPreparedStatementExecutionMapperTest {

  @Autowired
  private EstPreparedStatementExecutionMapper estPreparedStatementExecutionMapper;

  @Autowired
  private PlatformTransactionHelper transactionHelper;

  @Test
  void test() {
    EstPreparedStatementExecution param = EstPreparedStatementExecution.ofParam();
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
      estPreparedStatementExecutionMapper.insert(param);
    });


    Assertions.assertEquals(1, estPreparedStatementExecutionMapper.countByEntity(EstPreparedStatementExecution.ofParam()));
    Assertions.assertEquals(1, estPreparedStatementExecutionMapper.deleteByEntity(EstPreparedStatementExecution.ofParam()));
    Assertions.assertEquals(0, estPreparedStatementExecutionMapper.countByEntity(EstPreparedStatementExecution.ofParam()));
  }


}
