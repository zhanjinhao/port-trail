package cn.addenda.porttrail.server.curd;

import cn.addenda.component.base.collection.BatchUtils;
import cn.addenda.component.mybatis.helper.BatchDmlHelper;
import cn.addenda.porttrail.server.entity.AnalyzeSelectAllResultEntity;
import cn.addenda.porttrail.server.mapper.AnalyzeSelectAllResultEntityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 分析-select语句是否有*(AnalyzeSelectAllResultEntity)业务层
 *
 * @author addenda
 * @since 2026-02-24 18:52:04
 */
@Component
public class AnalyzeSelectAllResultEntityCurder {

  @Autowired
  private AnalyzeSelectAllResultEntityMapper analyzeSelectAllResultEntityMapper;

  @Autowired
  private BatchDmlHelper batchDmlHelper;

  public int insert(AnalyzeSelectAllResultEntity analyzeSelectAllResultEntity) {
    return analyzeSelectAllResultEntityMapper.insert(analyzeSelectAllResultEntity);
  }

  public int updateById(AnalyzeSelectAllResultEntity analyzeSelectAllResultEntity) {
    return analyzeSelectAllResultEntityMapper.updateById(analyzeSelectAllResultEntity);
  }

  public int deleteById(Long id) {
    return analyzeSelectAllResultEntityMapper.deleteById(id);
  }

  public void batchInsert(List<AnalyzeSelectAllResultEntity> analyzeSelectAllResultEntityList) {
    if (analyzeSelectAllResultEntityList == null) {
      return;
    }
    analyzeSelectAllResultEntityList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(analyzeSelectAllResultEntityList)) {
      return;
    }
    batchDmlHelper.batch(AnalyzeSelectAllResultEntityMapper.class, analyzeSelectAllResultEntityList,
            (mapper, analyzeSelectAllResultEntity) -> {
              mapper.insert(analyzeSelectAllResultEntity);
            });
  }

  public void batchUpdateById(List<AnalyzeSelectAllResultEntity> analyzeSelectAllResultEntityList) {
    if (analyzeSelectAllResultEntityList == null) {
      return;
    }
    analyzeSelectAllResultEntityList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(analyzeSelectAllResultEntityList)) {
      return;
    }
    batchDmlHelper.batch(AnalyzeSelectAllResultEntityMapper.class, analyzeSelectAllResultEntityList,
            (mapper, analyzeSelectAllResultEntity) -> {
              mapper.updateById(analyzeSelectAllResultEntity);
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
    batchDmlHelper.batch(AnalyzeSelectAllResultEntityMapper.class, idList,
            (mapper, id) -> {
              mapper.deleteById(id);
            });
  }

  public List<AnalyzeSelectAllResultEntity> queryByEntity(AnalyzeSelectAllResultEntity analyzeSelectAllResultEntity) {
    if (analyzeSelectAllResultEntity == null) {
      return new ArrayList<>();
    }

    return analyzeSelectAllResultEntityMapper.queryByEntity(analyzeSelectAllResultEntity);
  }

  public AnalyzeSelectAllResultEntity queryById(Long id) {
    if (id == null) {
      return null;
    }
    return analyzeSelectAllResultEntityMapper.queryById(id);
  }

  public List<AnalyzeSelectAllResultEntity> queryByIdList(List<Long> idList) {
    if (idList == null) {
      return new ArrayList<>();
    }
    idList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(idList)) {
      return new ArrayList<>();
    }
    return BatchUtils.applyListInBatches(idList,
            longs -> analyzeSelectAllResultEntityMapper.queryByIdList(longs));
  }

  public Map<Long, AnalyzeSelectAllResultEntity> queryMapByEntity(AnalyzeSelectAllResultEntity analyzeSelectAllResultEntity) {
    List<AnalyzeSelectAllResultEntity> analyzeSelectAllResultEntityList = queryByEntity(analyzeSelectAllResultEntity);
    return analyzeSelectAllResultEntityList.stream().collect(Collectors.toMap(AnalyzeSelectAllResultEntity::getId, a -> a));
  }

  public Map<Long, AnalyzeSelectAllResultEntity> queryMapByIdList(List<Long> idList) {
    List<AnalyzeSelectAllResultEntity> analyzeSelectAllResultEntityList = queryByIdList(idList);
    return analyzeSelectAllResultEntityList.stream().collect(Collectors.toMap(AnalyzeSelectAllResultEntity::getId, a -> a));
  }

}
