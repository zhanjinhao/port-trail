package cn.addenda.porttrail.server.curd;

import cn.addenda.component.base.collection.BatchUtils;
import cn.addenda.component.mybatis.helper.BatchDmlHelper;
import cn.addenda.porttrail.server.mapper.HttpClientExecutionResponseEntityMapper;
import cn.addenda.porttrail.server.entity.HttpClientExecutionResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class HttpClientExecutionResponseEntityCurder {

  @Autowired
  private HttpClientExecutionResponseEntityMapper httpClientExecutionResponseEntityMapper;

  @Autowired
  private BatchDmlHelper batchDmlHelper;

  public int insert(HttpClientExecutionResponseEntity httpClientExecutionResponseEntity) {
    return httpClientExecutionResponseEntityMapper.insert(httpClientExecutionResponseEntity);
  }
  public int updateById(HttpClientExecutionResponseEntity httpClientExecutionResponseEntity) {
    return httpClientExecutionResponseEntityMapper.updateById(httpClientExecutionResponseEntity);
  }
  public int deleteById(Long id) {
    return httpClientExecutionResponseEntityMapper.deleteById(id);
  }
  public void batchInsert(List<HttpClientExecutionResponseEntity> list) {
    if (list == null) { return; }
    list.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(list)) { return; }
    batchDmlHelper.batch(HttpClientExecutionResponseEntityMapper.class, list, (mapper, e) -> { mapper.insert(e); });
  }
  public void batchUpdateById(List<HttpClientExecutionResponseEntity> list) {
    if (list == null) { return; }
    list.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(list)) { return; }
    batchDmlHelper.batch(HttpClientExecutionResponseEntityMapper.class, list, (mapper, e) -> { mapper.updateById(e); });
  }
  public void batchDeleteById(List<Long> idList) {
    if (idList == null) { return; }
    idList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(idList)) { return; }
    batchDmlHelper.batch(HttpClientExecutionResponseEntityMapper.class, idList, (mapper, id) -> { mapper.deleteById(id); });
  }
  public List<HttpClientExecutionResponseEntity> queryByEntity(HttpClientExecutionResponseEntity e) {
    if (e == null) { return new ArrayList<>(); }
    return httpClientExecutionResponseEntityMapper.queryByEntity(e);
  }
  public HttpClientExecutionResponseEntity queryById(Long id) {
    if (id == null) { return null; }
    return httpClientExecutionResponseEntityMapper.queryById(id);
  }
  public List<HttpClientExecutionResponseEntity> queryByIdList(List<Long> idList) {
    if (idList == null) { return new ArrayList<>(); }
    idList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(idList)) { return new ArrayList<>(); }
    return BatchUtils.applyListInBatches(idList, longs -> httpClientExecutionResponseEntityMapper.queryByIdList(longs));
  }
  public Map<Long, HttpClientExecutionResponseEntity> queryMapByEntity(HttpClientExecutionResponseEntity e) {
    List<HttpClientExecutionResponseEntity> list = queryByEntity(e);
    return list.stream().collect(Collectors.toMap(HttpClientExecutionResponseEntity::getId, a -> a));
  }
  public Map<Long, HttpClientExecutionResponseEntity> queryMapByIdList(List<Long> idList) {
    List<HttpClientExecutionResponseEntity> list = queryByIdList(idList);
    return list.stream().collect(Collectors.toMap(HttpClientExecutionResponseEntity::getId, a -> a));
  }

}
