package com.lframework.xingyun.shkb.mappers;

import com.lframework.starter.web.core.mapper.BaseMapper;
import com.lframework.xingyun.shkb.dto.contract.task.ContractTaskProductDto;
import com.lframework.xingyun.shkb.entity.ContractTaskWorkCardProduct;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author kison
* @description 针对表【shkb_contract_task_work_card_product(必换件单数量)】的数据库操作Mapper
* @createDate 2025-06-03 15:56:56
* @Entity com.lframework.xingyun.shkb.entity.ContractTaskWorkCardProduct
*/
@Mapper
public interface ContractTaskWorkCardProductMapper extends BaseMapper<ContractTaskWorkCardProduct> {

    /**
     * 根据任务ID获取任务自身记录的必换件列表
     *
     * @param taskId 任务ID
     * @return 必换件列表DTO
     */
    List<ContractTaskProductDto> getTaskSpecificReplacementParts(String taskId);
    
    /**
     * 根据任务ID获取必换件列表
     *
     * @param taskId 任务ID
     * @return 必换件列表
     */
    List<ContractTaskWorkCardProduct> getByTaskId(String taskId);
}
