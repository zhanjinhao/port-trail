package cn.addenda.porttrail.server.mapper;

import cn.addenda.porttrail.server.entity.PortTrailAnalyzeThrowableLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * analyze阶段异常时记录的日志(PortTrailAnalyzeThrowableLog)表数据库访问层
 *
 * @author addenda
 * @since 2026-03-06 22:52:40
 */
public interface PortTrailAnalyzeThrowableLogMapper {
  /**
   * 新增数据
   */
  int insert(PortTrailAnalyzeThrowableLog portTrailAnalyzeThrowableLog);

  /**
   * 按ID更新数据
   */
  int updateById(PortTrailAnalyzeThrowableLog portTrailAnalyzeThrowableLog);

  /**
   * 按ID删除数据
   */
  int deleteById(@Param("id") Long id);

  /**
   * 按实体类删除数据
   */
  int deleteByEntity(PortTrailAnalyzeThrowableLog portTrailAnalyzeThrowableLog);

  /**
   * 按实体类查询数据
   */
  List<PortTrailAnalyzeThrowableLog> queryByEntity(PortTrailAnalyzeThrowableLog portTrailAnalyzeThrowableLog);

  /**
   * 按ID查询数据
   */
  PortTrailAnalyzeThrowableLog queryById(@Param("id") Long id);

  /**
   * 按ID集合查询数据
   */
  List<PortTrailAnalyzeThrowableLog> queryByIdList(@Param("idList") List<Long> idList);

  /**
   * 按实体类计数数据
   */
  Long countByEntity(PortTrailAnalyzeThrowableLog portTrailAnalyzeThrowableLog);

}

