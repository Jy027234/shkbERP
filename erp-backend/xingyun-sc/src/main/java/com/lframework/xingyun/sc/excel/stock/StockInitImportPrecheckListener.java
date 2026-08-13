package com.lframework.xingyun.sc.excel.stock;

import com.alibaba.excel.context.AnalysisContext;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lframework.starter.common.utils.NumberUtil;
import com.lframework.starter.common.utils.StringUtil;
import com.lframework.starter.web.core.components.excel.ExcelImportListener;
import com.lframework.starter.web.core.utils.ApplicationUtil;
import com.lframework.starter.web.core.utils.ExcelImportUtil;
import com.lframework.xingyun.basedata.entity.Product;
import com.lframework.xingyun.basedata.entity.StoreCenter;
import com.lframework.xingyun.basedata.entity.Supplier;
import com.lframework.xingyun.basedata.service.product.ProductService;
import com.lframework.xingyun.basedata.service.storecenter.StoreCenterService;
import com.lframework.xingyun.basedata.service.supplier.SupplierService;
import com.lframework.xingyun.sc.entity.StockInitImportRecord;
import com.lframework.xingyun.sc.entity.StockInitImportSerials;
import com.lframework.xingyun.sc.entity.StockInitImportStaging;
import com.lframework.xingyun.sc.service.StockInitImportRecordService;
import com.lframework.xingyun.sc.service.StockInitImportSerialsService;
import com.lframework.xingyun.sc.service.StockInitImportStagingService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.time.LocalDate;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import com.lframework.xingyun.sc.service.stock.ProductStockBatchService;
import com.lframework.xingyun.sc.service.stock.ProductStockSerialService;
import com.lframework.xingyun.sc.entity.ProductStockBatch;
import com.lframework.xingyun.sc.entity.ProductStockSerial;

/**
 * 库存初始化导入 预检监听器：解析、校验、写入staging/record/serials，不落库存
 */
public class StockInitImportPrecheckListener extends ExcelImportListener<StockInitImportModel> {

  private final String importBatchId;
  private final String taskId;
  private final boolean initOnly;
  private final List<String> successDetails = new ArrayList<>();
  private final List<String> failureDetails = new ArrayList<>();

  private int rowIndex = 0;

  public StockInitImportPrecheckListener(String importBatchId, String taskId, boolean initOnly) {
    this.importBatchId = importBatchId;
    this.setTaskId(taskId);
    this.taskId = taskId;
    this.initOnly = initOnly;
  }

  public List<String> getSuccessDetails() { return successDetails; }
  public List<String> getFailureDetails() { return failureDetails; }

  @Override
  protected void doInvoke(StockInitImportModel data, AnalysisContext context) {
    int i = rowIndex++;

    ProductService productService = ApplicationUtil.getBean(ProductService.class);
    StoreCenterService storeCenterService = ApplicationUtil.getBean(StoreCenterService.class);
    SupplierService supplierService = ApplicationUtil.getBean(SupplierService.class);
    StockInitImportRecordService recordService = ApplicationUtil.getBean(StockInitImportRecordService.class);
    StockInitImportStagingService stagingService = ApplicationUtil.getBean(StockInitImportStagingService.class);
    StockInitImportSerialsService serialsService = ApplicationUtil.getBean(StockInitImportSerialsService.class);

    String importLineId = String.valueOf(i + 1);
    StockInitImportRecord record = new StockInitImportRecord();
    record.setImportBatchId(importBatchId);
    record.setImportLineId(importLineId);
    record.setStatus("PENDING");
    record.setRetries(0);
    record.setCreatedAt(LocalDateTime.now());
    record.setUpdatedAt(LocalDateTime.now());

    try {
      // 基础校验
      if (StringUtil.isBlank(data.getProductCode())) throw new IllegalArgumentException("件号不能为空");
      if (StringUtil.isBlank(data.getScCode())) throw new IllegalArgumentException("仓库编号不能为空");
      if (data.getQty() == null || data.getQty() <= 0) throw new IllegalArgumentException("数量必须大于0");
      if (data.getTaxPrice() != null) {
        if (NumberUtil.lt(data.getTaxPrice(), BigDecimal.ZERO)) throw new IllegalArgumentException("采购价不能小于0");
        if (!NumberUtil.isNumberPrecision(data.getTaxPrice(), 2)) throw new IllegalArgumentException("采购价最多2位小数");
      }

      Product product = productService.getOne(Wrappers.lambdaQuery(Product.class)
          .eq(Product::getCode, data.getProductCode())
          .eq(Product::getAvailable, true));
      if (product == null) throw new IllegalArgumentException("件号不存在或未启用");

      StoreCenter sc = storeCenterService.getOne(Wrappers.lambdaQuery(StoreCenter.class)
          .eq(StoreCenter::getCode, data.getScCode())
          .eq(StoreCenter::getAvailable, true));
      if (sc == null) throw new IllegalArgumentException("仓库不存在或未启用");

      // 供应商（可选）：如果填写，则根据名称匹配一条启用的供应商
      Supplier supplier = null;
      if (!StringUtil.isBlank(data.getSupplierName())) {
        supplier = supplierService.getOne(Wrappers.lambdaQuery(Supplier.class)
            .eq(Supplier::getName, data.getSupplierName().trim())
            .eq(Supplier::getAvailable, true));
        if (supplier == null) {
          throw new IllegalArgumentException("供应商不存在或未启用");
        }
      }

      // 批次/序列规则
      if (Boolean.TRUE.equals(product.getIsBatch()) && StringUtil.isBlank(data.getBatchNumber())) {
        throw new IllegalArgumentException("启用批次管理时“批次号”必填");
      }
      if (Boolean.TRUE.equals(product.getIsSerial())) {
        if (StringUtil.isBlank(data.getSerialNumberList())) {
          throw new IllegalArgumentException("启用序列管理时“序列号列表”必填");
        }
        String[] serials = data.getSerialNumberList().split(",");
        if (serials.length != data.getQty()) {
          throw new IllegalArgumentException("序列号数量必须与数量一致");
        }
        // 行内序列去重
        Set<String> uniq = new HashSet<>();
        for (String s : serials) {
          String sn = s == null ? null : s.trim();
          if (StringUtil.isBlank(sn)) throw new IllegalArgumentException("序列号不能为空");
          if (!uniq.add(sn)) throw new IllegalArgumentException("序列号重复:" + sn);
        }
      }

      // 计算有效批次号（未启用批次管理时固定为 DEFAULT），并在未启用批次但填写了批次号时记录提示
      boolean tipIgnoreBatch = false;
      boolean tipSkipInit = false;
      String effectiveBatchNumber;
      if (Boolean.TRUE.equals(product.getIsBatch())) {
        effectiveBatchNumber = StringUtil.isBlank(data.getBatchNumber()) ? null : data.getBatchNumber().trim();
      } else {
        if (!StringUtil.isBlank(data.getBatchNumber())) {
          tipIgnoreBatch = true; // 未启用批次但填写了批次号，将忽略
        }
        effectiveBatchNumber = "DEFAULT";
      }

      // 数据库级序列号重复校验（同仓库、同航材、同批次下）
      if (Boolean.TRUE.equals(product.getIsSerial()) && !StringUtil.isBlank(data.getSerialNumberList())) {
        ProductStockBatchService batchService = ApplicationUtil.getBean(ProductStockBatchService.class);
        ProductStockSerialService serialService = ApplicationUtil.getBean(ProductStockSerialService.class);
        ProductStockBatch existBatch = batchService.getOne(Wrappers.lambdaQuery(ProductStockBatch.class)
            .eq(ProductStockBatch::getScId, sc.getId())
            .eq(ProductStockBatch::getProductId, product.getId())
            .eq(ProductStockBatch::getBatchNumber, effectiveBatchNumber));
        if (existBatch != null) {
          String[] serials = data.getSerialNumberList().split(",");
          for (String s : serials) {
            String sn = s == null ? null : s.trim();
            if (StringUtil.isBlank(sn)) continue;
            ProductStockSerial exist = serialService.getOne(Wrappers.lambdaQuery(ProductStockSerial.class)
                .eq(ProductStockSerial::getProductId, product.getId())
                .eq(ProductStockSerial::getBatchId, existBatch.getId())
                .eq(ProductStockSerial::getSerialNumber, sn));
            if (exist != null) {
              throw new IllegalArgumentException("序列号已存在：" + sn);
            }
          }
        }
      }

      // initOnly：仅库存初始化模式下，未启用序列管理时，如果目标批次已存在且库存>0，则提示将跳过
      if (initOnly && !Boolean.TRUE.equals(product.getIsSerial())) {
        ProductStockBatchService batchService = ApplicationUtil.getBean(ProductStockBatchService.class);
        ProductStockBatch existBatch = batchService.getOne(Wrappers.lambdaQuery(ProductStockBatch.class)
            .eq(ProductStockBatch::getScId, sc.getId())
            .eq(ProductStockBatch::getProductId, product.getId())
            .eq(ProductStockBatch::getBatchNumber, effectiveBatchNumber));
        if (existBatch != null && existBatch.getQuantity() != null && existBatch.getQuantity() > 0) {
          tipSkipInit = true;
        }
      }

      // 写 staging
      StockInitImportStaging staging = new StockInitImportStaging();
      staging.setImportBatchId(importBatchId);
      staging.setImportLineId(importLineId);
      staging.setProductId(product.getId());
      staging.setScId(sc.getId());
      staging.setQty(data.getQty());
      staging.setTaxPrice(data.getTaxPrice() == null ? BigDecimal.ZERO : data.getTaxPrice());
      staging.setBatchNumber(StringUtil.isBlank(data.getBatchNumber()) ? null : data.getBatchNumber().trim());
      staging.setShelfLocation(StringUtil.isBlank(data.getShelfLocation()) ? null : data.getShelfLocation().trim());
      if (!StringUtil.isBlank(data.getProductionDate())) {
        try {
          LocalDate ld = parseDateFlexible(data.getProductionDate());
          staging.setProductionDate(Date.valueOf(ld));
        } catch (DateTimeParseException e) {
          throw new IllegalArgumentException("生产日期格式有误，请按yyyy-MM-dd或yyyy/M/d格式填写");
        }
      }
      if (!StringUtil.isBlank(data.getExpiryDate())) {
        try {
          LocalDate ld = parseDateFlexible(data.getExpiryDate());
          staging.setExpiryDate(Date.valueOf(ld));
        } catch (DateTimeParseException e) {
          throw new IllegalArgumentException("失效日期格式有误，请按yyyy-MM-dd或yyyy/M/d格式填写");
        }
      }
      if (supplier != null) {
        staging.setSupplierId(supplier.getId());
      }
      staging.setCreatedAt(LocalDateTime.now());
      stagingService.save(staging);

      if (Boolean.TRUE.equals(product.getIsSerial()) && !StringUtil.isBlank(data.getSerialNumberList())) {
        String[] serials = data.getSerialNumberList().split(",");
        for (String s : serials) {
          String sn = s == null ? null : s.trim();
          StockInitImportSerials row = new StockInitImportSerials();
          row.setImportBatchId(importBatchId);
          row.setImportLineId(importLineId);
          row.setSerialNumber(sn);
          row.setShelfLocation(StringUtil.isBlank(data.getShelfLocation()) ? null : data.getShelfLocation().trim());
          row.setCreatedAt(LocalDateTime.now());
          serialsService.save(row);
        }
      }

      recordService.save(record);
      this.setSuccessProcess(i);
      String okMsg = "第" + (i + 1) + "行导入预检通过";
      if (tipIgnoreBatch) {
        okMsg = okMsg + "（提示：未启用批次，将忽略上传的批次号，使用默认批次）";
      }
      if (tipSkipInit) {
        okMsg = okMsg + "（提示：仅库存初始化模式，目标批次已存在非0库存，执行阶段将跳过）";
      }
      successDetails.add(okMsg);
    } catch (Exception ex) {
      record.setStatus("FAILED");
      record.setErrorMsg(ex.getMessage());
      record.setUpdatedAt(LocalDateTime.now());
      recordService.save(record);
      failureDetails.add("第" + (i + 1) + "行预检失败，原因：" + ex.getMessage());
      // 推送失败原因到任务提示，最多15条由工具限制
      ExcelImportUtil.addTipMsg(taskId, "第" + (i + 1) + "行预检失败，原因：" + ex.getMessage());
    }
  }

  @Override
  protected void afterAllAnalysed(AnalysisContext context) {
    // nothing here
  }

  @Override
  protected void doComplete() {
    // 汇总并设置任务状态，便于前端直接展示结果
    Map<String, Object> data = new HashMap<>();
    int success = successDetails.size();
    int failed = failureDetails.size();
    data.put("importBatchId", importBatchId);
    data.put("total", success + failed);
    data.put("success", success);
    data.put("failed", failed);
    data.put("successDetails", successDetails);
    data.put("failureDetails", failureDetails);

    ExcelImportUtil.setHasError(taskId, failed > 0);
    ExcelImportUtil.setData(taskId, data);
    ExcelImportUtil.finished(taskId);
  }

  private LocalDate parseDateFlexible(String raw) {
    if (raw == null) return null;
    String s = raw.trim();
    // 常见分隔符归一化：将斜杠替换为短横线
    s = s.replace('/', '-');
    // 允许不补零的年月日，例如 2020-8-8
    String[] patterns = new String[] {"yyyy-M-d", "yyyy-MM-dd"};
    for (String p : patterns) {
      try {
        return LocalDate.parse(s, DateTimeFormatter.ofPattern(p));
      } catch (DateTimeParseException ignore) { }
    }
    // 全部解析失败时，统一抛出 DateTimeParseException，由调用方转换为友好提示
    throw new DateTimeParseException("日期格式有误", s, 0);
  }
}
