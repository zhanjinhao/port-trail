package cn.addenda.porttrail.common.pojo.servlet.bo;

import cn.addenda.porttrail.common.pojo.servlet.dto.ServletRequestFormDataDto;
import cn.addenda.porttrail.common.pojo.servlet.dto.ServletRequestFormDataDtoList;

import java.util.ArrayList;

public class ServletRequestFormDataList extends ArrayList<ServletRequestFormData> {

  public ServletRequestFormDataList() {
  }

  public ServletRequestFormDataList(ServletRequestFormDataDtoList c) {
    super();
    for (ServletRequestFormDataDto servletRequestFormDataDto : c) {
      add(new ServletRequestFormData(servletRequestFormDataDto));
    }
  }

}
