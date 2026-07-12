package cn.addenda.porttrail.common.test.pojo.servlet;

import cn.addenda.porttrail.common.entrypoint.EntryPoint;
import cn.addenda.porttrail.common.entrypoint.EntryPointSnapshot;
import cn.addenda.porttrail.common.entrypoint.EntryPointType;
import cn.addenda.porttrail.common.pojo.servlet.bo.AbstractServletExecution;
import cn.addenda.porttrail.common.pojo.servlet.bo.ServletExecution;
import cn.addenda.porttrail.common.pojo.servlet.bo.ServletResponseBo;
import cn.addenda.porttrail.common.pojo.servlet.dto.AbstractServletDto;
import cn.addenda.porttrail.common.pojo.servlet.dto.ServletResponseDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

class ServletResponseTest {

  @Test
  void testDtoFromBo() {
    Map<String, List<String>> headers = new HashMap<>();
    headers.put("Server", Collections.singletonList("Apache-Coyote/1.1"));
    headers.put("Content-Type", Collections.singletonList("application/json"));
    EntryPointSnapshot snapshot = EntryPointSnapshot.of(
            Arrays.asList(EntryPoint.of(EntryPointType.SERVLET_JAVAX, "testDtoFromBo")),
            "main", "trace-sr1", 1L);

    ServletResponseBo bo = new ServletResponseBo("exec-001");
    bo.setContentType("application/json");
    bo.setContentLength(256);
    bo.setDatetime(1000000L);
    bo.setCharsetEncoding("UTF-8");
    bo.setStatus(200);
    bo.setHeaderMap(headers);
    bo.setBody("{\"status\":\"ok\"}");
    bo.setEntryPointSnapshot(snapshot);

    ServletResponseDto dto = new ServletResponseDto(bo);

    Assertions.assertEquals("exec-001", dto.getExecutionId());
    Assertions.assertEquals("application/json", dto.getContentType());
    Assertions.assertEquals(256, dto.getContentLength());
    Assertions.assertEquals(1000000L, dto.getDatetime());
    Assertions.assertEquals("UTF-8", dto.getCharsetEncoding());
    Assertions.assertEquals(200, dto.getStatus());
    Assertions.assertEquals(2, dto.getHeaderMap().size());
    Assertions.assertEquals(snapshot, dto.getEntryPointSnapshot());
  }

  @Test
  void testBoFromDto() {
    Map<String, List<String>> headers = new HashMap<>();
    headers.put("Content-Type", Collections.singletonList("text/plain"));
    EntryPointSnapshot snapshot = EntryPointSnapshot.of(
            Arrays.asList(EntryPoint.of(EntryPointType.SERVLET_JAVAX, "testBoFromDto")),
            "worker", "trace-sr2", 2L);

    ServletResponseDto dto = new ServletResponseDto("exec-002");
    dto.setContentType("text/plain");
    dto.setCharsetEncoding("ISO-8859-1");
    dto.setContentLength(0);
    dto.setDatetime(2000000L);
    dto.setStatus(404);
    dto.setHeaderMap(headers);
    dto.setBody(null);
    dto.setEntryPointSnapshot(snapshot);

    ServletResponseBo bo = new ServletResponseBo(dto);

    Assertions.assertEquals("exec-002", bo.getExecutionId());
    Assertions.assertEquals("text/plain", bo.getContentType());
    Assertions.assertEquals("ISO-8859-1", bo.getCharsetEncoding());
    Assertions.assertEquals(0, bo.getContentLength());
    Assertions.assertEquals(2000000L, bo.getDatetime());
    Assertions.assertEquals(404, bo.getStatus());
    Assertions.assertEquals(1, bo.getHeaderMap().size());
    Assertions.assertEquals(ServletExecution.SERVLET_EXECUTION_TYPE_RESPONSE, bo.getServletExecutionType());
    Assertions.assertEquals(snapshot, bo.getEntryPointSnapshot());
  }

  @Test
  void testBodySentinelBoToDto() {
    EntryPointSnapshot snapshot = EntryPointSnapshot.of(
            Arrays.asList(EntryPoint.of(EntryPointType.SERVLET_JAVAX, "sentinelBoToDto")),
            "sentinel", "trace-ssr1", 10L);
    ServletResponseBo bo = new ServletResponseBo("exec-s1");
    bo.setEntryPointSnapshot(snapshot);
    bo.setBody(AbstractServletExecution.UNSUPPORTED_CONTENT_TYPE);
    ServletResponseDto dto = new ServletResponseDto(bo);
    Assertions.assertArrayEquals(AbstractServletDto.getUNSUPPORTED_CONTENT_TYPE(), dto.getBody());
    Assertions.assertEquals(snapshot, dto.getEntryPointSnapshot());

    bo.setBody(AbstractServletExecution.UNSUPPORTED_CHARSET_ENCODING);
    dto = new ServletResponseDto(bo);
    Assertions.assertArrayEquals(AbstractServletDto.getUNSUPPORTED_CHARSET_ENCODING(), dto.getBody());
    Assertions.assertEquals(snapshot, dto.getEntryPointSnapshot());

    bo.setBody(AbstractServletExecution.BODY_EMPTY);
    dto = new ServletResponseDto(bo);
    Assertions.assertArrayEquals(AbstractServletDto.getBODY_EMPTY(), dto.getBody());
    Assertions.assertEquals(snapshot, dto.getEntryPointSnapshot());

    bo.setBody(AbstractServletExecution.BODY_EXCEED_LENGTH);
    dto = new ServletResponseDto(bo);
    Assertions.assertArrayEquals(AbstractServletDto.getBODY_EXCEED_LENGTH(), dto.getBody());
    Assertions.assertEquals(snapshot, dto.getEntryPointSnapshot());

    bo.setBody(ServletResponseBo.UNKNOWN_FILENAME);
    dto = new ServletResponseDto(bo);
    Assertions.assertArrayEquals(ServletResponseDto.getUNKNOWN_FILENAME(), dto.getBody());
    Assertions.assertEquals(snapshot, dto.getEntryPointSnapshot());
  }

  @Test
  void testBodySentinelDtoToBo() {
    EntryPointSnapshot snapshot = EntryPointSnapshot.of(
            Arrays.asList(EntryPoint.of(EntryPointType.SERVLET_JAVAX, "sentinelDtoToBo")),
            "sentinel", "trace-ssr2", 20L);
    ServletResponseDto dto = new ServletResponseDto("exec-s2");
    dto.setEntryPointSnapshot(snapshot);
    dto.setBody(AbstractServletDto.getUNSUPPORTED_CONTENT_TYPE());
    ServletResponseBo bo = new ServletResponseBo(dto);
    Assertions.assertEquals(AbstractServletExecution.UNSUPPORTED_CONTENT_TYPE, bo.getBody());
    Assertions.assertEquals(snapshot, bo.getEntryPointSnapshot());

    dto.setBody(AbstractServletDto.getUNSUPPORTED_CHARSET_ENCODING());
    bo = new ServletResponseBo(dto);
    Assertions.assertEquals(AbstractServletExecution.UNSUPPORTED_CHARSET_ENCODING, bo.getBody());
    Assertions.assertEquals(snapshot, bo.getEntryPointSnapshot());

    dto.setBody(AbstractServletDto.getBODY_EMPTY());
    bo = new ServletResponseBo(dto);
    Assertions.assertEquals(AbstractServletExecution.BODY_EMPTY, bo.getBody());
    Assertions.assertEquals(snapshot, bo.getEntryPointSnapshot());

    dto.setBody(AbstractServletDto.getBODY_EXCEED_LENGTH());
    bo = new ServletResponseBo(dto);
    Assertions.assertEquals(AbstractServletExecution.BODY_EXCEED_LENGTH, bo.getBody());
    Assertions.assertEquals(snapshot, bo.getEntryPointSnapshot());

    dto.setBody(ServletResponseDto.getUNKNOWN_FILENAME());
    bo = new ServletResponseBo(dto);
    Assertions.assertEquals(ServletResponseBo.UNKNOWN_FILENAME, bo.getBody());
    Assertions.assertEquals(snapshot, bo.getEntryPointSnapshot());
  }

  @Test
  void testBodySentinelRoundTrip() {
    EntryPointSnapshot snapshot = EntryPointSnapshot.of(
            Arrays.asList(EntryPoint.of(EntryPointType.SERVLET_JAVAX, "sentinelRoundTrip")),
            "sentinel", "trace-ssr3", 30L);
    ServletResponseBo bo1 = new ServletResponseBo("exec-s3");
    bo1.setEntryPointSnapshot(snapshot);
    bo1.setBody(AbstractServletExecution.UNSUPPORTED_CONTENT_TYPE);
    ServletResponseDto dto1 = new ServletResponseDto(bo1);
    ServletResponseBo bo2 = new ServletResponseBo(dto1);
    ServletResponseDto dto2 = new ServletResponseDto(bo2);
    Assertions.assertEquals(bo1.getBody(), bo2.getBody());
    Assertions.assertArrayEquals(dto1.getBody(), dto2.getBody());
    Assertions.assertEquals(bo1.getEntryPointSnapshot(), bo2.getEntryPointSnapshot());
    Assertions.assertEquals(dto1.getEntryPointSnapshot(), dto2.getEntryPointSnapshot());

    bo1.setBody(AbstractServletExecution.UNSUPPORTED_CHARSET_ENCODING);
    dto1 = new ServletResponseDto(bo1);
    bo2 = new ServletResponseBo(dto1);
    dto2 = new ServletResponseDto(bo2);
    Assertions.assertEquals(bo1.getBody(), bo2.getBody());
    Assertions.assertArrayEquals(dto1.getBody(), dto2.getBody());
    Assertions.assertEquals(bo1.getEntryPointSnapshot(), bo2.getEntryPointSnapshot());
    Assertions.assertEquals(dto1.getEntryPointSnapshot(), dto2.getEntryPointSnapshot());

    bo1.setBody(AbstractServletExecution.BODY_EMPTY);
    dto1 = new ServletResponseDto(bo1);
    bo2 = new ServletResponseBo(dto1);
    dto2 = new ServletResponseDto(bo2);
    Assertions.assertEquals(bo1.getBody(), bo2.getBody());
    Assertions.assertArrayEquals(dto1.getBody(), dto2.getBody());
    Assertions.assertEquals(bo1.getEntryPointSnapshot(), bo2.getEntryPointSnapshot());
    Assertions.assertEquals(dto1.getEntryPointSnapshot(), dto2.getEntryPointSnapshot());

    bo1.setBody(AbstractServletExecution.BODY_EXCEED_LENGTH);
    dto1 = new ServletResponseDto(bo1);
    bo2 = new ServletResponseBo(dto1);
    dto2 = new ServletResponseDto(bo2);
    Assertions.assertEquals(bo1.getBody(), bo2.getBody());
    Assertions.assertArrayEquals(dto1.getBody(), dto2.getBody());
    Assertions.assertEquals(bo1.getEntryPointSnapshot(), bo2.getEntryPointSnapshot());
    Assertions.assertEquals(dto1.getEntryPointSnapshot(), dto2.getEntryPointSnapshot());

    bo1.setBody(ServletResponseBo.UNKNOWN_FILENAME);
    dto1 = new ServletResponseDto(bo1);
    bo2 = new ServletResponseBo(dto1);
    dto2 = new ServletResponseDto(bo2);
    Assertions.assertEquals(bo1.getBody(), bo2.getBody());
    Assertions.assertArrayEquals(dto1.getBody(), dto2.getBody());
    Assertions.assertEquals(bo1.getEntryPointSnapshot(), bo2.getEntryPointSnapshot());
    Assertions.assertEquals(dto1.getEntryPointSnapshot(), dto2.getEntryPointSnapshot());
  }

  @Test
  void testFullRoundTrip() {
    ServletResponseBo bo1 = new ServletResponseBo("exec-003");
    bo1.setContentType("application/octet-stream");
    bo1.setContentLength(2048);
    bo1.setDatetime(3000000L);
    bo1.setCharsetEncoding("UTF-8");
    bo1.setStatus(500);
    bo1.setHeaderMap(new HashMap<>());
    bo1.setBody("error.log");
    bo1.setEntryPointSnapshot(EntryPointSnapshot.of(
            Arrays.asList(EntryPoint.of(EntryPointType.SERVLET_JAVAX, "responseTrip")),
            "rt-thread", "rt-trace", 88L));

    ServletResponseDto dto1 = new ServletResponseDto(bo1);
    ServletResponseBo bo2 = new ServletResponseBo(dto1);
    ServletResponseDto dto2 = new ServletResponseDto(bo2);

    Assertions.assertEquals(bo1.getExecutionId(), bo2.getExecutionId());
    Assertions.assertEquals(bo1.getContentType(), bo2.getContentType());
    Assertions.assertEquals(bo1.getContentLength(), bo2.getContentLength());
    Assertions.assertEquals(bo1.getDatetime(), bo2.getDatetime());
    Assertions.assertEquals(bo1.getCharsetEncoding(), bo2.getCharsetEncoding());
    Assertions.assertEquals(bo1.getStatus(), bo2.getStatus());
    Assertions.assertEquals(bo1.getHeaderMap(), bo2.getHeaderMap());
    Assertions.assertEquals(bo1.getBody(), bo2.getBody());
    Assertions.assertEquals(bo1.getEntryPointSnapshot(), bo2.getEntryPointSnapshot());

    Assertions.assertEquals(dto1.getExecutionId(), dto2.getExecutionId());
    Assertions.assertEquals(dto1.getContentType(), dto2.getContentType());
    Assertions.assertEquals(dto1.getContentLength(), dto2.getContentLength());
    Assertions.assertEquals(dto1.getDatetime(), dto2.getDatetime());
    Assertions.assertEquals(dto1.getCharsetEncoding(), dto2.getCharsetEncoding());
    Assertions.assertEquals(dto1.getStatus(), dto2.getStatus());
    Assertions.assertEquals(dto1.getHeaderMap(), dto2.getHeaderMap());
    Assertions.assertArrayEquals(dto1.getBody(), dto2.getBody());
    Assertions.assertEquals(dto1.getEntryPointSnapshot(), dto2.getEntryPointSnapshot());
  }

}
