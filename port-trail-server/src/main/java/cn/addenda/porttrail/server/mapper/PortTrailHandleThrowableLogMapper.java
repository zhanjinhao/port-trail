package cn.addenda.porttrail.server.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

import cn.addenda.porttrail.server.entity.PortTrailHandleThrowableLog;

/**
 * handle阶段异常时记录的日志(PortTrailHandleThrowableLog)表数据库访问层
 *
 * @author addenda
 * @since 2026-03-06 22:44:42
 */
public interface PortTrailHandleThrowableLogMapper {
  /**
   * 新增数据
   */
  int insert(PortTrailHandleThrowableLog portTrailHandleThrowableLog);

  /**
   * 按ID更新数据
   */
  int updateById(PortTrailHandleThrowableLog portTrailHandleThrowableLog);

  /**
   * 按ID删除数据
   */
  int deleteById(@Param("id") Long id);

  /**
   * 按实体类删除数据
   */
  int deleteByEntity(PortTrailHandleThrowableLog portTrailHandleThrowableLog);

  /**
   * 按实体类查询数据
   */
  List<PortTrailHandleThrowableLog> queryByEntity(PortTrailHandleThrowableLog portTrailHandleThrowableLog);

  /**
   * 按ID查询数据
   */
  PortTrailHandleThrowableLog queryById(@Param("id") Long id);

  /**
   * 按ID集合查询数据
   */
  List<PortTrailHandleThrowableLog> queryByIdList(@Param("idList") List<Long> idList);

  /**
   * 按实体类计数数据
   */
  Long countByEntity(PortTrailHandleThrowableLog portTrailHandleThrowableLog);

}

