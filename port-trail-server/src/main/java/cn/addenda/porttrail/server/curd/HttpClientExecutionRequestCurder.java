package cn.addenda.porttrail.server.curd;

import java.util.*;

import cn.addenda.component.base.collection.BatchUtils;
import cn.addenda.component.mybatis.helper.BatchDmlHelper;
import cn.addenda.porttrail.server.mapper.HttpClientExecutionRequestMapper;
import cn.addenda.porttrail.server.entity.HttpClientExecutionRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

import java.util.List;
import java.util.stream.Collectors;

/**
 * HttpClient请求(HttpClientExecutionRequest)业务层
 *
 * @author addenda
 * @since 2026-06-07 16:03:20
 */
@Component
public class HttpClientExecutionRequestCurder {

  @Autowired
  private HttpClientExecutionRequestMapper httpClientExecutionRequestMapper;

  @Autowired
  private BatchDmlHelper batchDmlHelper;

  public int insert(HttpClientExecutionRequest httpClientExecutionRequest) {
    return httpClientExecutionRequestMapper.insert(httpClientExecutionRequest);
  }

  public int updateById(HttpClientExecutionRequest httpClientExecutionRequest) {
    return httpClientExecutionRequestMapper.updateById(httpClientExecutionRequest);
  }

  public int deleteById(Long id) {
    return httpClientExecutionRequestMapper.deleteById(id);
  }

  public void batchInsert(List<HttpClientExecutionRequest> httpClientExecutionRequestList) {
    if (httpClientExecutionRequestList == null) {
      return;
    }
    httpClientExecutionRequestList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(httpClientExecutionRequestList)) {
      return;
    }
    batchDmlHelper.batch(HttpClientExecutionRequestMapper.class, httpClientExecutionRequestList,
            (mapper, httpClientExecutionRequest) -> {
              mapper.insert(httpClientExecutionRequest);
            });
  }

  public void batchUpdateById(List<HttpClientExecutionRequest> httpClientExecutionRequestList) {
    if (httpClientExecutionRequestList == null) {
      return;
    }
    httpClientExecutionRequestList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(httpClientExecutionRequestList)) {
      return;
    }
    batchDmlHelper.batch(HttpClientExecutionRequestMapper.class, httpClientExecutionRequestList,
            (mapper, httpClientExecutionRequest) -> {
              mapper.updateById(httpClientExecutionRequest);
            });
  }

  public void batchDeleteById(List<Long> idList) {
    if (idList == null) {
      return;
    }
    idList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(idList)) {
      return;
    }
    batchDmlHelper.batch(HttpClientExecutionRequestMapper.class, idList,
            (mapper, id) -> {
              mapper.deleteById(id);
            });
  }

  public List<HttpClientExecutionRequest> queryByEntity(HttpClientExecutionRequest httpClientExecutionRequest) {
    if (httpClientExecutionRequest == null) {
      return new ArrayList<>();
    }

    return httpClientExecutionRequestMapper.queryByEntity(httpClientExecutionRequest);
  }

  public HttpClientExecutionRequest queryById(Long id) {
    if (id == null) {
      return null;
    }
    return httpClientExecutionRequestMapper.queryById(id);
  }

  public List<HttpClientExecutionRequest> queryByIdList(List<Long> idList) {
    if (idList == null) {
      return new ArrayList<>();
    }
    idList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(idList)) {
      return new ArrayList<>();
    }
    return BatchUtils.applyListInBatches(idList,
            longs -> httpClientExecutionRequestMapper.queryByIdList(longs));
  }

  public Map<Long, HttpClientExecutionRequest> queryMapByEntity(HttpClientExecutionRequest httpClientExecutionRequest) {
    List<HttpClientExecutionRequest> httpClientExecutionRequestList = queryByEntity(httpClientExecutionRequest);
    return httpClientExecutionRequestList.stream().collect(Collectors.toMap(HttpClientExecutionRequest::getId, a -> a));
  }

  public Map<Long, HttpClientExecutionRequest> queryMapByIdList(List<Long> idList) {
    List<HttpClientExecutionRequest> httpClientExecutionRequestList = queryByIdList(idList);
    return httpClientExecutionRequestList.stream().collect(Collectors.toMap(HttpClientExecutionRequest::getId, a -> a));
  }

}

