package cn.addenda.porttrail.server.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

import cn.addenda.porttrail.server.entity.ServletExecutionHandleThrowableLog;

/**
 * ServletExecution在handle阶段异常时记录的日志(ServletExecutionHandleThrowableLog)表数据库访问层
 *
 * @author addenda
 * @since 2026-05-01 16:40:10
 */
public interface ServletExecutionHandleThrowableLogMapper {
  /**
   * 新增数据
   */
  int insert(ServletExecutionHandleThrowableLog servletExecutionHandleThrowableLog);

  /**
   * 按ID更新数据
   */
  int updateById(ServletExecutionHandleThrowableLog servletExecutionHandleThrowableLog);

  /**
   * 按ID删除数据
   */
  int deleteById(@Param("id") Long id);

  /**
   * 按实体类删除数据
   */
  int deleteByEntity(ServletExecutionHandleThrowableLog servletExecutionHandleThrowableLog);

  /**
   * 按实体类查询数据
   */
  List<ServletExecutionHandleThrowableLog> queryByEntity(ServletExecutionHandleThrowableLog servletExecutionHandleThrowableLog);

  /**
   * 按ID查询数据
   */
  ServletExecutionHandleThrowableLog queryById(@Param("id") Long id);

  /**
   * 按ID集合查询数据
   */
  List<ServletExecutionHandleThrowableLog> queryByIdList(@Param("idList") List<Long> idList);

  /**
   * 按实体类计数数据
   */
  Long countByEntity(ServletExecutionHandleThrowableLog servletExecutionHandleThrowableLog);

}

