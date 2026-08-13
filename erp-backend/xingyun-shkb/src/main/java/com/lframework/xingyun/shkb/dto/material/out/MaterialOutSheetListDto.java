package com.lframework.xingyun.shkb.dto.material.out;

import com.lframework.starter.web.core.dto.BaseDto;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 发料出库单列表DTO
 */
@Data
public class MaterialOutSheetListDto implements BaseDto {

  @ApiModelProperty("ID")
  private String id;

  @ApiModelProperty("单号")
  private String code;

  @ApiModelProperty("仓库ID")
  private String scId;

  @ApiModelProperty("仓库名称")
  private String scName;

  @ApiModelProperty("供应商ID")
  private String supplierId;

  @ApiModelProperty("供应商名称")
  private String supplierName;

  @ApiModelProperty("发料员ID")
  private String materialUserId;

  @ApiModelProperty("发料员姓名")
  private String materialUserName;

  @ApiModelProperty("发料单ID")
  private String materialOrderId;

  @ApiModelProperty("发料单号")
  private String materialOrderCode;

  @ApiModelProperty("商品数量")
  private Integer totalNum;

  @ApiModelProperty("发料金额")
  private BigDecimal totalAmount;

  @ApiModelProperty("状态")
  private Integer status;

  @ApiModelProperty("备注")
  private String description;

  @ApiModelProperty("创建人")
  private String createBy;

  @ApiModelProperty("创建时间")
  private LocalDateTime createTime;

  @ApiModelProperty("审核人")
  private String approveBy;

  @ApiModelProperty("审核时间")
  private LocalDateTime approveTime;
}
