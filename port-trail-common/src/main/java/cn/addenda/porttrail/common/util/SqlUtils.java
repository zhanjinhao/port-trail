package cn.addenda.porttrail.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Locale;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SqlUtils {

  public static final String SQL_TYPE_INSERT = "INSERT";
  public static final String SQL_TYPE_UPDATE = "UPDATE";
  public static final String SQL_TYPE_DELETE = "DELETE";
  public static final String SQL_TYPE_SELECT = "SELECT";

  public static boolean ifQuerySql(String sql) {
    return "SELECT".equals(getSqlType(sql));
  }

  public static String getSqlType(String sql) {
    int length = sql.length();
    int i = 0;
    while (i < length) {
      if (sql.charAt(i) <= ' ') {
        i++;
      } else {
        break;
      }
    }

    StringBuilder stringBuilder = new StringBuilder();
    while (i < length) {
      char c = sql.charAt(i);
      if (c > ' ') {
        stringBuilder.append(c);
        i++;
      } else {
        break;
      }
    }

    return stringBuilder.toString().toUpperCase(Locale.ROOT);
  }

}
