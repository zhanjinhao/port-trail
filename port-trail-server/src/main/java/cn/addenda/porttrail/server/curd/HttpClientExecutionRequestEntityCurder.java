package cn.addenda.porttrail.server.curd;

import cn.addenda.component.base.collection.BatchUtils;
import cn.addenda.component.mybatis.helper.BatchDmlHelper;
import cn.addenda.porttrail.server.mapper.HttpClientExecutionRequestEntityMapper;
import cn.addenda.porttrail.server.entity.HttpClientExecutionRequestEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class HttpClientExecutionRequestEntityCurder {

  @Autowired
  private HttpClientExecutionRequestEntityMapper httpClientExecutionRequestEntityMapper;

  @Autowired
  private BatchDmlHelper batchDmlHelper;

  public int insert(HttpClientExecutionRequestEntity httpClientExecutionRequestEntity) {
    return httpClientExecutionRequestEntityMapper.insert(httpClientExecutionRequestEntity);
  }

  public int updateById(HttpClientExecutionRequestEntity httpClientExecutionRequestEntity) {
    return httpClientExecutionRequestEntityMapper.updateById(httpClientExecutionRequestEntity);
  }

  public int deleteById(Long id) {
    return httpClientExecutionRequestEntityMapper.deleteById(id);
  }

  public void batchInsert(List<HttpClientExecutionRequestEntity> httpClientExecutionRequestEntityList) {
    if (httpClientExecutionRequestEntityList == null) { return; }
    httpClientExecutionRequestEntityList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(httpClientExecutionRequestEntityList)) { return; }
    batchDmlHelper.batch(HttpClientExecutionRequestEntityMapper.class, httpClientExecutionRequestEntityList,
            (mapper, httpClientExecutionRequestEntity) -> {
              mapper.insert(httpClientExecutionRequestEntity);
            });
  }

  public void batchUpdateById(List<HttpClientExecutionRequestEntity> httpClientExecutionRequestEntityList) {
    if (httpClientExecutionRequestEntityList == null) { return; }
    httpClientExecutionRequestEntityList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(httpClientExecutionRequestEntityList)) { return; }
    batchDmlHelper.batch(HttpClientExecutionRequestEntityMapper.class, httpClientExecutionRequestEntityList,
            (mapper, httpClientExecutionRequestEntity) -> {
              mapper.updateById(httpClientExecutionRequestEntity);
            });
  }

  public void batchDeleteById(List<Long> idList) {
    if (idList == null) { return; }
    idList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(idList)) { return; }
    batchDmlHelper.batch(HttpClientExecutionRequestEntityMapper.class, idList,
            (mapper, id) -> {
              mapper.deleteById(id);
            });
  }

  public List<HttpClientExecutionRequestEntity> queryByEntity(HttpClientExecutionRequestEntity httpClientExecutionRequestEntity) {
    if (httpClientExecutionRequestEntity == null) { return new ArrayList<>(); }
    return httpClientExecutionRequestEntityMapper.queryByEntity(httpClientExecutionRequestEntity);
  }

  public HttpClientExecutionRequestEntity queryById(Long id) {
    if (id == null) { return null; }
    return httpClientExecutionRequestEntityMapper.queryById(id);
  }

  public List<HttpClientExecutionRequestEntity> queryByIdList(List<Long> idList) {
    if (idList == null) { return new ArrayList<>(); }
    idList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(idList)) { return new ArrayList<>(); }
    return BatchUtils.applyListInBatches(idList, longs -> httpClientExecutionRequestEntityMapper.queryByIdList(longs));
  }

  public Map<Long, HttpClientExecutionRequestEntity> queryMapByEntity(HttpClientExecutionRequestEntity httpClientExecutionRequestEntity) {
    List<HttpClientExecutionRequestEntity> list = queryByEntity(httpClientExecutionRequestEntity);
    return list.stream().collect(Collectors.toMap(HttpClientExecutionRequestEntity::getId, a -> a));
  }

  public Map<Long, HttpClientExecutionRequestEntity> queryMapByIdList(List<Long> idList) {
    List<HttpClientExecutionRequestEntity> list = queryByIdList(idList);
    return list.stream().collect(Collectors.toMap(HttpClientExecutionRequestEntity::getId, a -> a));
  }

}
