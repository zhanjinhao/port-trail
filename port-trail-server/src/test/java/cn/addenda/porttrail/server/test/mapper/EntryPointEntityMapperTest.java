package cn.addenda.porttrail.server.test.mapper;

import cn.addenda.component.transaction.PlatformTransactionHelper;
import cn.addenda.porttrail.server.PortTrailServerApplication;
import cn.addenda.porttrail.server.entity.EntryPointEntity;
import cn.addenda.porttrail.server.mapper.EntryPointEntityMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = PortTrailServerApplication.class)
class EntryPointEntityMapperTest {

  @Autowired
  private EntryPointEntityMapper entryPointEntityMapper;

  @Autowired
  private PlatformTransactionHelper transactionHelper;

  @Test
  void test() {
    EntryPointEntity param = EntryPointEntity.ofParam();
    param.setEntryPointType("1");
    param.setDetail("2");
    param.setEntryId(3L);
    param.setEntryPointSnapshotId(4L);
    transactionHelper.doTransaction(() -> {
      entryPointEntityMapper.insert(param);
    });


    Assertions.assertNotNull(entryPointEntityMapper.queryById(param.getId()));
    Assertions.assertNotNull(entryPointEntityMapper.deleteById(param.getId()));
    Assertions.assertNull(entryPointEntityMapper.queryById(param.getId()));
  }


}
