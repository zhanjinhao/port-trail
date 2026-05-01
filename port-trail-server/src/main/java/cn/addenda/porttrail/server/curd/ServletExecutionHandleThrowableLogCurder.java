package cn.addenda.porttrail.server.curd;

import java.util.*;

import cn.addenda.component.base.collection.BatchUtils;
import cn.addenda.component.mybatis.helper.BatchDmlHelper;
import cn.addenda.porttrail.server.mapper.ServletExecutionHandleThrowableLogMapper;
import cn.addenda.porttrail.server.entity.ServletExecutionHandleThrowableLog;
import org.springframework.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ServletExecution在handle阶段异常时记录的日志(ServletExecutionHandleThrowableLog)业务层
 *
 * @author addenda
 * @since 2026-05-01 16:40:10
 */
@Component
public class ServletExecutionHandleThrowableLogCurder {

  @Autowired
  private ServletExecutionHandleThrowableLogMapper servletExecutionHandleThrowableLogMapper;

  @Autowired
  private BatchDmlHelper batchDmlHelper;

  public int insert(ServletExecutionHandleThrowableLog servletExecutionHandleThrowableLog) {
    return servletExecutionHandleThrowableLogMapper.insert(servletExecutionHandleThrowableLog);
  }

  public int updateById(ServletExecutionHandleThrowableLog servletExecutionHandleThrowableLog) {
    return servletExecutionHandleThrowableLogMapper.updateById(servletExecutionHandleThrowableLog);
  }

  public int deleteById(Long id) {
    return servletExecutionHandleThrowableLogMapper.deleteById(id);
  }

  public void batchInsert(List<ServletExecutionHandleThrowableLog> servletExecutionHandleThrowableLogList) {
    if (servletExecutionHandleThrowableLogList == null) {
      return;
    }
    servletExecutionHandleThrowableLogList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(servletExecutionHandleThrowableLogList)) {
      return;
    }
    batchDmlHelper.batch(ServletExecutionHandleThrowableLogMapper.class, servletExecutionHandleThrowableLogList,
            (mapper, servletExecutionHandleThrowableLog) -> {
              mapper.insert(servletExecutionHandleThrowableLog);
            });
  }

  public void batchUpdateById(List<ServletExecutionHandleThrowableLog> servletExecutionHandleThrowableLogList) {
    if (servletExecutionHandleThrowableLogList == null) {
      return;
    }
    servletExecutionHandleThrowableLogList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(servletExecutionHandleThrowableLogList)) {
      return;
    }
    batchDmlHelper.batch(ServletExecutionHandleThrowableLogMapper.class, servletExecutionHandleThrowableLogList,
            (mapper, servletExecutionHandleThrowableLog) -> {
              mapper.updateById(servletExecutionHandleThrowableLog);
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
    batchDmlHelper.batch(ServletExecutionHandleThrowableLogMapper.class, idList,
            (mapper, id) -> {
              mapper.deleteById(id);
            });
  }

  public List<ServletExecutionHandleThrowableLog> queryByEntity(ServletExecutionHandleThrowableLog servletExecutionHandleThrowableLog) {
    if (servletExecutionHandleThrowableLog == null) {
      return new ArrayList<>();
    }

    return servletExecutionHandleThrowableLogMapper.queryByEntity(servletExecutionHandleThrowableLog);
  }

  public ServletExecutionHandleThrowableLog queryById(Long id) {
    if (id == null) {
      return null;
    }
    return servletExecutionHandleThrowableLogMapper.queryById(id);
  }

  public List<ServletExecutionHandleThrowableLog> queryByIdList(List<Long> idList) {
    if (idList == null) {
      return new ArrayList<>();
    }
    idList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(idList)) {
      return new ArrayList<>();
    }
    return BatchUtils.applyListInBatches(idList,
            longs -> servletExecutionHandleThrowableLogMapper.queryByIdList(longs));
  }

  public Map<Long, ServletExecutionHandleThrowableLog> queryMapByEntity(ServletExecutionHandleThrowableLog servletExecutionHandleThrowableLog) {
    List<ServletExecutionHandleThrowableLog> servletExecutionHandleThrowableLogList = queryByEntity(servletExecutionHandleThrowableLog);
    return servletExecutionHandleThrowableLogList.stream().collect(Collectors.toMap(ServletExecutionHandleThrowableLog::getId, a -> a));
  }

  public Map<Long, ServletExecutionHandleThrowableLog> queryMapByIdList(List<Long> idList) {
    List<ServletExecutionHandleThrowableLog> servletExecutionHandleThrowableLogList = queryByIdList(idList);
    return servletExecutionHandleThrowableLogList.stream().collect(Collectors.toMap(ServletExecutionHandleThrowableLog::getId, a -> a));
  }

}

