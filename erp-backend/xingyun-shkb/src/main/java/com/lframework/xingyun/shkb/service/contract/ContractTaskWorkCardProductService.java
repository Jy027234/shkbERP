package com.lframework.xingyun.shkb.service.contract;

import com.lframework.starter.web.core.service.BaseMpService;
import com.lframework.xingyun.shkb.bo.contract.task.ContractTaskProductBo;
import com.lframework.xingyun.shkb.entity.ContractTaskWorkCardProduct;
import com.lframework.xingyun.shkb.vo.contract.task.ContractTaskWorkCardProductVo;

import java.util.List;

/**
* @author kison
* @description 针对表【shkb_contract_task_work_card_product(必换件单数量)】的数据库操作Service
* @createDate 2025-06-03 15:56:56
*/
public interface ContractTaskWorkCardProductService extends BaseMpService<ContractTaskWorkCardProduct> {

    /**
     * 根据任务ID获取任务自身记录的必换件列表
     *
     * @param taskId 任务ID
     * @return 必换件列表，如果没有任务自身记录的必换件则返回空列表
     */
    List<ContractTaskProductBo> getTaskSpecificReplacementParts(String taskId);
    
    /**
     * 保存任务必换件数量
     * 
     * @param vo 任务必换件数量VO
     */
    void saveTaskReplacementPartsQuantity(ContractTaskWorkCardProductVo vo);
    
    /**
     * 根据任务ID获取必换件列表
     *
     * @param taskId 任务ID
     * @return 必换件列表
     */
    List<ContractTaskWorkCardProduct> getByTaskId(String taskId);
}
