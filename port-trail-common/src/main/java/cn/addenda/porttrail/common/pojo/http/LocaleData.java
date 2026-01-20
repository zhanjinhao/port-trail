package cn.addenda.porttrail.common.pojo.http;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Setter
@Getter
@ToString
public class LocaleData implements Serializable {

  private static final long serialVersionUID = 1L;

  private String language;
  private String country;
  private String variant;

  public LocaleData() {
  }

  public LocaleData(String language, String country, String variant) {
    this.language = language;
    this.country = country;
    this.variant = variant;
  }

}
