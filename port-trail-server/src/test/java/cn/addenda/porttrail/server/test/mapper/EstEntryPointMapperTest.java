package cn.addenda.porttrail.server.test.mapper;

import cn.addenda.component.transaction.PlatformTransactionHelper;
import cn.addenda.porttrail.server.PortTrailServerApplication;
import cn.addenda.porttrail.server.entity.EstEntryPoint;
import cn.addenda.porttrail.server.mapper.EstEntryPointMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = PortTrailServerApplication.class)
class EstEntryPointMapperTest {

  @Autowired
  private EstEntryPointMapper estEntryPointMapper;

  @Autowired
  private PlatformTransactionHelper transactionHelper;

  @Test
  void test() {
    EstEntryPoint param = EstEntryPoint.ofParam();
    param.setEntryPointType("1");
    param.setDetail("2");
    param.setEntryId(3L);
    param.setEntryPointSnapshotId(4L);
    transactionHelper.doTransaction(() -> {
      estEntryPointMapper.insert(param);
    });


    Assertions.assertEquals(1, estEntryPointMapper.countByEntity(EstEntryPoint.ofParam()));
    Assertions.assertEquals(1, estEntryPointMapper.deleteByEntity(EstEntryPoint.ofParam()));
    Assertions.assertEquals(0, estEntryPointMapper.countByEntity(EstEntryPoint.ofParam()));
  }


}
