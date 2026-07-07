package cn.addenda.porttrail.server.test.mapper;

import cn.addenda.component.transaction.PlatformTransactionHelper;
import cn.addenda.porttrail.server.PortTrailServerApplication;
import cn.addenda.porttrail.server.entity.EntryPointSnapshotEntity;
import cn.addenda.porttrail.server.mapper.EntryPointSnapshotEntityMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = PortTrailServerApplication.class)
class EntryPointSnapshotEntityMapperTest {

  @Autowired
  private EntryPointSnapshotEntityMapper entryPointSnapshotEntityMapper;

  @Autowired
  private PlatformTransactionHelper transactionHelper;

  @Test
  void test() {
    EntryPointSnapshotEntity param = EntryPointSnapshotEntity.ofParam();
    param.setThreadName("1");
    param.setTraceId("test-trace-id-001");
    param.setSeqId(0L);
    transactionHelper.doTransaction(() -> {
      entryPointSnapshotEntityMapper.insert(param);
    });

    EntryPointSnapshotEntity queryResult = entryPointSnapshotEntityMapper.queryById(param.getId());
    Assertions.assertNotNull(queryResult);
    Assertions.assertEquals("test-trace-id-001", queryResult.getTraceId());
    Assertions.assertEquals(Long.valueOf(0L), queryResult.getSeqId());

    Assertions.assertNotNull(entryPointSnapshotEntityMapper.queryById(queryResult.getId()));
  }


}
