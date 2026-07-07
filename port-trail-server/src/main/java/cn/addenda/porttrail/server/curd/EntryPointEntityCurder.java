package cn.addenda.porttrail.server.curd;

import cn.addenda.component.base.collection.BatchUtils;
import cn.addenda.component.mybatis.helper.BatchDmlHelper;
import cn.addenda.porttrail.server.entity.EntryPointEntity;
import cn.addenda.porttrail.server.mapper.EntryPointEntityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * EntryPoint(EntryPointEntity)业务层
 *
 * @author addenda
 * @since 2026-02-16 18:40:58
 */
@Component
public class EntryPointEntityCurder {

  @Autowired
  private EntryPointEntityMapper entryPointEntityMapper;

  @Autowired
  private BatchDmlHelper batchDmlHelper;

  public int insert(EntryPointEntity entryPointEntity) {
    return entryPointEntityMapper.insert(entryPointEntity);
  }

  public int updateById(EntryPointEntity entryPointEntity) {
    return entryPointEntityMapper.updateById(entryPointEntity);
  }

  public int deleteById(Long id) {
    return entryPointEntityMapper.deleteById(id);
  }

  public void batchInsert(List<EntryPointEntity> entryPointEntityList) {
    if (entryPointEntityList == null) {
      return;
    }
    entryPointEntityList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(entryPointEntityList)) {
      return;
    }
    batchDmlHelper.batch(EntryPointEntityMapper.class, entryPointEntityList,
            (mapper, entryPointEntity) -> {
              mapper.insert(entryPointEntity);
            });
  }

  public void batchUpdateById(List<EntryPointEntity> entryPointEntityList) {
    if (entryPointEntityList == null) {
      return;
    }
    entryPointEntityList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(entryPointEntityList)) {
      return;
    }
    batchDmlHelper.batch(EntryPointEntityMapper.class, entryPointEntityList,
            (mapper, entryPointEntity) -> {
              mapper.updateById(entryPointEntity);
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
    batchDmlHelper.batch(EntryPointEntityMapper.class, idList,
            (mapper, id) -> {
              mapper.deleteById(id);
            });
  }

  public List<EntryPointEntity> queryByEntity(EntryPointEntity entryPointEntity) {
    if (entryPointEntity == null) {
      return new ArrayList<>();
    }

    return entryPointEntityMapper.queryByEntity(entryPointEntity);
  }

  public EntryPointEntity queryById(Long id) {
    if (id == null) {
      return null;
    }
    return entryPointEntityMapper.queryById(id);
  }

  public List<EntryPointEntity> queryByIdList(List<Long> idList) {
    if (idList == null) {
      return new ArrayList<>();
    }
    idList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(idList)) {
      return new ArrayList<>();
    }
    return BatchUtils.applyListInBatches(idList,
            longs -> entryPointEntityMapper.queryByIdList(longs));
  }

  public Map<Long, EntryPointEntity> queryMapByEntity(EntryPointEntity entryPointEntity) {
    List<EntryPointEntity> entryPointEntityList = queryByEntity(entryPointEntity);
    return entryPointEntityList.stream().collect(Collectors.toMap(EntryPointEntity::getId, a -> a));
  }

  public Map<Long, EntryPointEntity> queryMapByIdList(List<Long> idList) {
    List<EntryPointEntity> entryPointEntityList = queryByIdList(idList);
    return entryPointEntityList.stream().collect(Collectors.toMap(EntryPointEntity::getId, a -> a));
  }

}
