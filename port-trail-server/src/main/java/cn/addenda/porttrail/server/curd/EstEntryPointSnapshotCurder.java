package cn.addenda.porttrail.server.curd;

import cn.addenda.component.base.collection.BatchUtils;
import cn.addenda.component.mybatis.helper.BatchDmlHelper;
import cn.addenda.porttrail.server.entity.EstEntryPointSnapshot;
import cn.addenda.porttrail.server.mapper.EstEntryPointSnapshotMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * EntryPoint快照(EstEntryPointSnapshot)业务层
 *
 * @author addenda
 * @since 2026-02-16 19:03:56
 */
@Component
public class EstEntryPointSnapshotCurder {

  @Autowired
  private EstEntryPointSnapshotMapper estEntryPointSnapshotMapper;

  @Autowired
  private BatchDmlHelper batchDmlHelper;

  public int insert(EstEntryPointSnapshot estEntryPointSnapshot) {
    return estEntryPointSnapshotMapper.insert(estEntryPointSnapshot);
  }

  public int updateById(EstEntryPointSnapshot estEntryPointSnapshot) {
    return estEntryPointSnapshotMapper.updateById(estEntryPointSnapshot);
  }

  public int deleteById(Long id) {
    return estEntryPointSnapshotMapper.deleteById(id);
  }

  public void batchInsert(List<EstEntryPointSnapshot> estEntryPointSnapshotList) {
    if (estEntryPointSnapshotList == null) {
      return;
    }
    estEntryPointSnapshotList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(estEntryPointSnapshotList)) {
      return;
    }
    batchDmlHelper.batch(EstEntryPointSnapshotMapper.class, estEntryPointSnapshotList,
            (mapper, estEntryPointSnapshot) -> {
              mapper.insert(estEntryPointSnapshot);
            });
  }

  public void batchUpdateById(List<EstEntryPointSnapshot> estEntryPointSnapshotList) {
    if (estEntryPointSnapshotList == null) {
      return;
    }
    estEntryPointSnapshotList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(estEntryPointSnapshotList)) {
      return;
    }
    batchDmlHelper.batch(EstEntryPointSnapshotMapper.class, estEntryPointSnapshotList,
            (mapper, estEntryPointSnapshot) -> {
              mapper.updateById(estEntryPointSnapshot);
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
    batchDmlHelper.batch(EstEntryPointSnapshotMapper.class, idList,
            (mapper, id) -> {
              mapper.deleteById(id);
            });
  }

  public List<EstEntryPointSnapshot> queryByEntity(EstEntryPointSnapshot estEntryPointSnapshot) {
    if (estEntryPointSnapshot == null) {
      return new ArrayList<>();
    }

    return estEntryPointSnapshotMapper.queryByEntity(estEntryPointSnapshot);
  }

  public EstEntryPointSnapshot queryById(Long id) {
    if (id == null) {
      return null;
    }
    return estEntryPointSnapshotMapper.queryById(id);
  }

  public List<EstEntryPointSnapshot> queryByIdList(List<Long> idList) {
    if (idList == null) {
      return new ArrayList<>();
    }
    idList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(idList)) {
      return new ArrayList<>();
    }
    return BatchUtils.applyListInBatches(idList,
            longs -> estEntryPointSnapshotMapper.queryByIdList(longs));
  }

  public Map<Long, EstEntryPointSnapshot> queryMapByEntity(EstEntryPointSnapshot estEntryPointSnapshot) {
    List<EstEntryPointSnapshot> estEntryPointSnapshotList = queryByEntity(estEntryPointSnapshot);
    return estEntryPointSnapshotList.stream().collect(Collectors.toMap(EstEntryPointSnapshot::getId, a -> a));
  }

  public Map<Long, EstEntryPointSnapshot> queryMapByIdList(List<Long> idList) {
    List<EstEntryPointSnapshot> estEntryPointSnapshotList = queryByIdList(idList);
    return estEntryPointSnapshotList.stream().collect(Collectors.toMap(EstEntryPointSnapshot::getId, a -> a));
  }

}
