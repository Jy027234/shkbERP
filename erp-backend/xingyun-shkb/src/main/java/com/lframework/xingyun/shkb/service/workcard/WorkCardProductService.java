package com.lframework.xingyun.shkb.service.workcard;

import com.lframework.starter.web.core.service.BaseMpService;
import com.lframework.xingyun.shkb.bo.contract.task.ContractTaskProductBo;
import com.lframework.xingyun.shkb.bo.workcard.WorkCardProductBo;
import com.lframework.xingyun.shkb.entity.WorkCardProduct;
import com.lframework.xingyun.shkb.vo.workcard.BatchUpdateWorkCardProductVo;
import com.lframework.xingyun.shkb.vo.workcard.WorkCardProductVo;

import java.util.List;

/**
* @author kison
* @description 针对表【shkb_work_card_product(工卡必换件表)】的数据库操作Service
* @createDate 2025-05-15 15:52:38
*/
public interface WorkCardProductService extends BaseMpService<WorkCardProduct> {

    /**
     * 根据工卡ID查询必换件列表
     *
     * @param workCardId 工卡ID
     * @return 必换件列表
     */
    List<WorkCardProductBo> getByWorkCardId(String workCardId);

    /**
     * 批量添加工卡必换件
     *
     * @param vo 工卡必换件VO
     */
    void batchAdd(WorkCardProductVo vo);

    /**
     * 批量删除工卡必换件
     *
     * @param vo 工卡必换件VO
     */
    void batchDelete(WorkCardProductVo vo);
    
    /**
     * 批量修改工卡必换件数量
     *
     * @param vo 批量修改工卡必换件VO
     */
    void batchUpdateQuantity(BatchUpdateWorkCardProductVo vo);
    
    /**
     * 根据任务ID获取必换件列表
     *
     * @param taskId 任务ID
     * @return 必换件列表
     */
    List<ContractTaskProductBo> getTaskReplacementParts(String taskId);
}
