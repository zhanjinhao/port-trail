package cn.addenda.porttrail.server.mapper;

import cn.addenda.porttrail.server.entity.StatementSqlEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * StatementSql(StatementSqlEntity)表数据库访问层
 *
 * @author addenda
 * @since 2026-02-16 19:10:07
 */
public interface StatementSqlEntityMapper {
  /**
   * 新增数据
   */
  int insert(StatementSqlEntity statementSqlEntity);

  /**
   * 按ID更新数据
   */
  int updateById(StatementSqlEntity statementSqlEntity);

  /**
   * 按ID删除数据
   */
  int deleteById(@Param("id") Long id);

  /**
   * 按实体类删除数据
   */
  int deleteByEntity(StatementSqlEntity statementSqlEntity);

  /**
   * 按实体类查询数据
   */
  List<StatementSqlEntity> queryByEntity(StatementSqlEntity statementSqlEntity);

  /**
   * 按ID查询数据
   */
  StatementSqlEntity queryById(@Param("id") Long id);

  /**
   * 按ID集合查询数据
   */
  List<StatementSqlEntity> queryByIdList(@Param("idList") List<Long> idList);

  /**
   * 按实体类计数数据
   */
  Long countByEntity(StatementSqlEntity statementSqlEntity);

}
