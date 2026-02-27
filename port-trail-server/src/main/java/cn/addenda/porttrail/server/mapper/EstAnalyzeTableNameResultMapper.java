package cn.addenda.porttrail.server.mapper;

import cn.addenda.porttrail.server.entity.EstAnalyzeTableNameResult;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 分析-表名(EstAnalyzeTableNameResult)表数据库访问层
 *
 * @author addenda
 * @since 2026-02-24 16:44:43
 */
public interface EstAnalyzeTableNameResultMapper {
  /**
   * 新增数据
   */
  int insert(EstAnalyzeTableNameResult estAnalyzeTableNameResult);

  /**
   * 按ID更新数据
   */
  int updateById(EstAnalyzeTableNameResult estAnalyzeTableNameResult);

  /**
   * 按ID删除数据
   */
  int deleteById(@Param("id") Long id);

  /**
   * 按实体类删除数据
   */
  int deleteByEntity(EstAnalyzeTableNameResult estAnalyzeTableNameResult);

  /**
   * 按实体类查询数据
   */
  List<EstAnalyzeTableNameResult> queryByEntity(EstAnalyzeTableNameResult estAnalyzeTableNameResult);

  /**
   * 按ID查询数据
   */
  EstAnalyzeTableNameResult queryById(@Param("id") Long id);

  /**
   * 按ID集合查询数据
   */
  List<EstAnalyzeTableNameResult> queryByIdList(@Param("idList") List<Long> idList);

  /**
   * 按实体类计数数据
   */
  Long countByEntity(EstAnalyzeTableNameResult estAnalyzeTableNameResult);

}
