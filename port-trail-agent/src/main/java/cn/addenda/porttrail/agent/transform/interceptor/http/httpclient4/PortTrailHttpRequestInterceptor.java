package cn.addenda.porttrail.agent.transform.interceptor.http.httpclient4;

import cn.addenda.porttrail.agent.log.AgentPortTrailLoggerFactory;
import cn.addenda.porttrail.common.constant.MediaType;
import cn.addenda.porttrail.common.pojo.LocaleData;
import cn.addenda.porttrail.common.pojo.httpclient.bo.AbstractHttpClientExecution;
import cn.addenda.porttrail.common.pojo.httpclient.bo.HttpClientRequestBo;
import cn.addenda.porttrail.infrastructure.entrypoint.EntryPointStackContext;
import cn.addenda.porttrail.infrastructure.log.PortTrailLogger;
import cn.addenda.porttrail.infrastructure.writer.HttpClientWriter;
import org.apache.http.*;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.mime.HttpClient4MultipartFormVisitor;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.*;

public class PortTrailHttpRequestInterceptor extends AbstractPortTrailHttpInterceptor implements HttpRequestInterceptor {

  private static final PortTrailLogger log =
          AgentPortTrailLoggerFactory.getInstance().getPortTrailLogger(PortTrailHttpRequestInterceptor.class);

  private final int requestMaxBodyLength;

  private final HttpClientWriter httpClientWriter;

  public PortTrailHttpRequestInterceptor(int requestMaxBodyLength, HttpClientWriter httpClientWriter) {
    this.requestMaxBodyLength = requestMaxBodyLength;
    this.httpClientWriter = httpClientWriter;
  }

  private HttpClientRequestBo assembleHttpClientRequestBo(HttpRequest request, String executionId)
          throws IOException {
    HttpClientRequestBo httpClientRequestBo = new HttpClientRequestBo(executionId, CLIENT_NAME);
    httpClientRequestBo.setVersion(request.getProtocolVersion().toString());
    httpClientRequestBo.setScheme(null);
    httpClientRequestBo.setMethod(request.getRequestLine().getMethod());
    String uri = request.getRequestLine().getUri();
    int queryIdx = uri.indexOf('?');
    if (queryIdx >= 0) {
      httpClientRequestBo.setUri(uri.substring(0, queryIdx));
      httpClientRequestBo.setQueryString(uri.substring(queryIdx + 1));
    } else {
      httpClientRequestBo.setUri(uri);
      httpClientRequestBo.setQueryString(null);
    }
    Header contentTypeHeader = request.getFirstHeader(HTTP.CONTENT_TYPE);
    if (contentTypeHeader != null) {
      String contentType = contentTypeHeader.getValue();
      httpClientRequestBo.setContentType(contentType);
      String charset = extractCharsetFromContentType(contentType);
      if (charset != null) {
        httpClientRequestBo.setCharsetEncoding(charset);
      }
    }
    Map<String, List<String>> headerMap = new HashMap<>();
    Header[] headers = request.getAllHeaders();
    for (Header header : headers) {
      headerMap.computeIfAbsent(header.getName(), k -> new ArrayList<>()).add(header.getValue());
    }
    httpClientRequestBo.setHeaderMap(headerMap);
    httpClientRequestBo.setDatetime(System.currentTimeMillis());
    Header contentLengthHeader = request.getFirstHeader(HTTP.CONTENT_LEN);
    if (contentLengthHeader != null) {
      try {
        httpClientRequestBo.setContentLength(Integer.parseInt(contentLengthHeader.getValue()));
      } catch (NumberFormatException ignored) {
      }
    }
    Header acceptLanguageHeader = request.getFirstHeader("Accept-Language");
    if (acceptLanguageHeader != null) {
      String acceptLanguage = acceptLanguageHeader.getValue();
      String[] langEntries = acceptLanguage.split(",");
      if (langEntries.length > 0) {
        String primaryLang = langEntries[0].trim();
        String langTag = primaryLang.split("[;=]")[0].trim();
        String[] localeParts = langTag.split("-");
        String language = localeParts.length > 0 ? localeParts[0] : "";
        String country = localeParts.length > 1 ? localeParts[1] : "";
        String variant = localeParts.length > 2 ? localeParts[2] : "";
        httpClientRequestBo.setLocale(new LocaleData(language, country, variant));
      }
    }

    if (request instanceof HttpRequestWrapper) {
      fillScheme((HttpRequestWrapper) request, httpClientRequestBo);
    }

    if (request instanceof HttpEntityEnclosingRequest) {
      fillBody((HttpEntityEnclosingRequest) request, httpClientRequestBo);
    } else {
      httpClientRequestBo.setBody(AbstractHttpClientExecution.BODY_EMPTY);
    }

    httpClientRequestBo.setEntryPointSnapshot(EntryPointStackContext.snapshot());
    return httpClientRequestBo;
  }

  private void fillScheme(HttpRequestWrapper httpRequestWrapper, HttpClientRequestBo httpClientRequestBo) {
    HttpRequest original = httpRequestWrapper.getOriginal();
    if (original.getRequestLine().getUri().startsWith("https:")) {
      httpClientRequestBo.setScheme("https");
    } else {
      httpClientRequestBo.setScheme("http");
    }
  }

  private void fillBody(HttpEntityEnclosingRequest request, HttpClientRequestBo httpClientRequestBo) throws IOException {
    HttpEntity entity = request.getEntity();
    if (entity == null) {
      httpClientRequestBo.setBody(AbstractHttpClientExecution.BODY_EMPTY);
      return;
    }

    long contentLength = entity.getContentLength();
    if (contentLength == 0) {
      httpClientRequestBo.setBody(AbstractHttpClientExecution.BODY_EMPTY);
      return;
    }

    // 将实体包装为缓冲实体，允许多次读取
    if (!(entity instanceof BufferedHttpEntity)) {
      // 后续可以安全地多次读取
      request.setEntity(new BufferedHttpEntity(entity));
    }

    Header contentTypeHeader = entity.getContentType();
    if (contentTypeHeader == null) {
      contentTypeHeader = request.getFirstHeader(HTTP.CONTENT_TYPE);
    }
    String contentType = contentTypeHeader != null ? contentTypeHeader.getValue() : null;
    Header contentEncodingHeader = entity.getContentEncoding();
    if (contentEncodingHeader == null) {
      contentEncodingHeader = request.getFirstHeader(HTTP.CONTENT_ENCODING);
    }
    String contentEncoding = contentEncodingHeader != null ? contentEncodingHeader.getValue() : null;

    String charsetEncoding = Optional.ofNullable(httpClientRequestBo.getCharsetEncoding())
            .orElse(AbstractHttpClientExecution.DEFAULT_CHARSET);

    String executionId = httpClientRequestBo.getExecutionId();

    if (contentType == null) {
      // 如果没有content-type，按text处理，如果报错就设置为UNSUPPORTED_CONTENT_TYPE
      try {
        byte[] bodyBytes = EntityUtils.toByteArray(request.getEntity());
        httpClientRequestBo.setBody(extractTextRequestBody(bodyBytes, null, contentEncoding, charsetEncoding, executionId, true));
      } catch (Throwable t) {
        httpClientRequestBo.setBody(AbstractHttpClientExecution.UNSUPPORTED_CONTENT_TYPE);
      }
    } else if (MediaType.ifRequestContentType(contentType)) {
      if (MediaType.ifRequestTextContentType(contentType)) {
        byte[] bodyBytes = EntityUtils.toByteArray(request.getEntity());
        httpClientRequestBo.setBody(extractTextRequestBody(bodyBytes, contentType, contentEncoding, charsetEncoding, executionId, false));
      } else if (MediaType.ifRequestMultipartFormContentType(contentType)) {
        httpClientRequestBo.setBody(HttpClient4MultipartFormVisitor.extractMultipartFormRequestBody(entity));
      } else if (MediaType.ifRequestBinaryContentType(contentType)) {
        httpClientRequestBo.setBody(HttpClientRequestBo.BODY_BYTE_ARRAY);
      }
    } else {
      httpClientRequestBo.setBody(AbstractHttpClientExecution.UNSUPPORTED_CONTENT_TYPE);
    }
  }

  private String extractTextRequestBody(
          byte[] contentAsByteArray, String contentType,
          String contentEncoding, String characterEncoding, String executionId, boolean ifThrow)
          throws IOException {
    if (contentAsByteArray.length > 0) {
      if (contentAsByteArray.length > requestMaxBodyLength) {
        return AbstractHttpClientExecution.BODY_EXCEED_LENGTH;
      } else {
        String body = decodeBody(contentAsByteArray, contentEncoding, characterEncoding, executionId, ifThrow);
        if (MediaType.ifRequestFormUrlencodedContentType(contentType)) {
          return URLDecoder.decode(body, characterEncoding);
        }
        return body;
      }
    }
    return AbstractHttpClientExecution.BODY_EMPTY;
  }

  @Override
  public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
    String executionId = UUID.randomUUID().toString().replace("-", "");
    context.setAttribute(EXECUTION_ID_KEY, executionId);
    context.setAttribute(EXECUTION_ID_URI, request.getRequestLine().toString());

    try {
      HttpClientRequestBo httpClientRequestBo = assembleHttpClientRequestBo(request, executionId);
      httpClientWriter.writeHttpRequest(httpClientRequestBo);
    } catch (Throwable t) {
      log.error("unexpected error, requestLine: [{}].", request.getRequestLine().toString(), t);
    }
  }

  @Override
  protected PortTrailLogger getLog() {
    return log;
  }

}
