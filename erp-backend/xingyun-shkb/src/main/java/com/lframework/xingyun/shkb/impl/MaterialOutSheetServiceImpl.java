package com.lframework.xingyun.shkb.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageInfo;
import com.lframework.starter.common.constants.StringPool;
import com.lframework.starter.common.exceptions.impl.InputErrorException;
import com.lframework.starter.common.utils.Assert;
import com.lframework.starter.common.utils.CollectionUtil;
import com.lframework.starter.common.utils.NumberUtil;
import com.lframework.starter.common.utils.StringUtil;
import com.lframework.starter.web.core.components.security.SecurityUtil;
import com.lframework.starter.web.core.impl.BaseMpServiceImpl;
import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.utils.IdUtil;
import com.lframework.starter.web.core.utils.PageHelperUtil;
import com.lframework.starter.web.core.utils.PageResultUtil;
import com.lframework.xingyun.basedata.entity.Product;
import com.lframework.xingyun.basedata.entity.StoreCenter;
import com.lframework.xingyun.basedata.entity.Supplier;
import com.lframework.xingyun.basedata.service.product.ProductService;
import com.lframework.xingyun.basedata.service.product.ProductCategoryService;
import com.lframework.xingyun.basedata.service.product.ProductBrandService;
import com.lframework.xingyun.basedata.entity.ProductCategory;
import com.lframework.xingyun.basedata.entity.ProductBrand;
import com.lframework.xingyun.basedata.service.storecenter.StoreCenterService;
import com.lframework.xingyun.basedata.service.supplier.SupplierService;
import com.lframework.xingyun.core.annotations.OpLog;
import com.lframework.xingyun.core.annotations.OrderTimeLineLog;
import com.lframework.xingyun.core.enums.DefaultOpLogType;
import com.lframework.xingyun.core.enums.OrderTimeLineBizType;
import com.lframework.xingyun.core.service.GenerateCodeService;
import com.lframework.xingyun.core.utils.OpLogUtil;
import com.lframework.xingyun.shkb.components.code.GenerateCodeTypePool;
import com.lframework.xingyun.shkb.dto.material.out.MaterialOutSheetFullDto;
import com.lframework.xingyun.shkb.dto.material.out.MaterialOutSheetDetailDto;
import com.lframework.xingyun.shkb.dto.material.out.MaterialOutSheetDetailLotDto;
import com.lframework.xingyun.shkb.dto.material.out.MaterialOutSheetDetailSerialDto;
import com.lframework.xingyun.shkb.entity.MaterialOutSheet;
import com.lframework.xingyun.shkb.entity.MaterialOrder;
import com.lframework.xingyun.shkb.bo.material.out.QueryMaterialOutSheetBo;
import com.lframework.xingyun.shkb.entity.MaterialOutSheetDetail;
import com.lframework.xingyun.shkb.entity.MaterialOutSheetDetailSerial;
import com.lframework.xingyun.shkb.entity.MaterialOrderDetail;
import com.lframework.xingyun.shkb.enums.MaterialOutSheetStatus;
import com.lframework.xingyun.shkb.enums.MaterialStatus;
import com.lframework.xingyun.shkb.entity.ContractTaskMaterialApply;
import com.lframework.xingyun.shkb.entity.ContractTask;
import com.lframework.xingyun.shkb.mappers.MaterialOutSheetMapper;
import com.lframework.xingyun.shkb.mappers.MaterialOrderDetailMapper;
import com.lframework.xingyun.shkb.mappers.MaterialOrderMapper;
import com.lframework.xingyun.shkb.service.MaterialOutSheetDetailService;
import com.lframework.xingyun.shkb.service.MaterialOutSheetDetailSerialService;
import com.lframework.xingyun.shkb.service.MaterialOrderDetailService;
import com.lframework.xingyun.shkb.service.MaterialOrderService;
import com.lframework.xingyun.sc.entity.ProductStockBatch;
import com.lframework.xingyun.sc.entity.ProductStockSerial;
import com.lframework.xingyun.sc.service.stock.ProductStockBatchService;
import com.lframework.xingyun.sc.service.stock.ProductStockSerialService;
import com.lframework.xingyun.shkb.service.MaterialOutSheetService;
import com.lframework.xingyun.shkb.bo.material.out.BatchStockBo;
import com.lframework.xingyun.shkb.bo.material.out.SerialStockBo;
import com.lframework.xingyun.sc.service.stock.ProductStockService;
import com.lframework.xingyun.sc.vo.stock.SubProductStockVo;
import com.lframework.xingyun.sc.vo.stock.SubProductStockBatchVo;
import com.lframework.xingyun.sc.enums.ProductStockBizType;
import com.lframework.xingyun.shkb.vo.material.out.ApprovePassMaterialOutSheetVo;
import com.lframework.xingyun.shkb.vo.material.out.ApproveRefuseMaterialOutSheetVo;
import com.lframework.xingyun.shkb.vo.material.out.CreateMaterialOutSheetVo;
import com.lframework.xingyun.shkb.vo.material.out.QueryMaterialOutSheetVo;
import com.lframework.xingyun.shkb.vo.material.out.UpdateMaterialOutSheetVo;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.TreeSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* @author kison
* @description 针对表【tbl_material_out_sheet(发料出库单)】的数据库操作Service实现
* @createDate 2025-08-10 19:06:04
*/
@Service
public class MaterialOutSheetServiceImpl extends BaseMpServiceImpl<MaterialOutSheetMapper, MaterialOutSheet>
    implements MaterialOutSheetService{

    @Autowired
    private MaterialOutSheetDetailService materialOutSheetDetailService;

    @Autowired
    private MaterialOutSheetDetailSerialService materialOutSheetDetailSerialService;

    @Autowired
    private MaterialOrderDetailService materialOrderDetailService;

    @Autowired
    private MaterialOrderService materialOrderService;

    @Autowired
    private MaterialOrderMapper materialOrderMapper;

    @Autowired
    private MaterialOrderDetailMapper materialOrderDetailMapper;

    @Autowired
    private ProductStockBatchService productStockBatchService;

    @Autowired
    private ProductStockService productStockService;

    @Autowired
    private ProductStockSerialService productStockSerialService;

    @Autowired
    private StoreCenterService storeCenterService;

    @Autowired
    private SupplierService supplierService;



    @Autowired
    private ProductService productService;

    @Autowired
    private ProductCategoryService productCategoryService;

    @Autowired
    private ProductBrandService productBrandService;

    

    @Autowired
    private GenerateCodeService generateCodeService;

    @Autowired
    private MaterialOutSheetService materialOutSheetService;

    @Override
    public PageResult<MaterialOutSheet> query(Integer pageIndex, Integer pageSize, QueryMaterialOutSheetVo vo) {
        Assert.greaterThanZero(pageIndex);
        Assert.greaterThanZero(pageSize);

        PageHelperUtil.startPage(pageIndex, pageSize);
        List<MaterialOutSheet> datas = this.query(vo);

        return PageResultUtil.convert(new PageInfo<>(datas));
    }

    @Override
    public PageResult<QueryMaterialOutSheetBo> queryList(Integer pageIndex, Integer pageSize, QueryMaterialOutSheetVo vo) {
        Assert.greaterThanZero(pageIndex);
        Assert.greaterThanZero(pageSize);

        PageHelperUtil.startPage(pageIndex, pageSize);
        List<QueryMaterialOutSheetBo> datas = this.queryList(vo);

        return PageResultUtil.convert(new PageInfo<>(datas));
    }

    @Override
    public List<MaterialOutSheet> query(QueryMaterialOutSheetVo vo) {
        return getBaseMapper().query(vo);
    }

    @Override
    public List<QueryMaterialOutSheetBo> queryList(QueryMaterialOutSheetVo vo) {
        return getBaseMapper().queryList(vo);
    }

    @Override
    public MaterialOutSheetFullDto getDetail(String id) {
        // 直接由Mapper通过嵌套映射返回完整数据结构
        return getBaseMapper().getDetail(id);
    }

    @OpLog(type = DefaultOpLogType.OTHER, name = "创建发料出库单，单号：{}", params = "#code")
    @OrderTimeLineLog(type = OrderTimeLineBizType.CREATE, orderId = "#_result", name = "创建发料出库单")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String create(CreateMaterialOutSheetVo vo) {
        // 按新规则：创建时不做库存及数量校验，校验移动到审核通过时
        
        MaterialOutSheet sheet = new MaterialOutSheet();
        sheet.setId(IdUtil.getId());
        sheet.setCode(generateCodeService.generate(GenerateCodeTypePool.MATERIAL_OUT_SHEET));

        this.create(sheet, vo);

        // 创建后状态为“备料中”
        sheet.setStatus(MaterialOutSheetStatus.PREPARING.getCode());

        OpLogUtil.setVariable("code", sheet.getCode());
        OpLogUtil.setExtra(vo);

        getBaseMapper().insert(sheet);

        // 更新合同任务航材状态：如果是待备料或空则改为备料中
        if (!StringUtil.isBlank(vo.getMaterialOrderId())) {
            MaterialOrder materialOrder = materialOrderService.getById(vo.getMaterialOrderId());
            if (materialOrder != null && !StringUtil.isBlank(materialOrder.getMaterialApplyId())) {
                ContractTaskMaterialApply materialApply = getBaseMapper().getContractTaskMaterialApplyById(materialOrder.getMaterialApplyId());
                if (materialApply != null && !StringUtil.isBlank(materialApply.getTaskId())) {
                    ContractTask contractTask = getBaseMapper().getContractTaskById(materialApply.getTaskId());
                    if (contractTask != null && (StringUtil.isBlank(contractTask.getMaterialStatus()) || MaterialStatus.PENDING_PREPARATION.getCode().equals(contractTask.getMaterialStatus()))) {
                        contractTask.setMaterialStatus(MaterialStatus.PREPARING.getCode());
                        getBaseMapper().updateContractTask(contractTask.getId(), MaterialStatus.PREPARING.getCode());
                    }
                }
            }
        }

        return sheet.getId();
    }

    @OpLog(type = DefaultOpLogType.OTHER, name = "修改发料出库单，单号：{}", params = "#code")
    @OrderTimeLineLog(type = OrderTimeLineBizType.UPDATE, orderId = "#vo.id", name = "修改发料出库单")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update(UpdateMaterialOutSheetVo vo) {
        // 锁定单据，避免修改与审批并发覆盖状态或明细。
        MaterialOutSheet sheet = getBaseMapper().selectByIdForUpdate(vo.getId());
        if (sheet == null) {
            throw new InputErrorException("发料出库单不存在！");
        }

        // 允许“备料中”和“可领料”状态进行修改，其他状态不允许
        MaterialOutSheetRules.requireUpdatable(sheet.getStatus());

        // 删除明细
        Wrapper<MaterialOutSheetDetail> deleteDetailWrapper = Wrappers.<MaterialOutSheetDetail>lambdaQuery()
                .eq(MaterialOutSheetDetail::getSheetId, sheet.getId());
        materialOutSheetDetailService.remove(deleteDetailWrapper);

        // 删除序列号明细
        Wrapper<MaterialOutSheetDetailSerial> deleteSerialWrapper = Wrappers.<MaterialOutSheetDetailSerial>lambdaQuery()
                .eq(MaterialOutSheetDetailSerial::getSheetId, sheet.getId());
        materialOutSheetDetailSerialService.remove(deleteSerialWrapper);

        this.createFromUpdate(sheet, vo);

        // 修改后重新回到“备料中”状态
        sheet.setStatus(MaterialOutSheetStatus.PREPARING.getCode());

        LambdaUpdateWrapper<MaterialOutSheet> updateWrapper = Wrappers.<MaterialOutSheet>lambdaUpdate()
                .set(MaterialOutSheet::getApproveBy, null).set(MaterialOutSheet::getApproveTime, null)
                .set(MaterialOutSheet::getRefuseReason, null).eq(MaterialOutSheet::getId, sheet.getId());
        getBaseMapper().update(sheet, updateWrapper);

        OpLogUtil.setVariable("code", sheet.getCode());
        OpLogUtil.setExtra(vo);
    }

    @OpLog(type = DefaultOpLogType.OTHER, name = "发料出库单操作发料，单号：{}", params = "#code")
    @OrderTimeLineLog(type = OrderTimeLineBizType.APPROVE_PASS, orderId = "#vo.id", name = "操作发料")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void approvePass(ApprovePassMaterialOutSheetVo vo) {
        // 锁定单据，保证重复审批只能有一个事务进入库存扣减阶段。
        MaterialOutSheet sheet = getBaseMapper().selectByIdForUpdate(vo.getId());
        if (sheet == null) {
            throw new InputErrorException("发料出库单不存在！");
        }

        // 仅允许“备料中”和“可领料”两种状态执行发料
        MaterialOutSheetRules.requireApprovable(sheet.getStatus());

        // 同一发料单下的多张出库单必须串行更新明细及主表汇总，避免 totalOutNum 丢更新。
        MaterialOrder lockedMaterialOrder = null;
        List<MaterialOrderDetail> lockedOrderDetails = new ArrayList<>();
        if (!StringUtil.isBlank(sheet.getMaterialOrderId())) {
            lockedMaterialOrder = materialOrderMapper.selectByIdForUpdate(sheet.getMaterialOrderId());
            if (lockedMaterialOrder == null) {
                throw new InputErrorException("发料单不存在！");
            }
            // 按主键有序锁定本单引用的发料明细；规避批量锁 SQL 被全局排序器改写，
            // 同时以固定加锁顺序降低同一发料单并发审批时的死锁风险。
            lockedOrderDetails = this.lockMaterialOrderDetails(sheet);
        }

        // 审核前进行各类校验
        this.validateOnApprove(sheet, lockedOrderDetails);

        // 审核通过后状态为“已发料”
        sheet.setStatus(MaterialOutSheetStatus.ISSUED.getCode());
        sheet.setApproveBy(SecurityUtil.getCurrentUser().getId());
        sheet.setApproveTime(LocalDateTime.now());
        sheet.setDescription(StringUtil.isBlank(vo.getDescription()) ? StringPool.EMPTY_STR : vo.getDescription());

        // 审核通过时更新库存及发料单已出库数量
        this.updateStockOnApprove(sheet, lockedMaterialOrder, lockedOrderDetails);

        getBaseMapper().updateById(sheet);

        OpLogUtil.setVariable("code", sheet.getCode());
        OpLogUtil.setExtra(vo);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void directApprovePass(CreateMaterialOutSheetVo vo) {
        String id = this.create(vo);

        ApprovePassMaterialOutSheetVo approveVo = new ApprovePassMaterialOutSheetVo();
        approveVo.setId(id);

        this.approvePass(approveVo);
    }

    // 标记发料出库单为“可领料”状态
    @OpLog(type = DefaultOpLogType.OTHER, name = "将发料出库单标记为可领料，单号：{}", params = "#code")
    @OrderTimeLineLog(type = OrderTimeLineBizType.APPROVE_RETURN, orderId = "#vo.id", name = "标记为可领料，备注：{}", params = "#vo.refuseReason")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void markPickable(ApproveRefuseMaterialOutSheetVo vo) {
        // 与修改、审批共用单据行锁，防止审批扣库后被并发覆盖回“可领料”。
        MaterialOutSheet sheet = getBaseMapper().selectByIdForUpdate(vo.getId());
        if (sheet == null) {
            throw new InputErrorException("发料出库单不存在！");
        }

        MaterialOutSheetRules.requirePickable(sheet.getStatus());

        // 标记后状态为“可领料”
        sheet.setStatus(MaterialOutSheetStatus.PICKABLE.getCode());
        sheet.setApproveBy(SecurityUtil.getCurrentUser().getId());
        sheet.setRefuseReason(vo.getRefuseReason());

        getBaseMapper().updateById(sheet);

        OpLogUtil.setVariable("code", sheet.getCode());
        OpLogUtil.setExtra(vo);
    }

    @OpLog(type = DefaultOpLogType.OTHER, name = "删除发料出库单，单号：{}", params = "#code")
    @OrderTimeLineLog(type = OrderTimeLineBizType.UPDATE, orderId = "#id", name = "删除发料出库单")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteById(String id) {
        Assert.notBlank(id);
        // 锁定后再判断状态，避免审批与删除并发造成库存已扣但单据被删除。
        MaterialOutSheet sheet = getBaseMapper().selectByIdForUpdate(id);
        if (sheet == null) {
            throw new InputErrorException("发料出库单不存在！");
        }

        MaterialOutSheetRules.requireDeletable(sheet.getStatus());

        // 删除明细
        Wrapper<MaterialOutSheetDetail> deleteDetailWrapper = Wrappers.<MaterialOutSheetDetail>lambdaQuery()
                .eq(MaterialOutSheetDetail::getSheetId, sheet.getId());
        materialOutSheetDetailService.remove(deleteDetailWrapper);

        // 删除序列号明细
        Wrapper<MaterialOutSheetDetailSerial> deleteSerialWrapper = Wrappers.<MaterialOutSheetDetailSerial>lambdaQuery()
                .eq(MaterialOutSheetDetailSerial::getSheetId, sheet.getId());
        materialOutSheetDetailSerialService.remove(deleteSerialWrapper);

        // 删除发料出库单
        getBaseMapper().deleteById(id);

        OpLogUtil.setVariable("code", sheet.getCode());
    }

    /**
     * 创建发料出库单
     */
    private void create(MaterialOutSheet sheet, CreateMaterialOutSheetVo vo) {
        StoreCenter sc = storeCenterService.findById(vo.getScId());
        if (sc == null) {
            throw new InputErrorException("仓库不存在！");
        }

        sheet.setScId(vo.getScId());
        sheet.setSupplierId(vo.getSupplierId());
        sheet.setMaterialUserId(vo.getMaterialUserId());
        sheet.setMaterialDate(vo.getMaterialDate());
        sheet.setMaterialOrderId(vo.getMaterialOrderId());
        sheet.setDescription(StringUtil.isBlank(vo.getDescription()) ? StringPool.EMPTY_STR : vo.getDescription());

        int totalNum = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;

        int orderNo = 1;
        for (CreateMaterialOutSheetVo.CreateMaterialOutSheetDetailVo detailVo : vo.getDetails()) {
            MaterialOutSheetDetail detail = new MaterialOutSheetDetail();
            detail.setId(IdUtil.getId());
            detail.setSheetId(sheet.getId());

            Product product = productService.findById(detailVo.getProductId());
            if (product == null) {
                throw new InputErrorException("第" + orderNo + "行航材不存在！");
            }

            detail.setProductId(detailVo.getProductId());
            detail.setOutNum(detailVo.getOutNum());
            detail.setOrderNum(detailVo.getOrderNum());
            detail.setTaxPrice(detailVo.getTaxPrice() == null ? BigDecimal.ZERO : detailVo.getTaxPrice());
            detail.setTaxAmount(NumberUtil.mul(detail.getTaxPrice(), detail.getOutNum()));
            detail.setDescription(StringUtil.isBlank(detailVo.getDescription()) ? StringPool.EMPTY_STR : detailVo.getDescription());
            detail.setOrderNo(orderNo);
            detail.setSerialNumbers(detailVo.getSerialNumbers());
            // 记录发料单明细ID（如有）
            detail.setMaterialOrderDetailId(detailVo.getMaterialOrderDetailId());
            
            // 设置批次库存ID
            if (!StringUtil.isBlank(detailVo.getStockBatchId())) {
                detail.setStockBatchId(detailVo.getStockBatchId());
            }

            materialOutSheetDetailService.save(detail);

            // 处理序列号库存关系
            if (!CollectionUtil.isEmpty(detailVo.getSerials())) {
                for (String stockSerialId : detailVo.getSerials()) {
                    MaterialOutSheetDetailSerial detailSerial = new MaterialOutSheetDetailSerial();
                    detailSerial.setId(IdUtil.getId());
                    detailSerial.setSheetId(sheet.getId());
                    detailSerial.setProductId(detailVo.getProductId());
                    detailSerial.setStockSerialId(stockSerialId);
                    
                    materialOutSheetDetailSerialService.save(detailSerial);
                }
            }

            totalNum += detail.getOutNum();
            totalAmount = NumberUtil.add(totalAmount, detail.getTaxAmount());

            orderNo++;
        }

        sheet.setTotalNum(totalNum);
        sheet.setTotalAmount(totalAmount);
    }

    /**
     * 修改发料出库单
     */
    private void createFromUpdate(MaterialOutSheet sheet, UpdateMaterialOutSheetVo vo) {
        StoreCenter sc = storeCenterService.findById(vo.getScId());
        if (sc == null) {
            throw new InputErrorException("仓库不存在！");
        }

        sheet.setScId(vo.getScId());
        sheet.setSupplierId(vo.getSupplierId());
        sheet.setMaterialUserId(vo.getMaterialUserId());

        sheet.setMaterialDate(vo.getMaterialDate());
        sheet.setMaterialOrderId(vo.getMaterialOrderId());
        sheet.setDescription(StringUtil.isBlank(vo.getDescription()) ? StringPool.EMPTY_STR : vo.getDescription());

        int totalNum = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;

        int orderNo = 1;
        for (UpdateMaterialOutSheetVo.UpdateMaterialOutSheetDetailVo detailVo : vo.getDetails()) {
            MaterialOutSheetDetail detail = new MaterialOutSheetDetail();
            detail.setId(IdUtil.getId());
            detail.setSheetId(sheet.getId());

            Product product = productService.findById(detailVo.getProductId());
            if (product == null) {
                throw new InputErrorException("第" + orderNo + "行航材不存在！");
            }

            detail.setProductId(detailVo.getProductId());
            detail.setOutNum(detailVo.getOutNum());
            detail.setOrderNum(detailVo.getOrderNum());
            detail.setSerialNumbers(detailVo.getSerialNumbers());
            detail.setTaxPrice(detailVo.getTaxPrice() == null ? BigDecimal.ZERO : detailVo.getTaxPrice());
            detail.setTaxAmount(NumberUtil.mul(detail.getTaxPrice(), detail.getOutNum()));
            detail.setDescription(StringUtil.isBlank(detailVo.getDescription()) ? StringPool.EMPTY_STR : detailVo.getDescription());
            detail.setOrderNo(orderNo);
            // 记录发料单明细ID（如有）
            detail.setMaterialOrderDetailId(detailVo.getMaterialOrderDetailId());
            
            // 设置批次库存ID
            if (!StringUtil.isBlank(detailVo.getStockBatchId())) {
                detail.setStockBatchId(detailVo.getStockBatchId());
            }

            materialOutSheetDetailService.save(detail);

            // 处理序列号库存关系
            if (!CollectionUtil.isEmpty(detailVo.getSerials())) {
                for (String stockSerialId : detailVo.getSerials()) {
                    MaterialOutSheetDetailSerial detailSerial = new MaterialOutSheetDetailSerial();
                    detailSerial.setId(IdUtil.getId());
                    detailSerial.setSheetId(sheet.getId());
                    detailSerial.setProductId(detailVo.getProductId());
                    detailSerial.setStockSerialId(stockSerialId);
                    
                    materialOutSheetDetailSerialService.save(detailSerial);
                }
            }

            totalNum += detail.getOutNum();
            totalAmount = NumberUtil.add(totalAmount, detail.getTaxAmount());

            orderNo++;
        }

        sheet.setTotalNum(totalNum);
        sheet.setTotalAmount(totalAmount);
    }

    /**
     * 创建时验证库存和发料单出库数量
     */
    private void validateStock(CreateMaterialOutSheetVo vo) {
        // 验证发料单出库数量限制（需要按商品汇总验证）
        if (!StringUtil.isBlank(vo.getMaterialOrderId())) {
            this.validateMaterialOrderOutNumByProduct(vo.getMaterialOrderId(), vo.getDetails());
        }
        
        int orderNo = 1;
        for (CreateMaterialOutSheetVo.CreateMaterialOutSheetDetailVo detailVo : vo.getDetails()) {

            // 验证批次库存
            if (!StringUtil.isBlank(detailVo.getStockBatchId())) {
                ProductStockBatch batch = productStockBatchService.findById(detailVo.getStockBatchId());
                if (batch == null) {
                    throw new InputErrorException("第" + orderNo + "行批次库存不存在！");
                }
                if (batch.getQuantity() < detailVo.getOutNum()) {
                    throw new InputErrorException("第" + orderNo + "行批次库存不足！当前库存：" + batch.getQuantity() + 
                            "，申请出库：" + detailVo.getOutNum());
                }
            }

            // 验证序列号库存
            if (!CollectionUtil.isEmpty(detailVo.getSerials())) {
                for (String stockSerialId : detailVo.getSerials()) {
                    ProductStockSerial serial = productStockSerialService.getById(stockSerialId);
                    if (serial == null) {
                        throw new InputErrorException("第" + orderNo + "行序列号库存不存在！");
                    }
                    if (serial.getStockStatus() != 1) {
                        throw new InputErrorException("第" + orderNo + "行序列号[" + serial.getSerialNumber() + "]已出库，无法重复使用！");
                    }
                    if (!serial.getProductId().equals(detailVo.getProductId())) {
                        throw new InputErrorException("第" + orderNo + "行序列号[" + serial.getSerialNumber() + "]与航材不匹配！");
                    }
                }
            }
            
            orderNo++;
        }
    }
    /**
     * 按商品汇总验证发料单出库数量限制（解决同商品多批次问题）
     */
    private void validateMaterialOrderOutNumByProduct(String materialOrderId, List<CreateMaterialOutSheetVo.CreateMaterialOutSheetDetailVo> details) {
        // 按商品ID汇总出库数量
        Map<String, Integer> productOutNumMap = new HashMap<>();
        for (CreateMaterialOutSheetVo.CreateMaterialOutSheetDetailVo detail : details) {
            productOutNumMap.merge(detail.getProductId(), detail.getOutNum(), Integer::sum);
        }

        // 验证每个商品的总出库数量
        for (Map.Entry<String, Integer> entry : productOutNumMap.entrySet()) {
            String productId = entry.getKey();
            Integer totalOutNum = entry.getValue();
            
            // 查询发料单明细
            Wrapper<MaterialOrderDetail> wrapper = Wrappers.<MaterialOrderDetail>lambdaQuery()
                    .eq(MaterialOrderDetail::getOrderId, materialOrderId)
                    .eq(MaterialOrderDetail::getProductId, productId);
            MaterialOrderDetail orderDetail = materialOrderDetailService.getOne(wrapper);
            
            if (orderDetail == null) {
                throw new InputErrorException("航材ID：" + productId + " 在发料单中不存在！");
            }

            // 计算剩余可出库数量
            int remainingNum = orderDetail.getOrderNum() - orderDetail.getOutNum();
            if (totalOutNum > remainingNum) {
                throw new InputErrorException("航材ID：" + productId + " 出库数量超出限制！需要出库：" + orderDetail.getOrderNum() +
                        "，已出库：" + orderDetail.getOutNum() + "，剩余可出库：" + remainingNum + "，当前申请出库：" + totalOutNum);
            }
        }
    }

    /**
     * 锁定当前出库单引用的发料单明细。
     */
    private List<MaterialOrderDetail> lockMaterialOrderDetails(MaterialOutSheet sheet) {
        Wrapper<MaterialOutSheetDetail> detailWrapper = Wrappers.<MaterialOutSheetDetail>lambdaQuery()
                .eq(MaterialOutSheetDetail::getSheetId, sheet.getId());
        List<MaterialOutSheetDetail> details = materialOutSheetDetailService.list(detailWrapper);

        Set<String> orderDetailIds = new TreeSet<>();
        for (MaterialOutSheetDetail detail : details) {
            if (!StringUtil.isBlank(detail.getMaterialOrderDetailId())) {
                orderDetailIds.add(detail.getMaterialOrderDetailId());
            }
        }

        List<MaterialOrderDetail> lockedDetails = new ArrayList<>(orderDetailIds.size());
        for (String orderDetailId : orderDetailIds) {
            MaterialOrderDetail lockedDetail = materialOrderDetailMapper.selectByIdForUpdate(orderDetailId);
            if (lockedDetail != null) {
                lockedDetails.add(lockedDetail);
            }
        }
        return lockedDetails;
    }

    /**
     * 审核通过时更新库存状态
     */
    private void updateStockOnApprove(MaterialOutSheet sheet, MaterialOrder lockedMaterialOrder,
            List<MaterialOrderDetail> lockedOrderDetails) {
        // 查询出库单明细
        Wrapper<MaterialOutSheetDetail> detailWrapper = Wrappers.<MaterialOutSheetDetail>lambdaQuery()
                .eq(MaterialOutSheetDetail::getSheetId, sheet.getId())
                .orderByAsc(MaterialOutSheetDetail::getOrderNo);
        List<MaterialOutSheetDetail> details = materialOutSheetDetailService.list(detailWrapper);

        // 按商品汇总出库数量，用于更新ProductStock
        Map<String, Integer> productOutNumMap = new HashMap<>();
        
        for (MaterialOutSheetDetail detail : details) {
            // 汇总同商品的出库数量
            productOutNumMap.merge(detail.getProductId(), detail.getOutNum(), Integer::sum);

            // 1. 处理批次库存减少（为非批次管理商品也生成/使用默认批次号 DEFAULT 并走批次出库，确保有批次与日志）
            if (StringUtil.isBlank(detail.getStockBatchId())) {
                Product prod = productService.findById(detail.getProductId());
                if (prod != null && (prod.getIsBatch() == null || !prod.getIsBatch())) {
                    // 非批次管理：使用默认批次
                    ProductStockBatch defaultBatch = null;
                    Wrapper<ProductStockBatch> defBatchWrapper = Wrappers.<ProductStockBatch>lambdaQuery()
                            .eq(ProductStockBatch::getScId, sheet.getScId())
                            .eq(ProductStockBatch::getProductId, detail.getProductId())
                            .eq(ProductStockBatch::getBatchNumber, "DEFAULT");
                    List<ProductStockBatch> exist = productStockBatchService.list(defBatchWrapper);
                    if (!CollectionUtil.isEmpty(exist)) {
                        defaultBatch = exist.get(0);
                    } else {
                        defaultBatch = new ProductStockBatch();
                        defaultBatch.setId(IdUtil.getId());
                        defaultBatch.setScId(sheet.getScId());
                        defaultBatch.setProductId(detail.getProductId());
                        defaultBatch.setBatchNumber("DEFAULT");
                        // 将当前总库存映射到默认批次初始数量，避免首次扣减失败
                        Integer totalStock = 0;
                        try {
                            totalStock = productStockService.getByProductIdAndScId(detail.getProductId(), sheet.getScId()) != null
                                    ? productStockService.getByProductIdAndScId(detail.getProductId(), sheet.getScId()).getStockNum()
                                    : 0;
                        } catch (Exception ex) {
                            totalStock = 0;
                        }
                        defaultBatch.setQuantity(totalStock == null ? 0 : totalStock);
                        productStockBatchService.save(defaultBatch);
                    }

                    // 绑定到明细并持久化
                    detail.setStockBatchId(defaultBatch.getId());
                    materialOutSheetDetailService.updateById(detail);

                    // 执行批次出库
                    this.subStockBatch(detail, sheet);
                }
            } else {
                // 指定了批次，直接批次出库
                this.subStockBatch(detail, sheet);
            }

        }

        // 2. 每个商品只处理一次序列号，避免同商品多批次明细重复更新同一组序列号。
        for (String productId : productOutNumMap.keySet()) {
            this.updateSerialStock(sheet.getId(), productId);
        }
        
        // 3. 更新ProductStock总库存（按商品汇总，包含批次和非批次商品）
        // 使用subStockWithoutLog避免重复记录总库存流水，因为批次库存流水已经记录了详细信息
        for (Map.Entry<String, Integer> entry : productOutNumMap.entrySet()) {
            String productId = entry.getKey();
            Integer totalOutNum = entry.getValue();
            
            SubProductStockVo subProductStockVo = new SubProductStockVo();
            subProductStockVo.setProductId(productId);
            subProductStockVo.setScId(sheet.getScId());
            subProductStockVo.setStockNum(totalOutNum);
            subProductStockVo.setBizId(sheet.getId());
            subProductStockVo.setBizDetailId(sheet.getId()); // 使用出库单ID作为明细ID
            subProductStockVo.setBizCode(sheet.getCode());
            subProductStockVo.setBizType(ProductStockBizType.MATERIAL_ISSUE.getCode());
            
            productStockService.subStockWithoutLog(subProductStockVo);
        }
        
        // 4. 更新发料单明细已出库数量（基于 materialOrderDetailId）
        if (!StringUtil.isBlank(sheet.getMaterialOrderId())) {
            this.updateMaterialOrderDetailOutNum(sheet, lockedMaterialOrder, lockedOrderDetails);
        }
    }

    /**
     * 使用新的批次库存减少方法
     */
    private void subStockBatch(MaterialOutSheetDetail detail, MaterialOutSheet sheet) {
        SubProductStockBatchVo subStockBatchVo = new SubProductStockBatchVo();
        subStockBatchVo.setProductId(detail.getProductId());
        subStockBatchVo.setScId(sheet.getScId());
        subStockBatchVo.setStockBatchId(detail.getStockBatchId());
        subStockBatchVo.setStockNum(detail.getOutNum());
        subStockBatchVo.setCreateTime(LocalDateTime.now());
        subStockBatchVo.setBizId(sheet.getId());
        subStockBatchVo.setBizDetailId(detail.getId());
        subStockBatchVo.setBizCode(sheet.getCode());
        subStockBatchVo.setBizType(ProductStockBizType.MATERIAL_ISSUE.getCode());
        
        // 使用ProductStockService的批次库存减少方法
        productStockService.subStockBatch(subStockBatchVo);
    }

    /**
     * 更新批次库存数量（已废弃，使用subStockBatch代替）
     */
    @Deprecated
    private void updateBatchStock(String stockBatchId, Integer outNum) {
        ProductStockBatch batch = productStockBatchService.findById(stockBatchId);
        if (batch != null) {
            int newQuantity = batch.getQuantity() - outNum;
            if (newQuantity < 0) {
                throw new InputErrorException("批次库存不足，无法完成出库！");
            }
            batch.setQuantity(newQuantity);
            productStockBatchService.updateById(batch);
        }
    }

    /**
     * 更新序列号库存状态
     */
    private void updateSerialStock(String sheetId, String productId) {
        Wrapper<MaterialOutSheetDetailSerial> serialWrapper = Wrappers.<MaterialOutSheetDetailSerial>lambdaQuery()
                .eq(MaterialOutSheetDetailSerial::getSheetId, sheetId)
                .eq(MaterialOutSheetDetailSerial::getProductId, productId);
        List<MaterialOutSheetDetailSerial> detailSerials = materialOutSheetDetailSerialService.list(serialWrapper);

        for (MaterialOutSheetDetailSerial detailSerial : detailSerials) {
            boolean updated = productStockSerialService.update(
                    Wrappers.lambdaUpdate(ProductStockSerial.class)
                            .set(ProductStockSerial::getStockStatus, 0)
                            .eq(ProductStockSerial::getId, detailSerial.getStockSerialId())
                            .eq(ProductStockSerial::getProductId, productId)
                            .eq(ProductStockSerial::getStockStatus, 1));
            if (!updated) {
                throw new InputErrorException("序列号库存状态已变化，无法重复出库！");
            }
        }
    }

    /**
     * 更新发料单明细已出库数量
     */
    private void updateMaterialOrderDetailOutNum(MaterialOutSheet sheet, MaterialOrder materialOrder,
            List<MaterialOrderDetail> lockedOrderDetails) {
        Wrapper<MaterialOutSheetDetail> detailWrapper = Wrappers.<MaterialOutSheetDetail>lambdaQuery()
                .eq(MaterialOutSheetDetail::getSheetId, sheet.getId());
        List<MaterialOutSheetDetail> details = materialOutSheetDetailService.list(detailWrapper);

        // 按发料单明细ID汇总出库数量
        Map<String, Integer> detailOutMap = new HashMap<>();
        for (MaterialOutSheetDetail detail : details) {
            if (!StringUtil.isBlank(detail.getMaterialOrderDetailId())) {
                detailOutMap.merge(detail.getMaterialOrderDetailId(), detail.getOutNum(), Integer::sum);
            }
        }

        Map<String, MaterialOrderDetail> lockedDetailMap = new HashMap<>();
        for (MaterialOrderDetail orderDetail : lockedOrderDetails) {
            lockedDetailMap.put(orderDetail.getId(), orderDetail);
        }
        for (Map.Entry<String, Integer> entry : detailOutMap.entrySet()) {
            MaterialOrderDetail orderDetail = lockedDetailMap.get(entry.getKey());
            if (orderDetail == null || materialOrderDetailMapper.addOutNum(entry.getKey(), entry.getValue()) != 1) {
                throw new InputErrorException("发料单明细可出库数量已变化，请刷新后重试！");
            }
            orderDetail.setOutNum((orderDetail.getOutNum() == null ? 0 : orderDetail.getOutNum())
                    + entry.getValue());
        }

        // 同步更新发料单主表：主表已先通过 FOR UPDATE 串行化，直接在最新累计值上增加本单数量，
        // 避免 RR 隔离级别下重新汇总全部明细时读到旧快照并覆盖并发结果。
        if (materialOrder != null) {
            int totalOutNum = MaterialOutSheetRules.calculateTotalOutNum(materialOrder.getTotalOutNum(),
                    materialOrder.getTotalNum(), detailOutMap.values());

            materialOrder.setTotalOutNum(totalOutNum);
            materialOrder.setIsOutFinish(totalOutNum >= materialOrder.getTotalNum());
            materialOrderService.updateById(materialOrder);

            // 如果发料单完成出库，更新对应合同任务的航材状态
            if (materialOrder.getIsOutFinish() != null && materialOrder.getIsOutFinish()) {
                    // 查询发料单对应的发料申请单
                    if (!StringUtil.isBlank(materialOrder.getMaterialApplyId())) {
                        ContractTaskMaterialApply materialApply = getBaseMapper().getContractTaskMaterialApplyById(materialOrder.getMaterialApplyId());
                        if (materialApply != null && !StringUtil.isBlank(materialApply.getTaskId())) {
                            String taskId = materialApply.getTaskId();
                            // 查询该合同任务的所有发料申请单
                            List<ContractTaskMaterialApply> allApplies = getBaseMapper().getContractTaskMaterialAppliesByTaskId(taskId);
                            
                            boolean allOutFinish = true;
                            if (!CollectionUtil.isEmpty(allApplies)) {
                                for (ContractTaskMaterialApply apply : allApplies) {
                                    // 查询每个发料申请单对应的发料单
                                    List<MaterialOrder> orders = getBaseMapper().getMaterialOrdersByMaterialApplyId(apply.getId());
                                    if (!CollectionUtil.isEmpty(orders)) {
                                        for (MaterialOrder order : orders) {
                                            if (order.getIsOutFinish() == null || !order.getIsOutFinish()) {
                                                allOutFinish = false;
                                                break;
                                            }
                                        }
                                    }
                                    if (!allOutFinish) {
                                        break;
                                    }
                                }
                            }

                            // 更新合同任务的航材状态
                            String newMaterialStatus;
                            if (allOutFinish) {
                                newMaterialStatus = MaterialStatus.COMPLETED.getCode();
                            } else {
                                newMaterialStatus = MaterialStatus.PARTIAL_PICKED.getCode();
                            }

                            getBaseMapper().updateContractTask(taskId, newMaterialStatus);
                        }
                    }
            }
        }
    }

    /**
     * 审核前校验：
     * 1）若关联发料单，锁定校验 materialOrderDetailId 归属及剩余数量
     * 2）批次库存数量充足
     * 3）序列号库存有效且数量与出库数量一致（按商品汇总）
     */
    private void validateOnApprove(MaterialOutSheet sheet, List<MaterialOrderDetail> lockedOrderDetails) {
        // 查询明细
        Wrapper<MaterialOutSheetDetail> detailWrapper = Wrappers.<MaterialOutSheetDetail>lambdaQuery()
                .eq(MaterialOutSheetDetail::getSheetId, sheet.getId());
        List<MaterialOutSheetDetail> details = materialOutSheetDetailService.list(detailWrapper);

        if (CollectionUtil.isEmpty(details)) {
            throw new InputErrorException("出库明细不能为空！");
        }

        // 1）校验发料单剩余数量
        if (!StringUtil.isBlank(sheet.getMaterialOrderId())) {
            MaterialOutSheetRules.validateAndSumOrderDetails(sheet.getMaterialOrderId(), details,
                    lockedOrderDetails);
        }

        // 2）批次库存校验（并校验批次管理商品必须选择批次）
        int orderNo = 1;
        for (MaterialOutSheetDetail d : details) {
            Product product = productService.findById(d.getProductId());
            if (product == null) {
                throw new InputErrorException("第" + orderNo + "行航材不存在！");
            }

            // 批次管理商品必须选择批次
            if (Boolean.TRUE.equals(product.getIsBatch()) && StringUtil.isBlank(d.getStockBatchId())) {
                throw new InputErrorException("第" + orderNo + "行航材为批次管理，必须选择批次！");
            }

            if (!StringUtil.isBlank(d.getStockBatchId())) {
                ProductStockBatch batch = productStockBatchService.findById(d.getStockBatchId());
                if (batch == null) {
                    throw new InputErrorException("第" + orderNo + "行批次库存不存在！");
                }
                if (batch.getQuantity() < d.getOutNum()) {
                    throw new InputErrorException("第" + orderNo + "行批次库存不足！当前库存：" + batch.getQuantity() +
                            "，申请出库：" + d.getOutNum());
                }
            }
            orderNo++;
        }

        // 3）序列号校验：数量匹配与有效性
        // 按商品统计需要的序列号数量
        Map<String, Integer> productOutNumMap = new HashMap<>();
        Map<String, Set<String>> productBatchIds = new HashMap<>();
        for (MaterialOutSheetDetail d : details) {
            productOutNumMap.merge(d.getProductId(), d.getOutNum(), Integer::sum);
            if (!StringUtil.isBlank(d.getStockBatchId())) {
                productBatchIds.computeIfAbsent(d.getProductId(), ignored -> new HashSet<>())
                        .add(d.getStockBatchId());
            }
        }

        for (Map.Entry<String, Integer> e : productOutNumMap.entrySet()) {
            String productId = e.getKey();
            int needSerialCount = e.getValue();

            Wrapper<MaterialOutSheetDetailSerial> serialWrapper = Wrappers.<MaterialOutSheetDetailSerial>lambdaQuery()
                    .eq(MaterialOutSheetDetailSerial::getSheetId, sheet.getId())
                    .eq(MaterialOutSheetDetailSerial::getProductId, productId);
            List<MaterialOutSheetDetailSerial> serials = materialOutSheetDetailSerialService.list(serialWrapper);

            // 若商品为序列号管理，则必须提供序列号，且数量需与出库数量一致
            Product p = productService.findById(productId);
            if (p != null && Boolean.TRUE.equals(p.getIsSerial())) {
                if (CollectionUtil.isEmpty(serials)) {
                    throw new InputErrorException("航材ID：" + productId + " 为序列号管理，必须选择序列号！");
                }
                if (serials.size() != needSerialCount) {
                    throw new InputErrorException("航材ID：" + productId + " 序列号数量与出库数量不一致！应为：" + needSerialCount + "，实际：" + serials.size());
                }
            }

            if (!CollectionUtil.isEmpty(serials)) {
                for (MaterialOutSheetDetailSerial s : serials) {
                    ProductStockSerial serial = productStockSerialService.getById(s.getStockSerialId());
                    if (serial == null) {
                        throw new InputErrorException("航材ID：" + productId + " 存在无效的序列号记录！");
                    }
                    if (serial.getStockStatus() != 1) {
                        throw new InputErrorException("序列号[" + serial.getSerialNumber() + "]已出库，无法重复使用！");
                    }
                    if (!productId.equals(serial.getProductId())) {
                        throw new InputErrorException("序列号[" + serial.getSerialNumber() + "]与航材不匹配！");
                    }
                    ProductStockBatch serialBatch = productStockBatchService.findById(serial.getBatchId());
                    if (serialBatch == null || !sheet.getScId().equals(serialBatch.getScId())
                            || !productId.equals(serialBatch.getProductId())) {
                        throw new InputErrorException("序列号[" + serial.getSerialNumber() + "]不属于当前仓库航材库存！");
                    }
                    Set<String> allowedBatchIds = productBatchIds.get(productId);
                    if (!CollectionUtil.isEmpty(allowedBatchIds) && !allowedBatchIds.contains(serial.getBatchId())) {
                        throw new InputErrorException("序列号[" + serial.getSerialNumber() + "]不属于本次选择的批次！");
                    }
                }
            }
        }
    }

    @Override
    public List<BatchStockBo> queryBatchStock(String scId, String productId) {
        // 通过XML Mapper联表查询，返回富集批次库存数据
        return getBaseMapper().queryBatchStock(scId, productId);
    }

    @Override
    public List<SerialStockBo> querySerialStock(String scId, String productId) {
        // 通过XML Mapper联表查询，返回富集序列号库存数据
        return getBaseMapper().querySerialStock(scId, productId);
    }
}
