package com.lframework.xingyun.sc.excel.stock;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.lframework.starter.web.core.bo.BaseBo;
import com.lframework.starter.web.core.components.excel.ExcelModel;
import com.lframework.starter.web.core.utils.ApplicationUtil;
import com.lframework.xingyun.basedata.entity.Product;
import com.lframework.xingyun.basedata.entity.ProductBrand;
import com.lframework.xingyun.basedata.entity.ProductCategory;
import com.lframework.xingyun.basedata.entity.StoreCenter;
import com.lframework.xingyun.basedata.entity.Supplier;
import com.lframework.xingyun.basedata.service.product.ProductBrandService;
import com.lframework.xingyun.basedata.service.product.ProductCategoryService;
import com.lframework.xingyun.basedata.service.product.ProductService;
import com.lframework.xingyun.basedata.service.storecenter.StoreCenterService;
import com.lframework.xingyun.basedata.service.supplier.SupplierService;
import com.lframework.xingyun.sc.entity.ProductStockBatch;
import java.util.Date;
import lombok.Data;

/**
 * 航材批次库存-列表视图导出模型
 *
 * 对应前端批次库存列表字段：
 * 仓库编号、仓库名称、航材件号、航材名称、航材分类、航材制造商、供应商、库存数量、批次号、架位、生产日期、失效日期
 */
@Data
public class ProductStockBatchViewExportModel extends BaseBo<ProductStockBatch> implements ExcelModel {

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

  @ExcelProperty("库存数量")
  private Integer quantity;

  @ExcelProperty("批次号")
  private String batchNumber;

  @ExcelProperty("架位")
  private String shelfLocation;

  @ExcelProperty("生产日期")
  @DateTimeFormat("yyyy-MM-dd")
  private Date productionDate;

  @ExcelProperty("失效日期")
  @DateTimeFormat("yyyy-MM-dd")
  private Date expiryDate;

  public ProductStockBatchViewExportModel() {
  }

  public ProductStockBatchViewExportModel(ProductStockBatch dto) {
    super(dto);
  }

  @Override
  public <A> BaseBo<ProductStockBatch> convert(ProductStockBatch dto) {
    return this;
  }

  @Override
  protected void afterInit(ProductStockBatch dto) {
    // 仓库
    StoreCenterService storeCenterService = ApplicationUtil.getBean(StoreCenterService.class);
    StoreCenter sc = storeCenterService.findById(dto.getScId());
    if (sc != null) {
      this.scCode = sc.getCode();
      this.scName = sc.getName();
    }

    // 航材信息
    ProductService productService = ApplicationUtil.getBean(ProductService.class);
    Product product = productService.findById(dto.getProductId());
    if (product != null) {
      this.productCode = product.getCode();
      this.productName = product.getName();

      if (product.getCategoryId() != null) {
        ProductCategoryService categoryService = ApplicationUtil.getBean(ProductCategoryService.class);
        ProductCategory category = categoryService.findById(product.getCategoryId());
        if (category != null) {
          this.categoryName = category.getName();
        }
      }

      if (product.getBrandId() != null) {
        ProductBrandService brandService = ApplicationUtil.getBean(ProductBrandService.class);
        ProductBrand brand = brandService.findById(product.getBrandId());
        if (brand != null) {
          this.brandName = brand.getName();
        }
      }
    }

    // 供应商
    if (dto.getSupplierId() != null) {
      SupplierService supplierService = ApplicationUtil.getBean(SupplierService.class);
      Supplier supplier = supplierService.findById(dto.getSupplierId());
      if (supplier != null) {
        this.supplierName = supplier.getName();
      }
    }

    this.quantity = dto.getQuantity();
    this.batchNumber = dto.getBatchNumber();
    this.shelfLocation = dto.getShelfLocation();

    if (dto.getProductionDate() != null) {
      // LocalDate -> java.util.Date，使用 java.sql.Date 保留日期部分
      this.productionDate = java.sql.Date.valueOf(dto.getProductionDate());
    }
    if (dto.getExpiryDate() != null) {
      this.expiryDate = java.sql.Date.valueOf(dto.getExpiryDate());
    }
  }
}
