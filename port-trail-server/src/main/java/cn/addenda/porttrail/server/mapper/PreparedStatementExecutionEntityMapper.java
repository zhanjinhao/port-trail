package cn.addenda.porttrail.server.mapper;

import cn.addenda.porttrail.server.entity.PreparedStatementExecutionEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * PreparedStatementExecution(PreparedStatementExecutionEntity)表数据库访问层
 *
 * @author addenda
 * @since 2026-02-16 19:07:53
 */
public interface PreparedStatementExecutionEntityMapper {
  /**
   * 新增数据
   */
  int insert(PreparedStatementExecutionEntity preparedStatementExecutionEntity);

  /**
   * 按ID更新数据
   */
  int updateById(PreparedStatementExecutionEntity preparedStatementExecutionEntity);

  /**
   * 按ID删除数据
   */
  int deleteById(@Param("id") Long id);

  /**
   * 按实体类删除数据
   */
  int deleteByEntity(PreparedStatementExecutionEntity preparedStatementExecutionEntity);

  /**
   * 按实体类查询数据
   */
  List<PreparedStatementExecutionEntity> queryByEntity(PreparedStatementExecutionEntity preparedStatementExecutionEntity);

  /**
   * 按ID查询数据
   */
  PreparedStatementExecutionEntity queryById(@Param("id") Long id);

  /**
   * 按ID集合查询数据
   */
  List<PreparedStatementExecutionEntity> queryByIdList(@Param("idList") List<Long> idList);

  /**
   * 按实体类计数数据
   */
  Long countByEntity(PreparedStatementExecutionEntity preparedStatementExecutionEntity);

}
