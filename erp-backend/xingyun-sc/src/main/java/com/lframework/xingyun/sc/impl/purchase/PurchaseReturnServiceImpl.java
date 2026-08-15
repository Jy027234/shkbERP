package com.lframework.xingyun.sc.impl.purchase;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageInfo;
import com.lframework.starter.common.constants.StringPool;
import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.common.exceptions.impl.InputErrorException;
import com.lframework.starter.common.utils.Assert;
import com.lframework.starter.common.utils.NumberUtil;
import com.lframework.starter.common.utils.StringUtil;
import com.lframework.starter.web.core.components.security.SecurityUtil;
import com.lframework.starter.web.core.impl.BaseMpServiceImpl;
import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.utils.ApplicationUtil;
import com.lframework.starter.web.core.utils.IdUtil;
import com.lframework.starter.web.core.utils.PageHelperUtil;
import com.lframework.starter.web.core.utils.PageResultUtil;
import com.lframework.xingyun.basedata.entity.Product;
import com.lframework.xingyun.basedata.entity.StoreCenter;
import com.lframework.xingyun.basedata.entity.Supplier;
import com.lframework.xingyun.basedata.enums.ManageType;
import com.lframework.xingyun.basedata.service.product.ProductService;
import com.lframework.xingyun.basedata.service.storecenter.StoreCenterService;
import com.lframework.xingyun.basedata.service.supplier.SupplierService;
import com.lframework.xingyun.core.annotations.OpLog;
import com.lframework.xingyun.core.annotations.OrderTimeLineLog;
import com.lframework.xingyun.core.dto.order.ApprovePassOrderDto;
import com.lframework.xingyun.core.enums.OrderTimeLineBizType;
import com.lframework.xingyun.core.service.GenerateCodeService;
import com.lframework.xingyun.core.utils.OpLogUtil;
import com.lframework.xingyun.sc.components.code.GenerateCodeTypePool;
import com.lframework.xingyun.sc.dto.purchase.receive.GetPaymentDateDto;
import com.lframework.xingyun.sc.dto.purchase.returned.PurchaseReturnFullDto;
import com.lframework.xingyun.sc.entity.PurchaseConfig;
import com.lframework.xingyun.sc.entity.PurchaseReturn;
import com.lframework.xingyun.sc.entity.PurchaseReturnDetail;
import com.lframework.xingyun.sc.entity.ProductStockBatch;
import com.lframework.xingyun.sc.entity.ProductStockSerial;
import com.lframework.xingyun.sc.entity.ReceiveSheet;
import com.lframework.xingyun.sc.entity.ReceiveSheetDetail;
import com.lframework.xingyun.sc.enums.ProductStockBizType;
import com.lframework.xingyun.sc.enums.PurchaseReturnStatus;
import com.lframework.xingyun.sc.enums.ScOpLogType;
import com.lframework.xingyun.sc.enums.SettleStatus;
import com.lframework.xingyun.sc.events.order.impl.ApprovePassPurchaseReturnEvent;
import com.lframework.xingyun.sc.mappers.PurchaseReturnMapper;
import com.lframework.xingyun.sc.service.purchase.PurchaseConfigService;
import com.lframework.xingyun.sc.service.purchase.PurchaseReturnDetailService;
import com.lframework.xingyun.sc.service.purchase.PurchaseReturnService;
import com.lframework.xingyun.sc.service.purchase.ReceiveSheetDetailService;
import com.lframework.xingyun.sc.service.purchase.ReceiveSheetService;
import com.lframework.xingyun.sc.service.stock.ProductStockBatchService;
import com.lframework.xingyun.sc.service.stock.ProductStockSerialService;
import com.lframework.xingyun.sc.service.stock.ProductStockService;
import com.lframework.xingyun.sc.vo.purchase.returned.ApprovePassPurchaseReturnVo;
import com.lframework.xingyun.sc.vo.purchase.returned.ApproveRefusePurchaseReturnVo;
import com.lframework.xingyun.sc.vo.purchase.returned.CreatePurchaseReturnVo;
import com.lframework.xingyun.sc.vo.purchase.returned.QueryPurchaseReturnVo;
import com.lframework.xingyun.sc.vo.purchase.returned.ReturnProductVo;
import com.lframework.xingyun.sc.vo.purchase.returned.UpdatePurchaseReturnVo;
import com.lframework.xingyun.sc.vo.stock.SubProductStockVo;
import com.lframework.xingyun.template.inner.entity.SysUser;
import com.lframework.xingyun.template.inner.service.system.SysUserService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PurchaseReturnServiceImpl extends
    BaseMpServiceImpl<PurchaseReturnMapper, PurchaseReturn>
    implements PurchaseReturnService {

  @Autowired
  private PurchaseReturnDetailService purchaseReturnDetailService;

  @Autowired
  private GenerateCodeService generateCodeService;

  @Autowired
  private StoreCenterService storeCenterService;

  @Autowired
  private SupplierService supplierService;

  @Autowired
  private SysUserService userService;

  @Autowired
  private ProductService productService;

  @Autowired
  private ReceiveSheetService receiveSheetService;

  @Autowired
  private PurchaseConfigService purchaseConfigService;

  @Autowired
  private ReceiveSheetDetailService receiveSheetDetailService;

  @Autowired
  private ProductStockService productStockService;

  @Autowired
  private ProductStockBatchService productStockBatchService;

  @Autowired
  private ProductStockSerialService productStockSerialService;

  @Override
  public PageResult<PurchaseReturn> query(Integer pageIndex, Integer pageSize,
      QueryPurchaseReturnVo vo) {

    Assert.greaterThanZero(pageIndex);
    Assert.greaterThanZero(pageSize);

    PageHelperUtil.startPage(pageIndex, pageSize);
    List<PurchaseReturn> datas = this.query(vo);

    return PageResultUtil.convert(new PageInfo<>(datas));
  }

  @Override
  public List<PurchaseReturn> query(QueryPurchaseReturnVo vo) {

    return getBaseMapper().query(vo);
  }

  @Override
  public PurchaseReturnFullDto getDetail(String id) {

    return getBaseMapper().getDetail(id);
  }

  @Override
  public List<ProductStockSerial> getAvailableSerials(String receiveSheetDetailId) {

    ReceiveSheetDetail receiveDetail = receiveSheetDetailService.getById(receiveSheetDetailId);
    if (receiveDetail == null) {
      throw new InputErrorException("采购收货单明细不存在！");
    }

    ReceiveSheet receiveSheet = receiveSheetService.getById(receiveDetail.getSheetId());
    if (receiveSheet == null) {
      throw new InputErrorException("采购收货单不存在！");
    }

    Product product = productService.findById(receiveDetail.getProductId());
    if (product == null || !Boolean.TRUE.equals(product.getIsSerial())) {
      return List.of();
    }

    List<String> sourceSerialNumbers = parseSerialNumbers(receiveDetail.getSerialNumberList());
    if (sourceSerialNumbers.isEmpty()) {
      return List.of();
    }

    ProductStockBatch batch = findReturnBatch(receiveSheet.getScId(), product, receiveDetail);
    if (batch == null) {
      return List.of();
    }

    return productStockSerialService.list(
        Wrappers.lambdaQuery(ProductStockSerial.class)
            .eq(ProductStockSerial::getProductId, product.getId())
            .eq(ProductStockSerial::getBatchId, batch.getId())
            .eq(ProductStockSerial::getStockStatus, 1)
            .in(ProductStockSerial::getSerialNumber, sourceSerialNumbers)
            .orderByAsc(ProductStockSerial::getSerialNumber));
  }

  @OpLog(type = ScOpLogType.PURCHASE, name = "创建采购退货单，单号：{}", params = "#code")
  @OrderTimeLineLog(type = OrderTimeLineBizType.CREATE, orderId = "#_result", name = "创建退单")
  @Transactional(rollbackFor = Exception.class)
  @Override
  public String create(CreatePurchaseReturnVo vo) {

    PurchaseReturn purchaseReturn = new PurchaseReturn();
    purchaseReturn.setId(IdUtil.getId());
    purchaseReturn.setCode(generateCodeService.generate(GenerateCodeTypePool.PURCHASE_RETURN));

    PurchaseConfig purchaseConfig = purchaseConfigService.get();

    this.create(purchaseReturn, vo, purchaseConfig.getPurchaseReturnRequireReceive());

    purchaseReturn.setStatus(PurchaseReturnStatus.CREATED);

    getBaseMapper().insert(purchaseReturn);

    OpLogUtil.setVariable("code", purchaseReturn.getCode());
    OpLogUtil.setExtra(vo);

    return purchaseReturn.getId();
  }

  @OpLog(type = ScOpLogType.PURCHASE, name = "修改采购退货单，单号：{}", params = "#code")
  @OrderTimeLineLog(type = OrderTimeLineBizType.UPDATE, orderId = "#vo.id", name = "修改退单")
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void update(UpdatePurchaseReturnVo vo) {

    PurchaseReturn purchaseReturn = getBaseMapper().selectById(vo.getId());
    if (purchaseReturn == null) {
      throw new InputErrorException("采购退货单不存在！");
    }

    if (purchaseReturn.getStatus() != PurchaseReturnStatus.CREATED
        && purchaseReturn.getStatus() != PurchaseReturnStatus.APPROVE_REFUSE) {

      if (purchaseReturn.getStatus() == PurchaseReturnStatus.APPROVE_PASS) {
        throw new DefaultClientException("采购退货单已审核通过，无法修改！");
      }

      throw new DefaultClientException("采购退货单无法修改！");
    }

    boolean requireReceive = !StringUtil.isBlank(purchaseReturn.getReceiveSheetId());

    if (requireReceive) {
      //查询采购退货单明细
      Wrapper<PurchaseReturnDetail> queryDetailWrapper = Wrappers.lambdaQuery(
              PurchaseReturnDetail.class)
          .eq(PurchaseReturnDetail::getReturnId, purchaseReturn.getId());
      List<PurchaseReturnDetail> details = purchaseReturnDetailService.list(queryDetailWrapper);
      for (PurchaseReturnDetail detail : details) {
        if (!StringUtil.isBlank(detail.getReceiveSheetDetailId())) {
          //先恢复已退货数量
          receiveSheetDetailService.subReturnNum(detail.getReceiveSheetDetailId(),
              detail.getReturnNum());
        }
      }
    }

    // 删除采购退货单明细
    Wrapper<PurchaseReturnDetail> deleteDetailWrapper = Wrappers.lambdaQuery(
            PurchaseReturnDetail.class)
        .eq(PurchaseReturnDetail::getReturnId, purchaseReturn.getId());
    purchaseReturnDetailService.remove(deleteDetailWrapper);

    this.create(purchaseReturn, vo, requireReceive);

    purchaseReturn.setStatus(PurchaseReturnStatus.CREATED);

    List<PurchaseReturnStatus> statusList = new ArrayList<>();
    statusList.add(PurchaseReturnStatus.CREATED);
    statusList.add(PurchaseReturnStatus.APPROVE_REFUSE);

    Wrapper<PurchaseReturn> updateOrderWrapper = Wrappers.lambdaUpdate(PurchaseReturn.class)
        .set(PurchaseReturn::getApproveBy, null).set(PurchaseReturn::getApproveTime, null)
        .set(PurchaseReturn::getRefuseReason, StringPool.EMPTY_STR)
        .eq(PurchaseReturn::getId, purchaseReturn.getId())
        .in(PurchaseReturn::getStatus, statusList);
    if (getBaseMapper().updateAllColumn(purchaseReturn, updateOrderWrapper) != 1) {
      throw new DefaultClientException("采购退货单信息已过期，请刷新重试！");
    }

    OpLogUtil.setVariable("code", purchaseReturn.getCode());
    OpLogUtil.setExtra(vo);
  }

  @OpLog(type = ScOpLogType.PURCHASE, name = "审核通过采购退货单，单号：{}", params = "#code")
  @OrderTimeLineLog(type = OrderTimeLineBizType.APPROVE_PASS, orderId = "#vo.id", name = "审核通过")
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void approvePass(ApprovePassPurchaseReturnVo vo) {

    PurchaseReturn purchaseReturn = getBaseMapper().selectById(vo.getId());
    if (purchaseReturn == null) {
      throw new InputErrorException("采购退货单不存在！");
    }

    if (purchaseReturn.getStatus() != PurchaseReturnStatus.CREATED
        && purchaseReturn.getStatus() != PurchaseReturnStatus.APPROVE_REFUSE) {

      if (purchaseReturn.getStatus() == PurchaseReturnStatus.APPROVE_PASS) {
        throw new DefaultClientException("采购退货单已审核通过，不允许继续执行审核！");
      }

      throw new DefaultClientException("采购退货单无法审核通过！");
    }

    PurchaseConfig purchaseConfig = purchaseConfigService.get();

    if (!purchaseConfig.getPurchaseReturnMultipleRelateReceive()) {
      Wrapper<PurchaseReturn> checkWrapper = Wrappers.lambdaQuery(PurchaseReturn.class)
          .eq(PurchaseReturn::getReceiveSheetId, purchaseReturn.getReceiveSheetId())
          .ne(PurchaseReturn::getId, purchaseReturn.getId());
      if (getBaseMapper().selectCount(checkWrapper) > 0) {
        ReceiveSheet receiveSheet = receiveSheetService.getById(purchaseReturn.getReceiveSheetId());
        throw new DefaultClientException(
            "采购收货单号：" + receiveSheet.getCode()
                + "，已关联其他采购退货单，不允许关联多个采购退货单！");
      }
    }

    Wrapper<PurchaseReturnDetail> queryDetailWrapper = Wrappers.lambdaQuery(
            PurchaseReturnDetail.class)
        .eq(PurchaseReturnDetail::getReturnId, purchaseReturn.getId())
        .orderByAsc(PurchaseReturnDetail::getOrderNo);
    List<PurchaseReturnDetail> details = purchaseReturnDetailService.list(queryDetailWrapper);
    Map<String, List<ProductStockSerial>> serialsByDetail = validateSerialsOnApprove(
        purchaseReturn, details);

    purchaseReturn.setStatus(PurchaseReturnStatus.APPROVE_PASS);

    List<PurchaseReturnStatus> statusList = new ArrayList<>();
    statusList.add(PurchaseReturnStatus.CREATED);
    statusList.add(PurchaseReturnStatus.APPROVE_REFUSE);

    LambdaUpdateWrapper<PurchaseReturn> updateOrderWrapper = Wrappers.lambdaUpdate(
            PurchaseReturn.class)
        .set(PurchaseReturn::getApproveBy, SecurityUtil.getCurrentUser().getId())
        .set(PurchaseReturn::getApproveTime, LocalDateTime.now())
        .eq(PurchaseReturn::getId, purchaseReturn.getId())
        .in(PurchaseReturn::getStatus, statusList);
    if (!StringUtil.isBlank(vo.getDescription())) {
      updateOrderWrapper.set(PurchaseReturn::getDescription, vo.getDescription());
    }
    if (getBaseMapper().updateAllColumn(purchaseReturn, updateOrderWrapper) != 1) {
      throw new DefaultClientException("采购退货单信息已过期，请刷新重试！");
    }

    for (PurchaseReturnDetail detail : details) {
      SubProductStockVo subproductStockVo = new SubProductStockVo();

      subproductStockVo.setProductId(detail.getProductId());
      subproductStockVo.setScId(purchaseReturn.getScId());
      subproductStockVo.setStockNum(detail.getReturnNum());
      subproductStockVo.setTaxAmount(NumberUtil.mul(detail.getTaxPrice(), detail.getReturnNum()));
      subproductStockVo.setBizId(purchaseReturn.getId());
      subproductStockVo.setBizDetailId(detail.getId());
      subproductStockVo.setBizCode(purchaseReturn.getCode());
      subproductStockVo.setBizType(ProductStockBizType.PURCHASE_RETURN.getCode());

      productStockService.subStock(subproductStockVo);
      this.subBatchStock(purchaseReturn, detail);
      this.subSerialStock(detail, serialsByDetail.getOrDefault(detail.getId(), List.of()));
    }

    this.sendApprovePassEvent(purchaseReturn);

    OpLogUtil.setVariable("code", purchaseReturn.getCode());
    OpLogUtil.setExtra(vo);
  }

  @OrderTimeLineLog(type = OrderTimeLineBizType.APPROVE_PASS, orderId = "#_result", name = "直接审核通过")
  @Transactional(rollbackFor = Exception.class)
  @Override
  public String directApprovePass(CreatePurchaseReturnVo vo) {

    PurchaseReturnService thisService = getThis(this.getClass());

    String returnId = thisService.create(vo);

    ApprovePassPurchaseReturnVo approvePassVo = new ApprovePassPurchaseReturnVo();
    approvePassVo.setId(returnId);
    approvePassVo.setDescription(vo.getDescription());

    thisService.approvePass(approvePassVo);

    return returnId;
  }

  @OpLog(type = ScOpLogType.PURCHASE, name = "审核拒绝采购退货单，单号：{}", params = "#code")
  @OrderTimeLineLog(type = OrderTimeLineBizType.APPROVE_RETURN, orderId = "#vo.id", name = "审核拒绝，拒绝理由：{}", params = "#vo.refuseReason")
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void approveRefuse(ApproveRefusePurchaseReturnVo vo) {

    PurchaseReturn purchaseReturn = getBaseMapper().selectById(vo.getId());
    if (purchaseReturn == null) {
      throw new InputErrorException("采购退货单不存在！");
    }

    if (purchaseReturn.getStatus() != PurchaseReturnStatus.CREATED) {

      if (purchaseReturn.getStatus() == PurchaseReturnStatus.APPROVE_PASS) {
        throw new DefaultClientException("采购退货单已审核通过，不允许继续执行审核！");
      }

      if (purchaseReturn.getStatus() == PurchaseReturnStatus.APPROVE_REFUSE) {
        throw new DefaultClientException("采购退货单已审核拒绝，不允许继续执行审核！");
      }

      throw new DefaultClientException("采购退货单无法审核拒绝！");
    }

    purchaseReturn.setStatus(PurchaseReturnStatus.APPROVE_REFUSE);

    LambdaUpdateWrapper<PurchaseReturn> updateOrderWrapper = Wrappers.lambdaUpdate(
            PurchaseReturn.class)
        .set(PurchaseReturn::getApproveBy, SecurityUtil.getCurrentUser().getId())
        .set(PurchaseReturn::getApproveTime, LocalDateTime.now())
        .set(PurchaseReturn::getRefuseReason, vo.getRefuseReason())
        .eq(PurchaseReturn::getId, purchaseReturn.getId())
        .eq(PurchaseReturn::getStatus, PurchaseReturnStatus.CREATED);
    if (getBaseMapper().updateAllColumn(purchaseReturn, updateOrderWrapper) != 1) {
      throw new DefaultClientException("采购退货单信息已过期，请刷新重试！");
    }

    OpLogUtil.setVariable("code", purchaseReturn.getCode());
    OpLogUtil.setExtra(vo);
  }

  @OpLog(type = ScOpLogType.PURCHASE, name = "删除采购退货单，单号：{}", params = "#code")
  @OrderTimeLineLog(orderId = "#id", delete = true)
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void deleteById(String id) {

    Assert.notBlank(id);
    PurchaseReturn purchaseReturn = getBaseMapper().selectById(id);
    if (purchaseReturn == null) {
      throw new InputErrorException("采购退货单不存在！");
    }

    if (purchaseReturn.getStatus() != PurchaseReturnStatus.CREATED
        && purchaseReturn.getStatus() != PurchaseReturnStatus.APPROVE_REFUSE) {

      if (purchaseReturn.getStatus() == PurchaseReturnStatus.APPROVE_PASS) {
        throw new DefaultClientException("“审核通过”的采购退货单不允许执行删除操作！");
      }

      throw new DefaultClientException("采购退货单无法删除！");
    }

    if (!StringUtil.isBlank(purchaseReturn.getReceiveSheetId())) {
      //查询采购退货单明细
      Wrapper<PurchaseReturnDetail> queryDetailWrapper = Wrappers.lambdaQuery(
              PurchaseReturnDetail.class)
          .eq(PurchaseReturnDetail::getReturnId, purchaseReturn.getId());
      List<PurchaseReturnDetail> details = purchaseReturnDetailService.list(queryDetailWrapper);
      for (PurchaseReturnDetail detail : details) {
        if (!StringUtil.isBlank(detail.getReceiveSheetDetailId())) {
          //恢复已退货数量
          receiveSheetDetailService.subReturnNum(detail.getReceiveSheetDetailId(),
              detail.getReturnNum());
        }
      }
    }

    // 删除退货单明细
    Wrapper<PurchaseReturnDetail> deleteDetailWrapper = Wrappers.lambdaQuery(
            PurchaseReturnDetail.class)
        .eq(PurchaseReturnDetail::getReturnId, purchaseReturn.getId());
    purchaseReturnDetailService.remove(deleteDetailWrapper);

    // 删除退货单
    Wrapper<PurchaseReturn> deleteWrapper = Wrappers.lambdaQuery(PurchaseReturn.class)
        .eq(PurchaseReturn::getId, id).in(PurchaseReturn::getStatus, PurchaseReturnStatus.CREATED,
            PurchaseReturnStatus.APPROVE_REFUSE);
    if (!remove(deleteWrapper)) {
      throw new DefaultClientException("采购退货单信息已过期，请刷新重试！");
    }

    OpLogUtil.setVariable("code", purchaseReturn.getCode());
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public int setUnSettle(String id) {

    Wrapper<PurchaseReturn> updateWrapper = Wrappers.lambdaUpdate(PurchaseReturn.class)
        .set(PurchaseReturn::getSettleStatus, SettleStatus.UN_SETTLE).eq(PurchaseReturn::getId, id)
        .eq(PurchaseReturn::getSettleStatus, SettleStatus.PART_SETTLE);
    int count = getBaseMapper().update(updateWrapper);

    return count;
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public int setPartSettle(String id) {

    Wrapper<PurchaseReturn> updateWrapper = Wrappers.lambdaUpdate(PurchaseReturn.class)
        .set(PurchaseReturn::getSettleStatus, SettleStatus.PART_SETTLE)
        .eq(PurchaseReturn::getId, id)
        .in(PurchaseReturn::getSettleStatus, SettleStatus.UN_SETTLE, SettleStatus.PART_SETTLE);
    int count = getBaseMapper().update(updateWrapper);

    return count;
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public int setSettled(String id) {

    Wrapper<PurchaseReturn> updateWrapper = Wrappers.lambdaUpdate(PurchaseReturn.class)
        .set(PurchaseReturn::getSettleStatus, SettleStatus.SETTLED).eq(PurchaseReturn::getId, id)
        .in(PurchaseReturn::getSettleStatus, SettleStatus.UN_SETTLE, SettleStatus.PART_SETTLE);
    int count = getBaseMapper().update(updateWrapper);

    return count;
  }

  @Override
  public List<PurchaseReturn> getApprovedList(String supplierId, LocalDateTime startTime,
      LocalDateTime endTime,
      SettleStatus settleStatus) {

    return getBaseMapper().getApprovedList(supplierId, startTime, endTime, settleStatus);
  }

  private void create(PurchaseReturn purchaseReturn, CreatePurchaseReturnVo vo,
      boolean requireReceive) {

    StoreCenter sc = storeCenterService.findById(vo.getScId());
    if (sc == null) {
      throw new InputErrorException("仓库不存在！");
    }

    purchaseReturn.setScId(vo.getScId());

    Supplier supplier = supplierService.findById(vo.getSupplierId());
    if (supplier == null) {
      throw new InputErrorException("供应商不存在！");
    }
    purchaseReturn.setSupplierId(vo.getSupplierId());

    if (!StringUtil.isBlank(vo.getPurchaserId())) {
      SysUser purchaser = userService.findById(vo.getPurchaserId());
      if (purchaser == null) {
        throw new InputErrorException("采购员不存在！");
      }

      purchaseReturn.setPurchaserId(vo.getPurchaserId());
    }

    PurchaseConfig purchaseConfig = purchaseConfigService.get();

    GetPaymentDateDto paymentDate = receiveSheetService.getPaymentDate(supplier.getId());

    purchaseReturn.setPaymentDate(
        paymentDate.getAllowModify() ? vo.getPaymentDate() : paymentDate.getPaymentDate());

    if (requireReceive) {

      ReceiveSheet receiveSheet = receiveSheetService.getById(vo.getReceiveSheetId());
      if (receiveSheet == null) {
        throw new DefaultClientException("采购收货单不存在！");
      }

      purchaseReturn.setScId(receiveSheet.getScId());
      purchaseReturn.setSupplierId(receiveSheet.getSupplierId());
      purchaseReturn.setReceiveSheetId(receiveSheet.getId());

      if (!purchaseConfig.getPurchaseReturnMultipleRelateReceive()) {
        Wrapper<PurchaseReturn> checkWrapper = Wrappers.lambdaQuery(PurchaseReturn.class)
            .eq(PurchaseReturn::getReceiveSheetId, receiveSheet.getId())
            .ne(PurchaseReturn::getId, purchaseReturn.getId());
        if (getBaseMapper().selectCount(checkWrapper) > 0) {
          throw new DefaultClientException(
              "采购收货单号：" + receiveSheet.getCode()
                  + "，已关联其他采购退货单，不允许关联多个采购退货单！");
        }
      }
    }

    int returnNum = 0;
    int giftNum = 0;
    BigDecimal totalAmount = BigDecimal.ZERO;
    int orderNo = 1;
    Set<String> selectedSerialNumbers = new HashSet<>();
    for (ReturnProductVo productVo : vo.getProducts()) {
      ReceiveSheetDetail receiveDetail = null;
      if (requireReceive) {
        if (!StringUtil.isBlank(productVo.getReceiveSheetDetailId())) {
          receiveDetail = receiveSheetDetailService.getById(
              productVo.getReceiveSheetDetailId());
          if (receiveDetail == null) {
            throw new InputErrorException("第" + orderNo + "行采购收货单明细不存在！");
          }
          if (!Objects.equals(receiveDetail.getSheetId(), vo.getReceiveSheetId())) {
            throw new InputErrorException("第" + orderNo + "行采购收货单明细不属于所选采购收货单！");
          }
          if (!Objects.equals(receiveDetail.getProductId(), productVo.getProductId())) {
            throw new InputErrorException("第" + orderNo + "行商品与采购收货单明细不一致！");
          }
          productVo.setPurchasePrice(receiveDetail.getTaxPrice());
        } else {
          productVo.setPurchasePrice(BigDecimal.ZERO);
        }
      }

      boolean isGift = productVo.getPurchasePrice().doubleValue() == 0D;

      if (requireReceive) {
        if (StringUtil.isBlank(productVo.getReceiveSheetDetailId())) {
          if (!isGift) {
            throw new InputErrorException("第" + orderNo + "行商品必须为“赠品”！");
          }
        }
      }

      if (isGift) {
        giftNum += productVo.getReturnNum();
      } else {
        returnNum += productVo.getReturnNum();
      }

      totalAmount = NumberUtil.add(totalAmount,
          NumberUtil.mul(productVo.getPurchasePrice(), productVo.getReturnNum()));

      PurchaseReturnDetail detail = new PurchaseReturnDetail();
      detail.setId(IdUtil.getId());
      detail.setReturnId(purchaseReturn.getId());

      Product product = productService.findById(productVo.getProductId());
      if (product == null) {
        throw new InputErrorException("第" + orderNo + "行商品不存在！");
      }

      if (!NumberUtil.isNumberPrecision(productVo.getPurchasePrice(), 2)) {
        throw new InputErrorException("第" + orderNo + "行商品采购价最多允许2位小数！");
      }

      List<ProductStockSerial> selectedSerials = validateSerialSelection(purchaseReturn.getScId(),
          product, receiveDetail, productVo.getReturnNum(), productVo.getSerialNumberList(),
          "第" + orderNo + "行");
      for (ProductStockSerial selectedSerial : selectedSerials) {
        if (!selectedSerialNumbers.add(selectedSerial.getSerialNumber())) {
          throw new InputErrorException(
              "第" + orderNo + "行序列号[" + selectedSerial.getSerialNumber() + "]重复选择！");
        }
      }

      detail.setProductId(productVo.getProductId());
      detail.setReturnNum(productVo.getReturnNum());
      detail.setTaxPrice(productVo.getPurchasePrice());
      detail.setIsGift(isGift);
      detail.setTaxRate(product.getTaxRate());
      detail.setDescription(
          StringUtil.isBlank(productVo.getDescription()) ? StringPool.EMPTY_STR
              : productVo.getDescription());
      detail.setOrderNo(orderNo);
      detail.setSerialNumberList(selectedSerials.stream()
          .map(ProductStockSerial::getSerialNumber)
          .collect(Collectors.joining(",")));
      if (requireReceive && !StringUtil.isBlank(productVo.getReceiveSheetDetailId())) {
        detail.setReceiveSheetDetailId(productVo.getReceiveSheetDetailId());
        receiveSheetDetailService.addReturnNum(productVo.getReceiveSheetDetailId(),
            detail.getReturnNum());
      }

      purchaseReturnDetailService.save(detail);
      orderNo++;
    }
    purchaseReturn.setTotalNum(returnNum);
    purchaseReturn.setTotalGiftNum(giftNum);
    purchaseReturn.setTotalAmount(totalAmount);
    purchaseReturn.setDescription(
        StringUtil.isBlank(vo.getDescription()) ? StringPool.EMPTY_STR : vo.getDescription());
    purchaseReturn.setSettleStatus(this.getInitSettleStatus(supplier));
  }

  /**
   * 根据供应商获取初始结算状态
   *
   * @param supplier
   * @return
   */
  private SettleStatus getInitSettleStatus(Supplier supplier) {

    if (supplier.getManageType() == ManageType.DISTRIBUTION) {
      return SettleStatus.UN_SETTLE;
    } else {
      return SettleStatus.UN_REQUIRE;
    }
  }

  private void sendApprovePassEvent(PurchaseReturn r) {

    ApprovePassOrderDto dto = new ApprovePassOrderDto();
    dto.setId(r.getId());
    dto.setTotalAmount(r.getTotalAmount());
    dto.setApproveTime(r.getApproveTime());

    ApprovePassPurchaseReturnEvent event = new ApprovePassPurchaseReturnEvent(this, dto);

    ApplicationUtil.publishEvent(event);
  }

  private void subBatchStock(PurchaseReturn purchaseReturn, PurchaseReturnDetail returnDetail) {

    Product product = productService.findById(returnDetail.getProductId());
    if (product == null) {
      throw new InputErrorException("采购退货商品不存在！");
    }

    String batchNumber = "DEFAULT";
    if (Boolean.TRUE.equals(product.getIsBatch())) {
      if (StringUtil.isBlank(returnDetail.getReceiveSheetDetailId())) {
        throw new DefaultClientException(
            "（" + product.getCode() + "）" + product.getName() + "启用了批次管理，退货时必须关联采购收货单明细！");
      }
      ReceiveSheetDetail receiveDetail = receiveSheetDetailService.getById(
          returnDetail.getReceiveSheetDetailId());
      if (receiveDetail == null) {
        throw new DefaultClientException("采购收货单明细不存在，无法确定退货批次！");
      }
      batchNumber = StringUtil.isBlank(receiveDetail.getBatchNumber()) ? "DEFAULT"
          : receiveDetail.getBatchNumber();
    }

    ProductStockBatch batch = productStockBatchService.getOne(
        Wrappers.lambdaQuery(ProductStockBatch.class)
            .eq(ProductStockBatch::getScId, purchaseReturn.getScId())
            .eq(ProductStockBatch::getProductId, returnDetail.getProductId())
            .eq(ProductStockBatch::getBatchNumber, batchNumber));
    if (batch == null) {
      throw new DefaultClientException(
          "（" + product.getCode() + "）" + product.getName() + "对应批次库存不存在，不允许退货！");
    }
    if (productStockBatchService.subStock(batch.getId(), returnDetail.getProductId(),
        purchaseReturn.getScId(), returnDetail.getReturnNum()) != 1) {
      throw new DefaultClientException(
          "（" + product.getCode() + "）" + product.getName() + "对应批次库存不足，不允许退货！");
    }
  }

  private Map<String, List<ProductStockSerial>> validateSerialsOnApprove(
      PurchaseReturn purchaseReturn, List<PurchaseReturnDetail> details) {

    Map<String, List<ProductStockSerial>> result = new HashMap<>();
    Set<String> selectedSerialNumbers = new HashSet<>();
    int orderNo = 1;
    for (PurchaseReturnDetail detail : details) {
      Product product = productService.findById(detail.getProductId());
      if (product == null) {
        throw new InputErrorException("第" + orderNo + "行采购退货商品不存在！");
      }

      ReceiveSheetDetail receiveDetail = null;
      if (!StringUtil.isBlank(detail.getReceiveSheetDetailId())) {
        receiveDetail = receiveSheetDetailService.getById(detail.getReceiveSheetDetailId());
      }
      List<ProductStockSerial> serials = validateSerialSelection(purchaseReturn.getScId(), product,
          receiveDetail, detail.getReturnNum(), detail.getSerialNumberList(), "第" + orderNo + "行");
      for (ProductStockSerial serial : serials) {
        if (!selectedSerialNumbers.add(serial.getSerialNumber())) {
          throw new InputErrorException(
              "第" + orderNo + "行序列号[" + serial.getSerialNumber() + "]重复选择！");
        }
      }
      result.put(detail.getId(), serials);
      orderNo++;
    }
    return result;
  }

  private List<ProductStockSerial> validateSerialSelection(String scId, Product product,
      ReceiveSheetDetail receiveDetail, Integer returnNum, String serialNumberList,
      String rowLabel) {

    List<String> serialNumbers = parseSerialNumbers(serialNumberList);
    if (!Boolean.TRUE.equals(product.getIsSerial())) {
      if (!serialNumbers.isEmpty()) {
        throw new InputErrorException(rowLabel + "商品未启用序列号管理，不允许选择序列号！");
      }
      return List.of();
    }

    if (serialNumbers.size() != returnNum) {
      throw new InputErrorException(
          rowLabel + "序列号数量必须与退货数量一致！应为：" + returnNum + "，实际：" + serialNumbers.size());
    }

    Set<String> sourceSerialNumbers = null;
    ProductStockBatch expectedBatch = null;
    if (receiveDetail != null) {
      if (!Objects.equals(receiveDetail.getProductId(), product.getId())) {
        throw new InputErrorException(rowLabel + "采购收货单明细与商品不一致！");
      }
      sourceSerialNumbers = new HashSet<>(parseSerialNumbers(receiveDetail.getSerialNumberList()));
      expectedBatch = findReturnBatch(scId, product, receiveDetail);
      if (expectedBatch == null) {
        throw new DefaultClientException(rowLabel + "采购收货批次库存不存在，无法校验序列号！");
      }
    }

    List<ProductStockSerial> result = new ArrayList<>();
    for (String serialNumber : serialNumbers) {
      if (sourceSerialNumbers != null && !sourceSerialNumbers.contains(serialNumber)) {
        throw new InputErrorException(
            rowLabel + "序列号[" + serialNumber + "]不属于所选采购收货明细！");
      }

      ProductStockSerial serial = productStockSerialService.getOne(
          Wrappers.lambdaQuery(ProductStockSerial.class)
              .eq(ProductStockSerial::getSerialNumber, serialNumber));
      if (serial == null) {
        throw new InputErrorException(rowLabel + "序列号[" + serialNumber + "]不存在！");
      }
      if (!Objects.equals(serial.getProductId(), product.getId())) {
        throw new InputErrorException(rowLabel + "序列号[" + serialNumber + "]与商品不匹配！");
      }
      if (!Objects.equals(serial.getStockStatus(), 1)) {
        throw new DefaultClientException(rowLabel + "序列号[" + serialNumber + "]已不在库！");
      }

      ProductStockBatch serialBatch = productStockBatchService.findById(serial.getBatchId());
      if (serialBatch == null || !Objects.equals(serialBatch.getScId(), scId)
          || !Objects.equals(serialBatch.getProductId(), product.getId())) {
        throw new InputErrorException(rowLabel + "序列号[" + serialNumber + "]不属于当前仓库商品库存！");
      }
      if (expectedBatch != null && !Objects.equals(expectedBatch.getId(), serial.getBatchId())) {
        throw new InputErrorException(
            rowLabel + "序列号[" + serialNumber + "]不属于所选采购收货批次！");
      }
      result.add(serial);
    }
    return result;
  }

  private void subSerialStock(PurchaseReturnDetail detail,
      List<ProductStockSerial> serials) {

    for (ProductStockSerial serial : serials) {
      boolean updated = productStockSerialService.update(
          Wrappers.lambdaUpdate(ProductStockSerial.class)
              .set(ProductStockSerial::getStockStatus, 0)
              .eq(ProductStockSerial::getId, serial.getId())
              .eq(ProductStockSerial::getProductId, detail.getProductId())
              .eq(ProductStockSerial::getBatchId, serial.getBatchId())
              .eq(ProductStockSerial::getStockStatus, 1));
      if (!updated) {
        throw new DefaultClientException(
            "序列号[" + serial.getSerialNumber() + "]库存状态已变化，采购退货失败！");
      }
    }
  }

  private ProductStockBatch findReturnBatch(String scId, Product product,
      ReceiveSheetDetail receiveDetail) {

    String batchNumber = StringUtil.isBlank(receiveDetail.getBatchNumber()) ? "DEFAULT"
        : receiveDetail.getBatchNumber();
    return productStockBatchService.getOne(
        Wrappers.lambdaQuery(ProductStockBatch.class)
            .eq(ProductStockBatch::getScId, scId)
            .eq(ProductStockBatch::getProductId, product.getId())
            .eq(ProductStockBatch::getBatchNumber, batchNumber));
  }

  private List<String> parseSerialNumbers(String serialNumberList) {

    if (StringUtil.isBlank(serialNumberList)) {
      return List.of();
    }

    Set<String> serialNumbers = new LinkedHashSet<>();
    for (String value : serialNumberList.split("[,，\\r\\n]+")) {
      String serialNumber = value.trim();
      if (StringUtil.isBlank(serialNumber)) {
        continue;
      }
      if (!serialNumbers.add(serialNumber)) {
        throw new InputErrorException("序列号[" + serialNumber + "]重复！");
      }
    }
    return new ArrayList<>(serialNumbers);
  }
}
