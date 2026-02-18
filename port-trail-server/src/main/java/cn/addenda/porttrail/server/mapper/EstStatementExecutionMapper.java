package cn.addenda.porttrail.server.mapper;

import cn.addenda.porttrail.server.entity.EstStatementExecution;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * StatementExecution(EstStatementExecution)表数据库访问层
 *
 * @author addenda
 * @since 2026-02-16 19:09:36
 */
public interface EstStatementExecutionMapper {
  /**
   * 新增数据
   */
  int insert(EstStatementExecution estStatementExecution);

  /**
   * 按ID更新数据
   */
  int updateById(EstStatementExecution estStatementExecution);

  /**
   * 按ID删除数据
   */
  int deleteById(@Param("id") Long id);

  /**
   * 按实体类删除数据
   */
  int deleteByEntity(EstStatementExecution estStatementExecution);

  /**
   * 按实体类查询数据
   */
  List<EstStatementExecution> queryByEntity(EstStatementExecution estStatementExecution);

  /**
   * 按ID查询数据
   */
  EstStatementExecution queryById(@Param("id") Long id);

  /**
   * 按ID集合查询数据
   */
  List<EstStatementExecution> queryByIdList(@Param("idList") List<Long> idList);

  /**
   * 按实体类计数数据
   */
  Long countByEntity(EstStatementExecution estStatementExecution);

}
