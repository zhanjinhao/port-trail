package cn.addenda.porttrail.common.pojo.db.dto;

import cn.addenda.porttrail.common.pojo.AbstractDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public abstract class AbstractDbDto extends AbstractDto {

  private String dataSourcePortTrailId;

  private String connectionPortTrailId;

  private String statementPortTrailId;

}
