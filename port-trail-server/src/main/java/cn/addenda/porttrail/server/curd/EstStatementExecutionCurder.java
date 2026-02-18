package cn.addenda.porttrail.server.curd;

import cn.addenda.component.base.collection.BatchUtils;
import cn.addenda.component.mybatis.helper.BatchDmlHelper;
import cn.addenda.porttrail.server.entity.EstStatementExecution;
import cn.addenda.porttrail.server.mapper.EstStatementExecutionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * StatementExecution(EstStatementExecution)业务层
 *
 * @author addenda
 * @since 2026-02-16 19:09:36
 */
@Component
public class EstStatementExecutionCurder {

  @Autowired
  private EstStatementExecutionMapper estStatementExecutionMapper;

  @Autowired
  private BatchDmlHelper batchDmlHelper;

  public int insert(EstStatementExecution estStatementExecution) {
    return estStatementExecutionMapper.insert(estStatementExecution);
  }

  public int updateById(EstStatementExecution estStatementExecution) {
    return estStatementExecutionMapper.updateById(estStatementExecution);
  }

  public int deleteById(Long id) {
    return estStatementExecutionMapper.deleteById(id);
  }

  public void batchInsert(List<EstStatementExecution> estStatementExecutionList) {
    if (estStatementExecutionList == null) {
      return;
    }
    estStatementExecutionList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(estStatementExecutionList)) {
      return;
    }
    batchDmlHelper.batch(EstStatementExecutionMapper.class, estStatementExecutionList,
            (mapper, estStatementExecution) -> {
              mapper.insert(estStatementExecution);
            });
  }

  public void batchUpdateById(List<EstStatementExecution> estStatementExecutionList) {
    if (estStatementExecutionList == null) {
      return;
    }
    estStatementExecutionList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(estStatementExecutionList)) {
      return;
    }
    batchDmlHelper.batch(EstStatementExecutionMapper.class, estStatementExecutionList,
            (mapper, estStatementExecution) -> {
              mapper.updateById(estStatementExecution);
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
    batchDmlHelper.batch(EstStatementExecutionMapper.class, idList,
            (mapper, id) -> {
              mapper.deleteById(id);
            });
  }

  public List<EstStatementExecution> queryByEntity(EstStatementExecution estStatementExecution) {
    if (estStatementExecution == null) {
      return new ArrayList<>();
    }

    return estStatementExecutionMapper.queryByEntity(estStatementExecution);
  }

  public EstStatementExecution queryById(Long id) {
    if (id == null) {
      return null;
    }
    return estStatementExecutionMapper.queryById(id);
  }

  public List<EstStatementExecution> queryByIdList(List<Long> idList) {
    if (idList == null) {
      return new ArrayList<>();
    }
    idList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(idList)) {
      return new ArrayList<>();
    }
    return BatchUtils.applyListInBatches(idList,
            longs -> estStatementExecutionMapper.queryByIdList(longs));
  }

  public Map<Long, EstStatementExecution> queryMapByEntity(EstStatementExecution estStatementExecution) {
    List<EstStatementExecution> estStatementExecutionList = queryByEntity(estStatementExecution);
    return estStatementExecutionList.stream().collect(Collectors.toMap(EstStatementExecution::getId, a -> a));
  }

  public Map<Long, EstStatementExecution> queryMapByIdList(List<Long> idList) {
    List<EstStatementExecution> estStatementExecutionList = queryByIdList(idList);
    return estStatementExecutionList.stream().collect(Collectors.toMap(EstStatementExecution::getId, a -> a));
  }

}
