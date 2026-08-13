package com.lframework.xingyun.sc.excel.stock;

import com.alibaba.excel.annotation.ExcelProperty;
import com.lframework.starter.common.utils.NumberUtil;
import com.lframework.starter.web.core.bo.BaseBo;
import com.lframework.starter.web.core.components.excel.ExcelModel;
import com.lframework.starter.web.core.utils.ApplicationUtil;
import com.lframework.xingyun.basedata.entity.Product;
import com.lframework.xingyun.basedata.entity.ProductBrand;
import com.lframework.xingyun.basedata.entity.ProductCategory;
import com.lframework.xingyun.basedata.entity.StoreCenter;
import com.lframework.xingyun.basedata.service.product.ProductBrandService;
import com.lframework.xingyun.basedata.service.product.ProductCategoryService;
import com.lframework.xingyun.basedata.service.product.ProductService;
import com.lframework.xingyun.basedata.service.storecenter.StoreCenterService;
import com.lframework.xingyun.sc.entity.ProductStock;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 航材库存-列表视图导出模型
 *
 * 与前端航材库存列表字段保持一致：
 * 仓库编号、仓库名称、航材件号、航材名称、航材分类、航材制造商、库存数量、不含税价格、不含税金额
 */
@Data
public class ProductStockViewExportModel extends BaseBo<ProductStock> implements ExcelModel {

  /** 仓库编号 */
  @ExcelProperty("仓库编号")
  private String scCode;

  /** 仓库名称 */
  @ExcelProperty("仓库名称")
  private String scName;

  /** 航材件号 */
  @ExcelProperty("航材件号")
  private String productCode;

  /** 航材名称 */
  @ExcelProperty("航材名称")
  private String productName;

  /** 航材分类 */
  @ExcelProperty("航材分类")
  private String categoryName;

  /** 航材制造商 */
  @ExcelProperty("航材制造商")
  private String brandName;

  /** 库存数量 */
  @ExcelProperty("库存数量")
  private Integer stockNum;

  /** 不含税价格（实际取值仍为 taxPrice，只是表头文案与前端一致） */
  @ExcelProperty("不含税价格")
  private BigDecimal taxPrice;

  /** 不含税金额（实际取值仍为 taxAmount，只是表头文案与前端一致） */
  @ExcelProperty("不含税金额")
  private BigDecimal taxAmount;

  public ProductStockViewExportModel() {
  }

  public ProductStockViewExportModel(ProductStock dto) {
    super(dto);
  }

  @Override
  public <A> BaseBo<ProductStock> convert(ProductStock dto) {
    return this;
  }

  @Override
  protected void afterInit(ProductStock dto) {
    // 仓库信息
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

      // 分类
      if (product.getCategoryId() != null) {
        ProductCategoryService productCategoryService = ApplicationUtil.getBean(ProductCategoryService.class);
        ProductCategory category = productCategoryService.findById(product.getCategoryId());
        if (category != null) {
          this.categoryName = category.getName();
        }
      }

      // 品牌
      if (product.getBrandId() != null) {
        ProductBrandService productBrandService = ApplicationUtil.getBean(ProductBrandService.class);
        ProductBrand brand = productBrandService.findById(product.getBrandId());
        if (brand != null) {
          this.brandName = brand.getName();
        }
      }
    }

    // 数量和金额（与列表 BO 一致，保留两位小数）
    this.stockNum = dto.getStockNum();
    this.taxPrice = NumberUtil.getNumber(dto.getTaxPrice(), 2);
    this.taxAmount = NumberUtil.getNumber(dto.getTaxAmount(), 2);
  }
}
