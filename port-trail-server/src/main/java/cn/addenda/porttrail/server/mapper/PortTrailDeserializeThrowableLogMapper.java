package cn.addenda.porttrail.server.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

import cn.addenda.porttrail.server.entity.PortTrailDeserializeThrowableLog;

/**
 * deserialize阶段异常时记录的日志(PortTrailDeserializeThrowableLog)表数据库访问层
 *
 * @author addenda
 * @since 2026-03-06 22:36:06
 */
public interface PortTrailDeserializeThrowableLogMapper {
  /**
   * 新增数据
   */
  int insert(PortTrailDeserializeThrowableLog portTrailDeserializeThrowableLog);

  /**
   * 按ID更新数据
   */
  int updateById(PortTrailDeserializeThrowableLog portTrailDeserializeThrowableLog);

  /**
   * 按ID删除数据
   */
  int deleteById(@Param("id") Long id);

  /**
   * 按实体类删除数据
   */
  int deleteByEntity(PortTrailDeserializeThrowableLog portTrailDeserializeThrowableLog);

  /**
   * 按实体类查询数据
   */
  List<PortTrailDeserializeThrowableLog> queryByEntity(PortTrailDeserializeThrowableLog portTrailDeserializeThrowableLog);

  /**
   * 按ID查询数据
   */
  PortTrailDeserializeThrowableLog queryById(@Param("id") Long id);

  /**
   * 按ID集合查询数据
   */
  List<PortTrailDeserializeThrowableLog> queryByIdList(@Param("idList") List<Long> idList);

  /**
   * 按实体类计数数据
   */
  Long countByEntity(PortTrailDeserializeThrowableLog portTrailDeserializeThrowableLog);

}

