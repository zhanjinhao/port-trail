package cn.addenda.porttrail.agent.transform.interceptor.http.httpclient4;

import cn.addenda.porttrail.agent.log.AgentPortTrailLoggerFactory;
import cn.addenda.porttrail.common.constant.MediaType;
import cn.addenda.porttrail.common.pojo.LocaleData;
import cn.addenda.porttrail.common.pojo.httpclient.bo.AbstractHttpClientExecution;
import cn.addenda.porttrail.common.pojo.httpclient.bo.HttpClientResponseBo;
import cn.addenda.porttrail.infrastructure.entrypoint.EntryPointStackContext;
import cn.addenda.porttrail.infrastructure.log.PortTrailLogger;
import cn.addenda.porttrail.infrastructure.writer.HttpClientWriter;
import org.apache.http.*;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

public class PortTrailHttpResponseInterceptor extends AbstractPortTrailHttpInterceptor implements HttpResponseInterceptor {

  private static final PortTrailLogger log =
          AgentPortTrailLoggerFactory.getInstance().getPortTrailLogger(PortTrailHttpResponseInterceptor.class);

  private final int responseMaxBodyLength;

  private final HttpClientWriter httpClientWriter;

  public PortTrailHttpResponseInterceptor(int responseMaxBodyLength, HttpClientWriter httpClientWriter) {
    this.responseMaxBodyLength = responseMaxBodyLength;
    this.httpClientWriter = httpClientWriter;
  }

  private HttpClientResponseBo assembleHttpClientResponseBo(HttpResponse response, String executionId)
          throws IOException {
    HttpClientResponseBo httpClientResponseBo = new HttpClientResponseBo(executionId, CLIENT_NAME);
    Header contentTypeHeader = response.getFirstHeader(HTTP.CONTENT_TYPE);
    if (contentTypeHeader != null) {
      String contentType = contentTypeHeader.getValue();
      httpClientResponseBo.setContentType(contentType);
      String charset = extractCharsetFromContentType(contentType);
      if (charset != null) {
        httpClientResponseBo.setCharsetEncoding(charset);
      }
    }

    httpClientResponseBo.setDatetime(System.currentTimeMillis());

    Locale locale = response.getLocale();
    if (locale != null) {
      httpClientResponseBo.setLocale(new LocaleData(locale.getLanguage(), locale.getCountry(), locale.getVariant()));
    }

    httpClientResponseBo.setStatus(response.getStatusLine().getStatusCode());

    // 这里能获取到的header是在程序里设置的。在Tomcat或Jetty里设置的获取不到。
    Map<String, List<String>> headerMap = new HashMap<>();
    for (Header header : response.getAllHeaders()) {
      headerMap.computeIfAbsent(header.getName(), k -> new ArrayList<>()).add(header.getValue());
    }
    httpClientResponseBo.setHeaderMap(headerMap);

    fillBody(response, httpClientResponseBo);

    httpClientResponseBo.setEntryPointSnapshot(EntryPointStackContext.snapshot());
    return httpClientResponseBo;
  }

  private void fillBody(HttpResponse response, HttpClientResponseBo httpClientResponseBo)
          throws IOException {
    HttpEntity entity = response.getEntity();
    if (entity == null) {
      httpClientResponseBo.setContentLength(0);
      httpClientResponseBo.setBody(AbstractHttpClientExecution.BODY_EMPTY);
      return;
    }

    long contentLength = entity.getContentLength();
    httpClientResponseBo.setContentLength((int) contentLength);

    if (contentLength == 0) {
      httpClientResponseBo.setBody(AbstractHttpClientExecution.BODY_EMPTY);
      return;
    }

    // 将实体包装为缓冲实体，允许多次读取
    if (!(entity instanceof BufferedHttpEntity)) {
      // 后续可以安全地多次读取
      response.setEntity(new BufferedHttpEntity(entity));
    }

    String executionId = httpClientResponseBo.getExecutionId();

    Header contentEncodingHeader = entity.getContentEncoding();
    if (contentEncodingHeader == null) {
      contentEncodingHeader = response.getFirstHeader(HTTP.CONTENT_ENCODING);
    }
    String contentEncoding = contentEncodingHeader != null ? contentEncodingHeader.getValue() : "";

    String charsetEncoding = Optional.ofNullable(httpClientResponseBo.getCharsetEncoding())
            .orElse(AbstractHttpClientExecution.DEFAULT_CHARSET);

    String contentType = httpClientResponseBo.getContentType();
    if (contentType == null) {
      // 如果没有content-type，按text处理，如果报错就设置为UNSUPPORTED_CONTENT_TYPE
      try {
        byte[] bodyBytes = EntityUtils.toByteArray(response.getEntity());
        httpClientResponseBo.setBody(extractTextResponseBody(bodyBytes, contentEncoding, charsetEncoding, executionId, true));
      } catch (Throwable t) {
        httpClientResponseBo.setBody(AbstractHttpClientExecution.UNSUPPORTED_CONTENT_TYPE);
      }
    } else if (MediaType.ifResponseContentType(contentType)) {
      if (MediaType.ifResponseTextContentType(contentType)) {
        byte[] bodyBytes = EntityUtils.toByteArray(response.getEntity());
        httpClientResponseBo.setBody(extractTextResponseBody(bodyBytes, contentEncoding, charsetEncoding, executionId, false));
      } else if (MediaType.ifResponseBinaryContentType(contentType)) {
        httpClientResponseBo.setBody(extractBinaryResponseBody(response, charsetEncoding));
      }
    } else if (MediaType.ifResponseHttpContentType(contentType)) {
      try {
        byte[] bodyBytes = EntityUtils.toByteArray(response.getEntity());
        httpClientResponseBo.setBody(extractTextResponseBody(bodyBytes, contentEncoding, charsetEncoding, executionId, true));
      } catch (Throwable t) {
        httpClientResponseBo.setBody(AbstractHttpClientExecution.UNSUPPORTED_CONTENT_TYPE);
      }
    } else {
      httpClientResponseBo.setBody(AbstractHttpClientExecution.UNSUPPORTED_CONTENT_TYPE);
    }
  }

  private String extractTextResponseBody(
          byte[] contentAsByteArray,
          String contentEncoding, String characterEncoding, String executionId, boolean ifThrow)
          throws IOException {
    if (contentAsByteArray.length > 0) {
      if (contentAsByteArray.length > responseMaxBodyLength) {
        return AbstractHttpClientExecution.BODY_EXCEED_LENGTH;
      } else {
        return decodeBody(contentAsByteArray, contentEncoding, characterEncoding, executionId, ifThrow);
      }
    }
    return AbstractHttpClientExecution.BODY_EMPTY;
  }

  private String extractBinaryResponseBody(HttpResponse response, String charsetEncoding) {
    // 解析 ，获取attachment的filename
    Header contentTypeHeader = response.getFirstHeader("Content-Disposition");
    if (contentTypeHeader == null) {
      return HttpClientResponseBo.UNKNOWN_FILENAME;
    }
    String header = contentTypeHeader.getValue();
    if (header == null || header.isEmpty()) {
      return HttpClientResponseBo.UNKNOWN_FILENAME;
    } else {
      String lowerInput = header.toLowerCase();
      String target = "filename=";
      int index = lowerInput.indexOf(target);
      if (index == -1) {
        return HttpClientResponseBo.UNKNOWN_FILENAME;
      }
      // 使用原字符串截取保持原始大小写
      String result = header.substring(index + target.length());
      // 获取第一个分号前的内容
      int semicolonIndex = result.indexOf(';');
      if (semicolonIndex != -1) {
        result = result.substring(0, semicolonIndex);
      }
      // 去除首尾空格和引号
      result = result.trim();
      if (result.startsWith("\"") && result.endsWith("\"") && result.length() > 1) {
        result = result.substring(1, result.length() - 1);
      }
      if (result.isEmpty()) {
        return HttpClientResponseBo.UNKNOWN_FILENAME;
      }

      if (charsetEncoding == null) {
        return result;
      }
      try {
        return URLDecoder.decode(result, charsetEncoding);
      } catch (UnsupportedEncodingException e) {
        return result;
      }
    }
  }

  @Override
  public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
    String executionId = (String) context.getAttribute(EXECUTION_ID_KEY);

    try {
      HttpClientResponseBo httpClientResponseBo = assembleHttpClientResponseBo(response, executionId);
      httpClientWriter.writeHttpResponse(httpClientResponseBo);
    } catch (Throwable throwable) {
      log.error("unexpected error, requestLine: [{}], statusLine: [{}].",
              context.getAttribute(EXECUTION_ID_URI), response.getStatusLine().toString(), throwable);
    }
  }

  @Override
  protected PortTrailLogger getLog() {
    return log;
  }

}
