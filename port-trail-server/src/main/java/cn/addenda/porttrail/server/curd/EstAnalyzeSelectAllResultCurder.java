package cn.addenda.porttrail.server.curd;

import cn.addenda.component.base.collection.BatchUtils;
import cn.addenda.component.mybatis.helper.BatchDmlHelper;
import cn.addenda.porttrail.server.entity.EstAnalyzeSelectAllResult;
import cn.addenda.porttrail.server.mapper.EstAnalyzeSelectAllResultMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 分析-select语句是否有*(EstAnalyzeSelectAllResult)业务层
 *
 * @author addenda
 * @since 2026-02-24 18:52:04
 */
@Component
public class EstAnalyzeSelectAllResultCurder {

  @Autowired
  private EstAnalyzeSelectAllResultMapper estAnalyzeSelectAllResultMapper;

  @Autowired
  private BatchDmlHelper batchDmlHelper;

  public int insert(EstAnalyzeSelectAllResult estAnalyzeSelectAllResult) {
    return estAnalyzeSelectAllResultMapper.insert(estAnalyzeSelectAllResult);
  }

  public int updateById(EstAnalyzeSelectAllResult estAnalyzeSelectAllResult) {
    return estAnalyzeSelectAllResultMapper.updateById(estAnalyzeSelectAllResult);
  }

  public int deleteById(Long id) {
    return estAnalyzeSelectAllResultMapper.deleteById(id);
  }

  public void batchInsert(List<EstAnalyzeSelectAllResult> estAnalyzeSelectAllResultList) {
    if (estAnalyzeSelectAllResultList == null) {
      return;
    }
    estAnalyzeSelectAllResultList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(estAnalyzeSelectAllResultList)) {
      return;
    }
    batchDmlHelper.batch(EstAnalyzeSelectAllResultMapper.class, estAnalyzeSelectAllResultList,
            (mapper, estAnalyzeSelectAllResult) -> {
              mapper.insert(estAnalyzeSelectAllResult);
            });
  }

  public void batchUpdateById(List<EstAnalyzeSelectAllResult> estAnalyzeSelectAllResultList) {
    if (estAnalyzeSelectAllResultList == null) {
      return;
    }
    estAnalyzeSelectAllResultList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(estAnalyzeSelectAllResultList)) {
      return;
    }
    batchDmlHelper.batch(EstAnalyzeSelectAllResultMapper.class, estAnalyzeSelectAllResultList,
            (mapper, estAnalyzeSelectAllResult) -> {
              mapper.updateById(estAnalyzeSelectAllResult);
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
    batchDmlHelper.batch(EstAnalyzeSelectAllResultMapper.class, idList,
            (mapper, id) -> {
              mapper.deleteById(id);
            });
  }

  public List<EstAnalyzeSelectAllResult> queryByEntity(EstAnalyzeSelectAllResult estAnalyzeSelectAllResult) {
    if (estAnalyzeSelectAllResult == null) {
      return new ArrayList<>();
    }

    return estAnalyzeSelectAllResultMapper.queryByEntity(estAnalyzeSelectAllResult);
  }

  public EstAnalyzeSelectAllResult queryById(Long id) {
    if (id == null) {
      return null;
    }
    return estAnalyzeSelectAllResultMapper.queryById(id);
  }

  public List<EstAnalyzeSelectAllResult> queryByIdList(List<Long> idList) {
    if (idList == null) {
      return new ArrayList<>();
    }
    idList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(idList)) {
      return new ArrayList<>();
    }
    return BatchUtils.applyListInBatches(idList,
            longs -> estAnalyzeSelectAllResultMapper.queryByIdList(longs));
  }

  public Map<Long, EstAnalyzeSelectAllResult> queryMapByEntity(EstAnalyzeSelectAllResult estAnalyzeSelectAllResult) {
    List<EstAnalyzeSelectAllResult> estAnalyzeSelectAllResultList = queryByEntity(estAnalyzeSelectAllResult);
    return estAnalyzeSelectAllResultList.stream().collect(Collectors.toMap(EstAnalyzeSelectAllResult::getId, a -> a));
  }

  public Map<Long, EstAnalyzeSelectAllResult> queryMapByIdList(List<Long> idList) {
    List<EstAnalyzeSelectAllResult> estAnalyzeSelectAllResultList = queryByIdList(idList);
    return estAnalyzeSelectAllResultList.stream().collect(Collectors.toMap(EstAnalyzeSelectAllResult::getId, a -> a));
  }

}
