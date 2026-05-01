package cn.addenda.porttrail.server.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

import cn.addenda.porttrail.server.entity.ServletExecutionResponse;

/**
 * Servlet响应(ServletExecutionResponse)表数据库访问层
 *
 * @author addenda
 * @since 2026-05-01 18:48:35
 */
public interface ServletExecutionResponseMapper {
  /**
   * 新增数据
   */
  int insert(ServletExecutionResponse servletExecutionResponse);

  /**
   * 按ID更新数据
   */
  int updateById(ServletExecutionResponse servletExecutionResponse);

  /**
   * 按ID删除数据
   */
  int deleteById(@Param("id") Long id);

  /**
   * 按实体类删除数据
   */
  int deleteByEntity(ServletExecutionResponse servletExecutionResponse);

  /**
   * 按实体类查询数据
   */
  List<ServletExecutionResponse> queryByEntity(ServletExecutionResponse servletExecutionResponse);

  /**
   * 按ID查询数据
   */
  ServletExecutionResponse queryById(@Param("id") Long id);

  /**
   * 按ID集合查询数据
   */
  List<ServletExecutionResponse> queryByIdList(@Param("idList") List<Long> idList);

  /**
   * 按实体类计数数据
   */
  Long countByEntity(ServletExecutionResponse servletExecutionResponse);

}

