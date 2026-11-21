package cn.addenda.porttrail.server.test.helper;

import cn.addenda.porttrail.server.helper.AnalyzeDedupHandlerKeySupport;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntConsumer;

/**
 * {@link AnalyzeDedupHandlerKeySupport} 的手工微基准。
 *
 * <p>不是 JUnit 语义上的测试（没有断言），因此类名不带 {@code Test} 后缀，不参与常规
 * {@code mvn test}；需要单独执行：
 * <pre>
 * mvn -o -pl port-trail-server test -Dtest=AnalyzeDedupHandlerKeySupportBenchmark -Dsurefire.useFile=false
 * </pre>
 *
 * <p>方法说明：每个场景先预热，再跑多轮取中位数；用 volatile 写把结果「带出去」，
 * 防止 JIT 把计算整个消除。
 */
class AnalyzeDedupHandlerKeySupportBenchmark {

  private static final int ROUNDS = 5;

  private static final int WARMUP_OPS = 50_000;

  private static final String SHORT_SQL = "select id, name from t_order where id = ? and status = ?";

  private static final String MEDIUM_SQL = buildSql(50);

  private static final String LARGE_SQL = buildSql(300);

  private static final List<String> DETAILS_3 = Arrays.asList(
          "/api/order/create", "/api/order/pay", "/api/order/refund");

  private static final List<String> DETAILS_10 = buildDetails(10);

  private static final LocalDate STAT_DATE = LocalDate.of(2026, 11, 21);

  private static final byte[] DIGEST_BYTES = new byte[32];

  private static final char[] HEX = "0123456789abcdef".toCharArray();

  /**
   * 防止被测代码被 JIT 判定为无用代码。
   */
  private static volatile long sink;

  @Test
  void benchmark() throws Exception {
    System.out.println("=== AnalyzeDedupHandlerKeySupport benchmark ===");
    System.out.println("java.version = " + System.getProperty("java.version")
            + ", os.arch = " + System.getProperty("os.arch")
            + ", processors = " + Runtime.getRuntime().availableProcessors());
    System.out.println("shortSql = " + SHORT_SQL.length() + " chars, mediumSql = "
            + MEDIUM_SQL.length() + " chars, largeSql = " + LARGE_SQL.length() + " chars");
    System.out.printf("%-46s %12s %16s %14s%n", "scenario", "median ns/op", "ops/sec", "min ns/op");

    // 输入准备与预热
    measure("joinDetails(details=3)", 200_000,
            i -> sink = AnalyzeDedupHandlerKeySupport.joinDetails(DETAILS_3).length());
    measure("joinDetails(details=10)", 200_000,
            i -> sink = AnalyzeDedupHandlerKeySupport.joinDetails(DETAILS_10).length());

    measure("computeSqlHash(short)", 200_000,
            i -> sink = AnalyzeDedupHandlerKeySupport.computeSqlHash(SHORT_SQL).charAt(0));
    measure("computeSqlHash(medium 3KB)", 50_000,
            i -> sink = AnalyzeDedupHandlerKeySupport.computeSqlHash(MEDIUM_SQL).charAt(0));
    measure("computeSqlHash(large 20KB)", 10_000,
            i -> sink = AnalyzeDedupHandlerKeySupport.computeSqlHash(LARGE_SQL).charAt(0));

    measure("computeDedupKey(details=3, short)", 200_000,
            i -> sink = AnalyzeDedupHandlerKeySupport.computeDedupKey(
                    STAT_DATE, "SYS", "svc", "prod", DETAILS_3, SHORT_SQL).charAt(0));
    measure("computeDedupKey(details=10, medium)", 50_000,
            i -> sink = AnalyzeDedupHandlerKeySupport.computeDedupKey(
                    STAT_DATE, "SYS", "svc", "prod", DETAILS_10, MEDIUM_SQL).charAt(0));

    // 成本拆解
    measure("MessageDigest.getInstance(SHA-256)", 200_000,
            i -> sink = getInstance());
    measure("digest(short) with reused instance", 200_000,
            i -> digestReused());
    measure("getBytes(short, UTF-8)", 500_000,
            i -> sink = SHORT_SQL.getBytes(StandardCharsets.UTF_8)[0]);
    measure("bytes -> 64 hex chars", 500_000,
            i -> sink = toHex(DIGEST_BYTES).charAt(0));

    System.out.println("=== done, sink=" + sink + " ===");
  }

  private static void measure(String name, int ops, IntConsumer action) {
    // 预热：慢场景（大 SQL）也需要足够的预热次数，否则 JIT 未编译完就开始测量
    int warmupOps = Math.min(200_000, Math.max(WARMUP_OPS, ops));
    for (int i = 0; i < warmupOps; i++) {
      action.accept(i);
    }
    // 多轮测量
    double[] roundNsOps = new double[ROUNDS];
    for (int r = 0; r < ROUNDS; r++) {
      long start = System.nanoTime();
      for (int i = 0; i < ops; i++) {
        action.accept(i);
      }
      long elapsed = System.nanoTime() - start;
      roundNsOps[r] = (double) elapsed / ops;
    }
    Arrays.sort(roundNsOps);
    double median = roundNsOps[ROUNDS / 2];
    double min = roundNsOps[0];
    System.out.printf("%-46s %12.2f %16.0f %14.2f%n", name, median, 1_000_000_000d / median, min);
  }

  private static long getInstance() {
    try {
      return MessageDigest.getInstance("SHA-256").getDigestLength();
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  private static void digestReused() {
    try {
      MessageDigest messageDigest = REUSED.get();
      sink = messageDigest.digest(SHORT_SQL.getBytes(StandardCharsets.UTF_8))[0];
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  private static final ThreadLocal<MessageDigest> REUSED = ThreadLocal.withInitial(() -> {
    try {
      return MessageDigest.getInstance("SHA-256");
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  });

  private static String toHex(byte[] digest) {
    char[] chars = new char[digest.length * 2];
    for (int i = 0; i < digest.length; i++) {
      int b = digest[i] & 0xFF;
      chars[i * 2] = HEX[b >>> 4];
      chars[i * 2 + 1] = HEX[b & 0x0F];
    }
    return new String(chars);
  }

  private static String buildSql(int predicateCount) {
    StringBuilder builder = new StringBuilder("select id, name from t_order where 1 = 1");
    for (int i = 0; i < predicateCount; i++) {
      builder.append(" and col_").append(i).append(" = ?");
    }
    return builder.toString();
  }

  private static List<String> buildDetails(int count) {
    List<String> details = new ArrayList<>(count);
    for (int i = 0; i < count; i++) {
      details.add("/api/resource/" + i + "/action");
    }
    return details;
  }

}
