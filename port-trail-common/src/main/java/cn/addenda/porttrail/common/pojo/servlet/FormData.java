package cn.addenda.porttrail.common.pojo.servlet;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.*;

@Setter
@Getter
@ToString
public class FormData {

  private String contentType;

  private String name;

  private long size;

  private Map<String, List<String>> headerMap;

  private String submittedFileName;

  private String[] values;

  public FormData() {
  }

  public FormData(FormDataDto formDataDto) {
    this.setContentType(formDataDto.getContentType());
    this.setName(formDataDto.getName());
    this.setSize(formDataDto.getSize());
    Map<String, List<String>> headerMap2 = new HashMap<>();
    for (Map.Entry<String, List<String>> entry : formDataDto.getHeaderMap().entrySet()) {
      headerMap2.put(entry.getKey(), Optional.ofNullable(entry.getValue()).map(ArrayList::new).orElse(null));
    }
    this.setHeaderMap(headerMap2);
    this.setSubmittedFileName(formDataDto.getSubmittedFileName());
    this.setValues(formDataDto.getValues());
  }

}
