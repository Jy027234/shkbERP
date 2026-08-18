package com.lframework.xingyun.sc.impl.stock.take;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageInfo;
import com.lframework.starter.common.constants.StringPool;
import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.common.utils.Assert;
import com.lframework.starter.common.utils.CollectionUtil;
import com.lframework.starter.common.utils.ObjectUtil;
import com.lframework.starter.common.utils.StringUtil;
import com.lframework.starter.web.core.components.security.SecurityUtil;
import com.lframework.starter.web.core.impl.BaseMpServiceImpl;
import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.utils.IdUtil;
import com.lframework.starter.web.core.utils.PageHelperUtil;
import com.lframework.starter.web.core.utils.PageResultUtil;
import com.lframework.xingyun.basedata.entity.Product;
import com.lframework.xingyun.basedata.enums.ProductType;
import com.lframework.xingyun.basedata.service.product.ProductService;
import com.lframework.xingyun.core.annotations.OpLog;
import com.lframework.xingyun.core.annotations.OrderTimeLineLog;
import com.lframework.xingyun.core.enums.OrderTimeLineBizType;
import com.lframework.xingyun.core.service.GenerateCodeService;
import com.lframework.xingyun.core.utils.OpLogUtil;
import com.lframework.xingyun.sc.components.code.GenerateCodeTypePool;
import com.lframework.xingyun.sc.dto.stock.take.sheet.TakeStockSheetFullDto;
import com.lframework.xingyun.sc.dto.stock.take.sheet.TakeStockSheetProductDto;
import com.lframework.xingyun.sc.entity.ProductStockBatch;
import com.lframework.xingyun.sc.entity.TakeStockPlan;
import com.lframework.xingyun.sc.entity.TakeStockSheet;
import com.lframework.xingyun.sc.entity.TakeStockSheetDetail;
import com.lframework.xingyun.sc.entity.TakeStockSheetDetailBatch;
import com.lframework.xingyun.sc.entity.TakeStockSheetDetailSerial;
import com.lframework.xingyun.sc.enums.ScOpLogType;
import com.lframework.xingyun.sc.enums.TakeStockPlanStatus;
import com.lframework.xingyun.sc.enums.TakeStockPlanType;
import com.lframework.xingyun.sc.enums.TakeStockSheetStatus;
import com.lframework.xingyun.sc.events.stock.take.DeleteTakeStockPlanEvent;
import com.lframework.xingyun.sc.mappers.TakeStockSheetMapper;
import com.lframework.xingyun.sc.service.stock.ProductStockBatchService;
import com.lframework.xingyun.sc.service.stock.take.TakeStockPlanDetailService;
import com.lframework.xingyun.sc.service.stock.take.TakeStockPlanService;
import com.lframework.xingyun.sc.service.stock.take.TakeStockSheetDetailBatchService;
import com.lframework.xingyun.sc.service.stock.take.TakeStockSheetDetailSerialService;
import com.lframework.xingyun.sc.service.stock.take.TakeStockSheetDetailService;
import com.lframework.xingyun.sc.service.stock.take.TakeStockSheetService;
import com.lframework.xingyun.sc.vo.stock.take.sheet.ApprovePassTakeStockSheetVo;
import com.lframework.xingyun.sc.vo.stock.take.sheet.ApproveRefuseTakeStockSheetVo;
import com.lframework.xingyun.sc.vo.stock.take.sheet.CreateTakeStockSheetVo;
import com.lframework.xingyun.sc.vo.stock.take.sheet.QueryTakeStockSheetProductVo;
import com.lframework.xingyun.sc.vo.stock.take.sheet.QueryTakeStockSheetVo;
import com.lframework.xingyun.sc.vo.stock.take.sheet.TakeStockBatchDetailVo;
import com.lframework.xingyun.sc.vo.stock.take.sheet.TakeStockSerialDetailVo;
import com.lframework.xingyun.sc.vo.stock.take.sheet.TakeStockSheetProductVo;
import com.lframework.xingyun.sc.vo.stock.take.sheet.UpdateTakeStockSheetVo;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TakeStockSheetServiceImpl extends
    BaseMpServiceImpl<TakeStockSheetMapper, TakeStockSheet>
    implements TakeStockSheetService {

  @Autowired
  private TakeStockSheetDetailService takeStockSheetDetailService;

  @Autowired
  private TakeStockSheetDetailBatchService takeStockSheetDetailBatchService;

  @Autowired
  private TakeStockSheetDetailSerialService takeStockSheetDetailSerialService;

  @Autowired
  private ProductStockBatchService productStockBatchService;

  @Autowired
  private GenerateCodeService generateCodeService;

  @Autowired
  private TakeStockPlanService takeStockPlanService;

  @Autowired
  private TakeStockPlanDetailService takeStockPlanDetailService;

  @Autowired
  private ProductService productService;

  @Override
  public PageResult<TakeStockSheet> query(Integer pageIndex, Integer pageSize,
      QueryTakeStockSheetVo vo) {

    Assert.greaterThanZero(pageIndex);
    Assert.greaterThanZero(pageSize);

    PageHelperUtil.startPage(pageIndex, pageSize);
    List<TakeStockSheet> datas = this.query(vo);

    return PageResultUtil.convert(new PageInfo<>(datas));
  }

  @Override
  public List<TakeStockSheet> query(QueryTakeStockSheetVo vo) {

    return getBaseMapper().query(vo);
  }

  @Override
  public TakeStockSheetFullDto getDetail(String id) {

    TakeStockSheetFullDto data = getBaseMapper().getDetail(id);
    if (data == null) {
      throw new DefaultClientException("盘点单不存在！");
    }

    return data;
  }

  @OpLog(type = ScOpLogType.TAKE_STOCK, name = "新增盘点单，ID：{}", params = {"#id"})
  @OrderTimeLineLog(type = OrderTimeLineBizType.CREATE, orderId = "#_result", name = "创建盘点单")
  @Transactional(rollbackFor = Exception.class)
  @Override
  public String create(CreateTakeStockSheetVo vo) {

    TakeStockSheet data = new TakeStockSheet();
    data.setId(IdUtil.getId());
    data.setCode(generateCodeService.generate(GenerateCodeTypePool.TAKE_STOCK_SHEET));
    data.setPlanId(vo.getPlanId());
    data.setPreSheetId(vo.getPreSheetId());

    TakeStockPlan takeStockPlan = takeStockPlanService.getByIdForUpdate(vo.getPlanId());
    if (takeStockPlan == null) {
      throw new DefaultClientException("盘点任务不存在！");
    }

    if (takeStockPlan.getTakeStatus() != TakeStockPlanStatus.CREATED) {
      throw new DefaultClientException("关联盘点任务的盘点状态已改变，不允许进行新增！");
    }
    validateProducts(takeStockPlan, vo.getProducts());

    data.setScId(takeStockPlan.getScId());
    data.setStatus(TakeStockSheetStatus.CREATED);
    data.setDescription(
        StringUtil.isBlank(vo.getDescription()) ? StringPool.EMPTY_STR : vo.getDescription());

    getBaseMapper().insert(data);

    int orderNo = 1;
    for (TakeStockSheetProductVo product : vo.getProducts()) {
      TakeStockSheetDetail detail = new TakeStockSheetDetail();
      detail.setId(IdUtil.getId());
      detail.setSheetId(data.getId());
      detail.setProductId(product.getProductId());
      detail.setTakeNum(product.getTakeNum());
      detail.setDescription(
          StringUtil.isBlank(product.getDescription()) ? StringPool.EMPTY_STR
              : product.getDescription());
      detail.setOrderNo(orderNo++);

      takeStockSheetDetailService.save(detail);

      Product takeProduct = productService.findById(product.getProductId());
      saveTraceDetails(data.getId(), detail.getId(), data.getScId(), takeProduct, product);
    }

    // 盘点任务如果是单品盘点
    if (takeStockPlan.getTakeType() == TakeStockPlanType.SIMPLE) {
      takeStockPlanDetailService.savePlanDetailBySimple(takeStockPlan.getId(),
          vo.getProducts().stream().map(TakeStockSheetProductVo::getProductId)
              .collect(Collectors.toList()));
    }

    OpLogUtil.setVariable("id", data.getId());
    OpLogUtil.setExtra(vo);

    return data.getId();
  }

  @OpLog(type = ScOpLogType.TAKE_STOCK, name = "修改盘点单，ID：{}", params = {"#id"})
  @OrderTimeLineLog(type = OrderTimeLineBizType.UPDATE, orderId = "#_result", name = "修改盘点单")
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void update(UpdateTakeStockSheetVo vo) {

    TakeStockSheet data = getBaseMapper().selectById(vo.getId());
    if (ObjectUtil.isNull(data)) {
      throw new DefaultClientException("盘点单不存在！");
    }

    TakeStockPlan takeStockPlan = takeStockPlanService.getByIdForUpdate(data.getPlanId());
    if (takeStockPlan == null) {
      throw new DefaultClientException("盘点任务不存在！");
    }

    if (takeStockPlan.getTakeStatus() != TakeStockPlanStatus.CREATED) {
      throw new DefaultClientException("关联盘点任务的盘点状态已改变，不允许进行修改！");
    }
    validateProducts(takeStockPlan, vo.getProducts());

    LambdaUpdateWrapper<TakeStockSheet> updateWrapper = Wrappers.lambdaUpdate(TakeStockSheet.class)
        .set(TakeStockSheet::getDescription,
            StringUtil.isBlank(vo.getDescription()) ? StringPool.EMPTY_STR : vo.getDescription())
        .set(TakeStockSheet::getApproveBy, null).set(TakeStockSheet::getApproveTime, null)
        .set(TakeStockSheet::getRefuseReason, null)
        .set(TakeStockSheet::getStatus, TakeStockSheetStatus.CREATED)
        .eq(TakeStockSheet::getId, vo.getId())
        .in(TakeStockSheet::getStatus, TakeStockSheetStatus.CREATED,
            TakeStockSheetStatus.APPROVE_REFUSE);

    if (getBaseMapper().update(updateWrapper) != 1) {
      throw new DefaultClientException("盘点单信息已过期，请刷新重试！");
    }

    // 删除明细
    Wrapper<TakeStockSheetDetail> deleteDetailWrapper = Wrappers.lambdaQuery(
            TakeStockSheetDetail.class)
        .eq(TakeStockSheetDetail::getSheetId, data.getId());
    takeStockSheetDetailService.remove(deleteDetailWrapper);

    // 删除批次/序列号明细
    Wrapper<TakeStockSheetDetailBatch> deleteBatchWrapper = Wrappers.lambdaQuery(
            TakeStockSheetDetailBatch.class)
        .eq(TakeStockSheetDetailBatch::getSheetId, data.getId());
    takeStockSheetDetailBatchService.remove(deleteBatchWrapper);
    Wrapper<TakeStockSheetDetailSerial> deleteSerialWrapper = Wrappers.lambdaQuery(
            TakeStockSheetDetailSerial.class)
        .eq(TakeStockSheetDetailSerial::getSheetId, data.getId());
    takeStockSheetDetailSerialService.remove(deleteSerialWrapper);

    int orderNo = 1;
    for (TakeStockSheetProductVo product : vo.getProducts()) {
      TakeStockSheetDetail detail = new TakeStockSheetDetail();
      detail.setId(IdUtil.getId());
      detail.setSheetId(data.getId());
      detail.setProductId(product.getProductId());
      detail.setTakeNum(product.getTakeNum());
      detail.setDescription(
          StringUtil.isBlank(product.getDescription()) ? StringPool.EMPTY_STR
              : product.getDescription());
      detail.setOrderNo(orderNo++);

      takeStockSheetDetailService.save(detail);

      Product takeProduct = productService.findById(product.getProductId());
      saveTraceDetails(data.getId(), detail.getId(), data.getScId(), takeProduct, product);
    }

    // 盘点任务如果是单品盘点
    if (takeStockPlan.getTakeType() == TakeStockPlanType.SIMPLE) {
      takeStockPlanDetailService.savePlanDetailBySimple(takeStockPlan.getId(),
          vo.getProducts().stream().map(TakeStockSheetProductVo::getProductId)
              .collect(Collectors.toList()));
      takeStockPlanDetailService.deleteUnusedSimpleDetails(takeStockPlan.getId());
    }

    OpLogUtil.setVariable("id", data.getId());
    OpLogUtil.setExtra(vo);
  }

  @OpLog(type = ScOpLogType.TAKE_STOCK, name = "审核通过盘点单，ID：{}", params = {"#id"})
  @OrderTimeLineLog(type = OrderTimeLineBizType.APPROVE_PASS, orderId = "#vo.id", name = "审核通过")
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void approvePass(ApprovePassTakeStockSheetVo vo) {

    TakeStockSheet data = getBaseMapper().selectById(vo.getId());
    if (ObjectUtil.isNull(data)) {
      throw new DefaultClientException("盘点单不存在！");
    }

    TakeStockPlan takeStockPlan = takeStockPlanService.getByIdForUpdate(data.getPlanId());
    if (takeStockPlan == null) {
      throw new DefaultClientException("盘点任务不存在！");
    }

    if (takeStockPlan.getTakeStatus() != TakeStockPlanStatus.CREATED) {
      throw new DefaultClientException("关联盘点任务的盘点状态已改变，不允许继续执行审核！");
    }

    Wrapper<TakeStockSheet> updateWrapper = Wrappers.lambdaUpdate(TakeStockSheet.class)
        .set(TakeStockSheet::getApproveBy, SecurityUtil.getCurrentUser().getId())
        .set(TakeStockSheet::getApproveTime, LocalDateTime.now())
        .set(TakeStockSheet::getStatus, TakeStockSheetStatus.APPROVE_PASS)
        .eq(TakeStockSheet::getId, data.getId())
        .in(TakeStockSheet::getStatus, TakeStockSheetStatus.CREATED,
            TakeStockSheetStatus.APPROVE_REFUSE);
    if (getBaseMapper().update(updateWrapper) != 1) {
      throw new DefaultClientException("盘点单信息已过期，请刷新重试！");
    }

    Wrapper<TakeStockSheetDetail> queryDetailWrapper = Wrappers.lambdaQuery(
            TakeStockSheetDetail.class)
        .eq(TakeStockSheetDetail::getSheetId, data.getId())
        .orderByAsc(TakeStockSheetDetail::getOrderNo);
    List<TakeStockSheetDetail> details = takeStockSheetDetailService.list(queryDetailWrapper);
    for (TakeStockSheetDetail detail : details) {
      if (takeStockPlanDetailService.updateOriTakeNum(data.getPlanId(), detail.getProductId(),
          detail.getTakeNum()) != 1) {
        throw new DefaultClientException("盘点商品不属于关联盘点任务，请刷新后重试！");
      }
    }

    OpLogUtil.setVariable("id", data.getId());
    OpLogUtil.setExtra(vo);
  }

  @OrderTimeLineLog(type = OrderTimeLineBizType.APPROVE_PASS, orderId = "#_result", name = "直接审核通过")
  @Transactional(rollbackFor = Exception.class)
  @Override
  public String directApprovePass(CreateTakeStockSheetVo vo) {

    TakeStockSheetService thisService = getThis(this.getClass());

    String id = thisService.create(vo);

    ApprovePassTakeStockSheetVo approveVo = new ApprovePassTakeStockSheetVo();
    approveVo.setId(id);

    thisService.approvePass(approveVo);

    return id;
  }

  @OpLog(type = ScOpLogType.TAKE_STOCK, name = "审核拒绝盘点单，ID：{}", params = {"#id"})
  @OrderTimeLineLog(type = OrderTimeLineBizType.APPROVE_RETURN, orderId = "#vo.id", name = "审核拒绝，拒绝理由：{}", params = "#vo.refuseReason")
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void approveRefuse(ApproveRefuseTakeStockSheetVo vo) {

    TakeStockSheet data = getBaseMapper().selectById(vo.getId());
    if (ObjectUtil.isNull(data)) {
      throw new DefaultClientException("盘点单不存在！");
    }

    TakeStockPlan takeStockPlan = takeStockPlanService.getByIdForUpdate(data.getPlanId());
    if (takeStockPlan == null) {
      throw new DefaultClientException("盘点任务不存在！");
    }

    if (takeStockPlan.getTakeStatus() != TakeStockPlanStatus.CREATED) {
      throw new DefaultClientException("关联盘点任务的盘点状态已改变，不允许继续执行审核！");
    }

    Wrapper<TakeStockSheet> updateWrapper = Wrappers.lambdaUpdate(TakeStockSheet.class)
        .set(TakeStockSheet::getApproveBy, SecurityUtil.getCurrentUser().getId())
        .set(TakeStockSheet::getApproveTime, LocalDateTime.now())
        .set(TakeStockSheet::getRefuseReason,
            StringUtil.isBlank(vo.getRefuseReason()) ? StringPool.EMPTY_STR : vo.getRefuseReason())
        .set(TakeStockSheet::getStatus, TakeStockSheetStatus.APPROVE_REFUSE)
        .eq(TakeStockSheet::getId, data.getId())
        .eq(TakeStockSheet::getStatus, TakeStockSheetStatus.CREATED);
    if (getBaseMapper().update(updateWrapper) != 1) {
      throw new DefaultClientException("盘点单信息已过期，请刷新重试！");
    }

    OpLogUtil.setVariable("id", data.getId());
    OpLogUtil.setExtra(vo);
  }

  @OrderTimeLineLog(type = OrderTimeLineBizType.CANCEL_APPROVE, orderId = "#id", name = "取消审核")
  @OpLog(type = ScOpLogType.TAKE_STOCK, name = "取消审核通过盘点单，ID：{}", params = {"#id"})
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void cancelApprovePass(String id) {

    TakeStockSheet data = getBaseMapper().selectById(id);
    if (ObjectUtil.isNull(data)) {
      throw new DefaultClientException("盘点单不存在！");
    }

    TakeStockPlan takeStockPlan = takeStockPlanService.getByIdForUpdate(data.getPlanId());
    if (takeStockPlan == null) {
      throw new DefaultClientException("盘点任务不存在！");
    }

    if (takeStockPlan.getTakeStatus() != TakeStockPlanStatus.CREATED) {
      throw new DefaultClientException("关联盘点任务的盘点状态已改变，不允许执行取消审核！");
    }

    LambdaUpdateWrapper<TakeStockSheet> updateWrapper = Wrappers.lambdaUpdate(TakeStockSheet.class)
        .set(TakeStockSheet::getApproveBy, null).set(TakeStockSheet::getApproveTime, null)
        .set(TakeStockSheet::getRefuseReason, null)
        .set(TakeStockSheet::getStatus, TakeStockSheetStatus.CREATED)
        .eq(TakeStockSheet::getId, data.getId())
        .eq(TakeStockSheet::getStatus, TakeStockSheetStatus.APPROVE_PASS);

    if (getBaseMapper().update(updateWrapper) != 1) {
      throw new DefaultClientException("盘点单信息已过期，请刷新重试！");
    }

    Wrapper<TakeStockSheetDetail> queryDetailWrapper = Wrappers.lambdaQuery(
            TakeStockSheetDetail.class)
        .eq(TakeStockSheetDetail::getSheetId, data.getId())
        .orderByAsc(TakeStockSheetDetail::getOrderNo);
    List<TakeStockSheetDetail> details = takeStockSheetDetailService.list(queryDetailWrapper);
    for (TakeStockSheetDetail detail : details) {
      if (takeStockPlanDetailService.updateOriTakeNum(data.getPlanId(), detail.getProductId(),
          -detail.getTakeNum()) != 1) {
        throw new DefaultClientException("盘点商品不属于关联盘点任务，请刷新后重试！");
      }
    }

    OpLogUtil.setVariable("id", data.getId());
  }

  @OpLog(type = ScOpLogType.TAKE_STOCK, name = "删除盘点单，ID：{}", params = {"#id"})
  @OrderTimeLineLog(orderId = "#id", delete = true)
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void deleteById(String id) {

    TakeStockSheet data = getBaseMapper().selectById(id);
    if (ObjectUtil.isNull(data)) {
      throw new DefaultClientException("盘点单不存在！");
    }

    TakeStockPlan takeStockPlan = takeStockPlanService.getByIdForUpdate(data.getPlanId());
    if (takeStockPlan == null) {
      throw new DefaultClientException("盘点任务不存在！");
    }

    Wrapper<TakeStockSheet> deleteWrapper = Wrappers.lambdaQuery(TakeStockSheet.class)
        .eq(TakeStockSheet::getId, id)
        .in(TakeStockSheet::getStatus, TakeStockSheetStatus.CREATED,
            TakeStockSheetStatus.APPROVE_REFUSE);
    if (getBaseMapper().delete(deleteWrapper) != 1) {
      throw new DefaultClientException("盘点单信息已过期，请刷新重试！");
    }

    Wrapper<TakeStockSheetDetail> deleteDetailWrapper = Wrappers.lambdaQuery(
            TakeStockSheetDetail.class)
        .eq(TakeStockSheetDetail::getSheetId, data.getId());
    takeStockSheetDetailService.remove(deleteDetailWrapper);
    if (takeStockPlan.getTakeType() == TakeStockPlanType.SIMPLE) {
      takeStockPlanDetailService.deleteUnusedSimpleDetails(takeStockPlan.getId());
    }
  }

  @Override
  public Boolean hasRelatePreTakeStockSheet(String preSheetId) {

    return getBaseMapper().hasRelatePreTakeStockSheet(preSheetId);
  }

  @Override
  public Boolean hasUnApprove(String planId) {

    return getBaseMapper().hasUnApprove(planId);
  }

  @Override
  public PageResult<TakeStockSheetProductDto> queryTakeStockByCondition(Integer pageIndex,
      Integer pageSize, String planId, String condition) {

    Assert.greaterThanZero(pageIndex);
    Assert.greaterThanZero(pageSize);

    PageHelperUtil.startPage(pageIndex, pageSize);

    List<TakeStockSheetProductDto> datas = getBaseMapper().queryTakeStockByCondition(planId,
        condition);
    PageResult<TakeStockSheetProductDto> pageResult = PageResultUtil.convert(new PageInfo<>(datas));

    return pageResult;
  }

  @Override
  public PageResult<TakeStockSheetProductDto> queryTakeStockList(Integer pageIndex,
      Integer pageSize, QueryTakeStockSheetProductVo vo) {

    Assert.greaterThanZero(pageIndex);
    Assert.greaterThanZero(pageSize);

    PageHelperUtil.startPage(pageIndex, pageSize);

    List<TakeStockSheetProductDto> datas = getBaseMapper().queryTakeStockList(vo);
    PageResult<TakeStockSheetProductDto> pageResult = PageResultUtil.convert(new PageInfo<>(datas));

    return pageResult;
  }

  @Override
  public void cleanCacheByKey(Serializable key) {

  }

  private void validateProducts(TakeStockPlan takeStockPlan,
      List<TakeStockSheetProductVo> products) {

    Set<String> productIds = new HashSet<>();
    int orderNo = 1;
    for (TakeStockSheetProductVo productVo : products) {
      if (!productIds.add(productVo.getProductId())) {
        throw new DefaultClientException("第" + orderNo + "行航材重复录入！");
      }
      if (productVo.getTakeNum() == null || productVo.getTakeNum() < 0) {
        throw new DefaultClientException("第" + orderNo + "行盘点数量不能小于0！");
      }

      Product product = productService.findById(productVo.getProductId());
      if (product == null || product.getProductType() != ProductType.NORMAL) {
        throw new DefaultClientException("第" + orderNo + "行航材不存在或类型不支持盘点！");
      }
      if (takeStockPlan.getTakeType() != TakeStockPlanType.SIMPLE
          && takeStockPlanDetailService.getByPlanIdAndProductId(takeStockPlan.getId(),
          productVo.getProductId()) == null) {
        throw new DefaultClientException("第" + orderNo + "行航材不属于关联盘点任务！");
      }

      if (Boolean.TRUE.equals(product.getIsBatch())) {
        if (!CollectionUtil.isEmpty(productVo.getSerialDetails())) {
          throw new DefaultClientException("第" + orderNo + "行航材为批次管理，不允许录入序列号明细！");
        }
        if (CollectionUtil.isEmpty(productVo.getBatchDetails())) {
          throw new DefaultClientException(
              "第" + orderNo + "行航材启用了批次管理，必须逐批次录入盘点明细！");
        }
        Set<String> batchNumbers = new HashSet<>();
        int batchSum = 0;
        for (TakeStockBatchDetailVo batchVo : productVo.getBatchDetails()) {
          if (!batchNumbers.add(batchVo.getBatchNumber())) {
            throw new DefaultClientException(
                "第" + orderNo + "行航材批次[" + batchVo.getBatchNumber() + "]重复提交！");
          }
          if (batchVo.getTakeNum() == null || batchVo.getTakeNum() < 0) {
            throw new DefaultClientException(
                "第" + orderNo + "行航材批次[" + batchVo.getBatchNumber() + "]实盘数量不能小于0！");
          }
          batchSum += batchVo.getTakeNum();
        }
        if (batchSum != productVo.getTakeNum()) {
          throw new DefaultClientException(
              "第" + orderNo + "行航材批次实盘数量合计必须等于盘点数量！");
        }
      } else if (Boolean.TRUE.equals(product.getIsSerial())) {
        if (!CollectionUtil.isEmpty(productVo.getBatchDetails())) {
          throw new DefaultClientException("第" + orderNo + "行航材为序列号管理，不允许录入批次明细！");
        }
        if (CollectionUtil.isEmpty(productVo.getSerialDetails())) {
          throw new DefaultClientException(
              "第" + orderNo + "行航材启用了序列号管理，必须逐序列号录入盘点明细！");
        }
        Set<String> serialNumbers = new HashSet<>();
        int presentCount = 0;
        for (TakeStockSerialDetailVo serialVo : productVo.getSerialDetails()) {
          if (!serialNumbers.add(serialVo.getSerialNumber())) {
            throw new DefaultClientException(
                "第" + orderNo + "行航材序列号[" + serialVo.getSerialNumber() + "]重复提交！");
          }
          if (serialVo.getTakeStatus() == null
              || (serialVo.getTakeStatus() != 1 && serialVo.getTakeStatus() != 0)) {
            throw new DefaultClientException(
                "第" + orderNo + "行航材序列号[" + serialVo.getSerialNumber() + "]实盘状态不正确！");
          }
          if (serialVo.getTakeStatus() == 1) {
            presentCount++;
          }
        }
        if (presentCount != productVo.getTakeNum()) {
          throw new DefaultClientException(
              "第" + orderNo + "行航材实盘在库序列号数量必须等于盘点数量！");
        }
      } else if (!CollectionUtil.isEmpty(productVo.getBatchDetails())
          || !CollectionUtil.isEmpty(productVo.getSerialDetails())) {
        throw new DefaultClientException(
            "第" + orderNo + "行航材未启用批次/序列号管理，不允许录入批次或序列号明细！");
      }
      orderNo++;
    }
  }

  private void saveTraceDetails(String sheetId, String detailId, String scId, Product product,
      TakeStockSheetProductVo productVo) {

    if (Boolean.TRUE.equals(product.getIsBatch())) {
      if (CollectionUtil.isEmpty(productVo.getBatchDetails())) {
        return;
      }
      List<ProductStockBatch> stockBatches = productStockBatchService.list(
          Wrappers.lambdaQuery(ProductStockBatch.class)
              .eq(ProductStockBatch::getScId, scId)
              .eq(ProductStockBatch::getProductId, product.getId()));
      Map<String, Integer> batchStockMap = new HashMap<>();
      for (ProductStockBatch stockBatch : stockBatches) {
        batchStockMap.put(stockBatch.getBatchNumber(), stockBatch.getQuantity());
      }
      for (TakeStockBatchDetailVo batchVo : productVo.getBatchDetails()) {
        TakeStockSheetDetailBatch batch = new TakeStockSheetDetailBatch();
        batch.setId(IdUtil.getId());
        batch.setSheetId(sheetId);
        batch.setSheetDetailId(detailId);
        batch.setProductId(product.getId());
        batch.setBatchNumber(batchVo.getBatchNumber());
        batch.setStockNum(batchStockMap.getOrDefault(batchVo.getBatchNumber(), 0));
        batch.setTakeNum(batchVo.getTakeNum());
        batch.setDescription(
            StringUtil.isBlank(batchVo.getDescription()) ? StringPool.EMPTY_STR
                : batchVo.getDescription());
        batch.setCreateTime(LocalDateTime.now());

        takeStockSheetDetailBatchService.save(batch);
      }
    } else if (Boolean.TRUE.equals(product.getIsSerial())) {
      if (CollectionUtil.isEmpty(productVo.getSerialDetails())) {
        return;
      }
      for (TakeStockSerialDetailVo serialVo : productVo.getSerialDetails()) {
        TakeStockSheetDetailSerial serial = new TakeStockSheetDetailSerial();
        serial.setId(IdUtil.getId());
        serial.setSheetId(sheetId);
        serial.setSheetDetailId(detailId);
        serial.setProductId(product.getId());
        serial.setSerialNumber(serialVo.getSerialNumber());
        serial.setBatchNumber(serialVo.getBatchNumber());
        serial.setTakeStatus(serialVo.getTakeStatus());
        serial.setDescription(
            StringUtil.isBlank(serialVo.getDescription()) ? StringPool.EMPTY_STR
                : serialVo.getDescription());
        serial.setCreateTime(LocalDateTime.now());

        takeStockSheetDetailSerialService.save(serial);
      }
    }
  }

  @Service
  public static class DeleteTakeStockPlanListener implements
      ApplicationListener<DeleteTakeStockPlanEvent> {

    @Autowired
    private TakeStockSheetService takeStockSheetService;

    @Autowired
    private TakeStockSheetDetailService takeStockSheetDetailService;

    @Autowired
    private TakeStockSheetDetailBatchService takeStockSheetDetailBatchService;

    @Autowired
    private TakeStockSheetDetailSerialService takeStockSheetDetailSerialService;

    @OpLog(type = ScOpLogType.TAKE_STOCK, name = "删除库存盘点表，ID：{}", params = "#ids", loopFormat = true)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void onApplicationEvent(DeleteTakeStockPlanEvent deleteTakeStockPlanEvent) {

      Wrapper<TakeStockSheet> deleteWrapper = Wrappers.lambdaQuery(TakeStockSheet.class)
          .eq(TakeStockSheet::getPlanId, deleteTakeStockPlanEvent.getId());
      List<TakeStockSheet> sheets = takeStockSheetService.list(deleteWrapper);

      List<String> ids = CollectionUtil.emptyList();

      if (!CollectionUtil.isEmpty(sheets)) {
        ids = sheets.stream().map(TakeStockSheet::getId).collect(Collectors.toList());
        Wrapper<TakeStockSheetDetail> deleteDetailWrapper = Wrappers.lambdaQuery(
                TakeStockSheetDetail.class)
            .in(TakeStockSheetDetail::getSheetId, ids);
        takeStockSheetDetailService.remove(deleteDetailWrapper);
        Wrapper<TakeStockSheetDetailBatch> deleteBatchWrapper = Wrappers.lambdaQuery(
                TakeStockSheetDetailBatch.class)
            .in(TakeStockSheetDetailBatch::getSheetId, ids);
        takeStockSheetDetailBatchService.remove(deleteBatchWrapper);
        Wrapper<TakeStockSheetDetailSerial> deleteSerialWrapper = Wrappers.lambdaQuery(
                TakeStockSheetDetailSerial.class)
            .in(TakeStockSheetDetailSerial::getSheetId, ids);
        takeStockSheetDetailSerialService.remove(deleteSerialWrapper);
      }

      takeStockSheetService.remove(deleteWrapper);

      OpLogUtil.setVariable("ids", ids);
    }
  }
}
