package cn.addenda.porttrail.server.biz;

import cn.addenda.component.base.jackson.util.JacksonUtils;
import cn.addenda.component.transaction.PlatformTransactionHelper;
import cn.addenda.porttrail.common.entrypoint.EntryPointSnapshot;
import cn.addenda.porttrail.common.pojo.db.bo.PreparedStatementExecutionBo;
import cn.addenda.porttrail.common.pojo.db.bo.PreparedStatementParameter;
import cn.addenda.porttrail.common.pojo.db.dto.*;
import cn.addenda.porttrail.common.util.CompressUtils;
import cn.addenda.porttrail.common.util.DateUtils;
import cn.addenda.porttrail.common.util.JdkSerializationUtils;
import cn.addenda.porttrail.server.biz.analyze.AnalyzeHandlerScheduler;
import cn.addenda.porttrail.server.bo.analyze.param.AnalyzePreparedStatementExecutionParam;
import cn.addenda.porttrail.server.bo.analyze.param.AnalyzeStatementExecutionParam;
import cn.addenda.porttrail.server.bo.est.*;
import cn.addenda.porttrail.server.curd.*;
import cn.addenda.porttrail.server.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DbExecutionBizImpl implements DbExecutionBiz {

  @Autowired
  private PlatformTransactionHelper transactionHelperNew;

  @Autowired
  private EstDbConfigCurder estDbConfigCurder;

  @Autowired
  private EstEntryPointCurder estEntryPointCurder;

  @Autowired
  private EstEntryPointSnapshotCurder estEntryPointSnapshotCurder;

  @Autowired
  private EstPreparedStatementExecutionCurder estPreparedStatementExecutionCurder;

  @Autowired
  private EstPreparedStatementParameterCurder estPreparedStatementParameterCurder;

  @Autowired
  private EstStatementExecutionCurder estStatementExecutionCurder;

  @Autowired
  private EstStatementSqlCurder estStatementSqlCurder;

  @Autowired
  private AnalyzeHandlerScheduler analyzeHandlerScheduler;

  /**
   * 数据库配置数据是要落库的。
   */
  @Override
  public void handleDbConfig(DbConfigDto dbConfigDto) {
    try {
      insertDbConfig(dbConfigDto);
    } catch (DuplicateKeyException duplicateKeyException) {
      log.error("connection[{}]已经写入DbConfig表，无法再写入。当前的DbConfigDto：{}。",
              dbConfigDto.getConnectionPortTrailId(), JacksonUtils.toStr(dbConfigDto), duplicateKeyException);
    } catch (Throwable t1) {
      log.error("connection[{}]写入DbConfig表异常，重试写入。当前的DbConfigDto：{}。",
              dbConfigDto.getConnectionPortTrailId(), JacksonUtils.toStr(dbConfigDto), t1);
      try {
        insertDbConfig(dbConfigDto);
      } catch (Throwable t2) {
        log.error("connection[{}]写入DbConfig表再次异常，重试写入。当前的DbConfigDto：{}。",
                dbConfigDto.getConnectionPortTrailId(), JacksonUtils.toStr(dbConfigDto), t2);
      }
    }
  }

  private void insertDbConfig(DbConfigDto dbConfigDto) {
    transactionHelperNew.doTransaction(() -> {
      EntryPointSnapshot entryPointSnapshot = dbConfigDto.getEntryPointSnapshot();
      EstEntryPointSnapshot estEntryPointSnapshot = EstEntryPointSnapshot.ofParam();
      estEntryPointSnapshot.setThreadName(entryPointSnapshot.getThreadName());
      estEntryPointSnapshotCurder.insert(estEntryPointSnapshot);

      List<EstEntryPoint> estEntryPointList = entryPointSnapshot.getEntryPointList()
              .stream().map(entryPoint -> {
                EstEntryPoint estEntryPoint = EstEntryPoint.ofParam();
                estEntryPoint.setEntryPointType(entryPoint.getEntryPointType().toString());
                estEntryPoint.setDetail(entryPoint.getDetail());
                estEntryPoint.setEntryId(entryPoint.getEntryId());
                estEntryPoint.setEntryPointSnapshotId(estEntryPointSnapshot.getId());
                return estEntryPoint;
              }).collect(Collectors.toList());
      estEntryPointCurder.batchInsert(estEntryPointList);

      EstDbConfig estDbConfig = EstDbConfig.ofParam();
      estDbConfig.setSystemCode(dbConfigDto.getServiceRuntimeInfo().getSystemCode());
      estDbConfig.setServiceName(dbConfigDto.getServiceRuntimeInfo().getServiceName());
      estDbConfig.setImageName(dbConfigDto.getServiceRuntimeInfo().getImageName());
      estDbConfig.setEnv(dbConfigDto.getServiceRuntimeInfo().getEnv());
      estDbConfig.setInstanceId(dbConfigDto.getServiceRuntimeInfo().getInstanceId());
      estDbConfig.setDataSourcePortTrailId(dbConfigDto.getDataSourcePortTrailId());
      estDbConfig.setConnectionPortTrailId(dbConfigDto.getConnectionPortTrailId());
      estDbConfig.setStatementPortTrailId(dbConfigDto.getStatementPortTrailId());
      estDbConfig.setJdbcUrl(dbConfigDto.getJdbcUrl());
      estDbConfig.setUser(dbConfigDto.getUser());
      estDbConfig.setPassword(dbConfigDto.getPassword());
      estDbConfig.setDriverName(dbConfigDto.getDriverName());
      estDbConfig.setEntryPointSnapshotId(estEntryPointSnapshot.getId());
      estDbConfigCurder.insert(estDbConfig);
    });
  }

  @Override
  public void handlePreparedStatementExecution(PreparedStatementExecutionDto preparedStatementExecutionDto) {
    EstPreparedStatementExecutionBo estPreparedStatementExecutionBo = transactionHelperNew.doTransaction(() -> {
      EntryPointSnapshot entryPointSnapshot = preparedStatementExecutionDto.getEntryPointSnapshot();
      EstEntryPointSnapshot estEntryPointSnapshot = EstEntryPointSnapshot.ofParam();
      estEntryPointSnapshot.setThreadName(entryPointSnapshot.getThreadName());
      estEntryPointSnapshotCurder.insert(estEntryPointSnapshot);

      List<EstEntryPoint> estEntryPointList = entryPointSnapshot.getEntryPointList()
              .stream().map(entryPoint -> {
                EstEntryPoint estEntryPoint = EstEntryPoint.ofParam();
                estEntryPoint.setEntryPointType(entryPoint.getEntryPointType().toString());
                estEntryPoint.setDetail(entryPoint.getDetail());
                estEntryPoint.setEntryId(entryPoint.getEntryId());
                estEntryPoint.setEntryPointSnapshotId(estEntryPointSnapshot.getId());
                return estEntryPoint;
              }).collect(Collectors.toList());
      estEntryPointCurder.batchInsert(estEntryPointList);

      EstPreparedStatementExecution estPreparedStatementExecution = EstPreparedStatementExecution.ofParam();
      estPreparedStatementExecution.setSystemCode(preparedStatementExecutionDto.getServiceRuntimeInfo().getSystemCode());
      estPreparedStatementExecution.setServiceName(preparedStatementExecutionDto.getServiceRuntimeInfo().getServiceName());
      estPreparedStatementExecution.setImageName(preparedStatementExecutionDto.getServiceRuntimeInfo().getImageName());
      estPreparedStatementExecution.setEnv(preparedStatementExecutionDto.getServiceRuntimeInfo().getEnv());
      estPreparedStatementExecution.setInstanceId(preparedStatementExecutionDto.getServiceRuntimeInfo().getInstanceId());
      estPreparedStatementExecution.setDataSourcePortTrailId(preparedStatementExecutionDto.getDataSourcePortTrailId());
      estPreparedStatementExecution.setConnectionPortTrailId(preparedStatementExecutionDto.getConnectionPortTrailId());
      estPreparedStatementExecution.setStatementPortTrailId(preparedStatementExecutionDto.getStatementPortTrailId());
      estPreparedStatementExecution.setParameterizedSql(preparedStatementExecutionDto.getParameterizedSql());
      estPreparedStatementExecution.setStatementState(preparedStatementExecutionDto.getStatementState());
      estPreparedStatementExecution.setTxId(preparedStatementExecutionDto.getTxId());
      estPreparedStatementExecution.setStart(DateUtils.timestampToLocalDateTime(preparedStatementExecutionDto.getStart()));
      estPreparedStatementExecution.setEnd(DateUtils.timestampToLocalDateTime(preparedStatementExecutionDto.getEnd()));
      estPreparedStatementExecution.setCost((int) (preparedStatementExecutionDto.getEnd() - preparedStatementExecutionDto.getStart()));
      estPreparedStatementExecution.setEntryPointSnapshotId(estEntryPointSnapshot.getId());
      estPreparedStatementExecutionCurder.insert(estPreparedStatementExecution);

      PreparedStatementExecutionBo preparedStatementExecutionBo = new PreparedStatementExecutionBo(preparedStatementExecutionDto);
      List<PreparedStatementParameter> preparedStatementParameterList = preparedStatementExecutionBo.getPreparedStatementParameterList();
      List<PreparedStatementParameterDto> preparedStatementParameterDtoList = preparedStatementExecutionDto.getPreparedStatementParameterDtoList();

      List<EstPreparedStatementParameter> estPreparedStatementParameterList = new ArrayList<>();

      for (int i = 0; i < preparedStatementParameterList.size(); i++) {
        PreparedStatementParameter preparedStatementParameter = preparedStatementParameterList.get(i);
        PreparedStatementParameterDto preparedStatementParameterDto = preparedStatementParameterDtoList.get(i);

        EstPreparedStatementParameter estPreparedStatementParameter = EstPreparedStatementParameter.ofParam();
        estPreparedStatementParameterList.add(estPreparedStatementParameter);

        estPreparedStatementParameter.setPreparedStatementExecutionId(estPreparedStatementExecution.getId());
        estPreparedStatementParameter.setParameterJson(JacksonUtils.toStr(preparedStatementParameter));
        estPreparedStatementParameter.setParameterBytes(CompressUtils.compress(JdkSerializationUtils.serialize(preparedStatementParameterDto)));
        estPreparedStatementParameter.setCapacity(preparedStatementParameter.getCapacity());
        estPreparedStatementParameter.setOrderInConnection(preparedStatementParameter.getOrderInConnection());
        estPreparedStatementParameter.setOrderInStatement(preparedStatementParameter.getOrderInStatement());
        estPreparedStatementParameter.setPreparedStatementExecutionId(estPreparedStatementExecution.getId());
      }

      estPreparedStatementParameterCurder.batchInsert(estPreparedStatementParameterList);

      List<EstEntryPointBo> estEntryPointBoList = estEntryPointList.stream()
              .map(EstEntryPointBo::new)
              .collect(Collectors.toList());
      EstEntryPointSnapshotBo estEntryPointSnapshotBo = new EstEntryPointSnapshotBo(estEntryPointSnapshot);
      estEntryPointSnapshotBo.setEstEntryPointBoList(estEntryPointBoList);

      List<EstPreparedStatementParameterBo> estPreparedStatementParameterBoList = estPreparedStatementParameterList.stream()
              .map(EstPreparedStatementParameterBo::new)
              .collect(Collectors.toList());

      EstPreparedStatementExecutionBo result = new EstPreparedStatementExecutionBo(estPreparedStatementExecution);
      result.setEstEntryPointSnapshotBo(estEntryPointSnapshotBo);
      result.setEstPreparedStatementParameterBoList(estPreparedStatementParameterBoList);

      return result;
    });

    AnalyzePreparedStatementExecutionParam analyzePreparedStatementExecutionParam = new AnalyzePreparedStatementExecutionParam();
    analyzePreparedStatementExecutionParam.setEstPreparedStatementExecutionBo(estPreparedStatementExecutionBo);
    analyzePreparedStatementExecutionParam.setPreparedStatementExecutionDto(preparedStatementExecutionDto);
    analyzeHandlerScheduler.handle(analyzePreparedStatementExecutionParam);
  }

  @Override
  public void handleStatementExecution(StatementExecutionDto statementExecutionDto) {
    EstStatementExecutionBo estStatementExecutionBo = transactionHelperNew.doTransaction(() -> {
      EntryPointSnapshot entryPointSnapshot = statementExecutionDto.getEntryPointSnapshot();
      EstEntryPointSnapshot estEntryPointSnapshot = EstEntryPointSnapshot.ofParam();
      estEntryPointSnapshot.setThreadName(entryPointSnapshot.getThreadName());
      estEntryPointSnapshotCurder.insert(estEntryPointSnapshot);

      List<EstEntryPoint> estEntryPointList = entryPointSnapshot.getEntryPointList()
              .stream().map(entryPoint -> {
                EstEntryPoint estEntryPoint = EstEntryPoint.ofParam();
                estEntryPoint.setEntryPointType(entryPoint.getEntryPointType().toString());
                estEntryPoint.setDetail(entryPoint.getDetail());
                estEntryPoint.setEntryId(entryPoint.getEntryId());
                estEntryPoint.setEntryPointSnapshotId(estEntryPointSnapshot.getId());
                return estEntryPoint;
              }).collect(Collectors.toList());
      estEntryPointCurder.batchInsert(estEntryPointList);

      EstStatementExecution estStatementExecution = EstStatementExecution.ofParam();
      estStatementExecution.setSystemCode(statementExecutionDto.getServiceRuntimeInfo().getSystemCode());
      estStatementExecution.setServiceName(statementExecutionDto.getServiceRuntimeInfo().getServiceName());
      estStatementExecution.setImageName(statementExecutionDto.getServiceRuntimeInfo().getImageName());
      estStatementExecution.setEnv(statementExecutionDto.getServiceRuntimeInfo().getEnv());
      estStatementExecution.setInstanceId(statementExecutionDto.getServiceRuntimeInfo().getInstanceId());
      estStatementExecution.setDataSourcePortTrailId(statementExecutionDto.getDataSourcePortTrailId());
      estStatementExecution.setConnectionPortTrailId(statementExecutionDto.getConnectionPortTrailId());
      estStatementExecution.setStatementPortTrailId(statementExecutionDto.getStatementPortTrailId());
      estStatementExecution.setStatementState(statementExecutionDto.getStatementState());
      estStatementExecution.setTxId(statementExecutionDto.getTxId());
      estStatementExecution.setStart(DateUtils.timestampToLocalDateTime(statementExecutionDto.getStart()));
      estStatementExecution.setEnd(DateUtils.timestampToLocalDateTime(statementExecutionDto.getEnd()));
      estStatementExecution.setCost((int) (statementExecutionDto.getEnd() - statementExecutionDto.getStart()));
      estStatementExecution.setEntryPointSnapshotId(estEntryPointSnapshot.getId());
      estStatementExecutionCurder.insert(estStatementExecution);

      List<StatementSqlDto> statementSqlDtoList = statementExecutionDto.getStatementSqlDtoList();
      List<EstStatementSql> estStatementSqlList = new ArrayList<>();
      for (StatementSqlDto statementSqlDto : statementSqlDtoList) {
        EstStatementSql estStatementSql = EstStatementSql.ofParam();
        estStatementSqlList.add(estStatementSql);
        estStatementSql.setStatementExecutionId(estStatementExecution.getId());
        estStatementSql.setSql(statementSqlDto.getSql());
        estStatementSql.setOrderInStatement(statementSqlDto.getOrderInStatement());
        estStatementSql.setOrderInConnection(statementSqlDto.getOrderInConnection());
      }

      estStatementSqlCurder.batchInsert(estStatementSqlList);

      List<EstEntryPointBo> estEntryPointBoList = estEntryPointList.stream()
              .map(EstEntryPointBo::new)
              .collect(Collectors.toList());
      EstEntryPointSnapshotBo estEntryPointSnapshotBo = new EstEntryPointSnapshotBo(estEntryPointSnapshot);
      estEntryPointSnapshotBo.setEstEntryPointBoList(estEntryPointBoList);

      List<EstStatementSqlBo> estStatementSqlBoList = estStatementSqlList.stream()
              .map(EstStatementSqlBo::new)
              .collect(Collectors.toList());

      EstStatementExecutionBo result = new EstStatementExecutionBo(estStatementExecution);
      result.setEstEntryPointSnapshotBo(estEntryPointSnapshotBo);
      result.setEstStatementSqlBoList(estStatementSqlBoList);

      return result;
    });

    AnalyzeStatementExecutionParam analyzeStatementExecutionParam = new AnalyzeStatementExecutionParam();
    analyzeStatementExecutionParam.setEstStatementExecutionBo(estStatementExecutionBo);
    analyzeStatementExecutionParam.setStatementExecutionDto(statementExecutionDto);
    analyzeHandlerScheduler.handle(analyzeStatementExecutionParam);
  }

}
