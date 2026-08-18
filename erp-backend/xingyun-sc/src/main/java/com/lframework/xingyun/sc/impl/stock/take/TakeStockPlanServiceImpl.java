package com.lframework.xingyun.sc.impl.stock.take;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageInfo;
import com.lframework.starter.common.constants.StringPool;
import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.common.utils.Assert;
import com.lframework.starter.common.utils.CollectionUtil;
import com.lframework.starter.common.utils.NumberUtil;
import com.lframework.starter.common.utils.ObjectUtil;
import com.lframework.starter.common.utils.StringUtil;
import com.lframework.starter.web.core.impl.BaseMpServiceImpl;
import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.utils.ApplicationUtil;
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
import com.lframework.xingyun.basedata.vo.product.info.QueryProductVo;
import com.lframework.xingyun.core.annotations.OpLog;
import com.lframework.xingyun.core.components.qrtz.QrtzJob;
import com.lframework.xingyun.core.dto.stock.ProductStockChangeDto;
import com.lframework.xingyun.sc.events.stock.take.DeleteTakeStockPlanEvent;
import com.lframework.xingyun.core.queue.MqStringPool;
import com.lframework.xingyun.core.service.GenerateCodeService;
import com.lframework.xingyun.core.utils.OpLogUtil;
import com.lframework.xingyun.sc.components.code.GenerateCodeTypePool;
import com.lframework.xingyun.sc.dto.stock.take.plan.QueryTakeStockPlanProductDto;
import com.lframework.xingyun.sc.dto.stock.take.plan.TakeStockPlanFullDto;
import com.lframework.xingyun.sc.entity.ProductStock;
import com.lframework.xingyun.sc.entity.ProductStockBatch;
import com.lframework.xingyun.sc.entity.ProductStockSerial;
import com.lframework.xingyun.sc.entity.TakeStockConfig;
import com.lframework.xingyun.sc.entity.TakeStockPlan;
import com.lframework.xingyun.sc.entity.TakeStockPlanDetail;
import com.lframework.xingyun.sc.entity.TakeStockSheet;
import com.lframework.xingyun.sc.entity.TakeStockSheetDetailBatch;
import com.lframework.xingyun.sc.entity.TakeStockSheetDetailSerial;
import com.lframework.xingyun.sc.enums.ProductStockBizType;
import com.lframework.xingyun.sc.enums.ScOpLogType;
import com.lframework.xingyun.sc.enums.TakeStockPlanStatus;
import com.lframework.xingyun.sc.enums.TakeStockPlanType;
import com.lframework.xingyun.sc.enums.TakeStockSheetStatus;
import com.lframework.xingyun.sc.mappers.TakeStockPlanDetailMapper;
import com.lframework.xingyun.sc.mappers.TakeStockPlanMapper;
import com.lframework.xingyun.sc.service.stock.ProductStockBatchService;
import com.lframework.xingyun.sc.service.stock.ProductStockSerialService;
import com.lframework.xingyun.sc.service.stock.ProductStockService;
import com.lframework.xingyun.sc.service.stock.take.TakeStockConfigService;
import com.lframework.xingyun.sc.service.stock.take.TakeStockPlanDetailService;
import com.lframework.xingyun.sc.service.stock.take.TakeStockPlanService;
import com.lframework.xingyun.sc.service.stock.take.TakeStockSheetDetailBatchService;
import com.lframework.xingyun.sc.service.stock.take.TakeStockSheetDetailSerialService;
import com.lframework.xingyun.sc.service.stock.take.TakeStockSheetService;
import com.lframework.xingyun.sc.vo.stock.AddProductStockBatchVo;
import com.lframework.xingyun.sc.vo.stock.AddProductStockVo;
import com.lframework.xingyun.sc.vo.stock.SubProductStockBatchVo;
import com.lframework.xingyun.sc.vo.stock.SubProductStockVo;
import com.lframework.xingyun.sc.vo.stock.take.plan.CancelTakeStockPlanVo;
import com.lframework.xingyun.sc.vo.stock.take.plan.CreateTakeStockPlanVo;
import com.lframework.xingyun.sc.vo.stock.take.plan.HandleTakeStockPlanVo;
import com.lframework.xingyun.sc.vo.stock.take.plan.HandleTakeStockPlanVo.ProductVo;
import com.lframework.xingyun.sc.vo.stock.take.plan.QueryTakeStockPlanVo;
import com.lframework.xingyun.sc.vo.stock.take.plan.TakeStockPlanSelectorVo;
import com.lframework.xingyun.sc.vo.stock.take.plan.UpdateTakeStockPlanVo;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class TakeStockPlanServiceImpl extends BaseMpServiceImpl<TakeStockPlanMapper, TakeStockPlan>
    implements TakeStockPlanService {

  @Autowired
  private TakeStockPlanDetailService takeStockPlanDetailService;

  @Autowired
  private GenerateCodeService generateCodeService;

  @Autowired
  private ProductService productService;

  @Autowired
  private ProductStockService productStockService;

  @Autowired
  private ProductStockBatchService productStockBatchService;

  @Autowired
  private ProductStockSerialService productStockSerialService;

  @Autowired
  private TakeStockSheetDetailBatchService takeStockSheetDetailBatchService;

  @Autowired
  private TakeStockSheetDetailSerialService takeStockSheetDetailSerialService;

  @Autowired
  private TakeStockConfigService takeStockConfigService;

  @Autowired
  private TakeStockSheetService takeStockSheetService;

  @Autowired
  private ProductPurchaseService productPurchaseService;

  @Autowired
  private StoreCenterService storeCenterService;


  @Override
  public PageResult<TakeStockPlan> query(Integer pageIndex, Integer pageSize,
      QueryTakeStockPlanVo vo) {

    Assert.greaterThanZero(pageIndex);
    Assert.greaterThanZero(pageSize);

    PageHelperUtil.startPage(pageIndex, pageSize);
    List<TakeStockPlan> datas = this.query(vo);

    return PageResultUtil.convert(new PageInfo<>(datas));
  }

  @Override
  public List<TakeStockPlan> query(QueryTakeStockPlanVo vo) {

    return getBaseMapper().query(vo);
  }

  @Override
  public PageResult<TakeStockPlan> selector(Integer pageIndex, Integer pageSize,
      TakeStockPlanSelectorVo vo) {

    Assert.greaterThanZero(pageIndex);
    Assert.greaterThanZero(pageSize);

    PageHelperUtil.startPage(pageIndex, pageSize);
    List<TakeStockPlan> datas = getBaseMapper().selector(vo);

    return PageResultUtil.convert(new PageInfo<>(datas));
  }

  @Override
  public TakeStockPlanFullDto getDetail(String id) {

    return getBaseMapper().getDetail(id);
  }

  @Override
  public TakeStockPlan getByIdForUpdate(String id) {

    return getBaseMapper().selectByIdForUpdate(id);
  }

  @OpLog(type = ScOpLogType.TAKE_STOCK, name = "新增盘点任务，ID：{}", params = {"#id"})
  @Transactional(rollbackFor = Exception.class)
  @Override
  public String create(CreateTakeStockPlanVo vo) {

    if (storeCenterService.findById(vo.getScId()) == null) {
      throw new DefaultClientException("仓库不存在！");
    }

    TakeStockPlan data = new TakeStockPlan();
    data.setId(IdUtil.getId());
    data.setCode(generateCodeService.generate(GenerateCodeTypePool.TAKE_STOCK_PLAN));
    data.setScId(vo.getScId());
    data.setTakeType(EnumUtil.getByCode(TakeStockPlanType.class, vo.getTakeType()));
    if (data.getTakeType() == TakeStockPlanType.CATEGORY
        || data.getTakeType() == TakeStockPlanType.BRAND) {
      data.setBizId(StringUtil.join(",", vo.getBizIds()));
    }

    data.setTakeStatus(TakeStockPlanStatus.CREATED);
    data.setDescription(
        StringUtil.isBlank(vo.getDescription()) ? StringPool.EMPTY_STR : vo.getDescription());

    getBaseMapper().insert(data);

    List<Product> products = null;
    if (data.getTakeType() != TakeStockPlanType.SIMPLE) {
      // 单品盘点不生成明细
      if (data.getTakeType() == TakeStockPlanType.ALL) {
        // 全场盘点
        // 将所有商品添加明细
        QueryProductVo queryProductVo = new QueryProductVo();
        queryProductVo.setAvailable(Boolean.TRUE);
        queryProductVo.setProductType(ProductType.NORMAL.getCode());
        Integer count = productService.queryCount(queryProductVo);
        if (count > 2000) {
          throw new DefaultClientException(
              TakeStockPlanType.ALL.getDesc() + "最多支持2000个商品，当前系统内已经超过2000个商品，无法进行"
                  + TakeStockPlanType.ALL.getDesc());
        }
        products = productService.query(queryProductVo);
      } else if (data.getTakeType() == TakeStockPlanType.CATEGORY) {
        // 分类盘点
        products = productService.getByCategoryIds(vo.getBizIds(), ProductType.NORMAL.getCode());
      } else if (data.getTakeType() == TakeStockPlanType.BRAND) {
        // 品牌盘点
        products = productService.getByBrandIds(vo.getBizIds(), ProductType.NORMAL.getCode());
      }
    }

    if (data.getTakeType() != TakeStockPlanType.SIMPLE && CollectionUtil.isEmpty(products)) {
      throw new DefaultClientException("没有查询到商品信息，无法生成盘点任务！");
    }

    if (products != null) {
      List<String> productIds = products.stream().map(Product::getId)
          .collect(Collectors.toList());
      List<ProductStock> productStocks = productStockService.getByProductIdsAndScId(productIds,
          vo.getScId(), ProductType.NORMAL.getCode());
      int orderNo = 1;
      for (Product product : products) {
        ProductStock productStock = productStocks.stream()
            .filter(t -> t.getProductId().equals(product.getId()))
            .findFirst().orElse(null);

        TakeStockPlanDetail detail = new TakeStockPlanDetail();
        detail.setId(IdUtil.getId());
        detail.setPlanId(data.getId());
        detail.setProductId(product.getId());

        detail.setStockNum(productStock == null ? 0 : productStock.getStockNum());
        detail.setTotalOutNum(0);
        detail.setTotalInNum(0);
        detail.setOrderNo(orderNo++);

        takeStockPlanDetailService.save(detail);
      }
    }

    OpLogUtil.setVariable("id", data.getId());
    OpLogUtil.setExtra(vo);

    return data.getId();
  }

  @OpLog(type = ScOpLogType.TAKE_STOCK, name = "修改盘点任务，ID：{}", params = {"#id"})
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void update(UpdateTakeStockPlanVo vo) {

    TakeStockPlan data = getBaseMapper().selectByIdForUpdate(vo.getId());
    if (ObjectUtil.isNull(data)) {
      throw new DefaultClientException("盘点任务不存在！");
    }
    if (data.getTakeStatus() != TakeStockPlanStatus.CREATED) {
      throw new DefaultClientException("盘点任务状态已改变，不允许修改！");
    }
    if (!Objects.equals(data.getScId(), vo.getScId())) {
      throw new DefaultClientException("盘点任务创建后不允许修改仓库！");
    }

    LambdaUpdateWrapper<TakeStockPlan> updateWrapper = Wrappers.lambdaUpdate(TakeStockPlan.class)
        .set(TakeStockPlan::getDescription, vo.getDescription())
        .eq(TakeStockPlan::getId, vo.getId())
        .eq(TakeStockPlan::getTakeStatus, TakeStockPlanStatus.CREATED)
        .eq(TakeStockPlan::getScId, data.getScId());

    if (getBaseMapper().update(updateWrapper) != 1) {
      throw new DefaultClientException("盘点任务信息已过期，请刷新重试！");
    }

    OpLogUtil.setVariable("id", data.getId());
    OpLogUtil.setExtra(vo);
  }

  @Override
  public List<QueryTakeStockPlanProductDto> getProducts(String planId) {

    return getBaseMapper().getProducts(planId);
  }

  @OpLog(type = ScOpLogType.TAKE_STOCK, name = "差异生成，盘点任务ID：{}", params = {"#id"})
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void createDiff(String id) {

    TakeStockPlan data = getBaseMapper().selectByIdForUpdate(id);
    if (data == null) {
      throw new DefaultClientException("盘点任务不存在！");
    }

    // 判断是否还有没审核通过的盘点单
    if (takeStockSheetService.hasUnApprove(data.getId())) {
      throw new DefaultClientException("盘点任务存在未审核的库存盘点单，请优先处理库存盘点单！");
    }

    LambdaUpdateWrapper<TakeStockPlan> updateWrapper = Wrappers.lambdaUpdate(TakeStockPlan.class)
        .set(TakeStockPlan::getTakeStatus, TakeStockPlanStatus.DIFF_CREATED)
        .eq(TakeStockPlan::getId, data.getId())
        .eq(TakeStockPlan::getTakeStatus, TakeStockPlanStatus.CREATED);
    if (getBaseMapper().update(updateWrapper) != 1) {
      throw new DefaultClientException("盘点任务信息已过期，请刷新重试！");
    }

    Wrapper<TakeStockPlanDetail> queryDetailWrapper = Wrappers.lambdaQuery(
            TakeStockPlanDetail.class)
        .eq(TakeStockPlanDetail::getPlanId, data.getId())
        .orderByAsc(TakeStockPlanDetail::getOrderNo);
    List<TakeStockPlanDetail> details = takeStockPlanDetailService.list(queryDetailWrapper);
    if (CollectionUtil.isEmpty(details)) {
      throw new DefaultClientException("盘点任务不存在商品信息，不允许执行差异生成操作！");
    }
    for (TakeStockPlanDetail detail : details) {
      if (detail.getOriTakeNum() != null) {
        continue;
      }
      LambdaUpdateWrapper<TakeStockPlanDetail> updateDetailWrapper = Wrappers.lambdaUpdate(
              TakeStockPlanDetail.class).set(TakeStockPlanDetail::getOriTakeNum, 0)
          .eq(TakeStockPlanDetail::getId, detail.getId());

      takeStockPlanDetailService.update(updateDetailWrapper);
    }
  }

  @OpLog(type = ScOpLogType.TAKE_STOCK, name = "差异处理，盘点任务ID：{}", params = {"#id"})
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void handleDiff(HandleTakeStockPlanVo vo) {

    TakeStockPlan data = getBaseMapper().selectByIdForUpdate(vo.getId());
    if (data == null) {
      throw new DefaultClientException("盘点任务不存在！");
    }
    if (data.getTakeStatus() != TakeStockPlanStatus.DIFF_CREATED) {
      throw new DefaultClientException("盘点任务尚未生成差异或已经处理！");
    }

    TakeStockConfig config = takeStockConfigService.get();

    Wrapper<TakeStockPlanDetail> queryDetailWrapper = Wrappers.lambdaQuery(
            TakeStockPlanDetail.class)
        .eq(TakeStockPlanDetail::getPlanId, data.getId())
        .orderByAsc(TakeStockPlanDetail::getOrderNo);
    List<TakeStockPlanDetail> details = takeStockPlanDetailService.list(queryDetailWrapper);
    if (CollectionUtil.isEmpty(details)) {
      throw new DefaultClientException("盘点任务不存在商品信息，不允许执行差异处理操作！");
    }

    if (!config.getAllowChangeNum().equals(vo.getAllowChangeNum()) || !config.getAutoChangeStock()
        .equals(vo.getAutoChangeStock())) {
      throw new DefaultClientException("系统参数发生改变，请刷新页面后重试！");
    }

    Map<String, ProductVo> submittedProducts = new HashMap<>();
    for (ProductVo productVo : vo.getProducts()) {
      if (submittedProducts.putIfAbsent(productVo.getProductId(), productVo) != null) {
        throw new DefaultClientException("盘点商品[" + productVo.getProductId() + "]重复提交！");
      }
    }
    if (submittedProducts.size() != details.size()) {
      throw new DefaultClientException("盘点商品信息与任务明细不一致，请刷新后重试！");
    }

    // 加载已审核盘点单的批次/序列号明细（差异处理按追溯明细执行）
    Wrapper<TakeStockSheet> sheetQueryWrapper = Wrappers.lambdaQuery(TakeStockSheet.class)
        .eq(TakeStockSheet::getPlanId, data.getId())
        .eq(TakeStockSheet::getStatus, TakeStockSheetStatus.APPROVE_PASS);
    List<TakeStockSheet> approvedSheets = takeStockSheetService.list(sheetQueryWrapper);
    List<String> approvedSheetIds = approvedSheets.stream().map(TakeStockSheet::getId)
        .collect(Collectors.toList());

    Map<String, List<TakeStockSheetDetailBatch>> batchDetailMap = new HashMap<>();
    Map<String, List<TakeStockSheetDetailSerial>> serialDetailMap = new HashMap<>();
    if (!CollectionUtil.isEmpty(approvedSheetIds)) {
      List<TakeStockSheetDetailBatch> batchRows = takeStockSheetDetailBatchService.list(
          Wrappers.lambdaQuery(TakeStockSheetDetailBatch.class)
              .in(TakeStockSheetDetailBatch::getSheetId, approvedSheetIds)
              .orderByAsc(TakeStockSheetDetailBatch::getCreateTime));
      for (TakeStockSheetDetailBatch row : batchRows) {
        batchDetailMap.computeIfAbsent(row.getProductId(), k -> new ArrayList<>()).add(row);
      }
      List<TakeStockSheetDetailSerial> serialRows = takeStockSheetDetailSerialService.list(
          Wrappers.lambdaQuery(TakeStockSheetDetailSerial.class)
              .in(TakeStockSheetDetailSerial::getSheetId, approvedSheetIds)
              .orderByAsc(TakeStockSheetDetailSerial::getCreateTime));
      for (TakeStockSheetDetailSerial row : serialRows) {
        serialDetailMap.computeIfAbsent(row.getProductId(), k -> new ArrayList<>()).add(row);
      }
    }

    for (TakeStockPlanDetail detail : details) {
      ProductVo productVo = submittedProducts.get(detail.getProductId());
      if (productVo == null) {
        throw new DefaultClientException("盘点商品信息与任务明细不一致，请刷新后重试！");
      }

      Product product = productService.findById(detail.getProductId());
      if (product == null) {
        throw new DefaultClientException("盘点商品不存在！");
      }

      if (Boolean.TRUE.equals(product.getIsBatch())) {
        List<TakeStockSheetDetailBatch> batchRows = batchDetailMap.get(detail.getProductId());
        if (CollectionUtil.isEmpty(batchRows)) {
          throw new DefaultClientException(
              "航材（" + product.getCode() + "）" + product.getName()
                  + "启用了批次管理，当前盘点单缺少批次差异明细，不允许自动调整库存！");
        }
        int batchTakeSum = batchRows.stream()
            .mapToInt(TakeStockSheetDetailBatch::getTakeNum).sum();
        if (config.getAllowChangeNum() && productVo.getTakeNum() != null
            && productVo.getTakeNum() != batchTakeSum) {
          throw new DefaultClientException(
              "航材（" + product.getCode() + "）" + product.getName()
                  + "盘点数量与批次实盘数量合计不一致，请刷新后重试！");
        }
        detail.setTakeNum(batchTakeSum);
      } else if (Boolean.TRUE.equals(product.getIsSerial())) {
        List<TakeStockSheetDetailSerial> serialRows = serialDetailMap.get(detail.getProductId());
        if (CollectionUtil.isEmpty(serialRows)) {
          throw new DefaultClientException(
              "航材（" + product.getCode() + "）" + product.getName()
                  + "启用了序列号管理，当前盘点单缺少序列号差异明细，不允许自动调整库存！");
        }
        int presentCount = (int) serialRows.stream()
            .filter(s -> s.getTakeStatus() != null && s.getTakeStatus() == 1).count();
        if (config.getAllowChangeNum() && productVo.getTakeNum() != null
            && productVo.getTakeNum() != presentCount) {
          throw new DefaultClientException(
              "航材（" + product.getCode() + "）" + product.getName()
                  + "盘点数量与实盘在库序列号数量不一致，请刷新后重试！");
        }
        detail.setTakeNum(presentCount);
      } else if (config.getAllowChangeNum()) {
        // 如果允许修改盘点数量
        if (productVo.getTakeNum() == null) {
          throw new DefaultClientException("盘点数量不能为空！");
        }
        detail.setTakeNum(productVo.getTakeNum());
      } else {
        // 如果允许自动调整，那么盘点数量=盘点单的盘点数量 - 进项数量 + 出项数量，否则就等于盘点单的盘点数量
        detail.setTakeNum(config.getAutoChangeStock() ?
            detail.getOriTakeNum() - detail.getTotalInNum() + detail.getTotalOutNum() :
            detail.getOriTakeNum());
      }
      if (detail.getTakeNum() == null || detail.getTakeNum() < 0) {
        throw new DefaultClientException("盘点数量不能小于0！");
      }
      detail.setDescription(
          StringUtil.isBlank(productVo.getDescription()) ? StringPool.EMPTY_STR
              : productVo.getDescription());
    }

    LambdaUpdateWrapper<TakeStockPlan> updateWrapper = Wrappers.lambdaUpdate(TakeStockPlan.class)
        .set(TakeStockPlan::getDescription,
            StringUtil.isBlank(vo.getDescription()) ? StringPool.EMPTY_STR : vo.getDescription())
        .set(TakeStockPlan::getTakeStatus, TakeStockPlanStatus.FINISHED)
        .eq(TakeStockPlan::getId, data.getId())
        .eq(TakeStockPlan::getTakeStatus, TakeStockPlanStatus.DIFF_CREATED);
    if (getBaseMapper().update(updateWrapper) != 1) {
      throw new DefaultClientException("盘点任务信息已过期，请刷新重试！");
    }

    for (TakeStockPlanDetail detail : details) {
      LambdaUpdateWrapper<TakeStockPlanDetail> updateDetailWrapper = Wrappers.lambdaUpdate(
              TakeStockPlanDetail.class).set(TakeStockPlanDetail::getTakeNum, detail.getTakeNum())
          .set(TakeStockPlanDetail::getDescription, detail.getDescription())
          .eq(TakeStockPlanDetail::getId, detail.getId());
      takeStockPlanDetailService.update(updateDetailWrapper);
    }

    // 进行出入库操作
    for (TakeStockPlanDetail detail : details) {
      Product product = productService.findById(detail.getProductId());
      if (Boolean.TRUE.equals(product.getIsBatch())) {
        applyBatchStockChange(data, detail, product,
            batchDetailMap.get(detail.getProductId()));
      } else if (Boolean.TRUE.equals(product.getIsSerial())) {
        applySerialStockChange(data, detail, product,
            serialDetailMap.get(detail.getProductId()));
      } else if (!NumberUtil.equal(detail.getStockNum(), detail.getTakeNum())) {
        applyProductTotalChange(data, detail, product,
            detail.getTakeNum() - detail.getStockNum());
      }
    }

    OpLogUtil.setVariable("id", vo.getId());
    OpLogUtil.setExtra(vo);
  }

  /**
   * 普通航材总库存调整（盘盈入库/盘亏出库）。
   *
   * @param diff 净差异，正数为盘盈、负数为盘亏
   */
  private void applyProductTotalChange(TakeStockPlan data, TakeStockPlanDetail detail,
      Product product, int diff) {

    if (diff == 0) {
      return;
    }
    if (diff > 0) {
      ProductPurchase purchase = productPurchaseService.getById(product.getId());
      if (purchase == null || purchase.getPrice() == null) {
        throw new DefaultClientException(
            "航材（" + product.getCode() + "）" + product.getName() + "没有采购价格，无法执行盘盈入库！");
      }
      // 如果库存数量小于盘点数量，则报溢
      AddProductStockVo addProductStockVo = new AddProductStockVo();
      addProductStockVo.setProductId(detail.getProductId());
      addProductStockVo.setScId(data.getScId());
      addProductStockVo.setStockNum(diff);
      addProductStockVo.setDefaultTaxPrice(purchase.getPrice());
      addProductStockVo.setBizId(data.getId());
      addProductStockVo.setBizDetailId(detail.getId());
      addProductStockVo.setBizCode(data.getCode());
      addProductStockVo.setBizType(ProductStockBizType.TAKE_STOCK_IN.getCode());

      productStockService.addStock(addProductStockVo);
    } else {
      // 如果库存数量大于盘点数量，则报损
      SubProductStockVo subProductStockVo = new SubProductStockVo();
      subProductStockVo.setProductId(detail.getProductId());
      subProductStockVo.setScId(data.getScId());
      subProductStockVo.setStockNum(-diff);
      subProductStockVo.setBizId(data.getId());
      subProductStockVo.setBizDetailId(detail.getId());
      subProductStockVo.setBizCode(data.getCode());
      subProductStockVo.setBizType(ProductStockBizType.TAKE_STOCK_OUT.getCode());

      productStockService.subStock(subProductStockVo);
    }
  }

  /**
   * 批次管理航材差异处理：按批次明细逐批次盘盈/盘亏，总库存按净差异统一调整，
   * 保证批次数量合计与总库存账一致。
   */
  private void applyBatchStockChange(TakeStockPlan data, TakeStockPlanDetail detail,
      Product product, List<TakeStockSheetDetailBatch> batchRows) {

    int totalDiff = 0;
    for (TakeStockSheetDetailBatch batchRow : batchRows) {
      ProductStockBatch stockBatch = productStockBatchService.getOne(
          Wrappers.lambdaQuery(ProductStockBatch.class)
              .eq(ProductStockBatch::getScId, data.getScId())
              .eq(ProductStockBatch::getProductId, detail.getProductId())
              .eq(ProductStockBatch::getBatchNumber, batchRow.getBatchNumber()));
      int liveQty = stockBatch == null ? 0 : stockBatch.getQuantity();
      int diff = batchRow.getTakeNum() - liveQty;
      totalDiff += diff;
      if (diff == 0) {
        continue;
      }
      if (diff > 0) {
        String batchId;
        if (stockBatch == null) {
          ProductStockBatch newBatch = new ProductStockBatch();
          newBatch.setId(IdUtil.getId());
          newBatch.setScId(data.getScId());
          newBatch.setProductId(detail.getProductId());
          newBatch.setQuantity(0);
          newBatch.setBatchNumber(batchRow.getBatchNumber());
          newBatch.setCreateTime(LocalDateTime.now());
          productStockBatchService.save(newBatch);
          batchId = newBatch.getId();
        } else {
          batchId = stockBatch.getId();
        }
        AddProductStockBatchVo addBatchVo = new AddProductStockBatchVo();
        addBatchVo.setProductId(detail.getProductId());
        addBatchVo.setScId(data.getScId());
        addBatchVo.setStockBatchId(batchId);
        addBatchVo.setStockNum(diff);
        addBatchVo.setBizId(data.getId());
        addBatchVo.setBizDetailId(detail.getId());
        addBatchVo.setBizCode(data.getCode());
        addBatchVo.setBizType(ProductStockBizType.TAKE_STOCK_IN.getCode());

        productStockService.addStockBatch(addBatchVo);
      } else {
        SubProductStockBatchVo subBatchVo = new SubProductStockBatchVo();
        subBatchVo.setProductId(detail.getProductId());
        subBatchVo.setScId(data.getScId());
        subBatchVo.setStockBatchId(stockBatch.getId());
        subBatchVo.setStockNum(-diff);
        subBatchVo.setBizId(data.getId());
        subBatchVo.setBizDetailId(detail.getId());
        subBatchVo.setBizCode(data.getCode());
        subBatchVo.setBizType(ProductStockBizType.TAKE_STOCK_OUT.getCode());

        productStockService.subStockBatch(subBatchVo);
      }
    }

    applyProductTotalChange(data, detail, product, totalDiff);
  }

  /**
   * 序列号管理航材差异处理：一条序列号一条明细，逐条判定盘盈/盘亏，
   * 已出库/已锁定等状态序列号也允许盘点；总库存按净差异统一调整。
   */
  private void applySerialStockChange(TakeStockPlan data, TakeStockPlanDetail detail,
      Product product, List<TakeStockSheetDetailSerial> serialRows) {

    int totalDiff = 0;
    for (TakeStockSheetDetailSerial serialRow : serialRows) {
      ProductStockSerial stockSerial = productStockSerialService.getOne(
          Wrappers.lambdaQuery(ProductStockSerial.class)
              .eq(ProductStockSerial::getSerialNumber, serialRow.getSerialNumber()));
      boolean present = serialRow.getTakeStatus() != null && serialRow.getTakeStatus() == 1;
      if (stockSerial == null) {
        if (!present) {
          throw new DefaultClientException(
              "航材（" + product.getCode() + "）" + product.getName()
                  + "序列号[" + serialRow.getSerialNumber() + "]系统不存在但实盘缺失，数据矛盾，无法处理差异！");
        }
        if (StringUtil.isBlank(serialRow.getBatchNumber())) {
          throw new DefaultClientException(
              "航材（" + product.getCode() + "）" + product.getName()
                  + "盘盈序列号[" + serialRow.getSerialNumber() + "]必须指定所属批次号！");
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
          newBatch.setCreateTime(LocalDateTime.now());
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
        newSerial.setCreateTime(LocalDateTime.now());
        productStockSerialService.save(newSerial);
        if (productStockBatchService.addStock(batchId, detail.getProductId(), data.getScId(),
            1) != 1) {
          throw new DefaultClientException("批次库存已变化，盘盈序列号处理失败！");
        }
        totalDiff++;
        continue;
      }
      if (!detail.getProductId().equals(stockSerial.getProductId())) {
        throw new DefaultClientException(
            "序列号[" + serialRow.getSerialNumber() + "]不属于当前盘点航材！");
      }
      if (present) {
        if (stockSerial.getStockStatus() == 1) {
          continue; // 系统在库且实盘在库，一致无变化
        }
        // 盘盈：系统已出库但实盘在库，恢复为在库
        ProductStockBatch stockBatch = productStockBatchService.getById(stockSerial.getBatchId());
        if (stockBatch == null || !data.getScId().equals(stockBatch.getScId())) {
          throw new DefaultClientException(
              "序列号[" + serialRow.getSerialNumber() + "]所属批次不在盘点仓库，无法盘盈！");
        }
        if (productStockSerialService.updateStatus(stockSerial.getId(), 0, 1) != 1) {
          throw new DefaultClientException("序列号状态已变化，盘盈处理失败！");
        }
        if (productStockBatchService.addStock(stockSerial.getBatchId(), detail.getProductId(),
            data.getScId(), 1) != 1) {
          throw new DefaultClientException("批次库存已变化，盘盈序列号处理失败！");
        }
        totalDiff++;
      } else {
        if (stockSerial.getStockStatus() == 0) {
          continue; // 系统已出库且实盘缺失，一致无变化
        }
        // 盘亏：系统在库但实盘缺失，置为出库
        ProductStockBatch stockBatch = productStockBatchService.getById(stockSerial.getBatchId());
        if (stockBatch == null || !data.getScId().equals(stockBatch.getScId())) {
          throw new DefaultClientException(
              "序列号[" + serialRow.getSerialNumber() + "]所属批次不在盘点仓库，无法盘亏！");
        }
        if (productStockSerialService.updateStatus(stockSerial.getId(), 1, 0) != 1) {
          throw new DefaultClientException("序列号状态已变化，盘亏处理失败！");
        }
        if (productStockBatchService.subStock(stockSerial.getBatchId(), detail.getProductId(),
            data.getScId(), 1) != 1) {
          throw new DefaultClientException("批次库存不足或已变化，盘亏处理失败！");
        }
        totalDiff--;
      }
    }

    applyProductTotalChange(data, detail, product, totalDiff);
  }

  @OpLog(type = ScOpLogType.TAKE_STOCK, name = "作废盘点任务，ID：{}", params = {"#id"})
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void cancel(CancelTakeStockPlanVo vo) {

    TakeStockPlan data = getBaseMapper().selectByIdForUpdate(vo.getId());
    if (data == null) {
      throw new DefaultClientException("盘点任务不存在！");
    }

    LambdaUpdateWrapper<TakeStockPlan> updateWrapper = Wrappers.lambdaUpdate(TakeStockPlan.class)
        .set(TakeStockPlan::getTakeStatus, TakeStockPlanStatus.CANCELED)
        .eq(TakeStockPlan::getId, data.getId())
        .in(TakeStockPlan::getTakeStatus, TakeStockPlanStatus.CREATED,
            TakeStockPlanStatus.DIFF_CREATED);
    if (getBaseMapper().update(updateWrapper) != 1) {
      throw new DefaultClientException("盘点任务信息已过期，请刷新重试！");
    }

    OpLogUtil.setVariable("id", vo.getId());
    OpLogUtil.setExtra(vo);
  }

  @OpLog(type = ScOpLogType.TAKE_STOCK, name = "删除盘点任务，ID：{}", params = {"#id"})
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void deleteById(String id) {

    TakeStockPlan data = getBaseMapper().selectByIdForUpdate(id);
    if (data == null) {
      throw new DefaultClientException("盘点任务不存在！");
    }

    Wrapper<TakeStockPlan> deleteWrapper = Wrappers.lambdaQuery(TakeStockPlan.class)
        .eq(TakeStockPlan::getId, data.getId())
        .eq(TakeStockPlan::getTakeStatus, TakeStockPlanStatus.CANCELED);
    if (getBaseMapper().delete(deleteWrapper) != 1) {
      throw new DefaultClientException("盘点任务信息已过期，请刷新重试！");
    }

    Wrapper<TakeStockPlanDetail> deleteDetailWrapper = Wrappers.lambdaQuery(
            TakeStockPlanDetail.class)
        .eq(TakeStockPlanDetail::getPlanId, data.getId());
    takeStockPlanDetailService.remove(deleteDetailWrapper);

    DeleteTakeStockPlanEvent deleteEvent = new DeleteTakeStockPlanEvent(this, data.getId());
    ApplicationUtil.publishEvent(deleteEvent);
  }

  @Override
  public void cleanCacheByKey(Serializable key) {

  }

  /**
   * 自动作废任务
   */
  @Slf4j
  public static class AutoCancelJob extends QrtzJob {

    @Autowired
    private TakeStockPlanService takeStockPlanService;

    @Override
    public void onExecute(JobExecutionContext context) {

      String id = (String) context.getMergedJobDataMap().get("id");

      CancelTakeStockPlanVo cancelVo = new CancelTakeStockPlanVo();
      cancelVo.setId(id);
      takeStockPlanService.cancel(cancelVo);
    }
  }
}
