package cn.addenda.porttrail.server.biz;

import cn.addenda.component.base.jackson.util.JacksonUtils;
import cn.addenda.component.transaction.PlatformTransactionHelper;
import cn.addenda.porttrail.common.pojo.db.bo.PreparedStatementExecutionBo;
import cn.addenda.porttrail.common.pojo.db.bo.PreparedStatementParameter;
import cn.addenda.porttrail.common.pojo.db.dto.*;
import cn.addenda.porttrail.common.util.CompressUtils;
import cn.addenda.porttrail.common.util.DateUtils;
import cn.addenda.porttrail.common.util.JdkSerializationUtils;
import cn.addenda.porttrail.server.bo.EntryPointSnapshotEntityBo;
import cn.addenda.porttrail.server.bo.db.PreparedStatementExecutionEntityBo;
import cn.addenda.porttrail.server.bo.db.PreparedStatementParameterEntityBo;
import cn.addenda.porttrail.server.bo.db.StatementExecutionEntityBo;
import cn.addenda.porttrail.server.bo.db.StatementSqlEntityBo;
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
  private DbConfigEntityCurder dbConfigEntityCurder;

  @Autowired
  private PreparedStatementExecutionEntityCurder preparedStatementExecutionEntityCurder;

  @Autowired
  private PreparedStatementParameterEntityCurder preparedStatementParameterEntityCurder;

  @Autowired
  private StatementExecutionEntityCurder statementExecutionEntityCurder;

  @Autowired
  private StatementSqlEntityCurder statementSqlEntityCurder;

  @Autowired
  private EntryPointSnapshotEntityBiz entryPointSnapshotEntityBiz;

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
      EntryPointSnapshotEntityBo entryPointSnapshotEntityBo = entryPointSnapshotEntityBiz.insert(dbConfigDto.getEntryPointSnapshot());

      DbConfigEntity dbConfigEntity = DbConfigEntity.ofParam();
      dbConfigEntity.setSystemCode(dbConfigDto.getServiceRuntimeInfo().getSystemCode());
      dbConfigEntity.setServiceName(dbConfigDto.getServiceRuntimeInfo().getServiceName());
      dbConfigEntity.setImageName(dbConfigDto.getServiceRuntimeInfo().getImageName());
      dbConfigEntity.setEnv(dbConfigDto.getServiceRuntimeInfo().getEnv());
      dbConfigEntity.setInstanceId(dbConfigDto.getServiceRuntimeInfo().getInstanceId());
      dbConfigEntity.setDataSourcePortTrailId(dbConfigDto.getDataSourcePortTrailId());
      dbConfigEntity.setConnectionPortTrailId(dbConfigDto.getConnectionPortTrailId());
      dbConfigEntity.setStatementPortTrailId(dbConfigDto.getStatementPortTrailId());
      dbConfigEntity.setJdbcUrl(dbConfigDto.getJdbcUrl());
      dbConfigEntity.setUser(dbConfigDto.getUser());
      dbConfigEntity.setPassword(dbConfigDto.getPassword());
      dbConfigEntity.setDriverName(dbConfigDto.getDriverName());
      dbConfigEntity.setEntryPointSnapshotId(entryPointSnapshotEntityBo.getId());
      dbConfigEntityCurder.insert(dbConfigEntity);
    });
  }

  @Override
  public PreparedStatementExecutionEntityBo handlePreparedStatementExecution(PreparedStatementExecutionDto preparedStatementExecutionDto) {
    return transactionHelperNew.doTransaction(() -> {
      EntryPointSnapshotEntityBo entryPointSnapshotEntityBo = entryPointSnapshotEntityBiz.insert(preparedStatementExecutionDto.getEntryPointSnapshot());

      PreparedStatementExecutionEntity preparedStatementExecutionEntity = PreparedStatementExecutionEntity.ofParam();
      preparedStatementExecutionEntity.setSystemCode(preparedStatementExecutionDto.getServiceRuntimeInfo().getSystemCode());
      preparedStatementExecutionEntity.setServiceName(preparedStatementExecutionDto.getServiceRuntimeInfo().getServiceName());
      preparedStatementExecutionEntity.setImageName(preparedStatementExecutionDto.getServiceRuntimeInfo().getImageName());
      preparedStatementExecutionEntity.setEnv(preparedStatementExecutionDto.getServiceRuntimeInfo().getEnv());
      preparedStatementExecutionEntity.setInstanceId(preparedStatementExecutionDto.getServiceRuntimeInfo().getInstanceId());
      preparedStatementExecutionEntity.setDataSourcePortTrailId(preparedStatementExecutionDto.getDataSourcePortTrailId());
      preparedStatementExecutionEntity.setConnectionPortTrailId(preparedStatementExecutionDto.getConnectionPortTrailId());
      preparedStatementExecutionEntity.setStatementPortTrailId(preparedStatementExecutionDto.getStatementPortTrailId());
      preparedStatementExecutionEntity.setParameterizedSql(preparedStatementExecutionDto.getParameterizedSql());
      preparedStatementExecutionEntity.setStatementState(preparedStatementExecutionDto.getStatementState());
      preparedStatementExecutionEntity.setTxId(preparedStatementExecutionDto.getTxId());
      preparedStatementExecutionEntity.setStart(DateUtils.timestampToLocalDateTime(preparedStatementExecutionDto.getStart()));
      preparedStatementExecutionEntity.setEnd(DateUtils.timestampToLocalDateTime(preparedStatementExecutionDto.getEnd()));
      preparedStatementExecutionEntity.setCost((int) (preparedStatementExecutionDto.getEnd() - preparedStatementExecutionDto.getStart()));
      preparedStatementExecutionEntity.setEntryPointSnapshotId(entryPointSnapshotEntityBo.getId());
      preparedStatementExecutionEntityCurder.insert(preparedStatementExecutionEntity);

      PreparedStatementExecutionBo preparedStatementExecutionBo = new PreparedStatementExecutionBo(preparedStatementExecutionDto);
      List<PreparedStatementParameter> preparedStatementParameterList = preparedStatementExecutionBo.getPreparedStatementParameterList();
      List<PreparedStatementParameterDto> preparedStatementParameterDtoList = preparedStatementExecutionDto.getPreparedStatementParameterDtoList();

      List<PreparedStatementParameterEntity> preparedStatementParameterEntityList = new ArrayList<>();

      for (int i = 0; i < preparedStatementParameterList.size(); i++) {
        PreparedStatementParameter preparedStatementParameter = preparedStatementParameterList.get(i);
        PreparedStatementParameterDto preparedStatementParameterDto = preparedStatementParameterDtoList.get(i);

        PreparedStatementParameterEntity preparedStatementParameterEntity = PreparedStatementParameterEntity.ofParam();
        preparedStatementParameterEntityList.add(preparedStatementParameterEntity);

        preparedStatementParameterEntity.setPreparedStatementExecutionId(preparedStatementExecutionEntity.getId());
        preparedStatementParameterEntity.setParameterJson(JacksonUtils.toStr(preparedStatementParameter));
        preparedStatementParameterEntity.setParameterBytes(CompressUtils.compress(JdkSerializationUtils.serialize(preparedStatementParameterDto)));
        preparedStatementParameterEntity.setCapacity(preparedStatementParameter.getCapacity());
        preparedStatementParameterEntity.setOrderInStatement(preparedStatementParameter.getOrderInStatement());
        preparedStatementParameterEntity.setOrderInConnection(preparedStatementParameter.getOrderInConnection());
      }

      preparedStatementParameterEntityCurder.batchInsert(preparedStatementParameterEntityList);


      List<PreparedStatementParameterEntityBo> preparedStatementParameterEntityBoList = preparedStatementParameterEntityList.stream()
              .map(PreparedStatementParameterEntityBo::new)
              .collect(Collectors.toList());

      PreparedStatementExecutionEntityBo result = new PreparedStatementExecutionEntityBo(preparedStatementExecutionEntity);
      result.setEntryPointSnapshotEntityBo(entryPointSnapshotEntityBo);
      result.setPreparedStatementParameterEntityBoList(preparedStatementParameterEntityBoList);

      return result;
    });
  }

  @Override
  public StatementExecutionEntityBo handleStatementExecution(StatementExecutionDto statementExecutionDto) {
    return transactionHelperNew.doTransaction(() -> {
      EntryPointSnapshotEntityBo entryPointSnapshotEntityBo = entryPointSnapshotEntityBiz.insert(statementExecutionDto.getEntryPointSnapshot());

      StatementExecutionEntity statementExecutionEntity = StatementExecutionEntity.ofParam();
      statementExecutionEntity.setSystemCode(statementExecutionDto.getServiceRuntimeInfo().getSystemCode());
      statementExecutionEntity.setServiceName(statementExecutionDto.getServiceRuntimeInfo().getServiceName());
      statementExecutionEntity.setImageName(statementExecutionDto.getServiceRuntimeInfo().getImageName());
      statementExecutionEntity.setEnv(statementExecutionDto.getServiceRuntimeInfo().getEnv());
      statementExecutionEntity.setInstanceId(statementExecutionDto.getServiceRuntimeInfo().getInstanceId());
      statementExecutionEntity.setDataSourcePortTrailId(statementExecutionDto.getDataSourcePortTrailId());
      statementExecutionEntity.setConnectionPortTrailId(statementExecutionDto.getConnectionPortTrailId());
      statementExecutionEntity.setStatementPortTrailId(statementExecutionDto.getStatementPortTrailId());
      statementExecutionEntity.setStatementState(statementExecutionDto.getStatementState());
      statementExecutionEntity.setTxId(statementExecutionDto.getTxId());
      statementExecutionEntity.setStart(DateUtils.timestampToLocalDateTime(statementExecutionDto.getStart()));
      statementExecutionEntity.setEnd(DateUtils.timestampToLocalDateTime(statementExecutionDto.getEnd()));
      statementExecutionEntity.setCost((int) (statementExecutionDto.getEnd() - statementExecutionDto.getStart()));
      statementExecutionEntity.setEntryPointSnapshotId(entryPointSnapshotEntityBo.getId());
      statementExecutionEntityCurder.insert(statementExecutionEntity);

      List<StatementSqlDto> statementSqlDtoList = statementExecutionDto.getStatementSqlDtoList();
      List<StatementSqlEntity> statementSqlEntityList = new ArrayList<>();
      for (StatementSqlDto statementSqlDto : statementSqlDtoList) {
        StatementSqlEntity statementSqlEntity = StatementSqlEntity.ofParam();
        statementSqlEntityList.add(statementSqlEntity);
        statementSqlEntity.setStatementExecutionId(statementExecutionEntity.getId());
        statementSqlEntity.setSql(statementSqlDto.getSql());
        statementSqlEntity.setOrderInStatement(statementSqlDto.getOrderInStatement());
        statementSqlEntity.setOrderInConnection(statementSqlDto.getOrderInConnection());
      }

      statementSqlEntityCurder.batchInsert(statementSqlEntityList);

      List<StatementSqlEntityBo> statementSqlEntityBoList = statementSqlEntityList.stream()
              .map(StatementSqlEntityBo::new)
              .collect(Collectors.toList());

      StatementExecutionEntityBo result = new StatementExecutionEntityBo(statementExecutionEntity);
      result.setEntryPointSnapshotEntityBo(entryPointSnapshotEntityBo);
      result.setStatementSqlEntityBoList(statementSqlEntityBoList);

      return result;
    });

  }

}
