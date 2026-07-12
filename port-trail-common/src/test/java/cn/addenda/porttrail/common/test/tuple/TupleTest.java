package cn.addenda.porttrail.common.test.tuple;

import cn.addenda.porttrail.common.tuple.Binary;
import cn.addenda.porttrail.common.tuple.Ternary;
import cn.addenda.porttrail.common.tuple.Unary;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

class TupleTest {

  // ==================================================================
  //  Unary
  // ==================================================================

  @Test
  void testUnary_of() {
    Unary<String> u = Unary.of("hello");
    Assertions.assertEquals("hello", u.getF1());
  }

  @Test
  void testUnary_equals_sameObject() {
    Unary<String> u = Unary.of("hello");
    Assertions.assertEquals(u, u);
  }

  @Test
  void testUnary_equals_equalContent() {
    Unary<String> u1 = Unary.of("hello");
    Unary<String> u2 = Unary.of("hello");
    Assertions.assertEquals(u1, u2);
    Assertions.assertEquals(u2, u1);
  }

  @Test
  void testUnary_equals_differentContent() {
    Unary<String> u1 = Unary.of("hello");
    Unary<String> u2 = Unary.of("world");
    Assertions.assertNotEquals(u1, u2);
  }

  @Test
  void testUnary_equals_null() {
    Unary<String> u = Unary.of("hello");
    Assertions.assertNotEquals(u, null);
  }

  @Test
  void testUnary_equals_differentType() {
    Unary<String> u = Unary.of("hello");
    Assertions.assertNotEquals("hello", u);
  }

  @Test
  void testUnary_equals_nullField() {
    Unary<String> u1 = Unary.of(null);
    Unary<String> u2 = Unary.of(null);
    Assertions.assertEquals(u1, u2);
    Assertions.assertNotEquals(Unary.of("a"), Unary.of(null));
    Assertions.assertNotEquals(Unary.of(null), Unary.of("a"));
  }

  @Test
  void testUnary_hashCode_consistentWithEquals() {
    Unary<String> u1 = Unary.of("hello");
    Unary<String> u2 = Unary.of("hello");
    Assertions.assertEquals(u1.hashCode(), u2.hashCode());
  }

  @Test
  void testUnary_hashCode_nullSafe() {
    Assertions.assertEquals(Unary.of(null).hashCode(), Unary.of(null).hashCode());
    Unary.of(null).hashCode();
  }

  // ==================================================================
  //  Binary — Map.Entry 契约
  // ==================================================================

  @Test
  void testBinary_of() {
    Binary<String, Integer> b = Binary.of("key", 1);
    Assertions.assertEquals("key", b.getKey());
    Assertions.assertEquals(1, (int) b.getValue());
  }

  @Test
  void testBinary_getKey_getValue() {
    Binary<String, Integer> b = Binary.of("key", 1);
    Assertions.assertEquals("key", b.getKey());
    Assertions.assertEquals(1, (int) b.getValue());
  }

  @Test
  void testBinary_setValue() {
    Binary<String, Integer> b = Binary.of("key", 1);
    Integer old = b.setValue(2);
    Assertions.assertEquals(1, (int) old);
    Assertions.assertEquals(2, (int) b.getValue());
    Assertions.assertEquals("key", b.getKey());
  }

  @Test
  void testBinary_equals_sameObject() {
    Binary<String, Integer> b = Binary.of("key", 1);
    Assertions.assertEquals(b, b);
  }

  @Test
  void testBinary_equals_equalContent() {
    Binary<String, Integer> b1 = Binary.of("key", 1);
    Binary<String, Integer> b2 = Binary.of("key", 1);
    Assertions.assertEquals(b1, b2);
    Assertions.assertEquals(b2, b1);
  }

  @Test
  void testBinary_equals_differentKey() {
    Binary<String, Integer> b1 = Binary.of("key1", 1);
    Binary<String, Integer> b2 = Binary.of("key2", 1);
    Assertions.assertNotEquals(b1, b2);
  }

  @Test
  void testBinary_equals_differentValue() {
    Binary<String, Integer> b1 = Binary.of("key", 1);
    Binary<String, Integer> b2 = Binary.of("key", 2);
    Assertions.assertNotEquals(b1, b2);
  }

  @Test
  void testBinary_equals_null() {
    Binary<String, Integer> b = Binary.of("key", 1);
    Assertions.assertNotEquals(b, null);
  }

  @Test
  void testBinary_equals_nonMapEntry() {
    Binary<String, Integer> b = Binary.of("key", 1);
    Assertions.assertNotEquals("key", b);
  }

  @Test
  void testBinary_equals_nullFields() {
    Binary<String, Integer> b1 = Binary.of(null, null);
    Binary<String, Integer> b2 = Binary.of(null, null);
    Assertions.assertEquals(b1, b2);

    Binary<String, Integer> b3 = Binary.of(null, 1);
    Binary<String, Integer> b4 = Binary.of(null, 2);
    Assertions.assertNotEquals(b3, b4);

    Binary<String, Integer> b5 = Binary.of(null, 1);
    Binary<String, Integer> b6 = Binary.of("key", 1);
    Assertions.assertNotEquals(b5, b6);
  }

  // ---------- Map.Entry 契约：与 non-Binary Map.Entry 比较 ----------

  @Test
  void testBinary_equals_SimpleEntry_sameContent() {
    Binary<String, Integer> b = Binary.of("key", 1);
    Map.Entry<String, Integer> entry = new AbstractMap.SimpleEntry<>("key", 1);
    Assertions.assertEquals(b, entry);
    Assertions.assertEquals(entry, b);
  }

  @Test
  void testBinary_equals_SimpleEntry_differentKey() {
    Binary<String, Integer> b = Binary.of("key", 1);
    Map.Entry<String, Integer> entry = new AbstractMap.SimpleEntry<>("other", 1);
    Assertions.assertNotEquals(b, entry);
  }

  @Test
  void testBinary_equals_SimpleEntry_differentValue() {
    Binary<String, Integer> b = Binary.of("key", 1);
    Map.Entry<String, Integer> entry = new AbstractMap.SimpleEntry<>("key", 2);
    Assertions.assertNotEquals(b, entry);
  }

  @Test
  void testBinary_equals_SimpleEntry_nullKeyValue() {
    Binary<String, Integer> b = Binary.of(null, null);
    Map.Entry<String, Integer> entry = new AbstractMap.SimpleEntry<>(null, null);
    Assertions.assertEquals(b, entry);
  }

  @Test
  void testBinary_equals_customMapEntry() {
    Binary<String, String> b = Binary.of("a", "b");
    Map.Entry<String, String> custom = new Map.Entry<String, String>() {
      @Override
      public String getKey() { return "a"; }

      @Override
      public String getValue() { return "b"; }

      @Override
      public String setValue(String value) { throw new UnsupportedOperationException(); }
    };
    Assertions.assertEquals(b, custom);
  }

  // ---------- hashCode Map.Entry 契约 ----------

  @Test
  void testBinary_hashCode_consistentWithEquals() {
    Binary<String, Integer> b1 = Binary.of("key", 1);
    Binary<String, Integer> b2 = Binary.of("key", 1);
    Assertions.assertEquals(b1.hashCode(), b2.hashCode());
  }

  @Test
  void testBinary_hashCode_matchesMapEntryContract() {
    String key = "key";
    Integer value = 1;
    Binary<String, Integer> b = Binary.of(key, value);
    int expected = (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
    Assertions.assertEquals(expected, b.hashCode());
  }

  @Test
  void testBinary_hashCode_matchesMapEntryContract_nullKey() {
    Integer value = 1;
    Binary<String, Integer> b = Binary.of(null, value);
    int expected = 0 ^ value.hashCode();
    Assertions.assertEquals(expected, b.hashCode());
  }

  @Test
  void testBinary_hashCode_matchesMapEntryContract_nullValue() {
    String key = "key";
    Binary<String, Integer> b = Binary.of(key, null);
    int expected = key.hashCode() ^ 0;
    Assertions.assertEquals(expected, b.hashCode());
  }

  @Test
  void testBinary_hashCode_matchesMapEntryContract_nullBoth() {
    Binary<String, Integer> b = Binary.of(null, null);
    Assertions.assertEquals(0, b.hashCode());
  }

  @Test
  void testBinary_hashCode_matchesSimpleEntry() {
    Binary<String, Integer> b = Binary.of("key", 1);
    Map.Entry<String, Integer> entry = new AbstractMap.SimpleEntry<>("key", 1);
    Assertions.assertEquals(entry.hashCode(), b.hashCode());
  }

  // ---------- HashMap 兼容性 ----------

  @Test
  void testBinary_usedAsHashMapKey() {
    Map<Binary<String, Integer>, String> map = new HashMap<>();
    Binary<String, Integer> b1 = Binary.of("k1", 1);
    map.put(b1, "v1");

    Binary<String, Integer> b2 = Binary.of("k1", 1);
    Assertions.assertEquals("v1", map.get(b2));

    Binary<String, Integer> b3 = Binary.of("k2", 1);
    Assertions.assertNull(map.get(b3));
  }

  // ==================================================================
  //  Ternary
  // ==================================================================

  @Test
  void testTernary_of() {
    Ternary<String, Integer, Boolean> t = Ternary.of("a", 1, true);
    Assertions.assertEquals("a", t.getF1());
    Assertions.assertEquals(1, (int) t.getF2());
    Assertions.assertEquals(true, t.getF3());
  }

  @Test
  void testTernary_equals_sameObject() {
    Ternary<String, Integer, Boolean> t = Ternary.of("a", 1, true);
    Assertions.assertEquals(t, t);
  }

  @Test
  void testTernary_equals_equalContent() {
    Ternary<String, Integer, Boolean> t1 = Ternary.of("a", 1, true);
    Ternary<String, Integer, Boolean> t2 = Ternary.of("a", 1, true);
    Assertions.assertEquals(t1, t2);
    Assertions.assertEquals(t2, t1);
  }

  @Test
  void testTernary_equals_differentF1() {
    Ternary<String, Integer, Boolean> t1 = Ternary.of("a", 1, true);
    Ternary<String, Integer, Boolean> t2 = Ternary.of("b", 1, true);
    Assertions.assertNotEquals(t1, t2);
  }

  @Test
  void testTernary_equals_differentF2() {
    Ternary<String, Integer, Boolean> t1 = Ternary.of("a", 1, true);
    Ternary<String, Integer, Boolean> t2 = Ternary.of("a", 2, true);
    Assertions.assertNotEquals(t1, t2);
  }

  @Test
  void testTernary_equals_differentF3() {
    Ternary<String, Integer, Boolean> t1 = Ternary.of("a", 1, true);
    Ternary<String, Integer, Boolean> t2 = Ternary.of("a", 1, false);
    Assertions.assertNotEquals(t1, t2);
  }

  @Test
  void testTernary_equals_null() {
    Ternary<String, Integer, Boolean> t = Ternary.of("a", 1, true);
    Assertions.assertNotEquals(t, null);
  }

  @Test
  void testTernary_equals_differentType() {
    Ternary<String, Integer, Boolean> t = Ternary.of("a", 1, true);
    Assertions.assertNotEquals("a", t);
  }

  @Test
  void testTernary_equals_nullFields() {
    Ternary<String, Integer, Boolean> t1 = Ternary.of(null, null, null);
    Ternary<String, Integer, Boolean> t2 = Ternary.of(null, null, null);
    Assertions.assertEquals(t1, t2);

    Ternary<String, Integer, Boolean> t3 = Ternary.of("a", null, null);
    Ternary<String, Integer, Boolean> t4 = Ternary.of("a", null, null);
    Assertions.assertEquals(t3, t4);
    Assertions.assertNotEquals(Ternary.of("a", null, null), Ternary.of("b", null, null));
  }

  @Test
  void testTernary_hashCode_consistentWithEquals() {
    Ternary<String, Integer, Boolean> t1 = Ternary.of("a", 1, true);
    Ternary<String, Integer, Boolean> t2 = Ternary.of("a", 1, true);
    Assertions.assertEquals(t1.hashCode(), t2.hashCode());
  }

  @Test
  void testTernary_hashCode_nullSafe() {
    Assertions.assertEquals(
            Ternary.of(null, null, null).hashCode(),
            Ternary.of(null, null, null).hashCode());
    Ternary.of(null, null, null).hashCode();
  }

}
