package com.lframework.xingyun.shkb.vo.productstorage;

import com.lframework.starter.web.core.vo.PageVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class QueryProductStorageVo extends PageVo {

  @ApiModelProperty("客户名称")
  private String clientName;

  @ApiModelProperty("产品名称")
  private String productName;

  @ApiModelProperty("件号")
  private String productCode;

  @ApiModelProperty("序列号")
  private String serialNumber;
}
