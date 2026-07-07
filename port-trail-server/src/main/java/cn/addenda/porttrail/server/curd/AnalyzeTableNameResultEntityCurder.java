package cn.addenda.porttrail.server.curd;

import cn.addenda.component.base.collection.BatchUtils;
import cn.addenda.component.mybatis.helper.BatchDmlHelper;
import cn.addenda.porttrail.server.entity.AnalyzeTableNameResultEntity;
import cn.addenda.porttrail.server.mapper.AnalyzeTableNameResultEntityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class AnalyzeTableNameResultEntityCurder {

  @Autowired
  private AnalyzeTableNameResultEntityMapper analyzeTableNameResultEntityMapper;

  @Autowired
  private BatchDmlHelper batchDmlHelper;

  public int insert(AnalyzeTableNameResultEntity analyzeTableNameResultEntity) {
    return analyzeTableNameResultEntityMapper.insert(analyzeTableNameResultEntity);
  }

  public int updateById(AnalyzeTableNameResultEntity analyzeTableNameResultEntity) {
    return analyzeTableNameResultEntityMapper.updateById(analyzeTableNameResultEntity);
  }

  public int deleteById(Long id) {
    return analyzeTableNameResultEntityMapper.deleteById(id);
  }

  public void batchInsert(List<AnalyzeTableNameResultEntity> analyzeTableNameResultEntityList) {
    if (analyzeTableNameResultEntityList == null) { return; }
    analyzeTableNameResultEntityList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(analyzeTableNameResultEntityList)) { return; }
    batchDmlHelper.batch(AnalyzeTableNameResultEntityMapper.class, analyzeTableNameResultEntityList,
            (mapper, analyzeTableNameResultEntity) -> {
              mapper.insert(analyzeTableNameResultEntity);
            });
  }

  public void batchUpdateById(List<AnalyzeTableNameResultEntity> analyzeTableNameResultEntityList) {
    if (analyzeTableNameResultEntityList == null) { return; }
    analyzeTableNameResultEntityList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(analyzeTableNameResultEntityList)) { return; }
    batchDmlHelper.batch(AnalyzeTableNameResultEntityMapper.class, analyzeTableNameResultEntityList,
            (mapper, analyzeTableNameResultEntity) -> {
              mapper.updateById(analyzeTableNameResultEntity);
            });
  }

  public void batchDeleteById(List<Long> idList) {
    if (idList == null) { return; }
    idList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(idList)) { return; }
    batchDmlHelper.batch(AnalyzeTableNameResultEntityMapper.class, idList,
            (mapper, id) -> {
              mapper.deleteById(id);
            });
  }

  public List<AnalyzeTableNameResultEntity> queryByEntity(AnalyzeTableNameResultEntity analyzeTableNameResultEntity) {
    if (analyzeTableNameResultEntity == null) { return new ArrayList<>(); }
    return analyzeTableNameResultEntityMapper.queryByEntity(analyzeTableNameResultEntity);
  }

  public AnalyzeTableNameResultEntity queryById(Long id) {
    if (id == null) { return null; }
    return analyzeTableNameResultEntityMapper.queryById(id);
  }

  public List<AnalyzeTableNameResultEntity> queryByIdList(List<Long> idList) {
    if (idList == null) { return new ArrayList<>(); }
    idList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(idList)) { return new ArrayList<>(); }
    return BatchUtils.applyListInBatches(idList, longs -> analyzeTableNameResultEntityMapper.queryByIdList(longs));
  }

  public Map<Long, AnalyzeTableNameResultEntity> queryMapByEntity(AnalyzeTableNameResultEntity analyzeTableNameResultEntity) {
    List<AnalyzeTableNameResultEntity> list = queryByEntity(analyzeTableNameResultEntity);
    return list.stream().collect(Collectors.toMap(AnalyzeTableNameResultEntity::getId, a -> a));
  }

  public Map<Long, AnalyzeTableNameResultEntity> queryMapByIdList(List<Long> idList) {
    List<AnalyzeTableNameResultEntity> list = queryByIdList(idList);
    return list.stream().collect(Collectors.toMap(AnalyzeTableNameResultEntity::getId, a -> a));
  }

}
