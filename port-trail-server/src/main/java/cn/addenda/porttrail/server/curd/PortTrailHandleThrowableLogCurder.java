package cn.addenda.porttrail.server.curd;

import java.util.*;

import cn.addenda.component.base.collection.BatchUtils;
import cn.addenda.component.mybatis.helper.BatchDmlHelper;
import cn.addenda.porttrail.server.mapper.PortTrailHandleThrowableLogMapper;
import cn.addenda.porttrail.server.entity.PortTrailHandleThrowableLog;
import org.springframework.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

import java.util.List;
import java.util.stream.Collectors;

/**
 * handle阶段异常时记录的日志(PortTrailHandleThrowableLog)业务层
 *
 * @author addenda
 * @since 2026-03-06 22:44:42
 */
@Component
public class PortTrailHandleThrowableLogCurder {

  @Autowired
  private PortTrailHandleThrowableLogMapper portTrailHandleThrowableLogMapper;

  @Autowired
  private BatchDmlHelper batchDmlHelper;

  public int insert(PortTrailHandleThrowableLog portTrailHandleThrowableLog) {
    return portTrailHandleThrowableLogMapper.insert(portTrailHandleThrowableLog);
  }

  public int updateById(PortTrailHandleThrowableLog portTrailHandleThrowableLog) {
    return portTrailHandleThrowableLogMapper.updateById(portTrailHandleThrowableLog);
  }

  public int deleteById(Long id) {
    return portTrailHandleThrowableLogMapper.deleteById(id);
  }

  public void batchInsert(List<PortTrailHandleThrowableLog> portTrailHandleThrowableLogList) {
    if (portTrailHandleThrowableLogList == null) {
      return;
    }
    portTrailHandleThrowableLogList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(portTrailHandleThrowableLogList)) {
      return;
    }
    batchDmlHelper.batch(PortTrailHandleThrowableLogMapper.class, portTrailHandleThrowableLogList,
            (mapper, portTrailHandleThrowableLog) -> {
              mapper.insert(portTrailHandleThrowableLog);
            });
  }

  public void batchUpdateById(List<PortTrailHandleThrowableLog> portTrailHandleThrowableLogList) {
    if (portTrailHandleThrowableLogList == null) {
      return;
    }
    portTrailHandleThrowableLogList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(portTrailHandleThrowableLogList)) {
      return;
    }
    batchDmlHelper.batch(PortTrailHandleThrowableLogMapper.class, portTrailHandleThrowableLogList,
            (mapper, portTrailHandleThrowableLog) -> {
              mapper.updateById(portTrailHandleThrowableLog);
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
    batchDmlHelper.batch(PortTrailHandleThrowableLogMapper.class, idList,
            (mapper, id) -> {
              mapper.deleteById(id);
            });
  }

  public List<PortTrailHandleThrowableLog> queryByEntity(PortTrailHandleThrowableLog portTrailHandleThrowableLog) {
    if (portTrailHandleThrowableLog == null) {
      return new ArrayList<>();
    }

    return portTrailHandleThrowableLogMapper.queryByEntity(portTrailHandleThrowableLog);
  }

  public PortTrailHandleThrowableLog queryById(Long id) {
    if (id == null) {
      return null;
    }
    return portTrailHandleThrowableLogMapper.queryById(id);
  }

  public List<PortTrailHandleThrowableLog> queryByIdList(List<Long> idList) {
    if (idList == null) {
      return new ArrayList<>();
    }
    idList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(idList)) {
      return new ArrayList<>();
    }
    return BatchUtils.applyListInBatches(idList,
            longs -> portTrailHandleThrowableLogMapper.queryByIdList(longs));
  }

  public Map<Long, PortTrailHandleThrowableLog> queryMapByEntity(PortTrailHandleThrowableLog portTrailHandleThrowableLog) {
    List<PortTrailHandleThrowableLog> portTrailHandleThrowableLogList = queryByEntity(portTrailHandleThrowableLog);
    return portTrailHandleThrowableLogList.stream().collect(Collectors.toMap(PortTrailHandleThrowableLog::getId, a -> a));
  }

  public Map<Long, PortTrailHandleThrowableLog> queryMapByIdList(List<Long> idList) {
    List<PortTrailHandleThrowableLog> portTrailHandleThrowableLogList = queryByIdList(idList);
    return portTrailHandleThrowableLogList.stream().collect(Collectors.toMap(PortTrailHandleThrowableLog::getId, a -> a));
  }

}

