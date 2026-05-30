package cn.addenda.porttrail.common.pojo;

import java.util.ArrayList;

public class FormDataDtoList extends ArrayList<FormDataDto> {

  public FormDataDtoList() {
  }

  public FormDataDtoList(FormDataList c) {
    super();
    for (FormData formData : c) {
      add(new FormDataDto(formData));
    }
  }

}
