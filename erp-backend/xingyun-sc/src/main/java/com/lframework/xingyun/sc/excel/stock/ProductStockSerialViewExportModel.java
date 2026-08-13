package com.lframework.xingyun.sc.excel.stock;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.lframework.starter.web.core.components.excel.ExcelModel;
import com.lframework.xingyun.sc.bo.stock.serial.QueryProductStockSerialBo;
import java.util.Date;
import lombok.Data;

/**
 * 航材序列号库存-列表视图导出模型
 *
 * 对应前端序列号库存列表字段：
 * 仓库编号、仓库名称、航材件号、航材名称、航材分类、航材制造商、供应商、序列号、架位、库存状态、生产日期、失效日期
 */
@Data
public class ProductStockSerialViewExportModel implements ExcelModel {

  @ExcelProperty("仓库编号")
  private String scCode;

  @ExcelProperty("仓库名称")
  private String scName;

  @ExcelProperty("航材件号")
  private String productCode;

  @ExcelProperty("航材名称")
  private String productName;

  @ExcelProperty("航材分类")
  private String categoryName;

  @ExcelProperty("航材制造商")
  private String brandName;

  @ExcelProperty("供应商")
  private String supplierName;

  @ExcelProperty("序列号")
  private String serialNumber;

  @ExcelProperty("架位")
  private String shelfLocation;

  @ExcelProperty("库存状态")
  private String stockStatus;

  @ExcelProperty("生产日期")
  @DateTimeFormat("yyyy-MM-dd")
  private Date productionDate;

  @ExcelProperty("失效日期")
  @DateTimeFormat("yyyy-MM-dd")
  private Date expiryDate;

  public ProductStockSerialViewExportModel() {
  }

  public ProductStockSerialViewExportModel(QueryProductStockSerialBo dto) {
    // 仓库
    this.scCode = dto.getScCode();
    this.scName = dto.getScName();

    // 航材信息
    this.productCode = dto.getProductCode();
    this.productName = dto.getProductName();
    this.categoryName = dto.getCategoryName();
    this.brandName = dto.getBrandName();

    // 供应商
    this.supplierName = dto.getSupplierName();

    this.serialNumber = dto.getSerialNumber();
    this.shelfLocation = dto.getShelfLocation();

    // 库存状态：true=在库，false/空=出库（与前端列表 formatter 一致）
    if (dto.getStockStatus() != null && dto.getStockStatus()) {
      this.stockStatus = "在库";
    } else {
      this.stockStatus = "出库";
    }

    if (dto.getProductionDate() != null) {
      this.productionDate = java.sql.Date.valueOf(dto.getProductionDate());
    }
    if (dto.getExpiryDate() != null) {
      this.expiryDate = java.sql.Date.valueOf(dto.getExpiryDate());
    }
  }
}
