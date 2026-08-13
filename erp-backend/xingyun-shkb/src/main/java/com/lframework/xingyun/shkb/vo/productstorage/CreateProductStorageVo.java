package com.lframework.xingyun.shkb.vo.productstorage;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CreateProductStorageVo {

  @ApiModelProperty("客户名称")
  private String clientName;

  @ApiModelProperty("产品名称")
  private String productName;

  @ApiModelProperty("件号")
  private String productCode;

  @ApiModelProperty("序列号")
  private String serialNumber;

  @ApiModelProperty("入库时间(yyyy-MM-dd HH:mm:ss)")
  private String storageTime;

  @ApiModelProperty("出库时间(yyyy-MM-dd HH:mm:ss)")
  private String deliveryTime;

  @ApiModelProperty("备注")
  private String description;

  @ApiModelProperty("出入库单号")
  private String storageTrackingNumber;

  @ApiModelProperty("是否发料出库 0 未发料 1 已发料")
  private Integer isMaterialIssued;

  @ApiModelProperty("出库原因")
  private String deliveryReason;
}
