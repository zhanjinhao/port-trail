package cn.addenda.porttrail.server.mapper;

import cn.addenda.porttrail.server.entity.ServletExecutionRequestEntity;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface ServletExecutionRequestEntityMapper {
  int insert(ServletExecutionRequestEntity servletExecutionRequestEntity);
  int updateById(ServletExecutionRequestEntity servletExecutionRequestEntity);
  int deleteById(@Param("id") Long id);
  int deleteByEntity(ServletExecutionRequestEntity servletExecutionRequestEntity);
  List<ServletExecutionRequestEntity> queryByEntity(ServletExecutionRequestEntity servletExecutionRequestEntity);
  ServletExecutionRequestEntity queryById(@Param("id") Long id);
  List<ServletExecutionRequestEntity> queryByIdList(@Param("idList") List<Long> idList);
  Long countByEntity(ServletExecutionRequestEntity servletExecutionRequestEntity);
}
