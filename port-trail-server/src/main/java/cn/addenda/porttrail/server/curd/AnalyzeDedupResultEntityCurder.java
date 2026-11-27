package cn.addenda.porttrail.server.curd;

import cn.addenda.component.base.collection.BatchUtils;
import cn.addenda.porttrail.server.entity.AnalyzeDedupResultEntity;
import cn.addenda.porttrail.server.mapper.AnalyzeDedupResultEntityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 分析-问题SQL去重记录(AnalyzeDedupResultEntity)业务层
 *
 * @author addenda
 */
@Component
public class AnalyzeDedupResultEntityCurder {

  @Autowired
  private AnalyzeDedupResultEntityMapper analyzeDedupResultEntityMapper;

  public int insert(AnalyzeDedupResultEntity analyzeDedupResultEntity) {
    return analyzeDedupResultEntityMapper.insert(analyzeDedupResultEntity);
  }

  public int updateById(AnalyzeDedupResultEntity analyzeDedupResultEntity) {
    return analyzeDedupResultEntityMapper.updateById(analyzeDedupResultEntity);
  }

  public int mergeByDedupKey(AnalyzeDedupResultEntity analyzeDedupResultEntity) {
    return analyzeDedupResultEntityMapper.mergeByDedupKey(analyzeDedupResultEntity);
  }

  public int deleteById(Long id) {
    return analyzeDedupResultEntityMapper.deleteById(id);
  }

  public List<AnalyzeDedupResultEntity> queryByEntity(AnalyzeDedupResultEntity analyzeDedupResultEntity) {
    if (analyzeDedupResultEntity == null) {
      return new ArrayList<>();
    }
    return analyzeDedupResultEntityMapper.queryByEntity(analyzeDedupResultEntity);
  }

  public AnalyzeDedupResultEntity queryById(Long id) {
    if (id == null) {
      return null;
    }
    return analyzeDedupResultEntityMapper.queryById(id);
  }

  public String queryHitRulesByDedupKey(String dedupKey) {
    if (dedupKey == null) {
      return null;
    }
    return analyzeDedupResultEntityMapper.queryHitRulesByDedupKey(dedupKey);
  }

  public List<AnalyzeDedupResultEntity> queryByIdList(List<Long> idList) {
    if (idList == null) {
      return new ArrayList<>();
    }
    idList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(idList)) {
      return new ArrayList<>();
    }
    return BatchUtils.applyListInBatches(idList,
            longs -> analyzeDedupResultEntityMapper.queryByIdList(longs));
  }

  public Map<Long, AnalyzeDedupResultEntity> queryMapByEntity(AnalyzeDedupResultEntity analyzeDedupResultEntity) {
    List<AnalyzeDedupResultEntity> analyzeDedupResultEntityList = queryByEntity(analyzeDedupResultEntity);
    return analyzeDedupResultEntityList.stream().collect(Collectors.toMap(AnalyzeDedupResultEntity::getId, a -> a));
  }

  public Map<Long, AnalyzeDedupResultEntity> queryMapByIdList(List<Long> idList) {
    List<AnalyzeDedupResultEntity> analyzeDedupResultEntityList = queryByIdList(idList);
    return analyzeDedupResultEntityList.stream().collect(Collectors.toMap(AnalyzeDedupResultEntity::getId, a -> a));
  }

}
