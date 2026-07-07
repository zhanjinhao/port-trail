package cn.addenda.porttrail.server.mapper;

import cn.addenda.porttrail.server.entity.DbConfigEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 数据库配置(DbConfigEntity)表数据库访问层
 *
 * @author addenda
 * @since 2026-02-16 19:07:07
 */
public interface DbConfigEntityMapper {
  /**
   * 新增数据
   */
  int insert(DbConfigEntity dbConfigEntity);

  /**
   * 按ID更新数据
   */
  int updateById(DbConfigEntity dbConfigEntity);

  /**
   * 按ID删除数据
   */
  int deleteById(@Param("id") Long id);

  /**
   * 按实体类删除数据
   */
  int deleteByEntity(DbConfigEntity dbConfigEntity);

  /**
   * 按实体类查询数据
   */
  List<DbConfigEntity> queryByEntity(DbConfigEntity dbConfigEntity);

  /**
   * 按ID查询数据
   */
  DbConfigEntity queryById(@Param("id") Long id);

  /**
   * 按ID集合查询数据
   */
  List<DbConfigEntity> queryByIdList(@Param("idList") List<Long> idList);

  /**
   * 按实体类计数数据
   */
  Long countByEntity(DbConfigEntity dbConfigEntity);

}
