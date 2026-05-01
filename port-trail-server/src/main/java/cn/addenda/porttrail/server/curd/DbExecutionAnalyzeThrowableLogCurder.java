package cn.addenda.porttrail.server.curd;

import java.util.*;

import cn.addenda.component.base.collection.BatchUtils;
import cn.addenda.component.mybatis.helper.BatchDmlHelper;
import cn.addenda.porttrail.server.mapper.DbExecutionAnalyzeThrowableLogMapper;
import cn.addenda.porttrail.server.entity.DbExecutionAnalyzeThrowableLog;
import org.springframework.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DbExecution在analyze阶段异常时记录的日志(DbExecutionAnalyzeThrowableLog)业务层
 *
 * @author addenda
 * @since 2026-03-06 22:52:40
 */
@Component
public class DbExecutionAnalyzeThrowableLogCurder {

  @Autowired
  private DbExecutionAnalyzeThrowableLogMapper dbExecutionAnalyzeThrowableLogMapper;

  @Autowired
  private BatchDmlHelper batchDmlHelper;

  public int insert(DbExecutionAnalyzeThrowableLog dbExecutionAnalyzeThrowableLog) {
    return dbExecutionAnalyzeThrowableLogMapper.insert(dbExecutionAnalyzeThrowableLog);
  }

  public int updateById(DbExecutionAnalyzeThrowableLog dbExecutionAnalyzeThrowableLog) {
    return dbExecutionAnalyzeThrowableLogMapper.updateById(dbExecutionAnalyzeThrowableLog);
  }

  public int deleteById(Long id) {
    return dbExecutionAnalyzeThrowableLogMapper.deleteById(id);
  }

  public void batchInsert(List<DbExecutionAnalyzeThrowableLog> dbExecutionAnalyzeThrowableLogList) {
    if (dbExecutionAnalyzeThrowableLogList == null) {
      return;
    }
    dbExecutionAnalyzeThrowableLogList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(dbExecutionAnalyzeThrowableLogList)) {
      return;
    }
    batchDmlHelper.batch(DbExecutionAnalyzeThrowableLogMapper.class, dbExecutionAnalyzeThrowableLogList,
            (mapper, dbExecutionAnalyzeThrowableLog) -> {
              mapper.insert(dbExecutionAnalyzeThrowableLog);
            });
  }

  public void batchUpdateById(List<DbExecutionAnalyzeThrowableLog> dbExecutionAnalyzeThrowableLogList) {
    if (dbExecutionAnalyzeThrowableLogList == null) {
      return;
    }
    dbExecutionAnalyzeThrowableLogList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(dbExecutionAnalyzeThrowableLogList)) {
      return;
    }
    batchDmlHelper.batch(DbExecutionAnalyzeThrowableLogMapper.class, dbExecutionAnalyzeThrowableLogList,
            (mapper, dbExecutionAnalyzeThrowableLog) -> {
              mapper.updateById(dbExecutionAnalyzeThrowableLog);
            });
  }

  public void batchDeleteById(List<Long> idList) {
    if (idList == null) {
      return;
    }
    idList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(idList)) {
      return;
    }
    batchDmlHelper.batch(DbExecutionAnalyzeThrowableLogMapper.class, idList,
            (mapper, id) -> {
              mapper.deleteById(id);
            });
  }

  public List<DbExecutionAnalyzeThrowableLog> queryByEntity(DbExecutionAnalyzeThrowableLog dbExecutionAnalyzeThrowableLog) {
    if (dbExecutionAnalyzeThrowableLog == null) {
      return new ArrayList<>();
    }

    return dbExecutionAnalyzeThrowableLogMapper.queryByEntity(dbExecutionAnalyzeThrowableLog);
  }

  public DbExecutionAnalyzeThrowableLog queryById(Long id) {
    if (id == null) {
      return null;
    }
    return dbExecutionAnalyzeThrowableLogMapper.queryById(id);
  }

  public List<DbExecutionAnalyzeThrowableLog> queryByIdList(List<Long> idList) {
    if (idList == null) {
      return new ArrayList<>();
    }
    idList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(idList)) {
      return new ArrayList<>();
    }
    return BatchUtils.applyListInBatches(idList,
            longs -> dbExecutionAnalyzeThrowableLogMapper.queryByIdList(longs));
  }

  public Map<Long, DbExecutionAnalyzeThrowableLog> queryMapByEntity(DbExecutionAnalyzeThrowableLog dbExecutionAnalyzeThrowableLog) {
    List<DbExecutionAnalyzeThrowableLog> dbExecutionAnalyzeThrowableLogList = queryByEntity(dbExecutionAnalyzeThrowableLog);
    return dbExecutionAnalyzeThrowableLogList.stream().collect(Collectors.toMap(DbExecutionAnalyzeThrowableLog::getId, a -> a));
  }

  public Map<Long, DbExecutionAnalyzeThrowableLog> queryMapByIdList(List<Long> idList) {
    List<DbExecutionAnalyzeThrowableLog> dbExecutionAnalyzeThrowableLogList = queryByIdList(idList);
    return dbExecutionAnalyzeThrowableLogList.stream().collect(Collectors.toMap(DbExecutionAnalyzeThrowableLog::getId, a -> a));
  }

}

