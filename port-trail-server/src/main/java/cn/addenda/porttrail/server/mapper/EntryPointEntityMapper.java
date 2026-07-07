package cn.addenda.porttrail.server.mapper;

import cn.addenda.porttrail.server.entity.EntryPointEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * EntryPoint(EntryPointEntity)表数据库访问层
 *
 * @author addenda
 * @since 2026-02-16 18:40:58
 */
public interface EntryPointEntityMapper {
  /**
   * 新增数据
   */
  int insert(EntryPointEntity entryPointEntity);

  /**
   * 按ID更新数据
   */
  int updateById(EntryPointEntity entryPointEntity);

  /**
   * 按ID删除数据
   */
  int deleteById(@Param("id") Long id);

  /**
   * 按实体类删除数据
   */
  int deleteByEntity(EntryPointEntity entryPointEntity);

  /**
   * 按实体类查询数据
   */
  List<EntryPointEntity> queryByEntity(EntryPointEntity entryPointEntity);

  /**
   * 按ID查询数据
   */
  EntryPointEntity queryById(@Param("id") Long id);

  /**
   * 按ID集合查询数据
   */
  List<EntryPointEntity> queryByIdList(@Param("idList") List<Long> idList);

  /**
   * 按实体类计数数据
   */
  Long countByEntity(EntryPointEntity entryPointEntity);

}
