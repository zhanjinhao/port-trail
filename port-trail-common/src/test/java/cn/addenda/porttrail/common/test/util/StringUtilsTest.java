package cn.addenda.porttrail.common.test.util;

import cn.addenda.porttrail.common.util.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class StringUtilsTest {

  @Test
  void testExpandWithSpecifiedChar() {
    Assertions.assertEquals("00123", StringUtils.expandWithSpecifiedChar("123", '0', 5));
    Assertions.assertEquals("12345", StringUtils.expandWithSpecifiedChar("12345", '0', 5));
    Assertions.assertEquals("123456", StringUtils.expandWithSpecifiedChar("123456", '0', 5));
    Assertions.assertEquals("xxx", StringUtils.expandWithSpecifiedChar("", 'x', 3));
    Assertions.assertEquals("xxxab", StringUtils.expandWithSpecifiedChar("ab", 'x', 5));
  }

  @Test
  void testExpandWithZero() {
    Assertions.assertEquals("0007", StringUtils.expandWithZero("7", 4));
    Assertions.assertEquals("1234", StringUtils.expandWithZero("1234", 4));
    Assertions.assertEquals("000", StringUtils.expandWithZero("", 3));
  }

  @Test
  void testJoin() {
    Assertions.assertEquals("a,b,c", StringUtils.join(",", "a", "b", "c"));
    Assertions.assertEquals("a,c", StringUtils.join(",", "a", null, "  ", "c"));
    Assertions.assertEquals("", StringUtils.join(",", null, "", "  "));
    Assertions.assertEquals("a", StringUtils.join(",", "a"));
    Assertions.assertEquals("", StringUtils.join(","));
  }

  @Test
  void testDiscardNull() {
    Assertions.assertEquals("", StringUtils.discardNull(null));
    Assertions.assertEquals("abc", StringUtils.discardNull("abc"));
    Assertions.assertEquals("", StringUtils.discardNull(""));
  }

  @Test
  void testHasText() {
    Assertions.assertFalse(StringUtils.hasText((CharSequence) null));
    Assertions.assertFalse(StringUtils.hasText((CharSequence) ""));
    Assertions.assertFalse(StringUtils.hasText((CharSequence) "  "));
    Assertions.assertTrue(StringUtils.hasText((CharSequence) "a"));
    Assertions.assertTrue(StringUtils.hasText((CharSequence) " a "));

    Assertions.assertFalse(StringUtils.hasText((String) null));
    Assertions.assertFalse(StringUtils.hasText(""));
    Assertions.assertFalse(StringUtils.hasText("  "));
    Assertions.assertTrue(StringUtils.hasText("a"));
    Assertions.assertTrue(StringUtils.hasText(" a "));
  }

  @Test
  void testHasLength() {
    Assertions.assertFalse(StringUtils.hasLength(null));
    Assertions.assertFalse(StringUtils.hasLength(""));
    Assertions.assertTrue(StringUtils.hasLength("  "));
    Assertions.assertTrue(StringUtils.hasLength("a"));
  }

  @Test
  void testBiTrimSpecifiedChar() {
    Assertions.assertEquals("", StringUtils.biTrimSpecifiedChar("aaa", 'a'));
    Assertions.assertNull(StringUtils.biTrimSpecifiedChar(null, 'a'));
    Assertions.assertEquals("Hello", StringUtils.biTrimSpecifiedChar("  Hello  ", ' '));
    Assertions.assertEquals("Hello", StringUtils.biTrimSpecifiedChar("aaHello", 'a'));
    Assertions.assertEquals("Hello", StringUtils.biTrimSpecifiedChar("Helloaaa", 'a'));
    Assertions.assertEquals("Hello", StringUtils.biTrimSpecifiedChar("Hello", 'a'));
    Assertions.assertEquals("", StringUtils.biTrimSpecifiedChar("x", 'x'));
    Assertions.assertEquals("x", StringUtils.biTrimSpecifiedChar("x", 'y'));
  }

  @Test
  void testIsStrictlyNumeric() {
    Assertions.assertFalse(StringUtils.isStrictlyNumeric(null));
    Assertions.assertFalse(StringUtils.isStrictlyNumeric(""));
    Assertions.assertTrue(StringUtils.isStrictlyNumeric("123"));
    Assertions.assertTrue(StringUtils.isStrictlyNumeric("0123"));
    Assertions.assertFalse(StringUtils.isStrictlyNumeric("12a34"));
    Assertions.assertFalse(StringUtils.isStrictlyNumeric("-123"));
    Assertions.assertFalse(StringUtils.isStrictlyNumeric("12.34"));
    Assertions.assertFalse(StringUtils.isStrictlyNumeric(" 123"));
  }

  @Test
  void testSubstringMatch() {
    Assertions.assertTrue(StringUtils.substringMatch("hello", 0, "hel"));
    Assertions.assertFalse(StringUtils.substringMatch("hello", 0, "abc"));
    Assertions.assertTrue(StringUtils.substringMatch("hello", 2, "llo"));
    Assertions.assertTrue(StringUtils.substringMatch("hello", 4, "o"));
    Assertions.assertFalse(StringUtils.substringMatch("hello", 3, "lox"));
    Assertions.assertTrue(StringUtils.substringMatch("hello", 0, ""));
    Assertions.assertTrue(StringUtils.substringMatch("hello", 5, ""));
    Assertions.assertFalse(StringUtils.substringMatch("hello", 6, ""));
  }

  @Test
  void testStartsWithIgnoreCase() {
    Assertions.assertTrue(StringUtils.startsWithIgnoreCase("Hello", "hel"));
    Assertions.assertFalse(StringUtils.startsWithIgnoreCase("Hello", "abc"));
    Assertions.assertFalse(StringUtils.startsWithIgnoreCase(null, "hel"));
    Assertions.assertFalse(StringUtils.startsWithIgnoreCase("Hello", null));
    Assertions.assertFalse(StringUtils.startsWithIgnoreCase("hi", "hello"));
    Assertions.assertTrue(StringUtils.startsWithIgnoreCase("HELLO", "hello"));
    Assertions.assertTrue(StringUtils.startsWithIgnoreCase("HelloWorld", "HELLO"));
    Assertions.assertTrue(StringUtils.startsWithIgnoreCase("hElLo", "HeLlO"));
  }

}
