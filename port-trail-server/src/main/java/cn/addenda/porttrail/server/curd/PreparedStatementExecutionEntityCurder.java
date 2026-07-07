package cn.addenda.porttrail.server.curd;

import cn.addenda.component.base.collection.BatchUtils;
import cn.addenda.component.mybatis.helper.BatchDmlHelper;
import cn.addenda.porttrail.server.entity.PreparedStatementExecutionEntity;
import cn.addenda.porttrail.server.mapper.PreparedStatementExecutionEntityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * PreparedStatementExecution(PreparedStatementExecutionEntity)业务层
 *
 * @author addenda
 * @since 2026-02-16 19:07:53
 */
@Component
public class PreparedStatementExecutionEntityCurder {

  @Autowired
  private PreparedStatementExecutionEntityMapper preparedStatementExecutionEntityMapper;

  @Autowired
  private BatchDmlHelper batchDmlHelper;

  public int insert(PreparedStatementExecutionEntity preparedStatementExecutionEntity) {
    return preparedStatementExecutionEntityMapper.insert(preparedStatementExecutionEntity);
  }

  public int updateById(PreparedStatementExecutionEntity preparedStatementExecutionEntity) {
    return preparedStatementExecutionEntityMapper.updateById(preparedStatementExecutionEntity);
  }

  public int deleteById(Long id) {
    return preparedStatementExecutionEntityMapper.deleteById(id);
  }

  public void batchInsert(List<PreparedStatementExecutionEntity> preparedStatementExecutionEntityList) {
    if (preparedStatementExecutionEntityList == null) {
      return;
    }
    preparedStatementExecutionEntityList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(preparedStatementExecutionEntityList)) {
      return;
    }
    batchDmlHelper.batch(PreparedStatementExecutionEntityMapper.class, preparedStatementExecutionEntityList,
            (mapper, preparedStatementExecutionEntity) -> {
              mapper.insert(preparedStatementExecutionEntity);
            });
  }

  public void batchUpdateById(List<PreparedStatementExecutionEntity> preparedStatementExecutionEntityList) {
    if (preparedStatementExecutionEntityList == null) {
      return;
    }
    preparedStatementExecutionEntityList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(preparedStatementExecutionEntityList)) {
      return;
    }
    batchDmlHelper.batch(PreparedStatementExecutionEntityMapper.class, preparedStatementExecutionEntityList,
            (mapper, preparedStatementExecutionEntity) -> {
              mapper.updateById(preparedStatementExecutionEntity);
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
    batchDmlHelper.batch(PreparedStatementExecutionEntityMapper.class, idList,
            (mapper, id) -> {
              mapper.deleteById(id);
            });
  }

  public List<PreparedStatementExecutionEntity> queryByEntity(PreparedStatementExecutionEntity preparedStatementExecutionEntity) {
    if (preparedStatementExecutionEntity == null) {
      return new ArrayList<>();
    }

    return preparedStatementExecutionEntityMapper.queryByEntity(preparedStatementExecutionEntity);
  }

  public PreparedStatementExecutionEntity queryById(Long id) {
    if (id == null) {
      return null;
    }
    return preparedStatementExecutionEntityMapper.queryById(id);
  }

  public List<PreparedStatementExecutionEntity> queryByIdList(List<Long> idList) {
    if (idList == null) {
      return new ArrayList<>();
    }
    idList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(idList)) {
      return new ArrayList<>();
    }
    return BatchUtils.applyListInBatches(idList,
            longs -> preparedStatementExecutionEntityMapper.queryByIdList(longs));
  }

  public Map<Long, PreparedStatementExecutionEntity> queryMapByEntity(PreparedStatementExecutionEntity preparedStatementExecutionEntity) {
    List<PreparedStatementExecutionEntity> preparedStatementExecutionEntityList = queryByEntity(preparedStatementExecutionEntity);
    return preparedStatementExecutionEntityList.stream().collect(Collectors.toMap(PreparedStatementExecutionEntity::getId, a -> a));
  }

  public Map<Long, PreparedStatementExecutionEntity> queryMapByIdList(List<Long> idList) {
    List<PreparedStatementExecutionEntity> preparedStatementExecutionEntityList = queryByIdList(idList);
    return preparedStatementExecutionEntityList.stream().collect(Collectors.toMap(PreparedStatementExecutionEntity::getId, a -> a));
  }

}
