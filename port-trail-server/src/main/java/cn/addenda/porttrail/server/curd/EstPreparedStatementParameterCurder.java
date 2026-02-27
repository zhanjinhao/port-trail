package cn.addenda.porttrail.server.curd;

import cn.addenda.component.base.collection.BatchUtils;
import cn.addenda.component.mybatis.helper.BatchDmlHelper;
import cn.addenda.porttrail.server.entity.EstPreparedStatementParameter;
import cn.addenda.porttrail.server.mapper.EstPreparedStatementParameterMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * PreparedStatementParameter(EstPreparedStatementParameter)业务层
 *
 * @author addenda
 * @since 2026-02-16 19:08:23
 */
@Component
public class EstPreparedStatementParameterCurder {

  @Autowired
  private EstPreparedStatementParameterMapper estPreparedStatementParameterMapper;

  @Autowired
  private BatchDmlHelper batchDmlHelper;

  public int insert(EstPreparedStatementParameter estPreparedStatementParameter) {
    return estPreparedStatementParameterMapper.insert(estPreparedStatementParameter);
  }

  public int updateById(EstPreparedStatementParameter estPreparedStatementParameter) {
    return estPreparedStatementParameterMapper.updateById(estPreparedStatementParameter);
  }

  public int deleteById(Long id) {
    return estPreparedStatementParameterMapper.deleteById(id);
  }

  public void batchInsert(List<EstPreparedStatementParameter> estPreparedStatementParameterList) {
    if (estPreparedStatementParameterList == null) {
      return;
    }
    estPreparedStatementParameterList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(estPreparedStatementParameterList)) {
      return;
    }
    batchDmlHelper.batch(EstPreparedStatementParameterMapper.class, estPreparedStatementParameterList,
            (mapper, estPreparedStatementParameter) -> {
              mapper.insert(estPreparedStatementParameter);
            });
  }

  public void batchUpdateById(List<EstPreparedStatementParameter> estPreparedStatementParameterList) {
    if (estPreparedStatementParameterList == null) {
      return;
    }
    estPreparedStatementParameterList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(estPreparedStatementParameterList)) {
      return;
    }
    batchDmlHelper.batch(EstPreparedStatementParameterMapper.class, estPreparedStatementParameterList,
            (mapper, estPreparedStatementParameter) -> {
              mapper.updateById(estPreparedStatementParameter);
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
    batchDmlHelper.batch(EstPreparedStatementParameterMapper.class, idList,
            (mapper, id) -> {
              mapper.deleteById(id);
            });
  }

  public List<EstPreparedStatementParameter> queryByEntity(EstPreparedStatementParameter estPreparedStatementParameter) {
    if (estPreparedStatementParameter == null) {
      return new ArrayList<>();
    }

    return estPreparedStatementParameterMapper.queryByEntity(estPreparedStatementParameter);
  }

  public EstPreparedStatementParameter queryById(Long id) {
    if (id == null) {
      return null;
    }
    return estPreparedStatementParameterMapper.queryById(id);
  }

  public List<EstPreparedStatementParameter> queryByIdList(List<Long> idList) {
    if (idList == null) {
      return new ArrayList<>();
    }
    idList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(idList)) {
      return new ArrayList<>();
    }
    return BatchUtils.applyListInBatches(idList,
            longs -> estPreparedStatementParameterMapper.queryByIdList(longs));
  }

  public Map<Long, EstPreparedStatementParameter> queryMapByEntity(EstPreparedStatementParameter estPreparedStatementParameter) {
    List<EstPreparedStatementParameter> estPreparedStatementParameterList = queryByEntity(estPreparedStatementParameter);
    return estPreparedStatementParameterList.stream().collect(Collectors.toMap(EstPreparedStatementParameter::getId, a -> a));
  }

  public Map<Long, EstPreparedStatementParameter> queryMapByIdList(List<Long> idList) {
    List<EstPreparedStatementParameter> estPreparedStatementParameterList = queryByIdList(idList);
    return estPreparedStatementParameterList.stream().collect(Collectors.toMap(EstPreparedStatementParameter::getId, a -> a));
  }

}
