package com.lframework.xingyun.shkb.mappers;

import com.lframework.starter.web.core.mapper.BaseMapper;
import com.lframework.xingyun.shkb.entity.MaterialOrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
* @author kison
* @description 针对表【shkb_material_order_detail(发料出库单明细)】的数据库操作Mapper
* @createDate 2025-06-06 10:07:22
* @Entity com.lframework.xingyun.shkb.entity.MaterialOrderDetail
*/
@Mapper
public interface MaterialOrderDetailMapper extends BaseMapper<MaterialOrderDetail> {

    /**
     * 按主键锁定发料单明细，并读取最新已出库数量。
     */
    MaterialOrderDetail selectByIdForUpdate(@Param("id") String id);

    /**
     * 在不超过应发数量时原子累加已出库数量。
     */
    int addOutNum(@Param("id") String id, @Param("outNum") Integer outNum);
}


