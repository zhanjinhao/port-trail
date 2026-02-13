package cn.addenda.porttrail.common.pojo.servlet.dto;

import cn.addenda.porttrail.common.pojo.servlet.bo.ServletRequestFormData;
import cn.addenda.porttrail.common.pojo.servlet.bo.ServletRequestFormDataList;

import java.util.ArrayList;

public class ServletRequestFormDataDtoList extends ArrayList<ServletRequestFormDataDto> {

  public ServletRequestFormDataDtoList() {
  }

  public ServletRequestFormDataDtoList(ServletRequestFormDataList c) {
    super();
    for (ServletRequestFormData servletRequestFormData : c) {
      add(new ServletRequestFormDataDto(servletRequestFormData));
    }
  }

}
