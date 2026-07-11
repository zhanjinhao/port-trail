package cn.addenda.porttrail.common.test.pojo.httpclient;

import cn.addenda.porttrail.common.entrypoint.EntryPoint;
import cn.addenda.porttrail.common.entrypoint.EntryPointSnapshot;
import cn.addenda.porttrail.common.entrypoint.EntryPointType;
import cn.addenda.porttrail.common.pojo.LocaleData;
import cn.addenda.porttrail.common.pojo.httpclient.bo.AbstractHttpClientExecution;
import cn.addenda.porttrail.common.pojo.httpclient.bo.HttpClientExecution;
import cn.addenda.porttrail.common.pojo.httpclient.bo.HttpClientRequestBo;
import cn.addenda.porttrail.common.pojo.httpclient.dto.AbstractHttpClientDto;
import cn.addenda.porttrail.common.pojo.httpclient.dto.HttpClientRequestDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

class HttpClientRequestTest {

  @Test
  void testDtoFromBo() {
    EntryPointSnapshot snapshot = EntryPointSnapshot.of(
            Arrays.asList(EntryPoint.of(EntryPointType.REMOTE_HTTPCLIENT, "callApi")),
            "main", "trace-h1", 1L);
    Map<String, List<String>> headers = new HashMap<>();
    headers.put("Content-Type", Collections.singletonList("application/json"));
    LocaleData locale = new LocaleData("zh", "CN", "");

    HttpClientRequestBo bo = new HttpClientRequestBo("exec-001", "myClient");
    bo.setVersion("HTTP/1.1");
    bo.setScheme("https");
    bo.setMethod("POST");
    bo.setUri("/api/users");
    bo.setQueryString("page=1");
    bo.setContentType("application/json");
    bo.setCharsetEncoding("UTF-8");
    bo.setHeaderMap(headers);
    bo.setDatetime(1000000L);
    bo.setAllContentLength(50);
    bo.setContentLength(50);
    bo.setLocale(locale);
    bo.setBody("{\"name\":\"test\"}");
    bo.setEntryPointSnapshot(snapshot);

    HttpClientRequestDto dto = new HttpClientRequestDto(bo);

    Assertions.assertEquals("exec-001", dto.getExecutionId());
    Assertions.assertEquals("myClient", dto.getClientName());
    Assertions.assertEquals("HTTP/1.1", dto.getVersion());
    Assertions.assertEquals("https", dto.getScheme());
    Assertions.assertEquals("POST", dto.getMethod());
    Assertions.assertEquals("/api/users", dto.getUri());
    Assertions.assertEquals("page=1", dto.getQueryString());
    Assertions.assertEquals("application/json", dto.getContentType());
    Assertions.assertEquals("UTF-8", dto.getCharsetEncoding());
    Assertions.assertEquals(1, dto.getHeaderMap().size());
    Assertions.assertEquals(1000000L, dto.getDatetime());
    Assertions.assertEquals(50, dto.getAllContentLength());
    Assertions.assertEquals(50, dto.getContentLength());
    Assertions.assertEquals("zh", dto.getLocale().getLanguage());
    Assertions.assertEquals(snapshot, dto.getEntryPointSnapshot());
  }

  @Test
  void testBoFromDto() {
    EntryPointSnapshot snapshot = EntryPointSnapshot.of(
            Arrays.asList(EntryPoint.of(EntryPointType.REMOTE_HTTPCLIENT, "getRequest")),
            "worker", "trace-h2", 2L);

    HttpClientRequestDto dto = new HttpClientRequestDto("exec-002", "apiClient");
    dto.setVersion("HTTP/1.1");
    dto.setScheme("http");
    dto.setMethod("GET");
    dto.setUri("/api/health");
    dto.setQueryString(null);
    dto.setContentType(null);
    dto.setCharsetEncoding(null);
    Map<String, List<String>> headers = new HashMap<>();
    headers.put("Accept", Collections.singletonList("text/plain"));
    dto.setHeaderMap(headers);
    dto.setDatetime(2000000L);
    dto.setAllContentLength(-1);
    dto.setContentLength(0);
    dto.setLocale(new cn.addenda.porttrail.common.pojo.LocaleDataDto(new LocaleData("en", "US", "")));
    dto.setBody(null);
    dto.setEntryPointSnapshot(snapshot);

    HttpClientRequestBo bo = new HttpClientRequestBo(dto);

    Assertions.assertEquals("exec-002", bo.getExecutionId());
    Assertions.assertEquals("apiClient", bo.getClientName());
    Assertions.assertEquals("HTTP/1.1", bo.getVersion());
    Assertions.assertEquals("http", bo.getScheme());
    Assertions.assertEquals("GET", bo.getMethod());
    Assertions.assertEquals("/api/health", bo.getUri());
    Assertions.assertNull(bo.getQueryString());
    Assertions.assertNull(bo.getContentType());
    Assertions.assertNull(bo.getCharsetEncoding());
    Assertions.assertEquals(1, bo.getHeaderMap().size());
    Assertions.assertEquals(2000000L, bo.getDatetime());
    Assertions.assertEquals(-1, bo.getAllContentLength());
    Assertions.assertEquals(0, bo.getContentLength());
    Assertions.assertEquals("en", bo.getLocale().getLanguage());
    Assertions.assertEquals(HttpClientExecution.HTTP_CLIENT_EXECUTION_TYPE_REQUEST, bo.getHttpClientExecutionType());
    Assertions.assertEquals(snapshot, bo.getEntryPointSnapshot());
  }

  @Test
  void testBodySentinelBoToDto() {
    HttpClientRequestBo bo = new HttpClientRequestBo("exec-s1", "sentinelClient");
    bo.setBody(AbstractHttpClientExecution.UNSUPPORTED_CONTENT_TYPE);
    HttpClientRequestDto dto = new HttpClientRequestDto(bo);
    Assertions.assertArrayEquals(AbstractHttpClientDto.getUNSUPPORTED_CONTENT_TYPE(), dto.getBody());

    bo.setBody(AbstractHttpClientExecution.UNSUPPORTED_CHARSET_ENCODING);
    dto = new HttpClientRequestDto(bo);
    Assertions.assertArrayEquals(AbstractHttpClientDto.getUNSUPPORTED_CHARSET_ENCODING(), dto.getBody());

    bo.setBody(AbstractHttpClientExecution.BODY_EMPTY);
    dto = new HttpClientRequestDto(bo);
    Assertions.assertArrayEquals(AbstractHttpClientDto.getBODY_EMPTY(), dto.getBody());

    bo.setBody(AbstractHttpClientExecution.BODY_EXCEED_LENGTH);
    dto = new HttpClientRequestDto(bo);
    Assertions.assertArrayEquals(AbstractHttpClientDto.getBODY_EXCEED_LENGTH(), dto.getBody());

    bo.setBody(AbstractHttpClientExecution.UNSUPPORTED_CONTENT_ENCODING);
    dto = new HttpClientRequestDto(bo);
    Assertions.assertArrayEquals(AbstractHttpClientDto.getUNSUPPORTED_CONTENT_ENCODING(), dto.getBody());

    bo.setBody(HttpClientRequestBo.BODY_BYTE_ARRAY);
    dto = new HttpClientRequestDto(bo);
    Assertions.assertArrayEquals(HttpClientRequestDto.getBODY_BYTE_ARRAY(), dto.getBody());
  }

  @Test
  void testBodySentinelDtoToBo() {
    HttpClientRequestDto dto = new HttpClientRequestDto("exec-s2", "sentinelClient");
    dto.setBody(AbstractHttpClientDto.getUNSUPPORTED_CONTENT_TYPE());
    HttpClientRequestBo bo = new HttpClientRequestBo(dto);
    Assertions.assertEquals(AbstractHttpClientExecution.UNSUPPORTED_CONTENT_TYPE, bo.getBody());

    dto.setBody(AbstractHttpClientDto.getUNSUPPORTED_CHARSET_ENCODING());
    bo = new HttpClientRequestBo(dto);
    Assertions.assertEquals(AbstractHttpClientExecution.UNSUPPORTED_CHARSET_ENCODING, bo.getBody());

    dto.setBody(AbstractHttpClientDto.getBODY_EMPTY());
    bo = new HttpClientRequestBo(dto);
    Assertions.assertEquals(AbstractHttpClientExecution.BODY_EMPTY, bo.getBody());

    dto.setBody(AbstractHttpClientDto.getBODY_EXCEED_LENGTH());
    bo = new HttpClientRequestBo(dto);
    Assertions.assertEquals(AbstractHttpClientExecution.BODY_EXCEED_LENGTH, bo.getBody());

    dto.setBody(AbstractHttpClientDto.getUNSUPPORTED_CONTENT_ENCODING());
    bo = new HttpClientRequestBo(dto);
    Assertions.assertEquals(AbstractHttpClientExecution.UNSUPPORTED_CONTENT_ENCODING, bo.getBody());

    dto.setBody(HttpClientRequestDto.getBODY_BYTE_ARRAY());
    bo = new HttpClientRequestBo(dto);
    Assertions.assertEquals(HttpClientRequestBo.BODY_BYTE_ARRAY, bo.getBody());
  }

  @Test
  void testBodySentinelRoundTrip() {
    HttpClientRequestBo bo1 = new HttpClientRequestBo("exec-s3", "sentinelClient");
    bo1.setBody(AbstractHttpClientExecution.UNSUPPORTED_CONTENT_TYPE);
    HttpClientRequestDto dto1 = new HttpClientRequestDto(bo1);
    HttpClientRequestBo bo2 = new HttpClientRequestBo(dto1);
    HttpClientRequestDto dto2 = new HttpClientRequestDto(bo2);
    Assertions.assertEquals(bo1.getBody(), bo2.getBody());
    Assertions.assertArrayEquals(dto1.getBody(), dto2.getBody());

    bo1.setBody(AbstractHttpClientExecution.UNSUPPORTED_CHARSET_ENCODING);
    dto1 = new HttpClientRequestDto(bo1);
    bo2 = new HttpClientRequestBo(dto1);
    dto2 = new HttpClientRequestDto(bo2);
    Assertions.assertEquals(bo1.getBody(), bo2.getBody());
    Assertions.assertArrayEquals(dto1.getBody(), dto2.getBody());

    bo1.setBody(AbstractHttpClientExecution.BODY_EMPTY);
    dto1 = new HttpClientRequestDto(bo1);
    bo2 = new HttpClientRequestBo(dto1);
    dto2 = new HttpClientRequestDto(bo2);
    Assertions.assertEquals(bo1.getBody(), bo2.getBody());
    Assertions.assertArrayEquals(dto1.getBody(), dto2.getBody());

    bo1.setBody(AbstractHttpClientExecution.BODY_EXCEED_LENGTH);
    dto1 = new HttpClientRequestDto(bo1);
    bo2 = new HttpClientRequestBo(dto1);
    dto2 = new HttpClientRequestDto(bo2);
    Assertions.assertEquals(bo1.getBody(), bo2.getBody());
    Assertions.assertArrayEquals(dto1.getBody(), dto2.getBody());

    bo1.setBody(AbstractHttpClientExecution.UNSUPPORTED_CONTENT_ENCODING);
    dto1 = new HttpClientRequestDto(bo1);
    bo2 = new HttpClientRequestBo(dto1);
    dto2 = new HttpClientRequestDto(bo2);
    Assertions.assertEquals(bo1.getBody(), bo2.getBody());
    Assertions.assertArrayEquals(dto1.getBody(), dto2.getBody());

    bo1.setBody(HttpClientRequestBo.BODY_BYTE_ARRAY);
    dto1 = new HttpClientRequestDto(bo1);
    bo2 = new HttpClientRequestBo(dto1);
    dto2 = new HttpClientRequestDto(bo2);
    Assertions.assertEquals(bo1.getBody(), bo2.getBody());
    Assertions.assertArrayEquals(dto1.getBody(), dto2.getBody());
  }

  @Test
  void testFullRoundTrip() {
    EntryPointSnapshot snapshot = EntryPointSnapshot.of(
            Arrays.asList(EntryPoint.of(EntryPointType.REMOTE_HTTPCLIENT, "roundTripApi")),
            "roundtrip", "trace-h3", 3L);
    Map<String, List<String>> headers = new HashMap<>();
    headers.put("Content-Type", Collections.singletonList("application/xml"));
    headers.put("Accept", Collections.singletonList("application/xml"));
    LocaleData locale = new LocaleData("ja", "JP", "");

    HttpClientRequestBo bo1 = new HttpClientRequestBo("exec-003", "rtClient");
    bo1.setVersion("HTTP/2");
    bo1.setScheme("https");
    bo1.setMethod("PUT");
    bo1.setUri("/api/users/1");
    bo1.setQueryString("v=2");
    bo1.setContentType("application/xml");
    bo1.setCharsetEncoding("UTF-8");
    bo1.setHeaderMap(headers);
    bo1.setDatetime(3000000L);
    bo1.setAllContentLength(200);
    bo1.setContentLength(200);
    bo1.setLocale(locale);
    bo1.setBody("<user><name>test</name></user>");
    bo1.setEntryPointSnapshot(snapshot);

    HttpClientRequestDto dto1 = new HttpClientRequestDto(bo1);
    HttpClientRequestBo bo2 = new HttpClientRequestBo(dto1);
    HttpClientRequestDto dto2 = new HttpClientRequestDto(bo2);

    Assertions.assertEquals(bo1.getExecutionId(), bo2.getExecutionId());
    Assertions.assertEquals(bo1.getClientName(), bo2.getClientName());
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
    Assertions.assertEquals(dto1.getClientName(), dto2.getClientName());
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
