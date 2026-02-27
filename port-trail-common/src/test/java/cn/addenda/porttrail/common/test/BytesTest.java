package cn.addenda.porttrail.common.test;

import cn.addenda.porttrail.common.pojo.db.bo.PreparedStatementParameter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class BytesTest {

  @Test
  void test1() {
    byte[] unSupportedClass = PreparedStatementParameter.UN_SUPPORTED_PARAMETER;
    boolean equals = Arrays.equals(new byte[]{-1}, unSupportedClass);
    Assertions.assertTrue(equals);
  }

}
