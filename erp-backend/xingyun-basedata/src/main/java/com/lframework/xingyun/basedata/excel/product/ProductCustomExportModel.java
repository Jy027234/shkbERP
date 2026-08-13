package com.lframework.xingyun.basedata.excel.product;

import com.alibaba.excel.annotation.ExcelProperty;
import com.lframework.starter.web.core.bo.BaseBo;
import com.lframework.starter.web.core.components.excel.ExcelModel;
import com.lframework.starter.web.core.utils.ApplicationUtil;
import com.lframework.xingyun.basedata.entity.MachineType;
import com.lframework.xingyun.basedata.entity.Product;
import com.lframework.xingyun.basedata.entity.ProductBrand;
import com.lframework.xingyun.basedata.entity.ProductCategory;
import com.lframework.xingyun.basedata.service.machineType.MachineTypeService;
import com.lframework.xingyun.basedata.service.product.ProductBrandService;
import com.lframework.xingyun.basedata.service.product.ProductCategoryService;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 航材导出模型
 */
@Data
public class ProductCustomExportModel extends BaseBo<Product> implements ExcelModel {

  /** 件号 */
  @ExcelProperty("件号")
  private String code;

  /** 名称 */
  @ExcelProperty("名称")
  private String name;

  /** 分类名称 */
  @ExcelProperty("分类名称")
  private String categoryName;

  /** 机型 */
  @ExcelProperty("机型")
  private String machineTypeName;

  /** 制造商名称 */
  @ExcelProperty("制造商名称")
  private String brandName;

  /** 规格 */
  @ExcelProperty("规格")
  private String spec;

  /** 单位 */
  @ExcelProperty("单位")
  private String unit;

  /** 重量（kg） */
  @ExcelProperty("重量（kg）")
  private BigDecimal weight;

  /** 体积（cm³） */
  @ExcelProperty("体积（cm³）")
  private BigDecimal volume;

  /** 是否启用批次号管理 */
  @ExcelProperty("是否启用批次号管理")
  private String isBatch;

  /** 是否启用序列号管理 */
  @ExcelProperty("是否启用序列号管理")
  private String isSerial;

  public ProductCustomExportModel() {
  }

  public ProductCustomExportModel(Product dto) {
    super(dto);
  }

  @Override
  protected void afterInit(Product dto) {
    // 件号和基础信息
    this.code = dto.getCode();
    this.name = dto.getName();
    this.spec = dto.getSpec();
    this.unit = dto.getUnit();
    this.weight = dto.getWeight();
    this.volume = dto.getVolume();
    this.isBatch = dto.getIsBatch() != null && dto.getIsBatch() ? "是" : "否";
    this.isSerial = dto.getIsSerial() != null && dto.getIsSerial() ? "是" : "否";

    // 分类名称
    if (dto.getCategoryId() != null) {
      ProductCategoryService categoryService = ApplicationUtil.getBean(ProductCategoryService.class);
      ProductCategory category = categoryService.findById(dto.getCategoryId());
      if (category != null) {
        this.categoryName = category.getName();
      }
    }

    // 制造商名称
    if (dto.getBrandId() != null) {
      ProductBrandService brandService = ApplicationUtil.getBean(ProductBrandService.class);
      ProductBrand brand = brandService.findById(dto.getBrandId());
      if (brand != null) {
        this.brandName = brand.getName();
      }
    }

    // 机型名称
    if (dto.getMachineTypeId() != null) {
      MachineTypeService machineTypeService = ApplicationUtil.getBean(MachineTypeService.class);
      MachineType machineType = machineTypeService.findById(dto.getMachineTypeId());
      if (machineType != null) {
        this.machineTypeName = machineType.getName();
      }
    }
  }
}
