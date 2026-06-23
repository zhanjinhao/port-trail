package cn.addenda.porttrail.server.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

import cn.addenda.porttrail.server.entity.RedisExecutionHandleThrowableLog;

/**
 * RedisExecution在handle阶段异常时记录的日志(RedisExecutionHandleThrowableLog)表数据库访问层
 *
 * @author addenda
 * @since 2026-06-23 21:57:19
 */
public interface RedisExecutionHandleThrowableLogMapper {
  /**
   * 新增数据
   */
  int insert(RedisExecutionHandleThrowableLog redisExecutionHandleThrowableLog);

  /**
   * 按ID更新数据
   */
  int updateById(RedisExecutionHandleThrowableLog redisExecutionHandleThrowableLog);

  /**
   * 按ID删除数据
   */
  int deleteById(@Param("id") Long id);

  /**
   * 按实体类删除数据
   */
  int deleteByEntity(RedisExecutionHandleThrowableLog redisExecutionHandleThrowableLog);

  /**
   * 按实体类查询数据
   */
  List<RedisExecutionHandleThrowableLog> queryByEntity(RedisExecutionHandleThrowableLog redisExecutionHandleThrowableLog);

  /**
   * 按ID查询数据
   */
  RedisExecutionHandleThrowableLog queryById(@Param("id") Long id);

  /**
   * 按ID集合查询数据
   */
  List<RedisExecutionHandleThrowableLog> queryByIdList(@Param("idList") List<Long> idList);

  /**
   * 按实体类计数数据
   */
  Long countByEntity(RedisExecutionHandleThrowableLog redisExecutionHandleThrowableLog);

}

