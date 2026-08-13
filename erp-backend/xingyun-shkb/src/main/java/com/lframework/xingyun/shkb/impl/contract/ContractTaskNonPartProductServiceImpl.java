package com.lframework.xingyun.shkb.impl.contract;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.common.utils.CollectionUtil;
import com.lframework.starter.common.utils.FileUtil;
import com.lframework.starter.web.core.utils.IdUtil;
import com.lframework.starter.web.core.utils.UploadUtil;
import com.lframework.starter.web.core.impl.BaseMpServiceImpl;
import com.lframework.xingyun.shkb.bo.contract.task.ContractTaskNonPartProductBo;
import com.lframework.xingyun.shkb.entity.ContractTaskMaterialApply;
import com.lframework.xingyun.shkb.entity.ContractTaskNonPartFile;
import com.lframework.xingyun.shkb.entity.ContractTaskNonPartProduct;
import com.lframework.xingyun.shkb.mappers.ContractTaskMaterialApplyMapper;
import com.lframework.xingyun.shkb.mappers.ContractTaskNonPartProductMapper;
import com.lframework.xingyun.shkb.service.contract.ContractTaskNonPartFileService;
import com.lframework.xingyun.shkb.service.contract.ContractTaskNonPartProductService;
import com.lframework.xingyun.shkb.vo.contract.task.ContractTaskNonPartFileVo;
import com.lframework.xingyun.shkb.vo.contract.task.ContractTaskNonPartProductVo;
import com.lframework.xingyun.shkb.vo.contract.task.BatchUpdateContractTaskNonPartProductVo;
import com.lframework.xingyun.shkb.vo.contract.task.UpdateContractTaskNonPartProductVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
* @author kison
* @description 针对表【shkb_contract_task_non_part_product(非必换件数量)】的数据库操作Service实现
* @createDate 2025-06-04 14:16:19
*/
@Service
public class ContractTaskNonPartProductServiceImpl extends BaseMpServiceImpl<ContractTaskNonPartProductMapper, ContractTaskNonPartProduct>
    implements ContractTaskNonPartProductService {

    @Autowired
    private ContractTaskNonPartFileService contractTaskNonPartFileService;

    @Autowired
    private ContractTaskMaterialApplyMapper contractTaskMaterialApplyMapper;

    /**
     * 保存任务非必换件记录及其附件
     *
     * @param vo 非必换件信息
     * @return 非必换件ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String saveNonPartProduct(ContractTaskNonPartProductVo vo) {
        // 1. 保存非必换件记录
        ContractTaskNonPartProduct record = new ContractTaskNonPartProduct();
        record.setId(IdUtil.getId());
        record.setTaskId(vo.getTaskId());
        record.setProductId(vo.getProductId());
        record.setQuantity(vo.getQuantity());
        record.setReason(vo.getReason());
        
        this.save(record);
        
        // 2. 保存非必换件附件
        if (CollectionUtil.isNotEmpty(vo.getFiles())) {
            for (ContractTaskNonPartFileVo fileVo : vo.getFiles()) {
                ContractTaskNonPartFile fileRecord = new ContractTaskNonPartFile();
                fileRecord.setId(IdUtil.getId());
                fileRecord.setTaskId(vo.getTaskId());
                fileRecord.setNonPartId(record.getId());
                fileRecord.setUrl(fileVo.getUrl());
                fileRecord.setFileSuffix(fileVo.getFileSuffix());
                fileRecord.setFileSize(fileVo.getFileSize());
                fileRecord.setFileName(fileVo.getFileName());
                fileRecord.setContentType(fileVo.getContentType());
                
                contractTaskNonPartFileService.save(fileRecord);
            }
        }
        
        return record.getId();
    }

    /**
     * 获取任务非必换件记录列表
     *
     * @param taskId 任务ID
     * @return 非必换件记录列表
     */
    @Override
    public List<ContractTaskNonPartProduct> getByTaskId(String taskId) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ContractTaskNonPartProduct> queryWrapper = 
            com.baomidou.mybatisplus.core.toolkit.Wrappers.lambdaQuery(ContractTaskNonPartProduct.class)
                .eq(ContractTaskNonPartProduct::getTaskId, taskId);
        return this.list(queryWrapper);
    }
    
    @Override
    public List<ContractTaskNonPartProductBo> getTaskNonPartProducts(String taskId) {
        // 直接调用Mapper中的XML方法获取数据
        return getBaseMapper().getTaskNonPartProducts(taskId);
    }
    
    /**
     * 删除任务非必换件记录
     *
     * @param id 非必换件记录ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNonPartProduct(String id) {
        // 1. 先删除非必换件相关的附件记录
        contractTaskNonPartFileService.removeByNonPartId(id);
        
        // 2. 删除非必换件记录
        this.removeById(id);
    }
    
    /**
     * 修改任务非必换件数量
     *
     * @param vo 修改信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateNonPartProductQuantity(UpdateContractTaskNonPartProductVo vo) {
        // 根据ID获取非必换件记录
        ContractTaskNonPartProduct record = this.getById(vo.getId());
        if (record == null) {
            throw new DefaultClientException("非必换件记录不存在！");
        }
        // 检查是否有领料申请， 如果有，则检查是否已通过审批，过审后就不能变更数量。

        // 更新数量
        record.setQuantity(vo.getQuantity());
        
        this.updateById(record);
    }
    
    /**
     * 批量修改任务非必换件数量
     *
     * @param vo 批量修改信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateNonPartProductQuantity(BatchUpdateContractTaskNonPartProductVo vo) {

        // 检查任务是否存在发料审批记录， 如果有且是通过审批的则不允许再修改数量
        LambdaQueryWrapper<ContractTaskMaterialApply> lmq = new LambdaQueryWrapper<>();
        lmq.eq(ContractTaskMaterialApply::getTaskId, vo.getTaskId())
                .eq(ContractTaskMaterialApply::getApprovalStatus, 1);
        ContractTaskMaterialApply contractTaskMaterialApply = contractTaskMaterialApplyMapper.selectOne(lmq);
        if (contractTaskMaterialApply != null) {
            throw new DefaultClientException("任务已提交领料申请已审批，不能修改数量");
        }

        if (vo.getRecords() == null || vo.getRecords().isEmpty()) {
            return;
        }
        
        for (UpdateContractTaskNonPartProductVo record : vo.getRecords()) {
            // 根据ID获取非必换件记录
            ContractTaskNonPartProduct entity = this.getById(record.getId());
            if (entity == null) {
                throw new DefaultClientException("非必换件记录不存在！ID: " + record.getId());
            }
            
            // 更新数量
            entity.setQuantity(record.getQuantity());
            
            this.updateById(entity);
        }
    }
    
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
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String saveNonPartProductWithFiles(String taskId, String productId, Integer quantity, String reason, List<MultipartFile> files) {

        // 检查任务是否存在发料审批记录， 如果有且是通过审批的则不允许再修改数量
        LambdaQueryWrapper<ContractTaskMaterialApply> lmq = new LambdaQueryWrapper<>();
        lmq.eq(ContractTaskMaterialApply::getTaskId,taskId)
                .eq(ContractTaskMaterialApply::getApprovalStatus, 1);
        ContractTaskMaterialApply contractTaskMaterialApply = contractTaskMaterialApplyMapper.selectOne(lmq);
        if (contractTaskMaterialApply != null) {
            throw new DefaultClientException("任务已提交领料申请已审批，不能修改");
        }

        // 1. 创建非必换件记录
        ContractTaskNonPartProduct record = new ContractTaskNonPartProduct();
        record.setId(IdUtil.getId());
        record.setTaskId(taskId);
        record.setProductId(productId);
        record.setQuantity(quantity);
        record.setReason(reason);
        
        this.save(record);
        
        // 2. 处理文件上传和保存附件记录
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    // 上传文件并获取URL
                    String url = UploadUtil.upload(file).getUrl();
                    
                    // 创建附件记录
                    ContractTaskNonPartFile fileRecord = new ContractTaskNonPartFile();
                    fileRecord.setId(IdUtil.getId());
                    fileRecord.setTaskId(taskId);
                    fileRecord.setNonPartId(record.getId());
                    fileRecord.setUrl(url);
                    
                    // 获取文件后缀
                    String originalFilename = file.getOriginalFilename();
                    if (originalFilename != null) {
                        int lastDotIndex = originalFilename.lastIndexOf('.');
                        if (lastDotIndex > 0) {
                            fileRecord.setFileSuffix(originalFilename.substring(lastDotIndex + 1));
                        }
                    }
                    
                    // 设置文件大小
                    fileRecord.setFileSize(FileUtil.readableFileSize(file.getSize()));
                    
                    // 设置文件名称
                    fileRecord.setFileName(file.getOriginalFilename());
                    
                    // 设置ContentType
                    fileRecord.setContentType(file.getContentType());
                    
                    contractTaskNonPartFileService.save(fileRecord);
                }
            }
        }
        
        return record.getId();
    }    
}
