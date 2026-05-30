package cn.addenda.porttrail.common.pojo.servlet;

import java.util.ArrayList;

public class FormDataList extends ArrayList<FormData> {

  public FormDataList() {
  }

  public FormDataList(FormDataDtoList c) {
    super();
    for (FormDataDto formDataDto : c) {
      add(new FormData(formDataDto));
    }
  }

}
