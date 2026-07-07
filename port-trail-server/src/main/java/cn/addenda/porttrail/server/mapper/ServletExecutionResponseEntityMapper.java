package cn.addenda.porttrail.server.mapper;

import cn.addenda.porttrail.server.entity.ServletExecutionResponseEntity;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface ServletExecutionResponseEntityMapper {
  int insert(ServletExecutionResponseEntity servletExecutionResponseEntity);
  int updateById(ServletExecutionResponseEntity servletExecutionResponseEntity);
  int deleteById(@Param("id") Long id);
  int deleteByEntity(ServletExecutionResponseEntity servletExecutionResponseEntity);
  List<ServletExecutionResponseEntity> queryByEntity(ServletExecutionResponseEntity servletExecutionResponseEntity);
  ServletExecutionResponseEntity queryById(@Param("id") Long id);
  List<ServletExecutionResponseEntity> queryByIdList(@Param("idList") List<Long> idList);
  Long countByEntity(ServletExecutionResponseEntity servletExecutionResponseEntity);
}
