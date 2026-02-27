package cn.addenda.porttrail.server.curd;

import cn.addenda.component.base.collection.BatchUtils;
import cn.addenda.component.mybatis.helper.BatchDmlHelper;
import cn.addenda.porttrail.server.entity.EstAnalyzeTableNameResult;
import cn.addenda.porttrail.server.mapper.EstAnalyzeTableNameResultMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 分析-表名(EstAnalyzeTableNameResult)业务层
 *
 * @author addenda
 * @since 2026-02-24 16:44:43
 */
@Component
public class EstAnalyzeTableNameResultCurder {

  @Autowired
  private EstAnalyzeTableNameResultMapper estAnalyzeTableNameResultMapper;

  @Autowired
  private BatchDmlHelper batchDmlHelper;

  public int insert(EstAnalyzeTableNameResult estAnalyzeTableNameResult) {
    return estAnalyzeTableNameResultMapper.insert(estAnalyzeTableNameResult);
  }

  public int updateById(EstAnalyzeTableNameResult estAnalyzeTableNameResult) {
    return estAnalyzeTableNameResultMapper.updateById(estAnalyzeTableNameResult);
  }

  public int deleteById(Long id) {
    return estAnalyzeTableNameResultMapper.deleteById(id);
  }

  public void batchInsert(List<EstAnalyzeTableNameResult> estAnalyzeTableNameResultList) {
    if (estAnalyzeTableNameResultList == null) {
      return;
    }
    estAnalyzeTableNameResultList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(estAnalyzeTableNameResultList)) {
      return;
    }
    batchDmlHelper.batch(EstAnalyzeTableNameResultMapper.class, estAnalyzeTableNameResultList,
            (mapper, estAnalyzeTableNameResult) -> {
              mapper.insert(estAnalyzeTableNameResult);
            });
  }

  public void batchUpdateById(List<EstAnalyzeTableNameResult> estAnalyzeTableNameResultList) {
    if (estAnalyzeTableNameResultList == null) {
      return;
    }
    estAnalyzeTableNameResultList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(estAnalyzeTableNameResultList)) {
      return;
    }
    batchDmlHelper.batch(EstAnalyzeTableNameResultMapper.class, estAnalyzeTableNameResultList,
            (mapper, estAnalyzeTableNameResult) -> {
              mapper.updateById(estAnalyzeTableNameResult);
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
    batchDmlHelper.batch(EstAnalyzeTableNameResultMapper.class, idList,
            (mapper, id) -> {
              mapper.deleteById(id);
            });
  }

  public List<EstAnalyzeTableNameResult> queryByEntity(EstAnalyzeTableNameResult estAnalyzeTableNameResult) {
    if (estAnalyzeTableNameResult == null) {
      return new ArrayList<>();
    }

    return estAnalyzeTableNameResultMapper.queryByEntity(estAnalyzeTableNameResult);
  }

  public EstAnalyzeTableNameResult queryById(Long id) {
    if (id == null) {
      return null;
    }
    return estAnalyzeTableNameResultMapper.queryById(id);
  }

  public List<EstAnalyzeTableNameResult> queryByIdList(List<Long> idList) {
    if (idList == null) {
      return new ArrayList<>();
    }
    idList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(idList)) {
      return new ArrayList<>();
    }
    return BatchUtils.applyListInBatches(idList,
            longs -> estAnalyzeTableNameResultMapper.queryByIdList(longs));
  }

  public Map<Long, EstAnalyzeTableNameResult> queryMapByEntity(EstAnalyzeTableNameResult estAnalyzeTableNameResult) {
    List<EstAnalyzeTableNameResult> estAnalyzeTableNameResultList = queryByEntity(estAnalyzeTableNameResult);
    return estAnalyzeTableNameResultList.stream().collect(Collectors.toMap(EstAnalyzeTableNameResult::getId, a -> a));
  }

  public Map<Long, EstAnalyzeTableNameResult> queryMapByIdList(List<Long> idList) {
    List<EstAnalyzeTableNameResult> estAnalyzeTableNameResultList = queryByIdList(idList);
    return estAnalyzeTableNameResultList.stream().collect(Collectors.toMap(EstAnalyzeTableNameResult::getId, a -> a));
  }

}
