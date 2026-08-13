package com.lframework.xingyun.basedata.bo.product.info;

import com.lframework.starter.web.core.annotations.convert.EnumConvert;
import com.lframework.starter.web.core.bo.BaseBo;
import com.lframework.starter.web.core.utils.ApplicationUtil;
import com.lframework.xingyun.basedata.entity.Product;
import com.lframework.xingyun.basedata.entity.ProductBrand;
import com.lframework.xingyun.basedata.entity.ProductCategory;
import com.lframework.xingyun.basedata.entity.MachineType;
import com.lframework.xingyun.basedata.service.product.ProductBrandService;
import com.lframework.xingyun.basedata.service.product.ProductCategoryService;
import com.lframework.xingyun.basedata.service.machineType.MachineTypeService;
import com.lframework.starter.common.utils.StringUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ProductSelectorBo extends BaseBo<Product> {

  /**
   * ID
   */
  @ApiModelProperty("ID")
  private String id;

  /**
   * 编号
   */
  @ApiModelProperty("编号")
  private String code;

  /**
   * 名称
   */
  @ApiModelProperty("名称")
  private String name;

  /**
   * SKU
   */
  @ApiModelProperty("SKU")
  private String skuCode;

  /**
   * 简码
   */
  @ApiModelProperty("简码")
  private String externalCode;

  /**
   * 分类ID
   */
  @ApiModelProperty("分类ID")
  private String categoryId;

  /**
   * 分类名称
   */
  @ApiModelProperty("分类名称")
  private String categoryName;

  /**
   * 品牌ID
   */
  @ApiModelProperty("品牌ID")
  private String brandId;

  /**
   * 品牌名称
   */
  @ApiModelProperty("品牌名称")
  private String brandName;

  /**
   * 规格
   */
  @ApiModelProperty("规格")
  private String spec;

  /**
   * 单位
   */
  @ApiModelProperty("单位")
  private String unit;

  /**
   * 商品类型
   */
  @ApiModelProperty("商品类型")
  @EnumConvert
  private Integer productType;

  /**
   * 状态
   */
  @ApiModelProperty("状态")
  private Boolean available;
  
  /**
   * 机型ID
   */
  @ApiModelProperty("机型ID")
  private String machineTypeId;
  
  /**
   * 机型名称
   */
  @ApiModelProperty("机型名称")
  private String machineTypeName;
  
  /**
   * 机型编码
   */
  @ApiModelProperty("机型编码")
  private String machineTypeCode;
  
  /**
   * 件号ID
   */
  @ApiModelProperty("件号ID")
  private String partNumberId;
  
  /**
   * 件号名称
   */
  @ApiModelProperty("件号名称")
  private String partNumberName;
  
  /**
   * 件号编码
   */
  @ApiModelProperty("件号编码")
  private String partNumberCode;

  public ProductSelectorBo() {

  }

  public ProductSelectorBo(Product dto) {

    super(dto);
  }

  @Override
  protected void afterInit(Product dto) {
    ProductCategoryService productCategoryService = ApplicationUtil.getBean(
        ProductCategoryService.class);
    ProductCategory productCategory = productCategoryService.findById(dto.getCategoryId());
    this.categoryName = productCategory.getName();

    ProductBrandService productBrandService = ApplicationUtil.getBean(ProductBrandService.class);
    ProductBrand brand = productBrandService.findById(dto.getBrandId());
    if(brand != null) {
      this.brandName = brand.getName();
    }
    
    // 暂停使用件号表：将商品编号/名称视为件号编码/名称
    this.partNumberId = null;
    this.partNumberCode = dto.getCode();
    this.partNumberName = dto.getName();
    
    // 机型信息来自商品自身的machineTypeId
    this.machineTypeId = dto.getMachineTypeId();
    if (StringUtil.isNotBlank(this.machineTypeId)) {
      MachineTypeService machineTypeService = ApplicationUtil.getBean(MachineTypeService.class);
      MachineType machineType = machineTypeService.findById(this.machineTypeId);
      if (machineType != null) {
        this.machineTypeName = machineType.getName();
        this.machineTypeCode = machineType.getCode();
      }
    }
  }
}
