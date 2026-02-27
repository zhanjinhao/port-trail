package cn.addenda.porttrail.server.mapper;

import cn.addenda.porttrail.server.entity.EstEntryPoint;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * EntryPoint(EstEntryPoint)表数据库访问层
 *
 * @author addenda
 * @since 2026-02-16 18:40:58
 */
public interface EstEntryPointMapper {
  /**
   * 新增数据
   */
  int insert(EstEntryPoint estEntryPoint);

  /**
   * 按ID更新数据
   */
  int updateById(EstEntryPoint estEntryPoint);

  /**
   * 按ID删除数据
   */
  int deleteById(@Param("id") Long id);

  /**
   * 按实体类删除数据
   */
  int deleteByEntity(EstEntryPoint estEntryPoint);

  /**
   * 按实体类查询数据
   */
  List<EstEntryPoint> queryByEntity(EstEntryPoint estEntryPoint);

  /**
   * 按ID查询数据
   */
  EstEntryPoint queryById(@Param("id") Long id);

  /**
   * 按ID集合查询数据
   */
  List<EstEntryPoint> queryByIdList(@Param("idList") List<Long> idList);

  /**
   * 按实体类计数数据
   */
  Long countByEntity(EstEntryPoint estEntryPoint);

}
