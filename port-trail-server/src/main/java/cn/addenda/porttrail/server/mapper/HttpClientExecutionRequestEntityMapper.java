package cn.addenda.porttrail.server.mapper;

import cn.addenda.porttrail.server.entity.HttpClientExecutionRequestEntity;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface HttpClientExecutionRequestEntityMapper {
  int insert(HttpClientExecutionRequestEntity httpClientExecutionRequestEntity);
  int updateById(HttpClientExecutionRequestEntity httpClientExecutionRequestEntity);
  int deleteById(@Param("id") Long id);
  int deleteByEntity(HttpClientExecutionRequestEntity httpClientExecutionRequestEntity);
  List<HttpClientExecutionRequestEntity> queryByEntity(HttpClientExecutionRequestEntity httpClientExecutionRequestEntity);
  HttpClientExecutionRequestEntity queryById(@Param("id") Long id);
  List<HttpClientExecutionRequestEntity> queryByIdList(@Param("idList") List<Long> idList);
  Long countByEntity(HttpClientExecutionRequestEntity httpClientExecutionRequestEntity);
}
