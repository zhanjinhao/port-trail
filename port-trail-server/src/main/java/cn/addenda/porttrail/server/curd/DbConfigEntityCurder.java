package cn.addenda.porttrail.server.curd;

import cn.addenda.component.base.collection.BatchUtils;
import cn.addenda.component.mybatis.helper.BatchDmlHelper;
import cn.addenda.porttrail.server.entity.DbConfigEntity;
import cn.addenda.porttrail.server.mapper.DbConfigEntityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 数据库配置(DbConfigEntity)业务层
 *
 * @author addenda
 * @since 2026-02-16 19:07:07
 */
@Component
public class DbConfigEntityCurder {

  @Autowired
  private DbConfigEntityMapper dbConfigEntityMapper;

  @Autowired
  private BatchDmlHelper batchDmlHelper;

  public int insert(DbConfigEntity dbConfigEntity) {
    return dbConfigEntityMapper.insert(dbConfigEntity);
  }

  public int updateById(DbConfigEntity dbConfigEntity) {
    return dbConfigEntityMapper.updateById(dbConfigEntity);
  }

  public int deleteById(Long id) {
    return dbConfigEntityMapper.deleteById(id);
  }

  public void batchInsert(List<DbConfigEntity> dbConfigEntityList) {
    if (dbConfigEntityList == null) {
      return;
    }
    dbConfigEntityList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(dbConfigEntityList)) {
      return;
    }
    batchDmlHelper.batch(DbConfigEntityMapper.class, dbConfigEntityList,
            (mapper, dbConfigEntity) -> {
              mapper.insert(dbConfigEntity);
            });
  }

  public void batchUpdateById(List<DbConfigEntity> dbConfigEntityList) {
    if (dbConfigEntityList == null) {
      return;
    }
    dbConfigEntityList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(dbConfigEntityList)) {
      return;
    }
    batchDmlHelper.batch(DbConfigEntityMapper.class, dbConfigEntityList,
            (mapper, dbConfigEntity) -> {
              mapper.updateById(dbConfigEntity);
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
    batchDmlHelper.batch(DbConfigEntityMapper.class, idList,
            (mapper, id) -> {
              mapper.deleteById(id);
            });
  }

  public List<DbConfigEntity> queryByEntity(DbConfigEntity dbConfigEntity) {
    if (dbConfigEntity == null) {
      return new ArrayList<>();
    }

    return dbConfigEntityMapper.queryByEntity(dbConfigEntity);
  }

  public DbConfigEntity queryById(Long id) {
    if (id == null) {
      return null;
    }
    return dbConfigEntityMapper.queryById(id);
  }

  public List<DbConfigEntity> queryByIdList(List<Long> idList) {
    if (idList == null) {
      return new ArrayList<>();
    }
    idList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(idList)) {
      return new ArrayList<>();
    }
    return BatchUtils.applyListInBatches(idList,
            longs -> dbConfigEntityMapper.queryByIdList(longs));
  }

  public Map<Long, DbConfigEntity> queryMapByEntity(DbConfigEntity dbConfigEntity) {
    List<DbConfigEntity> dbConfigEntityList = queryByEntity(dbConfigEntity);
    return dbConfigEntityList.stream().collect(Collectors.toMap(DbConfigEntity::getId, a -> a));
  }

  public Map<Long, DbConfigEntity> queryMapByIdList(List<Long> idList) {
    List<DbConfigEntity> dbConfigEntityList = queryByIdList(idList);
    return dbConfigEntityList.stream().collect(Collectors.toMap(DbConfigEntity::getId, a -> a));
  }

}
