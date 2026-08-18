package com.lframework.xingyun.sc.impl.stock.adjust;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
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
import com.lframework.xingyun.core.service.GenerateCodeService;
import com.lframework.starter.web.core.utils.EnumUtil;
import com.lframework.starter.web.core.utils.IdUtil;
import com.lframework.starter.web.core.utils.PageHelperUtil;
import com.lframework.starter.web.core.utils.PageResultUtil;
import com.lframework.xingyun.basedata.entity.Product;
import com.lframework.xingyun.basedata.entity.ProductPurchase;
import com.lframework.xingyun.basedata.enums.ProductType;
import com.lframework.xingyun.basedata.service.product.ProductPurchaseService;
import com.lframework.xingyun.basedata.service.product.ProductService;
import com.lframework.xingyun.basedata.service.storecenter.StoreCenterService;
import com.lframework.xingyun.core.annotations.OrderTimeLineLog;
import com.lframework.xingyun.core.enums.OrderTimeLineBizType;
import com.lframework.xingyun.sc.components.code.GenerateCodeTypePool;
import com.lframework.xingyun.sc.dto.stock.adjust.stock.StockAdjustProductDto;
import com.lframework.xingyun.sc.dto.stock.adjust.stock.StockAdjustSheetFullDto;
import com.lframework.xingyun.sc.entity.ProductStockBatch;
import com.lframework.xingyun.sc.entity.ProductStockSerial;
import com.lframework.xingyun.sc.entity.StockAdjustSheet;
import com.lframework.xingyun.sc.entity.StockAdjustSheetDetail;
import com.lframework.xingyun.sc.entity.StockAdjustSheetDetailBatch;
import com.lframework.xingyun.sc.entity.StockAdjustSheetDetailSerial;
import com.lframework.xingyun.sc.entity.StockAdjustReason;
import com.lframework.xingyun.sc.enums.ProductStockBizType;
import com.lframework.xingyun.sc.enums.ScOpLogType;
import com.lframework.xingyun.sc.enums.StockAdjustSheetBizType;
import com.lframework.xingyun.sc.enums.StockAdjustSheetStatus;
import com.lframework.xingyun.sc.mappers.StockAdjustSheetMapper;
import com.lframework.xingyun.sc.service.stock.ProductStockBatchService;
import com.lframework.xingyun.sc.service.stock.ProductStockSerialService;
import com.lframework.xingyun.sc.service.stock.ProductStockService;
import com.lframework.xingyun.sc.service.stock.adjust.StockAdjustSheetDetailBatchService;
import com.lframework.xingyun.sc.service.stock.adjust.StockAdjustSheetDetailSerialService;
import com.lframework.xingyun.sc.service.stock.adjust.StockAdjustSheetDetailService;
import com.lframework.xingyun.sc.service.stock.adjust.StockAdjustReasonService;
import com.lframework.xingyun.sc.service.stock.adjust.StockAdjustSheetService;
import com.lframework.xingyun.sc.vo.stock.AddProductStockBatchVo;
import com.lframework.xingyun.sc.vo.stock.AddProductStockVo;
import com.lframework.xingyun.sc.vo.stock.SubProductStockBatchVo;
import com.lframework.xingyun.sc.vo.stock.SubProductStockVo;
import com.lframework.xingyun.sc.vo.stock.adjust.stock.ApprovePassStockAdjustSheetVo;
import com.lframework.xingyun.sc.vo.stock.adjust.stock.ApproveRefuseStockAdjustSheetVo;
import com.lframework.xingyun.sc.vo.stock.adjust.stock.CreateStockAdjustSheetVo;
import com.lframework.xingyun.sc.vo.stock.adjust.stock.QueryStockAdjustProductVo;
import com.lframework.xingyun.sc.vo.stock.adjust.stock.QueryStockAdjustSheetVo;
import com.lframework.xingyun.sc.vo.stock.adjust.stock.StockAdjustBatchDetailVo;
import com.lframework.xingyun.sc.vo.stock.adjust.stock.StockAdjustProductVo;
import com.lframework.xingyun.sc.vo.stock.adjust.stock.StockAdjustSerialDetailVo;
import com.lframework.xingyun.sc.vo.stock.adjust.stock.UpdateStockAdjustSheetVo;
import com.lframework.xingyun.core.annotations.OpLog;
import com.lframework.xingyun.core.utils.OpLogUtil;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockAdjustSheetServiceImpl extends
    BaseMpServiceImpl<StockAdjustSheetMapper, StockAdjustSheet>
    implements StockAdjustSheetService {

  @Autowired
  private StockAdjustSheetDetailService stockAdjustSheetDetailService;

  @Autowired
  private StockAdjustSheetDetailBatchService stockAdjustSheetDetailBatchService;

  @Autowired
  private StockAdjustSheetDetailSerialService stockAdjustSheetDetailSerialService;

  @Autowired
  private ProductStockBatchService productStockBatchService;

  @Autowired
  private ProductStockSerialService productStockSerialService;

  @Autowired
  private GenerateCodeService generateCodeService;

  @Autowired
  private ProductStockService productStockService;

  @Autowired
  private ProductPurchaseService productPurchaseService;

  @Autowired
  private ProductService productService;

  @Autowired
  private StoreCenterService storeCenterService;

  @Autowired
  private StockAdjustReasonService stockAdjustReasonService;

  @Override
  public PageResult<StockAdjustSheet> query(Integer pageIndex, Integer pageSize,
      QueryStockAdjustSheetVo vo) {

    Assert.greaterThanZero(pageIndex);
    Assert.greaterThanZero(pageSize);

    PageHelperUtil.startPage(pageIndex, pageSize);
    List<StockAdjustSheet> datas = this.query(vo);

    return PageResultUtil.convert(new PageInfo<>(datas));
  }

  @Override
  public List<StockAdjustSheet> query(QueryStockAdjustSheetVo vo) {

    return getBaseMapper().query(vo);
  }

  @Override
  public StockAdjustSheetFullDto getDetail(String id) {

    return getBaseMapper().getDetail(id);
  }

  @OpLog(type = ScOpLogType.STOCK_ADJUST, name = "新增库存调整单，ID：{}", params = {"#id"})
  @OrderTimeLineLog(type = OrderTimeLineBizType.CREATE, orderId = "#_result", name = "创建调整单")
  @Transactional(rollbackFor = Exception.class)
  @Override
  public String create(CreateStockAdjustSheetVo vo) {

    StockAdjustSheet data = new StockAdjustSheet();
    data.setId(IdUtil.getId());
    data.setCode(generateCodeService.generate(GenerateCodeTypePool.STOCK_ADJUST_SHEET));

    this.create(data, vo);

    getBaseMapper().insert(data);

    OpLogUtil.setVariable("id", data.getId());
    OpLogUtil.setExtra(vo);

    return data.getId();
  }

  @OpLog(type = ScOpLogType.STOCK_ADJUST, name = "修改库存调整单，ID：{}", params = {"#id"})
  @OrderTimeLineLog(type = OrderTimeLineBizType.UPDATE, orderId = "#vo.id", name = "修改调整单")
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void update(UpdateStockAdjustSheetVo vo) {

    StockAdjustSheet data = getBaseMapper().selectByIdForUpdate(vo.getId());
    if (ObjectUtil.isNull(data)) {
      throw new DefaultClientException("库存调整单不存在！");
    }

    if (data.getStatus() != StockAdjustSheetStatus.CREATED
        && data.getStatus() != StockAdjustSheetStatus.APPROVE_REFUSE) {

      if (data.getStatus() == StockAdjustSheetStatus.APPROVE_PASS) {
        throw new DefaultClientException("库存调整单已审核通过，无法修改！");
      }

      throw new DefaultClientException("库存调整单无法修改！");
    }

    // 删除出库单明细
    Wrapper<StockAdjustSheetDetail> deleteDetailWrapper = Wrappers.lambdaQuery(
            StockAdjustSheetDetail.class)
        .eq(StockAdjustSheetDetail::getSheetId, data.getId());
    stockAdjustSheetDetailService.remove(deleteDetailWrapper);

    // 删除批次/序列号明细
    Wrapper<StockAdjustSheetDetailBatch> deleteBatchWrapper = Wrappers.lambdaQuery(
            StockAdjustSheetDetailBatch.class)
        .eq(StockAdjustSheetDetailBatch::getSheetId, data.getId());
    stockAdjustSheetDetailBatchService.remove(deleteBatchWrapper);
    Wrapper<StockAdjustSheetDetailSerial> deleteSerialWrapper = Wrappers.lambdaQuery(
            StockAdjustSheetDetailSerial.class)
        .eq(StockAdjustSheetDetailSerial::getSheetId, data.getId());
    stockAdjustSheetDetailSerialService.remove(deleteSerialWrapper);

    this.create(data, vo);

    data.setStatus(StockAdjustSheetStatus.CREATED);

    List<StockAdjustSheetStatus> statusList = new ArrayList<>();
    statusList.add(StockAdjustSheetStatus.CREATED);
    statusList.add(StockAdjustSheetStatus.APPROVE_REFUSE);

    Wrapper<StockAdjustSheet> updateSheetWrapper = Wrappers.lambdaUpdate(
            StockAdjustSheet.class)
        .set(StockAdjustSheet::getApproveBy, null)
        .set(StockAdjustSheet::getApproveTime, null)
        .set(StockAdjustSheet::getRefuseReason, StringPool.EMPTY_STR)
        .eq(StockAdjustSheet::getId, data.getId())
        .in(StockAdjustSheet::getStatus, statusList);
    if (getBaseMapper().updateAllColumn(data, updateSheetWrapper) != 1) {
      throw new DefaultClientException("库存调整单信息已过期，请刷新重试！");
    }

    OpLogUtil.setVariable("id", data.getId());
    OpLogUtil.setExtra(vo);
  }

  @OpLog(type = ScOpLogType.STOCK_ADJUST, name = "删除库存调整单，ID：{}", params = {"#id"})
  @OrderTimeLineLog(orderId = "#id", delete = true)
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void deleteById(String id) {

    StockAdjustSheet data = getBaseMapper().selectByIdForUpdate(id);
    if (ObjectUtil.isNull(data)) {
      throw new DefaultClientException("库存调整单不存在！");
    }

    if (data.getStatus() == StockAdjustSheetStatus.APPROVE_PASS) {
      throw new DefaultClientException("“审核通过”的库存调整单不允许执行删除操作！");
    }

    Wrapper<StockAdjustSheet> deleteWrapper = Wrappers.lambdaQuery(StockAdjustSheet.class)
        .eq(StockAdjustSheet::getId, id)
        .in(StockAdjustSheet::getStatus, StockAdjustSheetStatus.CREATED,
            StockAdjustSheetStatus.APPROVE_REFUSE);
    if (getBaseMapper().delete(deleteWrapper) != 1) {
      throw new DefaultClientException("库存调整单信息已过期，请刷新重试！");
    }

    Wrapper<StockAdjustSheetDetail> deleteDetailWrapper = Wrappers.lambdaQuery(
            StockAdjustSheetDetail.class)
        .eq(StockAdjustSheetDetail::getSheetId, id);
    stockAdjustSheetDetailService.remove(deleteDetailWrapper);

    Wrapper<StockAdjustSheetDetailBatch> deleteBatchWrapper = Wrappers.lambdaQuery(
            StockAdjustSheetDetailBatch.class)
        .eq(StockAdjustSheetDetailBatch::getSheetId, id);
    stockAdjustSheetDetailBatchService.remove(deleteBatchWrapper);
    Wrapper<StockAdjustSheetDetailSerial> deleteSerialWrapper = Wrappers.lambdaQuery(
            StockAdjustSheetDetailSerial.class)
        .eq(StockAdjustSheetDetailSerial::getSheetId, id);
    stockAdjustSheetDetailSerialService.remove(deleteSerialWrapper);
  }

  @OpLog(type = ScOpLogType.STOCK_ADJUST, name = "审核通过库存调整单，ID：{}", params = {"#vo.id"})
  @OrderTimeLineLog(type = OrderTimeLineBizType.APPROVE_PASS, orderId = "#vo.id", name = "审核通过")
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void approvePass(ApprovePassStockAdjustSheetVo vo) {

    StockAdjustSheet data = getBaseMapper().selectByIdForUpdate(vo.getId());
    if (ObjectUtil.isNull(data)) {
      throw new DefaultClientException("库存调整单不存在！");
    }

    if (data.getStatus() != StockAdjustSheetStatus.CREATED
        && data.getStatus() != StockAdjustSheetStatus.APPROVE_REFUSE) {

      if (data.getStatus() == StockAdjustSheetStatus.APPROVE_PASS) {
        throw new DefaultClientException("库存调整单已审核通过，不允许继续执行审核！");
      }

      throw new DefaultClientException("库存调整单无法审核通过！");
    }

    Wrapper<StockAdjustSheetDetail> queryDetailWrapper = Wrappers.lambdaQuery(
            StockAdjustSheetDetail.class)
        .eq(StockAdjustSheetDetail::getSheetId, data.getId())
        .orderByAsc(StockAdjustSheetDetail::getOrderNo);
    List<StockAdjustSheetDetail> details = stockAdjustSheetDetailService.list(
        queryDetailWrapper);
    validateStoredSheet(data, details);

    LocalDateTime now = LocalDateTime.now();
    Wrapper<StockAdjustSheet> updateWrapper = Wrappers.lambdaUpdate(StockAdjustSheet.class)
        .eq(StockAdjustSheet::getId, data.getId())
        .in(StockAdjustSheet::getStatus, StockAdjustSheetStatus.CREATED,
            StockAdjustSheetStatus.APPROVE_REFUSE)
        .set(StockAdjustSheet::getApproveBy, SecurityUtil.getCurrentUser().getId())
        .set(StockAdjustSheet::getApproveTime, now)
        .set(StockAdjustSheet::getStatus, StockAdjustSheetStatus.APPROVE_PASS)
        .set(StockAdjustSheet::getDescription,
            StringUtil.isBlank(vo.getDescription()) ? StringPool.EMPTY_STR : vo.getDescription());
    if (getBaseMapper().update(updateWrapper) != 1) {
      throw new DefaultClientException("库存调整单信息已过期，请刷新重试！");
    }

    for (StockAdjustSheetDetail detail : details) {
      Product product = productService.findById(detail.getProductId());
      if (Boolean.TRUE.equals(product.getIsBatch())) {
        List<StockAdjustSheetDetailBatch> batchRows = stockAdjustSheetDetailBatchService.list(
            Wrappers.lambdaQuery(StockAdjustSheetDetailBatch.class)
                .eq(StockAdjustSheetDetailBatch::getSheetDetailId, detail.getId()));
        applyBatchStockChange(data, detail, product, batchRows, now);
        continue;
      }
      if (Boolean.TRUE.equals(product.getIsSerial())) {
        List<StockAdjustSheetDetailSerial> serialRows = stockAdjustSheetDetailSerialService.list(
            Wrappers.lambdaQuery(StockAdjustSheetDetailSerial.class)
                .eq(StockAdjustSheetDetailSerial::getSheetDetailId, detail.getId()));
        applySerialStockChange(data, detail, product, serialRows, now);
        continue;
      }
      if (data.getBizType() == StockAdjustSheetBizType.IN) {
        ProductPurchase productPurchase = productPurchaseService.getById(product.getId());
        // 入库
        AddProductStockVo addProductStockVo = new AddProductStockVo();
        addProductStockVo.setProductId(product.getId());
        addProductStockVo.setScId(data.getScId());
        addProductStockVo.setStockNum(detail.getStockNum());
        addProductStockVo.setDefaultTaxPrice(productPurchase.getPrice());
        addProductStockVo.setCreateTime(now);
        addProductStockVo.setBizId(data.getId());
        addProductStockVo.setBizDetailId(detail.getId());
        addProductStockVo.setBizCode(data.getCode());
        addProductStockVo.setBizType(ProductStockBizType.STOCK_ADJUST.getCode());

        productStockService.addStock(addProductStockVo);
      } else {
        SubProductStockVo subProductStockVo = new SubProductStockVo();
        subProductStockVo.setProductId(product.getId());
        subProductStockVo.setScId(data.getScId());
        subProductStockVo.setStockNum(detail.getStockNum());
        subProductStockVo.setCreateTime(now);
        subProductStockVo.setBizId(data.getId());
        subProductStockVo.setBizDetailId(detail.getId());
        subProductStockVo.setBizCode(data.getCode());
        subProductStockVo.setBizType(ProductStockBizType.STOCK_ADJUST.getCode());

        productStockService.subStock(subProductStockVo);
      }
    }
  }

  /**
   * 批次管理航材调整：按批次明细逐批次入库/出库，总库存按原明细数量统一调整，
   * 保证批次合计与总库存账一致；负库存由批次条件更新守卫拒绝。
   */
  private void applyBatchStockChange(StockAdjustSheet data, StockAdjustSheetDetail detail,
      Product product, List<StockAdjustSheetDetailBatch> batchRows, LocalDateTime now) {

    for (StockAdjustSheetDetailBatch batchRow : batchRows) {
      if (data.getBizType() == StockAdjustSheetBizType.IN) {
        ProductStockBatch stockBatch = productStockBatchService.getOne(
            Wrappers.lambdaQuery(ProductStockBatch.class)
                .eq(ProductStockBatch::getScId, data.getScId())
                .eq(ProductStockBatch::getProductId, detail.getProductId())
                .eq(ProductStockBatch::getBatchNumber, batchRow.getBatchNumber()));
        String batchId;
        if (stockBatch == null) {
          ProductStockBatch newBatch = new ProductStockBatch();
          newBatch.setId(IdUtil.getId());
          newBatch.setScId(data.getScId());
          newBatch.setProductId(detail.getProductId());
          newBatch.setQuantity(0);
          newBatch.setBatchNumber(batchRow.getBatchNumber());
          newBatch.setCreateTime(now);
          productStockBatchService.save(newBatch);
          batchId = newBatch.getId();
        } else {
          batchId = stockBatch.getId();
        }
        AddProductStockBatchVo addBatchVo = new AddProductStockBatchVo();
        addBatchVo.setProductId(detail.getProductId());
        addBatchVo.setScId(data.getScId());
        addBatchVo.setStockBatchId(batchId);
        addBatchVo.setStockNum(batchRow.getStockNum());
        addBatchVo.setCreateTime(now);
        addBatchVo.setBizId(data.getId());
        addBatchVo.setBizDetailId(detail.getId());
        addBatchVo.setBizCode(data.getCode());
        addBatchVo.setBizType(ProductStockBizType.STOCK_ADJUST.getCode());

        productStockService.addStockBatch(addBatchVo);
      } else {
        ProductStockBatch stockBatch = productStockBatchService.getOne(
            Wrappers.lambdaQuery(ProductStockBatch.class)
                .eq(ProductStockBatch::getScId, data.getScId())
                .eq(ProductStockBatch::getProductId, detail.getProductId())
                .eq(ProductStockBatch::getBatchNumber, batchRow.getBatchNumber()));
        if (stockBatch == null) {
          throw new DefaultClientException(
              "航材（" + product.getCode() + "）" + product.getName()
                  + "批次[" + batchRow.getBatchNumber() + "]库存不存在，无法调整出库！");
        }
        SubProductStockBatchVo subBatchVo = new SubProductStockBatchVo();
        subBatchVo.setProductId(detail.getProductId());
        subBatchVo.setScId(data.getScId());
        subBatchVo.setStockBatchId(stockBatch.getId());
        subBatchVo.setStockNum(batchRow.getStockNum());
        subBatchVo.setCreateTime(now);
        subBatchVo.setBizId(data.getId());
        subBatchVo.setBizDetailId(detail.getId());
        subBatchVo.setBizCode(data.getCode());
        subBatchVo.setBizType(ProductStockBizType.STOCK_ADJUST.getCode());

        productStockService.subStockBatch(subBatchVo);
      }
    }

    applyProductTotalChange(data, detail, product, now);
  }

  /**
   * 序列号管理航材调整：一条序列号一条明细；入库仅允许新序列号或已出库序列号
   * 重新入库（0->1），出库仅允许在库序列号出库（1->0），不允许状态直跳；
   * 重复序列号由唯一键与状态条件更新拒绝。
   */
  private void applySerialStockChange(StockAdjustSheet data, StockAdjustSheetDetail detail,
      Product product, List<StockAdjustSheetDetailSerial> serialRows, LocalDateTime now) {

    for (StockAdjustSheetDetailSerial serialRow : serialRows) {
      ProductStockSerial stockSerial = productStockSerialService.getOne(
          Wrappers.lambdaQuery(ProductStockSerial.class)
              .eq(ProductStockSerial::getSerialNumber, serialRow.getSerialNumber()));
      if (data.getBizType() == StockAdjustSheetBizType.IN) {
        if (stockSerial != null) {
          if (!detail.getProductId().equals(stockSerial.getProductId())) {
            throw new DefaultClientException(
                "序列号[" + serialRow.getSerialNumber() + "]不属于当前调整航材！");
          }
          if (stockSerial.getStockStatus() == 1) {
            throw new DefaultClientException(
                "航材（" + product.getCode() + "）" + product.getName()
                    + "序列号[" + serialRow.getSerialNumber() + "]已在库，不允许重复入库！");
          }
          ProductStockBatch stockBatch = productStockBatchService.getById(stockSerial.getBatchId());
          if (stockBatch == null || !data.getScId().equals(stockBatch.getScId())) {
            throw new DefaultClientException(
                "序列号[" + serialRow.getSerialNumber() + "]所属批次不在调整仓库，跨仓请走调拨流程！");
          }
          if (productStockSerialService.updateStatus(stockSerial.getId(), 0, 1) != 1) {
            throw new DefaultClientException("序列号状态已变化，调整入库处理失败！");
          }
          if (productStockBatchService.addStock(stockSerial.getBatchId(), detail.getProductId(),
              data.getScId(), 1) != 1) {
            throw new DefaultClientException("批次库存已变化，调整入库处理失败！");
          }
        } else {
          if (StringUtil.isBlank(serialRow.getBatchNumber())) {
            throw new DefaultClientException(
                "航材（" + product.getCode() + "）" + product.getName()
                    + "入库序列号[" + serialRow.getSerialNumber() + "]必须指定所属批次号！");
          }
          ProductStockBatch stockBatch = productStockBatchService.getOne(
              Wrappers.lambdaQuery(ProductStockBatch.class)
                  .eq(ProductStockBatch::getScId, data.getScId())
                  .eq(ProductStockBatch::getProductId, detail.getProductId())
                  .eq(ProductStockBatch::getBatchNumber, serialRow.getBatchNumber()));
          String batchId;
          if (stockBatch == null) {
            ProductStockBatch newBatch = new ProductStockBatch();
            newBatch.setId(IdUtil.getId());
            newBatch.setScId(data.getScId());
            newBatch.setProductId(detail.getProductId());
            newBatch.setQuantity(0);
            newBatch.setBatchNumber(serialRow.getBatchNumber());
            newBatch.setCreateTime(now);
            productStockBatchService.save(newBatch);
            batchId = newBatch.getId();
          } else {
            batchId = stockBatch.getId();
          }
          ProductStockSerial newSerial = new ProductStockSerial();
          newSerial.setId(IdUtil.getId());
          newSerial.setProductId(detail.getProductId());
          newSerial.setSerialNumber(serialRow.getSerialNumber());
          newSerial.setStockStatus(1);
          newSerial.setBatchId(batchId);
          newSerial.setCreateTime(now);
          productStockSerialService.save(newSerial);
          if (productStockBatchService.addStock(batchId, detail.getProductId(), data.getScId(),
              1) != 1) {
            throw new DefaultClientException("批次库存已变化，调整入库处理失败！");
          }
        }
      } else {
        if (stockSerial == null) {
          throw new DefaultClientException(
              "航材（" + product.getCode() + "）" + product.getName()
                  + "序列号[" + serialRow.getSerialNumber() + "]库存不存在，无法调整出库！");
        }
        if (!detail.getProductId().equals(stockSerial.getProductId())) {
          throw new DefaultClientException(
              "序列号[" + serialRow.getSerialNumber() + "]不属于当前调整航材！");
        }
        if (stockSerial.getStockStatus() == 0) {
          throw new DefaultClientException(
              "航材（" + product.getCode() + "）" + product.getName()
                  + "序列号[" + serialRow.getSerialNumber() + "]已出库，不允许重复出库！");
        }
        ProductStockBatch stockBatch = productStockBatchService.getById(stockSerial.getBatchId());
        if (stockBatch == null || !data.getScId().equals(stockBatch.getScId())) {
          throw new DefaultClientException(
              "序列号[" + serialRow.getSerialNumber() + "]所属批次不在调整仓库，跨仓请走调拨流程！");
        }
        if (productStockSerialService.updateStatus(stockSerial.getId(), 1, 0) != 1) {
          throw new DefaultClientException("序列号状态已变化，调整出库处理失败！");
        }
        if (productStockBatchService.subStock(stockSerial.getBatchId(), detail.getProductId(),
            data.getScId(), 1) != 1) {
          throw new DefaultClientException("批次库存不足或已变化，调整出库处理失败！");
        }
      }
    }

    applyProductTotalChange(data, detail, product, now);
  }

  /**
   * 普通航材总库存调整（入库/出库）。
   */
  private void applyProductTotalChange(StockAdjustSheet data, StockAdjustSheetDetail detail,
      Product product, LocalDateTime now) {

    if (data.getBizType() == StockAdjustSheetBizType.IN) {
      ProductPurchase productPurchase = productPurchaseService.getById(product.getId());
      AddProductStockVo addProductStockVo = new AddProductStockVo();
      addProductStockVo.setProductId(product.getId());
      addProductStockVo.setScId(data.getScId());
      addProductStockVo.setStockNum(detail.getStockNum());
      addProductStockVo.setDefaultTaxPrice(productPurchase.getPrice());
      addProductStockVo.setCreateTime(now);
      addProductStockVo.setBizId(data.getId());
      addProductStockVo.setBizDetailId(detail.getId());
      addProductStockVo.setBizCode(data.getCode());
      addProductStockVo.setBizType(ProductStockBizType.STOCK_ADJUST.getCode());

      productStockService.addStock(addProductStockVo);
    } else {
      SubProductStockVo subProductStockVo = new SubProductStockVo();
      subProductStockVo.setProductId(product.getId());
      subProductStockVo.setScId(data.getScId());
      subProductStockVo.setStockNum(detail.getStockNum());
      subProductStockVo.setCreateTime(now);
      subProductStockVo.setBizId(data.getId());
      subProductStockVo.setBizDetailId(detail.getId());
      subProductStockVo.setBizCode(data.getCode());
      subProductStockVo.setBizType(ProductStockBizType.STOCK_ADJUST.getCode());

      productStockService.subStock(subProductStockVo);
    }
  }

  @OrderTimeLineLog(type = OrderTimeLineBizType.APPROVE_PASS, orderId = "#_result", name = "直接审核通过")
  @Transactional(rollbackFor = Exception.class)
  @Override
  public String directApprovePass(CreateStockAdjustSheetVo vo) {

    StockAdjustSheetService thisService = getThis(this.getClass());

    String id = thisService.create(vo);

    ApprovePassStockAdjustSheetVo approvePassVo = new ApprovePassStockAdjustSheetVo();
    approvePassVo.setId(id);
    approvePassVo.setDescription(vo.getDescription());

    thisService.approvePass(approvePassVo);

    return id;
  }

  @OpLog(type = ScOpLogType.STOCK_ADJUST, name = "审核拒绝库存调整单，ID：{}", params = {"#id"})
  @OrderTimeLineLog(type = OrderTimeLineBizType.APPROVE_RETURN, orderId = "#vo.id", name = "审核拒绝，拒绝理由：{}", params = "#vo.refuseReason")
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void approveRefuse(ApproveRefuseStockAdjustSheetVo vo) {

    StockAdjustSheet data = getBaseMapper().selectByIdForUpdate(vo.getId());
    if (ObjectUtil.isNull(data)) {
      throw new DefaultClientException("库存调整单不存在！");
    }

    if (data.getStatus() != StockAdjustSheetStatus.CREATED
        && data.getStatus() != StockAdjustSheetStatus.APPROVE_REFUSE) {

      if (data.getStatus() == StockAdjustSheetStatus.APPROVE_PASS) {
        throw new DefaultClientException("库存调整单已审核通过，不允许继续执行审核！");
      }

      throw new DefaultClientException("库存调整单无法审核通过！");
    }

    Wrapper<StockAdjustSheet> updateWrapper = Wrappers.lambdaUpdate(StockAdjustSheet.class)
        .eq(StockAdjustSheet::getId, data.getId())
        .in(StockAdjustSheet::getStatus, StockAdjustSheetStatus.CREATED,
            StockAdjustSheetStatus.APPROVE_REFUSE)
        .set(StockAdjustSheet::getApproveBy, SecurityUtil.getCurrentUser().getId())
        .set(StockAdjustSheet::getApproveTime, LocalDateTime.now())
        .set(StockAdjustSheet::getRefuseReason, vo.getRefuseReason())
        .set(StockAdjustSheet::getStatus, StockAdjustSheetStatus.APPROVE_REFUSE);
    if (getBaseMapper().update(updateWrapper) != 1) {
      throw new DefaultClientException("库存调整单信息已过期，请刷新重试！");
    }

    OpLogUtil.setVariable("id", data.getId());
    OpLogUtil.setExtra(vo);
  }

  @Override
  public PageResult<StockAdjustProductDto> queryStockAdjustByCondition(Integer pageIndex,
      Integer pageSize, String scId, String condition) {

    Assert.greaterThanZero(pageIndex);
    Assert.greaterThanZero(pageSize);

    PageHelperUtil.startPage(pageIndex, pageSize);

    List<StockAdjustProductDto> datas = getBaseMapper().queryStockAdjustByCondition(scId,
        condition);
    PageResult<StockAdjustProductDto> pageResult = PageResultUtil.convert(
        new PageInfo<>(datas));

    return pageResult;
  }

  @Override
  public PageResult<StockAdjustProductDto> queryStockAdjustList(Integer pageIndex,
      Integer pageSize, QueryStockAdjustProductVo vo) {

    Assert.greaterThanZero(pageIndex);
    Assert.greaterThanZero(pageSize);

    PageHelperUtil.startPage(pageIndex, pageSize);

    List<StockAdjustProductDto> datas = getBaseMapper().queryStockAdjustList(vo);
    PageResult<StockAdjustProductDto> pageResult = PageResultUtil.convert(
        new PageInfo<>(datas));

    return pageResult;
  }

  @Override
  public void cleanCacheByKey(Serializable key) {

  }

  private void create(StockAdjustSheet data, CreateStockAdjustSheetVo vo) {

    data.setScId(vo.getScId());
    data.setStatus(StockAdjustSheetStatus.CREATED);
    data.setDescription(
        StringUtil.isBlank(vo.getDescription()) ? StringPool.EMPTY_STR : vo.getDescription());
    data.setBizType(EnumUtil.getByCode(StockAdjustSheetBizType.class, vo.getBizType()));
    data.setReasonId(vo.getReasonId());

    validateReferences(data);
    validateProducts(data.getBizType(), vo.getProducts());

    int orderNo = 1;
    for (StockAdjustProductVo product : vo.getProducts()) {
      StockAdjustSheetDetail detail = new StockAdjustSheetDetail();
      detail.setId(IdUtil.getId());
      detail.setSheetId(data.getId());
      detail.setProductId(product.getProductId());
      detail.setStockNum(product.getStockNum());
      detail.setDescription(
          StringUtil.isBlank(product.getDescription()) ? StringPool.EMPTY_STR
              : product.getDescription());
      detail.setOrderNo(orderNo++);

      stockAdjustSheetDetailService.save(detail);

      Product adjustProduct = productService.findById(product.getProductId());
      saveTraceDetails(data.getId(), detail.getId(), data.getScId(), adjustProduct, product);
    }
  }

  private void validateReferences(StockAdjustSheet data) {

    if (storeCenterService.findById(data.getScId()) == null) {
      throw new DefaultClientException("仓库不存在！");
    }
    StockAdjustReason reason = stockAdjustReasonService.findById(data.getReasonId());
    if (reason == null) {
      throw new DefaultClientException("库存调整原因不存在！");
    }
    if (!Boolean.TRUE.equals(reason.getAvailable())) {
      throw new DefaultClientException("库存调整原因已停用！");
    }
  }

  private void validateProducts(StockAdjustSheetBizType bizType,
      List<StockAdjustProductVo> products) {

    Set<String> productIds = new HashSet<>();
    int orderNo = 1;
    for (StockAdjustProductVo productVo : products) {
      if (!productIds.add(productVo.getProductId())) {
        throw new DefaultClientException("第" + orderNo + "行航材重复录入！");
      }
      if (productVo.getStockNum() == null || productVo.getStockNum() <= 0) {
        throw new DefaultClientException("第" + orderNo + "行航材的调整库存数量必须大于0！");
      }
      Product product = validateProduct(bizType, productVo.getProductId(), orderNo);

      if (Boolean.TRUE.equals(product.getIsBatch())) {
        if (!CollectionUtil.isEmpty(productVo.getSerialDetails())) {
          throw new DefaultClientException("第" + orderNo + "行航材为批次管理，不允许录入序列号明细！");
        }
        if (CollectionUtil.isEmpty(productVo.getBatchDetails())) {
          throw new DefaultClientException(
              "第" + orderNo + "行航材启用了批次管理，必须逐批次指定调整明细！");
        }
        Set<String> batchNumbers = new HashSet<>();
        int batchSum = 0;
        for (StockAdjustBatchDetailVo batchVo : productVo.getBatchDetails()) {
          if (!batchNumbers.add(batchVo.getBatchNumber())) {
            throw new DefaultClientException(
                "第" + orderNo + "行航材批次[" + batchVo.getBatchNumber() + "]重复提交！");
          }
          if (batchVo.getStockNum() == null || batchVo.getStockNum() <= 0) {
            throw new DefaultClientException(
                "第" + orderNo + "行航材批次[" + batchVo.getBatchNumber() + "]调整数量必须大于0！");
          }
          batchSum += batchVo.getStockNum();
        }
        if (batchSum != productVo.getStockNum()) {
          throw new DefaultClientException(
              "第" + orderNo + "行航材批次调整数量合计必须等于调整库存数量！");
        }
      } else if (Boolean.TRUE.equals(product.getIsSerial())) {
        if (!CollectionUtil.isEmpty(productVo.getBatchDetails())) {
          throw new DefaultClientException("第" + orderNo + "行航材为序列号管理，不允许录入批次明细！");
        }
        if (CollectionUtil.isEmpty(productVo.getSerialDetails())) {
          throw new DefaultClientException(
              "第" + orderNo + "行航材启用了序列号管理，必须逐序列号指定调整明细！");
        }
        Set<String> serialNumbers = new HashSet<>();
        for (StockAdjustSerialDetailVo serialVo : productVo.getSerialDetails()) {
          if (!serialNumbers.add(serialVo.getSerialNumber())) {
            throw new DefaultClientException(
                "第" + orderNo + "行航材序列号[" + serialVo.getSerialNumber() + "]重复提交！");
          }
        }
        if (serialNumbers.size() != productVo.getStockNum()) {
          throw new DefaultClientException(
              "第" + orderNo + "行航材序列号明细数量必须等于调整库存数量！");
        }
      } else if (!CollectionUtil.isEmpty(productVo.getBatchDetails())
          || !CollectionUtil.isEmpty(productVo.getSerialDetails())) {
        throw new DefaultClientException(
            "第" + orderNo + "行航材未启用批次/序列号管理，不允许录入批次或序列号明细！");
      }
      orderNo++;
    }
  }

  private void validateStoredSheet(StockAdjustSheet data, List<StockAdjustSheetDetail> details) {

    validateReferences(data);
    if (details.isEmpty()) {
      throw new DefaultClientException("库存调整单不存在航材明细，不允许审核！");
    }
    int orderNo = 1;
    for (StockAdjustSheetDetail detail : details) {
      if (detail.getStockNum() == null || detail.getStockNum() <= 0) {
        throw new DefaultClientException("第" + orderNo + "行航材的调整库存数量必须大于0！");
      }
      Product product = validateProduct(data.getBizType(), detail.getProductId(), orderNo);
      if (Boolean.TRUE.equals(product.getIsBatch())) {
        List<StockAdjustSheetDetailBatch> batchRows = stockAdjustSheetDetailBatchService.list(
            Wrappers.lambdaQuery(StockAdjustSheetDetailBatch.class)
                .eq(StockAdjustSheetDetailBatch::getSheetDetailId, detail.getId()));
        if (CollectionUtil.isEmpty(batchRows)) {
          throw new DefaultClientException(
              "航材（" + product.getCode() + "）" + product.getName()
                  + "启用了批次管理，当前库存调整单缺少批次明细，不允许调整库存！");
        }
        int batchSum = batchRows.stream()
            .mapToInt(StockAdjustSheetDetailBatch::getStockNum).sum();
        if (batchSum != detail.getStockNum()) {
          throw new DefaultClientException(
              "航材（" + product.getCode() + "）" + product.getName()
                  + "批次调整数量合计与调整库存数量不一致，请修改后重新提交！");
        }
      } else if (Boolean.TRUE.equals(product.getIsSerial())) {
        List<StockAdjustSheetDetailSerial> serialRows = stockAdjustSheetDetailSerialService.list(
            Wrappers.lambdaQuery(StockAdjustSheetDetailSerial.class)
                .eq(StockAdjustSheetDetailSerial::getSheetDetailId, detail.getId()));
        if (CollectionUtil.isEmpty(serialRows)) {
          throw new DefaultClientException(
              "航材（" + product.getCode() + "）" + product.getName()
                  + "启用了序列号管理，当前库存调整单缺少序列号明细，不允许调整库存！");
        }
        if (serialRows.size() != detail.getStockNum()) {
          throw new DefaultClientException(
              "航材（" + product.getCode() + "）" + product.getName()
                  + "序列号明细数量与调整库存数量不一致，请修改后重新提交！");
        }
      }
      orderNo++;
    }
  }

  private Product validateProduct(StockAdjustSheetBizType bizType, String productId, int orderNo) {

    Product product = productService.findById(productId);
    if (product == null || product.getProductType() != ProductType.NORMAL) {
      throw new DefaultClientException("第" + orderNo + "行航材不存在或类型不支持库存调整！");
    }
    if (bizType == StockAdjustSheetBizType.IN) {
      ProductPurchase purchase = productPurchaseService.getById(productId);
      if (purchase == null || purchase.getPrice() == null) {
        throw new DefaultClientException(
            "航材（" + product.getCode() + "）" + product.getName() + "没有采购价格，无法调整入库！");
      }
    }
    return product;
  }

  private void saveTraceDetails(String sheetId, String detailId, String scId, Product product,
      StockAdjustProductVo productVo) {

    if (Boolean.TRUE.equals(product.getIsBatch())) {
      if (CollectionUtil.isEmpty(productVo.getBatchDetails())) {
        return;
      }
      for (StockAdjustBatchDetailVo batchVo : productVo.getBatchDetails()) {
        StockAdjustSheetDetailBatch batch = new StockAdjustSheetDetailBatch();
        batch.setId(IdUtil.getId());
        batch.setSheetId(sheetId);
        batch.setSheetDetailId(detailId);
        batch.setProductId(product.getId());
        batch.setBatchNumber(batchVo.getBatchNumber());
        batch.setStockNum(batchVo.getStockNum());
        batch.setDescription(
            StringUtil.isBlank(batchVo.getDescription()) ? StringPool.EMPTY_STR
                : batchVo.getDescription());
        batch.setCreateTime(LocalDateTime.now());

        stockAdjustSheetDetailBatchService.save(batch);
      }
    } else if (Boolean.TRUE.equals(product.getIsSerial())) {
      if (CollectionUtil.isEmpty(productVo.getSerialDetails())) {
        return;
      }
      for (StockAdjustSerialDetailVo serialVo : productVo.getSerialDetails()) {
        StockAdjustSheetDetailSerial serial = new StockAdjustSheetDetailSerial();
        serial.setId(IdUtil.getId());
        serial.setSheetId(sheetId);
        serial.setSheetDetailId(detailId);
        serial.setProductId(product.getId());
        serial.setSerialNumber(serialVo.getSerialNumber());
        serial.setBatchNumber(serialVo.getBatchNumber());
        serial.setDescription(
            StringUtil.isBlank(serialVo.getDescription()) ? StringPool.EMPTY_STR
                : serialVo.getDescription());
        serial.setCreateTime(LocalDateTime.now());

        stockAdjustSheetDetailSerialService.save(serial);
      }
    }
  }
}
