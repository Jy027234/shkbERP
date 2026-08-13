package com.lframework.xingyun.shkb.service;

import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.service.BaseMpService;
import com.lframework.xingyun.shkb.dto.material.out.MaterialOutSheetFullDto;
import com.lframework.xingyun.shkb.bo.material.out.QueryMaterialOutSheetBo;
import com.lframework.xingyun.shkb.entity.MaterialOutSheet;
import com.lframework.xingyun.shkb.bo.material.out.BatchStockBo;
import com.lframework.xingyun.shkb.bo.material.out.SerialStockBo;
import com.lframework.xingyun.shkb.vo.material.out.ApprovePassMaterialOutSheetVo;
import com.lframework.xingyun.shkb.vo.material.out.ApproveRefuseMaterialOutSheetVo;
import com.lframework.xingyun.shkb.vo.material.out.CreateMaterialOutSheetVo;
import com.lframework.xingyun.shkb.vo.material.out.QueryMaterialOutSheetVo;
import com.lframework.xingyun.shkb.vo.material.out.UpdateMaterialOutSheetVo;
import java.util.List;

/**
* @author kison
* @description 针对表【tbl_material_out_sheet(发料出库单)】的数据库操作Service
* @createDate 2025-08-10 19:06:04
*/
public interface MaterialOutSheetService extends BaseMpService<MaterialOutSheet> {

    /**
     * 查询列表
     *
     * @param pageIndex 页码
     * @param pageSize  每页显示数量
     * @param vo        查询条件
     * @return 分页结果
     */
    PageResult<MaterialOutSheet> query(Integer pageIndex, Integer pageSize, QueryMaterialOutSheetVo vo);

    /**
     * 列表DTO分页查询（带展示字段）
     */
    PageResult<QueryMaterialOutSheetBo> queryList(Integer pageIndex, Integer pageSize, QueryMaterialOutSheetVo vo);

    /**
     * 查询列表
     *
     * @param vo 查询条件
     * @return 列表结果
     */
    List<MaterialOutSheet> query(QueryMaterialOutSheetVo vo);

    /**
     * 列表DTO查询（带展示字段）
     */
    List<QueryMaterialOutSheetBo> queryList(QueryMaterialOutSheetVo vo);

    /**
     * 根据ID查询详情
     *
     * @param id ID
     * @return 详情
     */
    MaterialOutSheetFullDto getDetail(String id);

    /**
     * 创建发料出库单
     *
     * @param vo 创建参数
     * @return 发料出库单ID
     */
    String create(CreateMaterialOutSheetVo vo);

    /**
     * 修改发料出库单
     *
     * @param vo 修改参数
     */
    void update(UpdateMaterialOutSheetVo vo);

    /**
     * 审核通过
     *
     * @param vo 审核参数
     */
    void approvePass(ApprovePassMaterialOutSheetVo vo);

    /**
     * 直接审核通过
     *
     * @param vo 创建参数
     */
    void directApprovePass(CreateMaterialOutSheetVo vo);

    /**
     * 标记为可领料
     *
     * @param vo 操作参数
     */
    void markPickable(ApproveRefuseMaterialOutSheetVo vo);

    /**
     * 删除发料出库单
     *
     * @param id ID
     */
    void deleteById(String id);

    /**
     * 查询批次库存列表
     *
     * @param scId 仓库ID
     * @param productId 商品ID
     * @return 批次库存列表
     */
    List<BatchStockBo> queryBatchStock(String scId, String productId);

    /**
     * 查询序列号库存列表
     *
     * @param scId 仓库ID
     * @param productId 商品ID
     * @return 序列号库存列表
     */
    List<SerialStockBo> querySerialStock(String scId, String productId);
}
