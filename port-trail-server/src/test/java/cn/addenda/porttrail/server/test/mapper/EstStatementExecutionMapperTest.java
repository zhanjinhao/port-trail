package cn.addenda.porttrail.server.test.mapper;

import cn.addenda.component.transaction.PlatformTransactionHelper;
import cn.addenda.porttrail.server.PortTrailServerApplication;
import cn.addenda.porttrail.server.entity.EstStatementExecution;
import cn.addenda.porttrail.server.mapper.EstStatementExecutionMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest(classes = PortTrailServerApplication.class)
class EstStatementExecutionMapperTest {

  @Autowired
  private EstStatementExecutionMapper estStatementExecutionMapper;

  @Autowired
  private PlatformTransactionHelper transactionHelper;

  @Test
  void test() {
    EstStatementExecution param = EstStatementExecution.ofParam();
    param.setSystemCode("1");
    param.setServiceName("2");
    param.setImageName("3");
    param.setEnv("4");
    param.setInstanceId("5");
    param.setDataSourcePortTrailId("6");
    param.setConnectionPortTrailId("7");
    param.setStatementPortTrailId("8");
    param.setStatementState("9");
    param.setTxId("10");
    param.setStart(LocalDateTime.now());
    param.setEnd(LocalDateTime.now());
    param.setCost(0);
    param.setEntryPointSnapshotId(13L);
    transactionHelper.doTransaction(() -> {
      estStatementExecutionMapper.insert(param);
    });


    Assertions.assertEquals(1, estStatementExecutionMapper.countByEntity(EstStatementExecution.ofParam()));
    Assertions.assertEquals(1, estStatementExecutionMapper.deleteByEntity(EstStatementExecution.ofParam()));
    Assertions.assertEquals(0, estStatementExecutionMapper.countByEntity(EstStatementExecution.ofParam()));
  }


}
