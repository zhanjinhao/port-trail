package cn.addenda.porttrail.server.curd;

import java.util.*;

import cn.addenda.component.base.collection.BatchUtils;
import cn.addenda.component.mybatis.helper.BatchDmlHelper;
import cn.addenda.porttrail.server.mapper.DbExecutionHandleThrowableLogMapper;
import cn.addenda.porttrail.server.entity.DbExecutionHandleThrowableLog;
import org.springframework.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

import java.util.List;
import java.util.stream.Collectors;

/**
 * handle阶段异常时记录的日志(DbExecutionHandleThrowableLog)业务层
 *
 * @author addenda
 * @since 2026-03-06 22:44:42
 */
@Component
public class DbExecutionHandleThrowableLogCurder {

  @Autowired
  private DbExecutionHandleThrowableLogMapper dbExecutionHandleThrowableLogMapper;

  @Autowired
  private BatchDmlHelper batchDmlHelper;

  public int insert(DbExecutionHandleThrowableLog dbExecutionHandleThrowableLog) {
    return dbExecutionHandleThrowableLogMapper.insert(dbExecutionHandleThrowableLog);
  }

  public int updateById(DbExecutionHandleThrowableLog dbExecutionHandleThrowableLog) {
    return dbExecutionHandleThrowableLogMapper.updateById(dbExecutionHandleThrowableLog);
  }

  public int deleteById(Long id) {
    return dbExecutionHandleThrowableLogMapper.deleteById(id);
  }

  public void batchInsert(List<DbExecutionHandleThrowableLog> dbExecutionHandleThrowableLogList) {
    if (dbExecutionHandleThrowableLogList == null) {
      return;
    }
    dbExecutionHandleThrowableLogList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(dbExecutionHandleThrowableLogList)) {
      return;
    }
    batchDmlHelper.batch(DbExecutionHandleThrowableLogMapper.class, dbExecutionHandleThrowableLogList,
            (mapper, dbExecutionHandleThrowableLog) -> {
              mapper.insert(dbExecutionHandleThrowableLog);
            });
  }

  public void batchUpdateById(List<DbExecutionHandleThrowableLog> dbExecutionHandleThrowableLogList) {
    if (dbExecutionHandleThrowableLogList == null) {
      return;
    }
    dbExecutionHandleThrowableLogList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(dbExecutionHandleThrowableLogList)) {
      return;
    }
    batchDmlHelper.batch(DbExecutionHandleThrowableLogMapper.class, dbExecutionHandleThrowableLogList,
            (mapper, dbExecutionHandleThrowableLog) -> {
              mapper.updateById(dbExecutionHandleThrowableLog);
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
    batchDmlHelper.batch(DbExecutionHandleThrowableLogMapper.class, idList,
            (mapper, id) -> {
              mapper.deleteById(id);
            });
  }

  public List<DbExecutionHandleThrowableLog> queryByEntity(DbExecutionHandleThrowableLog dbExecutionHandleThrowableLog) {
    if (dbExecutionHandleThrowableLog == null) {
      return new ArrayList<>();
    }

    return dbExecutionHandleThrowableLogMapper.queryByEntity(dbExecutionHandleThrowableLog);
  }

  public DbExecutionHandleThrowableLog queryById(Long id) {
    if (id == null) {
      return null;
    }
    return dbExecutionHandleThrowableLogMapper.queryById(id);
  }

  public List<DbExecutionHandleThrowableLog> queryByIdList(List<Long> idList) {
    if (idList == null) {
      return new ArrayList<>();
    }
    idList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(idList)) {
      return new ArrayList<>();
    }
    return BatchUtils.applyListInBatches(idList,
            longs -> dbExecutionHandleThrowableLogMapper.queryByIdList(longs));
  }

  public Map<Long, DbExecutionHandleThrowableLog> queryMapByEntity(DbExecutionHandleThrowableLog dbExecutionHandleThrowableLog) {
    List<DbExecutionHandleThrowableLog> dbExecutionHandleThrowableLogList = queryByEntity(dbExecutionHandleThrowableLog);
    return dbExecutionHandleThrowableLogList.stream().collect(Collectors.toMap(DbExecutionHandleThrowableLog::getId, a -> a));
  }

  public Map<Long, DbExecutionHandleThrowableLog> queryMapByIdList(List<Long> idList) {
    List<DbExecutionHandleThrowableLog> dbExecutionHandleThrowableLogList = queryByIdList(idList);
    return dbExecutionHandleThrowableLogList.stream().collect(Collectors.toMap(DbExecutionHandleThrowableLog::getId, a -> a));
  }

}

