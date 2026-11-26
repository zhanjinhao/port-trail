package cn.addenda.porttrail.server.test.mapper;

import cn.addenda.component.transaction.PlatformTransactionHelper;
import cn.addenda.porttrail.server.PortTrailServerApplication;
import cn.addenda.porttrail.server.entity.AnalyzeDedupResultEntity;
import cn.addenda.porttrail.server.mapper.AnalyzeDedupResultEntityMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;

import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest(classes = PortTrailServerApplication.class)
class AnalyzeDedupResultEntityMapperTest {

  @Autowired
  private AnalyzeDedupResultEntityMapper analyzeDedupResultEntityMapper;

  @Autowired
  private PlatformTransactionHelper transactionHelper;

  @Test
  void test() {
    AnalyzeDedupResultEntity param = newEntity();

    transactionHelper.doTransaction(() -> {
      analyzeDedupResultEntityMapper.insert(param);
    });

    AnalyzeDedupResultEntity queried = analyzeDedupResultEntityMapper.queryById(param.getId());
    Assertions.assertNotNull(queried);
    Assertions.assertEquals(param.getDedupKey(), queried.getDedupKey());
    Assertions.assertEquals(param.getStatPeriod(), queried.getStatPeriod());
    Assertions.assertEquals("SELECT_ALL", queried.getHitRules());

    // 唯一索引 uk_dedup_key 兜底幂等
    AnalyzeDedupResultEntity duplicate = newEntity();
    duplicate.setDedupKey(param.getDedupKey());
    Assertions.assertThrows(DuplicateKeyException.class,
            () -> transactionHelper.doTransaction(() -> {
              analyzeDedupResultEntityMapper.insert(duplicate);
            }));

    // 合并：新增 cost 规则；select_all 的首次证据保持不变
    AnalyzeDedupResultEntity merge = newEntity();
    merge.setDedupKey(param.getDedupKey());
    merge.setSelectAllOuterId(999L);
    merge.setIfCostTooHigh(1);
    merge.setCostTooHighOuterId(888L);
    merge.setCostTooHighCost(321);
    Assertions.assertEquals(1, merge(merge));

    queried = analyzeDedupResultEntityMapper.queryById(param.getId());
    Assertions.assertEquals(1, queried.getIfSelectAll());
    Assertions.assertEquals(param.getSelectAllOuterId(), queried.getSelectAllOuterId());
    Assertions.assertEquals(1, queried.getIfCostTooHigh());
    Assertions.assertEquals(888L, queried.getCostTooHighOuterId());
    Assertions.assertEquals(321, queried.getCostTooHighCost());
    Assertions.assertEquals("SELECT_ALL,COST_TOO_HIGH", queried.getHitRules());

    // 再次命中 cost：flag 不变，首次的 cost 证据保留
    AnalyzeDedupResultEntity mergeAgain = newEntity();
    mergeAgain.setDedupKey(param.getDedupKey());
    mergeAgain.setIfCostTooHigh(1);
    mergeAgain.setCostTooHighOuterId(777L);
    mergeAgain.setCostTooHighCost(555);
    Assertions.assertEquals(1, merge(mergeAgain));

    queried = analyzeDedupResultEntityMapper.queryById(param.getId());
    Assertions.assertEquals(888L, queried.getCostTooHighOuterId());
    Assertions.assertEquals(321, queried.getCostTooHighCost());
    Assertions.assertEquals("SELECT_ALL,COST_TOO_HIGH", queried.getHitRules());

    Assertions.assertNotNull(analyzeDedupResultEntityMapper.deleteById(param.getId()));
    Assertions.assertNull(analyzeDedupResultEntityMapper.queryById(param.getId()));
  }

  private int merge(AnalyzeDedupResultEntity entity) {
    AtomicInteger updated = new AtomicInteger();
    transactionHelper.doTransaction(() -> updated.set(analyzeDedupResultEntityMapper.mergeByDedupKey(entity)));
    return updated.get();
  }

  private AnalyzeDedupResultEntity newEntity() {
    AnalyzeDedupResultEntity entity = AnalyzeDedupResultEntity.ofParam();
    entity.setDedupKey((UUID.randomUUID().toString() + UUID.randomUUID()).replace("-", ""));
    entity.setStatPeriod(LocalDate.now().toString());
    entity.setSource("PREPARED_STATEMENT_EXECUTION");
    entity.setSystemCode("SYS");
    entity.setServiceName("svc");
    entity.setEnv("sit");
    entity.setImageName("image");
    entity.setInstanceId("instance");
    entity.setEntryPointDetails("/a,/b");
    entity.setSqlType("SELECT");
    entity.setParameterizedSql("select * from t_order where id = ?");
    entity.setIfSelectAll(1);
    entity.setSelectAllOuterId(1L);
    entity.setIfFullScan(0);
    entity.setIfInTooMany(0);
    entity.setIfCostTooHigh(0);
    entity.setHitRules("SELECT_ALL");
    return entity;
  }

}
