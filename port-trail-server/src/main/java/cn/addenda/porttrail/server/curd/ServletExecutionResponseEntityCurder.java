package cn.addenda.porttrail.server.curd;

import cn.addenda.component.base.collection.BatchUtils;
import cn.addenda.component.mybatis.helper.BatchDmlHelper;
import cn.addenda.porttrail.server.mapper.ServletExecutionResponseEntityMapper;
import cn.addenda.porttrail.server.entity.ServletExecutionResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ServletExecutionResponseEntityCurder {

  @Autowired
  private ServletExecutionResponseEntityMapper servletExecutionResponseEntityMapper;

  @Autowired
  private BatchDmlHelper batchDmlHelper;

  public int insert(ServletExecutionResponseEntity entity) { return servletExecutionResponseEntityMapper.insert(entity); }
  public int updateById(ServletExecutionResponseEntity entity) { return servletExecutionResponseEntityMapper.updateById(entity); }
  public int deleteById(Long id) { return servletExecutionResponseEntityMapper.deleteById(id); }
  public void batchInsert(List<ServletExecutionResponseEntity> list) {
    if (list == null) { return; } list.removeIf(Objects::isNull); if (CollectionUtils.isEmpty(list)) { return; }
    batchDmlHelper.batch(ServletExecutionResponseEntityMapper.class, list, (mapper, e) -> { mapper.insert(e); });
  }
  public void batchUpdateById(List<ServletExecutionResponseEntity> list) {
    if (list == null) { return; } list.removeIf(Objects::isNull); if (CollectionUtils.isEmpty(list)) { return; }
    batchDmlHelper.batch(ServletExecutionResponseEntityMapper.class, list, (mapper, e) -> { mapper.updateById(e); });
  }
  public void batchDeleteById(List<Long> idList) {
    if (idList == null) { return; } idList.removeIf(Objects::isNull); if (CollectionUtils.isEmpty(idList)) { return; }
    batchDmlHelper.batch(ServletExecutionResponseEntityMapper.class, idList, (mapper, id) -> { mapper.deleteById(id); });
  }
  public List<ServletExecutionResponseEntity> queryByEntity(ServletExecutionResponseEntity e) {
    if (e == null) { return new ArrayList<>(); } return servletExecutionResponseEntityMapper.queryByEntity(e);
  }
  public ServletExecutionResponseEntity queryById(Long id) {
    if (id == null) { return null; } return servletExecutionResponseEntityMapper.queryById(id);
  }
  public List<ServletExecutionResponseEntity> queryByIdList(List<Long> idList) {
    if (idList == null) { return new ArrayList<>(); } idList.removeIf(Objects::isNull); if (CollectionUtils.isEmpty(idList)) { return new ArrayList<>(); }
    return BatchUtils.applyListInBatches(idList, longs -> servletExecutionResponseEntityMapper.queryByIdList(longs));
  }
  public Map<Long, ServletExecutionResponseEntity> queryMapByEntity(ServletExecutionResponseEntity e) {
    List<ServletExecutionResponseEntity> list = queryByEntity(e); return list.stream().collect(Collectors.toMap(ServletExecutionResponseEntity::getId, a -> a));
  }
  public Map<Long, ServletExecutionResponseEntity> queryMapByIdList(List<Long> idList) {
    List<ServletExecutionResponseEntity> list = queryByIdList(idList); return list.stream().collect(Collectors.toMap(ServletExecutionResponseEntity::getId, a -> a));
  }

}
