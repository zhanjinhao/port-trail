package cn.addenda.porttrail.server.curd;

import java.util.*;

import cn.addenda.component.base.collection.BatchUtils;
import cn.addenda.component.mybatis.helper.BatchDmlHelper;
import cn.addenda.porttrail.server.mapper.RedisExecutionEntityMapper;
import cn.addenda.porttrail.server.entity.RedisExecutionEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

import java.util.List;
import java.util.stream.Collectors;

/**
 * RedisExecution(RedisExecutionEntity)业务层
 *
 * @author addenda
 * @since 2026-06-23 22:13:26
 */
@Component
public class RedisExecutionEntityCurder {

  @Autowired
  private RedisExecutionEntityMapper redisExecutionEntityMapper;

  @Autowired
  private BatchDmlHelper batchDmlHelper;

  public int insert(RedisExecutionEntity redisExecutionEntity) {
    return redisExecutionEntityMapper.insert(redisExecutionEntity);
  }

  public int updateById(RedisExecutionEntity redisExecutionEntity) {
    return redisExecutionEntityMapper.updateById(redisExecutionEntity);
  }

  public int deleteById(Long id) {
    return redisExecutionEntityMapper.deleteById(id);
  }

  public void batchInsert(List<RedisExecutionEntity> redisExecutionEntityList) {
    if (redisExecutionEntityList == null) {
      return;
    }
    redisExecutionEntityList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(redisExecutionEntityList)) {
      return;
    }
    batchDmlHelper.batch(RedisExecutionEntityMapper.class, redisExecutionEntityList,
            (mapper, redisExecutionEntity) -> {
              mapper.insert(redisExecutionEntity);
            });
  }

  public void batchUpdateById(List<RedisExecutionEntity> redisExecutionEntityList) {
    if (redisExecutionEntityList == null) {
      return;
    }
    redisExecutionEntityList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(redisExecutionEntityList)) {
      return;
    }
    batchDmlHelper.batch(RedisExecutionEntityMapper.class, redisExecutionEntityList,
            (mapper, redisExecutionEntity) -> {
              mapper.updateById(redisExecutionEntity);
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
    batchDmlHelper.batch(RedisExecutionEntityMapper.class, idList,
            (mapper, id) -> {
              mapper.deleteById(id);
            });
  }

  public List<RedisExecutionEntity> queryByEntity(RedisExecutionEntity redisExecutionEntity) {
    if (redisExecutionEntity == null) {
      return new ArrayList<>();
    }

    return redisExecutionEntityMapper.queryByEntity(redisExecutionEntity);
  }

  public RedisExecutionEntity queryById(Long id) {
    if (id == null) {
      return null;
    }
    return redisExecutionEntityMapper.queryById(id);
  }

  public List<RedisExecutionEntity> queryByIdList(List<Long> idList) {
    if (idList == null) {
      return new ArrayList<>();
    }
    idList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(idList)) {
      return new ArrayList<>();
    }
    return BatchUtils.applyListInBatches(idList,
            longs -> redisExecutionEntityMapper.queryByIdList(longs));
  }

  public Map<Long, RedisExecutionEntity> queryMapByEntity(RedisExecutionEntity redisExecutionEntity) {
    List<RedisExecutionEntity> redisExecutionEntityList = queryByEntity(redisExecutionEntity);
    return redisExecutionEntityList.stream().collect(Collectors.toMap(RedisExecutionEntity::getId, a -> a));
  }

  public Map<Long, RedisExecutionEntity> queryMapByIdList(List<Long> idList) {
    List<RedisExecutionEntity> redisExecutionEntityList = queryByIdList(idList);
    return redisExecutionEntityList.stream().collect(Collectors.toMap(RedisExecutionEntity::getId, a -> a));
  }

}

