package cn.addenda.porttrail.server.controller;

import cn.addenda.component.base.jackson.util.JacksonUtils;
import cn.addenda.porttrail.common.pojo.db.bo.DbConfigBo;
import cn.addenda.porttrail.common.pojo.db.bo.PreparedStatementExecutionBo;
import cn.addenda.porttrail.common.pojo.db.bo.StatementExecutionBo;
import cn.addenda.porttrail.common.pojo.db.dto.DbConfigDto;
import cn.addenda.porttrail.common.pojo.db.dto.PreparedStatementExecutionDto;
import cn.addenda.porttrail.common.pojo.db.dto.StatementExecutionDto;
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
    DbConfigBo dbConfigBo = new DbConfigBo(dbConfigDto);
    System.out.println(JacksonUtils.toStr(dbConfigBo) + " of " + JacksonUtils.toStr(dbConfigDto.getServiceRuntimeInfo()));
  }

  @PostMapping(value = "receiveStatementExecution", consumes = "application/octet-stream")
  public void receiveStatementExecution(@RequestBody byte[] bytes) {
    // 处理接收到的字节数组
    bytes = CompressUtils.decompress(bytes);
    // 可以通过 JacksonUtils 或其他方式反序列化
    StatementExecutionDto statementExecutionDto = (StatementExecutionDto) JdkSerializationUtils.deserialize(bytes);
    StatementExecutionBo statementExecutionBo = new StatementExecutionBo(statementExecutionDto);
    System.out.println(JacksonUtils.toStr(statementExecutionBo) + " of " + JacksonUtils.toStr(statementExecutionDto.getServiceRuntimeInfo()));
  }

  @PostMapping(value = "receivePreparedStatementExecution", consumes = "application/octet-stream")
  public void receivePreparedStatementExecution(@RequestBody byte[] bytes) {
    // 处理接收到的字节数组
    bytes = CompressUtils.decompress(bytes);
    // 可以通过 JacksonUtils 或其他方式反序列化
    PreparedStatementExecutionDto preparedStatementExecutionDto = (PreparedStatementExecutionDto) JdkSerializationUtils.deserialize(bytes);
    PreparedStatementExecutionBo preparedStatementExecutionBo = new PreparedStatementExecutionBo(preparedStatementExecutionDto);
    System.out.println(JacksonUtils.toStr(preparedStatementExecutionBo) + " of " + JacksonUtils.toStr(preparedStatementExecutionDto.getServiceRuntimeInfo()));
  }

}
