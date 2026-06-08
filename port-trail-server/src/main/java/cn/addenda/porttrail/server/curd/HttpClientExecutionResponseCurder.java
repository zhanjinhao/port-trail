package cn.addenda.porttrail.server.curd;

import java.util.*;

import cn.addenda.component.base.collection.BatchUtils;
import cn.addenda.component.mybatis.helper.BatchDmlHelper;
import cn.addenda.porttrail.server.mapper.HttpClientExecutionResponseMapper;
import cn.addenda.porttrail.server.entity.HttpClientExecutionResponse;
import org.springframework.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

import java.util.List;
import java.util.stream.Collectors;

/**
 * HttpClient响应(HttpClientExecutionResponse)业务层
 *
 * @author addenda
 * @since 2026-06-07 16:03:56
 */
@Component
public class HttpClientExecutionResponseCurder {

  @Autowired
  private HttpClientExecutionResponseMapper httpClientExecutionResponseMapper;

  @Autowired
  private BatchDmlHelper batchDmlHelper;

  public int insert(HttpClientExecutionResponse httpClientExecutionResponse) {
    return httpClientExecutionResponseMapper.insert(httpClientExecutionResponse);
  }

  public int updateById(HttpClientExecutionResponse httpClientExecutionResponse) {
    return httpClientExecutionResponseMapper.updateById(httpClientExecutionResponse);
  }

  public int deleteById(Long id) {
    return httpClientExecutionResponseMapper.deleteById(id);
  }

  public void batchInsert(List<HttpClientExecutionResponse> httpClientExecutionResponseList) {
    if (httpClientExecutionResponseList == null) {
      return;
    }
    httpClientExecutionResponseList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(httpClientExecutionResponseList)) {
      return;
    }
    batchDmlHelper.batch(HttpClientExecutionResponseMapper.class, httpClientExecutionResponseList,
            (mapper, httpClientExecutionResponse) -> {
              mapper.insert(httpClientExecutionResponse);
            });
  }

  public void batchUpdateById(List<HttpClientExecutionResponse> httpClientExecutionResponseList) {
    if (httpClientExecutionResponseList == null) {
      return;
    }
    httpClientExecutionResponseList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(httpClientExecutionResponseList)) {
      return;
    }
    batchDmlHelper.batch(HttpClientExecutionResponseMapper.class, httpClientExecutionResponseList,
            (mapper, httpClientExecutionResponse) -> {
              mapper.updateById(httpClientExecutionResponse);
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
    batchDmlHelper.batch(HttpClientExecutionResponseMapper.class, idList,
            (mapper, id) -> {
              mapper.deleteById(id);
            });
  }

  public List<HttpClientExecutionResponse> queryByEntity(HttpClientExecutionResponse httpClientExecutionResponse) {
    if (httpClientExecutionResponse == null) {
      return new ArrayList<>();
    }

    return httpClientExecutionResponseMapper.queryByEntity(httpClientExecutionResponse);
  }

  public HttpClientExecutionResponse queryById(Long id) {
    if (id == null) {
      return null;
    }
    return httpClientExecutionResponseMapper.queryById(id);
  }

  public List<HttpClientExecutionResponse> queryByIdList(List<Long> idList) {
    if (idList == null) {
      return new ArrayList<>();
    }
    idList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(idList)) {
      return new ArrayList<>();
    }
    return BatchUtils.applyListInBatches(idList,
            longs -> httpClientExecutionResponseMapper.queryByIdList(longs));
  }

  public Map<Long, HttpClientExecutionResponse> queryMapByEntity(HttpClientExecutionResponse httpClientExecutionResponse) {
    List<HttpClientExecutionResponse> httpClientExecutionResponseList = queryByEntity(httpClientExecutionResponse);
    return httpClientExecutionResponseList.stream().collect(Collectors.toMap(HttpClientExecutionResponse::getId, a -> a));
  }

  public Map<Long, HttpClientExecutionResponse> queryMapByIdList(List<Long> idList) {
    List<HttpClientExecutionResponse> httpClientExecutionResponseList = queryByIdList(idList);
    return httpClientExecutionResponseList.stream().collect(Collectors.toMap(HttpClientExecutionResponse::getId, a -> a));
  }

}

