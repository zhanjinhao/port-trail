package cn.addenda.porttrail.server.test.util;

import cn.addenda.porttrail.server.helper.SqlHelper;
import cn.addenda.porttrail.server.helper.SqlParseResult;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * SqlHelper 单元测试，覆盖 select * 与 in 两种解析场景。
 *
 * <p>线程池配置与 {@code SqlHelperConfig} 保持一致：JSqlParser 的 parse 会提交任务到该线程池，
 * 池不可用会直接导致解析失败。
 */
class SqlHelperTest {

  private static ExecutorService executorService;

  private static SqlHelper sqlHelper;

  @BeforeAll
  static void beforeAll() {
    executorService = new ThreadPoolExecutor(
            10,
            10,
            10000,
            TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(10),
            new SqlHelperThreadFactory(),
            new ThreadPoolExecutor.CallerRunsPolicy());

    sqlHelper = new SqlHelper(executorService);
  }

  @AfterAll
  static void afterAll() {
    executorService.shutdown();
  }

  // ------------------------------------------------------------ select * ------------------------------------------------------------

  @Test
  void testSelectAll() {
    Assertions.assertTrue(sqlHelper.checkIfHasSelectAll("SELECT * FROM t_order"));
  }

  @Test
  void testSelectAllWithAlias() {
    Assertions.assertTrue(sqlHelper.checkIfHasSelectAll("SELECT t.* FROM t_order t"));
  }

  @Test
  void testSelectAllWithCondition() {
    Assertions.assertTrue(sqlHelper.checkIfHasSelectAll(
            "SELECT * FROM t_order WHERE user_id = ? AND delete_flag = ?"));
  }

  @Test
  void testSelectColumns() {
    Assertions.assertFalse(sqlHelper.checkIfHasSelectAll("SELECT id, name FROM t_order"));
  }

  /**
   * count(*) 不算 select *：MySelectAllChecker 对 count 函数直接短路，不遍历其参数。
   */
  @Test
  void testSelectCountStarIsNotSelectAll() {
    Assertions.assertFalse(sqlHelper.checkIfHasSelectAll("SELECT COUNT(*) FROM t_order"));
  }

  @Test
  void testSelectColumnsWithCondition() {
    String sql = "SELECT id AS id, user_id AS userId, ac_type AS acType FROM T_ADJUST_FLIGHT_TOP\n"
            + "\n"
            + " WHERE USER_ID = ?\n"
            + "\n"
            + "AND\n"
            + "DELETE_FLAG = ?";
    Assertions.assertFalse(sqlHelper.checkIfHasSelectAll(sql));
  }

  // ------------------------------------------------------------ in ------------------------------------------------------------

  @Test
  void testInWithLiterals() {
    SqlParseResult result = sqlHelper.parseSql("SELECT id FROM t_order WHERE id IN (1, 2, 3)");
    Assertions.assertNotNull(result);
    Assertions.assertEquals(3, result.getMaxInElementCount());
  }

  @Test
  void testInWithJdbcParameters() {
    SqlParseResult result = sqlHelper.parseSql("SELECT id FROM t_order WHERE id IN (?, ?, ?)");
    Assertions.assertNotNull(result);
    Assertions.assertEquals(3, result.getMaxInElementCount());
  }

  /**
   * 多个 IN 子句时取元素个数的最大值。
   */
  @Test
  void testMultipleInTakesMax() {
    SqlParseResult result = sqlHelper.parseSql(
            "SELECT id FROM t_order WHERE id IN (1, 2) AND status IN (1, 2, 3, 4, 5)");
    Assertions.assertNotNull(result);
    Assertions.assertEquals(5, result.getMaxInElementCount());
  }

  /**
   * in (select ...) 是子查询，右表达式不是 ExpressionList，不参与计数。
   */
  @Test
  void testInSubQueryIsNotCounted() {
    SqlParseResult result = sqlHelper.parseSql(
            "SELECT id FROM t_order WHERE id IN (SELECT order_id FROM t_order_item)");
    Assertions.assertNotNull(result);
    Assertions.assertEquals(0, result.getMaxInElementCount());
  }

  @Test
  void testNoInClause() {
    SqlParseResult result = sqlHelper.parseSql("SELECT id FROM t_order WHERE id = ?");
    Assertions.assertNotNull(result);
    Assertions.assertEquals(0, result.getMaxInElementCount());
  }

  // ------------------------------------------------------------ 一次解析同时拿到多个结论 ------------------------------------------------------------

  @Test
  void testParseSqlReturnsBothConclusions() {
    SqlParseResult result = sqlHelper.parseSql("SELECT * FROM t_order WHERE id IN (?, ?, ?, ?)");
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isHasSelectAll());
    Assertions.assertEquals(4, result.getMaxInElementCount());
  }

  @Test
  void testParseSqlReturnsTableNames() {
    SqlParseResult result = sqlHelper.parseSql(
            "SELECT * FROM t_adjust_flight_top WHERE user_id = ?");
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isHasSelectAll());
    Set<String> tableNameSet = result.getTableNameSet();
    Assertions.assertNotNull(tableNameSet);
    Assertions.assertTrue(tableNameSet.contains("t_adjust_flight_top"));
  }

  /**
   * parseSql 与两个单独方法必须给出完全一致的结论，否则统一入口会引入行为差异。
   */
  @Test
  void testParseSqlConsistentWithSingleMethods() {
    String[] sqlArray = new String[]{
            "SELECT * FROM t_order WHERE id IN (1, 2, 3)",
            "SELECT id, name FROM t_order WHERE id = ?",
            "SELECT COUNT(*) FROM t_order WHERE id IN (1, 2)",
            "SELECT t.* FROM t_order t WHERE t.id IN (?, ?)"
    };
    for (String sql : sqlArray) {
      SqlParseResult result = sqlHelper.parseSql(sql);
      Assertions.assertNotNull(result);
      Assertions.assertEquals(sqlHelper.checkIfHasSelectAll(sql), result.isHasSelectAll(),
              "hasSelectAll 不一致: " + sql);
      Assertions.assertEquals(sqlHelper.getTableNameSet(sql), result.getTableNameSet(),
              "tableNameSet 不一致: " + sql);
    }
  }

  // ------------------------------------------------------------ 子查询 ------------------------------------------------------------

  /**
   * 外层只查 id，但子查询里有 select *，仍应判定为命中了 select *。
   */
  @Test
  void testSubQuerySelectAll() {
    Assertions.assertTrue(sqlHelper.checkIfHasSelectAll(
            "SELECT id FROM (SELECT * FROM t_order) t"));
  }

  @Test
  void testSubQueryNoSelectAll() {
    Assertions.assertFalse(sqlHelper.checkIfHasSelectAll(
            "SELECT id FROM (SELECT id, name FROM t_order) t"));
  }

  @Test
  void testSubQueryIn() {
    SqlParseResult result = sqlHelper.parseSql(
            "SELECT id FROM (SELECT id FROM t_order WHERE status IN (1, 2, 3, 4)) t");
    Assertions.assertNotNull(result);
    Assertions.assertEquals(4, result.getMaxInElementCount());
  }

  /**
   * 外层 in 是子查询不计数，但子查询内部的 in 要计数。
   */
  @Test
  void testInSubQueryInnerIn() {
    SqlParseResult result = sqlHelper.parseSql(
            "SELECT id FROM t_order WHERE id IN (SELECT order_id FROM t_order_item WHERE status IN (1, 2, 3))");
    Assertions.assertNotNull(result);
    Assertions.assertEquals(3, result.getMaxInElementCount());
  }

  @Test
  void testSubQuerySelectAllAndIn() {
    SqlParseResult result = sqlHelper.parseSql(
            "SELECT id FROM (SELECT * FROM t_order WHERE status IN (?, ?, ?)) t");
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isHasSelectAll());
    Assertions.assertEquals(3, result.getMaxInElementCount());
  }

  // ------------------------------------------------------------ with ------------------------------------------------------------

  @Test
  void testWithSelectAll() {
    Assertions.assertTrue(sqlHelper.checkIfHasSelectAll(
            "WITH cte AS (SELECT * FROM t_order) SELECT id FROM cte"));
  }

  @Test
  void testWithNoSelectAll() {
    Assertions.assertFalse(sqlHelper.checkIfHasSelectAll(
            "WITH cte AS (SELECT id, name FROM t_order) SELECT id FROM cte"));
  }

  @Test
  void testWithIn() {
    SqlParseResult result = sqlHelper.parseSql(
            "WITH cte AS (SELECT id FROM t_order WHERE status IN (1, 2, 3)) SELECT id FROM cte");
    Assertions.assertNotNull(result);
    Assertions.assertEquals(3, result.getMaxInElementCount());
  }

  @Test
  void testWithOuterSelectAll() {
    SqlParseResult result = sqlHelper.parseSql(
            "WITH cte AS (SELECT id FROM t_order WHERE status IN (?, ?, ?)) SELECT * FROM cte");
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isHasSelectAll());
    Assertions.assertEquals(3, result.getMaxInElementCount());
  }

  /**
   * 多个 CTE，以及 CTE 里再套子查询。
   */
  @Test
  void testMultipleWithItems() {
    SqlParseResult result = sqlHelper.parseSql(
            "WITH cte1 AS (SELECT id FROM t_order WHERE status IN (1, 2)), "
                    + "cte2 AS (SELECT * FROM cte1) "
                    + "SELECT id FROM cte2");
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isHasSelectAll());
    Assertions.assertEquals(2, result.getMaxInElementCount());
  }

  /**
   * 深层嵌套子查询。
   */
  @Test
  void testDeeplyNestedSubQuerySelectAll() {
    Assertions.assertTrue(sqlHelper.checkIfHasSelectAll(
            "SELECT id FROM (SELECT id FROM (SELECT * FROM t_order) a) b"));
  }

  @Test
  void testUnionSelectAll() {
    Assertions.assertTrue(sqlHelper.checkIfHasSelectAll(
            "SELECT id FROM t_order UNION SELECT * FROM t_order_item"));
  }

  /**
   * CTE 内层与外层都有 in，取全局最大值。
   */
  @Test
  void testWithBothInnerAndOuterIn() {
    SqlParseResult result = sqlHelper.parseSql(
            "WITH cte AS (SELECT id FROM t_order WHERE status IN (1, 2)) "
                    + "SELECT id FROM cte WHERE id IN (1, 2, 3, 4, 5)");
    Assertions.assertNotNull(result);
    Assertions.assertEquals(5, result.getMaxInElementCount());
  }

  /**
   * CTE 里再套子查询，in 藏在最内层。
   */
  @Test
  void testWithNestedSubQueryInCte() {
    SqlParseResult result = sqlHelper.parseSql(
            "WITH cte AS (SELECT id FROM (SELECT id FROM t_order WHERE status IN (?, ?, ?)) x) "
                    + "SELECT id FROM cte");
    Assertions.assertNotNull(result);
    Assertions.assertEquals(3, result.getMaxInElementCount());
  }

  /**
   * WITH 语法里的表名能提取出来
   */
  @Test
  void testWithTableNames() {
    SqlParseResult result = sqlHelper.parseSql(
            "WITH cte AS (SELECT id FROM t_order WHERE status IN (1, 2)) SELECT id FROM cte");
    Assertions.assertNotNull(result);
    Set<String> tableNameSet = result.getTableNameSet();
    Assertions.assertNotNull(tableNameSet);
    Assertions.assertTrue(tableNameSet.contains("t_order"),
            "应能提取出 CTE 内部的 t_order，实际: " + tableNameSet);
  }

  // ------------------------------------------------------------ 解析失败 ------------------------------------------------------------

  @Test
  void testParseSqlOnInvalidSql() {
    Assertions.assertNull(sqlHelper.parseSql("SELECT FROM WHERE"));
  }

  @Test
  void testCheckIfHasSelectAllOnInvalidSql() {
    Assertions.assertFalse(sqlHelper.checkIfHasSelectAll("SELECT FROM WHERE"));
  }

  @Test
  void testGetTableNameSetOnInvalidSql() {
    Assertions.assertNull(sqlHelper.getTableNameSet("SELECT FROM WHERE"));
  }

  private static class SqlHelperThreadFactory implements ThreadFactory {

    private final AtomicLong nameCounter = new AtomicLong(0L);

    @Override
    public Thread newThread(Runnable r) {
      Thread thread = new Thread(r);
      thread.setName("sqlHelper-thread-" + nameCounter.getAndIncrement());
      return thread;
    }
  }

}
