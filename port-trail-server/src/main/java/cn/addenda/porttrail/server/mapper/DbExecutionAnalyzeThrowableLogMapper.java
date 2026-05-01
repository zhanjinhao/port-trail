package cn.addenda.porttrail.server.mapper;

import cn.addenda.porttrail.server.entity.DbExecutionAnalyzeThrowableLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * DbExecution在analyze阶段异常时记录的日志(DbExecutionAnalyzeThrowableLog)表数据库访问层
 *
 * @author addenda
 * @since 2026-03-06 22:52:40
 */
public interface DbExecutionAnalyzeThrowableLogMapper {
  /**
   * 新增数据
   */
  int insert(DbExecutionAnalyzeThrowableLog dbExecutionAnalyzeThrowableLog);

  /**
   * 按ID更新数据
   */
  int updateById(DbExecutionAnalyzeThrowableLog dbExecutionAnalyzeThrowableLog);

  /**
   * 按ID删除数据
   */
  int deleteById(@Param("id") Long id);

  /**
   * 按实体类删除数据
   */
  int deleteByEntity(DbExecutionAnalyzeThrowableLog dbExecutionAnalyzeThrowableLog);

  /**
   * 按实体类查询数据
   */
  List<DbExecutionAnalyzeThrowableLog> queryByEntity(DbExecutionAnalyzeThrowableLog dbExecutionAnalyzeThrowableLog);

  /**
   * 按ID查询数据
   */
  DbExecutionAnalyzeThrowableLog queryById(@Param("id") Long id);

  /**
   * 按ID集合查询数据
   */
  List<DbExecutionAnalyzeThrowableLog> queryByIdList(@Param("idList") List<Long> idList);

  /**
   * 按实体类计数数据
   */
  Long countByEntity(DbExecutionAnalyzeThrowableLog dbExecutionAnalyzeThrowableLog);

}

