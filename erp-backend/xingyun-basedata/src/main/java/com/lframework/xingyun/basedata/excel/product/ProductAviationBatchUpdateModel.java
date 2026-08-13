package com.lframework.xingyun.basedata.excel.product;

import com.alibaba.excel.annotation.ExcelProperty;
import com.lframework.starter.web.core.annotations.excel.ExcelRequired;
import com.lframework.starter.web.core.components.excel.ExcelModel;
import lombok.Data;

/**
 * 航材批量修改（仅机型）导入模板
 */
@Data
public class ProductAviationBatchUpdateModel implements ExcelModel {

  /**
   * 件号
   */
  @ExcelRequired
  @ExcelProperty("件号")
  private String code;

  /**
   * 名称（可选修改）
   */
  @ExcelProperty("名称")
  private String name;

  /**
   * 机型
   */
  @ExcelRequired
  @ExcelProperty("机型")
  private String machineTypeName;
}
