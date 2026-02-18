package cn.addenda.porttrail.server.curd;

import cn.addenda.component.base.collection.BatchUtils;
import cn.addenda.component.mybatis.helper.BatchDmlHelper;
import cn.addenda.porttrail.server.entity.EstEntryPoint;
import cn.addenda.porttrail.server.mapper.EstEntryPointMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * EntryPoint(EstEntryPoint)业务层
 *
 * @author addenda
 * @since 2026-02-16 18:40:58
 */
@Component
public class EstEntryPointCurder {

  @Autowired
  private EstEntryPointMapper estEntryPointMapper;

  @Autowired
  private BatchDmlHelper batchDmlHelper;

  public int insert(EstEntryPoint estEntryPoint) {
    return estEntryPointMapper.insert(estEntryPoint);
  }

  public int updateById(EstEntryPoint estEntryPoint) {
    return estEntryPointMapper.updateById(estEntryPoint);
  }

  public int deleteById(Long id) {
    return estEntryPointMapper.deleteById(id);
  }

  public void batchInsert(List<EstEntryPoint> estEntryPointList) {
    if (estEntryPointList == null) {
      return;
    }
    estEntryPointList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(estEntryPointList)) {
      return;
    }
    batchDmlHelper.batch(EstEntryPointMapper.class, estEntryPointList,
            (mapper, estEntryPoint) -> {
              mapper.insert(estEntryPoint);
            });
  }

  public void batchUpdateById(List<EstEntryPoint> estEntryPointList) {
    if (estEntryPointList == null) {
      return;
    }
    estEntryPointList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(estEntryPointList)) {
      return;
    }
    batchDmlHelper.batch(EstEntryPointMapper.class, estEntryPointList,
            (mapper, estEntryPoint) -> {
              mapper.updateById(estEntryPoint);
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
    batchDmlHelper.batch(EstEntryPointMapper.class, idList,
            (mapper, id) -> {
              mapper.deleteById(id);
            });
  }

  public List<EstEntryPoint> queryByEntity(EstEntryPoint estEntryPoint) {
    if (estEntryPoint == null) {
      return new ArrayList<>();
    }

    return estEntryPointMapper.queryByEntity(estEntryPoint);
  }

  public EstEntryPoint queryById(Long id) {
    if (id == null) {
      return null;
    }
    return estEntryPointMapper.queryById(id);
  }

  public List<EstEntryPoint> queryByIdList(List<Long> idList) {
    if (idList == null) {
      return new ArrayList<>();
    }
    idList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(idList)) {
      return new ArrayList<>();
    }
    return BatchUtils.applyListInBatches(idList,
            longs -> estEntryPointMapper.queryByIdList(longs));
  }

  public Map<Long, EstEntryPoint> queryMapByEntity(EstEntryPoint estEntryPoint) {
    List<EstEntryPoint> estEntryPointList = queryByEntity(estEntryPoint);
    return estEntryPointList.stream().collect(Collectors.toMap(EstEntryPoint::getId, a -> a));
  }

  public Map<Long, EstEntryPoint> queryMapByIdList(List<Long> idList) {
    List<EstEntryPoint> estEntryPointList = queryByIdList(idList);
    return estEntryPointList.stream().collect(Collectors.toMap(EstEntryPoint::getId, a -> a));
  }

}
