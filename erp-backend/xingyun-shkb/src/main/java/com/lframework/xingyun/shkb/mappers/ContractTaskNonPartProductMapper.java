package com.lframework.xingyun.shkb.mappers;

import com.lframework.starter.web.core.mapper.BaseMapper;
import com.lframework.xingyun.shkb.bo.contract.task.ContractTaskNonPartProductBo;
import com.lframework.xingyun.shkb.entity.ContractTaskNonPartProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author kison
* @description 针对表【shkb_contract_task_non_part_product(非必换件数量)】的数据库操作Mapper
* @createDate 2025-06-04 14:16:19
* @Entity com.lframework.xingyun.shkb.entity.ContractTaskNonPartProduct
*/
@Mapper
public interface ContractTaskNonPartProductMapper extends BaseMapper<ContractTaskNonPartProduct> {
    
    /**
     * 获取任务非必换件记录列表
     *
     * @param taskId 任务ID
     * @return 非必换件记录列表
     */
    List<ContractTaskNonPartProductBo> getTaskNonPartProducts(@Param("taskId") String taskId);
    
    /**
     * 根据任务ID获取非必换件列表
     *
     * @param taskId 任务ID
     * @return 非必换件列表
     */
    List<ContractTaskNonPartProduct> getByTaskId(@Param("taskId") String taskId);
}
