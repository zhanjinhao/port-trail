package org.apache.http.entity.mime;

import cn.addenda.porttrail.common.pojo.FormData;
import cn.addenda.porttrail.common.pojo.FormDataList;
import cn.addenda.porttrail.common.pojo.httpclient.bo.AbstractHttpClientExecution;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.content.ContentBody;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpClient4MultipartFormVisitor {

  public static Object extractMultipartFormRequestBody(HttpEntity entity) {
    if (!(entity instanceof MultipartFormEntity)) {
      return AbstractHttpClientExecution.UNSUPPORTED_CONTENT_TYPE;
    }
    MultipartFormEntity multipartFormEntity = (MultipartFormEntity) entity;
    AbstractMultipartForm multipart = multipartFormEntity.getMultipart();
    List<FormBodyPart> bodyPartList = multipart.getBodyParts();

    FormDataList formDataList = new FormDataList();
    for (FormBodyPart bodyPart : bodyPartList) {
      FormData formData = new FormData();
      formDataList.add(formData);

      ContentBody contentBody = bodyPart.getBody();
      formData.setContentType(contentBody.getMimeType());
      formData.setName(bodyPart.getName());

      long contentLength = contentBody.getContentLength();
      formData.setSize(contentLength >= 0 ? contentLength : 0L);

      Map<String, List<String>> headerMap = new HashMap<>();
      Header header = bodyPart.getHeader();
      for (MinimalField field : header.getFields()) {
        headerMap.computeIfAbsent(field.getName(), k -> new ArrayList<>()).add(field.getBody());
      }
      formData.setHeaderMap(headerMap);

      String filename = contentBody.getFilename();
      formData.setSubmittedFileName(filename);

      if (filename == null) {
        try {
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          contentBody.writeTo(baos);
          String charset = contentBody.getCharset();
          if (charset == null) {
            charset = Optional.ofNullable(multipart.charset).map(Charset::name).orElse(null);
            if (charset == null) {
              charset = AbstractHttpClientExecution.DEFAULT_CHARSET;
            }
          }
          formData.setValues(new String[]{baos.toString(charset)});
        } catch (IOException e) {
          formData.setValues(null);
        }
      }
    }
    return formDataList;
  }

}
