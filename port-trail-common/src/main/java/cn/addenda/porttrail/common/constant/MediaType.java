package cn.addenda.porttrail.common.constant;

import cn.addenda.porttrail.common.util.StringUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MediaType {

  public static final String MULTIPART_FORM_DATA_VALUE = "multipart/form-data";

  public static final String APPLICATION_JSON_VALUE = "application/json";
  public static final String APPLICATION_FORM_URLENCODED_VALUE = "application/x-www-form-urlencoded";
  public static final String APPLICATION_XML_VALUE = "application/xml";
  public static final String TEXT_HTML_VALUE = "text/html";
  public static final String TEXT_XML_VALUE = "text/xml";
  public static final String TEXT_PLAIN_VALUE = "text/plain";
  public static final String APPLICATION_ATOM_XML_VALUE = "application/atom+xml";
  public static final String APPLICATION_PROBLEM_JSON_VALUE = "application/problem+json";
  public static final String APPLICATION_PROBLEM_XML_VALUE = "application/problem+xml";
  public static final String APPLICATION_RSS_XML_VALUE = "application/rss+xml";
  public static final String APPLICATION_NDJSON_VALUE = "application/x-ndjson";
  public static final String APPLICATION_XHTML_XML_VALUE = "application/xhtml+xml";
  public static final String TEXT_MARKDOWN_VALUE = "text/markdown";
  public static final String APPLICATION_SOAP_XML = "application/soap+xml";
  public static final String APPLICATION_SVG_XML = "application/svg+xml";

  // todo 需要分析 event-stream 的执行流程
  public static final String TEXT_EVENT_STREAM_VALUE = "text/event-stream";

  public static final String APPLICATION_OCTET_STREAM_VALUE = "application/octet-stream";
  public static final String APPLICATION_PDF_VALUE = "application/pdf";
  public static final String IMAGE_GIF_VALUE = "image/gif";
  public static final String IMAGE_JPEG_VALUE = "image/jpeg";
  public static final String IMAGE_PNG_VALUE = "image/png";
  public static final String IMAGE_JPG_VALUE = "image/jpg";
  public static final String IMAGE_BMP = "image/bmp";
  public static final String IMAGE_TIFF = "image/tiff";
  public static final String IMAGE_WEBP = "image/webp";

  public static boolean ifRequestContentType(String contentType) {
    return ifRequestMultipartFormContentType(contentType)
            || ifRequestTextContentType(contentType)
            || ifRequestBinaryContentType(contentType);
  }

  public static boolean ifRequestTextContentType(String contentType) {
    if (contentType == null) {
      return false;
    }
    return StringUtils.startsWithIgnoreCase(contentType, MediaType.APPLICATION_JSON_VALUE)
            || StringUtils.startsWithIgnoreCase(contentType, MediaType.APPLICATION_XML_VALUE)
            || StringUtils.startsWithIgnoreCase(contentType, MediaType.TEXT_HTML_VALUE)
            || StringUtils.startsWithIgnoreCase(contentType, MediaType.TEXT_XML_VALUE)
            || StringUtils.startsWithIgnoreCase(contentType, MediaType.TEXT_PLAIN_VALUE)
            || StringUtils.startsWithIgnoreCase(contentType, MediaType.APPLICATION_ATOM_XML_VALUE)
            || StringUtils.startsWithIgnoreCase(contentType, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
            || StringUtils.startsWithIgnoreCase(contentType, MediaType.APPLICATION_PROBLEM_XML_VALUE)
            || StringUtils.startsWithIgnoreCase(contentType, MediaType.APPLICATION_RSS_XML_VALUE)
            || StringUtils.startsWithIgnoreCase(contentType, MediaType.APPLICATION_NDJSON_VALUE)
            || StringUtils.startsWithIgnoreCase(contentType, MediaType.APPLICATION_XHTML_XML_VALUE)
            || StringUtils.startsWithIgnoreCase(contentType, MediaType.TEXT_MARKDOWN_VALUE)
            || StringUtils.startsWithIgnoreCase(contentType, MediaType.APPLICATION_SOAP_XML)
            || StringUtils.startsWithIgnoreCase(contentType, MediaType.APPLICATION_SVG_XML)
            || ifRequestFormUrlencodedContentType(contentType);
  }

  public static boolean ifRequestFormUrlencodedContentType(String contentType) {
    if (contentType == null) {
      return false;
    }
    return StringUtils.startsWithIgnoreCase(contentType, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
  }

  public static boolean ifRequestMultipartFormContentType(String contentType) {
    if (contentType == null) {
      return false;
    }
    return StringUtils.startsWithIgnoreCase(contentType, MediaType.MULTIPART_FORM_DATA_VALUE);
  }

  public static boolean ifRequestBinaryContentType(String contentType) {
    if (contentType == null) {
      return false;
    }
    return StringUtils.startsWithIgnoreCase(contentType, MediaType.APPLICATION_OCTET_STREAM_VALUE)
            || StringUtils.startsWithIgnoreCase(contentType, MediaType.APPLICATION_PDF_VALUE)
            || StringUtils.startsWithIgnoreCase(contentType, MediaType.IMAGE_GIF_VALUE)
            || StringUtils.startsWithIgnoreCase(contentType, MediaType.IMAGE_JPEG_VALUE)
            || StringUtils.startsWithIgnoreCase(contentType, MediaType.IMAGE_PNG_VALUE)
            || StringUtils.startsWithIgnoreCase(contentType, MediaType.IMAGE_JPG_VALUE)
            || StringUtils.startsWithIgnoreCase(contentType, MediaType.IMAGE_BMP)
            || StringUtils.startsWithIgnoreCase(contentType, MediaType.IMAGE_TIFF)
            || StringUtils.startsWithIgnoreCase(contentType, MediaType.IMAGE_WEBP);
  }

  public static boolean ifResponseContentType(String contentType) {
    return ifResponseBinaryContentType(contentType)
            || ifResponseTextContentType(contentType);
  }

  public static boolean ifResponseTextContentType(String contentType) {
    if (contentType == null) {
      return false;
    }
    return StringUtils.startsWithIgnoreCase(contentType, MediaType.APPLICATION_JSON_VALUE)
            || StringUtils.startsWithIgnoreCase(contentType, MediaType.APPLICATION_XML_VALUE)
            || StringUtils.startsWithIgnoreCase(contentType, MediaType.TEXT_HTML_VALUE)
            || StringUtils.startsWithIgnoreCase(contentType, MediaType.TEXT_XML_VALUE)
            || StringUtils.startsWithIgnoreCase(contentType, MediaType.TEXT_PLAIN_VALUE)
            || StringUtils.startsWithIgnoreCase(contentType, MediaType.APPLICATION_ATOM_XML_VALUE)
            || StringUtils.startsWithIgnoreCase(contentType, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
            || StringUtils.startsWithIgnoreCase(contentType, MediaType.APPLICATION_PROBLEM_XML_VALUE)
            || StringUtils.startsWithIgnoreCase(contentType, MediaType.APPLICATION_RSS_XML_VALUE)
            || StringUtils.startsWithIgnoreCase(contentType, MediaType.APPLICATION_NDJSON_VALUE)
            || StringUtils.startsWithIgnoreCase(contentType, MediaType.APPLICATION_XHTML_XML_VALUE)
            || StringUtils.startsWithIgnoreCase(contentType, MediaType.TEXT_MARKDOWN_VALUE)
            || StringUtils.startsWithIgnoreCase(contentType, MediaType.APPLICATION_SOAP_XML)
            || StringUtils.startsWithIgnoreCase(contentType, MediaType.APPLICATION_SVG_XML)
            || ifRequestFormUrlencodedContentType(contentType);
  }

  public static boolean ifResponseBinaryContentType(String contentType) {
    if (contentType == null) {
      return false;
    }
    return StringUtils.startsWithIgnoreCase(contentType, MediaType.APPLICATION_OCTET_STREAM_VALUE)
            || StringUtils.startsWithIgnoreCase(contentType, MediaType.APPLICATION_PDF_VALUE)
            || StringUtils.startsWithIgnoreCase(contentType, MediaType.IMAGE_GIF_VALUE)
            || StringUtils.startsWithIgnoreCase(contentType, MediaType.IMAGE_JPEG_VALUE)
            || StringUtils.startsWithIgnoreCase(contentType, MediaType.IMAGE_PNG_VALUE)
            || StringUtils.startsWithIgnoreCase(contentType, MediaType.IMAGE_JPG_VALUE)
            || StringUtils.startsWithIgnoreCase(contentType, MediaType.IMAGE_BMP)
            || StringUtils.startsWithIgnoreCase(contentType, MediaType.IMAGE_TIFF)
            || StringUtils.startsWithIgnoreCase(contentType, MediaType.IMAGE_WEBP);
  }

}
