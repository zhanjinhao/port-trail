package cn.addenda.porttrail.common.test.util;

import cn.addenda.porttrail.common.util.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class StringUtilsTest {

  @Test
  void testBiTrimSpecifiedChar() {
    Assertions.assertEquals("", StringUtils.biTrimSpecifiedChar("aaa", 'a'));
  }

}
