package cn.addenda.porttrail.server.curd;

import java.util.*;

import cn.addenda.component.base.collection.BatchUtils;
import cn.addenda.component.mybatis.helper.BatchDmlHelper;
import cn.addenda.porttrail.server.mapper.PortTrailAnalyzeThrowableLogMapper;
import cn.addenda.porttrail.server.entity.PortTrailAnalyzeThrowableLog;
import org.springframework.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

import java.util.List;
import java.util.stream.Collectors;

/**
 * analyze阶段异常时记录的日志(PortTrailAnalyzeThrowableLog)业务层
 *
 * @author addenda
 * @since 2026-03-06 22:52:40
 */
@Component
public class PortTrailAnalyzeThrowableLogCurder {

  @Autowired
  private PortTrailAnalyzeThrowableLogMapper portTrailAnalyzeThrowableLogMapper;

  @Autowired
  private BatchDmlHelper batchDmlHelper;

  public int insert(PortTrailAnalyzeThrowableLog portTrailAnalyzeThrowableLog) {
    return portTrailAnalyzeThrowableLogMapper.insert(portTrailAnalyzeThrowableLog);
  }

  public int updateById(PortTrailAnalyzeThrowableLog portTrailAnalyzeThrowableLog) {
    return portTrailAnalyzeThrowableLogMapper.updateById(portTrailAnalyzeThrowableLog);
  }

  public int deleteById(Long id) {
    return portTrailAnalyzeThrowableLogMapper.deleteById(id);
  }

  public void batchInsert(List<PortTrailAnalyzeThrowableLog> portTrailAnalyzeThrowableLogList) {
    if (portTrailAnalyzeThrowableLogList == null) {
      return;
    }
    portTrailAnalyzeThrowableLogList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(portTrailAnalyzeThrowableLogList)) {
      return;
    }
    batchDmlHelper.batch(PortTrailAnalyzeThrowableLogMapper.class, portTrailAnalyzeThrowableLogList,
            (mapper, portTrailAnalyzeThrowableLog) -> {
              mapper.insert(portTrailAnalyzeThrowableLog);
            });
  }

  public void batchUpdateById(List<PortTrailAnalyzeThrowableLog> portTrailAnalyzeThrowableLogList) {
    if (portTrailAnalyzeThrowableLogList == null) {
      return;
    }
    portTrailAnalyzeThrowableLogList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(portTrailAnalyzeThrowableLogList)) {
      return;
    }
    batchDmlHelper.batch(PortTrailAnalyzeThrowableLogMapper.class, portTrailAnalyzeThrowableLogList,
            (mapper, portTrailAnalyzeThrowableLog) -> {
              mapper.updateById(portTrailAnalyzeThrowableLog);
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
    batchDmlHelper.batch(PortTrailAnalyzeThrowableLogMapper.class, idList,
            (mapper, id) -> {
              mapper.deleteById(id);
            });
  }

  public List<PortTrailAnalyzeThrowableLog> queryByEntity(PortTrailAnalyzeThrowableLog portTrailAnalyzeThrowableLog) {
    if (portTrailAnalyzeThrowableLog == null) {
      return new ArrayList<>();
    }

    return portTrailAnalyzeThrowableLogMapper.queryByEntity(portTrailAnalyzeThrowableLog);
  }

  public PortTrailAnalyzeThrowableLog queryById(Long id) {
    if (id == null) {
      return null;
    }
    return portTrailAnalyzeThrowableLogMapper.queryById(id);
  }

  public List<PortTrailAnalyzeThrowableLog> queryByIdList(List<Long> idList) {
    if (idList == null) {
      return new ArrayList<>();
    }
    idList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(idList)) {
      return new ArrayList<>();
    }
    return BatchUtils.applyListInBatches(idList,
            longs -> portTrailAnalyzeThrowableLogMapper.queryByIdList(longs));
  }

  public Map<Long, PortTrailAnalyzeThrowableLog> queryMapByEntity(PortTrailAnalyzeThrowableLog portTrailAnalyzeThrowableLog) {
    List<PortTrailAnalyzeThrowableLog> portTrailAnalyzeThrowableLogList = queryByEntity(portTrailAnalyzeThrowableLog);
    return portTrailAnalyzeThrowableLogList.stream().collect(Collectors.toMap(PortTrailAnalyzeThrowableLog::getId, a -> a));
  }

  public Map<Long, PortTrailAnalyzeThrowableLog> queryMapByIdList(List<Long> idList) {
    List<PortTrailAnalyzeThrowableLog> portTrailAnalyzeThrowableLogList = queryByIdList(idList);
    return portTrailAnalyzeThrowableLogList.stream().collect(Collectors.toMap(PortTrailAnalyzeThrowableLog::getId, a -> a));
  }

}

