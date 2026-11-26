package cn.addenda.porttrail.server.test.helper;

import cn.addenda.porttrail.server.helper.AnalyzeDedupHandlerKeySupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

class AnalyzeDedupHandlerKeySupportTest {

  private static final String STAT_PERIOD = "2026-11-21";

  private static final String SQL = "select * from t_order where id = ?";

  /**
   * detail 按调用链顺序排列，顺序不同代表不同的调用链，必须记为两条数据。
   */
  @Test
  void testDetailOrderAffectsKey() {
    String key1 = AnalyzeDedupHandlerKeySupport.computeDedupKey(
            STAT_PERIOD, "SYS", "svc", "prod", Arrays.asList("/a", "/b", "/c"), SQL);
    String key2 = AnalyzeDedupHandlerKeySupport.computeDedupKey(
            STAT_PERIOD, "SYS", "svc", "prod", Arrays.asList("/c", "/a", "/b"), SQL);
    Assertions.assertNotEquals(key1, key2);
  }

  /**
   * 同一个 entry point 在调用链里出现多次时，重复次数是区分上下文的信息，必须记为两条数据。
   */
  @Test
  void testDuplicateDetailAffectsKey() {
    String key1 = AnalyzeDedupHandlerKeySupport.computeDedupKey(
            STAT_PERIOD, "SYS", "svc", "prod", Arrays.asList("/a", "/b"), SQL);
    String key2 = AnalyzeDedupHandlerKeySupport.computeDedupKey(
            STAT_PERIOD, "SYS", "svc", "prod", Arrays.asList("/a", "/b", "/a"), SQL);
    Assertions.assertNotEquals(key1, key2);
  }

  @Test
  void testDuplicateCountProducesDifferentKey() {
    String twice = AnalyzeDedupHandlerKeySupport.computeDedupKey(
            STAT_PERIOD, "SYS", "svc", "prod", Arrays.asList("/a", "/a", "/b"), SQL);
    String threeTimes = AnalyzeDedupHandlerKeySupport.computeDedupKey(
            STAT_PERIOD, "SYS", "svc", "prod", Arrays.asList("/a", "/a", "/a", "/b"), SQL);
    Assertions.assertNotEquals(twice, threeTimes);
  }

  @Test
  void testDifferentInputProducesDifferentKey() {
    String base = AnalyzeDedupHandlerKeySupport.computeDedupKey(
            STAT_PERIOD, "SYS", "svc", "prod", Collections.singletonList("/a"), SQL);

    Assertions.assertNotEquals(base, AnalyzeDedupHandlerKeySupport.computeDedupKey(
            STAT_PERIOD, "SYS", "svc", "prod", Collections.singletonList("/b"), SQL));
    Assertions.assertNotEquals(base, AnalyzeDedupHandlerKeySupport.computeDedupKey(
            STAT_PERIOD, "SYS", "svc", "prod", Collections.singletonList("/a"), SQL + " "));
    Assertions.assertNotEquals(base, AnalyzeDedupHandlerKeySupport.computeDedupKey(
            STAT_PERIOD, "SYS2", "svc", "prod", Collections.singletonList("/a"), SQL));
    Assertions.assertNotEquals(base, AnalyzeDedupHandlerKeySupport.computeDedupKey(
            STAT_PERIOD, "SYS", "svc2", "prod", Collections.singletonList("/a"), SQL));
    Assertions.assertNotEquals(base, AnalyzeDedupHandlerKeySupport.computeDedupKey(
            STAT_PERIOD, "SYS", "svc", "sit", Collections.singletonList("/a"), SQL));
    Assertions.assertNotEquals(base, AnalyzeDedupHandlerKeySupport.computeDedupKey(
            "2026-11-22", "SYS", "svc", "prod", Collections.singletonList("/a"), SQL));
  }

  @Test
  void testNullSafe() {
    String key = AnalyzeDedupHandlerKeySupport.computeDedupKey(null, null, null, null, null, null);
    Assertions.assertEquals(64, key.length());
    Assertions.assertEquals("", AnalyzeDedupHandlerKeySupport.joinDetails(null));
    Assertions.assertEquals("", AnalyzeDedupHandlerKeySupport.joinDetails(Collections.emptyList()));
    // null 元素跳过，但空字符串是合法的 detail，必须保留（含它两侧的分隔符）
    Assertions.assertEquals("b", AnalyzeDedupHandlerKeySupport.joinDetails(Arrays.asList(null, "b")));
    Assertions.assertEquals(",b", AnalyzeDedupHandlerKeySupport.joinDetails(Arrays.asList("", "b")));
  }

  @Test
  void testHashFormat() {
    String dedupKey = AnalyzeDedupHandlerKeySupport.computeDedupKey(
            STAT_PERIOD, "SYS", "svc", "prod", Collections.singletonList("/a"), SQL);
    String sqlHash = AnalyzeDedupHandlerKeySupport.computeSqlHash(SQL);

    Assertions.assertTrue(dedupKey.matches("[0-9a-f]{64}"));
    Assertions.assertTrue(sqlHash.matches("[0-9a-f]{64}"));
    // 相同 SQL 的 sqlHash 稳定，且与日期、环境无关
    Assertions.assertEquals(sqlHash, AnalyzeDedupHandlerKeySupport.computeSqlHash(SQL));
    Assertions.assertNotEquals(dedupKey, sqlHash);
  }

  @Test
  void testJoinDetailsPreservesOrderAndDuplicate() {
    Assertions.assertEquals("/c,/a,/b,/a",
            AnalyzeDedupHandlerKeySupport.joinDetails(Arrays.asList("/c", "/a", "/b", "/a")));
  }

}
