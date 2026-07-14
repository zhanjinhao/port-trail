package cn.addenda.porttrail.common.test.util;

import cn.addenda.porttrail.common.exception.PortTrailException;
import cn.addenda.porttrail.common.util.CompressUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

class CompressUtilsTest {

  @Test
  void testRoundTrip() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 100; i++) {
      sb.append("hello port-trail! 你好，世界！");
    }
    byte[] original = sb.toString().getBytes(StandardCharsets.UTF_8);
    byte[] decompressed = CompressUtils.decompress(CompressUtils.compress(original));
    Assertions.assertArrayEquals(original, decompressed);
  }

  @Test
  void testEmptyContent() {
    // 原始数据为空时，compress 产生的是合法的空内容流，decompress 应正常返回空数组而非抛异常
    byte[] compressed = CompressUtils.compress(new byte[0]);
    byte[] decompressed = CompressUtils.decompress(compressed);
    Assertions.assertArrayEquals(new byte[0], decompressed);
  }

  @Test
  void testNull() {
    Assertions.assertNull(CompressUtils.compress(null));
    Assertions.assertNull(CompressUtils.decompress(null));
  }

  @Test
  void testTruncatedThrows() {
    byte[] compressed = CompressUtils.compress("some content that is long enough".getBytes(StandardCharsets.UTF_8));
    // 截断压缩数据，模拟数据不完整，应抛出 PortTrailException
    byte[] truncated = Arrays.copyOf(compressed, compressed.length / 2);
    Assertions.assertThrows(PortTrailException.class, () -> CompressUtils.decompress(truncated));
  }
}
