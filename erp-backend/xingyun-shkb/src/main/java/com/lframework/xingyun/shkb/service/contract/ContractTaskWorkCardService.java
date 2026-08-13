package com.lframework.xingyun.shkb.service.contract;

import com.lframework.starter.web.core.service.BaseMpService;
import com.lframework.xingyun.shkb.entity.ContractTaskWorkCard;
import com.lframework.xingyun.shkb.vo.contract.task.ContractTaskWorkCardVo;

import java.util.List;

/**
 * 合同任务工卡Service接口
 *
 * @author kison
 */
public interface ContractTaskWorkCardService extends BaseMpService<ContractTaskWorkCard> {

    /**
     * 根据任务ID查询任务工卡列表
     *
     * @param taskId 任务ID
     * @return 任务工卡列表
     */
    List<ContractTaskWorkCard> getByTaskId(String taskId);

    /**
     * 批量添加任务工卡
     *
     * @param vo 任务工卡VO
     */
    void batchAdd(ContractTaskWorkCardVo vo);

    /**
     * 批量删除任务工卡
     *
     * @param vo 任务工卡VO
     */
    void batchDelete(ContractTaskWorkCardVo vo);
}
