package cn.addenda.porttrail.server.curd;

import cn.addenda.component.base.collection.BatchUtils;
import cn.addenda.component.mybatis.helper.BatchDmlHelper;
import cn.addenda.porttrail.server.entity.EstStatementSql;
import cn.addenda.porttrail.server.mapper.EstStatementSqlMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * StatementSql(EstStatementSql)业务层
 *
 * @author addenda
 * @since 2026-02-16 19:10:07
 */
@Component
public class EstStatementSqlCurder {

  @Autowired
  private EstStatementSqlMapper estStatementSqlMapper;

  @Autowired
  private BatchDmlHelper batchDmlHelper;

  public int insert(EstStatementSql estStatementSql) {
    return estStatementSqlMapper.insert(estStatementSql);
  }

  public int updateById(EstStatementSql estStatementSql) {
    return estStatementSqlMapper.updateById(estStatementSql);
  }

  public int deleteById(Long id) {
    return estStatementSqlMapper.deleteById(id);
  }

  public void batchInsert(List<EstStatementSql> estStatementSqlList) {
    if (estStatementSqlList == null) {
      return;
    }
    estStatementSqlList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(estStatementSqlList)) {
      return;
    }
    batchDmlHelper.batch(EstStatementSqlMapper.class, estStatementSqlList,
            (mapper, estStatementSql) -> {
              mapper.insert(estStatementSql);
            });
  }

  public void batchUpdateById(List<EstStatementSql> estStatementSqlList) {
    if (estStatementSqlList == null) {
      return;
    }
    estStatementSqlList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(estStatementSqlList)) {
      return;
    }
    batchDmlHelper.batch(EstStatementSqlMapper.class, estStatementSqlList,
            (mapper, estStatementSql) -> {
              mapper.updateById(estStatementSql);
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
    batchDmlHelper.batch(EstStatementSqlMapper.class, idList,
            (mapper, id) -> {
              mapper.deleteById(id);
            });
  }

  public List<EstStatementSql> queryByEntity(EstStatementSql estStatementSql) {
    if (estStatementSql == null) {
      return new ArrayList<>();
    }

    return estStatementSqlMapper.queryByEntity(estStatementSql);
  }

  public EstStatementSql queryById(Long id) {
    if (id == null) {
      return null;
    }
    return estStatementSqlMapper.queryById(id);
  }

  public List<EstStatementSql> queryByIdList(List<Long> idList) {
    if (idList == null) {
      return new ArrayList<>();
    }
    idList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(idList)) {
      return new ArrayList<>();
    }
    return BatchUtils.applyListInBatches(idList,
            longs -> estStatementSqlMapper.queryByIdList(longs));
  }

  public Map<Long, EstStatementSql> queryMapByEntity(EstStatementSql estStatementSql) {
    List<EstStatementSql> estStatementSqlList = queryByEntity(estStatementSql);
    return estStatementSqlList.stream().collect(Collectors.toMap(EstStatementSql::getId, a -> a));
  }

  public Map<Long, EstStatementSql> queryMapByIdList(List<Long> idList) {
    List<EstStatementSql> estStatementSqlList = queryByIdList(idList);
    return estStatementSqlList.stream().collect(Collectors.toMap(EstStatementSql::getId, a -> a));
  }

}
