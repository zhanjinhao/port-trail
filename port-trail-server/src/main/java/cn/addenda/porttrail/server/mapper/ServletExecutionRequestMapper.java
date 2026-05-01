package cn.addenda.porttrail.server.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

import cn.addenda.porttrail.server.entity.ServletExecutionRequest;

/**
 * Servlet请求(ServletExecutionRequest)表数据库访问层
 *
 * @author addenda
 * @since 2026-05-01 15:24:43
 */
public interface ServletExecutionRequestMapper {
  /**
   * 新增数据
   */
  int insert(ServletExecutionRequest servletExecutionRequest);

  /**
   * 按ID更新数据
   */
  int updateById(ServletExecutionRequest servletExecutionRequest);

  /**
   * 按ID删除数据
   */
  int deleteById(@Param("id") Long id);

  /**
   * 按实体类删除数据
   */
  int deleteByEntity(ServletExecutionRequest servletExecutionRequest);

  /**
   * 按实体类查询数据
   */
  List<ServletExecutionRequest> queryByEntity(ServletExecutionRequest servletExecutionRequest);

  /**
   * 按ID查询数据
   */
  ServletExecutionRequest queryById(@Param("id") Long id);

  /**
   * 按ID集合查询数据
   */
  List<ServletExecutionRequest> queryByIdList(@Param("idList") List<Long> idList);

  /**
   * 按实体类计数数据
   */
  Long countByEntity(ServletExecutionRequest servletExecutionRequest);

}

