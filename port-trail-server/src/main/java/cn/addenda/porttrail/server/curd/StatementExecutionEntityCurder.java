package cn.addenda.porttrail.server.curd;

import cn.addenda.component.base.collection.BatchUtils;
import cn.addenda.component.mybatis.helper.BatchDmlHelper;
import cn.addenda.porttrail.server.entity.StatementExecutionEntity;
import cn.addenda.porttrail.server.mapper.StatementExecutionEntityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * StatementExecution(StatementExecutionEntity)业务层
 *
 * @author addenda
 * @since 2026-02-16 19:09:36
 */
@Component
public class StatementExecutionEntityCurder {

  @Autowired
  private StatementExecutionEntityMapper statementExecutionEntityMapper;

  @Autowired
  private BatchDmlHelper batchDmlHelper;

  public int insert(StatementExecutionEntity statementExecutionEntity) {
    return statementExecutionEntityMapper.insert(statementExecutionEntity);
  }

  public int updateById(StatementExecutionEntity statementExecutionEntity) {
    return statementExecutionEntityMapper.updateById(statementExecutionEntity);
  }

  public int deleteById(Long id) {
    return statementExecutionEntityMapper.deleteById(id);
  }

  public void batchInsert(List<StatementExecutionEntity> statementExecutionEntityList) {
    if (statementExecutionEntityList == null) {
      return;
    }
    statementExecutionEntityList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(statementExecutionEntityList)) {
      return;
    }
    batchDmlHelper.batch(StatementExecutionEntityMapper.class, statementExecutionEntityList,
            (mapper, statementExecutionEntity) -> {
              mapper.insert(statementExecutionEntity);
            });
  }

  public void batchUpdateById(List<StatementExecutionEntity> statementExecutionEntityList) {
    if (statementExecutionEntityList == null) {
      return;
    }
    statementExecutionEntityList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(statementExecutionEntityList)) {
      return;
    }
    batchDmlHelper.batch(StatementExecutionEntityMapper.class, statementExecutionEntityList,
            (mapper, statementExecutionEntity) -> {
              mapper.updateById(statementExecutionEntity);
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
    batchDmlHelper.batch(StatementExecutionEntityMapper.class, idList,
            (mapper, id) -> {
              mapper.deleteById(id);
            });
  }

  public List<StatementExecutionEntity> queryByEntity(StatementExecutionEntity statementExecutionEntity) {
    if (statementExecutionEntity == null) {
      return new ArrayList<>();
    }

    return statementExecutionEntityMapper.queryByEntity(statementExecutionEntity);
  }

  public StatementExecutionEntity queryById(Long id) {
    if (id == null) {
      return null;
    }
    return statementExecutionEntityMapper.queryById(id);
  }

  public List<StatementExecutionEntity> queryByIdList(List<Long> idList) {
    if (idList == null) {
      return new ArrayList<>();
    }
    idList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(idList)) {
      return new ArrayList<>();
    }
    return BatchUtils.applyListInBatches(idList,
            longs -> statementExecutionEntityMapper.queryByIdList(longs));
  }

  public Map<Long, StatementExecutionEntity> queryMapByEntity(StatementExecutionEntity statementExecutionEntity) {
    List<StatementExecutionEntity> statementExecutionEntityList = queryByEntity(statementExecutionEntity);
    return statementExecutionEntityList.stream().collect(Collectors.toMap(StatementExecutionEntity::getId, a -> a));
  }

  public Map<Long, StatementExecutionEntity> queryMapByIdList(List<Long> idList) {
    List<StatementExecutionEntity> statementExecutionEntityList = queryByIdList(idList);
    return statementExecutionEntityList.stream().collect(Collectors.toMap(StatementExecutionEntity::getId, a -> a));
  }

}
