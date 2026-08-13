package com.lframework.xingyun.shkb.service.contract;

import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.service.BaseMpService;
import com.lframework.xingyun.shkb.bo.contract.task.GetContractTaskBo;
import com.lframework.xingyun.shkb.bo.contract.task.QueryContractTaskBo;
import com.lframework.xingyun.shkb.bo.contract.task.TaskPartListBo;
import com.lframework.xingyun.shkb.entity.ContractTask;
import com.lframework.xingyun.shkb.vo.contract.task.DispatchTaskVo;
import com.lframework.xingyun.shkb.vo.contract.task.IssueMaterialVo;
import com.lframework.xingyun.shkb.vo.contract.task.OfflineAppraisalVo;
import com.lframework.xingyun.shkb.vo.contract.task.QueryContractTaskVo;
import com.lframework.xingyun.shkb.vo.contract.task.UpdateTaskStatusVo;
import com.lframework.xingyun.shkb.vo.contract.task.UpdateContractTaskApprovalFileNumberVo;
import com.lframework.xingyun.shkb.vo.contract.task.UpdateContractTaskVo;

import java.util.List;
import java.util.Map;

/**
* @author kison
* @description 针对表【shkb_contract_task(合同任务)】的数据库操作Service
* @createDate 2025-05-09 11:47:33
*/
public interface ContractTaskService extends BaseMpService<ContractTask> {

    /**
     * 根据合同ID创建合同任务
     *
     * @param contractId 合同ID
     * @return 合同任务ID
     */
    String createContractTask(String contractId);
    
    /**
     * 查询合同任务列表
     *
     * @param pageIndex 页码
     * @param pageSize  每页条数
     * @param vo        参数
     * @return 合同任务列表
     */
    PageResult<QueryContractTaskBo> query(Integer pageIndex, Integer pageSize, QueryContractTaskVo vo);
    
    /**
     * 根据ID获取合同任务详情
     *
     * @param id 合同任务ID
     * @return 合同任务详情
     */
    GetContractTaskBo getDetail(String id);

    /**
     * 修改合同任务及其合同维度的维修信息。
     *
     * @param vo 修改参数
     */
    void update(UpdateContractTaskVo vo);
    
    /**
     * 线下鉴定
     *
     * @param vo 线下鉴定参数
     */
    void offlineAppraisal(OfflineAppraisalVo vo);
    
    /**
     * 任务派发
     *
     * @param vo 任务派发参数
     * @return 新创建的合同ID，如果没有创建新合同则返回null
     */
    String dispatchTask(DispatchTaskVo vo);
    
    /**
     * 任务发料出库
     *
     * @param vo IssueMaterialVo
     * @return 发料出库单ID
     */
    String issueMaterial(IssueMaterialVo vo);
    
    /**
     * 获取任务换件清单列表
     *
     * @param taskId 任务ID
     * @param scId 仓库ID
     * @return 换件清单列表，包含必换件和非必换件的商品信息、机型、件号、数量和库存信息
     */
    Map<String, List<TaskPartListBo>> getTaskPartList(String taskId, String scId);
    
    /**
     * 修改合同任务状态
     *
     * @param vo 修改任务状态参数
     */
    void updateTaskStatus(UpdateTaskStatusVo vo);

    /**
     * 修改合同任务放行文件编号
     *
     * @param vo 修改放行文件编号参数
     */
    void updateApprovalFileNumber(UpdateContractTaskApprovalFileNumberVo vo);
}
