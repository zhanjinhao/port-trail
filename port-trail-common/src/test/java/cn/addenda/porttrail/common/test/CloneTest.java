package cn.addenda.porttrail.common.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CloneTest {

  @Test
  void testByteClone() {
    byte[] bytes = {-1};
    byte[] clone = bytes.clone();
    System.out.println(clone.length + ", " + clone[0]);
    Assertions.assertArrayEquals(bytes, clone);
  }

}
