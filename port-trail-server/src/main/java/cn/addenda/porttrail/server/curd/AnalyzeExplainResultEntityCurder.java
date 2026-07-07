package cn.addenda.porttrail.server.curd;

import cn.addenda.component.base.collection.BatchUtils;
import cn.addenda.component.mybatis.helper.BatchDmlHelper;
import cn.addenda.porttrail.server.entity.AnalyzeExplainResultEntity;
import cn.addenda.porttrail.server.mapper.AnalyzeExplainResultEntityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 分析-SQL的explain(AnalyzeExplainResultEntity)业务层
 *
 * @author addenda
 * @since 2026-02-24 21:12:52
 */
@Component
public class AnalyzeExplainResultEntityCurder {

  @Autowired
  private AnalyzeExplainResultEntityMapper analyzeExplainResultEntityMapper;

  @Autowired
  private BatchDmlHelper batchDmlHelper;

  public int insert(AnalyzeExplainResultEntity analyzeExplainResultEntity) {
    return analyzeExplainResultEntityMapper.insert(analyzeExplainResultEntity);
  }

  public int updateById(AnalyzeExplainResultEntity analyzeExplainResultEntity) {
    return analyzeExplainResultEntityMapper.updateById(analyzeExplainResultEntity);
  }

  public int deleteById(Long id) {
    return analyzeExplainResultEntityMapper.deleteById(id);
  }

  public void batchInsert(List<AnalyzeExplainResultEntity> analyzeExplainResultEntityList) {
    if (analyzeExplainResultEntityList == null) {
      return;
    }
    analyzeExplainResultEntityList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(analyzeExplainResultEntityList)) {
      return;
    }
    batchDmlHelper.batch(AnalyzeExplainResultEntityMapper.class, analyzeExplainResultEntityList,
            (mapper, analyzeExplainResultEntity) -> {
              mapper.insert(analyzeExplainResultEntity);
            });
  }

  public void batchUpdateById(List<AnalyzeExplainResultEntity> analyzeExplainResultEntityList) {
    if (analyzeExplainResultEntityList == null) {
      return;
    }
    analyzeExplainResultEntityList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(analyzeExplainResultEntityList)) {
      return;
    }
    batchDmlHelper.batch(AnalyzeExplainResultEntityMapper.class, analyzeExplainResultEntityList,
            (mapper, analyzeExplainResultEntity) -> {
              mapper.updateById(analyzeExplainResultEntity);
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
    batchDmlHelper.batch(AnalyzeExplainResultEntityMapper.class, idList,
            (mapper, id) -> {
              mapper.deleteById(id);
            });
  }

  public List<AnalyzeExplainResultEntity> queryByEntity(AnalyzeExplainResultEntity analyzeExplainResultEntity) {
    if (analyzeExplainResultEntity == null) {
      return new ArrayList<>();
    }

    return analyzeExplainResultEntityMapper.queryByEntity(analyzeExplainResultEntity);
  }

  public AnalyzeExplainResultEntity queryById(Long id) {
    if (id == null) {
      return null;
    }
    return analyzeExplainResultEntityMapper.queryById(id);
  }

  public List<AnalyzeExplainResultEntity> queryByIdList(List<Long> idList) {
    if (idList == null) {
      return new ArrayList<>();
    }
    idList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(idList)) {
      return new ArrayList<>();
    }
    return BatchUtils.applyListInBatches(idList,
            longs -> analyzeExplainResultEntityMapper.queryByIdList(longs));
  }

  public Map<Long, AnalyzeExplainResultEntity> queryMapByEntity(AnalyzeExplainResultEntity analyzeExplainResultEntity) {
    List<AnalyzeExplainResultEntity> analyzeExplainResultEntityList = queryByEntity(analyzeExplainResultEntity);
    return analyzeExplainResultEntityList.stream().collect(Collectors.toMap(AnalyzeExplainResultEntity::getId, a -> a));
  }

  public Map<Long, AnalyzeExplainResultEntity> queryMapByIdList(List<Long> idList) {
    List<AnalyzeExplainResultEntity> analyzeExplainResultEntityList = queryByIdList(idList);
    return analyzeExplainResultEntityList.stream().collect(Collectors.toMap(AnalyzeExplainResultEntity::getId, a -> a));
  }

}
