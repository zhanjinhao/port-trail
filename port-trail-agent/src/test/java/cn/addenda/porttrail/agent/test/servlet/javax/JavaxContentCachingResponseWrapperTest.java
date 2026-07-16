package cn.addenda.porttrail.agent.test.servlet.javax;

import cn.addenda.porttrail.agent.transform.interceptor.servlet.javax.JavaxContentCachingResponseWrapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.servlet.ServletOutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

class JavaxContentCachingResponseWrapperTest {

  // ==================== 1. outputStream write - normal ====================

  @Test
  void testOutputStreamWrite_normal() throws Exception {
    FakeHttpServletResponse fake = new FakeHttpServletResponse();
    JavaxContentCachingResponseWrapper wrapper = new JavaxContentCachingResponseWrapper(fake, -1);
    byte[] input = "hello stream!".getBytes(StandardCharsets.UTF_8);

    ServletOutputStream os = wrapper.getOutputStream();
    os.write(input);
    os.flush();

    // client received full data (write-through)
    Assertions.assertArrayEquals(input, fake.getCapturedBytes());
    // cache captured full data
    Assertions.assertArrayEquals(input, wrapper.getContentAsByteArray());
    Assertions.assertEquals(input.length, wrapper.getContentSize());
    Assertions.assertFalse(wrapper.isContentExceedLimit());
  }

  // ==================== 2. outputStream write - overflow ====================

  @Test
  void testOutputStreamWrite_overflow() throws Exception {
    FakeHttpServletResponse fake = new FakeHttpServletResponse();
    int cacheLimit = 5;
    JavaxContentCachingResponseWrapper wrapper =
            new JavaxContentCachingResponseWrapper(fake, cacheLimit);
    byte[] input = "a long text exceeding the limit".getBytes(StandardCharsets.UTF_8);

    ServletOutputStream os = wrapper.getOutputStream();
    os.write(input);
    os.flush();

    // client receives FULL data (write-through is not affected by cache limit)
    Assertions.assertArrayEquals(input, fake.getCapturedBytes());
    // cache is truncated to limit
    Assertions.assertEquals(0, wrapper.getContentAsByteArray().length);
    Assertions.assertEquals(input.length, wrapper.getContentSize());
    Assertions.assertTrue(wrapper.isContentExceedLimit());
  }

  // ==================== 3. writer write - normal ====================

  @Test
  void testWriterWrite_normal() throws Exception {
    FakeHttpServletResponse fake = new FakeHttpServletResponse();
    fake.setCharacterEncoding(StandardCharsets.UTF_8.name());
    JavaxContentCachingResponseWrapper wrapper = new JavaxContentCachingResponseWrapper(fake, -1);
    String text = "hello writer!";

    PrintWriter w = wrapper.getWriter();
    w.write(text);
    w.flush();

    // client received the text (#7 fix: writer path no longer lost)
    String clientText = new String(fake.getCapturedBytes(), fake.getCharacterEncoding());
    Assertions.assertEquals(text, clientText);
    // cache captured the same content
    String cacheText = new String(wrapper.getContentAsByteArray(), fake.getCharacterEncoding());
    Assertions.assertEquals(text, cacheText);
    Assertions.assertFalse(wrapper.isContentExceedLimit());
  }

  // ==================== 4. writer write - overflow ====================

  @Test
  void testWriterWrite_overflow() throws Exception {
    FakeHttpServletResponse fake = new FakeHttpServletResponse();
    fake.setCharacterEncoding(StandardCharsets.UTF_8.name());
    int cacheLimit = 10;
    JavaxContentCachingResponseWrapper wrapper =
            new JavaxContentCachingResponseWrapper(fake, cacheLimit);
    String text = "this is a long piece of text that exceeds the cache limit configured above";

    PrintWriter w = wrapper.getWriter();
    w.write(text);
    w.flush();

    // client receives FULL text
    String clientText = new String(fake.getCapturedBytes(), fake.getCharacterEncoding());
    Assertions.assertEquals(text, clientText);
    // cache truncated
    Assertions.assertEquals(0, wrapper.getContentAsByteArray().length);
    Assertions.assertEquals(text.getBytes(fake.getCharacterEncoding()).length, wrapper.getContentSize());
    Assertions.assertTrue(wrapper.isContentExceedLimit());
  }

  // ==================== 5. sendError – uncommitted, no prior body ====================

  @Test
  void testSendError_noPriorBody() throws Exception {
    FakeHttpServletResponse fake = new FakeHttpServletResponse();
    JavaxContentCachingResponseWrapper wrapper = new JavaxContentCachingResponseWrapper(fake, -1);

    wrapper.sendError(503, "Service Unavailable");

    Assertions.assertEquals(503, fake.getStatus());
    Assertions.assertEquals("Service Unavailable", fake.getErrorMessage());
    // no body was written => client sees empty
    Assertions.assertEquals(0, fake.getCapturedBytes().length);
  }

  // ==================== 6. sendError – uncommitted, with prior body ====================

  @Test
  void testSendError_withPriorBody_noDoubleWrite() throws Exception {
    FakeHttpServletResponse fake = new FakeHttpServletResponse();
    JavaxContentCachingResponseWrapper wrapper = new JavaxContentCachingResponseWrapper(fake, -1);
    byte[] body = "body before error".getBytes(StandardCharsets.UTF_8);

    // write body, then sendError
    wrapper.getOutputStream().write(body);
    wrapper.sendError(400);

    Assertions.assertEquals(400, fake.getStatus());
    // client bytes == the originally written body (NOT doubled by old copyBodyToResponse)
    Assertions.assertArrayEquals(body, fake.getCapturedBytes());
    Assertions.assertFalse(wrapper.isContentExceedLimit());
    Assertions.assertEquals(body.length, wrapper.getContentSize());
  }

  // ==================== 7. sendError – already committed ====================

  @Test
  void testSendError_alreadyCommitted() throws Exception {
    FakeHttpServletResponse fake = new FakeHttpServletResponse();
    JavaxContentCachingResponseWrapper wrapper = new JavaxContentCachingResponseWrapper(fake, -1);

    // write to commit, then explicitly mark committed
    wrapper.getOutputStream().write("x".getBytes(StandardCharsets.UTF_8));
    fake.setCommitted(true);

    // should not throw; falls back to setStatus silently
    Assertions.assertDoesNotThrow(() -> wrapper.sendError(500));
    Assertions.assertEquals(500, fake.getStatus());
  }

  // ==================== 8. sendRedirect ====================

  @Test
  void testSendRedirect_noDoubleWrite() throws Exception {
    FakeHttpServletResponse fake = new FakeHttpServletResponse();
    JavaxContentCachingResponseWrapper wrapper = new JavaxContentCachingResponseWrapper(fake, -1);
    byte[] body = "some body before redirect".getBytes(StandardCharsets.UTF_8);

    wrapper.getOutputStream().write(body);
    wrapper.sendRedirect("/somewhere");

    // client bytes == body written once (no double-write from old copyBodyToResponse)
    Assertions.assertArrayEquals(body, fake.getCapturedBytes());
    Assertions.assertEquals("/somewhere", fake.getRedirectLocation());
  }

  // ==================== 9. outputStream: 恰好等于 limit ====================

  @Test
  void testOutputStreamWrite_exactLimit() throws Exception {
    FakeHttpServletResponse fake = new FakeHttpServletResponse();
    int cacheLimit = 5;
    JavaxContentCachingResponseWrapper wrapper =
            new JavaxContentCachingResponseWrapper(fake, cacheLimit);
    byte[] input = {0, 1, 2, 3, 4}; // exactly 5 bytes

    ServletOutputStream os = wrapper.getOutputStream();
    os.write(input);
    os.flush();

    Assertions.assertArrayEquals(input, fake.getCapturedBytes());
    Assertions.assertEquals(input.length, wrapper.getContentAsByteArray().length);
    Assertions.assertEquals(input.length, wrapper.getContentSize());
    Assertions.assertFalse(wrapper.isContentExceedLimit());
  }

  // ==================== 10. outputStream: limit + 1 立刻清空缓存 ====================

  @Test
  void testOutputStreamWrite_oneByteOverLimit() throws Exception {
    FakeHttpServletResponse fake = new FakeHttpServletResponse();
    int cacheLimit = 5;
    JavaxContentCachingResponseWrapper wrapper =
            new JavaxContentCachingResponseWrapper(fake, cacheLimit);
    byte[] input = {0, 1, 2, 3, 4, 5}; // 6 bytes > limit

    ServletOutputStream os = wrapper.getOutputStream();
    os.write(input);
    os.flush();

    // client receives full data
    Assertions.assertArrayEquals(input, fake.getCapturedBytes());
    // cache cleared entirely once contentWritten exceeds limit
    Assertions.assertEquals(0, wrapper.getContentAsByteArray().length);
    Assertions.assertTrue(wrapper.isContentExceedLimit());
    // getContentSize reflects total written, not just cached
    Assertions.assertEquals(input.length, wrapper.getContentSize());
  }

  // ==================== 11. 逐字节递增写入，跨越 limit ====================

  @Test
  void testIncrementalWrites_crossLimit() throws Exception {
    FakeHttpServletResponse fake = new FakeHttpServletResponse();
    int cacheLimit = 5;
    JavaxContentCachingResponseWrapper wrapper =
            new JavaxContentCachingResponseWrapper(fake, cacheLimit);

    ServletOutputStream os = wrapper.getOutputStream();

    // write 5 bytes one-by-one: should all be cached, not exceeded yet
    for (int i = 0; i < cacheLimit; i++) {
      os.write(i);
    }
    os.flush();
    Assertions.assertEquals(cacheLimit, wrapper.getContentAsByteArray().length);
    Assertions.assertFalse(wrapper.isContentExceedLimit());

    // 6th byte: crosses limit -> cache cleared
    os.write(99);
    os.flush();
    Assertions.assertEquals(0, wrapper.getContentAsByteArray().length);
    Assertions.assertTrue(wrapper.isContentExceedLimit());

    // 7th byte: still exceeded, cache stays empty
    os.write(100);
    os.flush();
    Assertions.assertEquals(0, wrapper.getContentAsByteArray().length);
    Assertions.assertTrue(wrapper.isContentExceedLimit());

    // client gets all 7 bytes
    Assertions.assertEquals(cacheLimit + 2, fake.getCapturedBytes().length);
    Assertions.assertEquals(cacheLimit + 2, wrapper.getContentSize());
  }

  // ==================== 12. getContentSize returns total written (not cached) ====================

  @Test
  void testGetContentSize_tracksTotalAfterExceed() throws Exception {
    FakeHttpServletResponse fake = new FakeHttpServletResponse();
    int cacheLimit = 3;
    JavaxContentCachingResponseWrapper wrapper =
            new JavaxContentCachingResponseWrapper(fake, cacheLimit);
    byte[] input = "abcdefgh".getBytes(StandardCharsets.UTF_8); // 8 bytes

    wrapper.getOutputStream().write(input);
    wrapper.getOutputStream().flush();

    // total 8 bytes tracked
    Assertions.assertEquals(8, wrapper.getContentSize());
    // but cache was cleared (8 > 3)
    Assertions.assertEquals(0, wrapper.getContentAsByteArray().length);
    Assertions.assertTrue(wrapper.isContentExceedLimit());
  }

  // ==================== 13. writer: 恰好等于 limit ====================

  @Test
  void testWriterWrite_exactLimit() throws Exception {
    FakeHttpServletResponse fake = new FakeHttpServletResponse();
    fake.setCharacterEncoding(StandardCharsets.US_ASCII.name());
    int cacheLimit = 5;
    JavaxContentCachingResponseWrapper wrapper =
            new JavaxContentCachingResponseWrapper(fake, cacheLimit);
    String text = "12345"; // exactly 5 ASCII chars = 5 bytes

    PrintWriter w = wrapper.getWriter();
    w.write(text);
    w.flush();

    String clientText = new String(fake.getCapturedBytes(), fake.getCharacterEncoding());
    Assertions.assertEquals(text, clientText);
    String cacheText = new String(wrapper.getContentAsByteArray(), fake.getCharacterEncoding());
    Assertions.assertEquals(text, cacheText);
    Assertions.assertFalse(wrapper.isContentExceedLimit());
  }

  // ==================== 14. writer: limit + 1 ====================

  @Test
  void testWriterWrite_oneByteOverLimit() throws Exception {
    FakeHttpServletResponse fake = new FakeHttpServletResponse();
    fake.setCharacterEncoding(StandardCharsets.US_ASCII.name());
    int cacheLimit = 5;
    JavaxContentCachingResponseWrapper wrapper =
            new JavaxContentCachingResponseWrapper(fake, cacheLimit);
    String text = "123456"; // 6 ASCII chars

    PrintWriter w = wrapper.getWriter();
    w.write(text);
    w.flush();

    String clientText = new String(fake.getCapturedBytes(), fake.getCharacterEncoding());
    Assertions.assertEquals(text, clientText);
    Assertions.assertEquals(0, wrapper.getContentAsByteArray().length);
    Assertions.assertTrue(wrapper.isContentExceedLimit());
    Assertions.assertEquals(6, wrapper.getContentSize());
  }

  // ==================== 15. 无限制（-1），缓存不清空 ====================

  @Test
  void testNoLimit_cacheNeverCleared() throws Exception {
    FakeHttpServletResponse fake = new FakeHttpServletResponse();
    JavaxContentCachingResponseWrapper wrapper =
            new JavaxContentCachingResponseWrapper(fake, -1);
    byte[] input = "some long data that would normally exceed a small limit"
            .getBytes(StandardCharsets.UTF_8);

    ServletOutputStream os = wrapper.getOutputStream();
    os.write(input);
    os.flush();

    Assertions.assertArrayEquals(input, fake.getCapturedBytes());
    Assertions.assertArrayEquals(input, wrapper.getContentAsByteArray());
    Assertions.assertEquals(input.length, wrapper.getContentSize());
    Assertions.assertFalse(wrapper.isContentExceedLimit());
  }

  // ==================== 16. 超限后继续写入，缓存保持清空 ====================

  @Test
  void testSubsequentWriteAfterExceed_staysCleared() throws Exception {
    FakeHttpServletResponse fake = new FakeHttpServletResponse();
    int cacheLimit = 3;
    JavaxContentCachingResponseWrapper wrapper =
            new JavaxContentCachingResponseWrapper(fake, cacheLimit);
    byte[] first = "1234".getBytes(StandardCharsets.UTF_8);  // 4 bytes, exceeds limit
    byte[] second = "ab".getBytes(StandardCharsets.UTF_8);   // 2 more bytes

    ServletOutputStream os = wrapper.getOutputStream();
    os.write(first);
    os.flush();

    Assertions.assertEquals(0, wrapper.getContentAsByteArray().length);
    Assertions.assertTrue(wrapper.isContentExceedLimit());

    os.write(second);
    os.flush();

    Assertions.assertEquals(0, wrapper.getContentAsByteArray().length);
    Assertions.assertTrue(wrapper.isContentExceedLimit());

    // client receives all 6 bytes
    Assertions.assertEquals(6, fake.getCapturedBytes().length);
    Assertions.assertEquals(6, wrapper.getContentSize());
    Assertions.assertArrayEquals("1234ab".getBytes(StandardCharsets.UTF_8), fake.getCapturedBytes());
  }
}
