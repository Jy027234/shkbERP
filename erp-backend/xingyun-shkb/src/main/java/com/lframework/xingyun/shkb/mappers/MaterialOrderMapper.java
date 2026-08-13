package com.lframework.xingyun.shkb.mappers;

import com.lframework.starter.web.core.mapper.BaseMapper;
import com.lframework.xingyun.shkb.bo.material.QueryMaterialOrderBo;
import com.lframework.xingyun.shkb.dto.material.MaterialOrderFullDto;
import com.lframework.xingyun.shkb.entity.MaterialOrder;
import com.lframework.xingyun.shkb.vo.material.QueryMaterialOrderVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
* @author kison
* @description 针对表【shkb_material_order(发料出库单)】的数据库操作Mapper
* @createDate 2025-06-06 10:07:22
* @Entity com.lframework.xingyun.shkb.entity.MaterialOrder
*/
@Mapper
public interface MaterialOrderMapper extends BaseMapper<MaterialOrder> {

    /**
     * 锁定发料单，避免多张出库单并发覆盖汇总状态。
     */
    MaterialOrder selectByIdForUpdate(@Param("id") String id);

    /**
     * 查询发料单列表
     *
     * @param vo 查询条件
     * @return 发料单列表
     */
    List<QueryMaterialOrderBo> query(@Param("vo") QueryMaterialOrderVo vo);

    /**
     * 根据ID查询详情
     *
     * @param id ID
     * @return 详情
     */
    MaterialOrderFullDto getDetail(@Param("id") String id);
}



