package cn.addenda.porttrail.common.test.pojo.servlet;

import cn.addenda.porttrail.common.pojo.LocaleData;
import cn.addenda.porttrail.common.pojo.servlet.bo.AbstractServletExecution;
import cn.addenda.porttrail.common.pojo.servlet.bo.ServletExecution;
import cn.addenda.porttrail.common.pojo.servlet.bo.ServletRequestBo;
import cn.addenda.porttrail.common.pojo.servlet.dto.AbstractServletDto;
import cn.addenda.porttrail.common.pojo.servlet.dto.ServletRequestDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

class ServletRequestTest {

  @Test
  void testDtoFromBo() {
    Map<String, List<String>> headers = new HashMap<>();
    headers.put("Content-Type", Collections.singletonList("application/json"));
    LocaleData locale = new LocaleData("zh", "CN", "");

    ServletRequestBo bo = new ServletRequestBo("exec-001");
    bo.setVersion("HTTP/1.1");
    bo.setScheme("https");
    bo.setMethod("POST");
    bo.setUri("/api/login");
    bo.setQueryString("user=admin");
    bo.setContentType("application/json");
    bo.setCharsetEncoding("UTF-8");
    bo.setHeaderMap(headers);
    bo.setDatetime(1000000L);
    bo.setAllContentLength(30);
    bo.setContentLength(30);
    bo.setLocale(locale);
    bo.setBody("{\"user\":\"admin\",\"pass\":\"123\"}");

    ServletRequestDto dto = new ServletRequestDto(bo);

    Assertions.assertEquals("exec-001", dto.getExecutionId());
    Assertions.assertEquals("HTTP/1.1", dto.getVersion());
    Assertions.assertEquals("https", dto.getScheme());
    Assertions.assertEquals("POST", dto.getMethod());
    Assertions.assertEquals("/api/login", dto.getUri());
    Assertions.assertEquals("user=admin", dto.getQueryString());
    Assertions.assertEquals("application/json", dto.getContentType());
    Assertions.assertEquals("UTF-8", dto.getCharsetEncoding());
    Assertions.assertEquals(1, dto.getHeaderMap().size());
    Assertions.assertEquals(1000000L, dto.getDatetime());
    Assertions.assertEquals(30, dto.getAllContentLength());
    Assertions.assertEquals(30, dto.getContentLength());
    Assertions.assertEquals("zh", dto.getLocale().getLanguage());
  }

  @Test
  void testBoFromDto() {
    ServletRequestDto dto = new ServletRequestDto("exec-002");
    dto.setVersion("HTTP/1.1");
    dto.setScheme("http");
    dto.setMethod("GET");
    dto.setUri("/api/health");
    dto.setQueryString(null);
    dto.setContentType(null);
    dto.setCharsetEncoding(null);
    dto.setHeaderMap(new HashMap<>());
    dto.setDatetime(2000000L);
    dto.setAllContentLength(-1);
    dto.setContentLength(0);
    dto.setLocale(new cn.addenda.porttrail.common.pojo.LocaleDataDto(new LocaleData("en", "US", "")));
    dto.setBody(null);

    ServletRequestBo bo = new ServletRequestBo(dto);

    Assertions.assertEquals("exec-002", bo.getExecutionId());
    Assertions.assertEquals("HTTP/1.1", bo.getVersion());
    Assertions.assertEquals("http", bo.getScheme());
    Assertions.assertEquals("GET", bo.getMethod());
    Assertions.assertEquals("/api/health", bo.getUri());
    Assertions.assertNull(bo.getQueryString());
    Assertions.assertNull(bo.getContentType());
    Assertions.assertNull(bo.getCharsetEncoding());
    Assertions.assertEquals(2000000L, bo.getDatetime());
    Assertions.assertEquals(-1, bo.getAllContentLength());
    Assertions.assertEquals(0, bo.getContentLength());
    Assertions.assertEquals("en", bo.getLocale().getLanguage());
    Assertions.assertEquals(ServletExecution.SERVLET_EXECUTION_TYPE_REQUEST, bo.getServletExecutionType());
  }

  @Test
  void testBodySentinelBoToDto() {
    ServletRequestBo bo = new ServletRequestBo("exec-s1");
    bo.setBody(AbstractServletExecution.UNSUPPORTED_CONTENT_TYPE);
    ServletRequestDto dto = new ServletRequestDto(bo);
    Assertions.assertArrayEquals(AbstractServletDto.getUNSUPPORTED_CONTENT_TYPE(), dto.getBody());

    bo.setBody(AbstractServletExecution.UNSUPPORTED_CHARSET_ENCODING);
    dto = new ServletRequestDto(bo);
    Assertions.assertArrayEquals(AbstractServletDto.getUNSUPPORTED_CHARSET_ENCODING(), dto.getBody());

    bo.setBody(AbstractServletExecution.BODY_EMPTY);
    dto = new ServletRequestDto(bo);
    Assertions.assertArrayEquals(AbstractServletDto.getBODY_EMPTY(), dto.getBody());

    bo.setBody(AbstractServletExecution.BODY_EXCEED_LENGTH);
    dto = new ServletRequestDto(bo);
    Assertions.assertArrayEquals(AbstractServletDto.getBODY_EXCEED_LENGTH(), dto.getBody());

    bo.setBody(ServletRequestBo.BODY_BYTE_ARRAY);
    dto = new ServletRequestDto(bo);
    Assertions.assertArrayEquals(ServletRequestDto.getBODY_BYTE_ARRAY(), dto.getBody());
  }

  @Test
  void testBodySentinelDtoToBo() {
    ServletRequestDto dto = new ServletRequestDto("exec-s2");
    dto.setBody(AbstractServletDto.getUNSUPPORTED_CONTENT_TYPE());
    ServletRequestBo bo = new ServletRequestBo(dto);
    Assertions.assertEquals(AbstractServletExecution.UNSUPPORTED_CONTENT_TYPE, bo.getBody());

    dto.setBody(AbstractServletDto.getUNSUPPORTED_CHARSET_ENCODING());
    bo = new ServletRequestBo(dto);
    Assertions.assertEquals(AbstractServletExecution.UNSUPPORTED_CHARSET_ENCODING, bo.getBody());

    dto.setBody(AbstractServletDto.getBODY_EMPTY());
    bo = new ServletRequestBo(dto);
    Assertions.assertEquals(AbstractServletExecution.BODY_EMPTY, bo.getBody());

    dto.setBody(AbstractServletDto.getBODY_EXCEED_LENGTH());
    bo = new ServletRequestBo(dto);
    Assertions.assertEquals(AbstractServletExecution.BODY_EXCEED_LENGTH, bo.getBody());

    dto.setBody(ServletRequestDto.getBODY_BYTE_ARRAY());
    bo = new ServletRequestBo(dto);
    Assertions.assertEquals(ServletRequestBo.BODY_BYTE_ARRAY, bo.getBody());
  }

  @Test
  void testBodySentinelRoundTrip() {
    ServletRequestBo bo1 = new ServletRequestBo("exec-s3");
    bo1.setBody(AbstractServletExecution.UNSUPPORTED_CONTENT_TYPE);
    ServletRequestDto dto1 = new ServletRequestDto(bo1);
    ServletRequestBo bo2 = new ServletRequestBo(dto1);
    ServletRequestDto dto2 = new ServletRequestDto(bo2);
    Assertions.assertEquals(bo1.getBody(), bo2.getBody());
    Assertions.assertArrayEquals(dto1.getBody(), dto2.getBody());

    bo1.setBody(AbstractServletExecution.UNSUPPORTED_CHARSET_ENCODING);
    dto1 = new ServletRequestDto(bo1);
    bo2 = new ServletRequestBo(dto1);
    dto2 = new ServletRequestDto(bo2);
    Assertions.assertEquals(bo1.getBody(), bo2.getBody());
    Assertions.assertArrayEquals(dto1.getBody(), dto2.getBody());

    bo1.setBody(AbstractServletExecution.BODY_EMPTY);
    dto1 = new ServletRequestDto(bo1);
    bo2 = new ServletRequestBo(dto1);
    dto2 = new ServletRequestDto(bo2);
    Assertions.assertEquals(bo1.getBody(), bo2.getBody());
    Assertions.assertArrayEquals(dto1.getBody(), dto2.getBody());

    bo1.setBody(AbstractServletExecution.BODY_EXCEED_LENGTH);
    dto1 = new ServletRequestDto(bo1);
    bo2 = new ServletRequestBo(dto1);
    dto2 = new ServletRequestDto(bo2);
    Assertions.assertEquals(bo1.getBody(), bo2.getBody());
    Assertions.assertArrayEquals(dto1.getBody(), dto2.getBody());

    bo1.setBody(ServletRequestBo.BODY_BYTE_ARRAY);
    dto1 = new ServletRequestDto(bo1);
    bo2 = new ServletRequestBo(dto1);
    dto2 = new ServletRequestDto(bo2);
    Assertions.assertEquals(bo1.getBody(), bo2.getBody());
    Assertions.assertArrayEquals(dto1.getBody(), dto2.getBody());
  }

  @Test
  void testFullRoundTrip() {
    Map<String, List<String>> headers = new HashMap<>();
    headers.put("Accept", Collections.singletonList("text/html"));
    LocaleData locale = new LocaleData("ja", "JP", "");

    ServletRequestBo bo1 = new ServletRequestBo("exec-003");
    bo1.setVersion("HTTP/2");
    bo1.setScheme("https");
    bo1.setMethod("PUT");
    bo1.setUri("/api/users/1");
    bo1.setQueryString("v=2");
    bo1.setContentType("text/html");
    bo1.setCharsetEncoding("UTF-8");
    bo1.setHeaderMap(headers);
    bo1.setDatetime(3000000L);
    bo1.setAllContentLength(100);
    bo1.setContentLength(100);
    bo1.setLocale(locale);
    bo1.setBody("<html><body>OK</body></html>");

    ServletRequestDto dto1 = new ServletRequestDto(bo1);
    ServletRequestBo bo2 = new ServletRequestBo(dto1);
    ServletRequestDto dto2 = new ServletRequestDto(bo2);

    Assertions.assertEquals(bo1.getExecutionId(), bo2.getExecutionId());
    Assertions.assertEquals(bo1.getVersion(), bo2.getVersion());
    Assertions.assertEquals(bo1.getScheme(), bo2.getScheme());
    Assertions.assertEquals(bo1.getMethod(), bo2.getMethod());
    Assertions.assertEquals(bo1.getUri(), bo2.getUri());
    Assertions.assertEquals(bo1.getQueryString(), bo2.getQueryString());
    Assertions.assertEquals(bo1.getContentType(), bo2.getContentType());
    Assertions.assertEquals(bo1.getCharsetEncoding(), bo2.getCharsetEncoding());
    Assertions.assertEquals(bo1.getDatetime(), bo2.getDatetime());
    Assertions.assertEquals(bo1.getAllContentLength(), bo2.getAllContentLength());
    Assertions.assertEquals(bo1.getContentLength(), bo2.getContentLength());
    Assertions.assertEquals(bo1.getLocale().getLanguage(), bo2.getLocale().getLanguage());
    Assertions.assertEquals(bo1.getBody(), bo2.getBody());

    Assertions.assertEquals(dto1.getExecutionId(), dto2.getExecutionId());
    Assertions.assertEquals(dto1.getVersion(), dto2.getVersion());
    Assertions.assertEquals(dto1.getScheme(), dto2.getScheme());
    Assertions.assertEquals(dto1.getMethod(), dto2.getMethod());
    Assertions.assertEquals(dto1.getUri(), dto2.getUri());
    Assertions.assertEquals(dto1.getQueryString(), dto2.getQueryString());
    Assertions.assertEquals(dto1.getContentType(), dto2.getContentType());
    Assertions.assertEquals(dto1.getCharsetEncoding(), dto2.getCharsetEncoding());
    Assertions.assertEquals(dto1.getDatetime(), dto2.getDatetime());
    Assertions.assertEquals(dto1.getAllContentLength(), dto2.getAllContentLength());
    Assertions.assertEquals(dto1.getContentLength(), dto2.getContentLength());
  }

}
