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
   * todo with语法里的表明能提取出来吗
   */
  public Set<String> getTableNameSet(String sql) {
    try {
      Statement statement = CCJSqlParserUtil.parse(sql, executorService,
              ccjSqlParser -> {
                ccjSqlParser.withBackslashEscapeCharacter(true);
                ccjSqlParser.withSquareBracketQuotation(true);
              });
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
      Statement statement = CCJSqlParserUtil.parse(sql, executorService,
              ccjSqlParser -> {
                ccjSqlParser.withBackslashEscapeCharacter(true);
                ccjSqlParser.withSquareBracketQuotation(true);
              });
      MySelectAllChecker mySelectAllChecker = new MySelectAllChecker();
      statement.accept(mySelectAllChecker);
      return mySelectAllChecker.isHasSelectAll();
    } catch (JSQLParserException e) {
      log.error("Error parsing SQL: {}.", sql, e);
    }
    // todo
    return false;
  }

}
