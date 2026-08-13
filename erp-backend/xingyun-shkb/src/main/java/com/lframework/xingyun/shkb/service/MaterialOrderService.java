package com.lframework.xingyun.shkb.service;

import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.service.BaseMpService;
import com.lframework.xingyun.shkb.bo.material.QueryMaterialOrderBo;
import com.lframework.xingyun.shkb.dto.material.MaterialOrderFullDto;
import com.lframework.xingyun.shkb.entity.MaterialOrder;
import com.lframework.xingyun.shkb.vo.material.CreateMaterialOrderFromApplyVo;
import com.lframework.xingyun.shkb.vo.material.QueryMaterialOrderVo;
import java.util.List;

/**
* @author kison
* @description 针对表【shkb_material_order(发料出库单)】的数据库操作Service
* @createDate 2025-06-06 10:07:22
*/
public interface MaterialOrderService extends BaseMpService<MaterialOrder> {

    /**
     * 查询列表
     *
     * @param pageIndex 页码
     * @param pageSize  每页显示数量
     * @param vo        查询条件
     * @return 分页结果
     */
    PageResult<QueryMaterialOrderBo> query(Integer pageIndex, Integer pageSize, QueryMaterialOrderVo vo);

    /**
     * 查询列表
     *
     * @param vo 查询条件
     * @return 列表
     */
    List<QueryMaterialOrderBo> query(QueryMaterialOrderVo vo);

    /**
     * 根据ID查询详情
     *
     * @param id ID
     * @return 详情
     */
    MaterialOrderFullDto getDetail(String id);

    /**
     * 创建发料单
     *
     * @param vo 创建参数
     * 基于发料申请单创建发料单
     */
    String createFromApply(CreateMaterialOrderFromApplyVo vo);
}
