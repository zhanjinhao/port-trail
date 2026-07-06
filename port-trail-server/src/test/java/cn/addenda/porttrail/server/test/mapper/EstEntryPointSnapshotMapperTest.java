package cn.addenda.porttrail.server.test.mapper;

import cn.addenda.component.transaction.PlatformTransactionHelper;
import cn.addenda.porttrail.server.PortTrailServerApplication;
import cn.addenda.porttrail.server.entity.EstEntryPointSnapshot;
import cn.addenda.porttrail.server.mapper.EstEntryPointSnapshotMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = PortTrailServerApplication.class)
class EstEntryPointSnapshotMapperTest {

  @Autowired
  private EstEntryPointSnapshotMapper estEntryPointSnapshotMapper;

  @Autowired
  private PlatformTransactionHelper transactionHelper;

  @Test
  void test() {
    EstEntryPointSnapshot param = EstEntryPointSnapshot.ofParam();
    param.setThreadName("1");
    param.setTraceId("test-trace-id-001");
    param.setSeqId(0L);
    transactionHelper.doTransaction(() -> {
      estEntryPointSnapshotMapper.insert(param);
    });

    EstEntryPointSnapshot queryResult = estEntryPointSnapshotMapper.queryById(param.getId());
    Assertions.assertNotNull(queryResult);
    Assertions.assertEquals("test-trace-id-001", queryResult.getTraceId());
    Assertions.assertEquals(Long.valueOf(0L), queryResult.getSeqId());

    Assertions.assertNotNull(estEntryPointSnapshotMapper.queryById(queryResult.getId()));
  }


}
