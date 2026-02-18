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
    transactionHelper.doTransaction(() -> {
      estEntryPointSnapshotMapper.insert(param);
    });


    Assertions.assertEquals(1, estEntryPointSnapshotMapper.countByEntity(EstEntryPointSnapshot.ofParam()));
    Assertions.assertEquals(1, estEntryPointSnapshotMapper.deleteByEntity(EstEntryPointSnapshot.ofParam()));
    Assertions.assertEquals(0, estEntryPointSnapshotMapper.countByEntity(EstEntryPointSnapshot.ofParam()));
  }


}
