package cn.addenda.porttrail.server.mapper;

import cn.addenda.porttrail.server.entity.EstAnalyzeExplainResult;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 分析-SQL的explain(EstAnalyzeExplainResult)表数据库访问层
 *
 * @author addenda
 * @since 2026-02-24 21:12:52
 */
public interface EstAnalyzeExplainResultMapper {
  /**
   * 新增数据
   */
  int insert(EstAnalyzeExplainResult estAnalyzeExplainResult);

  /**
   * 按ID更新数据
   */
  int updateById(EstAnalyzeExplainResult estAnalyzeExplainResult);

  /**
   * 按ID删除数据
   */
  int deleteById(@Param("id") Long id);

  /**
   * 按实体类删除数据
   */
  int deleteByEntity(EstAnalyzeExplainResult estAnalyzeExplainResult);

  /**
   * 按实体类查询数据
   */
  List<EstAnalyzeExplainResult> queryByEntity(EstAnalyzeExplainResult estAnalyzeExplainResult);

  /**
   * 按ID查询数据
   */
  EstAnalyzeExplainResult queryById(@Param("id") Long id);

  /**
   * 按ID集合查询数据
   */
  List<EstAnalyzeExplainResult> queryByIdList(@Param("idList") List<Long> idList);

  /**
   * 按实体类计数数据
   */
  Long countByEntity(EstAnalyzeExplainResult estAnalyzeExplainResult);

}
