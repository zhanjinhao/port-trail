package cn.addenda.porttrail.server.curd;

import cn.addenda.component.base.collection.BatchUtils;
import cn.addenda.component.mybatis.helper.BatchDmlHelper;
import cn.addenda.porttrail.server.entity.StatementSqlEntity;
import cn.addenda.porttrail.server.mapper.StatementSqlEntityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * StatementSql(StatementSqlEntity)业务层
 *
 * @author addenda
 * @since 2026-02-16 19:10:07
 */
@Component
public class StatementSqlEntityCurder {

  @Autowired
  private StatementSqlEntityMapper statementSqlEntityMapper;

  @Autowired
  private BatchDmlHelper batchDmlHelper;

  public int insert(StatementSqlEntity statementSqlEntity) {
    return statementSqlEntityMapper.insert(statementSqlEntity);
  }

  public int updateById(StatementSqlEntity statementSqlEntity) {
    return statementSqlEntityMapper.updateById(statementSqlEntity);
  }

  public int deleteById(Long id) {
    return statementSqlEntityMapper.deleteById(id);
  }

  public void batchInsert(List<StatementSqlEntity> statementSqlEntityList) {
    if (statementSqlEntityList == null) {
      return;
    }
    statementSqlEntityList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(statementSqlEntityList)) {
      return;
    }
    batchDmlHelper.batch(StatementSqlEntityMapper.class, statementSqlEntityList,
            (mapper, statementSqlEntity) -> {
              mapper.insert(statementSqlEntity);
            });
  }

  public void batchUpdateById(List<StatementSqlEntity> statementSqlEntityList) {
    if (statementSqlEntityList == null) {
      return;
    }
    statementSqlEntityList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(statementSqlEntityList)) {
      return;
    }
    batchDmlHelper.batch(StatementSqlEntityMapper.class, statementSqlEntityList,
            (mapper, statementSqlEntity) -> {
              mapper.updateById(statementSqlEntity);
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
    batchDmlHelper.batch(StatementSqlEntityMapper.class, idList,
            (mapper, id) -> {
              mapper.deleteById(id);
            });
  }

  public List<StatementSqlEntity> queryByEntity(StatementSqlEntity statementSqlEntity) {
    if (statementSqlEntity == null) {
      return new ArrayList<>();
    }

    return statementSqlEntityMapper.queryByEntity(statementSqlEntity);
  }

  public StatementSqlEntity queryById(Long id) {
    if (id == null) {
      return null;
    }
    return statementSqlEntityMapper.queryById(id);
  }

  public List<StatementSqlEntity> queryByIdList(List<Long> idList) {
    if (idList == null) {
      return new ArrayList<>();
    }
    idList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(idList)) {
      return new ArrayList<>();
    }
    return BatchUtils.applyListInBatches(idList,
            longs -> statementSqlEntityMapper.queryByIdList(longs));
  }

  public Map<Long, StatementSqlEntity> queryMapByEntity(StatementSqlEntity statementSqlEntity) {
    List<StatementSqlEntity> statementSqlEntityList = queryByEntity(statementSqlEntity);
    return statementSqlEntityList.stream().collect(Collectors.toMap(StatementSqlEntity::getId, a -> a));
  }

  public Map<Long, StatementSqlEntity> queryMapByIdList(List<Long> idList) {
    List<StatementSqlEntity> statementSqlEntityList = queryByIdList(idList);
    return statementSqlEntityList.stream().collect(Collectors.toMap(StatementSqlEntity::getId, a -> a));
  }

}
