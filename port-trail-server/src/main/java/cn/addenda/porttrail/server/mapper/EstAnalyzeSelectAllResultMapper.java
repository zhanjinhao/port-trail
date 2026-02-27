package cn.addenda.porttrail.server.mapper;

import cn.addenda.porttrail.server.entity.EstAnalyzeSelectAllResult;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 分析-select语句是否有*(EstAnalyzeSelectAllResult)表数据库访问层
 *
 * @author addenda
 * @since 2026-02-24 18:52:04
 */
public interface EstAnalyzeSelectAllResultMapper {
  /**
   * 新增数据
   */
  int insert(EstAnalyzeSelectAllResult estAnalyzeSelectAllResult);

  /**
   * 按ID更新数据
   */
  int updateById(EstAnalyzeSelectAllResult estAnalyzeSelectAllResult);

  /**
   * 按ID删除数据
   */
  int deleteById(@Param("id") Long id);

  /**
   * 按实体类删除数据
   */
  int deleteByEntity(EstAnalyzeSelectAllResult estAnalyzeSelectAllResult);

  /**
   * 按实体类查询数据
   */
  List<EstAnalyzeSelectAllResult> queryByEntity(EstAnalyzeSelectAllResult estAnalyzeSelectAllResult);

  /**
   * 按ID查询数据
   */
  EstAnalyzeSelectAllResult queryById(@Param("id") Long id);

  /**
   * 按ID集合查询数据
   */
  List<EstAnalyzeSelectAllResult> queryByIdList(@Param("idList") List<Long> idList);

  /**
   * 按实体类计数数据
   */
  Long countByEntity(EstAnalyzeSelectAllResult estAnalyzeSelectAllResult);

}
