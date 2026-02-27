package cn.addenda.porttrail.server.test.mapper;

import cn.addenda.component.transaction.PlatformTransactionHelper;
import cn.addenda.porttrail.server.PortTrailServerApplication;
import cn.addenda.porttrail.server.entity.EstDbConfig;
import cn.addenda.porttrail.server.mapper.EstDbConfigMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest(classes = PortTrailServerApplication.class)
class EstDbConfigMapperTest {

  @Autowired
  private EstDbConfigMapper estDbConfigMapper;

  @Autowired
  private PlatformTransactionHelper transactionHelper;

  @Test
  void test() {
    EstDbConfig param = EstDbConfig.ofParam();
    param.setSystemCode("1");
    param.setServiceName("2");
    param.setImageName("3");
    param.setEnv("4");
    param.setInstanceId("5");
    param.setDataSourcePortTrailId("6");
    param.setConnectionPortTrailId(UUID.randomUUID().toString());
    param.setStatementPortTrailId("8");
    param.setJdbcUrl("9");
    param.setUser("10");
    param.setPassword("11");
    param.setDriverName("12");
    param.setEntryPointSnapshotId(13L);
    transactionHelper.doTransaction(() -> {
      estDbConfigMapper.insert(param);
    });


    Assertions.assertEquals(1, estDbConfigMapper.countByEntity(EstDbConfig.ofParam()));
    Assertions.assertEquals(1, estDbConfigMapper.deleteByEntity(EstDbConfig.ofParam()));
    Assertions.assertEquals(0, estDbConfigMapper.countByEntity(EstDbConfig.ofParam()));
  }

}
