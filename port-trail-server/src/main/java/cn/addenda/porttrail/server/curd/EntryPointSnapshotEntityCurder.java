package cn.addenda.porttrail.server.curd;

import cn.addenda.component.base.collection.BatchUtils;
import cn.addenda.component.mybatis.helper.BatchDmlHelper;
import cn.addenda.porttrail.server.entity.EntryPointSnapshotEntity;
import cn.addenda.porttrail.server.mapper.EntryPointSnapshotEntityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * EntryPoint快照(EntryPointSnapshotEntity)业务层
 *
 * @author addenda
 * @since 2026-02-16 19:03:56
 */
@Component
public class EntryPointSnapshotEntityCurder {

  @Autowired
  private EntryPointSnapshotEntityMapper entryPointSnapshotEntityMapper;

  @Autowired
  private BatchDmlHelper batchDmlHelper;

  public int insert(EntryPointSnapshotEntity entryPointSnapshotEntity) {
    return entryPointSnapshotEntityMapper.insert(entryPointSnapshotEntity);
  }

  public int updateById(EntryPointSnapshotEntity entryPointSnapshotEntity) {
    return entryPointSnapshotEntityMapper.updateById(entryPointSnapshotEntity);
  }

  public int deleteById(Long id) {
    return entryPointSnapshotEntityMapper.deleteById(id);
  }

  public void batchInsert(List<EntryPointSnapshotEntity> entryPointSnapshotEntityList) {
    if (entryPointSnapshotEntityList == null) {
      return;
    }
    entryPointSnapshotEntityList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(entryPointSnapshotEntityList)) {
      return;
    }
    batchDmlHelper.batch(EntryPointSnapshotEntityMapper.class, entryPointSnapshotEntityList,
            (mapper, entryPointSnapshotEntity) -> {
              mapper.insert(entryPointSnapshotEntity);
            });
  }

  public void batchUpdateById(List<EntryPointSnapshotEntity> entryPointSnapshotEntityList) {
    if (entryPointSnapshotEntityList == null) {
      return;
    }
    entryPointSnapshotEntityList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(entryPointSnapshotEntityList)) {
      return;
    }
    batchDmlHelper.batch(EntryPointSnapshotEntityMapper.class, entryPointSnapshotEntityList,
            (mapper, entryPointSnapshotEntity) -> {
              mapper.updateById(entryPointSnapshotEntity);
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
    batchDmlHelper.batch(EntryPointSnapshotEntityMapper.class, idList,
            (mapper, id) -> {
              mapper.deleteById(id);
            });
  }

  public List<EntryPointSnapshotEntity> queryByEntity(EntryPointSnapshotEntity entryPointSnapshotEntity) {
    if (entryPointSnapshotEntity == null) {
      return new ArrayList<>();
    }

    return entryPointSnapshotEntityMapper.queryByEntity(entryPointSnapshotEntity);
  }

  public EntryPointSnapshotEntity queryById(Long id) {
    if (id == null) {
      return null;
    }
    return entryPointSnapshotEntityMapper.queryById(id);
  }

  public List<EntryPointSnapshotEntity> queryByIdList(List<Long> idList) {
    if (idList == null) {
      return new ArrayList<>();
    }
    idList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(idList)) {
      return new ArrayList<>();
    }
    return BatchUtils.applyListInBatches(idList,
            longs -> entryPointSnapshotEntityMapper.queryByIdList(longs));
  }

  public Map<Long, EntryPointSnapshotEntity> queryMapByEntity(EntryPointSnapshotEntity entryPointSnapshotEntity) {
    List<EntryPointSnapshotEntity> entryPointSnapshotEntityList = queryByEntity(entryPointSnapshotEntity);
    return entryPointSnapshotEntityList.stream().collect(Collectors.toMap(EntryPointSnapshotEntity::getId, a -> a));
  }

  public Map<Long, EntryPointSnapshotEntity> queryMapByIdList(List<Long> idList) {
    List<EntryPointSnapshotEntity> entryPointSnapshotEntityList = queryByIdList(idList);
    return entryPointSnapshotEntityList.stream().collect(Collectors.toMap(EntryPointSnapshotEntity::getId, a -> a));
  }

}
