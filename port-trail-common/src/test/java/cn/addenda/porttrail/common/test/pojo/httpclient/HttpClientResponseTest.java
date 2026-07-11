package cn.addenda.porttrail.common.test.pojo.httpclient;

import cn.addenda.porttrail.common.entrypoint.EntryPoint;
import cn.addenda.porttrail.common.entrypoint.EntryPointSnapshot;
import cn.addenda.porttrail.common.entrypoint.EntryPointType;
import cn.addenda.porttrail.common.pojo.httpclient.bo.AbstractHttpClientExecution;
import cn.addenda.porttrail.common.pojo.httpclient.bo.HttpClientExecution;
import cn.addenda.porttrail.common.pojo.httpclient.bo.HttpClientResponseBo;
import cn.addenda.porttrail.common.pojo.httpclient.dto.AbstractHttpClientDto;
import cn.addenda.porttrail.common.pojo.httpclient.dto.HttpClientResponseDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

class HttpClientResponseTest {

  @Test
  void testDtoFromBo() {
    EntryPointSnapshot snapshot = EntryPointSnapshot.of(
            Arrays.asList(EntryPoint.of(EntryPointType.REMOTE_HTTPCLIENT, "getResponse")),
            "main", "trace-hr1", 1L);
    Map<String, List<String>> headers = new HashMap<>();
    headers.put("Server", Collections.singletonList("nginx"));
    headers.put("Content-Type", Collections.singletonList("text/html"));

    HttpClientResponseBo bo = new HttpClientResponseBo("exec-001", "myClient");
    bo.setContentType("text/html");
    bo.setContentLength(1024);
    bo.setDatetime(1000000L);
    bo.setCharsetEncoding("UTF-8");
    bo.setStatus(200);
    bo.setHeaderMap(headers);
    bo.setBody("<html><body>OK</body></html>");
    bo.setEntryPointSnapshot(snapshot);

    HttpClientResponseDto dto = new HttpClientResponseDto(bo);

    Assertions.assertEquals("exec-001", dto.getExecutionId());
    Assertions.assertEquals("myClient", dto.getClientName());
    Assertions.assertEquals("text/html", dto.getContentType());
    Assertions.assertEquals(1024, dto.getContentLength());
    Assertions.assertEquals(1000000L, dto.getDatetime());
    Assertions.assertEquals("UTF-8", dto.getCharsetEncoding());
    Assertions.assertEquals(200, dto.getStatus());
    Assertions.assertEquals(2, dto.getHeaderMap().size());
    Assertions.assertEquals(snapshot, dto.getEntryPointSnapshot());
  }

  @Test
  void testBoFromDto() {
    EntryPointSnapshot snapshot = EntryPointSnapshot.of(
            Arrays.asList(EntryPoint.of(EntryPointType.REMOTE_HTTPCLIENT, "errorResponse")),
            "worker", "trace-hr2", 2L);
    Map<String, List<String>> headers = new HashMap<>();
    headers.put("Content-Type", Collections.singletonList("application/json"));

    HttpClientResponseDto dto = new HttpClientResponseDto("exec-002", "errorClient");
    dto.setContentType("application/json");
    dto.setCharsetEncoding("UTF-8");
    dto.setContentLength(50);
    dto.setDatetime(2000000L);
    dto.setStatus(500);
    dto.setHeaderMap(headers);
    dto.setBody(null);
    dto.setEntryPointSnapshot(snapshot);

    HttpClientResponseBo bo = new HttpClientResponseBo(dto);

    Assertions.assertEquals("exec-002", bo.getExecutionId());
    Assertions.assertEquals("errorClient", bo.getClientName());
    Assertions.assertEquals("application/json", bo.getContentType());
    Assertions.assertEquals("UTF-8", bo.getCharsetEncoding());
    Assertions.assertEquals(50, bo.getContentLength());
    Assertions.assertEquals(2000000L, bo.getDatetime());
    Assertions.assertEquals(500, bo.getStatus());
    Assertions.assertEquals(1, bo.getHeaderMap().size());
    Assertions.assertEquals(HttpClientExecution.HTTP_CLIENT_EXECUTION_TYPE_RESPONSE, bo.getHttpClientExecutionType());
    Assertions.assertEquals(snapshot, bo.getEntryPointSnapshot());
  }

  @Test
  void testBodySentinelBoToDto() {
    HttpClientResponseBo bo = new HttpClientResponseBo("exec-s1", "sentinelClient");
    bo.setBody(AbstractHttpClientExecution.UNSUPPORTED_CONTENT_TYPE);
    HttpClientResponseDto dto = new HttpClientResponseDto(bo);
    Assertions.assertArrayEquals(AbstractHttpClientDto.getUNSUPPORTED_CONTENT_TYPE(), dto.getBody());

    bo.setBody(AbstractHttpClientExecution.UNSUPPORTED_CHARSET_ENCODING);
    dto = new HttpClientResponseDto(bo);
    Assertions.assertArrayEquals(AbstractHttpClientDto.getUNSUPPORTED_CHARSET_ENCODING(), dto.getBody());

    bo.setBody(AbstractHttpClientExecution.BODY_EMPTY);
    dto = new HttpClientResponseDto(bo);
    Assertions.assertArrayEquals(AbstractHttpClientDto.getBODY_EMPTY(), dto.getBody());

    bo.setBody(AbstractHttpClientExecution.BODY_EXCEED_LENGTH);
    dto = new HttpClientResponseDto(bo);
    Assertions.assertArrayEquals(AbstractHttpClientDto.getBODY_EXCEED_LENGTH(), dto.getBody());

    bo.setBody(AbstractHttpClientExecution.UNSUPPORTED_CONTENT_ENCODING);
    dto = new HttpClientResponseDto(bo);
    Assertions.assertArrayEquals(AbstractHttpClientDto.getUNSUPPORTED_CONTENT_ENCODING(), dto.getBody());

    bo.setBody(HttpClientResponseBo.UNKNOWN_FILENAME);
    dto = new HttpClientResponseDto(bo);
    Assertions.assertArrayEquals(HttpClientResponseDto.getUNKNOWN_FILENAME(), dto.getBody());
  }

  @Test
  void testBodySentinelDtoToBo() {
    HttpClientResponseDto dto = new HttpClientResponseDto("exec-s2", "sentinelClient");
    dto.setBody(AbstractHttpClientDto.getUNSUPPORTED_CONTENT_TYPE());
    HttpClientResponseBo bo = new HttpClientResponseBo(dto);
    Assertions.assertEquals(AbstractHttpClientExecution.UNSUPPORTED_CONTENT_TYPE, bo.getBody());

    dto.setBody(AbstractHttpClientDto.getUNSUPPORTED_CHARSET_ENCODING());
    bo = new HttpClientResponseBo(dto);
    Assertions.assertEquals(AbstractHttpClientExecution.UNSUPPORTED_CHARSET_ENCODING, bo.getBody());

    dto.setBody(AbstractHttpClientDto.getBODY_EMPTY());
    bo = new HttpClientResponseBo(dto);
    Assertions.assertEquals(AbstractHttpClientExecution.BODY_EMPTY, bo.getBody());

    dto.setBody(AbstractHttpClientDto.getBODY_EXCEED_LENGTH());
    bo = new HttpClientResponseBo(dto);
    Assertions.assertEquals(AbstractHttpClientExecution.BODY_EXCEED_LENGTH, bo.getBody());

    dto.setBody(AbstractHttpClientDto.getUNSUPPORTED_CONTENT_ENCODING());
    bo = new HttpClientResponseBo(dto);
    Assertions.assertEquals(AbstractHttpClientExecution.UNSUPPORTED_CONTENT_ENCODING, bo.getBody());

    dto.setBody(HttpClientResponseDto.getUNKNOWN_FILENAME());
    bo = new HttpClientResponseBo(dto);
    Assertions.assertEquals(HttpClientResponseBo.UNKNOWN_FILENAME, bo.getBody());
  }

  @Test
  void testBodySentinelRoundTrip() {
    HttpClientResponseBo bo1 = new HttpClientResponseBo("exec-s3", "sentinelClient");
    bo1.setBody(AbstractHttpClientExecution.UNSUPPORTED_CONTENT_TYPE);
    HttpClientResponseDto dto1 = new HttpClientResponseDto(bo1);
    HttpClientResponseBo bo2 = new HttpClientResponseBo(dto1);
    HttpClientResponseDto dto2 = new HttpClientResponseDto(bo2);
    Assertions.assertEquals(bo1.getBody(), bo2.getBody());
    Assertions.assertArrayEquals(dto1.getBody(), dto2.getBody());

    bo1.setBody(AbstractHttpClientExecution.UNSUPPORTED_CHARSET_ENCODING);
    dto1 = new HttpClientResponseDto(bo1);
    bo2 = new HttpClientResponseBo(dto1);
    dto2 = new HttpClientResponseDto(bo2);
    Assertions.assertEquals(bo1.getBody(), bo2.getBody());
    Assertions.assertArrayEquals(dto1.getBody(), dto2.getBody());

    bo1.setBody(AbstractHttpClientExecution.BODY_EMPTY);
    dto1 = new HttpClientResponseDto(bo1);
    bo2 = new HttpClientResponseBo(dto1);
    dto2 = new HttpClientResponseDto(bo2);
    Assertions.assertEquals(bo1.getBody(), bo2.getBody());
    Assertions.assertArrayEquals(dto1.getBody(), dto2.getBody());

    bo1.setBody(AbstractHttpClientExecution.BODY_EXCEED_LENGTH);
    dto1 = new HttpClientResponseDto(bo1);
    bo2 = new HttpClientResponseBo(dto1);
    dto2 = new HttpClientResponseDto(bo2);
    Assertions.assertEquals(bo1.getBody(), bo2.getBody());
    Assertions.assertArrayEquals(dto1.getBody(), dto2.getBody());

    bo1.setBody(AbstractHttpClientExecution.UNSUPPORTED_CONTENT_ENCODING);
    dto1 = new HttpClientResponseDto(bo1);
    bo2 = new HttpClientResponseBo(dto1);
    dto2 = new HttpClientResponseDto(bo2);
    Assertions.assertEquals(bo1.getBody(), bo2.getBody());
    Assertions.assertArrayEquals(dto1.getBody(), dto2.getBody());

    bo1.setBody(HttpClientResponseBo.UNKNOWN_FILENAME);
    dto1 = new HttpClientResponseDto(bo1);
    bo2 = new HttpClientResponseBo(dto1);
    dto2 = new HttpClientResponseDto(bo2);
    Assertions.assertEquals(bo1.getBody(), bo2.getBody());
    Assertions.assertArrayEquals(dto1.getBody(), dto2.getBody());
  }

  @Test
  void testFullRoundTrip() {
    EntryPointSnapshot snapshot = EntryPointSnapshot.of(
            Arrays.asList(EntryPoint.of(EntryPointType.REMOTE_HTTPCLIENT, "roundTripResp")),
            "roundtrip", "trace-hr3", 3L);

    HttpClientResponseBo bo1 = new HttpClientResponseBo("exec-003", "rtClient");
    bo1.setContentType("application/octet-stream");
    bo1.setContentLength(4096);
    bo1.setDatetime(3000000L);
    bo1.setCharsetEncoding("ISO-8859-1");
    bo1.setStatus(404);
    bo1.setHeaderMap(new HashMap<>());
    bo1.setBody("binary-data.txt");
    bo1.setEntryPointSnapshot(snapshot);

    HttpClientResponseDto dto1 = new HttpClientResponseDto(bo1);
    HttpClientResponseBo bo2 = new HttpClientResponseBo(dto1);
    HttpClientResponseDto dto2 = new HttpClientResponseDto(bo2);

    Assertions.assertEquals(bo1.getExecutionId(), bo2.getExecutionId());
    Assertions.assertEquals(bo1.getClientName(), bo2.getClientName());
    Assertions.assertEquals(bo1.getContentType(), bo2.getContentType());
    Assertions.assertEquals(bo1.getContentLength(), bo2.getContentLength());
    Assertions.assertEquals(bo1.getDatetime(), bo2.getDatetime());
    Assertions.assertEquals(bo1.getCharsetEncoding(), bo2.getCharsetEncoding());
    Assertions.assertEquals(bo1.getStatus(), bo2.getStatus());
    Assertions.assertEquals(bo1.getHeaderMap(), bo2.getHeaderMap());
    Assertions.assertEquals(bo1.getBody(), bo2.getBody());

    Assertions.assertEquals(dto1.getExecutionId(), dto2.getExecutionId());
    Assertions.assertEquals(dto1.getClientName(), dto2.getClientName());
    Assertions.assertEquals(dto1.getContentType(), dto2.getContentType());
    Assertions.assertEquals(dto1.getContentLength(), dto2.getContentLength());
    Assertions.assertEquals(dto1.getDatetime(), dto2.getDatetime());
    Assertions.assertEquals(dto1.getCharsetEncoding(), dto2.getCharsetEncoding());
    Assertions.assertEquals(dto1.getStatus(), dto2.getStatus());
    Assertions.assertEquals(dto1.getHeaderMap(), dto2.getHeaderMap());
  }

}
