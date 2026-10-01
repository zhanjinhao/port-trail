package cn.addenda.porttrail.server.helper;

import lombok.*;

import java.io.Serializable;
import java.util.Set;

/**
 * 一次 SQL 解析产出的全部结论。
 *
 * <p>同一个 AST 上跑完所有 visitor 后一次性返回，供各 AnalyzeHandler 复用；
 * 同时可作为 Redis 解析缓存的值对象（按 SQL 文本 hash 做 key，跨环境跨天共享）。
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SqlParseResult implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * 是否查询了所有字段（select *）
   */
  private boolean hasSelectAll;

  /**
   * IN 子句里元素个数的最大值
   */
  private int maxInElementCount;

  /**
   * SQL 涉及的表名集合，解析失败时为 null
   */
  private Set<String> tableNameSet;

}