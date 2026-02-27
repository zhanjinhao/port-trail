package cn.addenda.porttrail.server.mapper;

import cn.addenda.porttrail.server.entity.EstPreparedStatementParameter;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * PreparedStatementParameter(EstPreparedStatementParameter)表数据库访问层
 *
 * @author addenda
 * @since 2026-02-16 19:08:23
 */
public interface EstPreparedStatementParameterMapper {
  /**
   * 新增数据
   */
  int insert(EstPreparedStatementParameter estPreparedStatementParameter);

  /**
   * 按ID更新数据
   */
  int updateById(EstPreparedStatementParameter estPreparedStatementParameter);

  /**
   * 按ID删除数据
   */
  int deleteById(@Param("id") Long id);

  /**
   * 按实体类删除数据
   */
  int deleteByEntity(EstPreparedStatementParameter estPreparedStatementParameter);

  /**
   * 按实体类查询数据
   */
  List<EstPreparedStatementParameter> queryByEntity(EstPreparedStatementParameter estPreparedStatementParameter);

  /**
   * 按ID查询数据
   */
  EstPreparedStatementParameter queryById(@Param("id") Long id);

  /**
   * 按ID集合查询数据
   */
  List<EstPreparedStatementParameter> queryByIdList(@Param("idList") List<Long> idList);

  /**
   * 按实体类计数数据
   */
  Long countByEntity(EstPreparedStatementParameter estPreparedStatementParameter);

}
