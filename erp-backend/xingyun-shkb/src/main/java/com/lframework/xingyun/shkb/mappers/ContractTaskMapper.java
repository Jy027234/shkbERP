package com.lframework.xingyun.shkb.mappers;

import com.lframework.starter.web.core.mapper.BaseMapper;
import com.lframework.xingyun.shkb.bo.contract.task.GetContractTaskBo;
import com.lframework.xingyun.shkb.bo.contract.task.QueryContractTaskBo;
import com.lframework.xingyun.shkb.bo.contract.task.TaskPartListBo;
import com.lframework.xingyun.shkb.entity.ContractTask;
import com.lframework.xingyun.shkb.vo.contract.task.QueryContractTaskVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author kison
* @description 针对表【shkb_contract_task(合同任务)】的数据库操作Mapper
* @createDate 2025-05-09 11:47:33
* @Entity com.lframework.xingyun.shkb.entity.ContractTask
*/
@Mapper
public interface ContractTaskMapper extends BaseMapper<ContractTask> {

    /**
     * 查询合同任务列表
     *
     * @param vo 查询参数
     * @return 合同任务列表
     */
    List<QueryContractTaskBo> query(QueryContractTaskVo vo);

    /**
     * 按合同查询维修类型列表
     * @param contractId 合同ID
     */
    List<QueryContractTaskBo.RepairTypeBo> selectRepairTypesByContractId(String contractId);

    /**
     * 按任务查询工卡列表
     * @param taskId 任务ID
     */
    List<QueryContractTaskBo.WorkCardBo> selectWorkCardsByTaskId(String taskId);
    
    /**
     * 根据ID获取合同任务详情
     *
     * @param id 合同任务ID
     * @return 合同任务详情
     */
    GetContractTaskBo getDetail(String id);
    
    /**
     * 获取任务换件清单列表
     *
     * @param taskId 任务ID
     * @param scId 仓库ID
     * @return 换件清单列表
     */
    List<TaskPartListBo> getTaskPartList(String taskId, String scId);
    
    /**
     * 获取任务必换件清单列表
     *
     * @param taskId 任务ID
     * @param scId 仓库ID
     * @return 必换件清单列表
     */
    List<TaskPartListBo> getTaskMandatoryPartList(String taskId, String scId);

    /**
     * 获取任务非必换件清单列表
     *
     * @param taskId 任务ID
     * @param scId 仓库ID
     * @return 非必换件清单列表
     */
    List<TaskPartListBo> getTaskNonMandatoryPartList(String taskId, String scId);
}
