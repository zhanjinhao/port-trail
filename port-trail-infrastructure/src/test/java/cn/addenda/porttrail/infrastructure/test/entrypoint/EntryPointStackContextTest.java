package cn.addenda.porttrail.infrastructure.test.entrypoint;

import cn.addenda.porttrail.common.entrypoint.EntryPoint;
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
    System.out.println(EntryPointStackContext.snapshot());

    EntryPointStackContext.pushEntryPoint(EntryPoint.of(EntryPointType.TX_TRANSACTIONAL, "2"));
    System.out.println(EntryPointStackContext.snapshot());

    EntryPointStackContext.pushEntryPoint(EntryPoint.of(EntryPointType.ORM_MYBATIS, "3"));
    System.out.println(EntryPointStackContext.snapshot());


    EntryPointStackContext.popEntryPoint();
    System.out.println(EntryPointStackContext.snapshot());

    EntryPointStackContext.popEntryPoint();
    System.out.println(EntryPointStackContext.snapshot());

    EntryPointStackContext.popEntryPoint();
    System.out.println(EntryPointStackContext.snapshot());

  }

}
