package cn.addenda.porttrail.common.pojo.http.bo;

import cn.addenda.porttrail.common.pojo.http.dto.HttpRequestFormDataDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.*;

@Setter
@Getter
@ToString
public class HttpRequestFormData {

  private String name;

  private long size;

  private Map<String, List<String>> headerMap;

  public HttpRequestFormData() {
  }

  public HttpRequestFormData(HttpRequestFormDataDto httpRequestFormDataDto) {
    this.setName(httpRequestFormDataDto.getName());
    this.setSize(httpRequestFormDataDto.getSize());
    Map<String, List<String>> headerMap2 = new HashMap<>();
    for (Map.Entry<String, List<String>> entry : httpRequestFormDataDto.getHeaderMap().entrySet()) {
      headerMap2.put(entry.getKey(), Optional.ofNullable(entry.getValue()).map(ArrayList::new).orElse(null));
    }
    this.setHeaderMap(headerMap2);
  }

}
