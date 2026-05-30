package cn.addenda.porttrail.common.pojo.servlet;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Setter
@Getter
@ToString
public class LocaleDataDto implements Serializable {

  private static final long serialVersionUID = 1L;

  private String language;
  private String country;
  private String variant;

  public LocaleDataDto() {
  }

  public LocaleDataDto(LocaleData localeData) {
    this.language = localeData.getLanguage();
    this.country = localeData.getCountry();
    this.variant = localeData.getVariant();
  }

}
