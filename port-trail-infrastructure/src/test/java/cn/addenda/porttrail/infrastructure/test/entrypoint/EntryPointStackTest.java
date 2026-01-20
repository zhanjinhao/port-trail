package cn.addenda.porttrail.infrastructure.test.entrypoint;

import cn.addenda.porttrail.common.entrypoint.EntryPoint;
import cn.addenda.porttrail.common.entrypoint.EntryPointType;
import cn.addenda.porttrail.infrastructure.entrypoint.EntryPointStack;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class EntryPointStackTest {

  @Test
  void test1() {
    EntryPointStack entryPointStack = new EntryPointStack();
    System.out.println(entryPointStack);
    Assertions.assertEquals(0, entryPointStack.size());

    entryPointStack.push(EntryPoint.of(EntryPointType.SERVLET_JAVAX, "1"));
    System.out.println(entryPointStack);
    Assertions.assertEquals(1, entryPointStack.size());

    entryPointStack.push(EntryPoint.of(EntryPointType.ORM_MYBATIS, "2"));
    System.out.println(entryPointStack);
    Assertions.assertEquals(2, entryPointStack.size());

    entryPointStack.pop();
    System.out.println(entryPointStack);
    Assertions.assertEquals(1, entryPointStack.size());

    entryPointStack.pop();
    System.out.println(entryPointStack);
    Assertions.assertEquals(0, entryPointStack.size());
  }

}
