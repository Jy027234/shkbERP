package com.lframework.xingyun.sc.excel.stock;

import com.alibaba.excel.annotation.ExcelProperty;
import com.lframework.starter.web.core.components.excel.ExcelModel;
import com.lframework.starter.web.core.annotations.excel.ExcelRequired;
import lombok.Data;

/**
 * 库存初始化导入模板
 */
@Data
public class StockInitImportModel implements ExcelModel {

  /**
   * 件号（航材件号）
   */
  @ExcelRequired
  @ExcelProperty("件号")
  private String productCode;

  /**
   * 仓库编号
   */
  @ExcelRequired
  @ExcelProperty("仓库编号")
  private String scCode;

  /**
   * 数量
   */
  @ExcelRequired
  @ExcelProperty("数量")
  private Integer qty;

  /**
   * 批次号（启用批次时必填，否则忽略）
   */
  @ExcelProperty("批次号")
  private String batchNumber;

  /**
   * 序列号列表（启用序列时必填，英文逗号分隔）
   */
  @ExcelProperty("序列号列表")
  private String serialNumberList;


  /**
   * 架位（批次层级）
   */
  @ExcelProperty("架位")
  private String shelfLocation;

  /**
   * 生产日期（yyyy-MM-dd）
   */
  @ExcelProperty("生产日期")
  private String productionDate;

  /**
   * 失效日期（yyyy-MM-dd）
   */
  @ExcelProperty("失效日期")
  private String expiryDate;

  /**
   * 采购价（元）
   */
  @ExcelProperty("采购价（元）")
  private java.math.BigDecimal taxPrice;

  /**
   * 供应商名称（可选，根据名称匹配启用状态的供应商）
   */
  @ExcelProperty("供应商")
  private String supplierName;

}
