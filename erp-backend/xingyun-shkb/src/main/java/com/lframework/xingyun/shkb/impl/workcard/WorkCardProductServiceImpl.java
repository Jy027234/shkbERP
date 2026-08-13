package com.lframework.xingyun.shkb.impl.workcard;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lframework.starter.common.utils.CollectionUtil;
import com.lframework.starter.web.core.impl.BaseMpServiceImpl;
import com.lframework.starter.web.core.utils.IdUtil;
import com.lframework.xingyun.shkb.bo.contract.task.ContractTaskProductBo;
import com.lframework.xingyun.shkb.bo.workcard.WorkCardProductBo;
import com.lframework.xingyun.shkb.dto.contract.task.ContractTaskProductDto;
import com.lframework.xingyun.shkb.entity.WorkCardProduct;
import com.lframework.xingyun.shkb.mappers.WorkCardProductMapper;
import com.lframework.xingyun.shkb.service.workcard.WorkCardProductService;
import com.lframework.xingyun.shkb.vo.workcard.BatchUpdateWorkCardProductVo;
import com.lframework.xingyun.shkb.vo.workcard.WorkCardProductVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
* @author kison
* @description 针对表【shkb_work_card_product(工卡必换件表)】的数据库操作Service实现
* @createDate 2025-05-15 15:52:38
*/
@Service
public class WorkCardProductServiceImpl extends BaseMpServiceImpl<WorkCardProductMapper, WorkCardProduct>
    implements WorkCardProductService {

    @Override
    public List<WorkCardProductBo> getByWorkCardId(String workCardId) {
        // 查询工卡必换件列表
        LambdaQueryWrapper<WorkCardProduct> queryWrapper = Wrappers.lambdaQuery(WorkCardProduct.class)
                .eq(WorkCardProduct::getWorkCardId, workCardId);
        
        List<WorkCardProduct> products = this.list(queryWrapper);
        
        // 转换为 BO 对象
        return products.stream().map(WorkCardProductBo::new).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchAdd(WorkCardProductVo vo) {
        if (CollectionUtil.isEmpty(vo.getProductIds())) {
            return;
        }
        
        // 删除已存在的相同商品记录，避免重复添加
        LambdaQueryWrapper<WorkCardProduct> deleteWrapper = Wrappers.lambdaQuery(WorkCardProduct.class)
                .eq(WorkCardProduct::getWorkCardId, vo.getWorkCardId())
                .in(WorkCardProduct::getProductId, vo.getProductIds());
        this.remove(deleteWrapper);
        
        // 批量添加必换件
        List<WorkCardProduct> products = vo.getProductIds().stream().map(productId -> {
            WorkCardProduct product = new WorkCardProduct();
            product.setId(IdUtil.getId());
            product.setWorkCardId(vo.getWorkCardId());
            product.setProductId(productId);
            product.setQuantity(1);
            return product;
        }).collect(Collectors.toList());
        
        this.saveBatch(products);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDelete(WorkCardProductVo vo) {
        if (CollectionUtil.isEmpty(vo.getProductIds())) {
            return;
        }
        
        // 批量删除必换件
        LambdaQueryWrapper<WorkCardProduct> deleteWrapper = Wrappers.lambdaQuery(WorkCardProduct.class)
                .eq(WorkCardProduct::getWorkCardId, vo.getWorkCardId())
                .in(WorkCardProduct::getProductId, vo.getProductIds());
        this.remove(deleteWrapper);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateQuantity(BatchUpdateWorkCardProductVo vo) {
        if (CollectionUtil.isEmpty(vo.getProducts())) {
            return;
        }
        
        // 批量更新必换件数量
        for (BatchUpdateWorkCardProductVo.ProductInfo product : vo.getProducts()) {
            // 根据ID查询必换件
            WorkCardProduct workCardProduct = this.getById(product.getId());
            if (workCardProduct == null) {
                continue;
            }
            
            // 验证是否属于当前工卡
            if (!workCardProduct.getWorkCardId().equals(vo.getWorkCardId())) {
                continue;
            }
            
            // 更新数量
            workCardProduct.setQuantity(product.getQuantity());
            this.updateById(workCardProduct);
        }
    }
    
    @Override
    public List<ContractTaskProductBo> getTaskReplacementParts(String taskId) {
        // 使用XML查询获取任务必换件列表
        List<ContractTaskProductDto> dtos = getBaseMapper().getTaskReplacementParts(taskId);
        
        // 转换为BO对象
        return dtos.stream()
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
}
