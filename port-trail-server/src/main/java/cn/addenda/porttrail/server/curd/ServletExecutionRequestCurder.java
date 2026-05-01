package cn.addenda.porttrail.server.curd;

import java.util.*;

import cn.addenda.component.base.collection.BatchUtils;
import cn.addenda.component.mybatis.helper.BatchDmlHelper;
import cn.addenda.porttrail.server.mapper.ServletExecutionRequestMapper;
import cn.addenda.porttrail.server.entity.ServletExecutionRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servlet请求(ServletExecutionRequest)业务层
 *
 * @author addenda
 * @since 2026-05-01 15:24:44
 */
@Component
public class ServletExecutionRequestCurder {

  @Autowired
  private ServletExecutionRequestMapper servletExecutionRequestMapper;

  @Autowired
  private BatchDmlHelper batchDmlHelper;

  public int insert(ServletExecutionRequest servletExecutionRequest) {
    return servletExecutionRequestMapper.insert(servletExecutionRequest);
  }

  public int updateById(ServletExecutionRequest servletExecutionRequest) {
    return servletExecutionRequestMapper.updateById(servletExecutionRequest);
  }

  public int deleteById(Long id) {
    return servletExecutionRequestMapper.deleteById(id);
  }

  public void batchInsert(List<ServletExecutionRequest> servletExecutionRequestList) {
    if (servletExecutionRequestList == null) {
      return;
    }
    servletExecutionRequestList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(servletExecutionRequestList)) {
      return;
    }
    batchDmlHelper.batch(ServletExecutionRequestMapper.class, servletExecutionRequestList,
            (mapper, servletExecutionRequest) -> {
              mapper.insert(servletExecutionRequest);
            });
  }

  public void batchUpdateById(List<ServletExecutionRequest> servletExecutionRequestList) {
    if (servletExecutionRequestList == null) {
      return;
    }
    servletExecutionRequestList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(servletExecutionRequestList)) {
      return;
    }
    batchDmlHelper.batch(ServletExecutionRequestMapper.class, servletExecutionRequestList,
            (mapper, servletExecutionRequest) -> {
              mapper.updateById(servletExecutionRequest);
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
    batchDmlHelper.batch(ServletExecutionRequestMapper.class, idList,
            (mapper, id) -> {
              mapper.deleteById(id);
            });
  }

  public List<ServletExecutionRequest> queryByEntity(ServletExecutionRequest servletExecutionRequest) {
    if (servletExecutionRequest == null) {
      return new ArrayList<>();
    }

    return servletExecutionRequestMapper.queryByEntity(servletExecutionRequest);
  }

  public ServletExecutionRequest queryById(Long id) {
    if (id == null) {
      return null;
    }
    return servletExecutionRequestMapper.queryById(id);
  }

  public List<ServletExecutionRequest> queryByIdList(List<Long> idList) {
    if (idList == null) {
      return new ArrayList<>();
    }
    idList.removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(idList)) {
      return new ArrayList<>();
    }
    return BatchUtils.applyListInBatches(idList,
            longs -> servletExecutionRequestMapper.queryByIdList(longs));
  }

  public Map<Long, ServletExecutionRequest> queryMapByEntity(ServletExecutionRequest servletExecutionRequest) {
    List<ServletExecutionRequest> servletExecutionRequestList = queryByEntity(servletExecutionRequest);
    return servletExecutionRequestList.stream().collect(Collectors.toMap(ServletExecutionRequest::getId, a -> a));
  }

  public Map<Long, ServletExecutionRequest> queryMapByIdList(List<Long> idList) {
    List<ServletExecutionRequest> servletExecutionRequestList = queryByIdList(idList);
    return servletExecutionRequestList.stream().collect(Collectors.toMap(ServletExecutionRequest::getId, a -> a));
  }

}
