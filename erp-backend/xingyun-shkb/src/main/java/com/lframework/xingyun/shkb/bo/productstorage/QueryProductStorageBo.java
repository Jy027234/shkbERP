package com.lframework.xingyun.shkb.bo.productstorage;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lframework.starter.web.core.bo.BaseBo;
import com.lframework.xingyun.shkb.entity.ProductStorage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class QueryProductStorageBo extends BaseBo<ProductStorage> {

  @ApiModelProperty("ID")
  private String id;

  @ApiModelProperty("客户名称")
  private String clientName;

  @ApiModelProperty("产品名称")
  private String productName;

  @ApiModelProperty("件号")
  private String productCode;

  @ApiModelProperty("序列号")
  private String serialNumber;

  @ApiModelProperty("入库时间")
  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  private String storageTime;

  @ApiModelProperty("出库时间")
  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  private String deliveryTime;

  @ApiModelProperty("备注")
  private String description;

  @ApiModelProperty("出入库单号")
  private String storageTrackingNumber;
  @ApiModelProperty("出库原因")
  private String deliveryReason;

  public QueryProductStorageBo(ProductStorage dto) {
    super(dto);
  }
}
