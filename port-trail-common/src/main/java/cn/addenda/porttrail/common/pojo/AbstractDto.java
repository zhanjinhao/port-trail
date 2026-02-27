package cn.addenda.porttrail.common.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Setter
@Getter
@ToString
public abstract class AbstractDto implements Serializable {

  private static final long serialVersionUID = 1L;

  private ServiceRuntimeInfo serviceRuntimeInfo;

}
