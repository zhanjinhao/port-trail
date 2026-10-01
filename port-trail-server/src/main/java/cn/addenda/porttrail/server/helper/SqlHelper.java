package cn.addenda.porttrail.server.helper;

import cn.addenda.porttrail.server.sql.MySelectAllChecker;
import cn.addenda.porttrail.server.sql.MyTablesNamesFinder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;

import java.util.Set;
import java.util.concurrent.ExecutorService;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SqlHelper {

  private ExecutorService executorService;

  public SqlHelper(ExecutorService executorService) {
    this.executorService = executorService;
  }

  /**
   * 统一解析入口：只 parse 一次，在同一个 AST 上跑完所有 visitor。
   *
   * <p>需要多个解析结论时用这个，不要分别调 {@link #getTableNameSet} / {@link #checkIfHasSelectAll}，
   * 否则同一条 SQL 会被重复解析。SQL 解析是 CPU 密集操作，重复解析会打满 sqlHelper 线程池。
   *
   * @return 解析失败时返回 null
   */
  public SqlParseResult parseSql(String sql) {
    try {
      Statement statement = parse(sql);
      MySelectAllChecker mySelectAllChecker = new MySelectAllChecker();
      statement.accept(mySelectAllChecker);
      MyTablesNamesFinder finder = new MyTablesNamesFinder();
      Set<String> tableNameSet = finder.getTables(statement);
      return new SqlParseResult(mySelectAllChecker.isHasSelectAll(),
              mySelectAllChecker.getMaxInElementCount(), tableNameSet);
    } catch (JSQLParserException e) {
      log.error("Error parsing SQL: {}.", sql, e);
    }
    // todo
    return null;
  }

  private Statement parse(String sql) throws JSQLParserException {
    return CCJSqlParserUtil.parse(sql, executorService,
            ccjSqlParser -> {
              ccjSqlParser.withBackslashEscapeCharacter(true);
              ccjSqlParser.withSquareBracketQuotation(true);
            });
  }

  /**
   * todo with语法里的表明能提取出来吗
   */
  public Set<String> getTableNameSet(String sql) {
    try {
      Statement statement = parse(sql);
      MyTablesNamesFinder finder = new MyTablesNamesFinder();
      return finder.getTables(statement);
    } catch (JSQLParserException e) {
      log.error("Error parsing SQL: {}.", sql, e);
    }
    // todo
    return null;
  }

  public boolean checkIfHasSelectAll(String sql) {
    try {
      Statement statement = parse(sql);
      MySelectAllChecker mySelectAllChecker = new MySelectAllChecker();
      statement.accept(mySelectAllChecker);
      return mySelectAllChecker.isHasSelectAll();
    } catch (JSQLParserException e) {
      log.error("Error parsing SQL: {}.", sql, e);
    }
    // todo
    return false;
  }

  public String formatSql(String sql) {
    String[] split = sql.split("\n");

    StringBuilder stringBuilder = new StringBuilder();
    for (String s : split) {
      stringBuilder.append(s).append(" ");
    }

    return stringBuilder.toString();
  }

}