package com.lframework.xingyun.shkb.mappers;

import com.lframework.starter.web.core.mapper.BaseMapper;
import com.lframework.xingyun.shkb.bo.contract.task.QueryContractTaskMaterialApplyBo;
import com.lframework.xingyun.shkb.entity.ContractTaskMaterialApply;
import com.lframework.xingyun.shkb.vo.contract.task.QueryContractTaskMaterialApplyVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author kison
* @description 针对表【shkb_contract_task_material_apply(领料申请)】的数据库操作Mapper
* @createDate 2025-06-04 17:14:59
* @Entity com.lframework.xingyun.shkb.entity.ContractTaskMaterialApply
*/
@Mapper
public interface ContractTaskMaterialApplyMapper extends BaseMapper<ContractTaskMaterialApply> {

    /**
     * 查询领料申请列表
     * 
     * @param vo 查询条件
     * @return 领料申请列表
     */
    List<QueryContractTaskMaterialApplyBo> query(@Param("vo") QueryContractTaskMaterialApplyVo vo);

    /**
     * 查询领料申请列表总数
     * 
     * @param vo 查询条件
     * @return 总数
     */
    Integer count(@Param("vo") QueryContractTaskMaterialApplyVo vo);
}
