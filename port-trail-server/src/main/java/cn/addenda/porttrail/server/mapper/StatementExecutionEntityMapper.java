package cn.addenda.porttrail.server.mapper;

import cn.addenda.porttrail.server.entity.StatementExecutionEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * StatementExecution(StatementExecutionEntity)表数据库访问层
 *
 * @author addenda
 * @since 2026-02-16 19:09:36
 */
public interface StatementExecutionEntityMapper {
  /**
   * 新增数据
   */
  int insert(StatementExecutionEntity statementExecutionEntity);

  /**
   * 按ID更新数据
   */
  int updateById(StatementExecutionEntity statementExecutionEntity);

  /**
   * 按ID删除数据
   */
  int deleteById(@Param("id") Long id);

  /**
   * 按实体类删除数据
   */
  int deleteByEntity(StatementExecutionEntity statementExecutionEntity);

  /**
   * 按实体类查询数据
   */
  List<StatementExecutionEntity> queryByEntity(StatementExecutionEntity statementExecutionEntity);

  /**
   * 按ID查询数据
   */
  StatementExecutionEntity queryById(@Param("id") Long id);

  /**
   * 按ID集合查询数据
   */
  List<StatementExecutionEntity> queryByIdList(@Param("idList") List<Long> idList);

  /**
   * 按实体类计数数据
   */
  Long countByEntity(StatementExecutionEntity statementExecutionEntity);

}
