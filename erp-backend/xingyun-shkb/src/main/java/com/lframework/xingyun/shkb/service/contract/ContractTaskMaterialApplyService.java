package com.lframework.xingyun.shkb.service.contract;

import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.service.BaseMpService;
import com.lframework.xingyun.shkb.bo.contract.task.QueryContractTaskMaterialApplyBo;
import com.lframework.xingyun.shkb.entity.ContractTaskMaterialApply;
import com.lframework.xingyun.shkb.vo.contract.task.ApproveContractTaskMaterialApplyVo;
import com.lframework.xingyun.shkb.vo.contract.task.CreateContractTaskMaterialApplyVo;
import com.lframework.xingyun.shkb.vo.contract.task.QueryContractTaskMaterialApplyVo;

/**
* @author kison
* @description 针对表【shkb_contract_task_material_apply(领料申请)】的数据库操作Service
* @createDate 2025-06-04 17:14:59
*/
public interface ContractTaskMaterialApplyService extends BaseMpService<ContractTaskMaterialApply> {

    /**
     * 创建领料申请
     *
     * @param vo 创建领料申请VO
     * @return 领料申请ID
     */
    String create(CreateContractTaskMaterialApplyVo vo);
    
    /**
     * 查询领料申请列表
     *
     * @param pageIndex 页码
     * @param pageSize  每页条数
     * @param vo        查询条件
     * @return 查询结果
     */
    PageResult<QueryContractTaskMaterialApplyBo> query(Integer pageIndex, Integer pageSize,
            QueryContractTaskMaterialApplyVo vo);
            
    /**
     * 审批领料申请
     *
     * @param vo 审批领料申请VO
     */
    void approve(ApproveContractTaskMaterialApplyVo vo);

    /**
     * 按任务ID将已审核通过的领料申请重置为待审状态
     *
     * @param taskId 任务ID
     */
    void resetToPendingByTaskId(String taskId);

    /**
     * 按申请编号将已审核通过的领料申请重置为待审状态
     *
     * @param applyCode 申请编号
     */
    void resetToPendingByApplyCode(String applyCode);
}
