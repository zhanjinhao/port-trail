package cn.addenda.porttrail.server.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

import cn.addenda.porttrail.server.entity.HttpClientExecutionResponse;

/**
 * HttpClient响应(HttpClientExecutionResponse)表数据库访问层
 *
 * @author addenda
 * @since 2026-06-07 16:03:56
 */
public interface HttpClientExecutionResponseMapper {
  /**
   * 新增数据
   */
  int insert(HttpClientExecutionResponse httpClientExecutionResponse);

  /**
   * 按ID更新数据
   */
  int updateById(HttpClientExecutionResponse httpClientExecutionResponse);

  /**
   * 按ID删除数据
   */
  int deleteById(@Param("id") Long id);

  /**
   * 按实体类删除数据
   */
  int deleteByEntity(HttpClientExecutionResponse httpClientExecutionResponse);

  /**
   * 按实体类查询数据
   */
  List<HttpClientExecutionResponse> queryByEntity(HttpClientExecutionResponse httpClientExecutionResponse);

  /**
   * 按ID查询数据
   */
  HttpClientExecutionResponse queryById(@Param("id") Long id);

  /**
   * 按ID集合查询数据
   */
  List<HttpClientExecutionResponse> queryByIdList(@Param("idList") List<Long> idList);

  /**
   * 按实体类计数数据
   */
  Long countByEntity(HttpClientExecutionResponse httpClientExecutionResponse);

}

