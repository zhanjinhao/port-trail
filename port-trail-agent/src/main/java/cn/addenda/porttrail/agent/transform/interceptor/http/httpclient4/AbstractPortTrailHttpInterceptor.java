package cn.addenda.porttrail.agent.transform.interceptor.http.httpclient4;

import cn.addenda.porttrail.common.exception.PortTrailException;
import cn.addenda.porttrail.common.pojo.httpclient.bo.AbstractHttpClientExecution;
import cn.addenda.porttrail.infrastructure.log.PortTrailLogger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.format.DateTimeFormatter;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

public abstract class AbstractPortTrailHttpInterceptor {

  protected static final String EXECUTION_ID_KEY = "@port#trail$execution%id^";
  protected static final String EXECUTION_ID_URI = "@port#trail$execution%uri^";
  protected static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

  protected static final String CLIENT_NAME = "httpClient4";

  protected String extractCharsetFromContentType(String contentType) {
    if (contentType == null) {
      return null;
    }
    for (String part : contentType.split(";")) {
      part = part.trim();
      if (part.toLowerCase().startsWith("charset=")) {
        return part.substring("charset=".length()).replace("\"", "").trim();
      }
    }
    return null;
  }

  /**
   * @param bodyBytes       字节数组
   * @param contentEncoding 压缩算法
   * @param charsetEncoding 字符集
   */
  protected String decodeBody(byte[] bodyBytes, String contentEncoding, String charsetEncoding, String executionId, boolean ifThrow)
          throws IOException {
    if (contentEncoding == null) {
      return convertBytesToString(bodyBytes, charsetEncoding, executionId, ifThrow);
    }

    switch (contentEncoding) {
      case "gzip":
        return convertBytesToString(gzipDecompress(bodyBytes), charsetEncoding, executionId, ifThrow);
      case "deflate":
        return convertBytesToString(deflateDecompress(bodyBytes), charsetEncoding, executionId, ifThrow);
      case "identity":
      case "":
        return convertBytesToString(bodyBytes, charsetEncoding, executionId, ifThrow);
      default:
        return AbstractHttpClientExecution.UNSUPPORTED_CONTENT_ENCODING;
    }
  }

  protected String convertBytesToString(byte[] bytes, String characterEncoding, String executionId, boolean ifThrow) {
    try {
      return new String(bytes, (characterEncoding == null || characterEncoding.isEmpty()) ? AbstractHttpClientExecution.DEFAULT_CHARSET : characterEncoding);
    } catch (UnsupportedEncodingException e) {
      if (ifThrow) {
        throw new PortTrailException(e);
      }
      getLog().error("unsupported character encoding [{}:{}], executionId: [{}].",
              characterEncoding, AbstractHttpClientExecution.DEFAULT_CHARSET, executionId);
      return AbstractHttpClientExecution.UNSUPPORTED_CHARSET_ENCODING;
    }
  }

  protected byte[] gzipDecompress(byte[] compressedData) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (GZIPInputStream gzis = new GZIPInputStream(new ByteArrayInputStream(compressedData))) {
      byte[] buffer = new byte[1024];
      int len;
      while ((len = gzis.read(buffer)) > 0) {
        baos.write(buffer, 0, len);
      }
    }
    return baos.toByteArray();
  }

  protected byte[] deflateDecompress(byte[] compressedData) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (InflaterInputStream iis = new InflaterInputStream(new ByteArrayInputStream(compressedData))) {
      byte[] buffer = new byte[1024];
      int len;
      while ((len = iis.read(buffer)) > 0) {
        baos.write(buffer, 0, len);
      }
    }
    return baos.toByteArray();
  }

  protected abstract PortTrailLogger getLog();

}
