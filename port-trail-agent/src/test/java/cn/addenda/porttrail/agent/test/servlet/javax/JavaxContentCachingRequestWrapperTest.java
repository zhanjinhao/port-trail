package cn.addenda.porttrail.agent.test.servlet.javax;

import cn.addenda.porttrail.agent.transform.interceptor.servlet.javax.JavaxContentCachingRequestWrapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.servlet.ServletInputStream;
import java.io.BufferedReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

class JavaxContentCachingRequestWrapperTest {

  // ==================== 1. inputStream read - normal ====================

  @Test
  void testInputStreamRead_normal() throws Exception {
    byte[] input = "hello".getBytes(StandardCharsets.UTF_8);
    FakeHttpServletRequest fake = new FakeHttpServletRequest(input);
    JavaxContentCachingRequestWrapper wrapper =
            new JavaxContentCachingRequestWrapper(fake, 100);

    ServletInputStream is = wrapper.getInputStream();
    byte[] buf = new byte[100];
    int len = is.read(buf);

    Assertions.assertEquals(input.length, len);
    Assertions.assertArrayEquals(input, Arrays.copyOf(buf, len));
    Assertions.assertArrayEquals(input, wrapper.getContentAsByteArray());
    Assertions.assertEquals(input.length, wrapper.getContentSize());
    Assertions.assertFalse(wrapper.isContentExceedLimit());
  }

  // ==================== 2. inputStream read - exceed limit ====================

  @Test
  void testInputStreamRead_exceedLimit() throws Exception {
    byte[] input = "hello world!".getBytes(StandardCharsets.UTF_8); // 12 bytes
    int cacheLimit = 5;
    FakeHttpServletRequest fake = new FakeHttpServletRequest(input);
    JavaxContentCachingRequestWrapper wrapper =
            new JavaxContentCachingRequestWrapper(fake, cacheLimit);

    ServletInputStream is = wrapper.getInputStream();
    byte[] buf = new byte[100];
    int len = is.read(buf);

    // data read correctly
    Assertions.assertEquals(input.length, len);
    Assertions.assertArrayEquals(input, Arrays.copyOf(buf, len));
    // cache cleared once contentWritten exceeds limit
    Assertions.assertEquals(0, wrapper.getContentAsByteArray().length);
    Assertions.assertTrue(wrapper.isContentExceedLimit());
    // getContentSize returns total bytes read
    Assertions.assertEquals(input.length, wrapper.getContentSize());
  }

  // ==================== 3. inputStream read - exact limit ====================

  @Test
  void testInputStreamRead_exactLimit() throws Exception {
    byte[] input = {0, 1, 2, 3, 4}; // exactly 5 bytes
    int cacheLimit = 5;
    FakeHttpServletRequest fake = new FakeHttpServletRequest(input);
    JavaxContentCachingRequestWrapper wrapper =
            new JavaxContentCachingRequestWrapper(fake, cacheLimit);

    ServletInputStream is = wrapper.getInputStream();
    byte[] buf = new byte[100];
    int len = is.read(buf);

    Assertions.assertEquals(input.length, len);
    Assertions.assertArrayEquals(input, wrapper.getContentAsByteArray());
    Assertions.assertFalse(wrapper.isContentExceedLimit());
    Assertions.assertEquals(cacheLimit, wrapper.getContentSize());
  }

  // ==================== 4. inputStream read - limit + 1 ====================

  @Test
  void testInputStreamRead_oneByteOverLimit() throws Exception {
    byte[] input = {10, 20, 30, 40, 50, 60}; // 6 bytes
    int cacheLimit = 5;
    FakeHttpServletRequest fake = new FakeHttpServletRequest(input);
    JavaxContentCachingRequestWrapper wrapper =
            new JavaxContentCachingRequestWrapper(fake, cacheLimit);

    ServletInputStream is = wrapper.getInputStream();
    byte[] buf = new byte[100];
    int len = is.read(buf);

    Assertions.assertEquals(input.length, len);
    Assertions.assertArrayEquals(input, Arrays.copyOf(buf, len));
    // cache cleared
    Assertions.assertEquals(0, wrapper.getContentAsByteArray().length);
    Assertions.assertTrue(wrapper.isContentExceedLimit());
    Assertions.assertEquals(input.length, wrapper.getContentSize());
  }

  // ==================== 5. incremental reads cross limit ====================

  @Test
  void testIncrementalReads_crossLimit() throws Exception {
    byte[] input = {1, 2, 3, 4, 5, 6, 7}; // 7 bytes
    int cacheLimit = 5;
    FakeHttpServletRequest fake = new FakeHttpServletRequest(input);
    JavaxContentCachingRequestWrapper wrapper =
            new JavaxContentCachingRequestWrapper(fake, cacheLimit);

    ServletInputStream is = wrapper.getInputStream();

    // read 5 bytes one-by-one: all cached
    for (int i = 0; i < cacheLimit; i++) {
      int b = is.read();
      Assertions.assertEquals(input[i], (byte) b);
    }
    Assertions.assertEquals(cacheLimit, wrapper.getContentAsByteArray().length);
    Assertions.assertFalse(wrapper.isContentExceedLimit());

    // 6th byte: crosses limit -> cache cleared
    int b6 = is.read();
    Assertions.assertEquals(input[cacheLimit], (byte) b6);
    Assertions.assertEquals(0, wrapper.getContentAsByteArray().length);
    Assertions.assertTrue(wrapper.isContentExceedLimit());

    // 7th byte: still exceeded, cache stays empty
    int b7 = is.read();
    Assertions.assertEquals(input[cacheLimit + 1], (byte) b7);
    Assertions.assertEquals(0, wrapper.getContentAsByteArray().length);
    Assertions.assertTrue(wrapper.isContentExceedLimit());

    Assertions.assertEquals(input.length, wrapper.getContentSize());
  }

  // ==================== 6. reader read - normal ====================

  @Test
  void testReaderRead_normal() throws Exception {
    String text = "hello reader!";
    byte[] input = text.getBytes(StandardCharsets.UTF_8);
    FakeHttpServletRequest fake = new FakeHttpServletRequest(input);
    JavaxContentCachingRequestWrapper wrapper =
            new JavaxContentCachingRequestWrapper(fake, 100);

    BufferedReader reader = wrapper.getReader();
    char[] cbuf = new char[100];
    int len = reader.read(cbuf);

    String result = new String(cbuf, 0, len);
    Assertions.assertEquals(text, result);
    Assertions.assertEquals(input.length, wrapper.getContentSize());
    Assertions.assertFalse(wrapper.isContentExceedLimit());
  }

  // ==================== 7. reader read - exceed limit ====================

  @Test
  void testReaderRead_exceedLimit() throws Exception {
    String text = "hello reader overflow!";
    byte[] input = text.getBytes(StandardCharsets.UTF_8);
    int cacheLimit = 5;
    FakeHttpServletRequest fake = new FakeHttpServletRequest(input);
    JavaxContentCachingRequestWrapper wrapper =
            new JavaxContentCachingRequestWrapper(fake, cacheLimit);

    BufferedReader reader = wrapper.getReader();
    char[] cbuf = new char[100];
    int len = reader.read(cbuf);

    String result = new String(cbuf, 0, len);
    Assertions.assertEquals(text, result);
    Assertions.assertEquals(0, wrapper.getContentAsByteArray().length);
    Assertions.assertTrue(wrapper.isContentExceedLimit());
    Assertions.assertEquals(input.length, wrapper.getContentSize());
  }

  // ==================== 8. partial reads then full consume ====================

  @Test
  void testPartialReads_thenFullConsume() throws Exception {
    byte[] input = {10, 20, 30, 40, 50, 60, 70, 80};
    int cacheLimit = 3;
    FakeHttpServletRequest fake = new FakeHttpServletRequest(input);
    JavaxContentCachingRequestWrapper wrapper =
            new JavaxContentCachingRequestWrapper(fake, cacheLimit);

    ServletInputStream is = wrapper.getInputStream();

    // read first 2 bytes
    byte[] buf1 = new byte[2];
    int r1 = is.read(buf1);
    Assertions.assertEquals(2, r1);
    Assertions.assertArrayEquals(new byte[]{10, 20}, buf1);
    Assertions.assertEquals(2, wrapper.getContentSize());
    Assertions.assertFalse(wrapper.isContentExceedLimit());

    // read 1 more byte (now at 3, equals limit)
    int r2 = is.read(buf1); // buffer has 2 bytes, read 1
    // this could read 1 or 2 depending on stream state
    // let's just verify we can continue reading
    Assertions.assertTrue(r2 > 0);

    // read remaining
    int totalRead = r1 + r2;
    byte[] remaining = new byte[input.length - totalRead];
    int remainingRead = 0;
    while (remainingRead < remaining.length) {
      int r = is.read(remaining, remainingRead, remaining.length - remainingRead);
      if (r == -1) break;
      remainingRead += r;
    }
    totalRead += remainingRead;

    Assertions.assertEquals(input.length, totalRead);
    Assertions.assertEquals(input.length, wrapper.getContentSize());
    Assertions.assertTrue(wrapper.isContentExceedLimit());
    Assertions.assertEquals(0, wrapper.getContentAsByteArray().length);
  }

  // ==================== 9. form POST: getParameterMap triggers parameter caching ====================

  @Test
  void testFormPost_parameterMap_triggersCaching() throws Exception {
    FakeHttpServletRequest fake = new FakeHttpServletRequest(new byte[0]);
    fake.setContentType("application/x-www-form-urlencoded");
    fake.setMethod("POST");
    Map<String, String[]> params = new HashMap<>();
    params.put("nodeType", new String[]{"3"});
    params.put("name", new String[]{"hello"});
    fake.setParameterMap(params);
    JavaxContentCachingRequestWrapper wrapper =
            new JavaxContentCachingRequestWrapper(fake, 500);

    // any parameter access triggers caching
    wrapper.getParameterMap();

    // cache should contain URL-encoded form data
    String cacheContent = new String(wrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
    Assertions.assertTrue(cacheContent.length() > 0);
    Assertions.assertTrue(cacheContent.contains("nodeType=3"));
    Assertions.assertTrue(cacheContent.contains("name=hello"));
    Assertions.assertFalse(wrapper.isContentExceedLimit());
  }

  // ==================== 10. form POST: getParameter() triggers caching ====================

  @Test
  void testFormPost_getParameter_triggersCaching() throws Exception {
    FakeHttpServletRequest fake = new FakeHttpServletRequest(new byte[0]);
    fake.setContentType("application/x-www-form-urlencoded");
    fake.setMethod("POST");
    Map<String, String[]> params = new HashMap<>();
    params.put("a", new String[]{"1"});
    fake.setParameterMap(params);
    JavaxContentCachingRequestWrapper wrapper =
            new JavaxContentCachingRequestWrapper(fake, 500);

    wrapper.getParameter("a");

    String cacheContent = new String(wrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
    Assertions.assertEquals(URLEncoder.encode("a", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8"), cacheContent);
    Assertions.assertTrue(cacheContent.length() > 0);
    Assertions.assertFalse(wrapper.isContentExceedLimit());
  }

  // ==================== 11. form POST: parameter with null value ====================

  @Test
  void testFormPost_parameterWithNullValue() throws Exception {
    FakeHttpServletRequest fake = new FakeHttpServletRequest(new byte[0]);
    fake.setContentType("application/x-www-form-urlencoded");
    fake.setMethod("POST");
    Map<String, String[]> params = new HashMap<>();
    params.put("key", new String[]{null});
    fake.setParameterMap(params);
    JavaxContentCachingRequestWrapper wrapper =
            new JavaxContentCachingRequestWrapper(fake, 500);

    wrapper.getParameterMap();

    // null value writes only the name (no "=")
    String cacheContent = new String(wrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
    Assertions.assertEquals(URLEncoder.encode("key", "UTF-8"), cacheContent);
  }

  // ==================== 12. form POST: params exceed cache limit ====================

  @Test
  void testFormPost_paramsExceedCacheLimit() throws Exception {
    FakeHttpServletRequest fake = new FakeHttpServletRequest(new byte[0]);
    fake.setContentType("application/x-www-form-urlencoded");
    fake.setMethod("POST");
    Map<String, String[]> params = new HashMap<>();
    // long value exceeds small cache limit
    params.put("key", new String[]{"a-long-value-that-exceeds-cache-limit"});
    fake.setParameterMap(params);
    int cacheLimit = 5;
    JavaxContentCachingRequestWrapper wrapper =
            new JavaxContentCachingRequestWrapper(fake, cacheLimit);

    wrapper.getParameterMap();

    Assertions.assertTrue(wrapper.isContentExceedLimit());
    Assertions.assertEquals(0, wrapper.getContentAsByteArray().length);
  }

  // ==================== 13. not triggered when not POST ====================

  @Test
  void testFormPost_notTriggeredForGet() throws Exception {
    FakeHttpServletRequest fake = new FakeHttpServletRequest(new byte[0]);
    fake.setContentType("application/x-www-form-urlencoded");
    fake.setMethod("GET"); // GET, not POST
    Map<String, String[]> params = new HashMap<>();
    params.put("a", new String[]{"1"});
    fake.setParameterMap(params);
    JavaxContentCachingRequestWrapper wrapper =
            new JavaxContentCachingRequestWrapper(fake, 500);

    wrapper.getParameterMap();

    // isFormPost() returns false → no caching
    Assertions.assertEquals(0, wrapper.getContentAsByteArray().length);
    Assertions.assertEquals(0, wrapper.getContentSize());
  }

  // ==================== 14. not triggered after stream has been read ====================

  @Test
  void testFormPost_notTriggeredAfterStreamRead() throws Exception {
    byte[] input = "some-body-data".getBytes(StandardCharsets.UTF_8);
    FakeHttpServletRequest fake = new FakeHttpServletRequest(input);
    fake.setContentType("application/x-www-form-urlencoded");
    fake.setMethod("POST");
    Map<String, String[]> params = new HashMap<>();
    params.put("a", new String[]{"1"});
    fake.setParameterMap(params);
    JavaxContentCachingRequestWrapper wrapper =
            new JavaxContentCachingRequestWrapper(fake, 500);

    // read from stream first → contentWritten > 0
    ServletInputStream is = wrapper.getInputStream();
    byte[] buf = new byte[100];
    int len = is.read(buf);
    Assertions.assertEquals(input.length, len);

    String before = new String(wrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
    // call getParameter — should NOT overwrite stream content
    wrapper.getParameterMap();

    // cache still has the stream content, not parameter content
    String after = new String(wrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
    Assertions.assertEquals(before, after);
    Assertions.assertTrue(after.contains("some-body-data"));
    Assertions.assertFalse(after.contains("a=1"));
  }

  // ==================== 15. repeated calls to getParameterMap() do not duplicate cache ====================

  @Test
  void testFormPost_repeatedCalls_dontDuplicateCache() throws Exception {
    FakeHttpServletRequest fake = new FakeHttpServletRequest(new byte[0]);
    fake.setContentType("application/x-www-form-urlencoded");
    fake.setMethod("POST");
    Map<String, String[]> params = new HashMap<>();
    params.put("a", new String[]{"1"});
    params.put("b", new String[]{"2"});
    fake.setParameterMap(params);
    JavaxContentCachingRequestWrapper wrapper =
            new JavaxContentCachingRequestWrapper(fake, 500);

    // first call writes params to cache
    wrapper.getParameterMap();
    byte[] firstBytes = wrapper.getContentAsByteArray().clone();
    int firstLen = firstBytes.length;
    Assertions.assertTrue(firstLen > 0);

    // second call: guard `contentWritten == 0` is false → skip write
    wrapper.getParameterMap();
    Assertions.assertArrayEquals(firstBytes, wrapper.getContentAsByteArray());
    Assertions.assertEquals(firstLen, wrapper.getContentSize());

    // also test cross-method: getParameter() after getParameterMap() doesn't append
    wrapper.getParameter("a");
    Assertions.assertArrayEquals(firstBytes, wrapper.getContentAsByteArray());
    Assertions.assertEquals(firstLen, wrapper.getContentSize());

    // getParameterNames() also doesn't append
    wrapper.getParameterNames();
    Assertions.assertArrayEquals(firstBytes, wrapper.getContentAsByteArray());
    Assertions.assertEquals(firstLen, wrapper.getContentSize());
  }

}
