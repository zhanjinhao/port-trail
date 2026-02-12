package cn.addenda.porttrail.common.pojo.db.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
@ToString
public class PreparedStatementParameterDto implements Serializable {

  private static final long serialVersionUID = 1L;

  private int orderInStatement;

  private int orderInConnection;

  private List<String> setMethodList;

  private List<Class<?>> parameterTypeList;

  private List<byte[]> parameterValueList;

}
