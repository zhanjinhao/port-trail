package cn.addenda.porttrail.server.curd;

import cn.addenda.component.base.collection.BatchUtils;
import cn.addenda.component.mybatis.helper.BatchDmlHelper;
import cn.addenda.porttrail.server.entity.EstDbConfig;
import cn.addenda.porttrail.server.mapper.EstDbConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 数据库配置(EstDbConfig)业务层
 *
 * @author addenda
 * @since 2026-02-16 19:07:07
 */
@Component
public class EstDbConfigCurder {

  @Autowired
  private EstDbConfigMapper estDbConfigMapper;

  @Autowired
  private BatchDmlHelper batchDmlHelper;

  public int insert(EstDbConfig estDbConfig) {
    return estDbConfigMapper.insert(estDbConfig);
  }

  public int updateById(EstDbConfig estDbConfig) {
    return estDbConfigMapper.updateById(estDbConfig);
  }

  public int deleteById(Long id) {
    return estDbConfigMapper.deleteById(id);
  }

  public void batchInsert(List<EstDbConfig> estDbConfigList) {
    if (estDbConfigList == null) {
      return;
    }
    estDbConfigList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(estDbConfigList)) {
      return;
    }
    batchDmlHelper.batch(EstDbConfigMapper.class, estDbConfigList,
            (mapper, estDbConfig) -> {
              mapper.insert(estDbConfig);
            });
  }

  public void batchUpdateById(List<EstDbConfig> estDbConfigList) {
    if (estDbConfigList == null) {
      return;
    }
    estDbConfigList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(estDbConfigList)) {
      return;
    }
    batchDmlHelper.batch(EstDbConfigMapper.class, estDbConfigList,
            (mapper, estDbConfig) -> {
              mapper.updateById(estDbConfig);
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
    batchDmlHelper.batch(EstDbConfigMapper.class, idList,
            (mapper, id) -> {
              mapper.deleteById(id);
            });
  }

  public List<EstDbConfig> queryByEntity(EstDbConfig estDbConfig) {
    if (estDbConfig == null) {
      return new ArrayList<>();
    }

    return estDbConfigMapper.queryByEntity(estDbConfig);
  }

  public EstDbConfig queryById(Long id) {
    if (id == null) {
      return null;
    }
    return estDbConfigMapper.queryById(id);
  }

  public List<EstDbConfig> queryByIdList(List<Long> idList) {
    if (idList == null) {
      return new ArrayList<>();
    }
    idList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(idList)) {
      return new ArrayList<>();
    }
    return BatchUtils.applyListInBatches(idList,
            longs -> estDbConfigMapper.queryByIdList(longs));
  }

  public Map<Long, EstDbConfig> queryMapByEntity(EstDbConfig estDbConfig) {
    List<EstDbConfig> estDbConfigList = queryByEntity(estDbConfig);
    return estDbConfigList.stream().collect(Collectors.toMap(EstDbConfig::getId, a -> a));
  }

  public Map<Long, EstDbConfig> queryMapByIdList(List<Long> idList) {
    List<EstDbConfig> estDbConfigList = queryByIdList(idList);
    return estDbConfigList.stream().collect(Collectors.toMap(EstDbConfig::getId, a -> a));
  }

}
