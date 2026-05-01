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
import cn.addenda.porttrail.server.biz.DbExecutionBiz;
import cn.addenda.porttrail.server.biz.analyze.AnalyzeHandlerScheduler;
import cn.addenda.porttrail.server.bo.analyze.param.AnalyzePreparedStatementExecutionParam;
import cn.addenda.porttrail.server.bo.analyze.param.AnalyzeStatementExecutionParam;
import cn.addenda.porttrail.server.bo.est.EstPreparedStatementExecutionBo;
import cn.addenda.porttrail.server.bo.est.EstStatementExecutionBo;
import cn.addenda.porttrail.server.entity.DbExecutionAnalyzeThrowableLog;
import cn.addenda.porttrail.server.entity.PortTrailDeserializeThrowableLog;
import cn.addenda.porttrail.server.entity.DbExecutionHandleThrowableLog;
import cn.addenda.porttrail.server.manager.DbExecutionAnalyzeThrowableLogManager;
import cn.addenda.porttrail.server.manager.PortTrailDeserializeThrowableLogManager;
import cn.addenda.porttrail.server.manager.DbExecutionHandleThrowableLogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("dbExecution")
public class DbExecutionController {

  @Autowired
  private DbExecutionBiz dbExecutionBiz;

  @Autowired
  private AnalyzeHandlerScheduler analyzeHandlerScheduler;

  @Autowired
  private PortTrailDeserializeThrowableLogManager portTrailDeserializeThrowableLogManager;

  @Autowired
  private DbExecutionHandleThrowableLogManager dbExecutionHandleThrowableLogManager;

  @Autowired
  private DbExecutionAnalyzeThrowableLogManager dbExecutionAnalyzeThrowableLogManager;

  @PostMapping("receiveDbConfig")
  public void receiveDbConfig(@RequestBody DbConfigDto dbConfigDto) {
    DbConfigBo dbConfigBo = new DbConfigBo(dbConfigDto);
    dbExecutionBiz.handleDbConfig(dbConfigDto);
    System.out.println(JacksonUtils.toStr(dbConfigBo) + " of " + JacksonUtils.toStr(dbConfigDto.getServiceRuntimeInfo()));
  }

  @PostMapping(value = "receiveStatementExecution", consumes = "application/octet-stream")
  public void receiveStatementExecution(@RequestBody byte[] bytes) {
    StatementExecutionDto statementExecutionDto;
    StatementExecutionBo statementExecutionBo;
    try {
      // 处理接收到的字节数组
      bytes = CompressUtils.decompress(bytes);
      statementExecutionDto = (StatementExecutionDto) JdkSerializationUtils.deserialize(bytes);
      statementExecutionBo = new StatementExecutionBo(statementExecutionDto);
    } catch (Throwable throwable) {
      portTrailDeserializeThrowableLogManager.insert(bytes, PortTrailDeserializeThrowableLog.DESERIALIZE_TYPE_STATEMENT_EXECUTION, throwable);
      return;
    }

    EstStatementExecutionBo estStatementExecutionBo;
    try {
      estStatementExecutionBo = dbExecutionBiz.handleStatementExecution(statementExecutionDto);
    } catch (Throwable throwable) {
      dbExecutionHandleThrowableLogManager.insert(bytes, DbExecutionHandleThrowableLog.HANDLE_TYPE_STATEMENT_EXECUTION,
              statementExecutionBo, statementExecutionDto.getServiceRuntimeInfo(), throwable);
      return;
    }

    try {
      AnalyzeStatementExecutionParam analyzeStatementExecutionParam = new AnalyzeStatementExecutionParam();
      analyzeStatementExecutionParam.setEstStatementExecutionBo(estStatementExecutionBo);
      analyzeStatementExecutionParam.setStatementExecutionDto(statementExecutionDto);
      analyzeHandlerScheduler.handle(analyzeStatementExecutionParam);
    } catch (Throwable throwable) {
      dbExecutionAnalyzeThrowableLogManager.insert(bytes, DbExecutionAnalyzeThrowableLog.ANALYZE_TYPE_STATEMENT_EXECUTION,
              statementExecutionBo, statementExecutionDto.getServiceRuntimeInfo(), estStatementExecutionBo.getId(), throwable);
    }
  }

  @PostMapping(value = "receivePreparedStatementExecution", consumes = "application/octet-stream")
  public void receivePreparedStatementExecution(@RequestBody byte[] bytes) {
    PreparedStatementExecutionDto preparedStatementExecutionDto;
    PreparedStatementExecutionBo preparedStatementExecutionBo;
    try {
      // 处理接收到的字节数组
      bytes = CompressUtils.decompress(bytes);
      preparedStatementExecutionDto = (PreparedStatementExecutionDto) JdkSerializationUtils.deserialize(bytes);
      preparedStatementExecutionBo = new PreparedStatementExecutionBo(preparedStatementExecutionDto);
    } catch (Throwable throwable) {
      portTrailDeserializeThrowableLogManager.insert(bytes, PortTrailDeserializeThrowableLog.DESERIALIZE_TYPE_PREPARED_STATEMENT_EXECUTION, throwable);
      return;
    }

    EstPreparedStatementExecutionBo estPreparedStatementExecutionBo;
    try {
      estPreparedStatementExecutionBo = dbExecutionBiz.handlePreparedStatementExecution(preparedStatementExecutionDto);
    } catch (Throwable throwable) {
      dbExecutionHandleThrowableLogManager.insert(bytes, DbExecutionHandleThrowableLog.HANDLE_TYPE_PREPARED_STATEMENT_EXECUTION,
              preparedStatementExecutionBo, preparedStatementExecutionDto.getServiceRuntimeInfo(), throwable);
      return;
    }

    try {
      AnalyzePreparedStatementExecutionParam analyzePreparedStatementExecutionParam = new AnalyzePreparedStatementExecutionParam();
      analyzePreparedStatementExecutionParam.setEstPreparedStatementExecutionBo(estPreparedStatementExecutionBo);
      analyzePreparedStatementExecutionParam.setPreparedStatementExecutionDto(preparedStatementExecutionDto);
      analyzeHandlerScheduler.handle(analyzePreparedStatementExecutionParam);
    } catch (Throwable throwable) {
      dbExecutionAnalyzeThrowableLogManager.insert(bytes, DbExecutionAnalyzeThrowableLog.ANALYZE_TYPE_PREPARED_STATEMENT_EXECUTION,
              preparedStatementExecutionBo, preparedStatementExecutionDto.getServiceRuntimeInfo(), estPreparedStatementExecutionBo.getId(), throwable);
    }
  }

}
