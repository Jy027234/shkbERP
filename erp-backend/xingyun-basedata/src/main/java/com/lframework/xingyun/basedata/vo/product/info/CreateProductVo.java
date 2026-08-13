package com.lframework.xingyun.basedata.vo.product.info;

import com.lframework.starter.web.core.components.validation.IsEnum;
import com.lframework.starter.web.core.vo.BaseVo;
import com.lframework.xingyun.basedata.enums.ProductType;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreateProductVo implements BaseVo, Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * 编号
   */
  @ApiModelProperty(value = "件号", required = true)
  @Pattern(regexp = "^.{1,100}$", message = "件号长度不能超过100位")
  @NotBlank(message = "请输入件号！")
  private String code;

  /**
   * 名称
   */
  @ApiModelProperty(value = "名称", required = true)
  @NotBlank(message = "请输入名称！")
  private String name;

  /**
   * 简称
   */
  @ApiModelProperty(value = "简称")
  private String shortName;

  /**
   * 商品SKU编号
   */
  @ApiModelProperty(value = "商品SKU编号", required = true)
  @NotBlank(message = "商品SKU编号不能为空！")
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
  @NotBlank(message = "分类ID不能为空！")
  private String categoryId;

  /**
   * 品牌ID
   */
  @ApiModelProperty("品牌ID")
  private String brandId;

  /**
   * 规格
   */
  @ApiModelProperty("规格")
  private String spec;

  /**
   * 机型ID
   */
  @ApiModelProperty(value = "机型ID", required = true)
  @NotBlank(message = "机型ID不能为空！")
  private String machineTypeId;

  /**
   * 单位
   */
  @ApiModelProperty("单位")
  private String unit;

  /**
   * 进项税率（%）
   */
  @ApiModelProperty(value = "进项税率（%）")
  @Min(value = 0, message = "进项税率（%）不允许小于0！")
  @Digits(integer = 10, fraction = 0, message = "进项税率（%）必须为整数！")
  private BigDecimal taxRate = BigDecimal.ZERO;

  /**
   * 销项税率（%）
   */
  @ApiModelProperty(value = "销项税率（%）")
  @Min(value = 0, message = "销项税率（%）不允许小于0！")
  @Digits(integer = 10, fraction = 0, message = "销项税率（%）必须为整数！")
  private BigDecimal saleTaxRate = BigDecimal.ZERO;

  /**
   * 商品类型
   */
  @ApiModelProperty(value = "商品类型", required = true)
  @NotNull(message = "商品类型不能为空！")
  @IsEnum(message = "商品类型格式错误！", enumClass = ProductType.class)
  private Integer productType;

  /**
   * 重量（kg）
   */
  @ApiModelProperty(value = "重量（kg）")
  @Digits(integer = 10, fraction = 2, message = "重量最多允许2位小数！")
  private BigDecimal weight;

  /**
   * 体积（cm3）
   */
  @ApiModelProperty(value = "体积（cm3）")
  @Digits(integer = 10, fraction = 2, message = "体积最多允许2位小数！")
  private BigDecimal volume;

  /**
   * 单品
   */
  @ApiModelProperty(value = "单品")
  @Valid
  private List<ProductBundleVo> productBundles;

  /**
   * 商品属性
   */
  @ApiModelProperty(value = "商品属性")
  @Valid
  private List<ProductPropertyRelationVo> properties;

  /**
   * 采购价
   */
  @ApiModelProperty("采购价")
  private BigDecimal purchasePrice = BigDecimal.ZERO;

  /**
   * 销售价
   */
  @ApiModelProperty("销售价")
  private BigDecimal salePrice;

  /**
   * 零售价
   */
  @ApiModelProperty("零售价")
  private BigDecimal retailPrice;

  /**
   * 是否启用批次号管理
   */
  @ApiModelProperty("是否启用批次号管理")
  private Boolean isBatch = Boolean.FALSE;

  /**
   * 是否启用序列号管理
   */
  @ApiModelProperty("是否启用序列号管理")
  private Boolean isSerial = Boolean.FALSE;
}
