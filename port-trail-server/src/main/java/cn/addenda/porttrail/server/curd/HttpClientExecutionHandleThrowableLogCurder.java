package cn.addenda.porttrail.server.curd;

import java.util.*;

import cn.addenda.component.base.collection.BatchUtils;
import cn.addenda.component.mybatis.helper.BatchDmlHelper;
import cn.addenda.porttrail.server.mapper.HttpClientExecutionHandleThrowableLogMapper;
import cn.addenda.porttrail.server.entity.HttpClientExecutionHandleThrowableLog;
import org.springframework.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

import java.util.List;
import java.util.stream.Collectors;

/**
 * HttpClientExecution在handle阶段异常时记录的日志(HttpClientExecutionHandleThrowableLog)业务层
 *
 * @author addenda
 * @since 2026-06-08 18:20:15
 */
@Component
public class HttpClientExecutionHandleThrowableLogCurder {

  @Autowired
  private HttpClientExecutionHandleThrowableLogMapper httpClientExecutionHandleThrowableLogMapper;

  @Autowired
  private BatchDmlHelper batchDmlHelper;

  public int insert(HttpClientExecutionHandleThrowableLog httpClientExecutionHandleThrowableLog) {
    return httpClientExecutionHandleThrowableLogMapper.insert(httpClientExecutionHandleThrowableLog);
  }

  public int updateById(HttpClientExecutionHandleThrowableLog httpClientExecutionHandleThrowableLog) {
    return httpClientExecutionHandleThrowableLogMapper.updateById(httpClientExecutionHandleThrowableLog);
  }

  public int deleteById(Long id) {
    return httpClientExecutionHandleThrowableLogMapper.deleteById(id);
  }

  public void batchInsert(List<HttpClientExecutionHandleThrowableLog> httpClientExecutionHandleThrowableLogList) {
    if (httpClientExecutionHandleThrowableLogList == null) {
      return;
    }
    httpClientExecutionHandleThrowableLogList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(httpClientExecutionHandleThrowableLogList)) {
      return;
    }
    batchDmlHelper.batch(HttpClientExecutionHandleThrowableLogMapper.class, httpClientExecutionHandleThrowableLogList,
            (mapper, httpClientExecutionHandleThrowableLog) -> {
              mapper.insert(httpClientExecutionHandleThrowableLog);
            });
  }

  public void batchUpdateById(List<HttpClientExecutionHandleThrowableLog> httpClientExecutionHandleThrowableLogList) {
    if (httpClientExecutionHandleThrowableLogList == null) {
      return;
    }
    httpClientExecutionHandleThrowableLogList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(httpClientExecutionHandleThrowableLogList)) {
      return;
    }
    batchDmlHelper.batch(HttpClientExecutionHandleThrowableLogMapper.class, httpClientExecutionHandleThrowableLogList,
            (mapper, httpClientExecutionHandleThrowableLog) -> {
              mapper.updateById(httpClientExecutionHandleThrowableLog);
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
    batchDmlHelper.batch(HttpClientExecutionHandleThrowableLogMapper.class, idList,
            (mapper, id) -> {
              mapper.deleteById(id);
            });
  }

  public List<HttpClientExecutionHandleThrowableLog> queryByEntity(HttpClientExecutionHandleThrowableLog httpClientExecutionHandleThrowableLog) {
    if (httpClientExecutionHandleThrowableLog == null) {
      return new ArrayList<>();
    }

    return httpClientExecutionHandleThrowableLogMapper.queryByEntity(httpClientExecutionHandleThrowableLog);
  }

  public HttpClientExecutionHandleThrowableLog queryById(Long id) {
    if (id == null) {
      return null;
    }
    return httpClientExecutionHandleThrowableLogMapper.queryById(id);
  }

  public List<HttpClientExecutionHandleThrowableLog> queryByIdList(List<Long> idList) {
    if (idList == null) {
      return new ArrayList<>();
    }
    idList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(idList)) {
      return new ArrayList<>();
    }
    return BatchUtils.applyListInBatches(idList,
            longs -> httpClientExecutionHandleThrowableLogMapper.queryByIdList(longs));
  }

  public Map<Long, HttpClientExecutionHandleThrowableLog> queryMapByEntity(HttpClientExecutionHandleThrowableLog httpClientExecutionHandleThrowableLog) {
    List<HttpClientExecutionHandleThrowableLog> httpClientExecutionHandleThrowableLogList = queryByEntity(httpClientExecutionHandleThrowableLog);
    return httpClientExecutionHandleThrowableLogList.stream().collect(Collectors.toMap(HttpClientExecutionHandleThrowableLog::getId, a -> a));
  }

  public Map<Long, HttpClientExecutionHandleThrowableLog> queryMapByIdList(List<Long> idList) {
    List<HttpClientExecutionHandleThrowableLog> httpClientExecutionHandleThrowableLogList = queryByIdList(idList);
    return httpClientExecutionHandleThrowableLogList.stream().collect(Collectors.toMap(HttpClientExecutionHandleThrowableLog::getId, a -> a));
  }

}

