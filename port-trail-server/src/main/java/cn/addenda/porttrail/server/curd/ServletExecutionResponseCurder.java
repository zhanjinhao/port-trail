package cn.addenda.porttrail.server.curd;

import java.util.*;

import cn.addenda.component.base.collection.BatchUtils;
import cn.addenda.component.mybatis.helper.BatchDmlHelper;
import cn.addenda.porttrail.server.mapper.ServletExecutionResponseMapper;
import cn.addenda.porttrail.server.entity.ServletExecutionResponse;
import org.springframework.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servlet响应(ServletExecutionResponse)业务层
 *
 * @author addenda
 * @since 2026-05-01 18:48:35
 */
@Component
public class ServletExecutionResponseCurder {

  @Autowired
  private ServletExecutionResponseMapper servletExecutionResponseMapper;

  @Autowired
  private BatchDmlHelper batchDmlHelper;

  public int insert(ServletExecutionResponse servletExecutionResponse) {
    return servletExecutionResponseMapper.insert(servletExecutionResponse);
  }

  public int updateById(ServletExecutionResponse servletExecutionResponse) {
    return servletExecutionResponseMapper.updateById(servletExecutionResponse);
  }

  public int deleteById(Long id) {
    return servletExecutionResponseMapper.deleteById(id);
  }

  public void batchInsert(List<ServletExecutionResponse> servletExecutionResponseList) {
    if (servletExecutionResponseList == null) {
      return;
    }
    servletExecutionResponseList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(servletExecutionResponseList)) {
      return;
    }
    batchDmlHelper.batch(ServletExecutionResponseMapper.class, servletExecutionResponseList,
            (mapper, servletExecutionResponse) -> {
              mapper.insert(servletExecutionResponse);
            });
  }

  public void batchUpdateById(List<ServletExecutionResponse> servletExecutionResponseList) {
    if (servletExecutionResponseList == null) {
      return;
    }
    servletExecutionResponseList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(servletExecutionResponseList)) {
      return;
    }
    batchDmlHelper.batch(ServletExecutionResponseMapper.class, servletExecutionResponseList,
            (mapper, servletExecutionResponse) -> {
              mapper.updateById(servletExecutionResponse);
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
    batchDmlHelper.batch(ServletExecutionResponseMapper.class, idList,
            (mapper, id) -> {
              mapper.deleteById(id);
            });
  }

  public List<ServletExecutionResponse> queryByEntity(ServletExecutionResponse servletExecutionResponse) {
    if (servletExecutionResponse == null) {
      return new ArrayList<>();
    }

    return servletExecutionResponseMapper.queryByEntity(servletExecutionResponse);
  }

  public ServletExecutionResponse queryById(Long id) {
    if (id == null) {
      return null;
    }
    return servletExecutionResponseMapper.queryById(id);
  }

  public List<ServletExecutionResponse> queryByIdList(List<Long> idList) {
    if (idList == null) {
      return new ArrayList<>();
    }
    idList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(idList)) {
      return new ArrayList<>();
    }
    return BatchUtils.applyListInBatches(idList,
            longs -> servletExecutionResponseMapper.queryByIdList(longs));
  }

  public Map<Long, ServletExecutionResponse> queryMapByEntity(ServletExecutionResponse servletExecutionResponse) {
    List<ServletExecutionResponse> servletExecutionResponseList = queryByEntity(servletExecutionResponse);
    return servletExecutionResponseList.stream().collect(Collectors.toMap(ServletExecutionResponse::getId, a -> a));
  }

  public Map<Long, ServletExecutionResponse> queryMapByIdList(List<Long> idList) {
    List<ServletExecutionResponse> servletExecutionResponseList = queryByIdList(idList);
    return servletExecutionResponseList.stream().collect(Collectors.toMap(ServletExecutionResponse::getId, a -> a));
  }

}

