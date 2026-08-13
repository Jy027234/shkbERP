package com.lframework.xingyun.shkb.impl.contract;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.github.pagehelper.PageInfo;
import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.common.utils.CollectionUtil;
import com.lframework.starter.web.core.components.security.SecurityUtil;
import com.lframework.starter.web.core.impl.BaseMpServiceImpl;
import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.utils.IdUtil;
import com.lframework.starter.web.core.utils.PageHelperUtil;
import com.lframework.starter.web.core.utils.PageResultUtil;
import com.lframework.xingyun.basedata.entity.Product;
import com.lframework.xingyun.basedata.mappers.ProductMapper;
import com.lframework.xingyun.sc.entity.ProductStock;
import com.lframework.xingyun.sc.entity.ProductStockLog;
import com.lframework.xingyun.sc.enums.ProductStockBizType;
import com.lframework.xingyun.sc.mappers.ProductStockMapper;
import com.lframework.xingyun.sc.mappers.ProductStockLogMapper;
import com.lframework.xingyun.shkb.bo.contract.task.ContractTaskWorkCardBo;
import com.lframework.xingyun.shkb.bo.contract.task.GetContractTaskBo;
import com.lframework.xingyun.shkb.bo.contract.task.QueryContractTaskBo;
import com.lframework.xingyun.shkb.entity.Contract;
import com.lframework.xingyun.shkb.entity.ContractRepair;
import com.lframework.xingyun.shkb.entity.ContractTask;
import com.lframework.xingyun.shkb.entity.ContractTaskRepairStatusRecord;
import com.lframework.xingyun.shkb.entity.ContractTaskWorkCard;
import com.lframework.xingyun.shkb.entity.WorkCard;
import com.lframework.xingyun.shkb.entity.WorkCardProduct;
import com.lframework.xingyun.shkb.enums.*;
import com.lframework.xingyun.shkb.mappers.ContractTaskMapper;
import com.lframework.xingyun.shkb.mappers.ContractMapper;
import com.lframework.xingyun.shkb.mappers.ContractRepairMapper;
import com.lframework.xingyun.shkb.mappers.ContractTaskRepairStatusRecordMapper;
import com.lframework.xingyun.shkb.mappers.ContractTaskWorkCardMapper;
import com.lframework.xingyun.shkb.mappers.ContractTaskWorkCardProductMapper;
import com.lframework.xingyun.shkb.mappers.ContractTaskNonPartProductMapper;
import com.lframework.xingyun.shkb.mappers.ContractTaskMaterialApplyMapper;
import com.lframework.xingyun.shkb.mappers.MaterialOrderMapper;
import com.lframework.xingyun.shkb.mappers.MaterialOrderDetailMapper;
import com.lframework.xingyun.shkb.mappers.WorkCardMapper;
import com.lframework.xingyun.shkb.mappers.WorkCardProductMapper;
import com.lframework.xingyun.shkb.service.contract.ContractTaskService;
import com.lframework.xingyun.shkb.service.contract.ContractTaskWorkCardService;
import com.lframework.xingyun.shkb.service.contract.ContractRepairService;

import com.lframework.xingyun.shkb.bo.contract.task.TaskPartListBo;
import com.lframework.xingyun.shkb.entity.ContractTaskMaterialApply;
import com.lframework.xingyun.shkb.entity.ContractTaskWorkCardProduct;
import com.lframework.xingyun.shkb.entity.ContractTaskNonPartProduct;
import com.lframework.xingyun.shkb.entity.MaterialOrder;
import com.lframework.xingyun.shkb.entity.MaterialOrderDetail;
import com.lframework.xingyun.shkb.utils.EnumUtil;
import com.lframework.xingyun.shkb.vo.contract.task.IssueMaterialVo;
import com.lframework.xingyun.template.inner.entity.SysUser;
import com.lframework.xingyun.template.inner.mappers.system.SysUserMapper;
import com.lframework.xingyun.shkb.vo.contract.task.DispatchTaskVo;
import com.lframework.xingyun.shkb.vo.contract.task.OfflineAppraisalVo;
import com.lframework.xingyun.shkb.vo.contract.task.QueryContractTaskVo;
import com.lframework.xingyun.shkb.vo.contract.task.UpdateTaskStatusVo;
import com.lframework.xingyun.shkb.vo.contract.task.UpdateContractTaskApprovalFileNumberVo;
import com.lframework.xingyun.shkb.vo.contract.task.UpdateContractTaskVo;
import java.time.format.DateTimeFormatter;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
* @author kison
* @description 针对表【shkb_contract_task(合同任务)】的数据库操作Service实现
* @createDate 2025-05-09 11:47:33
*/
@Service
@Slf4j
public class ContractTaskServiceImpl extends BaseMpServiceImpl<ContractTaskMapper, ContractTask>
    implements ContractTaskService{

    @Autowired
    private ContractMapper contractMapper;
    
    @Autowired
    private ContractRepairMapper contractRepairMapper;
    
    @Autowired
    private SysUserMapper sysUserMapper;
    
    @Autowired
    private ContractTaskWorkCardProductMapper contractTaskWorkCardProductMapper;
    
    @Autowired
    private ContractTaskNonPartProductMapper contractTaskNonPartProductMapper;
    
    @Autowired
    private ContractTaskMaterialApplyMapper contractTaskMaterialApplyMapper;
    
    @Autowired
    private MaterialOrderMapper materialOrderMapper;
    
    @Autowired
    private MaterialOrderDetailMapper materialOrderDetailMapper;
    
    @Autowired
    private ProductStockMapper productStockMapper;
    
    @Autowired
    private ProductStockLogMapper productStockLogMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ContractTaskRepairStatusRecordMapper contractTaskRepairStatusRecordMapper;

    @Autowired
    private ContractTaskWorkCardService contractTaskWorkCardService;

    @Autowired
    private ContractRepairService contractRepairService;

    @Autowired
    private WorkCardMapper workCardMapper;

    @Autowired
    private WorkCardProductMapper workCardProductMapper;

    @Autowired
    private ContractTaskWorkCardMapper contractTaskWorkCardMapper;

    @Autowired
    private ContractTaskMapper contractTaskMapper;

    /**
     * 获取任务换件清单列表
     *
     * @param taskId 任务ID
     * @param scId 仓库ID
     * @return 换件清单列表，包含必换件和非必换件的商品信息
     */
    @Override
    public Map<String, List<TaskPartListBo>> getTaskPartList(String taskId, String scId) {
        // 检查任务是否存在
        ContractTask task = this.getById(taskId);
        if (task == null) {
            throw new DefaultClientException("任务不存在！");
        }
        
        // 使用Mapper分别查询必换件和非必换件清单，传入仓库ID
        List<TaskPartListBo> mandatoryParts = getBaseMapper().getTaskMandatoryPartList(taskId, scId);
        List<TaskPartListBo> nonMandatoryParts = getBaseMapper().getTaskNonMandatoryPartList(taskId, scId);
        // 将两个列表放入Map中返回
        Map<String, List<TaskPartListBo>> result = new HashMap<>();
        result.put("mandatoryParts", mandatoryParts);
        result.put("nonMandatoryParts", nonMandatoryParts);
        
        return result;
    }
    
    /**
     * 任务发料出库
     *
     * @param vo 任务发料请求
     * @return 发料出库单ID
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String issueMaterial(IssueMaterialVo vo) {
        String taskId = vo.getTaskId();
        String scId = vo.getScId();
        String remark = vo.getRemark();
        // 1. 查询任务信息
        ContractTask task = this.getById(taskId);
        if (task == null) {
            throw new DefaultClientException("任务不存在");
        }
        
        // 2. 检查任务状态，确保任务处于可发料状态
        if (TaskStatus.RETURNED.getCode().equals(task.getTaskStatus())) {
            throw new DefaultClientException("任务已退回，不能发料");
        }
        
        // 3. 检查是否已发料
        if (Boolean.TRUE.equals(task.getIsMaterialIssued())) {
            throw new DefaultClientException("任务已发料，不能重复发料");
        }
        
        // 4. 检查领料申请状态
        LambdaQueryWrapper<ContractTaskMaterialApply> queryWrapper = Wrappers.lambdaQuery(ContractTaskMaterialApply.class)
                .eq(ContractTaskMaterialApply::getTaskId, taskId);
        ContractTaskMaterialApply materialApply = contractTaskMaterialApplyMapper.selectOne(queryWrapper);
        
        if (materialApply == null) {
            throw new DefaultClientException("任务未提交领料申请，不能发料");
        }
        
        if (materialApply.getApprovalStatus() != 1) { // 1表示审批通过
            throw new DefaultClientException("领料申请未通过审批，不能发料");
        }
        
        // 5. 获取任务的必换件和非必换件列表
        // 直接使用Mapper获取数据，避免循环依赖
        List<ContractTaskWorkCardProduct> replacementParts = contractTaskWorkCardProductMapper.getByTaskId(taskId);
                
        List<ContractTaskNonPartProduct> nonReplacementParts = contractTaskNonPartProductMapper.getByTaskId(taskId);
        
        // 6. 检查库存是否充足
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
        
        // 检查库存 - 使用指定仓库ID
        for (Map.Entry<String, Integer> entry : productQuantityMap.entrySet()) {
            String productId = entry.getKey();
            Integer requiredQuantity = entry.getValue();
            
            // 查询指定仓库的库存
            ProductStock stock = productStockMapper.getByProductIdAndScId(productId, scId);
            if (stock == null || stock.getStockNum() < requiredQuantity) {
                Product product = productMapper.selectById(productId);
                throw new DefaultClientException("商品[" + product.getName() + "]在所选仓库中库存不足，请先进行采购补充库存");
            }
        }
        
        // 7. 创建发料出库单
        MaterialOrder materialOrder = new MaterialOrder();
        materialOrder.setId(IdUtil.getId());
        // 生成发料出库单号：FL + 日期 + 6位随机数
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomStr = String.format("%06d", (int)(Math.random() * 1000000));
        materialOrder.setCode("FL" + dateStr + randomStr);
        materialOrder.setTotalNum(0); // 初始化总数量，后续累加
        materialOrder.setTotalAmount(BigDecimal.ZERO); // 初始化总金额，后续累加
        materialOrder.setDescription("任务发料出库，任务ID：" + taskId);
        materialOrder.setCreateBy(SecurityUtil.getCurrentUser().getId());
        materialOrder.setCreateById(SecurityUtil.getCurrentUser().getId());
        materialOrder.setCreateTime(LocalDateTime.now());
        materialOrder.setDescription(remark);
        materialOrder.setScId(scId); // 设置仓库ID
        materialOrder.setMaterialApplyId(materialApply.getId());
        
        // 8. 创建发料出库明细并扣减库存
        List<MaterialOrderDetail> details = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        int totalNum = 0;
        
        // 处理必换件
        for (ContractTaskWorkCardProduct part : replacementParts) {
            String productId = part.getProductId();
            int quantity = part.getQuantity();
            
            // 获取指定仓库的库存信息
            ProductStock stock = productStockMapper.getByProductIdAndScId(productId, scId);
            BigDecimal taxPrice = stock.getTaxPrice();
            BigDecimal taxAmount = taxPrice.multiply(new BigDecimal(quantity));
            
            // 创建出库明细
            MaterialOrderDetail detail = new MaterialOrderDetail();
            detail.setId(IdUtil.getId());
            detail.setOrderId(materialOrder.getId());
            detail.setProductId(productId);
            detail.setOutNum(quantity);
            detail.setOrderNum(quantity);
            detail.setTaxPrice(taxPrice);
            detail.setTaxAmount(taxAmount);
            detail.setDescription("必换件出库");
            
            details.add(detail);
            totalAmount = totalAmount.add(taxAmount);
            totalNum += quantity;
            
            // 扣减库存 - 使用指定仓库ID
            // 直接使用Mapper调用扣减库存
            productStockMapper.subStock(productId, scId, quantity, 
                taxAmount, stock.getStockNum(), stock.getTaxAmount(), true);
                
            // 添加库存变动记录
            createProductStockLog(scId, productId, quantity, stock, taxAmount, materialOrder, detail.getId());
        }
        
        // 处理非必换件
        for (ContractTaskNonPartProduct part : nonReplacementParts) {
            String productId = part.getProductId();
            int quantity = part.getQuantity();
            
            // 获取指定仓库的库存信息
            ProductStock stock = productStockMapper.getByProductIdAndScId(productId, scId);
            BigDecimal taxPrice = stock.getTaxPrice();
            BigDecimal taxAmount = taxPrice.multiply(new BigDecimal(quantity));
            
            // 创建出库明细
            MaterialOrderDetail detail = new MaterialOrderDetail();
            detail.setId(IdUtil.getId());
            detail.setOrderId(materialOrder.getId());
            detail.setProductId(productId);
            detail.setOutNum(quantity);
            detail.setOrderNum(quantity);
            detail.setTaxPrice(taxPrice);
            detail.setTaxAmount(taxAmount);
            detail.setDescription("非必换件出库");
            
            details.add(detail);
            totalAmount = totalAmount.add(taxAmount);
            totalNum += quantity;
            
            // 扣减库存 - 使用指定仓库ID
            // 直接使用Mapper调用扣减库存
            productStockMapper.subStock(productId, scId, quantity, 
                taxAmount, stock.getStockNum(), stock.getTaxAmount(), true);
                
            // 添加库存变动记录
            createProductStockLog(scId, productId, quantity, stock, taxAmount, materialOrder, detail.getId());
        }
        
        // 更新发料出库单总金额和总数量
        materialOrder.setTotalAmount(totalAmount);
        materialOrder.setTotalNum(totalNum);
        materialOrder.setTotalOutNum(totalNum);
        materialOrder.setIsOutFinish(Boolean.TRUE);
        
        // 9. 保存发料出库单和明细
        materialOrderMapper.insert(materialOrder);
        // 逐条插入明细记录
        for (MaterialOrderDetail detail : details) {
            materialOrderDetailMapper.insert(detail);
        }
        
        // 10. 更新任务发料状态
        task.setIsMaterialIssued(Boolean.TRUE);
        task.setMaterialStatus(MaterialStatus.COMPLETED.getCode());
        this.updateById(task);
        
        return materialOrder.getId();
    }
    
    /**
     * 创建库存变动日志记录
     * 
     * @param scId 仓库ID
     * @param productId 商品ID
     * @param quantity 出库数量
     * @param stock 库存信息
     * @param taxAmount 出库金额
     * @param materialOrder 出库单
     * @param detailId 出库单明细ID
     */
    private void createProductStockLog(String scId, String productId, int quantity, 
            ProductStock stock, BigDecimal taxAmount, MaterialOrder materialOrder, String detailId) {
        // 添加库存变动记录
        ProductStockLog stockLog = new ProductStockLog();
        stockLog.setId(IdUtil.getId());
        stockLog.setScId(scId);
        stockLog.setProductId(productId);
        stockLog.setOriStockNum(stock.getStockNum());
        stockLog.setCurStockNum(stock.getStockNum() - quantity);
        stockLog.setStockNum(-quantity); // 出库数量为负数
        stockLog.setOriTaxPrice(stock.getTaxPrice());
        stockLog.setCurTaxPrice(stock.getTaxPrice());
        stockLog.setTaxAmount(taxAmount.negate()); // 出库金额为负数
        stockLog.setCreateBy(SecurityUtil.getCurrentUser().getName());
        stockLog.setCreateById(SecurityUtil.getCurrentUser().getId());
        stockLog.setCreateTime(LocalDateTime.now());
        stockLog.setBizId(materialOrder.getId());
        stockLog.setBizDetailId(detailId);
        stockLog.setBizCode(materialOrder.getCode());
        stockLog.setBizType(ProductStockBizType.MATERIAL_ISSUE);
        
        productStockLogMapper.insert(stockLog);
    }
    


    /**
     * 根据合同ID创建合同任务
     *
     * @param contractId 合同ID
     * @return 合同任务ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createContractTask(String contractId) {
        // 查询合同信息
        Contract contract = contractMapper.selectById(contractId);
        if (contract == null) {
            throw new DefaultClientException("合同不存在！");
        }
        
        // 原子化修改合同状态为“任务执行中”，避免并发下重复创建任务
        // 仅当合同状态为 WAIT_CREATE 时才允许更新成功（受影响行数为1）
        int affected = contractMapper.update(null,
            Wrappers.lambdaUpdate(Contract.class)
                .set(Contract::getContractStatus, ContractStatus.TASK_EXECUTING)
                .eq(Contract::getId, contractId)
                .eq(Contract::getContractStatus, ContractStatus.WAIT_CREATE)
        );
        if (affected == 0) {
            // 可能已被其他请求更新或已存在任务
            throw new DefaultClientException("合同状态已变更或任务已生成，请勿重复创建！");
        }

        // 再次防御性检查：是否已存在任务（理论上不应存在）
        LambdaQueryWrapper<ContractTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ContractTask::getContractId, contractId);
        if (contractTaskMapper.selectCount(queryWrapper) > 0) {
            throw new DefaultClientException("当前合同已存在任务，请勿重复创建！");
        }

        // 创建合同任务
        ContractTask task = new ContractTask();
        task.setId(IdUtil.getId());
        task.setContractId(contractId);
        
        // 根据合同类型设置任务类型
        Integer contractTypeCode = contract.getContractType();
        if (ContractType.AVIATION.getCode().equals(contractTypeCode)) {
            task.setTaskType(TaskType.AVIATION.getCode());
        } else if (ContractType.RECEIVE_WB.getCode().equals(contractTypeCode)) {
            task.setTaskType(TaskType.RECEIVE_WB.getCode());
        } else if (ContractType.RECEIVE_L.getCode().equals(contractTypeCode)) {
            task.setTaskType(TaskType.RECEIVE_L.getCode());
        } else {
            throw new DefaultClientException("不支持的合同类型！");
        }
        
        // 设置任务状态为待技术评估
        String taskStatusCode = TaskStatus.WAIT_EVALUATION.getCode();
        log.info("创建合同任务，任务状态为：{}", taskStatusCode);
        task.setTaskStatus(taskStatusCode);

        // 设置任务维修状态
        task.setRepairStatus(RepairStatus.WAIT_CHECK.getCode());
        // 设置初始航材状态为待备料
        task.setMaterialStatus(MaterialStatus.PENDING_PREPARATION.getCode());
        // 录入检查记录
        ContractTaskRepairStatusRecord taskRecord = new ContractTaskRepairStatusRecord();
        taskRecord.setRepairStatus(RepairStatus.WAIT_CHECK.getCode());
        taskRecord.setTaskId(task.getId());
        contractTaskRepairStatusRecordMapper.insert(taskRecord);

        // 保存合同任务
        this.save(task);
        
        // 录入任务工卡和任务必换件
        addTaskWorkCardsAndProducts(task.getId(), contract);
        
        return task.getId();
    }
    
    /**
     * 为任务录入匹配的工卡和必换件
     * 
     * @param taskId 任务ID
     * @param contract 合同信息
     */
    private void addTaskWorkCardsAndProducts(String taskId, Contract contract) {
        // 获取合同的维修类型列表
        List<ContractRepair> contractRepairs = contractRepairMapper.selectList(
            Wrappers.lambdaQuery(ContractRepair.class)
                .eq(ContractRepair::getContractId, contract.getId())
        );
        
        if (CollectionUtil.isEmpty(contractRepairs)) {
            log.warn("合同{}没有维修类型信息，无法匹配工卡", contract.getId());
            return;
        }
        
        // 获取合同的件号和客户ID
        String partNumberId = contract.getPartNumberId();
        String customerId = contract.getCustomerId();
        
        // 收集所有维修类型ID
        List<String> repairTypeIds = contractRepairs.stream()
            .map(ContractRepair::getRepairTypeId)
            .collect(Collectors.toList());
        
        // 查找匹配的工卡：维修类型、件号都匹配的工卡
        List<WorkCard> matchedWorkCards = workCardMapper.selectList(
            Wrappers.lambdaQuery(WorkCard.class)
                .in(WorkCard::getRepairTypeId, repairTypeIds)
                .eq(WorkCard::getPartNumberId, partNumberId)
                .eq(WorkCard::getAvailable, true) // 只选择可用的工卡
        );
        
        if (CollectionUtil.isEmpty(matchedWorkCards)) {
            log.warn("未找到与任务{}匹配的工卡", taskId);
            return;
        }
        
        log.info("为任务{}找到{}张匹配的工卡", taskId, matchedWorkCards.size());
        
        // 为每个匹配的工卡创建任务工卡记录，并录入其必换件
        for (WorkCard workCard : matchedWorkCards) {
            // 创建任务工卡记录
            ContractTaskWorkCard taskWorkCard = new ContractTaskWorkCard();
            taskWorkCard.setId(IdUtil.getId());
            taskWorkCard.setTaskId(taskId);
            taskWorkCard.setWorkCardId(workCard.getId());
            
            // 保存任务工卡
            contractTaskWorkCardMapper.insert(taskWorkCard);
            
            // 查询工卡的必换件
            List<WorkCardProduct> workCardProducts = workCardProductMapper.selectList(
                Wrappers.lambdaQuery(WorkCardProduct.class)
                    .eq(WorkCardProduct::getWorkCardId, workCard.getId())
            );
            
            if (CollectionUtil.isNotEmpty(workCardProducts)) {
                // 为每个必换件创建任务必换件记录
                for (WorkCardProduct product : workCardProducts) {
                    ContractTaskWorkCardProduct taskProduct = new ContractTaskWorkCardProduct();
                    taskProduct.setId(IdUtil.getId());
                    taskProduct.setTaskId(taskId);
                    taskProduct.setProductId(product.getProductId());
                    taskProduct.setQuantity(product.getQuantity() != null ? product.getQuantity() : 1); // 如果数量为空，默认为1
                    taskProduct.setWorkCardId(workCard.getId());
                    
                    // 保存任务必换件
                    contractTaskWorkCardProductMapper.insert(taskProduct);
                }
                
                log.info("为任务{}的工卡{}录入了{}个必换件", taskId, workCard.getId(), workCardProducts.size());
            } else {
                log.warn("工卡{}没有必换件", workCard.getId());
            }
        }
    }
    
    /**
     * 查询合同任务列表
     *
     * @param pageIndex 页码
     * @param pageSize  每页条数
     * @param vo        参数
     * @return 合同任务列表
     */
    @Override
    public PageResult<QueryContractTaskBo> query(Integer pageIndex, Integer pageSize, QueryContractTaskVo vo) {
        // 分页查询
        PageHelperUtil.startPage(pageIndex, pageSize);
        List<QueryContractTaskBo> datas = getBaseMapper().query(vo);

        // 为每条记录补充集合字段（维修类型、工卡）
        if (CollectionUtil.isNotEmpty(datas)) {
            for (QueryContractTaskBo bo : datas) {
                // 维修类型：按合同维度
                List<QueryContractTaskBo.RepairTypeBo> repairTypes = getBaseMapper().selectRepairTypesByContractId(bo.getContractId());
                bo.setRepairTypes(repairTypes);

                // 工卡：按任务维度
                List<QueryContractTaskBo.WorkCardBo> workCards = getBaseMapper().selectWorkCardsByTaskId(bo.getId());
                bo.setWorkCards(workCards);
            }
        }

        // 处理枚举类型的显示名称和日期字段
        datas.forEach(bo -> {
            // 设置任务类型名称
            if (bo.getTaskType() != null) {
                TaskType taskType = EnumUtil.getByCode(TaskType.class, bo.getTaskType());
                if (taskType != null) {
                    bo.setTaskTypeName(taskType.getDesc());
                }
            }
            
            // 设置任务状态名称
            if (bo.getTaskStatus() != null) {
                TaskStatus taskStatus = EnumUtil.getByCode(TaskStatus.class, bo.getTaskStatus());
                if (taskStatus != null) {
                    bo.setTaskStatusName(taskStatus.getDesc());
                }
            }

            List<QueryContractTaskBo.RepairTypeBo> repairTypes = bo.getRepairTypes();
            String repairTypesLabel = "";
            if (CollectionUtil.isNotEmpty(repairTypes)) {
                for (QueryContractTaskBo.RepairTypeBo repairType : repairTypes) {
                    repairTypesLabel += repairType.getName() + ",";
                }
                repairTypesLabel = repairTypesLabel.substring(0, repairTypesLabel.length() - 1);
            }
            bo.setRepairTypesLabel(repairTypesLabel);

            // 必换件单号
            String replacementPartNumber = bo.getContractCode() + "BHJ";
            String otherReplacementPartNumber = bo.getContractCode() + "FBH";
            bo.setReplacementPartNumber(replacementPartNumber);
            bo.setOtherReplacementPartNumber(otherReplacementPartNumber);

            List<QueryContractTaskBo.WorkCardBo> workCards = bo.getWorkCards();
            String workCardNumberList = "";
            if (CollectionUtil.isNotEmpty(workCards)) {
                for (QueryContractTaskBo.WorkCardBo workCard : workCards) {
                    workCardNumberList += workCard.getCode() + ",";
                }
                workCardNumberList = workCardNumberList.substring(0, workCardNumberList.length() - 1);
            }
            bo.setWorkCardNumberList(workCardNumberList);
            String repairStatus = bo.getRepairStatus();
            if (StringUtils.isNotBlank(repairStatus)) {
                RepairStatus status = EnumUtil.getByCode(RepairStatus.class, repairStatus);
                if (status != null) {
                    bo.setRepairStatusLabel(status.getDesc());
                }
            }

            // 设置航材状态名称
            String materialStatus = bo.getMaterialStatus();
            if (StringUtils.isNotBlank(materialStatus)) {
                MaterialStatus status = EnumUtil.getByCode(MaterialStatus.class, materialStatus);
                if (status != null) {
                    bo.setMaterialStatusName(status.getDesc());
                }
            }
        });
        
        // 创建 PageInfo 对象并返回分页结果
        PageInfo<QueryContractTaskBo> pageInfo = new PageInfo<>(datas);
        return PageResultUtil.convert(pageInfo);
    }

    /**
     * 修改合同任务放行文件编号
     *
     * @param vo 修改放行文件编号参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateApprovalFileNumber(UpdateContractTaskApprovalFileNumberVo vo) {
        ContractTask task = this.getById(vo.getId());
        if (task == null) {
            throw new DefaultClientException("合同任务不存在！");
        }

        task.setApprovalFileNumber(vo.getApprovalFileNumber());
        this.updateById(task);
    }
    
    /**
     * 根据ID获取合同任务详情
     *
     * @param id 合同任务ID
     * @return 合同任务详情
     */
    @Override
    public GetContractTaskBo getDetail(String id) {
        GetContractTaskBo bo = getBaseMapper().getDetail(id);
        if (bo == null) {
            return null;
        }
        
        // 设置任务类型名称
        if (bo.getTaskType() != null) {
            TaskType taskType = EnumUtil.getByCode(TaskType.class, bo.getTaskType());
            if (taskType != null) {
                bo.setTaskTypeName(taskType.getDesc());
            }
        }
        
        // 设置任务状态名称
        if (bo.getTaskStatus() != null) {
            TaskStatus taskStatus = EnumUtil.getByCode(TaskStatus.class, bo.getTaskStatus());
            if (taskStatus != null) {
                bo.setTaskStatusName(taskStatus.getDesc());
            }
        }
        // 设置维修状态名称
        if (bo.getRepairStatus() != null) {
            RepairStatus repairStatus = EnumUtil.getByCode(RepairStatus.class, bo.getRepairStatus());
            if (repairStatus != null) {
                bo.setRepairStatusName(repairStatus.getDesc());
            }
        }

        // 设置航材状态名称
        if (bo.getMaterialStatus() != null) {
            MaterialStatus materialStatus = EnumUtil.getByCode(MaterialStatus.class, bo.getMaterialStatus());
            if (materialStatus != null) {
                bo.setMaterialStatusName(materialStatus.getDesc());
            }
        }

        // 维修类型信息已在XML中关联查询出来，无需再次查询
        
        // 查询任务工卡列表
        List<ContractTaskWorkCard> workCards = contractTaskWorkCardService.getByTaskId(id);
        if (CollectionUtil.isNotEmpty(workCards)) {
            List<ContractTaskWorkCardBo> workCardBos = workCards.stream()
                    .map(ContractTaskWorkCardBo::new)
                    .collect(Collectors.toList());
            bo.setWorkCards(workCardBos);
        }
        
        return bo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UpdateContractTaskVo vo) {
        ContractTask task = this.getById(vo.getId());
        if (task == null) {
            throw new DefaultClientException("合同任务不存在！");
        }

        Contract contract = contractMapper.selectById(task.getContractId());
        if (contract == null) {
            throw new DefaultClientException("合同不存在！");
        }

        Product product = productMapper.selectById(vo.getPartNumberId());
        if (product == null) {
            throw new DefaultClientException("件号对应的航材不存在！");
        }
        if (!Objects.equals(vo.getMachineTypeId(), product.getMachineTypeId())) {
            throw new DefaultClientException("件号与机型不匹配！");
        }
        if (EnumUtil.getByCode(TaskStatus.class, vo.getTaskStatus()) == null) {
            throw new DefaultClientException("任务状态无效！");
        }
        if (EnumUtil.getByCode(TaskType.class, vo.getTaskType()) == null) {
            throw new DefaultClientException("任务类型无效！");
        }

        contract.setPartNumberId(vo.getPartNumberId());
        contract.setSerialNumber(vo.getSerialNumber());
        contract.setOtherRepairRequirements(vo.getOtherRepairRequirements());
        contract.setStorageTime(vo.getStorageTime());
        contract.setPlannedCompletionTime(vo.getPlannedCompletionTime());
        if (contractMapper.updateById(contract) != 1) {
            throw new DefaultClientException("合同信息已过期，请刷新重试！");
        }
        contractRepairService.updateContractRepairs(contract.getId(), vo.getRepairTypeIds());

        if (!Objects.equals(task.getTaskStatus(), vo.getTaskStatus())) {
            UpdateTaskStatusVo statusVo = new UpdateTaskStatusVo();
            statusVo.setTaskId(task.getId());
            statusVo.setTaskStatus(vo.getTaskStatus());
            statusVo.setReason(task.getReturnRepairReason());
            updateTaskStatus(statusVo);
            task = this.getById(vo.getId());
        }

        task.setTaskType(vo.getTaskType());
        task.setDescription(vo.getDescription());
        if (!this.updateById(task)) {
            throw new DefaultClientException("合同任务信息已过期，请刷新重试！");
        }
    }
    
    /**
     * 线下鉴定
     *
     * @param vo 线下鉴定参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void offlineAppraisal(OfflineAppraisalVo vo) {
        // 查询合同任务
        ContractTask task = this.getById(vo.getId());
        if (task == null) {
            throw new DefaultClientException("合同任务不存在！");
        }
        
        // 检查任务状态是否为待技术评估
        if (!TaskStatus.WAIT_EVALUATION.getCode().equals(task.getTaskStatus())) {
            throw new DefaultClientException("当前任务状态不是待技术评估，无法进行线下鉴定！");
        }
        
        // 根据鉴定结果更新任务状态
        if (vo.getApproved()) {
            // 通过：更新任务状态为待派发
            task.setTaskStatus(TaskStatus.WAIT_DISPATCH.getCode());
        } else {
            // 不通过：更新任务状态为退回
            task.setTaskStatus(TaskStatus.RETURNED.getCode());
            
            // 同时更新合同状态为合同关闭
            Contract contract = contractMapper.selectById(task.getContractId());
            if (contract != null) {
                contract.setContractStatus(ContractStatus.CONTRACT_CLOSE);
                contractMapper.updateById(contract);
            }
        }
        
        // 更新任务备注
        if (vo.getDescription() != null && !vo.getDescription().isEmpty()) {
            task.setDescription(vo.getDescription());
        }
        
        // 保存任务更新
        this.updateById(task);
        
        log.info("线下鉴定完成，任务ID：{}，鉴定结果：{}", vo.getId(), vo.getApproved() ? "通过" : "不通过");
    }
    
    /**
     * 任务派发
     *
     * @param vo 任务派发参数
     * @return 新创建的合同ID，如果没有创建新合同则返回null
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String dispatchTask(DispatchTaskVo vo) {
        // 查询合同任务
        ContractTask task = this.getById(vo.getId());
        if (task == null) {
            throw new DefaultClientException("合同任务不存在！");
        }
        
        // 检查任务状态是否为待派发
        if (!TaskStatus.WAIT_DISPATCH.getCode().equals(task.getTaskStatus())) {
            throw new DefaultClientException("当前任务状态不是待派发，无法进行派发操作！");
        }
        
        // 获取任务类型
        String taskType = task.getTaskType();
        
        // 获取合同信息
        Contract contract = contractMapper.selectById(task.getContractId());
        if (contract == null) {
            throw new DefaultClientException("合同不存在！");
        }
        
        // 更新派发信息
        task.setTaskUserId(vo.getTaskUserId());
        
        // 判断任务类型和用户单位编码
        boolean isReturnedTask = false;
        String newContractId = null;
        
        // 获取用户信息
        SysUser user = sysUserMapper.findById(vo.getTaskUserId());
        if (user == null) {
            throw new DefaultClientException("派发的用户不存在！");
        }
        
        // 获取用户的单位编码
        String unitCode = user.getUnitCode();
        task.setDispatch(unitCode);
        // 如果是返厂WB或L任务，且派发的用户单位编码是SH开头的，则任务状态改为退回
        if ((TaskType.RECEIVE_WB.getCode().equals(taskType) || TaskType.RECEIVE_L.getCode().equals(taskType)) 
                && unitCode != null && unitCode.startsWith("SH")) {
            task.setTaskStatus(TaskStatus.RETURNED.getCode());
            isReturnedTask = true;
            
            // 更新合同状态为合同关闭
            contract.setContractStatus(ContractStatus.CONTRACT_CLOSE);
            contractMapper.updateById(contract);
            
            // 创建新合同
            newContractId = createNewContract(contract, task.getId());
            
            // 更新任务的shContractId字段
            task.setShContractId(newContractId);
        } else {
            // 其他情况为维修执行状态
            task.setTaskStatus(TaskStatus.EXECUTION.getCode());
        }
        
        // 保存任务更新
        this.updateById(task);
        
        log.info("任务派发完成，任务ID：{}，派发给用户ID：{}，任务状态：{}", 
                vo.getId(), vo.getTaskUserId(), 
                isReturnedTask ? "退回" : "维修执行");
        
        return newContractId;
    }
    
    /**
     * 创建新合同
     * 
     * @param sourceContract 源合同
     * @param taskId 任务ID
     * @return 新合同ID
     */
    /**
     * 修改合同任务状态
     *
     * @param vo 修改任务状态参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTaskStatus(UpdateTaskStatusVo vo) {
        // 获取任务信息
        ContractTask task = this.getById(vo.getTaskId());
        if (task == null) {
            throw new DefaultClientException("合同任务不存在！");
        }
        
        // 更新任务状态
        task.setTaskStatus(vo.getTaskStatus());
        // 如果是退修状态且提供了退修原因，则记录退修原因
        if (TaskStatus.RETURNED.getCode().equals(vo.getTaskStatus())) {
            task.setReturnRepairReason(vo.getReason());
            // 同时将合同记录为任务退修状态
            Contract contract = contractMapper.selectById(task.getContractId());
            if(contract == null) {
                throw new DefaultClientException("合同不存在！");
            }
            contract.setContractStatus(ContractStatus.TASK_RETURN);
            contractMapper.updateById( contract);
        } else if (TaskStatus.CLOSED.getCode().equals(vo.getTaskStatus())) {
            // 若任务状态更新为 任务关闭，同步将合同状态置为 任务关闭
            Contract contract = contractMapper.selectById(task.getContractId());
            if (contract == null) {
                throw new DefaultClientException("合同不存在！");
            }
            contract.setContractStatus(ContractStatus.TASK_CLOSE);
            contractMapper.updateById(contract);
        }
        this.updateById(task);
        
        log.info("合同任务状态已更新，任务ID：{}，新状态：{}", vo.getTaskId(), vo.getTaskStatus());
    }
    
    private String createNewContract(Contract sourceContract, String taskId) {
        // 创建新合同对象
        Contract newContract = new Contract();
        
        // 设置新合同ID
        String newContractId = IdUtil.getId();
        newContract.setId(newContractId);
        
        // 复制原合同信息
        newContract.setPartNumberId(sourceContract.getPartNumberId());
        newContract.setContractTime(sourceContract.getContractTime());
        newContract.setCustomerId(sourceContract.getCustomerId());
        newContract.setSerialNumber(sourceContract.getSerialNumber());
        newContract.setOtherRepairRequirements(sourceContract.getOtherRepairRequirements());
        newContract.setStorageTime(sourceContract.getStorageTime());
        newContract.setPlannedCompletionTime(sourceContract.getPlannedCompletionTime());
        newContract.setDeliveryTime(sourceContract.getDeliveryTime());
        newContract.setContractPrice(sourceContract.getContractPrice());
        newContract.setReplacementPartPrice(sourceContract.getReplacementPartPrice());
        newContract.setContractType(sourceContract.getContractType());
        newContract.setAvailable(true);
        
        // 生成新的合同编号（原编号 + 3位随机数）
        Random random = new Random();
        int randomNum = random.nextInt(900) + 100; // 生成100-999之间的随机数
        newContract.setCode(sourceContract.getCode() + randomNum);
        
        // 设置新的合同名称（原名称 + "复制"）
        newContract.setName(sourceContract.getName() + "复制");
        
        // 设置合同状态为待生成合同任务
        newContract.setContractStatus(ContractStatus.WAIT_CREATE);
        
        // 设置来源任务ID
        newContract.setFromContractTaskId(taskId);
        
        // 保存新合同
        contractMapper.insert(newContract);
        
        // 复制合同维修类型
        List<ContractRepair> contractRepairs = contractRepairMapper.selectList(
                new LambdaQueryWrapper<ContractRepair>().eq(ContractRepair::getContractId, sourceContract.getId()));
        if (CollectionUtil.isNotEmpty(contractRepairs)) {
            // 创建新合同的维修类型关联
            for (ContractRepair repair : contractRepairs) {
                ContractRepair newRepair = new ContractRepair();
                newRepair.setId(IdUtil.getId());
                newRepair.setContractId(newContractId);
                newRepair.setRepairTypeId(repair.getRepairTypeId());
                contractRepairMapper.insert(newRepair);
            }
        }
        
        log.info("创建新合同成功，ID：{}，编号：{}", newContractId, newContract.getCode());
        
        return newContractId;
    }
}
