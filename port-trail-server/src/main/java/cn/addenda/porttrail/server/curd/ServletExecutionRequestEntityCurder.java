package cn.addenda.porttrail.server.curd;

import cn.addenda.component.base.collection.BatchUtils;
import cn.addenda.component.mybatis.helper.BatchDmlHelper;
import cn.addenda.porttrail.server.mapper.ServletExecutionRequestEntityMapper;
import cn.addenda.porttrail.server.entity.ServletExecutionRequestEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ServletExecutionRequestEntityCurder {

  @Autowired
  private ServletExecutionRequestEntityMapper servletExecutionRequestEntityMapper;

  @Autowired
  private BatchDmlHelper batchDmlHelper;

  public int insert(ServletExecutionRequestEntity entity) { return servletExecutionRequestEntityMapper.insert(entity); }
  public int updateById(ServletExecutionRequestEntity entity) { return servletExecutionRequestEntityMapper.updateById(entity); }
  public int deleteById(Long id) { return servletExecutionRequestEntityMapper.deleteById(id); }
  public void batchInsert(List<ServletExecutionRequestEntity> list) {
    if (list == null) { return; }
    list.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(list)) { return; }
    batchDmlHelper.batch(ServletExecutionRequestEntityMapper.class, list, (mapper, e) -> { mapper.insert(e); });
  }
  public void batchUpdateById(List<ServletExecutionRequestEntity> list) {
    if (list == null) { return; }
    list.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(list)) { return; }
    batchDmlHelper.batch(ServletExecutionRequestEntityMapper.class, list, (mapper, e) -> { mapper.updateById(e); });
  }
  public void batchDeleteById(List<Long> idList) {
    if (idList == null) { return; }
    idList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(idList)) { return; }
    batchDmlHelper.batch(ServletExecutionRequestEntityMapper.class, idList, (mapper, id) -> { mapper.deleteById(id); });
  }
  public List<ServletExecutionRequestEntity> queryByEntity(ServletExecutionRequestEntity e) {
    if (e == null) { return new ArrayList<>(); }
    return servletExecutionRequestEntityMapper.queryByEntity(e);
  }
  public ServletExecutionRequestEntity queryById(Long id) {
    if (id == null) { return null; }
    return servletExecutionRequestEntityMapper.queryById(id);
  }
  public List<ServletExecutionRequestEntity> queryByIdList(List<Long> idList) {
    if (idList == null) { return new ArrayList<>(); }
    idList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(idList)) { return new ArrayList<>(); }
    return BatchUtils.applyListInBatches(idList, longs -> servletExecutionRequestEntityMapper.queryByIdList(longs));
  }
  public Map<Long, ServletExecutionRequestEntity> queryMapByEntity(ServletExecutionRequestEntity e) {
    List<ServletExecutionRequestEntity> list = queryByEntity(e);
    return list.stream().collect(Collectors.toMap(ServletExecutionRequestEntity::getId, a -> a));
  }
  public Map<Long, ServletExecutionRequestEntity> queryMapByIdList(List<Long> idList) {
    List<ServletExecutionRequestEntity> list = queryByIdList(idList);
    return list.stream().collect(Collectors.toMap(ServletExecutionRequestEntity::getId, a -> a));
  }

}
