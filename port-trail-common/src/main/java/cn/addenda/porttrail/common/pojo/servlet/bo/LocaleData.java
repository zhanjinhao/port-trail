package cn.addenda.porttrail.common.pojo.servlet.bo;

import cn.addenda.porttrail.common.pojo.servlet.dto.LocaleDataDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class LocaleData {

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

  public LocaleData(LocaleDataDto localeDataDto) {
    this.language = localeDataDto.getCountry();
    this.country = localeDataDto.getCountry();
    this.variant = localeDataDto.getVariant();
  }

}
