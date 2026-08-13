package com.lframework.xingyun.basedata.excel.product;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.lframework.starter.web.core.annotations.excel.ExcelRequired;
import com.lframework.starter.web.core.components.excel.ExcelModel;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductCustomImportModel implements ExcelModel {

  /**
   * ID
   */
  @ExcelIgnore
  private String id;

  /**
   * 编号
   */
  @ExcelRequired
  @ExcelProperty("件号")
  private String code;

  /**
   * 名称
   */
  @ExcelRequired
  @ExcelProperty("名称")
  private String name;


  /**
   * 分类名称
   */
  @ExcelRequired
  @ExcelProperty("分类名称")
  private String categoryName;


  /**
   * 机型
   */
  @ExcelRequired
  @ExcelProperty("机型")
  private String machineTypeName;

  /**
   * 制造商名称
   */
  @ExcelProperty("制造商名称")
  private String brandName;


  /**
   * 规格
   */
  @ExcelProperty("规格")
  private String spec;

  /**
   * 单位
   */
  @ExcelProperty("单位")
  private String unit;

  /**
   * 重量（kg）
   */
  @ExcelProperty("重量（kg）")
  private Double weight;

  /**
   * 体积（cm³）
   */
  @ExcelProperty("体积（cm³）")
  private Double volume;

  /**
   * 采购价
   */
  @ExcelProperty("采购价（元）")
  private BigDecimal purchasePrice;




  /**
   * 是否启用批次号管理
   */
  @ExcelProperty("是否启用批次号管理")
  private String isBatch;

  /**
   * 是否启用序列号管理
   */
  @ExcelProperty("是否启用序列号管理")
  private String isSerial;
}
