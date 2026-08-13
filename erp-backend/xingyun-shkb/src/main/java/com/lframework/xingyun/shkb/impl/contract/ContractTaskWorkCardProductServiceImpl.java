package com.lframework.xingyun.shkb.impl.contract;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.common.utils.CollectionUtil;
import com.lframework.starter.web.core.impl.BaseMpServiceImpl;
import com.lframework.starter.web.core.utils.IdUtil;
import com.lframework.xingyun.shkb.bo.contract.task.ContractTaskProductBo;
import com.lframework.xingyun.shkb.dto.contract.task.ContractTaskProductDto;
import com.lframework.xingyun.shkb.entity.ContractTaskMaterialApply;
import com.lframework.xingyun.shkb.entity.ContractTaskWorkCardProduct;
import com.lframework.xingyun.shkb.mappers.ContractTaskMaterialApplyMapper;
import com.lframework.xingyun.shkb.service.contract.ContractTaskWorkCardProductService;
import com.lframework.xingyun.shkb.mappers.ContractTaskWorkCardProductMapper;
import com.lframework.xingyun.shkb.vo.contract.task.ContractTaskWorkCardProductVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author kison
* @description 针对表【shkb_contract_task_work_card_product(必换件单数量)】的数据库操作Service实现
* @createDate 2025-06-03 15:56:56
*/
@Service
public class ContractTaskWorkCardProductServiceImpl extends BaseMpServiceImpl<ContractTaskWorkCardProductMapper, ContractTaskWorkCardProduct>
    implements ContractTaskWorkCardProductService {

    @Autowired
    private ContractTaskMaterialApplyMapper contractTaskMaterialApplyMapper;

    @Override
    public List<ContractTaskProductBo> getTaskSpecificReplacementParts(String taskId) {
        // 使用XML查询获取任务自身记录的必换件列表
        List<ContractTaskProductDto> dtoList = getBaseMapper().getTaskSpecificReplacementParts(taskId);
        
        if (CollectionUtil.isEmpty(dtoList)) {
            return Collections.emptyList();
        }
        
        // 转换DTO为BO
        return dtoList.stream()
                .map(dto -> {
                    ContractTaskProductBo bo = new ContractTaskProductBo();
                    bo.setId(dto.getId());
                    bo.setWorkCardId(dto.getWorkCardId());
                    bo.setWorkCardCode(dto.getWorkCardCode());
                    bo.setWorkCardName(dto.getWorkCardName());
                    bo.setRepairTypeId(dto.getRepairTypeId());
                    bo.setRepairTypeName(dto.getRepairTypeName());
                    bo.setPartNumberId(dto.getPartNumberId());
                    bo.setPartNumber(dto.getPartNumber());
                    bo.setPartNumberCode(dto.getPartNumberCode());
                    bo.setMachineTypeName(dto.getMachineTypeName());
                    bo.setProductMachineTypeName(dto.getProductMachineTypeName());
                    bo.setProductId(dto.getProductId());
                    bo.setProductCode(dto.getProductCode());
                    bo.setProductName(dto.getProductName());
                    bo.setProductSpec(dto.getProductSpec());
                    bo.setProductUnit(dto.getProductUnit());
                    bo.setQuantity(dto.getQuantity());
                    return bo;
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ContractTaskWorkCardProduct> getByTaskId(String taskId) {
        LambdaQueryWrapper<ContractTaskWorkCardProduct> queryWrapper = Wrappers.lambdaQuery(ContractTaskWorkCardProduct.class)
                .eq(ContractTaskWorkCardProduct::getTaskId, taskId);
        return this.list(queryWrapper);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveTaskReplacementPartsQuantity(ContractTaskWorkCardProductVo vo) {

        // 检查任务是否存在发料审批记录， 如果有且是通过审批的则不允许再修改数量
        LambdaQueryWrapper<ContractTaskMaterialApply> lmq = new LambdaQueryWrapper<>();
        lmq.eq(ContractTaskMaterialApply::getTaskId, vo.getTaskId())
                .eq(ContractTaskMaterialApply::getApprovalStatus, 1);
        ContractTaskMaterialApply contractTaskMaterialApply = contractTaskMaterialApplyMapper.selectOne(lmq);
        if (contractTaskMaterialApply != null) {
            throw new DefaultClientException("任务已提交领料申请已审批，不能修改数量");
        }


        // 先删除任务原有的必换件数量记录
        LambdaQueryWrapper<ContractTaskWorkCardProduct> queryWrapper = Wrappers.lambdaQuery(ContractTaskWorkCardProduct.class)
                .eq(ContractTaskWorkCardProduct::getTaskId, vo.getTaskId());
        this.remove(queryWrapper);
        
        // 批量保存新的必换件数量记录
        if (!CollectionUtil.isEmpty(vo.getProducts())) {
            List<ContractTaskWorkCardProduct> products = vo.getProducts().stream()
                    .map(item -> {
                        ContractTaskWorkCardProduct product = new ContractTaskWorkCardProduct();
                        product.setId(IdUtil.getId());
                        product.setTaskId(vo.getTaskId());
                        product.setWorkCardId(item.getWorkCardId());
                        product.setProductId(item.getProductId());
                        product.setQuantity(item.getQuantity());
                        return product;
                    }).collect(Collectors.toList());
            
            this.saveBatch(products);
        }
    }
}
