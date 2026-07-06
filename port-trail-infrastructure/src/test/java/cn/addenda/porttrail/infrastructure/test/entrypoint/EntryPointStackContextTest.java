package cn.addenda.porttrail.infrastructure.test.entrypoint;

import cn.addenda.porttrail.common.entrypoint.EntryPoint;
import cn.addenda.porttrail.common.entrypoint.EntryPointSnapshot;
import cn.addenda.porttrail.common.entrypoint.EntryPointType;
import cn.addenda.porttrail.infrastructure.entrypoint.EntryPointStackContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class EntryPointStackContextTest {

  @Test
  void test1() {
    Assertions.assertNotNull(EntryPointStackContext.snapshot());

    EntryPointStackContext.pushEntryPoint(EntryPoint.of(EntryPointType.SERVLET_JAVAX, "1"));
    Assertions.assertEquals(1, EntryPointStackContext.snapshot().getEntryPointList().size());

    EntryPointStackContext.pushEntryPoint(EntryPoint.of(EntryPointType.TX_TRANSACTIONAL, "2"));
    Assertions.assertEquals(2, EntryPointStackContext.snapshot().getEntryPointList().size());

    EntryPointStackContext.pushEntryPoint(EntryPoint.of(EntryPointType.ORM_MYBATIS, "3"));
    Assertions.assertEquals(3, EntryPointStackContext.snapshot().getEntryPointList().size());

    System.out.println(EntryPointStackContext.snapshot());

    System.out.println();

    EntryPointStackContext.popEntryPoint();
    Assertions.assertEquals(2, EntryPointStackContext.snapshot().getEntryPointList().size());

    EntryPointStackContext.popEntryPoint();
    Assertions.assertEquals(1, EntryPointStackContext.snapshot().getEntryPointList().size());

    EntryPointStackContext.popEntryPoint();
    Assertions.assertNotNull(EntryPointStackContext.snapshot());

  }

  @Test
  void test2() {

    EntryPointStackContext.pushEntryPoint(EntryPoint.of(EntryPointType.SERVLET_JAVAX, "1"));
    EntryPointSnapshot snapshot1 = EntryPointStackContext.snapshot();
    Assertions.assertNotNull(snapshot1.getTraceId());
    Assertions.assertEquals(Long.valueOf(0L), snapshot1.getSeqId());
    System.out.println(snapshot1);

    EntryPointStackContext.pushEntryPoint(EntryPoint.of(EntryPointType.TX_TRANSACTIONAL, "2"));
    EntryPointSnapshot snapshot2 = EntryPointStackContext.snapshot();
    Assertions.assertEquals(snapshot1.getTraceId(), snapshot2.getTraceId());
    Assertions.assertEquals(Long.valueOf(1L), snapshot2.getSeqId());
    System.out.println(snapshot2);

    EntryPointStackContext.pushEntryPoint(EntryPoint.of(EntryPointType.ORM_MYBATIS, "3"));
    EntryPointSnapshot snapshot3 = EntryPointStackContext.snapshot();
    Assertions.assertEquals(snapshot1.getTraceId(), snapshot3.getTraceId());
    Assertions.assertEquals(Long.valueOf(2L), snapshot3.getSeqId());
    System.out.println(snapshot3);


    EntryPointStackContext.popEntryPoint();
    EntryPointSnapshot snapshot4 = EntryPointStackContext.snapshot();
    Assertions.assertEquals(snapshot1.getTraceId(), snapshot4.getTraceId());
    Assertions.assertEquals(Long.valueOf(3L), snapshot4.getSeqId());
    System.out.println(snapshot4);

    EntryPointStackContext.popEntryPoint();
    EntryPointSnapshot snapshot5 = EntryPointStackContext.snapshot();
    Assertions.assertEquals(snapshot1.getTraceId(), snapshot5.getTraceId());
    Assertions.assertEquals(Long.valueOf(4L), snapshot5.getSeqId());
    System.out.println(snapshot5);

    EntryPointStackContext.popEntryPoint();
    EntryPointSnapshot snapshot6 = EntryPointStackContext.snapshot();
    Assertions.assertNotEquals(snapshot1.getTraceId(), snapshot6.getTraceId());
    Assertions.assertEquals(Long.valueOf(0L), snapshot6.getSeqId());
    System.out.println(snapshot6);

  }

}
