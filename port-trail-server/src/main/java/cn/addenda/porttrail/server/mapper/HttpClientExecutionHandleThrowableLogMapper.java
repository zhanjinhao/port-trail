package cn.addenda.porttrail.server.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

import cn.addenda.porttrail.server.entity.HttpClientExecutionHandleThrowableLog;

/**
 * HttpClientExecution在handle阶段异常时记录的日志(HttpClientExecutionHandleThrowableLog)表数据库访问层
 *
 * @author addenda
 * @since 2026-06-08 18:20:15
 */
public interface HttpClientExecutionHandleThrowableLogMapper {
  /**
   * 新增数据
   */
  int insert(HttpClientExecutionHandleThrowableLog httpClientExecutionHandleThrowableLog);

  /**
   * 按ID更新数据
   */
  int updateById(HttpClientExecutionHandleThrowableLog httpClientExecutionHandleThrowableLog);

  /**
   * 按ID删除数据
   */
  int deleteById(@Param("id") Long id);

  /**
   * 按实体类删除数据
   */
  int deleteByEntity(HttpClientExecutionHandleThrowableLog httpClientExecutionHandleThrowableLog);

  /**
   * 按实体类查询数据
   */
  List<HttpClientExecutionHandleThrowableLog> queryByEntity(HttpClientExecutionHandleThrowableLog httpClientExecutionHandleThrowableLog);

  /**
   * 按ID查询数据
   */
  HttpClientExecutionHandleThrowableLog queryById(@Param("id") Long id);

  /**
   * 按ID集合查询数据
   */
  List<HttpClientExecutionHandleThrowableLog> queryByIdList(@Param("idList") List<Long> idList);

  /**
   * 按实体类计数数据
   */
  Long countByEntity(HttpClientExecutionHandleThrowableLog httpClientExecutionHandleThrowableLog);

}

