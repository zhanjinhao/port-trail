package cn.addenda.porttrail.server.controller;

import cn.addenda.component.base.jackson.util.JacksonUtils;
import cn.addenda.porttrail.common.pojo.db.bo.DbConfigBo;
import cn.addenda.porttrail.common.pojo.db.bo.PreparedSqlExecutionBo;
import cn.addenda.porttrail.common.pojo.db.bo.SqlExecutionBo;
import cn.addenda.porttrail.common.pojo.db.dto.DbConfigDto;
import cn.addenda.porttrail.common.pojo.db.dto.PreparedSqlExecutionDto;
import cn.addenda.porttrail.common.pojo.db.dto.SqlExecutionDto;
import cn.addenda.porttrail.common.util.CompressUtils;
import cn.addenda.porttrail.common.util.JdkSerializationUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("dbExecution")
public class DbExecutionController {

  @PostMapping("receiveDbConfig")
  public void receiveDbConfig(@RequestBody DbConfigDto dbConfigDto) {
    DbConfigBo dbConfigBo = DbConfigBo.createByDbConfigDto(dbConfigDto);
    System.out.println(JacksonUtils.toStr(dbConfigBo) + " of " + JacksonUtils.toStr(dbConfigDto.getServiceRuntimeInfo()));
  }

  @PostMapping(value = "receiveSqlExecution", consumes = "application/octet-stream")
  public void receiveSqlExecution(@RequestBody byte[] bytes) {
    // 处理接收到的字节数组
    bytes = CompressUtils.decompress(bytes);
    // 可以通过 JacksonUtils 或其他方式反序列化
    SqlExecutionDto sqlExecutionDto = (SqlExecutionDto) JdkSerializationUtils.deserialize(bytes);
    SqlExecutionBo sqlExecutionBo =
            SqlExecutionBo.createBySqlExecutionDto(sqlExecutionDto);
    System.out.println(JacksonUtils.toStr(sqlExecutionBo) + " of " + JacksonUtils.toStr(sqlExecutionDto.getServiceRuntimeInfo()));
  }

  @PostMapping(value = "receivePreparedSqlExecution", consumes = "application/octet-stream")
  public void receivePreparedSqlExecution(@RequestBody byte[] bytes) {
    // 处理接收到的字节数组
    bytes = CompressUtils.decompress(bytes);
    // 可以通过 JacksonUtils 或其他方式反序列化
    PreparedSqlExecutionDto preparedSqlExecutionDto = (PreparedSqlExecutionDto) JdkSerializationUtils.deserialize(bytes);
    PreparedSqlExecutionBo preparedSqlExecutionBo =
            PreparedSqlExecutionBo.createByPreparedSqlExecutionDto(preparedSqlExecutionDto);
    System.out.println(JacksonUtils.toStr(preparedSqlExecutionBo) + " of " + JacksonUtils.toStr(preparedSqlExecutionDto.getServiceRuntimeInfo()));
  }

}
