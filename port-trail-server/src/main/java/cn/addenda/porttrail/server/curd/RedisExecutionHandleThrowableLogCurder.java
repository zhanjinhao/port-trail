package cn.addenda.porttrail.server.curd;

import java.util.*;

import cn.addenda.component.base.collection.BatchUtils;
import cn.addenda.component.mybatis.helper.BatchDmlHelper;
import cn.addenda.porttrail.server.mapper.RedisExecutionHandleThrowableLogMapper;
import cn.addenda.porttrail.server.entity.RedisExecutionHandleThrowableLog;
import org.springframework.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

import java.util.List;
import java.util.stream.Collectors;

/**
 * RedisExecution在handle阶段异常时记录的日志(RedisExecutionHandleThrowableLog)业务层
 *
 * @author addenda
 * @since 2026-06-23 21:57:19
 */
@Component
public class RedisExecutionHandleThrowableLogCurder {

  @Autowired
  private RedisExecutionHandleThrowableLogMapper redisExecutionHandleThrowableLogMapper;

  @Autowired
  private BatchDmlHelper batchDmlHelper;

  public int insert(RedisExecutionHandleThrowableLog redisExecutionHandleThrowableLog) {
    return redisExecutionHandleThrowableLogMapper.insert(redisExecutionHandleThrowableLog);
  }

  public int updateById(RedisExecutionHandleThrowableLog redisExecutionHandleThrowableLog) {
    return redisExecutionHandleThrowableLogMapper.updateById(redisExecutionHandleThrowableLog);
  }

  public int deleteById(Long id) {
    return redisExecutionHandleThrowableLogMapper.deleteById(id);
  }

  public void batchInsert(List<RedisExecutionHandleThrowableLog> redisExecutionHandleThrowableLogList) {
    if (redisExecutionHandleThrowableLogList == null) {
      return;
    }
    redisExecutionHandleThrowableLogList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(redisExecutionHandleThrowableLogList)) {
      return;
    }
    batchDmlHelper.batch(RedisExecutionHandleThrowableLogMapper.class, redisExecutionHandleThrowableLogList,
            (mapper, redisExecutionHandleThrowableLog) -> {
              mapper.insert(redisExecutionHandleThrowableLog);
            });
  }

  public void batchUpdateById(List<RedisExecutionHandleThrowableLog> redisExecutionHandleThrowableLogList) {
    if (redisExecutionHandleThrowableLogList == null) {
      return;
    }
    redisExecutionHandleThrowableLogList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(redisExecutionHandleThrowableLogList)) {
      return;
    }
    batchDmlHelper.batch(RedisExecutionHandleThrowableLogMapper.class, redisExecutionHandleThrowableLogList,
            (mapper, redisExecutionHandleThrowableLog) -> {
              mapper.updateById(redisExecutionHandleThrowableLog);
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
    batchDmlHelper.batch(RedisExecutionHandleThrowableLogMapper.class, idList,
            (mapper, id) -> {
              mapper.deleteById(id);
            });
  }

  public List<RedisExecutionHandleThrowableLog> queryByEntity(RedisExecutionHandleThrowableLog redisExecutionHandleThrowableLog) {
    if (redisExecutionHandleThrowableLog == null) {
      return new ArrayList<>();
    }

    return redisExecutionHandleThrowableLogMapper.queryByEntity(redisExecutionHandleThrowableLog);
  }

  public RedisExecutionHandleThrowableLog queryById(Long id) {
    if (id == null) {
      return null;
    }
    return redisExecutionHandleThrowableLogMapper.queryById(id);
  }

  public List<RedisExecutionHandleThrowableLog> queryByIdList(List<Long> idList) {
    if (idList == null) {
      return new ArrayList<>();
    }
    idList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(idList)) {
      return new ArrayList<>();
    }
    return BatchUtils.applyListInBatches(idList,
            longs -> redisExecutionHandleThrowableLogMapper.queryByIdList(longs));
  }

  public Map<Long, RedisExecutionHandleThrowableLog> queryMapByEntity(RedisExecutionHandleThrowableLog redisExecutionHandleThrowableLog) {
    List<RedisExecutionHandleThrowableLog> redisExecutionHandleThrowableLogList = queryByEntity(redisExecutionHandleThrowableLog);
    return redisExecutionHandleThrowableLogList.stream().collect(Collectors.toMap(RedisExecutionHandleThrowableLog::getId, a -> a));
  }

  public Map<Long, RedisExecutionHandleThrowableLog> queryMapByIdList(List<Long> idList) {
    List<RedisExecutionHandleThrowableLog> redisExecutionHandleThrowableLogList = queryByIdList(idList);
    return redisExecutionHandleThrowableLogList.stream().collect(Collectors.toMap(RedisExecutionHandleThrowableLog::getId, a -> a));
  }

}

