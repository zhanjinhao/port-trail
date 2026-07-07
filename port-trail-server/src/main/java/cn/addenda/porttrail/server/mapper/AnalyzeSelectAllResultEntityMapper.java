package cn.addenda.porttrail.server.mapper;

import cn.addenda.porttrail.server.entity.AnalyzeSelectAllResultEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 分析-select语句是否有*(AnalyzeSelectAllResultEntity)表数据库访问层
 *
 * @author addenda
 * @since 2026-02-24 18:52:04
 */
public interface AnalyzeSelectAllResultEntityMapper {
  /**
   * 新增数据
   */
  int insert(AnalyzeSelectAllResultEntity analyzeSelectAllResultEntity);

  /**
   * 按ID更新数据
   */
  int updateById(AnalyzeSelectAllResultEntity analyzeSelectAllResultEntity);

  /**
   * 按ID删除数据
   */
  int deleteById(@Param("id") Long id);

  /**
   * 按实体类删除数据
   */
  int deleteByEntity(AnalyzeSelectAllResultEntity analyzeSelectAllResultEntity);

  /**
   * 按实体类查询数据
   */
  List<AnalyzeSelectAllResultEntity> queryByEntity(AnalyzeSelectAllResultEntity analyzeSelectAllResultEntity);

  /**
   * 按ID查询数据
   */
  AnalyzeSelectAllResultEntity queryById(@Param("id") Long id);

  /**
   * 按ID集合查询数据
   */
  List<AnalyzeSelectAllResultEntity> queryByIdList(@Param("idList") List<Long> idList);

  /**
   * 按实体类计数数据
   */
  Long countByEntity(AnalyzeSelectAllResultEntity analyzeSelectAllResultEntity);

}
