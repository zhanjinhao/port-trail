package cn.addenda.porttrail.common.pojo.servlet;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.*;

@Setter
@Getter
@ToString
public class FormDataDto implements Serializable {

  private static final long serialVersionUID = 1L;

  private String contentType;

  private String name;

  private long size;

  private Map<String, List<String>> headerMap;

  private String submittedFileName;

  private String[] values;

  public FormDataDto() {
  }

  public FormDataDto(FormData formData) {
    this.setContentType(formData.getContentType());
    this.setName(formData.getName());
    this.setSize(formData.getSize());
    Map<String, List<String>> headerMap2 = new HashMap<>();
    for (Map.Entry<String, List<String>> entry : formData.getHeaderMap().entrySet()) {
      headerMap2.put(entry.getKey(), Optional.ofNullable(entry.getValue()).map(ArrayList::new).orElse(null));
    }
    this.setHeaderMap(headerMap2);
    this.setSubmittedFileName(formData.getSubmittedFileName());
    this.setValues(formData.getValues());
  }

}
