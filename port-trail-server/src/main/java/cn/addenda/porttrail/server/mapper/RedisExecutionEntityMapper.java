package cn.addenda.porttrail.server.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

import cn.addenda.porttrail.server.entity.RedisExecutionEntity;

/**
 * RedisExecution(RedisExecutionEntity)表数据库访问层
 *
 * @author addenda
 * @since 2026-06-23 22:13:26
 */
public interface RedisExecutionEntityMapper {
  /**
   * 新增数据
   */
  int insert(RedisExecutionEntity redisExecutionEntity);

  /**
   * 按ID更新数据
   */
  int updateById(RedisExecutionEntity redisExecutionEntity);

  /**
   * 按ID删除数据
   */
  int deleteById(@Param("id") Long id);

  /**
   * 按实体类删除数据
   */
  int deleteByEntity(RedisExecutionEntity redisExecutionEntity);

  /**
   * 按实体类查询数据
   */
  List<RedisExecutionEntity> queryByEntity(RedisExecutionEntity redisExecutionEntity);

  /**
   * 按ID查询数据
   */
  RedisExecutionEntity queryById(@Param("id") Long id);

  /**
   * 按ID集合查询数据
   */
  List<RedisExecutionEntity> queryByIdList(@Param("idList") List<Long> idList);

  /**
   * 按实体类计数数据
   */
  Long countByEntity(RedisExecutionEntity redisExecutionEntity);

}

