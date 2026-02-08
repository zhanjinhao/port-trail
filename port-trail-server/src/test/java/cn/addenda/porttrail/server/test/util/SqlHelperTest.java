package cn.addenda.porttrail.server.test.util;

import cn.addenda.porttrail.common.util.StringUtils;
import cn.addenda.porttrail.server.PortTrailServerApplication;
import cn.addenda.porttrail.server.helper.SqlHelper;
import cn.addenda.porttrail.server.test.SqlReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@SpringBootTest(classes = PortTrailServerApplication.class)
class SqlHelperTest {

  @Autowired
  private SqlHelper sqlHelper;

  @Test
  void testGetTableNameSet_INSERT() {
    String[] sqls = {
    };
    String[] reads = SqlReader.read("src/test/resources/cn/addenda/porttrail/server/test/util/sql_tablename_insert.test", sqls);

    for (int i = 0; i < reads.length; i++) {
      String read = reads[i];

      testGetTableNameSet(read, i);
    }
  }

  @Test
  void testGetTableNameSet_SELECT() {
    String[] sqls = {
    };
    String[] reads = SqlReader.read("src/test/resources/cn/addenda/porttrail/server/test/util/sql_tablename_select.test", sqls);

    for (int i = 0; i < reads.length; i++) {
      String read = reads[i];

      testGetTableNameSet(read, i);
    }
  }

  @Test
  void testGetTableNameSet_UPDATE() {
    String[] sqls = {
    };
    String[] reads = SqlReader.read("src/test/resources/cn/addenda/porttrail/server/test/util/sql_tablename_update.test", sqls);

    for (int i = 0; i < reads.length; i++) {
      String read = reads[i];

      testGetTableNameSet(read, i);
    }
  }

  void testGetTableNameSet(String read, int i) {
    if (!StringUtils.hasText(read)) {
      return;
    }
    if (read.trim().startsWith("-")) {
      return;
    }
    String[] split = read.split(";");
    String sql = split[0];
    if (!StringUtils.hasText(sql)) {
      return;
    }
    System.out.println((i + 1) + " : " + sql);

    Set<String> tableNameList = Objects.requireNonNull(sqlHelper.getTableNameSet(sql)).stream().map(a -> StringUtils.biTrimSpecifiedChar(a, '`')).sorted().collect(Collectors.toCollection(LinkedHashSet::new));

    Set<String> expected = Arrays.stream(split[1].trim().split(",")).map(String::trim).sorted().collect(Collectors.toCollection(LinkedHashSet::new));

    Assertions.assertEquals(expected, tableNameList);

    System.out.println(tableNameList);
  }

  @Test
  void testCheckIfHasSelectAll_INSERT() {
    String[] sqls = {
    };
    String[] reads = SqlReader.read("src/test/resources/cn/addenda/porttrail/server/test/util/sql_selectall_insert.test", sqls);

    for (int i = 0; i < reads.length; i++) {
      String read = reads[i];
      testCheckIfHasSelectAll(read, i);
    }
  }

  @Test
  void testCheckIfHasSelectAll_SELECT() {
    String[] sqls = {
    };
    String[] reads = SqlReader.read("src/test/resources/cn/addenda/porttrail/server/test/util/sql_selectall_select.test", sqls);

    for (int i = 0; i < reads.length; i++) {
      String read = reads[i];
      testCheckIfHasSelectAll(read, i);
    }
  }

  @Test
  void testCheckIfHasSelectAll_UPDATE() {
    String[] sqls = {
    };
    String[] reads = SqlReader.read("src/test/resources/cn/addenda/porttrail/server/test/util/sql_selectall_update.test", sqls);

    for (int i = 0; i < reads.length; i++) {
      String read = reads[i];
      testCheckIfHasSelectAll(read, i);
    }
  }

  void testCheckIfHasSelectAll(String read, int i) {
    if (!StringUtils.hasText(read)) {
      return;
    }
    String[] split = read.split(";");

    String sql = split[0];
    if (!StringUtils.hasText(sql)) {
      return;
    }
    boolean b = sqlHelper.checkIfHasSelectAll(sql);
    System.out.println((i + 1) + " : " + sql);
    Assertions.assertEquals(Boolean.parseBoolean(split[1].trim()), b);
  }

}
