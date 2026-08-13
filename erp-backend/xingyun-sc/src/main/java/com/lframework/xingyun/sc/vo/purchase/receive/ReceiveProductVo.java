package com.lframework.xingyun.sc.vo.purchase.receive;

import com.lframework.starter.web.core.vo.BaseVo;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class ReceiveProductVo implements BaseVo, Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * 商品ID
   */
  @ApiModelProperty("商品ID")
  private String productId;

  /**
   * 采购价
   */
  @ApiModelProperty("采购价")
  private BigDecimal purchasePrice;

  /**
   * 收货数量
   */
  @ApiModelProperty("收货数量")
  private Integer receiveNum;

  /**
   * 备注
   */
  @ApiModelProperty("备注")
  private String description;

  /**
   * 采购订单明细ID
   */
  @ApiModelProperty("采购订单明细ID")
  private String purchaseOrderDetailId;
  
  /**
   * 批次号
   */
  @ApiModelProperty("批次号")
  private String batchNumber;
  
  /**
   * 唯一序列号列表，多个序列号用逗号,隔开
   */
  @ApiModelProperty("唯一序列号列表")
  private String serialNumberList;
  
  /**
   * 生产日期
   */
  @ApiModelProperty("生产日期")
  private String productionDate;
  
  /**
   * 失效日期
   */
  @ApiModelProperty("失效日期")
  private String expiryDate;
}
