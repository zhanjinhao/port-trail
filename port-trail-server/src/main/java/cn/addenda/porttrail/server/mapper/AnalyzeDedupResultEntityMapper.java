package cn.addenda.porttrail.server.mapper;

import cn.addenda.porttrail.server.entity.AnalyzeDedupResultEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 分析-问题SQL去重记录(AnalyzeDedupResultEntity)表数据库访问层
 *
 * @author addenda
 */
public interface AnalyzeDedupResultEntityMapper {
  /**
   * 新增数据
   */
  int insert(AnalyzeDedupResultEntity analyzeDedupResultEntity);

  /**
   * 按ID更新数据
   */
  int updateById(AnalyzeDedupResultEntity analyzeDedupResultEntity);

  /**
   * 按去重键幂等合并：flag 只增不减，证据列保留第一次的值，hit_rules 由合并后的 flag 重算。
   *
   * @return 受影响行数；行不存在时为 0
   */
  int mergeByDedupKey(AnalyzeDedupResultEntity analyzeDedupResultEntity);

  /**
   * 按ID删除数据
   */
  int deleteById(@Param("id") Long id);

  /**
   * 按实体类删除数据
   */
  int deleteByEntity(AnalyzeDedupResultEntity analyzeDedupResultEntity);

  /**
   * 按实体类查询数据
   */
  List<AnalyzeDedupResultEntity> queryByEntity(AnalyzeDedupResultEntity analyzeDedupResultEntity);

  /**
   * 按ID查询数据
   */
  AnalyzeDedupResultEntity queryById(@Param("id") Long id);

  /**
   * 按去重键只查询 hit_rules（走唯一索引 uk_dedup_key）；行不存在或字段为空时返回 null
   */
  String queryHitRulesByDedupKey(@Param("dedupKey") String dedupKey);

  /**
   * 按ID集合查询数据
   */
  List<AnalyzeDedupResultEntity> queryByIdList(@Param("idList") List<Long> idList);

  /**
   * 按实体类计数数据
   */
  Long countByEntity(AnalyzeDedupResultEntity analyzeDedupResultEntity);

}
