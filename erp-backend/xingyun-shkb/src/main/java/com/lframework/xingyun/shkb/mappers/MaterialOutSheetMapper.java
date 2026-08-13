package com.lframework.xingyun.shkb.mappers;

import com.lframework.starter.web.core.mapper.BaseMapper;
import com.lframework.xingyun.shkb.dto.material.out.MaterialOutSheetFullDto;
import com.lframework.xingyun.shkb.bo.material.out.QueryMaterialOutSheetBo;
import com.lframework.xingyun.shkb.entity.MaterialOutSheet;
import com.lframework.xingyun.shkb.bo.material.out.BatchStockBo;
import com.lframework.xingyun.shkb.bo.material.out.SerialStockBo;
import com.lframework.xingyun.shkb.vo.material.out.QueryMaterialOutSheetVo;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
* @author kison
* @description 针对表【tbl_material_out_sheet(发料出库单)】的数据库操作Mapper
* @createDate 2025-08-10 19:06:04
* @Entity com.lframework.xingyun.shkb.entity.MaterialOutSheet
*/
@Mapper
public interface MaterialOutSheetMapper extends BaseMapper<MaterialOutSheet> {

    /**
     * 锁定出库单，串行化同一单据的状态变更。
     */
    MaterialOutSheet selectByIdForUpdate(@Param("id") String id);

    /**
     * 查询列表
     *
     * @param vo 查询条件
     * @return 列表结果
     */
    List<MaterialOutSheet> query(@Param("vo") QueryMaterialOutSheetVo vo);

    /**
     * 列表查询（直接映射到BO，带展示字段）
     */
    List<QueryMaterialOutSheetBo> queryList(@Param("vo") QueryMaterialOutSheetVo vo);

    /**
     * 根据ID查询详情
     *
     * @param id ID
     * @return 详情
     */
    MaterialOutSheetFullDto getDetail(@Param("id") String id);

    /**
     * 查询批次库存（带商品、件号、机型、仓库信息）
     */
    List<BatchStockBo> queryBatchStock(@Param("scId") String scId, @Param("productId") String productId);

    /**
     * 查询序列号库存（带批次号、仓库、航材名称、件号、机型、在库状态）
     */
    List<SerialStockBo> querySerialStock(@Param("scId") String scId, @Param("productId") String productId);

    /**
     * 根据ID查询领料申请单
     */
    com.lframework.xingyun.shkb.entity.ContractTaskMaterialApply getContractTaskMaterialApplyById(@Param("id") String id);

    /**
     * 根据ID查询合同任务
     */
    com.lframework.xingyun.shkb.entity.ContractTask getContractTaskById(@Param("id") String id);

    /**
     * 更新合同任务航材状态
     */
    void updateContractTask(@Param("taskId") String taskId, @Param("materialStatus") String materialStatus);

    /**
     * 根据合同任务ID查询所有发料申请单
     */
    List<com.lframework.xingyun.shkb.entity.ContractTaskMaterialApply> getContractTaskMaterialAppliesByTaskId(@Param("taskId") String taskId);

    /**
     * 根据发料申请单ID查询对应的发料单
     */
    List<com.lframework.xingyun.shkb.entity.MaterialOrder> getMaterialOrdersByMaterialApplyId(@Param("materialApplyId") String materialApplyId);
}



