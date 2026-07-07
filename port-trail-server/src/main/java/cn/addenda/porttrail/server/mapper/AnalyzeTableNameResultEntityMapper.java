package cn.addenda.porttrail.server.mapper;

import cn.addenda.porttrail.server.entity.AnalyzeTableNameResultEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 分析-表名(AnalyzeTableNameResultEntity)表数据库访问层
 *
 * @author addenda
 * @since 2026-02-24 16:44:43
 */
public interface AnalyzeTableNameResultEntityMapper {
  int insert(AnalyzeTableNameResultEntity analyzeTableNameResultEntity);
  int updateById(AnalyzeTableNameResultEntity analyzeTableNameResultEntity);
  int deleteById(@Param("id") Long id);
  int deleteByEntity(AnalyzeTableNameResultEntity analyzeTableNameResultEntity);
  List<AnalyzeTableNameResultEntity> queryByEntity(AnalyzeTableNameResultEntity analyzeTableNameResultEntity);
  AnalyzeTableNameResultEntity queryById(@Param("id") Long id);
  List<AnalyzeTableNameResultEntity> queryByIdList(@Param("idList") List<Long> idList);
  Long countByEntity(AnalyzeTableNameResultEntity analyzeTableNameResultEntity);
}
