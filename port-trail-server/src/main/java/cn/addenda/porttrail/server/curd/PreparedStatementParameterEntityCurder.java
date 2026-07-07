package cn.addenda.porttrail.server.curd;

import cn.addenda.component.base.collection.BatchUtils;
import cn.addenda.component.mybatis.helper.BatchDmlHelper;
import cn.addenda.porttrail.server.entity.PreparedStatementParameterEntity;
import cn.addenda.porttrail.server.mapper.PreparedStatementParameterEntityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * PreparedStatementParameter(PreparedStatementParameterEntity)业务层
 *
 * @author addenda
 * @since 2026-02-16 19:08:23
 */
@Component
public class PreparedStatementParameterEntityCurder {

  @Autowired
  private PreparedStatementParameterEntityMapper preparedStatementParameterEntityMapper;

  @Autowired
  private BatchDmlHelper batchDmlHelper;

  public int insert(PreparedStatementParameterEntity preparedStatementParameterEntity) {
    return preparedStatementParameterEntityMapper.insert(preparedStatementParameterEntity);
  }

  public int updateById(PreparedStatementParameterEntity preparedStatementParameterEntity) {
    return preparedStatementParameterEntityMapper.updateById(preparedStatementParameterEntity);
  }

  public int deleteById(Long id) {
    return preparedStatementParameterEntityMapper.deleteById(id);
  }

  public void batchInsert(List<PreparedStatementParameterEntity> preparedStatementParameterEntityList) {
    if (preparedStatementParameterEntityList == null) {
      return;
    }
    preparedStatementParameterEntityList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(preparedStatementParameterEntityList)) {
      return;
    }
    batchDmlHelper.batch(PreparedStatementParameterEntityMapper.class, preparedStatementParameterEntityList,
            (mapper, preparedStatementParameterEntity) -> {
              mapper.insert(preparedStatementParameterEntity);
            });
  }

  public void batchUpdateById(List<PreparedStatementParameterEntity> preparedStatementParameterEntityList) {
    if (preparedStatementParameterEntityList == null) {
      return;
    }
    preparedStatementParameterEntityList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(preparedStatementParameterEntityList)) {
      return;
    }
    batchDmlHelper.batch(PreparedStatementParameterEntityMapper.class, preparedStatementParameterEntityList,
            (mapper, preparedStatementParameterEntity) -> {
              mapper.updateById(preparedStatementParameterEntity);
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
    batchDmlHelper.batch(PreparedStatementParameterEntityMapper.class, idList,
            (mapper, id) -> {
              mapper.deleteById(id);
            });
  }

  public List<PreparedStatementParameterEntity> queryByEntity(PreparedStatementParameterEntity preparedStatementParameterEntity) {
    if (preparedStatementParameterEntity == null) {
      return new ArrayList<>();
    }

    return preparedStatementParameterEntityMapper.queryByEntity(preparedStatementParameterEntity);
  }

  public PreparedStatementParameterEntity queryById(Long id) {
    if (id == null) {
      return null;
    }
    return preparedStatementParameterEntityMapper.queryById(id);
  }

  public List<PreparedStatementParameterEntity> queryByIdList(List<Long> idList) {
    if (idList == null) {
      return new ArrayList<>();
    }
    idList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(idList)) {
      return new ArrayList<>();
    }
    return BatchUtils.applyListInBatches(idList,
            longs -> preparedStatementParameterEntityMapper.queryByIdList(longs));
  }

  public Map<Long, PreparedStatementParameterEntity> queryMapByEntity(PreparedStatementParameterEntity preparedStatementParameterEntity) {
    List<PreparedStatementParameterEntity> preparedStatementParameterEntityList = queryByEntity(preparedStatementParameterEntity);
    return preparedStatementParameterEntityList.stream().collect(Collectors.toMap(PreparedStatementParameterEntity::getId, a -> a));
  }

  public Map<Long, PreparedStatementParameterEntity> queryMapByIdList(List<Long> idList) {
    List<PreparedStatementParameterEntity> preparedStatementParameterEntityList = queryByIdList(idList);
    return preparedStatementParameterEntityList.stream().collect(Collectors.toMap(PreparedStatementParameterEntity::getId, a -> a));
  }

}
