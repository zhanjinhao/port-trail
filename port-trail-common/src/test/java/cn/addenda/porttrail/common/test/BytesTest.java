package cn.addenda.porttrail.common.test;

import cn.addenda.porttrail.common.pojo.db.PreparedStatementParameterWrapper;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class BytesTest {

  @Test
  void test1() {
    byte[] unSupportedClass = PreparedStatementParameterWrapper.UN_SUPPORTED_PARAMETER;
    boolean equals = Arrays.equals(new byte[]{-1}, unSupportedClass);
    System.out.println(equals);
  }

}
