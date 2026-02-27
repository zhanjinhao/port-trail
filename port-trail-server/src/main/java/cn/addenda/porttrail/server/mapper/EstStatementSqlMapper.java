package cn.addenda.porttrail.server.mapper;

import cn.addenda.porttrail.server.entity.EstStatementSql;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * StatementSql(EstStatementSql)表数据库访问层
 *
 * @author addenda
 * @since 2026-02-16 19:10:07
 */
public interface EstStatementSqlMapper {
  /**
   * 新增数据
   */
  int insert(EstStatementSql estStatementSql);

  /**
   * 按ID更新数据
   */
  int updateById(EstStatementSql estStatementSql);

  /**
   * 按ID删除数据
   */
  int deleteById(@Param("id") Long id);

  /**
   * 按实体类删除数据
   */
  int deleteByEntity(EstStatementSql estStatementSql);

  /**
   * 按实体类查询数据
   */
  List<EstStatementSql> queryByEntity(EstStatementSql estStatementSql);

  /**
   * 按ID查询数据
   */
  EstStatementSql queryById(@Param("id") Long id);

  /**
   * 按ID集合查询数据
   */
  List<EstStatementSql> queryByIdList(@Param("idList") List<Long> idList);

  /**
   * 按实体类计数数据
   */
  Long countByEntity(EstStatementSql estStatementSql);

}
