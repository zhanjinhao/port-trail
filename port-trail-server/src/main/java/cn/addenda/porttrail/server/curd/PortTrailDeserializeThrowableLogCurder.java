package cn.addenda.porttrail.server.curd;

import java.util.*;

import cn.addenda.component.base.collection.BatchUtils;
import cn.addenda.component.mybatis.helper.BatchDmlHelper;
import cn.addenda.porttrail.server.mapper.PortTrailDeserializeThrowableLogMapper;
import cn.addenda.porttrail.server.entity.PortTrailDeserializeThrowableLog;
import org.springframework.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

import java.util.List;
import java.util.stream.Collectors;

/**
 * deserialize阶段异常时记录的日志(PortTrailDeserializeThrowableLog)业务层
 *
 * @author addenda
 * @since 2026-03-06 22:36:06
 */
@Component
public class PortTrailDeserializeThrowableLogCurder {

  @Autowired
  private PortTrailDeserializeThrowableLogMapper portTrailDeserializeThrowableLogMapper;

  @Autowired
  private BatchDmlHelper batchDmlHelper;

  public int insert(PortTrailDeserializeThrowableLog portTrailDeserializeThrowableLog) {
    return portTrailDeserializeThrowableLogMapper.insert(portTrailDeserializeThrowableLog);
  }

  public int updateById(PortTrailDeserializeThrowableLog portTrailDeserializeThrowableLog) {
    return portTrailDeserializeThrowableLogMapper.updateById(portTrailDeserializeThrowableLog);
  }

  public int deleteById(Long id) {
    return portTrailDeserializeThrowableLogMapper.deleteById(id);
  }

  public void batchInsert(List<PortTrailDeserializeThrowableLog> portTrailDeserializeThrowableLogList) {
    if (portTrailDeserializeThrowableLogList == null) {
      return;
    }
    portTrailDeserializeThrowableLogList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(portTrailDeserializeThrowableLogList)) {
      return;
    }
    batchDmlHelper.batch(PortTrailDeserializeThrowableLogMapper.class, portTrailDeserializeThrowableLogList,
            (mapper, portTrailDeserializeThrowableLog) -> {
              mapper.insert(portTrailDeserializeThrowableLog);
            });
  }

  public void batchUpdateById(List<PortTrailDeserializeThrowableLog> portTrailDeserializeThrowableLogList) {
    if (portTrailDeserializeThrowableLogList == null) {
      return;
    }
    portTrailDeserializeThrowableLogList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(portTrailDeserializeThrowableLogList)) {
      return;
    }
    batchDmlHelper.batch(PortTrailDeserializeThrowableLogMapper.class, portTrailDeserializeThrowableLogList,
            (mapper, portTrailDeserializeThrowableLog) -> {
              mapper.updateById(portTrailDeserializeThrowableLog);
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
    batchDmlHelper.batch(PortTrailDeserializeThrowableLogMapper.class, idList,
            (mapper, id) -> {
              mapper.deleteById(id);
            });
  }

  public List<PortTrailDeserializeThrowableLog> queryByEntity(PortTrailDeserializeThrowableLog portTrailDeserializeThrowableLog) {
    if (portTrailDeserializeThrowableLog == null) {
      return new ArrayList<>();
    }

    return portTrailDeserializeThrowableLogMapper.queryByEntity(portTrailDeserializeThrowableLog);
  }

  public PortTrailDeserializeThrowableLog queryById(Long id) {
    if (id == null) {
      return null;
    }
    return portTrailDeserializeThrowableLogMapper.queryById(id);
  }

  public List<PortTrailDeserializeThrowableLog> queryByIdList(List<Long> idList) {
    if (idList == null) {
      return new ArrayList<>();
    }
    idList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(idList)) {
      return new ArrayList<>();
    }
    return BatchUtils.applyListInBatches(idList,
            longs -> portTrailDeserializeThrowableLogMapper.queryByIdList(longs));
  }

  public Map<Long, PortTrailDeserializeThrowableLog> queryMapByEntity(PortTrailDeserializeThrowableLog portTrailDeserializeThrowableLog) {
    List<PortTrailDeserializeThrowableLog> portTrailDeserializeThrowableLogList = queryByEntity(portTrailDeserializeThrowableLog);
    return portTrailDeserializeThrowableLogList.stream().collect(Collectors.toMap(PortTrailDeserializeThrowableLog::getId, a -> a));
  }

  public Map<Long, PortTrailDeserializeThrowableLog> queryMapByIdList(List<Long> idList) {
    List<PortTrailDeserializeThrowableLog> portTrailDeserializeThrowableLogList = queryByIdList(idList);
    return portTrailDeserializeThrowableLogList.stream().collect(Collectors.toMap(PortTrailDeserializeThrowableLog::getId, a -> a));
  }

}

