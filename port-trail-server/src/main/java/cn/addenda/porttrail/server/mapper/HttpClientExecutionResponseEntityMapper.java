package cn.addenda.porttrail.server.mapper;

import cn.addenda.porttrail.server.entity.HttpClientExecutionResponseEntity;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface HttpClientExecutionResponseEntityMapper {
  int insert(HttpClientExecutionResponseEntity httpClientExecutionResponseEntity);
  int updateById(HttpClientExecutionResponseEntity httpClientExecutionResponseEntity);
  int deleteById(@Param("id") Long id);
  int deleteByEntity(HttpClientExecutionResponseEntity httpClientExecutionResponseEntity);
  List<HttpClientExecutionResponseEntity> queryByEntity(HttpClientExecutionResponseEntity httpClientExecutionResponseEntity);
  HttpClientExecutionResponseEntity queryById(@Param("id") Long id);
  List<HttpClientExecutionResponseEntity> queryByIdList(@Param("idList") List<Long> idList);
  Long countByEntity(HttpClientExecutionResponseEntity httpClientExecutionResponseEntity);
}
