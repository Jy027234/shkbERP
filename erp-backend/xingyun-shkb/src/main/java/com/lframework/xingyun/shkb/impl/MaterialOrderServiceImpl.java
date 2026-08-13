package com.lframework.xingyun.shkb.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageInfo;
import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.common.utils.StringUtil;
import com.lframework.starter.web.core.impl.BaseMpServiceImpl;
import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.utils.PageHelperUtil;
import com.lframework.starter.web.core.utils.PageResultUtil;
import com.lframework.starter.web.core.utils.IdUtil;
import com.lframework.xingyun.shkb.bo.material.QueryMaterialOrderBo;
import com.lframework.xingyun.shkb.dto.material.MaterialOrderFullDto;
import com.lframework.xingyun.shkb.entity.ContractTaskMaterialApply;
import com.lframework.xingyun.shkb.entity.MaterialOrder;
import com.lframework.xingyun.shkb.entity.MaterialOrderDetail;
import com.lframework.xingyun.shkb.entity.ContractTaskWorkCardProduct;
import com.lframework.xingyun.shkb.entity.ContractTaskNonPartProduct;
import com.lframework.xingyun.sc.entity.ProductStock;
import com.lframework.xingyun.basedata.entity.Product;
import com.lframework.xingyun.shkb.mappers.ContractTaskMaterialApplyMapper;
import com.lframework.xingyun.shkb.mappers.MaterialOrderMapper;
import com.lframework.xingyun.shkb.mappers.MaterialOrderDetailMapper;
import com.lframework.xingyun.shkb.mappers.ContractTaskWorkCardProductMapper;
import com.lframework.xingyun.shkb.mappers.ContractTaskNonPartProductMapper;
import com.lframework.xingyun.sc.mappers.ProductStockMapper;
import com.lframework.xingyun.basedata.mappers.ProductMapper;
import com.lframework.xingyun.shkb.service.MaterialOrderService;
import com.lframework.xingyun.shkb.vo.material.CreateMaterialOrderFromApplyVo;
import com.lframework.xingyun.shkb.vo.material.QueryMaterialOrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
* @author kison
* @description 针对表【shkb_material_order(发料出库单)】的数据库操作Service实现
* @createDate 2025-06-06 10:07:22
*/
@Service
public class MaterialOrderServiceImpl extends BaseMpServiceImpl<MaterialOrderMapper, MaterialOrder>
    implements MaterialOrderService{

    @Autowired
    private MaterialOrderMapper materialOrderMapper;

    @Autowired
    private ContractTaskMaterialApplyMapper contractTaskMaterialApplyMapper;
    
    @Autowired
    private MaterialOrderDetailMapper materialOrderDetailMapper;
    
    @Autowired
    private ContractTaskWorkCardProductMapper contractTaskWorkCardProductMapper;
    
    @Autowired
    private ContractTaskNonPartProductMapper contractTaskNonPartProductMapper;
    
    @Autowired
    private ProductStockMapper productStockMapper;
    
    @Autowired
    private ProductMapper productMapper;

    @Override
    public PageResult<QueryMaterialOrderBo> query(Integer pageIndex, Integer pageSize, QueryMaterialOrderVo vo) {
        PageHelperUtil.startPage(pageIndex, pageSize);
        List<QueryMaterialOrderBo> datas = getBaseMapper().query(vo);
        return PageResultUtil.convert(new PageInfo<>(datas));
    }

    @Override
    public List<QueryMaterialOrderBo> query(QueryMaterialOrderVo vo) {
        return getBaseMapper().query(vo);
    }

    @Override
    public MaterialOrderFullDto getDetail(String id) {
        return materialOrderMapper.getDetail(id);
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createFromApply(CreateMaterialOrderFromApplyVo vo) {
        // 1. 验证发料申请单是否存在且已审核通过
        ContractTaskMaterialApply materialApply = contractTaskMaterialApplyMapper.selectById(vo.getMaterialApplyId());
        if (materialApply == null) {
            throw new DefaultClientException("发料申请单不存在！");
        }
        
        if (materialApply.getApprovalStatus() != 1) { // 1表示审批通过
            throw new DefaultClientException("发料申请单未通过审批，不能生成发料单！");
        }
        
        // 2. 检查是否已经生成过发料单
        LambdaQueryWrapper<MaterialOrder> queryWrapper = Wrappers.lambdaQuery(MaterialOrder.class)
                .eq(MaterialOrder::getMaterialApplyId, vo.getMaterialApplyId());
        MaterialOrder existOrder = materialOrderMapper.selectOne(queryWrapper);
        if (existOrder != null) {
            throw new DefaultClientException("该发料申请单已生成发料单，不能重复生成！");
        }
        
        // 3. 获取任务的必换件和非必换件列表
        String taskId = materialApply.getTaskId();
        List<ContractTaskWorkCardProduct> replacementParts = contractTaskWorkCardProductMapper.getByTaskId(taskId);
        List<ContractTaskNonPartProduct> nonReplacementParts = contractTaskNonPartProductMapper.getByTaskId(taskId);
        
        // 4. 检查库存是否充足
        Map<String, Integer> productQuantityMap = new HashMap<>();
        
        // 汇总必换件所需数量
        for (ContractTaskWorkCardProduct part : replacementParts) {
            String productId = part.getProductId();
            int quantity = part.getQuantity();
            productQuantityMap.put(productId, productQuantityMap.getOrDefault(productId, 0) + quantity);
        }
        
        // 汇总非必换件所需数量
        for (ContractTaskNonPartProduct part : nonReplacementParts) {
            String productId = part.getProductId();
            int quantity = part.getQuantity();
            productQuantityMap.put(productId, productQuantityMap.getOrDefault(productId, 0) + quantity);
        }
        
        // 5. 创建发料单
        MaterialOrder materialOrder = new MaterialOrder();
        materialOrder.setId(IdUtil.getId());
        
        // 生成发料单号：FL + 日期 + 6位随机数
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomStr = String.format("%06d", (int)(Math.random() * 1000000));
        materialOrder.setCode("FL" + dateStr + randomStr);
        
        materialOrder.setScId(vo.getScId());
        materialOrder.setTotalNum(0); // 初始化总数量，后续累加
        materialOrder.setTotalOutNum(0);
        materialOrder.setTotalAmount(java.math.BigDecimal.ZERO); // 初始化总金额，后续累加
        materialOrder.setDescription(vo.getDescription());
        materialOrder.setMaterialApplyId(vo.getMaterialApplyId());
        materialOrder.setIsOutFinish(Boolean.FALSE);
        
        // 6. 创建发料单明细
        List<MaterialOrderDetail> details = new ArrayList<>();
        java.math.BigDecimal totalAmount = java.math.BigDecimal.ZERO;
        int totalNum = 0;
        
        // 处理必换件
        for (ContractTaskWorkCardProduct part : replacementParts) {
            String productId = part.getProductId();
            int quantity = part.getQuantity();

            BigDecimal taxPrice = new BigDecimal(0);
            BigDecimal taxAmount = new BigDecimal(0);

            // 获取指定仓库的库存信息
            ProductStock stock = productStockMapper.getByProductIdAndScId(productId, vo.getScId());
            if (stock != null) {
                taxPrice = stock.getTaxPrice();
                taxAmount = taxPrice.multiply(new BigDecimal(quantity));
            }


            // 创建发料单明细
            MaterialOrderDetail detail = new MaterialOrderDetail();
            detail.setId(IdUtil.getId());
            detail.setOrderId(materialOrder.getId());
            detail.setProductId(productId);
            detail.setOutNum(0);
            detail.setOrderNum(quantity);
            detail.setTaxPrice(taxPrice);
            detail.setTaxAmount(taxAmount);
            details.add(detail);
            totalAmount = totalAmount.add(taxAmount);
            totalNum += quantity;
        }
        
        // 处理非必换件
        for (ContractTaskNonPartProduct part : nonReplacementParts) {
            String productId = part.getProductId();
            int quantity = part.getQuantity();
            BigDecimal taxPrice = new BigDecimal(0);
            BigDecimal taxAmount = new BigDecimal(0);
            // 获取指定仓库的库存信息
            ProductStock stock = productStockMapper.getByProductIdAndScId(productId, vo.getScId());
            if(stock !=null ) {
                taxPrice = stock.getTaxPrice();
                taxAmount = taxPrice.multiply(new BigDecimal(quantity));
            }
            // 创建发料单明细
            MaterialOrderDetail detail = new MaterialOrderDetail();
            detail.setId(IdUtil.getId());
            detail.setOrderId(materialOrder.getId());
            detail.setProductId(productId);
            detail.setOutNum(0);
            detail.setOrderNum(quantity);
            detail.setTaxPrice(taxPrice);
            detail.setTaxAmount(taxAmount);
            details.add(detail);
            totalAmount = totalAmount.add(taxAmount);
            totalNum += quantity;
        }
        
        // 更新发料单总金额和总数量
        materialOrder.setTotalAmount(totalAmount);
        materialOrder.setTotalNum(totalNum);
        
        // 7. 保存发料单和明细
        materialOrderMapper.insert(materialOrder);
        // 逐条插入明细记录
        for (MaterialOrderDetail detail : details) {
            materialOrderDetailMapper.insert(detail);
        }
        
        return materialOrder.getId();
    }
}



