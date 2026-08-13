package com.lframework.xingyun.shkb.service.contract;

import com.lframework.starter.web.core.service.BaseMpService;
import com.lframework.xingyun.shkb.bo.contract.task.ContractTaskRepairStatusRecordBo;
import com.lframework.xingyun.shkb.entity.ContractTaskRepairStatusRecord;
import com.lframework.xingyun.shkb.vo.contract.task.CreateContractTaskRepairStatusRecordVo;

import java.util.List;

/**
* @author kison
* @description 针对表【shkb_contract_task_repair_status_record(合同任务维修状态记录)】的数据库操作Service
* @createDate 2025-05-28 16:00:01
*/
public interface ContractTaskRepairStatusRecordService extends BaseMpService<ContractTaskRepairStatusRecord> {

    /**
     * 创建合同任务维修状态记录
     *
     * @param vo 创建维修状态记录信息
     * @return 维修状态记录ID
     */
    String create(CreateContractTaskRepairStatusRecordVo vo);
    
    /**
     * 获取任务维修状态记录列表
     *
     * @param taskId 任务ID
     * @return 维修状态记录列表
     */
    List<ContractTaskRepairStatusRecordBo> getByTaskId(String taskId);
}
