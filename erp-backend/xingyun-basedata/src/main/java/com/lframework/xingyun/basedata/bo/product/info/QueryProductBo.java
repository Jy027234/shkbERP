package com.lframework.xingyun.basedata.bo.product.info;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lframework.starter.common.constants.StringPool;
import com.lframework.starter.common.utils.StringUtil;
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
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class QueryProductBo extends BaseBo<Product> {

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
   * 名称
   */
  @ApiModelProperty("简称")
  private String shortName;

  /**
   * SKU
   */
  @ApiModelProperty("SKU")
  private String skuCode;

  /**
   * 分类名称
   */
  @ApiModelProperty("分类名称")
  private String categoryName;

  /**
   * 品牌名称
   */
  @ApiModelProperty("品牌名称")
  private String brandName;

  /**
   * 件号编码
   */
  @ApiModelProperty("件号编码")
  private String partNumberCode;

  /**
   * 件号名称
   */
  @ApiModelProperty("件号名称")
  private String partNumberName;

  /**
   * 机型编码
   */
  @ApiModelProperty("机型编码")
  private String machineTypeCode;

  /**
   * 机型名称
   */
  @ApiModelProperty("机型名称")
  private String machineTypeName;

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
   * 是否启用批次号管理
   */
  @ApiModelProperty("是否启用批次号管理")
  private Boolean isBatch;

  /**
   * 是否启用序列号管理
   */
  @ApiModelProperty("是否启用序列号管理")
  private Boolean isSerial;

  /**
   * 创建时间
   */
  @ApiModelProperty("创建时间")
  @JsonFormat(pattern = StringPool.DATE_TIME_PATTERN)
  private LocalDateTime createTime;

  /**
   * 修改时间
   */
  @ApiModelProperty("修改时间")
  @JsonFormat(pattern = StringPool.DATE_TIME_PATTERN)
  private LocalDateTime updateTime;

  public QueryProductBo() {

  }

  public QueryProductBo(Product dto) {

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
    
    // 设置批次号管理和序列号管理状态
    this.isBatch = dto.getIsBatch();
    this.isSerial = dto.getIsSerial();
    
    // 直接根据商品表的机型ID获取机型信息
    if (StringUtil.isNotBlank(dto.getMachineTypeId())) {
      try {
        MachineTypeService machineTypeService = ApplicationUtil.getBean(MachineTypeService.class);
        MachineType machineType = machineTypeService.findById(dto.getMachineTypeId());
        if (machineType != null) {
          this.machineTypeCode = machineType.getCode();
          this.machineTypeName = machineType.getName();
        }
      } catch (Exception e) {
        // 忽略异常，避免影响主流程
      }
    }
  }
}
