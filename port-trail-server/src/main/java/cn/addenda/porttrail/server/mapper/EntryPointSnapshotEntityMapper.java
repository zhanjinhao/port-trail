package cn.addenda.porttrail.server.mapper;

import cn.addenda.porttrail.server.entity.EntryPointSnapshotEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * EntryPoint快照(EntryPointSnapshotEntity)表数据库访问层
 *
 * @author addenda
 * @since 2026-02-16 19:03:56
 */
public interface EntryPointSnapshotEntityMapper {
  /**
   * 新增数据
   */
  int insert(EntryPointSnapshotEntity entryPointSnapshotEntity);

  /**
   * 按ID更新数据
   */
  int updateById(EntryPointSnapshotEntity entryPointSnapshotEntity);

  /**
   * 按ID删除数据
   */
  int deleteById(@Param("id") Long id);

  /**
   * 按实体类删除数据
   */
  int deleteByEntity(EntryPointSnapshotEntity entryPointSnapshotEntity);

  /**
   * 按实体类查询数据
   */
  List<EntryPointSnapshotEntity> queryByEntity(EntryPointSnapshotEntity entryPointSnapshotEntity);

  /**
   * 按ID查询数据
   */
  EntryPointSnapshotEntity queryById(@Param("id") Long id);

  /**
   * 按ID集合查询数据
   */
  List<EntryPointSnapshotEntity> queryByIdList(@Param("idList") List<Long> idList);

  /**
   * 按实体类计数数据
   */
  Long countByEntity(EntryPointSnapshotEntity entryPointSnapshotEntity);

}
