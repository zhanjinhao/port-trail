package cn.addenda.porttrail.server.mapper;

import cn.addenda.porttrail.server.entity.EstDbConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 数据库配置(EstDbConfig)表数据库访问层
 *
 * @author addenda
 * @since 2026-02-16 19:07:07
 */
public interface EstDbConfigMapper {
  /**
   * 新增数据
   */
  int insert(EstDbConfig estDbConfig);

  /**
   * 按ID更新数据
   */
  int updateById(EstDbConfig estDbConfig);

  /**
   * 按ID删除数据
   */
  int deleteById(@Param("id") Long id);

  /**
   * 按实体类删除数据
   */
  int deleteByEntity(EstDbConfig estDbConfig);

  /**
   * 按实体类查询数据
   */
  List<EstDbConfig> queryByEntity(EstDbConfig estDbConfig);

  /**
   * 按ID查询数据
   */
  EstDbConfig queryById(@Param("id") Long id);

  /**
   * 按ID集合查询数据
   */
  List<EstDbConfig> queryByIdList(@Param("idList") List<Long> idList);

  /**
   * 按实体类计数数据
   */
  Long countByEntity(EstDbConfig estDbConfig);

}
