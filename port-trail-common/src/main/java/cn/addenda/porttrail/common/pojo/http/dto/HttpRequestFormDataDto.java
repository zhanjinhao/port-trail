package cn.addenda.porttrail.common.pojo.http.dto;

import cn.addenda.porttrail.common.pojo.http.bo.HttpRequestFormData;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.*;

@Setter
@Getter
@ToString
public class HttpRequestFormDataDto implements Serializable {

  private static final long serialVersionUID = 1L;

  private String name;

  private long size;

  private Map<String, List<String>> headerMap;

  public HttpRequestFormDataDto(HttpRequestFormData httpRequestFormData) {
    this.setName(httpRequestFormData.getName());
    this.setSize(httpRequestFormData.getSize());
    Map<String, List<String>> headerMap2 = new HashMap<>();
    for (Map.Entry<String, List<String>> entry : httpRequestFormData.getHeaderMap().entrySet()) {
      headerMap2.put(entry.getKey(), Optional.ofNullable(entry.getValue()).map(ArrayList::new).orElse(null));
    }
    this.setHeaderMap(headerMap2);
  }

}
