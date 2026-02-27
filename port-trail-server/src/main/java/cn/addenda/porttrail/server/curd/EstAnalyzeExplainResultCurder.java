package cn.addenda.porttrail.server.curd;

import cn.addenda.component.base.collection.BatchUtils;
import cn.addenda.component.mybatis.helper.BatchDmlHelper;
import cn.addenda.porttrail.server.entity.EstAnalyzeExplainResult;
import cn.addenda.porttrail.server.mapper.EstAnalyzeExplainResultMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 分析-SQL的explain(EstAnalyzeExplainResult)业务层
 *
 * @author addenda
 * @since 2026-02-24 21:12:52
 */
@Component
public class EstAnalyzeExplainResultCurder {

  @Autowired
  private EstAnalyzeExplainResultMapper estAnalyzeExplainResultMapper;

  @Autowired
  private BatchDmlHelper batchDmlHelper;

  public int insert(EstAnalyzeExplainResult estAnalyzeExplainResult) {
    return estAnalyzeExplainResultMapper.insert(estAnalyzeExplainResult);
  }

  public int updateById(EstAnalyzeExplainResult estAnalyzeExplainResult) {
    return estAnalyzeExplainResultMapper.updateById(estAnalyzeExplainResult);
  }

  public int deleteById(Long id) {
    return estAnalyzeExplainResultMapper.deleteById(id);
  }

  public void batchInsert(List<EstAnalyzeExplainResult> estAnalyzeExplainResultList) {
    if (estAnalyzeExplainResultList == null) {
      return;
    }
    estAnalyzeExplainResultList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(estAnalyzeExplainResultList)) {
      return;
    }
    batchDmlHelper.batch(EstAnalyzeExplainResultMapper.class, estAnalyzeExplainResultList,
            (mapper, estAnalyzeExplainResult) -> {
              mapper.insert(estAnalyzeExplainResult);
            });
  }

  public void batchUpdateById(List<EstAnalyzeExplainResult> estAnalyzeExplainResultList) {
    if (estAnalyzeExplainResultList == null) {
      return;
    }
    estAnalyzeExplainResultList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(estAnalyzeExplainResultList)) {
      return;
    }
    batchDmlHelper.batch(EstAnalyzeExplainResultMapper.class, estAnalyzeExplainResultList,
            (mapper, estAnalyzeExplainResult) -> {
              mapper.updateById(estAnalyzeExplainResult);
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
    batchDmlHelper.batch(EstAnalyzeExplainResultMapper.class, idList,
            (mapper, id) -> {
              mapper.deleteById(id);
            });
  }

  public List<EstAnalyzeExplainResult> queryByEntity(EstAnalyzeExplainResult estAnalyzeExplainResult) {
    if (estAnalyzeExplainResult == null) {
      return new ArrayList<>();
    }

    return estAnalyzeExplainResultMapper.queryByEntity(estAnalyzeExplainResult);
  }

  public EstAnalyzeExplainResult queryById(Long id) {
    if (id == null) {
      return null;
    }
    return estAnalyzeExplainResultMapper.queryById(id);
  }

  public List<EstAnalyzeExplainResult> queryByIdList(List<Long> idList) {
    if (idList == null) {
      return new ArrayList<>();
    }
    idList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(idList)) {
      return new ArrayList<>();
    }
    return BatchUtils.applyListInBatches(idList,
            longs -> estAnalyzeExplainResultMapper.queryByIdList(longs));
  }

  public Map<Long, EstAnalyzeExplainResult> queryMapByEntity(EstAnalyzeExplainResult estAnalyzeExplainResult) {
    List<EstAnalyzeExplainResult> estAnalyzeExplainResultList = queryByEntity(estAnalyzeExplainResult);
    return estAnalyzeExplainResultList.stream().collect(Collectors.toMap(EstAnalyzeExplainResult::getId, a -> a));
  }

  public Map<Long, EstAnalyzeExplainResult> queryMapByIdList(List<Long> idList) {
    List<EstAnalyzeExplainResult> estAnalyzeExplainResultList = queryByIdList(idList);
    return estAnalyzeExplainResultList.stream().collect(Collectors.toMap(EstAnalyzeExplainResult::getId, a -> a));
  }

}
