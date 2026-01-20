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

  public static final String APPLICATION_OCTET_STREAM_VALUE = "application/octet-stream";
  public static final String APPLICATION_PDF_VALUE = "application/pdf";
  public static final String IMAGE_GIF_VALUE = "image/gif";
  public static final String IMAGE_JPEG_VALUE = "image/jpeg";
  public static final String IMAGE_PNG_VALUE = "image/png";
  public static final String IMAGE_JPG_VALUE = "image/jpg";

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
            || StringUtils.startsWithIgnoreCase(contentType, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            || StringUtils.startsWithIgnoreCase(contentType, MediaType.APPLICATION_XML_VALUE)
            || StringUtils.startsWithIgnoreCase(contentType, MediaType.TEXT_HTML_VALUE)
            || StringUtils.startsWithIgnoreCase(contentType, MediaType.TEXT_XML_VALUE)
            || StringUtils.startsWithIgnoreCase(contentType, MediaType.TEXT_PLAIN_VALUE);
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
    return StringUtils.startsWithIgnoreCase(contentType, MediaType.APPLICATION_OCTET_STREAM_VALUE);
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
            || StringUtils.startsWithIgnoreCase(contentType, MediaType.TEXT_PLAIN_VALUE);
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
            || StringUtils.startsWithIgnoreCase(contentType, MediaType.IMAGE_JPG_VALUE);
  }

}
