package cn.addenda.porttrail.common.test.pojo.servlet;

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

    ServletResponseBo bo = new ServletResponseBo("exec-001");
    bo.setContentType("application/json");
    bo.setContentLength(256);
    bo.setDatetime(1000000L);
    bo.setCharsetEncoding("UTF-8");
    bo.setStatus(200);
    bo.setHeaderMap(headers);
    bo.setBody("{\"status\":\"ok\"}");

    ServletResponseDto dto = new ServletResponseDto(bo);

    Assertions.assertEquals("exec-001", dto.getExecutionId());
    Assertions.assertEquals("application/json", dto.getContentType());
    Assertions.assertEquals(256, dto.getContentLength());
    Assertions.assertEquals(1000000L, dto.getDatetime());
    Assertions.assertEquals("UTF-8", dto.getCharsetEncoding());
    Assertions.assertEquals(200, dto.getStatus());
    Assertions.assertEquals(2, dto.getHeaderMap().size());
  }

  @Test
  void testBoFromDto() {
    Map<String, List<String>> headers = new HashMap<>();
    headers.put("Content-Type", Collections.singletonList("text/plain"));

    ServletResponseDto dto = new ServletResponseDto("exec-002");
    dto.setContentType("text/plain");
    dto.setCharsetEncoding("ISO-8859-1");
    dto.setContentLength(0);
    dto.setDatetime(2000000L);
    dto.setStatus(404);
    dto.setHeaderMap(headers);
    dto.setBody(null);

    ServletResponseBo bo = new ServletResponseBo(dto);

    Assertions.assertEquals("exec-002", bo.getExecutionId());
    Assertions.assertEquals("text/plain", bo.getContentType());
    Assertions.assertEquals("ISO-8859-1", bo.getCharsetEncoding());
    Assertions.assertEquals(0, bo.getContentLength());
    Assertions.assertEquals(2000000L, bo.getDatetime());
    Assertions.assertEquals(404, bo.getStatus());
    Assertions.assertEquals(1, bo.getHeaderMap().size());
    Assertions.assertEquals(ServletExecution.SERVLET_EXECUTION_TYPE_RESPONSE, bo.getServletExecutionType());
  }

  @Test
  void testBodySentinelBoToDto() {
    ServletResponseBo bo = new ServletResponseBo("exec-s1");
    bo.setBody(AbstractServletExecution.UNSUPPORTED_CONTENT_TYPE);
    ServletResponseDto dto = new ServletResponseDto(bo);
    Assertions.assertArrayEquals(AbstractServletDto.getUNSUPPORTED_CONTENT_TYPE(), dto.getBody());

    bo.setBody(AbstractServletExecution.UNSUPPORTED_CHARSET_ENCODING);
    dto = new ServletResponseDto(bo);
    Assertions.assertArrayEquals(AbstractServletDto.getUNSUPPORTED_CHARSET_ENCODING(), dto.getBody());

    bo.setBody(AbstractServletExecution.BODY_EMPTY);
    dto = new ServletResponseDto(bo);
    Assertions.assertArrayEquals(AbstractServletDto.getBODY_EMPTY(), dto.getBody());

    bo.setBody(AbstractServletExecution.BODY_EXCEED_LENGTH);
    dto = new ServletResponseDto(bo);
    Assertions.assertArrayEquals(AbstractServletDto.getBODY_EXCEED_LENGTH(), dto.getBody());

    bo.setBody(ServletResponseBo.UNKNOWN_FILENAME);
    dto = new ServletResponseDto(bo);
    Assertions.assertArrayEquals(ServletResponseDto.getUNKNOWN_FILENAME(), dto.getBody());
  }

  @Test
  void testBodySentinelDtoToBo() {
    ServletResponseDto dto = new ServletResponseDto("exec-s2");
    dto.setBody(AbstractServletDto.getUNSUPPORTED_CONTENT_TYPE());
    ServletResponseBo bo = new ServletResponseBo(dto);
    Assertions.assertEquals(AbstractServletExecution.UNSUPPORTED_CONTENT_TYPE, bo.getBody());

    dto.setBody(AbstractServletDto.getUNSUPPORTED_CHARSET_ENCODING());
    bo = new ServletResponseBo(dto);
    Assertions.assertEquals(AbstractServletExecution.UNSUPPORTED_CHARSET_ENCODING, bo.getBody());

    dto.setBody(AbstractServletDto.getBODY_EMPTY());
    bo = new ServletResponseBo(dto);
    Assertions.assertEquals(AbstractServletExecution.BODY_EMPTY, bo.getBody());

    dto.setBody(AbstractServletDto.getBODY_EXCEED_LENGTH());
    bo = new ServletResponseBo(dto);
    Assertions.assertEquals(AbstractServletExecution.BODY_EXCEED_LENGTH, bo.getBody());

    dto.setBody(ServletResponseDto.getUNKNOWN_FILENAME());
    bo = new ServletResponseBo(dto);
    Assertions.assertEquals(ServletResponseBo.UNKNOWN_FILENAME, bo.getBody());
  }

  @Test
  void testBodySentinelRoundTrip() {
    ServletResponseBo bo1 = new ServletResponseBo("exec-s3");
    bo1.setBody(AbstractServletExecution.UNSUPPORTED_CONTENT_TYPE);
    ServletResponseDto dto1 = new ServletResponseDto(bo1);
    ServletResponseBo bo2 = new ServletResponseBo(dto1);
    ServletResponseDto dto2 = new ServletResponseDto(bo2);
    Assertions.assertEquals(bo1.getBody(), bo2.getBody());
    Assertions.assertArrayEquals(dto1.getBody(), dto2.getBody());

    bo1.setBody(AbstractServletExecution.UNSUPPORTED_CHARSET_ENCODING);
    dto1 = new ServletResponseDto(bo1);
    bo2 = new ServletResponseBo(dto1);
    dto2 = new ServletResponseDto(bo2);
    Assertions.assertEquals(bo1.getBody(), bo2.getBody());
    Assertions.assertArrayEquals(dto1.getBody(), dto2.getBody());

    bo1.setBody(AbstractServletExecution.BODY_EMPTY);
    dto1 = new ServletResponseDto(bo1);
    bo2 = new ServletResponseBo(dto1);
    dto2 = new ServletResponseDto(bo2);
    Assertions.assertEquals(bo1.getBody(), bo2.getBody());
    Assertions.assertArrayEquals(dto1.getBody(), dto2.getBody());

    bo1.setBody(AbstractServletExecution.BODY_EXCEED_LENGTH);
    dto1 = new ServletResponseDto(bo1);
    bo2 = new ServletResponseBo(dto1);
    dto2 = new ServletResponseDto(bo2);
    Assertions.assertEquals(bo1.getBody(), bo2.getBody());
    Assertions.assertArrayEquals(dto1.getBody(), dto2.getBody());

    bo1.setBody(ServletResponseBo.UNKNOWN_FILENAME);
    dto1 = new ServletResponseDto(bo1);
    bo2 = new ServletResponseBo(dto1);
    dto2 = new ServletResponseDto(bo2);
    Assertions.assertEquals(bo1.getBody(), bo2.getBody());
    Assertions.assertArrayEquals(dto1.getBody(), dto2.getBody());
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

    Assertions.assertEquals(dto1.getExecutionId(), dto2.getExecutionId());
    Assertions.assertEquals(dto1.getContentType(), dto2.getContentType());
    Assertions.assertEquals(dto1.getContentLength(), dto2.getContentLength());
    Assertions.assertEquals(dto1.getDatetime(), dto2.getDatetime());
    Assertions.assertEquals(dto1.getCharsetEncoding(), dto2.getCharsetEncoding());
    Assertions.assertEquals(dto1.getStatus(), dto2.getStatus());
    Assertions.assertEquals(dto1.getHeaderMap(), dto2.getHeaderMap());
  }

}
