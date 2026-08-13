package com.lframework.xingyun.shkb.service.contract;

import com.lframework.starter.web.core.service.BaseMpService;
import com.lframework.xingyun.shkb.bo.contract.task.ContractTaskNonPartProductBo;
import com.lframework.xingyun.shkb.entity.ContractTaskNonPartProduct;
import com.lframework.xingyun.shkb.vo.contract.task.ContractTaskNonPartProductVo;
import com.lframework.xingyun.shkb.vo.contract.task.BatchUpdateContractTaskNonPartProductVo;
import com.lframework.xingyun.shkb.vo.contract.task.UpdateContractTaskNonPartProductVo;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
* @author kison
* @description 针对表【shkb_contract_task_non_part_product(非必换件数量)】的数据库操作Service
* @createDate 2025-06-04 14:16:19
*/
public interface ContractTaskNonPartProductService extends BaseMpService<ContractTaskNonPartProduct> {

    /**
     * 保存任务非必换件记录及其附件
     *
     * @param vo 非必换件信息
     * @return 非必换件ID
     */
    String saveNonPartProduct(ContractTaskNonPartProductVo vo);
    
    /**
     * 获取任务非必换件记录列表
     *
     * @param taskId 任务ID
     * @return 非必换件记录列表
     */
    List<ContractTaskNonPartProductBo> getTaskNonPartProducts(String taskId);
    
    /**
     * 删除任务非必换件记录
     *
     * @param id 非必换件记录ID
     */
    void deleteNonPartProduct(String id);
    
    /**
     * 修改任务非必换件数量
     *
     * @param vo 修改信息
     */
    void updateNonPartProductQuantity(UpdateContractTaskNonPartProductVo vo);
    
    /**
     * 批量修改任务非必换件数量
     *
     * @param vo 批量修改信息
     */
    void batchUpdateNonPartProductQuantity(BatchUpdateContractTaskNonPartProductVo vo);
    
    /**
     * 保存任务非必换件记录及其附件（直接处理文件上传）
     *
     * @param taskId 任务ID
     * @param productId 商品ID
     * @param quantity 数量
     * @param reason 原因说明
     * @param files 上传的文件列表
     * @return 非必换件ID
     */
    String saveNonPartProductWithFiles(String taskId, String productId, Integer quantity, String reason, List<MultipartFile> files);
    
    /**
     * 根据任务ID获取非必换件列表
     *
     * @param taskId 任务ID
     * @return 非必换件列表
     */
    List<ContractTaskNonPartProduct> getByTaskId(String taskId);
}
