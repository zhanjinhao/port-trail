package cn.addenda.porttrail.common.pojo.servlet.bo;

import cn.addenda.porttrail.common.pojo.servlet.dto.ServletRequestFormDataDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.*;

@Setter
@Getter
@ToString
public class ServletRequestFormData {

  private String contentType;

  private String name;

  private long size;

  private Map<String, List<String>> headerMap;

  private String submittedFileName;

  private String[] values;

  public ServletRequestFormData() {
  }

  public ServletRequestFormData(ServletRequestFormDataDto servletRequestFormDataDto) {
    this.setContentType(servletRequestFormDataDto.getContentType());
    this.setName(servletRequestFormDataDto.getName());
    this.setSize(servletRequestFormDataDto.getSize());
    Map<String, List<String>> headerMap2 = new HashMap<>();
    for (Map.Entry<String, List<String>> entry : servletRequestFormDataDto.getHeaderMap().entrySet()) {
      headerMap2.put(entry.getKey(), Optional.ofNullable(entry.getValue()).map(ArrayList::new).orElse(null));
    }
    this.setHeaderMap(headerMap2);
    this.setSubmittedFileName(servletRequestFormDataDto.getSubmittedFileName());
    this.setValues(servletRequestFormDataDto.getValues());
  }

}
