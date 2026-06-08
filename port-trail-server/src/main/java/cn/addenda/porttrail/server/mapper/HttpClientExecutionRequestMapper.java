package cn.addenda.porttrail.server.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

import cn.addenda.porttrail.server.entity.HttpClientExecutionRequest;

/**
 * HttpClient请求(HttpClientExecutionRequest)表数据库访问层
 *
 * @author addenda
 * @since 2026-06-07 16:03:20
 */
public interface HttpClientExecutionRequestMapper {
  /**
   * 新增数据
   */
  int insert(HttpClientExecutionRequest httpClientExecutionRequest);

  /**
   * 按ID更新数据
   */
  int updateById(HttpClientExecutionRequest httpClientExecutionRequest);

  /**
   * 按ID删除数据
   */
  int deleteById(@Param("id") Long id);

  /**
   * 按实体类删除数据
   */
  int deleteByEntity(HttpClientExecutionRequest httpClientExecutionRequest);

  /**
   * 按实体类查询数据
   */
  List<HttpClientExecutionRequest> queryByEntity(HttpClientExecutionRequest httpClientExecutionRequest);

  /**
   * 按ID查询数据
   */
  HttpClientExecutionRequest queryById(@Param("id") Long id);

  /**
   * 按ID集合查询数据
   */
  List<HttpClientExecutionRequest> queryByIdList(@Param("idList") List<Long> idList);

  /**
   * 按实体类计数数据
   */
  Long countByEntity(HttpClientExecutionRequest httpClientExecutionRequest);

}

