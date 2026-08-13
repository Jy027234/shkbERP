package com.lframework.xingyun.sc.impl;

import com.lframework.xingyun.sc.service.StockInitImportFlowService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lframework.starter.common.utils.StringUtil;
import com.lframework.starter.web.core.utils.ExcelUtil;
import com.lframework.starter.web.core.utils.ExcelImportUtil;
import com.lframework.starter.web.core.utils.IdUtil;
import com.lframework.xingyun.basedata.entity.Product;
import com.lframework.xingyun.basedata.entity.StoreCenter;
import com.lframework.xingyun.basedata.service.product.ProductService;
import com.lframework.xingyun.basedata.service.storecenter.StoreCenterService;
import com.lframework.xingyun.sc.entity.ProductStockBatch;
import com.lframework.xingyun.sc.entity.ProductStockSerial;
import com.lframework.xingyun.sc.entity.StockInitImportRecord;
import com.lframework.xingyun.sc.entity.StockInitImportSerials;
import com.lframework.xingyun.sc.entity.StockInitImportStaging;
import com.lframework.xingyun.sc.enums.ProductStockBizType;
import com.lframework.xingyun.sc.excel.stock.StockInitImportModel;
import com.lframework.xingyun.sc.excel.stock.StockInitImportPrecheckListener;
import com.lframework.xingyun.sc.service.StockInitImportRecordService;
import com.lframework.xingyun.sc.service.StockInitImportSerialsService;
import com.lframework.xingyun.sc.service.StockInitImportStagingService;
import com.lframework.xingyun.sc.service.stock.ProductStockBatchService;
import com.lframework.xingyun.sc.service.stock.ProductStockSerialService;
import com.lframework.xingyun.sc.service.stock.ProductStockService;
import com.lframework.xingyun.sc.vo.stock.AddProductStockVo;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StockInitImportFlowServiceImpl implements StockInitImportFlowService {

  @Autowired
  private StockInitImportRecordService recordService;
  @Autowired
  private StockInitImportStagingService stagingService;
  @Autowired
  private StockInitImportSerialsService serialsService;
  @Autowired
  private ProductService productService;
  @Autowired
  private StoreCenterService storeCenterService;
  @Autowired
  private ProductStockService productStockService;
  @Autowired
  private ProductStockBatchService productStockBatchService;
  @Autowired
  private ProductStockSerialService productStockSerialService;

  @Override
  public Map<String, Object> precheck(String taskId, MultipartFile file, boolean initOnly) {
    String importBatchId = IdUtil.getId();
    StockInitImportPrecheckListener listener = new StockInitImportPrecheckListener(importBatchId, taskId, initOnly);
    ExcelUtil.read(file, StockInitImportModel.class, listener).sheet().doRead();
    Map<String, Object> data = new HashMap<>();
    data.put("importBatchId", importBatchId);
    int success = listener.getSuccessDetails().size();
    int failed = listener.getFailureDetails().size();
    data.put("total", success + failed);
    data.put("success", success);
    data.put("failed", failed);
    data.put("successDetails", listener.getSuccessDetails());
    data.put("failureDetails", listener.getFailureDetails());
    return data;
  }

  @Override
  public Map<String, Object> execute(String importBatchId, String taskId, boolean initOnly) {
    // 分批处理未成功记录，并通过 ExcelImportUtil 上报进度
    int pageSize = 300;
    int success = 0;
    int failed = 0;
    int processed = 0;
    List<String> successDetails = new ArrayList<>();
    List<String> failureDetails = new ArrayList<>();

    // 统计总数
    int total = Math.toIntExact(recordService.count(
        Wrappers.lambdaQuery(StockInitImportRecord.class)
            .eq(StockInitImportRecord::getImportBatchId, importBatchId)
            .ne(StockInitImportRecord::getStatus, "SUCCESS")));

    // 初始化任务进度
    if (StringUtil.isNotBlank(taskId)) {
      ExcelImportUtil.initUploadTask(taskId);
      ExcelImportUtil.setProcess(taskId, total);
      ExcelImportUtil.setSuccessProcess(taskId, 0);
    }

    while (true) {
      // 每次都从头按行号顺序抓取一批未成功记录，避免在状态不断变化时使用 offset 导致遗漏
      List<StockInitImportRecord> todos = recordService.list(
          Wrappers.lambdaQuery(StockInitImportRecord.class)
              .eq(StockInitImportRecord::getImportBatchId, importBatchId)
              .ne(StockInitImportRecord::getStatus, "SUCCESS")
              .orderByAsc(StockInitImportRecord::getImportLineId)
              .last("limit " + pageSize));
      if (todos == null || todos.isEmpty()) break;

      for (StockInitImportRecord rec : todos) {
        boolean ok = false;
        try {
          processOne(importBatchId, rec, initOnly);
          rec.setStatus("SUCCESS");
          rec.setErrorMsg(null);
          ok = true;
        } catch (Exception ex) {
          rec.setStatus("FAILED");
          rec.setErrorMsg(ex.getMessage());
          if (StringUtil.isNotBlank(taskId)) {
            ExcelImportUtil.addTipMsg(taskId, "行" + rec.getImportLineId() + "执行失败：" + ex.getMessage());
          }
        }
        rec.setRetries(rec.getRetries() == null ? 1 : rec.getRetries() + 1);
        recordService.updateById(rec);
        if (ok) {
          success++;
          successDetails.add("行" + rec.getImportLineId() + "执行成功");
        } else {
          failed++;
          failureDetails.add("行" + rec.getImportLineId() + "执行失败：" + rec.getErrorMsg());
        }

        processed++;
        if (StringUtil.isNotBlank(taskId)) {
          ExcelImportUtil.setSuccessProcess(taskId, processed);
        }
      }
    }

    Map<String, Object> data = new HashMap<>();
    data.put("importBatchId", importBatchId);
    data.put("total", success + failed);
    data.put("success", success);
    data.put("failed", failed);
    data.put("successDetails", successDetails);
    data.put("failureDetails", failureDetails);

    if (StringUtil.isNotBlank(taskId)) {
      ExcelImportUtil.setHasError(taskId, failed > 0);
      ExcelImportUtil.setData(taskId, data);
      ExcelImportUtil.finished(taskId);
    }
    return data;
  }

  @Transactional(rollbackFor = Exception.class)
  protected void processOne(String importBatchId, StockInitImportRecord rec, boolean initOnly) {
    // 读取 staging
    StockInitImportStaging st = stagingService.getOne(
        Wrappers.lambdaQuery(StockInitImportStaging.class)
            .eq(StockInitImportStaging::getImportBatchId, importBatchId)
            .eq(StockInitImportStaging::getImportLineId, rec.getImportLineId()));
    if (st == null) throw new IllegalStateException("未找到staging数据");

    Product product = productService.findById(st.getProductId());
    StoreCenter sc = storeCenterService.findById(st.getScId());
    if (product == null || sc == null) throw new IllegalStateException("商品或仓库不存在");

    // 2) 批次判断（先确定批次与是否需要跳过）
    String batchNumber;
    if (Boolean.TRUE.equals(product.getIsBatch())) {
      // 启用批次：必须提供批次号
      if (StringUtil.isBlank(st.getBatchNumber())) {
        throw new IllegalStateException("启用批次管理时“批次号”必填");
      }
      batchNumber = st.getBatchNumber();
    } else {
      // 未启用批次：一律使用默认批次，忽略上传中的批次号
      batchNumber = "DEFAULT";
    }
    ProductStockBatch batch = productStockBatchService.getOne(Wrappers.lambdaQuery(ProductStockBatch.class)
        .eq(ProductStockBatch::getScId, st.getScId())
        .eq(ProductStockBatch::getProductId, st.getProductId())
        .eq(ProductStockBatch::getBatchNumber, batchNumber));

    // initOnly：仅库存初始化模式下，未启用序列管理时，如果目标批次已存在且库存>0，则跳过（不做任何数量写入）
    if (initOnly && !Boolean.TRUE.equals(product.getIsSerial())) {
      if (batch != null && batch.getQuantity() != null && batch.getQuantity() > 0) {
        return; // 视为成功跳过，不抛错
      }
    }

    // 1) 入总库存（放在跳过判定之后，避免误加库存）
    AddProductStockVo add = new AddProductStockVo();
    add.setProductId(st.getProductId());
    add.setScId(st.getScId());
    add.setStockNum(st.getQty());
    add.setTaxPrice(st.getTaxPrice());
    add.setDefaultTaxPrice(st.getTaxPrice());
    add.setBizId(importBatchId);
    add.setBizDetailId(rec.getImportLineId());
    add.setBizType(ProductStockBizType.STOCK_ADJUST.getCode());
    productStockService.addStock(add);

    if (batch == null) {
      batch = new ProductStockBatch();
      batch.setId(IdUtil.getId());
      batch.setScId(st.getScId());
      batch.setProductId(st.getProductId());
      batch.setBatchNumber(batchNumber);
      batch.setQuantity(st.getQty());
      // 新建批次，直接写入staging的架位及生产/失效
      batch.setShelfLocation(st.getShelfLocation());
      batch.setProductionDate(st.getProductionDate() == null ? null : st.getProductionDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
      batch.setExpiryDate(st.getExpiryDate() == null ? null : st.getExpiryDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
      // 仅当导入行显式提供供应商时才写入 supplierId，避免清空已有数据
      if (st.getSupplierId() != null) {
        batch.setSupplierId(st.getSupplierId());
      }
      productStockBatchService.save(batch);
    } else {
      // 数量累加
      batch.setQuantity((batch.getQuantity() == null ? 0 : batch.getQuantity()) + st.getQty());
      // 架位规则：双方非空且不同，则不更新；原空且新有值则补齐
      if (StringUtil.isBlank(batch.getShelfLocation())) {
        if (!StringUtil.isBlank(st.getShelfLocation())) {
          batch.setShelfLocation(st.getShelfLocation());
        }
      }
      // 生产/失效：原空时补齐
      if (batch.getProductionDate() == null && st.getProductionDate() != null) {
        LocalDate ld = st.getProductionDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        batch.setProductionDate(ld);
      }
      if (batch.getExpiryDate() == null && st.getExpiryDate() != null) {
        LocalDate ld = st.getExpiryDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        batch.setExpiryDate(ld);
      }
      // 仅当导入行显式提供供应商时才更新 supplierId，未填写时保持原值不变
      if (st.getSupplierId() != null) {
        batch.setSupplierId(st.getSupplierId());
      }
      productStockBatchService.updateById(batch);
    }

    // 3) 序列号
    if (Boolean.TRUE.equals(product.getIsSerial())) {
      List<StockInitImportSerials> serialRows = serialsService.list(Wrappers.lambdaQuery(StockInitImportSerials.class)
          .eq(StockInitImportSerials::getImportBatchId, importBatchId)
          .eq(StockInitImportSerials::getImportLineId, rec.getImportLineId()));
      if (serialRows != null && !serialRows.isEmpty()) {
        for (StockInitImportSerials row : serialRows) {
          // 校验：同商品、同批次下序列号不得重复
          ProductStockSerial exist = productStockSerialService.getOne(
              Wrappers.lambdaQuery(ProductStockSerial.class)
                  .eq(ProductStockSerial::getProductId, st.getProductId())
                  .eq(ProductStockSerial::getBatchId, batch.getId())
                  .eq(ProductStockSerial::getSerialNumber, row.getSerialNumber())
          );
          if (exist != null) {
            throw new IllegalStateException("序列号已存在：" + row.getSerialNumber());
          }
          ProductStockSerial serial = new ProductStockSerial();
          serial.setId(IdUtil.getId());
          serial.setProductId(st.getProductId());
          serial.setSerialNumber(row.getSerialNumber());
          serial.setStockStatus(1); // 在库
          serial.setBatchId(batch.getId());
          serial.setProductionDate(batch.getProductionDate());
          serial.setExpiryDate(batch.getExpiryDate());
          serial.setShelfLocation(StringUtil.isBlank(row.getShelfLocation()) ? batch.getShelfLocation() : row.getShelfLocation());
          // 序列号为新建记录，如导入行提供了供应商，则一并写入 supplierId
          if (st.getSupplierId() != null) {
            serial.setSupplierId(st.getSupplierId());
          }
          productStockSerialService.save(serial);
        }
      }
    }
  }
}
