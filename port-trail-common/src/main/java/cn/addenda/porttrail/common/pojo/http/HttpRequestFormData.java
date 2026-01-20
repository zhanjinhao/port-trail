package cn.addenda.porttrail.common.pojo.http;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@ToString
public class HttpRequestFormData implements Serializable {

  private static final long serialVersionUID = 1L;

  private String name;

  private long size;

  private Map<String, List<String>> headerMap;

}
