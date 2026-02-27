package cn.addenda.porttrail.server.curd;

import cn.addenda.component.base.collection.BatchUtils;
import cn.addenda.component.mybatis.helper.BatchDmlHelper;
import cn.addenda.porttrail.server.entity.EstPreparedStatementExecution;
import cn.addenda.porttrail.server.mapper.EstPreparedStatementExecutionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * PreparedStatementExecution(EstPreparedStatementExecution)业务层
 *
 * @author addenda
 * @since 2026-02-16 19:07:53
 */
@Component
public class EstPreparedStatementExecutionCurder {

  @Autowired
  private EstPreparedStatementExecutionMapper estPreparedStatementExecutionMapper;

  @Autowired
  private BatchDmlHelper batchDmlHelper;

  public int insert(EstPreparedStatementExecution estPreparedStatementExecution) {
    return estPreparedStatementExecutionMapper.insert(estPreparedStatementExecution);
  }

  public int updateById(EstPreparedStatementExecution estPreparedStatementExecution) {
    return estPreparedStatementExecutionMapper.updateById(estPreparedStatementExecution);
  }

  public int deleteById(Long id) {
    return estPreparedStatementExecutionMapper.deleteById(id);
  }

  public void batchInsert(List<EstPreparedStatementExecution> estPreparedStatementExecutionList) {
    if (estPreparedStatementExecutionList == null) {
      return;
    }
    estPreparedStatementExecutionList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(estPreparedStatementExecutionList)) {
      return;
    }
    batchDmlHelper.batch(EstPreparedStatementExecutionMapper.class, estPreparedStatementExecutionList,
            (mapper, estPreparedStatementExecution) -> {
              mapper.insert(estPreparedStatementExecution);
            });
  }

  public void batchUpdateById(List<EstPreparedStatementExecution> estPreparedStatementExecutionList) {
    if (estPreparedStatementExecutionList == null) {
      return;
    }
    estPreparedStatementExecutionList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(estPreparedStatementExecutionList)) {
      return;
    }
    batchDmlHelper.batch(EstPreparedStatementExecutionMapper.class, estPreparedStatementExecutionList,
            (mapper, estPreparedStatementExecution) -> {
              mapper.updateById(estPreparedStatementExecution);
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
    batchDmlHelper.batch(EstPreparedStatementExecutionMapper.class, idList,
            (mapper, id) -> {
              mapper.deleteById(id);
            });
  }

  public List<EstPreparedStatementExecution> queryByEntity(EstPreparedStatementExecution estPreparedStatementExecution) {
    if (estPreparedStatementExecution == null) {
      return new ArrayList<>();
    }

    return estPreparedStatementExecutionMapper.queryByEntity(estPreparedStatementExecution);
  }

  public EstPreparedStatementExecution queryById(Long id) {
    if (id == null) {
      return null;
    }
    return estPreparedStatementExecutionMapper.queryById(id);
  }

  public List<EstPreparedStatementExecution> queryByIdList(List<Long> idList) {
    if (idList == null) {
      return new ArrayList<>();
    }
    idList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(idList)) {
      return new ArrayList<>();
    }
    return BatchUtils.applyListInBatches(idList,
            longs -> estPreparedStatementExecutionMapper.queryByIdList(longs));
  }

  public Map<Long, EstPreparedStatementExecution> queryMapByEntity(EstPreparedStatementExecution estPreparedStatementExecution) {
    List<EstPreparedStatementExecution> estPreparedStatementExecutionList = queryByEntity(estPreparedStatementExecution);
    return estPreparedStatementExecutionList.stream().collect(Collectors.toMap(EstPreparedStatementExecution::getId, a -> a));
  }

  public Map<Long, EstPreparedStatementExecution> queryMapByIdList(List<Long> idList) {
    List<EstPreparedStatementExecution> estPreparedStatementExecutionList = queryByIdList(idList);
    return estPreparedStatementExecutionList.stream().collect(Collectors.toMap(EstPreparedStatementExecution::getId, a -> a));
  }

}
